package com.hnh.auth;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Objects;

public class Authentication implements AutoCloseable {

	private static final Integer COOKIE_SIZE = 32;
	private static final Integer TOKEN_SIZE = 32;

	private static final String CERT_FILE = "/authsrv.crt";
	private static final String CERT_FACTORY_TYPE = "X.509";
	private static final String SECURITY_PROTOCOL = "TLS";
	private static final String SECURITY_ALGORITHM = "PKIX";
	private static final String HASH_ALGORITHM = "SHA-256";

	private static final String COMMAND_LOGIN = "pw";
	private static final String COMMAND_LOGIN_BY_TOKEN = "token";
	private static final String COMMAND_GET_COOKIE = "cookie";
	private static final String COMMAND_GET_TOKEN = "mktoken";

	private static final String ANSWER_OK = "ok";
	private static final String ANSWER_NO = "no";

	private int certificateAliasIncrement = 0;
	private KeyStore trustedStore;
	private Socket connectionSocket;

	private String host;
	private Integer port = 1871;

	public static Authentication of() {
		return new Authentication();
	}

	public static byte[] passwordHash(final String password) {
		Objects.requireNonNull(password, "[Password] should not be empty");
		try {
			final MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
			messageDigest.update(password.getBytes(StandardCharsets.UTF_8));
			return messageDigest.digest();
		}catch (Exception e) {
			throw AuthenticationException.throwPasswordHashingFailed(e);
		}
	}

	private Authentication() {

		try {
			trustedStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustedStore.load(null, null);

			final InputStream certStream = Authentication.class.getResourceAsStream(CERT_FILE);
			final CertificateFactory certificateFactory = CertificateFactory.getInstance(CERT_FACTORY_TYPE);
			final Certificate certificate = certificateFactory.generateCertificate(certStream);

			trustedStore.setCertificateEntry(generateCertificateAlias(), certificate);
		}catch (Exception e) {
			AuthenticationException.throwInitializationFailed(e);
		}

	}

	public Authentication setHost(final String host) {
		Objects.requireNonNull(host, "[Host] should not be empty");
		this.host = host;
		return this;
	}

	public Authentication setPort(final Integer port) {
		Objects.requireNonNull(port, "[Port] should not be empty");
		this.port = port;
		return this;
	}

	public Authentication init() {

		Objects.requireNonNull(host, "[Host] should not be empty");
		Objects.requireNonNull(port, "[Port] should not be empty");

		try {
			final SSLContext sslContext = SSLContext.getInstance(SECURITY_PROTOCOL);
			final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(SECURITY_ALGORITHM);
			trustManagerFactory.init(trustedStore);
			final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            sslContext.init(null, trustManagers, new SecureRandom());

			final SSLSocketFactory socketFactory = sslContext.getSocketFactory();
			final Socket socket = new Socket();
			socket.connect(new InetSocketAddress(host, port));
			connectionSocket = socketFactory.createSocket(socket, host, port, true);
		}catch (Exception e) {
			AuthenticationException.throwInitializationFailed(e);
		}

		return this;

	}

	public AuthenticationResult login(final String username, final byte[] passwordHash) {
		Objects.requireNonNull(username, "[Username] should not be empty");

		doLogin(COMMAND_LOGIN, username, passwordHash);
		final byte[] cookie = retrieveCookie();
		final byte[] token = retrieveToken();

		close();

		return new AuthenticationResult(cookie, token);
	}

    public AuthenticationResult loginByToken(final String username, final byte[] token) {
	    Objects.requireNonNull(username, "[Username] should not be empty");

	    doLogin(COMMAND_LOGIN_BY_TOKEN, username, token);
        final byte[] cookie = retrieveCookie();

        close();

	    return new AuthenticationResult(cookie, null);
    }

	@Override
	public void close() {
        try {
            connectionSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	// logic

	private void doLogin(final String command, final String username, final byte[] passwordHash) {
	    final DataWriter writer = new DataWriter();
	    writer.addString(command);
		writer.addString(username);
		writer.addbytes(passwordHash);
		sendMessage(writer);

		final DataReader response = readMessage();
		final String status = response.string();
		final String details = response.string();
		if (status.equals(ANSWER_NO)) {
			AuthenticationException.throwAuthenticationFailed(details);
		}

		checkResponseStatusOk(status);
	}

	private byte[] retrieveCookie() {
        return retrieveData(COMMAND_GET_COOKIE, COOKIE_SIZE);
    }

	private byte[] retrieveToken() {
        return retrieveData(COMMAND_GET_TOKEN, TOKEN_SIZE);
    }

    private byte[] retrieveData(final String commandGet, final Integer size) {
        final DataWriter message = new DataWriter();
        message.addString(commandGet);
        sendMessage(message);

        final DataReader response = readMessage();
        final String status = response.string();
        checkResponseStatusOk(status);

        return response.bytes(size);
    }

    // read & write

	private void sendMessage(final DataWriter message) {
		try {
			final byte[] data = message.bytes();

			final DataWriter writerWithLength = new DataWriter();
			writerWithLength.adduint8((byte) ((data.length & 0xFF00) >> 8));
			writerWithLength.adduint8((byte) ((data.length & 0x00FF)));
			writerWithLength.addbytes(data);

			connectionSocket.getOutputStream().write(writerWithLength.bytes());
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private DataReader readMessage() {
		try {
			final DataReader lengthHeader = read(2);
			final int messageLength = (lengthHeader.uint8() << 8)
                    | lengthHeader.uint8();
			return read(messageLength);
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private DataReader read(final int count) throws IOException {
		final byte[] result = new byte[count];
		for (int index = 0, rv; index < count; index += rv) {
			rv = connectionSocket.getInputStream().read(result, index, count - index);
			if (rv < 0) {
				throw (new IOException("Premature end of input"));
			}
		}
		return new DataReader(result);
	}

	// other

	private void checkResponseStatusOk(final String status) {
		if (!status.equals(ANSWER_OK)) {
			AuthenticationException.throwUnknownServerAnswer(status);
		}
	}

	private String generateCertificateAlias() {
		return "cert-" + certificateAliasIncrement++;
	}

}
