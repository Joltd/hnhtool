package com.hnh.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Dummy {

    public static void main(String[] args) throws Exception {

        if (args.length == 2) {
            loginByPassword(args[0], args[1]);
            return;
        }

        if (args.length == 1) {
            loginByToken(args[0]);
            return;
        }

        System.out.println("Run as 'java -jar auth.jar <username> <password>' skip password for login by token");

    }

    private static void loginByPassword(final String username, final String password) throws IOException {
        final Authentication authentication = Authentication.of()
                .setHost("game.havenandhearth.com")
                .init();

        final AuthenticationResult result = authentication.login(
                username,
                Authentication.passwordHash(password)
        );

        final FileOutputStream cookieStream = new FileOutputStream(new File("D:\\cookie.dat"));
        cookieStream.write(result.getCookie());
        final FileOutputStream tokenStream = new FileOutputStream(new File("D:\\token.dat"));
        tokenStream.write(result.getToken());

        System.out.println("Cookie = " + Arrays.toString(result.getCookie()));
        System.out.println("Token = " + Arrays.toString(result.getToken()));
    }

    private static void loginByToken(final String username) {

        try (final FileInputStream stream = new FileInputStream("D:\\token.dat")) {

            final int available = stream.available();
            if (available <= 0) {
                throw new RuntimeException("Unable to read token.dat");
            }

            final byte[] token = new byte[available];
            final int read = stream.read(token);
            if (read < 0) {
                throw new RuntimeException("Unable to read token.dat");
            }

            final Authentication authentication = Authentication.of()
                    .setHost("game.havenandhearth.com")
                    .init();

            final AuthenticationResult result = authentication.loginByToken(
                    username,
                    token
            );

            final FileOutputStream cookieStream = new FileOutputStream(new File("D:\\cookie.dat"));
            cookieStream.write(result.getCookie());

            System.out.println("Cookie = " + Arrays.toString(result.getCookie()));

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

    }

}
