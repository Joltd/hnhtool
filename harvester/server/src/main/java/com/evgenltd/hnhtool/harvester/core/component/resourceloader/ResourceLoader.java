package com.evgenltd.hnhtool.harvester.core.component.resourceloader;

import com.evgenltd.hnhtools.common.ApplicationException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 11-12-2019 00:09</p>
 */
public class ResourceLoader {

    private static final String CERT_FILE = "/ressrv.crt";
    private static final String CERT_FACTORY_TYPE = "X.509";
    private static final String SECURITY_PROTOCOL = "TLS";
    private static final String SECURITY_ALGORITHM = "PKIX";

    private SSLContext sslContext;

    public ResourceLoader() {
        try {
            final KeyStore trustedStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustedStore.load(null, null);

            final InputStream certStream = getClass().getResourceAsStream(CERT_FILE);
            final CertificateFactory certificateFactory = CertificateFactory.getInstance(CERT_FACTORY_TYPE);
            final Certificate certificate = certificateFactory.generateCertificate(certStream);

            trustedStore.setCertificateEntry("cert-0", certificate);

            sslContext = SSLContext.getInstance(SECURITY_PROTOCOL);
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(SECURITY_ALGORITHM);
            trustManagerFactory.init(trustedStore);
            final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            sslContext.init(null, trustManagers, new SecureRandom());
        } catch (final Exception e) {
            throw new ApplicationException("Unable to initialize resource loader SSL context", e);
        }

    }

    public HttpComponentsClientHttpRequestFactory buildRequestFactory() {

        final CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .build();

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        return requestFactory;
    }

}
