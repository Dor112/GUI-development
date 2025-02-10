package org.example;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        float a = console.nextFloat();
        float b = console.nextFloat();
        Square test = new Square(a,b);
        test.math();
    }
}