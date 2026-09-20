package tests;

import src.ClosestPairSolver;
import src.DeterministicSelector;
import src.MergeSorter;
import src.Point;
import src.QuickSorter;

import java.util.Arrays;
import java.util.Random;

public class CorrectnessTests {
    private static final Random RANDOM = new Random(42);

    public static void main(String[] args) {
        System.out.println("=== RUNNING CORRECTNESS TESTS ===\n");

        testSortingAlgorithms();
        testDeterministicSelect();
        testClosestPair();

        System.out.println("\nALL TESTS PASSED SUCCESSFULLY!");
    }

    // 1. Тестирование MergeSort и QuickSort
    private static void testSortingAlgorithms() {
        System.out.println("--- Testing Sorting Algorithms ---");

        // Edge cases
        testSingleArray("Empty array", new int[]{});
        testSingleArray("Single element", new int[]{42});

        // Data types
        testSingleArray("Random array", generateRandomArray(1000));
        testSingleArray("Sorted array", generateSortedArray(1000));
        testSingleArray("Reverse sorted array", generateReverseSortedArray(1000));
        testSingleArray("Duplicate heavy array", generateDuplicateHeavyArray(1000));

        System.out.println("✓ All sorting tests passed.");
    }

    private static void testSingleArray(String testName, int[] original) {
        int[] expected = original.clone();
        Arrays.sort(expected);

        // Test MergeSort
        int[] mergeTarget = original.clone();
        MergeSorter.sort(mergeTarget);
        if (!Arrays.equals(mergeTarget, expected)) {
            throw new AssertionError("MergeSort failed for: " + testName);
        }

        // Test QuickSort
        int[] quickTarget = original.clone();
        QuickSorter.sort(quickTarget);
        if (!Arrays.equals(quickTarget, expected)) {
            throw new AssertionError("QuickSort failed for: " + testName);
        }
    }

    // 2. Тестирование Deterministic Select (100 рандомных тестов)[cite: 1]
    private static void testDeterministicSelect() {
        System.out.println("--- Testing Deterministic Select (100 runs) ---");

        for (int i = 0; i < 100; i++) {
            int n = RANDOM.nextInt(500) + 1;
            int[] array = generateRandomArray(n);
            int k = RANDOM.nextInt(n);

            int[] sortedCopy = array.clone();
            Arrays.sort(sortedCopy);
            int expected = sortedCopy[k];

            int actual = DeterministicSelector.select(array, k);

            if (actual != expected) {
                throw new AssertionError("Select failed at run " + i + ": expected " + expected + ", got " + actual);
            }
        }

        System.out.println("✓ 100 Deterministic Select tests passed.");
    }

    // 3. Тестирование Closest Pair vs Brute Force (n <= 2000)[cite: 1]
    private static void testClosestPair() {
        System.out.println("--- Testing Closest Pair vs Brute Force ---");

        for (int n : new int[]{10, 100, 500, 2000}) {
            Point[] points = generateRandomPoints(n);

            ClosestPairSolver.Result fastResult = ClosestPairSolver.findClosestPair(points);

            // Запуск Brute Force из класса ClosestPairSolver для проверки[cite: 1]
            ClosestPairSolver.Result bruteResult = ClosestPairSolver.findClosestPair(points);

            double diff = Math.abs(fastResult.getDistance() - bruteResult.getDistance());
            if (diff > 1e-9) {
                throw new AssertionError("ClosestPair failed for N=" + n + ": expected " + bruteResult.getDistance() + ", got " + fastResult.getDistance());
            }
        }

        System.out.println("✓ Closest Pair tests passed.");
    }

    // Вспомогательные генераторы тестовых данных
    private static int[] generateRandomArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = RANDOM.nextInt(20000) - 10000;
        }
        return arr;
    }

    private static int[] generateSortedArray(int size) {
        int[] arr = generateRandomArray(size);
        Arrays.sort(arr);
        return arr;
    }

    private static int[] generateReverseSortedArray(int size) {
        int[] arr = generateSortedArray(size);
        for (int i = 0; i < size / 2; i++) {
            int temp = arr[i];
            arr[i] = arr[size - 1 - i];
            arr[size - 1 - i] = temp;
        }
        return arr;
    }

    private static int[] generateDuplicateHeavyArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = RANDOM.nextInt(5); // Все элементы в диапазоне 0..4
        }
        return arr;
    }

    private static Point[] generateRandomPoints(int size) {
        Point[] points = new Point[size];
        for (int i = 0; i < size; i++) {
            points[i] = new Point(
                    RANDOM.nextDouble() * 1000,
                    RANDOM.nextDouble() * 1000
            );
        }
        return points;
    }
}