package org.example;

import java.util.Scanner;
public class Main {

    public static double function(double x) {
        return x*x*x + x + 1;
    }
    public static double findRoot(double a, double b, double aim) {

        double mid;

        if (function(a) * function(b) > 0) {
            throw new IllegalArgumentException("На заданном интервале нет корня или их четное количество.");
        }

        while ((b - a) / 2 > aim) {
            mid = (a + b) / 2;
            if (function(mid) == 0.0) {
                return mid;
            } else if (function(a) * function(mid) < 0) {
                b = mid;
            } else {
                a = mid;
            }
        }
        return (a + b) / 2;
    }

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        double a = console.nextDouble();
        double b = console.nextDouble();
        double aim = console.nextDouble();

        double root = findRoot(a, b, aim);
        System.out.println("Найденный корень: " + root);
    }
}
