package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Experiment {
    private static final Random RANDOM = new Random(42);
    private static final String CSV_FILE = "results/results.csv";

    public static void main(String[] args) {
        System.out.println("=== STARTING BENCHMARK EXPERIMENTS ===");

        File dir = new File("results");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            writer.println("algorithm,n,type,timeNs,recursionDepth");

            warmupJVM();

            int[] sizes = {1000, 10000, 100000, 1000000};
            String[] types = {"Random", "Sorted", "Reverse-sorted", "Duplicate-heavy"};

            for (int n : sizes) {
                for (String type : types) {
                    runSortExperiment(writer, "MergeSort", n, type);
                    runSortExperiment(writer, "QuickSort", n, type);
                }
            }

            for (int n : sizes) {
                runSelectExperiment(writer, n);
            }

            int[] pointSizes = {1000, 5000, 10000, 50000, 100000};
            for (int n : pointSizes) {
                runClosestPairExperiment(writer, n);
            }

            System.out.println("\n✓ Experiments complete! Results saved to " + CSV_FILE);

        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }

    private static void runSortExperiment(PrintWriter writer, String algoName, int n, String type) {
        int[] original = generateData(n, type);
        int[] target = original.clone();

        long startTime = System.nanoTime();
        int maxDepth = 0;

        if ("MergeSort".equals(algoName)) {
            MergeSorter.sort(target);
            maxDepth = (int) (Math.log(n) / Math.log(2)) + 1;
        } else if ("QuickSort".equals(algoName)) {
            QuickSorter.sort(target);
            maxDepth = QuickSorter.getMaxDepth();
        }

        long elapsedTime = System.nanoTime() - startTime;

        writer.printf("%s,%d,%s,%d,%d\n", algoName, n, type, elapsedTime, maxDepth);
        System.out.printf("[%s] N=%-7d | Type=%-15s | Time=%8.3f ms | Depth=%d\n",
                algoName, n, type, elapsedTime / 1e6, maxDepth);
    }

    private static void runSelectExperiment(PrintWriter writer, int n) {
        int[] original = generateData(n, "Random");
        int k = n / 2;

        long startTime = System.nanoTime();
        DeterministicSelector.select(original, k);
        long elapsedTime = System.nanoTime() - startTime;
        int maxDepth = DeterministicSelector.getMaxDepth();

        writer.printf("DeterministicSelect,%d,Random,%d,%d\n", n, elapsedTime, maxDepth);
        System.out.printf("[DeterministicSelect] N=%-7d | Time=%8.3f ms | Depth=%d\n",
                n, elapsedTime / 1e6, maxDepth);
    }

    private static void runClosestPairExperiment(PrintWriter writer, int n) {
        Point[] points = generateRandomPoints(n);

        long startTime = System.nanoTime();
        ClosestPairSolver.findClosestPair(points);
        long elapsedTime = System.nanoTime() - startTime;
        int maxDepth = ClosestPairSolver.getMaxDepth();

        writer.printf("ClosestPair,%d,Random,%d,%d\n", n, elapsedTime, maxDepth);
        System.out.printf("[ClosestPair] N=%-7d | Time=%8.3f ms | Depth=%d\n",
                n, elapsedTime / 1e6, maxDepth);
    }

    private static void warmupJVM() {
        System.out.println("Warming up JVM...");
        for (int i = 0; i < 5; i++) {
            int[] dummy = generateData(5000, "Random");
            MergeSorter.sort(dummy.clone());
            QuickSorter.sort(dummy.clone());
            DeterministicSelector.select(dummy, 2500);
            ClosestPairSolver.findClosestPair(generateRandomPoints(1000));
        }
        System.out.println("Warmup done.\n");
    }

    private static int[] generateData(int size, String type) {
        int[] arr = new int[size];
        switch (type) {
            case "Random":
                for (int i = 0; i < size; i++) arr[i] = RANDOM.nextInt();
                break;
            case "Sorted":
                for (int i = 0; i < size; i++) arr[i] = i;
                break;
            case "Reverse-sorted":
                for (int i = 0; i < size; i++) arr[i] = size - i;
                break;
            case "Duplicate-heavy":
                for (int i = 0; i < size; i++) arr[i] = RANDOM.nextInt(10);
                break;
        }
        return arr;
    }

    private static Point[] generateRandomPoints(int size) {
        Point[] points = new Point[size];
        for (int i = 0; i < size; i++) {
            points[i] = new Point(RANDOM.nextDouble() * 10000, RANDOM.nextDouble() * 10000);
        }
        return points;
    }
}