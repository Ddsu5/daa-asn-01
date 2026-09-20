package src;

public class MergeSorter {
    private static final int CUTOFF = 16;

    private MergeSorter() {
    }

    public static void sort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        int[] buffer = new int[array.length];
        sort(array, buffer, 0, array.length - 1);
    }

    private static void sort(int[] array, int[] buffer, int left, int right) {
        if (right - left + 1 <= CUTOFF) {
            insertionSort(array, left, right);
            return;
        }

        int mid = left + (right - left) / 2;

        sort(array, buffer, left, mid);
        sort(array, buffer, mid + 1, right);

        if (array[mid] <= array[mid + 1]) {
            return;
        }

        merge(array, buffer, left, mid, right);
    }

    private static void insertionSort(int[] array, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int value = array[i];
            int j = i - 1;

            while (j >= left && array[j] > value) {
                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = value;
        }
    }


    private static void merge(
            int[] array,
            int[] buffer,
            int left,
            int mid,
            int right) {

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (array[i] <= array[j]) {
                buffer[k++] = array[i++];
            } else {
                buffer[k++] = array[j++];
            }
        }

        while (i <= mid) {
            buffer[k++] = array[i++];
        }

        while (j <= right) {
            buffer[k++] = array[j++];
        }

        for (int index = left; index <= right; index++) {
            array[index] = buffer[index];
        }
    }
}
