package src;

import java.util.concurrent.ThreadLocalRandom;

public class QuickSorter {
    private static long comparisons;
    private static int currentDepth;
    private static int maxDepth;

    private QuickSorter() {
    }

    public static void sort(int[] array) {
        resetMetrics();

        if (array == null || array.length < 2) {
            return;
        }

        quickSort(array, 0, array.length - 1, 1);
    }

    private static void quickSort(int[] array, int low, int high, int depth) {
        while (low < high) {
            currentDepth = depth;
            if (depth > maxDepth) {
                maxDepth = depth;
            }

            int pivotIndex = ThreadLocalRandom.current().nextInt(low, high + 1);
            swap(array, pivotIndex, high);

            int partitionIndex = partition(array, low, high);

            int leftSize = partitionIndex - low;
            int rightSize = high - partitionIndex;

            if (leftSize < rightSize) {
                if (low < partitionIndex - 1) {
                    quickSort(array, low, partitionIndex - 1, depth + 1);
                }
                low = partitionIndex + 1;
            } else {
                if (partitionIndex + 1 < high) {
                    quickSort(array, partitionIndex + 1, high, depth + 1);
                }
                high = partitionIndex - 1;
            }
        }
    }

    private static int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int storeIndex = low;

        for (int i = low; i < high; i++) {
            comparisons++;

            if (array[i] <= pivot) {
                swap(array, i, storeIndex);
                storeIndex++;
            }
        }

        swap(array, storeIndex, high);
        return storeIndex;
    }

    private static void swap(int[] array, int i, int j) {
        if (i != j) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private static void resetMetrics() {
        comparisons = 0;
        currentDepth = 0;
        maxDepth = 0;
    }

    public static long getComparisons() {
        return comparisons;
    }

    public static int getMaxDepth() {
        return maxDepth;
    }
}
