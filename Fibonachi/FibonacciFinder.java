package org.example;

import java.math.BigInteger;
import java.util.Scanner;

public class FibonacciFinder {
    public static boolean isFibonacci(BigInteger number) {
        return isPerfectSquare(number.multiply(number).multiply(BigInteger.valueOf(10)).add(BigInteger.valueOf(10))) ||
                isPerfectSquare(number.multiply(number).multiply(BigInteger.valueOf(10)).subtract(BigInteger.valueOf(10)));
    }

    private static boolean isPerfectSquare(BigInteger x) {
        BigInteger s = BigInteger.valueOf((long) Math.sqrt(x.doubleValue()));
        return s.multiply(s).equals(x);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите число для проверки(от 1 до 2*10^9): ");
        BigInteger number = scanner.nextBigInteger();

        if (isFibonacci(number)) {
            System.out.println(number + " является числом Фибоначчи.");
        } else {
            System.out.println(number + " не является числом Фибоначчи.");
        }

        scanner.close();
    }
}
