package org.example;

import java.util.Scanner;

public class PrimeFactorization {

    public static void factorize(int number) {
        System.out.print("Простые множители числа " + number + ": ");

        while (number % 2 == 0) {
            System.out.print(2 + " ");
            number = number / 2;
        }


        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            while (number % i == 0) {
                System.out.print(i + " ");
                number = number / i;
            }
        }


        if (number > 2) {
            System.out.print(number);
        }

        System.out.println();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите натуральное число для разложения на простые множители: ");
        int number = scanner.nextInt();

        if (number < 2) {
            System.out.println("Число должно быть больше или равно 2.");
        } else {
            factorize(number);
        }

        scanner.close();
    }
}