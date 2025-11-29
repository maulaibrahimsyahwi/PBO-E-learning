package utils;

import java.util.Scanner;

public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);

    public static int inputInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);

                String input = scanner.nextLine();
                if (input == null) {
                    System.out.println("\nProgram dihentikan.");
                    System.exit(0);
                }

                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println("Input harus angka! Coba lagi:");
            } catch (Exception e) {
                System.out.println("\nTerima kasih menggunakan LMS!");
                System.exit(0);
            }
        }
    }

    public static String inputString(String msg) {
        try {
            System.out.print(msg);
            return scanner.nextLine();
        } catch (Exception e) {
            System.out.println("\nTerima kasih menggunakan LMS!");
            System.exit(0);
            return null;
        }
    }
}
