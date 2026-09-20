# Divide-and-Conquer Algorithm Analysis

## A. Project Overview
This project presents an empirical analysis, theoretical evaluation, and practical implementation of four core divide-and-conquer algorithms:
1. **MergeSort**: Sorting algorithm utilizing a pre-allocated auxiliary buffer with Insertion Sort cutoff for small sub-arrays ($N \le 16$).
2. **QuickSort**: In-place randomized quicksort implementing a tail-recursion call stack optimization (recursing on the smaller partition and iterating over the larger).
3. **Deterministic Select (Median-of-Medians)**: Worst-case linear time $O(n)$ selection algorithm using 3-way partitioning.
4. **Closest Pair of Points**: Computational geometry algorithm running in $\Theta(n \log n)$ time using efficient $Y$-sorted strip checking.

---

## B. Algorithm Analysis

### 1. MergeSort
* **Mechanism**: Recursively divides arrays into equal halves, sorts them, and merges using a single pre-allocated temporary buffer.
* **Time Complexity**: $\Theta(n \log n)$ across best, average, and worst cases.
* **Space Complexity**: $O(n)$ auxiliary memory space for the temporary buffer.
* **Recurrence Relation**:
  $$T(n) = 2T(n/2) + \Theta(n)$$
  Applying the **Master Theorem** (Case 2) where $a = 2, b = 2, f(n) = \Theta(n)$:
  Since $n^{\log_b a} = n^1 = n = \Theta(f(n))$, $T(n) = \Theta(n \log n)$.

### 2. QuickSort
* **Mechanism**: Picks a random pivot, partitions elements into $\le$ and $>$ subsets, recurses on the smaller sub-array first, and loops on the larger sub-array.
* **Time Complexity**: Average $\Theta(n \log n)$, Worst $O(n^2)$ (mitigated by random pivot choice).
* **Space Complexity**: $O(\log n)$ recursion stack space guaranteed by tail-call optimization.
* **Recurrence Relation**:
  $$T(n) = T(k) + T(n - k - 1) + \Theta(n)$$
  For an average balanced partition ($k \approx n/2$):
  $$T(n) = 2T(n/2) + \Theta(n) \implies \Theta(n \log n)$$

### 3. Deterministic Select (Median-of-Medians)
* **Mechanism**: Divides elements into groups of 5, finds group medians, recursively determines the median-of-medians as a pivot, and performs 3-way partitioning.
* **Time Complexity**: Guaranteed worst-case $\Theta(n)$.
* **Space Complexity**: $O(\log n)$ stack space.
* **Recurrence Relation**:
  $$T(n) \le T(n/5) + T(7n/10) + \Theta(n)$$
  By induction / Akra-Bazzi intuition: $1/5 + 7/10 = 9/10 < 1$, which proves the sub-problems shrink geometrically, maintaining linear time $T(n) = \Theta(n)$.

### 4. Closest Pair of Points
* **Mechanism**: Sorts points by $X$, splits space along the median line, computes minimum half-space distance $\delta = \min(\delta_L, \delta_R)$, creates a strip of width $2\delta$, and checks points in $Y$-sorted order (checking at most 7-8 points per candidate).
* **Time Complexity**: $\Theta(n \log n)$.
* **Space Complexity**: $O(n)$ for auxiliary arrays.
* **Recurrence Relation**:
  $$T(n) = 2T(n/2) + \Theta(n)$$
  By **Master Theorem** (Case 2): $T(n) = \Theta(n \log n)$.

---

## C. Experimental Results

### Benchmark Summary (Random Input Data)

| Algorithm | N = 1,000 | N = 10,000 | N = 100,000 | N = 1,000,000 |
| :--- | :--- | :--- | :--- | :--- |
| **MergeSort** | 0.21 ms (Depth 10) | 8.37 ms (Depth 14) | 11.75 ms (Depth 17) | 129.61 ms (Depth 20) |
| **QuickSort** | 0.14 ms (Depth 6) | 0.93 ms (Depth 9) | 11.97 ms (Depth 11) | 138.57 ms (Depth 13) |
| **Deterministic Select** | 0.10 ms (Depth 9) | 0.73 ms (Depth 11) | 4.94 ms (Depth 14) | 39.25 ms (Depth 18) |
| **Closest Pair** | 3.47 ms (Depth 10) | 42.15 ms (Depth 13) | 178.92 ms (Depth 17) | — |

*Plots generated from `results/results.csv` can be found in `docs/plots/Tvsn.png` and `docs/plots/RdvsN.png`.*

---

## D. Discussion

1. **Theoretical vs. Empirical Alignment**:
   Empirical performance closely matches theoretical predictions. MergeSort, QuickSort, and Closest Pair exhibit $O(n \log n)$ scaling, whereas Deterministic Select demonstrates linear $O(n)$ growth.
2. **Impact of Input Structure**:
   * **Sorted Arrays**: MergeSort exhibits $O(n)$ performance on pre-sorted arrays due to the `array[mid] <= array[mid+1]` optimization check.
   * **Duplicates**: Standard Lomuto QuickSort slows down significantly on duplicate-heavy inputs ($536\text{ ms}$ at $N=100k$) due to unbalanced $O(n^2)$ splits when equal values fall into a single sub-array.
3. **Recursion Stack Optimization**:
   By explicitly recursing on the smaller partition and using an iterative `while` loop for the larger partition, QuickSort limits stack depth to $O(\log n)$ ($d \le 14$ even at $N=1,000,000$).
4. **Deterministic Pivot Guarantees**:
   The median-of-medians algorithm guarantees that at least $30\%$ of the elements are strictly smaller than the pivot and at least $30\%$ are larger, avoiding bad splits and securing $O(n)$ bounds.
5. **Closest Pair Efficiency**:
   By maintaining points sorted by $Y$ coordinate during recursive splits, the strip search avoids full $O(n^2)$ distance comparisons, checking at most $7$ neighbors per point.
6. **JVM & System Considerations**:
   JIT compiler warmups significantly reduce execution variances. Garbage collection overhead is minimized by re-using temporary buffers across recursive calls.

---

## E. Reflection
Implementing and benchmarking these algorithms provided practical insights into architectural memory access and recursion controls. Handling edge cases in the Closest Pair algorithm (such as duplicate points with identical coordinates) required careful tie-breaking logic using composite comparators and duplicate counts to maintain valid array bounds and optimal time bounds.

---

## F. Verification Screenshots
* `docs/screenshots/img.png`: All unit and correctness tests passing.
* `docs/screenshots/img2.png`: Benchmark execution terminal output.
