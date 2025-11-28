package utils;

import java.util.Scanner;

public class InputUtil {

    private static Scanner input = new Scanner(System.in);

    public static String inputString(String text) {
        System.out.print(text);
        return input.nextLine();
    }

    public static int inputInt(String text) {
        System.out.print(text);
        while (true) {
            try {
                return Integer.parseInt(input.nextLine());
            } catch (Exception e) {
                System.out.print("Input harus angka! Coba lagi: ");
            }
        }
    }
}
