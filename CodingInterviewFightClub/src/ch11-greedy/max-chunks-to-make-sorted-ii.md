# 11.26 Max Chunks To Make Sorted II

> **Source**: [`src/main/kotlin/greedy/MaxChuncksToMakeSorted_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/MaxChuncksToMakeSorted_II.kt)
> **Pattern**: prefix-max/suffix-min cut test · **Core page**

## The Problem

Max chunks so that sorting each chunk sorts the whole array (**duplicates allowed**).

- Constraints: n ≤ 2000; values ±10⁹.

## Examples

```
Input:  arr = [2,1,3,4,4]   -> Output: 4   ([2,1],[3],[4],[4])
Input:  arr = [5,4,3,2,1]   -> Output: 1
```

## Intuition — a cut is valid iff the prefix's max ≤ the suffix's min

Sorting each chunk works iff no value crosses a cut — the prefix's max must be ≤ the suffix's min:

```kotlin
val leftMax = IntArray(n)
leftMax[0] = arr[0]
for (i in 1 until n) leftMax[i] = maxOf(leftMax[i - 1], arr[i])

val rightMin = IntArray(n)
rightMin[n - 1] = arr[n - 1]
for (i in n - 2 downTo 0) rightMin[i] = minOf(rightMin[i + 1], arr[i])

var chunks = 1
for (i in 0 until n - 1) {
    if (leftMax[i] <= rightMin[i + 1]) chunks++    // a valid cut between i and i+1
}
return chunks
```

**Why the max/min invariant?** A cut after i is legal iff everything ≤ i is ≤ everything > i — then the two sides sort independently and concatenate correctly. The [11.0](pattern-primer.md) "monotone partition" test, with duplicates handled by ≤.

## Approach 1 — Prefix-max/suffix-min (the repo's version, optimal)

```kotlin
class MaxChuncksToMakeSorted_II {
    /**
     * @param arr input array
     * @return    max number of sortable chunks
     */
    fun maxChunksToSorted(arr: IntArray): Int {
        val n = arr.size

        val leftMax = IntArray(n)
        leftMax[0] = arr[0]
        for (i in 1 until n) {
            leftMax[i] = maxOf(leftMax[i - 1], arr[i])
        }

        val rightMin = IntArray(n)
        rightMin[n - 1] = arr[n - 1]
        for (i in n - 2 downTo 0) {
            rightMin[i] = minOf(rightMin[i + 1], arr[i])
        }

        var chunks = 1
        for (i in 0 until n - 1) {
            if (leftMax[i] <= rightMin[i + 1]) chunks++
        }
        return chunks
    }
}
```

```java
public class MaxChunksToMakeSortedII {
    /**
     * @param arr input array
     * @return    max number of sortable chunks
     */
    public int maxChunksToSorted(int[] arr) {
        int n = arr.length;
        int[] leftMax = new int[n];
        int[] rightMin = new int[n];

        leftMax[0] = arr[0];
        for (int i = 1; i < n; i++) leftMax[i] = Math.max(leftMax[i - 1], arr[i]);

        rightMin[n - 1] = arr[n - 1];
        for (int i = n - 2; i >= 0; i--) rightMin[i] = Math.min(rightMin[i + 1], arr[i]);

        int chunks = 1;
        for (int i = 0; i < n - 1; i++) {
            if (leftMax[i] <= rightMin[i + 1]) chunks++;
        }
        return chunks;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MaxChunksToMakeSortedII {
public:
    /**
     * @param arr input array
     * @return    max number of sortable chunks
     */
    int maxChunksToSorted(std::vector<int>& arr) {
        int n = arr.size();
        std::vector<int> leftMax(n), rightMin(n);

        leftMax[0] = arr[0];
        for (int i = 1; i < n; i++) leftMax[i] = std::max(leftMax[i - 1], arr[i]);

        rightMin[n - 1] = arr[n - 1];
        for (int i = n - 2; i >= 0; i--) rightMin[i] = std::min(rightMin[i + 1], arr[i]);

        int chunks = 1;
        for (int i = 0; i < n - 1; i++) {
            if (leftMax[i] <= rightMin[i + 1]) chunks++;
        }
        return chunks;
    }
};
```

```python
def max_chunks_to_sorted(arr: list[int]) -> int:
    """
    @param arr: input array
    @return:    max number of sortable chunks
    """
    n = len(arr)

    left_max = [0] * n
    left_max[0] = arr[0]
    for i in range(1, n):
        left_max[i] = max(left_max[i - 1], arr[i])

    right_min = [0] * n
    right_min[-1] = arr[-1]
    for i in range(n - 2, -1, -1):
        right_min[i] = min(right_min[i + 1], arr[i])

    chunks = 1
    for i in range(n - 1):
        if left_max[i] <= right_min[i + 1]:
            chunks += 1

    return chunks
```

```rust
impl Solution {
    /// @param arr input array
    /// @return    max number of sortable chunks
    pub fn max_chunks_to_sorted(arr: Vec<i32>) -> i32 {
        let n = arr.len();
        let mut left_max = vec![0; n];
        let mut right_min = vec![0; n];

        left_max[0] = arr[0];
        for i in 1..n { left_max[i] = left_max[i - 1].max(arr[i]); }

        right_min[n - 1] = arr[n - 1];
        for i in (0..n - 1).rev() { right_min[i] = right_min[i + 1].min(arr[i]); }

        let mut chunks = 1;
        for i in 0..n - 1 {
            if left_max[i] <= right_min[i + 1] { chunks += 1; }
        }
        chunks
    }
}
```

## Dry run

**Input:** `arr = [2,1,3,4,4]`.

```
leftMax:  [2,2,3,4,4]
rightMin: [1,1,3,4,4]
cuts: i=0: 2 <= 1? no.  i=1: 2 <= 3? yes -> +1.  i=2: 3 <= 4? yes -> +1.  i=3: 4 <= 4? yes -> +1.
chunks = 1 + 3 = 4 ✓  ([2,1],[3],[4],[4])
```

## Complexity

**Time.** Three passes:

$$
T(n) = O(n)
$$

**Space.** Two arrays (or a stack):

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Max Chunks To Make Sorted** — the permutation version (I): `maxSoFar == index` test.
- **Interview follow-up:** "Why does ≤ (not <) handle duplicates?" Equal values can straddle a cut harmlessly — `leftMax ≤ rightMin` allows the split, and the equal pair sorts consistently on either side.
