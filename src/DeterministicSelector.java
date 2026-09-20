package src;

public class DeterministicSelector {
    private static long comparisons;
    private static int maxDepth;

    private DeterministicSelector() {
    }

    public static int select(int[] array, int k) {
        resetMetrics();

        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array must not be empty.");
        }

        if (k < 0 || k >= array.length) {
            throw new IllegalArgumentException("k must be between 0 and n - 1.");
        }

        int[] copy = array.clone();
        return select(copy, 0, copy.length - 1, k, 1);
    }

    private static int select(
            int[] array,
            int left,
            int right,
            int k,
            int depth) {

        if (depth > maxDepth) {
            maxDepth = depth;
        }

        if (left == right) {
            return array[left];
        }

        int pivotValue = medianOfMedians(array, left, right);

        int[] bounds = partitionThreeWay(array, left, right, pivotValue);

        int lessEnd = bounds[0];
        int greaterStart = bounds[1];

        if (k < lessEnd - left + 1) {
            return select(array, left, lessEnd, k, depth + 1);
        }

        int equalStart = lessEnd + 1;
        int equalEnd = greaterStart - 1;

        if (k <= equalEnd - left) {
            return pivotValue;
        }

        int newK = k - (greaterStart - left);
        return select(array, greaterStart, right, newK, depth + 1);
    }

    private static int medianOfMedians(int[] array, int left, int right) {
        int size = right - left + 1;

        if (size <= 5) {
            insertionSortRange(array, left, right);
            return array[left + size / 2];
        }

        int medianCount = 0;

        for (int groupStart = left; groupStart <= right; groupStart += 5) {
            int groupEnd = Math.min(groupStart + 4, right);

            insertionSortRange(array, groupStart, groupEnd);

            int medianIndex = groupStart + (groupEnd - groupStart) / 2;
            swap(array, left + medianCount, medianIndex);
            medianCount++;
        }

        return select(
                array,
                left,
                left + medianCount - 1,
                medianCount / 2,
                1
        );
    }

    private static int[] partitionThreeWay(
            int[] array,
            int left,
            int right,
            int pivotValue) {

        int less = left;
        int current = left;
        int greater = right;

        while (current <= greater) {
            comparisons++;

            if (array[current] < pivotValue) {
                swap(array, less++, current++);
            } else if (array[current] > pivotValue) {
                swap(array, current, greater--);
            } else {
                current++;
            }
        }

        return new int[]{less - 1, greater + 1};
    }

    private static void insertionSortRange(int[] array, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int value = array[i];
            int j = i - 1;

            while (j >= left) {
                comparisons++;

                if (array[j] <= value) {
                    break;
                }

                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = value;
        }
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
        maxDepth = 0;
    }

    public static long getComparisons() {
        return comparisons;
    }

    public static int getMaxDepth() {
        return maxDepth;
    }
}
