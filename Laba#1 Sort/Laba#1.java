package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NumberProcessor {


    public static List<Integer> readNumbersFromFile(String filePath) {
        List<Integer> numbers = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.contains(",")) {

                    String[] parts = line.split(",");
                    for (String part : parts) {
                        numbers.add(Integer.parseInt(part.trim()));
                    }
                } else {

                    numbers.add(Integer.parseInt(line));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: Файл не найден.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Неверный формат данных в файле.");
        }
        return numbers;
    }


    public static int calculateSum(List<Integer> numbers) {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }


    public static double calculateAverage(List<Integer> numbers) {
        return (double) calculateSum(numbers) / numbers.size();
    }


    public static int findMax(List<Integer> numbers) {
        int max = numbers.get(0);
        for (int number : numbers) {
            if (number > max) {
                max = number;
            }
        }
        return max;
    }


    public static int findMin(List<Integer> numbers) {
        int min = numbers.get(0);
        for (int number : numbers) {
            if (number < min) {
                min = number;
            }
        }
        return min;
    }


    public static double calculateVariance(List<Integer> numbers) {
        double average = calculateAverage(numbers);
        double variance = 0;
        for (int number : numbers) {
            variance += Math.pow(number - average, 2);
        }
        return variance / numbers.size();
    }

    private static int partition(List<Integer> numbers, int low, int high) {
        int pivot = numbers.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (numbers.get(j) < pivot) {
                i++;
                int temp = numbers.get(i);
                numbers.set(i, numbers.get(j));
                numbers.set(j, temp);
            }
        }
        int temp = numbers.get(i + 1);
        numbers.set(i + 1, numbers.get(high));
        numbers.set(high, temp);
        return i + 1;
    }

    public static void quickSort(List<Integer> numbers, int low, int high) {
        if (low < high) {
            int pi = partition(numbers, low, high);
            quickSort(numbers, low, pi - 1);
            quickSort(numbers, pi + 1, high);
        }
    }



    public static void printArray(List<Integer> numbers) {
        System.out.print("Массив: [");
        for (int i = 0; i < numbers.size(); i++) {
            System.out.print(numbers.get(i));
            if (i < numbers.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите путь к файлу: ");
        String filePath = scanner.nextLine();

        List<Integer> numbers = readNumbersFromFile(filePath);
        if (numbers.isEmpty()) {
            System.out.println("Не удалось прочитать данные из файла.");
            return;
        }

        printArray(numbers);

        System.out.println("Сумма элементов: " + calculateSum(numbers));
        System.out.println("Среднее значение: " + calculateAverage(numbers));
        System.out.println("Максимальный элемент: " + findMax(numbers));
        System.out.println("Минимальный элемент: " + findMin(numbers));
        System.out.println("Дисперсия: " + calculateVariance(numbers));

        quickSort(numbers, 0, numbers.size() - 1);
        System.out.println("Отсортированный массив:");
        printArray(numbers);

        scanner.close();
    }
}
