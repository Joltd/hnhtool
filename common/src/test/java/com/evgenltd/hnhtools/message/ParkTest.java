package com.evgenltd.hnhtools.message;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-11-2019 01:56</p>
 */
public class ParkTest {

    private static volatile boolean shutdown = false;

    public static void main(String[] args) {

        new Thread(() -> {
            while (!shutdown) {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                System.out.println("Unparked");
            }
            System.out.println("Thread finished");
        }).start();

        final Scanner scanner = new Scanner(System.in);

        while (true) {
            final String command = scanner.nextLine();
            switch (command) {
                case "unp":
                    shutdown = true;
                    return;
                default:
                    System.out.println("Unknown command");
            }
        }

    }

}
