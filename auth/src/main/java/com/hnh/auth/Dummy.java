package com.hnh.auth;

import java.util.Arrays;

public class Dummy {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Run as java -jar auth.jar <username> <password>");
            return;
        }

        final Authentication authentication = Authentication.of()
                .setHost("game.havenandhearth.com")
                .init();

        final AuthenticationResult result = authentication.login(
                args[0],
                Authentication.passwordHash(args[1])
        );

        System.out.println("Cookie = " + Arrays.toString(result.getCookie()));
        System.out.println("Token = " + Arrays.toString(result.getToken()));

    }

}
