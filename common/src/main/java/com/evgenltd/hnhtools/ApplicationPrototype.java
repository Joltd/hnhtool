package com.evgenltd.hnhtools;

import com.evgenltd.hnhtools.baseclient.BaseClient;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnh.auth.Authentication;
import com.hnh.auth.AuthenticationResult;

import java.util.Scanner;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 13-03-2019 00:34</p>
 */
public class ApplicationPrototype {

    public static void main(String[] args) {



        final ObjectMapper mapper = new ObjectMapper();
        final BaseClient baseClient = new BaseClient(mapper);
        baseClient.withMonitor();
        baseClient.setServer("game.havenandhearth.com", 1870);
        baseClient.setCredentials("Grafbredbery", auth());
        baseClient.setRelQueue(relAccessor -> {});
        baseClient.setObjectDataQueue(objectDataAccessor -> {});

        Scanner scanner = new Scanner(System.in);
        while (true) {
            final String command = scanner.nextLine();
            switch (command) {
                case "c":
                    baseClient.connect();
                    break;
                case "d":
                    baseClient.disconnect();
                    return;
                case "i":
                    baseClient.printHealth();
                    break;
                default:
                    System.out.println("Unknown command [" + command + "]");
            }
        }

    }

    private static byte[] auth() {

        try (final Authentication auth = Authentication.of()) {
            auth.setHost("game.havenandhearth.com");
            auth.init();
            final AuthenticationResult result = auth.login(
                    "Grafbredbery",
                    Authentication.passwordHash("15051953")
            );
            return result.getCookie();
        } catch (Exception e) {
            throw new ApplicationException(e);
        }

    }

}
