# 14.1 Merge Sort

> **Source:** [`src/main/kotlin/sorting/MergeSort.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sorting/MergeSort.kt)
> **Pattern:** divide + merge · **Core page**

## The Problem

Implement merge sort — sort an array of integers.

- Constraints: classic sorting; $O(n \log n)$ worst case, stable.

## Examples

```
Input:  [38, 27, 43, 3, 9, 82, 10]
Output: [3, 9, 10, 27, 38, 43, 82]
```

## Intuition — sort halves, then *merge* two sorted halves

Merge sort is the divide-and-conquer template:

1. **Divide** — split the array at `mid = n / 2`.
2. **Conquer** — recursively sort the left half and the right half.
3. **Merge** — the key step: two *sorted* halves merge into one sorted whole by the classic two-pointer compare-and-append.

**Why is the merge correct and cheap?** Merging two sorted lists is $O(n)$: at every step the smallest remaining element overall is one of the two fronts — the same compare-and-advance from [4.3](../ch04-linked-lists/merge-two-sorted-lists.md), applied to arrays. The recursion splits until single-element arrays (trivially sorted), then merges upward — so every level merges $O(n)$ total elements, and there are $\log n$ levels: $O(n \log n)$.

**The space cost is the honest trade-off:** each recursion level allocates the `L` and `R` copies — $O(n)$ per level, $O(n \log n)$ if naively allocated per call (the repo's version allocates per call; an in-place merge with a single buffer reduces it to $O(n)$). This is the classic "merge sort vs quicksort" contrast: guaranteed $O(n \log n)$ and stable, at the price of extra memory.

**Why learn it?** It's the template for: counting inversions ($O(n \log n)$, the merge's compare counts), merge-k-lists ([Chapter 4](../ch04-linked-lists/index.md)), and external sorts. The merge step is the reusable piece; the divide is boilerplate.

## Approach 1 — Bubble/insertion sort (too slow)

$O(n^2)$ comparisons — fine for small arrays, and a good "why we need better" baseline.

## Approach 2 — Recursive merge sort (the repo's version, optimal)

```kotlin
class MergeSort {
    /**
     * Merge two sorted halves L and R into A
     * @param A          destination array
     * @param L          sorted left half
     * @param leftCount  size of L
     * @param R          sorted right half
     * @param rightCount size of R
     */
    fun merge(A: IntArray, L: IntArray, leftCount: Int, R: IntArray, rightCount: Int) {
        var (i, j, k) = listOf(0, 0, 0)

        while (i < leftCount && j < rightCount) {      // two-pointer compare-and-advance
            if (L[i] < R[j]) A[k++] = L[i++]
            else A[k++] = R[j++]
        }
        while (i < leftCount) A[k++] = L[i++]          // leftover from L
        while (j < rightCount) A[k++] = R[j++]         // leftover from R
    }

    /**
     * @param arr array to sort (in place, via the merged halves)
     * @param n   size of the segment being sorted
     */
    fun merge_sort(arr: IntArray, n: Int) {
        if (n < 2) return                              // base case: 0/1 elements sorted
        val mid = n / 2

        val L = IntArray(mid) { arr[it] }              // copy left half
        val R = IntArray(n - mid) { arr[it + mid] }    // copy right half

        merge_sort(L, mid)                             // sort left
        merge_sort(R, n - mid)                         // sort right
        merge(arr, L, mid, R, n - mid)                 // merge back
    }
}
```

```java
public class MergeSort {
    /** @param arr array to sort @param n segment size */
    public void mergeSort(int[] arr, int n) {
        if (n < 2) return;                             // base case
        int mid = n / 2;
        int[] L = new int[mid];
        int[] R = new int[n - mid];
        System.arraycopy(arr, 0, L, 0, mid);           // copy left half
        System.arraycopy(arr, mid, R, 0, n - mid);     // copy right half

        mergeSort(L, mid);                             // sort left
        mergeSort(R, n - mid);                         // sort right
        merge(arr, L, mid, R, n - mid);                // merge back
    }

    /** Merge two sorted halves into arr */
    private void merge(int[] arr, int[] L, int left, int[] R, int right) {
        int i = 0, j = 0, k = 0;
        while (i < left && j < right) arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
        while (i < left) arr[k++] = L[i++];
        while (j < right) arr[k++] = R[j++];
    }
}
```

```cpp
#include <vector>

class MergeSort {
    void merge(std::vector<int>& arr, std::vector<int>& L,
               std::vector<int>& R) {
        int i = 0, j = 0, k = 0;
        while (i < (int)L.size() && j < (int)R.size()) arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
        while (i < (int)L.size()) arr[k++] = L[i++];
        while (j < (int)R.size()) arr[k++] = R[j++];
    }

public:
    /** @param arr array to sort */
    void mergeSort(std::vector<int>& arr) {
        int n = arr.size();
        if (n < 2) return;                             // base case
        int mid = n / 2;

        std::vector<int> L(arr.begin(), arr.begin() + mid);   // copy left half
        std::vector<int> R(arr.begin() + mid, arr.end());     // copy right half

        mergeSort(L);                                  // sort left
        mergeSort(R);                                  // sort right
        merge(arr, L, R);                              // merge back
    }
};
```

```python
def merge_sort(arr: list[int]) -> list[int]:
    """
    @param arr: array to sort
    @return:    sorted array (new list; the recursive flavor)
    """
    if len(arr) < 2:
        return arr
    mid = len(arr) // 2

    left = merge_sort(arr[:mid])       # sort left half
    right = merge_sort(arr[mid:])      # sort right half

    merged = []
    i = j = 0
    while i < len(left) and j < len(right):      # two-pointer compare-and-advance
        if left[i] <= right[j]:
            merged.append(left[i]); i += 1
        else:
            merged.append(right[j]); j += 1
    merged.extend(left[i:])
    merged.extend(right[j:])
    return merged
```

```rust
impl Solution {
    /// @param arr array to sort
    /// @return    sorted array
    pub fn merge_sort(arr: Vec<i32>) -> Vec<i32> {
        fn merge(a: &[i32], b: &[i32]) -> Vec<i32> {
            let mut out = Vec::with_capacity(a.len() + b.len());
            let (mut i, mut j) = (0, 0);
            while i < a.len() && j < b.len() {           // two-pointer compare-and-advance
                if a[i] <= b[j] { out.push(a[i]); i += 1; } else { out.push(b[j]); j += 1; }
            }
            out.extend_from_slice(&a[i..]);
            out.extend_from_slice(&b[j..]);
            out
        }

        if arr.len() < 2 { return arr; }
        let mid = arr.len() / 2;
        merge(&merge_sort(arr[..mid].to_vec()), &merge_sort(arr[mid..].to_vec()))
    }
}
```

## Dry run

**Input:** `[38, 27, 43, 3, 9, 82, 10]`.

```
merge_sort([38,27,43,3,9,82,10])   n=7, mid=3
  L=[38,27,43], R=[3,9,82,10]
  merge_sort(L): L=[38,27,43] -> mid=1 -> L2=[38], R2=[27,43]
    merge_sort([27,43]) -> [27,43]
    merge([38],[27,43]): 27 < 38 -> [27, 38, 43]
  merge_sort(R): R=[3,9,82,10] -> [3, 9, 10, 82]
  merge([27,38,43],[3,9,10,82]):
    3,9,10 < 27 -> [3,9,10]; then 27,38,43,82 -> [3,9,10,27,38,43,82] ✓
```

The merge at the top level shows the compare-and-advance in full: three elements from `R` (3,9,10) are consumed before any of `L` — the two pointers walking independent fronts, exactly like the linked-list merge of [4.3](../ch04-linked-lists/merge-two-sorted-lists.md).

## Complexity

**Time.** $\log n$ levels, $O(n)$ merges:

$$
T(n) = O(n \log n) \quad \text{(worst, average, best — all identical)}
$$

**Space.** $O(n)$ per merge level (or $O(n)$ total with a shared buffer):

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Count Inversions** — the merge's comparison can *count*: every time `R[j]` is taken before `L[i]`, the remaining `L[i..]` are all inversions. Same $O(n \log n)$; the "merge step carries extra state" pattern.
- **Merge K Sorted Lists** ([4.3](../ch04-linked-lists/index.md) variant) — pairwise merge this template $k-1$ times, or heap it.
- **Sort An Array** — the repo's `sorting/` folder plus the standard-library sort; the interview value of this page is writing merge by hand *once*.
- **Interview follow-up:** "Why is merge sort stable but quicksort not?" Stability comes from the merge's `<=` tie-break (left elements before right ones on ties); quicksort's swap-based partition moves equal elements across each other. Stability matters when sorting by one key while preserving another's order — say that context unprompted.
