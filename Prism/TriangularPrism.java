package org.example;

import java.util.Scanner;

public class TriangularPrism {
    private double baseA;
    private double baseB;
    private double baseC;
    private double height;


    public TriangularPrism(double baseA, double baseB, double baseC, double height) {
        this.baseA = baseA;
        this.baseB = baseB;
        this.baseC = baseC;
        this.height = height;
    }


    public double calculateLateralSurfaceArea() {

        double perimeter = baseA + baseB + baseC;
        return perimeter * height;
    }


    public double calculateVolume() {

        double baseArea = calculateBaseArea();
        return baseArea * height;
    }


    private double calculateBaseArea() {
        double s = (baseA + baseB + baseC) / 2;
        return Math.sqrt(s * (s - baseA) * (s - baseB) * (s - baseC));
    }

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        System.out.println("Введите длину стороны A: ");
        double baseA = console.nextDouble();
        System.out.println("Введите длину стороны B: ");
        double baseB = console.nextDouble();
        System.out.println("Введите длину стороны C: ");
        double baseC = console.nextDouble();
        System.out.println("Введите высоту призмы: ");
        double height = console.nextDouble();
        TriangularPrism prism = new TriangularPrism(baseA, baseB, baseC, height);

        System.out.println("Площадь боковой поверхности: " + prism.calculateLateralSurfaceArea());
        System.out.println("Объем призмы: " + prism.calculateVolume());
    }
}