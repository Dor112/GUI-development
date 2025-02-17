package org.example;

import java.util.Scanner;

public class FibonacciFinder {
    public static boolean isFibonacci(int number) {
        return isPerfectSquare(5 * number * number + 4) || isPerfectSquare(5 * number * number - 4);
    }

    private static boolean isPerfectSquare(int x) {
        int s = (int) Math.sqrt(x);
        return (s * s == x);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите число для проверки(от 1 до 2*10^9: ");
        int number = scanner.nextInt();

        if (isFibonacci(number)) {
            System.out.println(number + " является числом Фибоначчи.");
        } else {
            System.out.println(number + " не является числом Фибоначчи.");
        }

        scanner.close();
    }
}
