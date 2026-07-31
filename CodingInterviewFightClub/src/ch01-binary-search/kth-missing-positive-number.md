# 1.11 Kth Missing Positive Number

> **Source:** [`src/main/kotlin/binarysearch/KThMissingPositiveNumber.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/KThMissingPositiveNumber.kt)
> **Pattern:** index-space counting + lower bound · **Core page**

## The Problem

Given a **strictly increasing** array `arr` of positive integers, find the **`k`-th missing positive integer**. The missing integers are counted from 1 upward, excluding the values present in `arr`.

- Constraints: $1 \le n \le 10^4$, $1 \le k \le 10^4$, strictly increasing `arr`.

## Examples

```
Input:  arr = [2, 3, 4, 7, 11], k = 5
Output: 9
Explanation: the missing positives are 1, 5, 6, 8, 9, 10, ...; the 5th is 9.

Input:  arr = [1, 2, 3, 4], k = 2
Output: 6
Explanation: missing are 5, 6, 7, ...; the 2nd is 6.
```

## Intuition — count the missing numbers *before* each position

The naive plan: walk from 1 upward, skipping values in `arr`, until you've seen k missing numbers. $O(n + k)$ — fine for small inputs, but there's a much sharper structure.

Define the **missing count before index `i`**:

$$
\text{missing}(i) = arr[i] - (i + 1)
$$

Why? Up to and including `arr[i]`, there are `arr[i]` positive integers total; of those, `i + 1` appear in the array (indices $0..i$). The rest — `arr[i] - (i + 1)` — are missing.

Since `arr` is strictly increasing, $\text{missing}(i)$ is **non-decreasing** in `i`: each step $i \to i+1$ adds $arr[i+1] - arr[i] - 1 \ge 0$ missing numbers. So the predicate

$$
P(i) = (\text{missing}(i) \ge k)
$$

is **monotone** (false, false, ..., true, true, ...) — Template A material. Let $i^*$ = the first index with missing(i) ≥ k. Then:

- All missing numbers up to position $i^*$ number ≥ k, and just *before* $i^*$ there are < k missing.
- The k-th missing number must be **greater than** `arr[i^*]` (it's past the last present value before the count reaches k)... wait, careful — actually the k-th missing is found *within* the gap ending at `arr[i^*]`.

Let me redo the accounting precisely. Let $m = \text{missing}(i^*)$. Just before index $i^*$, missing = missing(i^* - 1) < k. Between `arr[i^* - 1]` and `arr[i^*]` there are `arr[i^*] - arr[i^* - 1] - 1` missing numbers. The k-th missing overall is the $(k - \text{missing}(i^*-1))$-th missing number in that gap, which equals:

$$
\text{answer} = arr[i^*] - (\text{missing}(i^*) - k + 1)
$$

The neat formula that the code uses instead: **`answer = i^* + k`**. Let's prove it: since missing(i*) = arr[i*] − (i*+1) ≥ k and missing(i*−1) < k, we get

$$
arr[i^*] = \text{missing}(i^*) + i^* + 1
$$

The k-th missing is `missing(i*) − k + 1` positions before `arr[i*]`:

$$
arr[i^*] - (\text{missing}(i^*) - k + 1) = (i^* + 1 + \text{missing}(i^*)) - \text{missing}(i^*) + k - 1 = i^* + k
$$

Beautiful: **the answer is literally `left + k`** where `left` is the lower-bound index. And if `k > missing(n-1)` (the k-th missing is past the end of the array), the loop exits with `left == n`, and `n + k` is still correct. The formula handles the "append" case for free.

## Approach 1 — Linear scan

Walk `i = 1, 2, ...`, skip values in `arr`, count misses until k. $O(n + k)$ time. Works, but when `arr = [1, 2, 3, ..., 10000]` and `k = 10000` you scan ~20,000 numbers while the binary search needs 14 probes.

## Approach 2 — Binary search on the missing-count (optimal)

```kotlin
/**
 * @param arr the strictly increasing array of positive integers
 * @param k   the ordinal (1-based) of the missing positive integer to return
 * @return    the k-th missing positive integer
 */
fun findKthPositive(arr: IntArray, k: Int): Int {
    var (left, right) = 0 to arr.size

    while (left < right) {
        val mid = left + (right - left) / 2
        val missingCount = arr[mid] - (mid + 1)   // missing positives before index mid

        when {
            missingCount < k -> left = mid + 1    // not enough missing yet -> look right
            else             -> right = mid       // k-th missing is at or before mid
        }
    }
    return left + k   // the closed-form answer (see intuition for the proof)
}
```

```java
public class KthMissingPositiveNumber {
    /**
     * @param arr the strictly increasing array of positive integers
     * @param k   the ordinal (1-based) of the missing positive integer to return
     * @return    the k-th missing positive integer
     */
    public int findKthPositive(int[] arr, int k) {
        int left = 0, right = arr.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            int missingCount = arr[mid] - (mid + 1);
            if (missingCount < k) left = mid + 1;
            else right = mid;
        }
        return left + k;
    }
}
```

```cpp
#include <vector>

class KthMissingPositiveNumber {
public:
    /**
     * @param arr the strictly increasing array of positive integers
     * @param k   the ordinal (1-based) of the missing positive integer to return
     * @return    the k-th missing positive integer
     */
    int findKthPositive(const std::vector<int>& arr, int k) {
        int left = 0, right = (int)arr.size();
        while (left < right) {
            int mid = left + (right - left) / 2;
            int missingCount = arr[mid] - (mid + 1);
            if (missingCount < k) left = mid + 1;
            else right = mid;
        }
        return left + k;
    }
};
```

```python
def find_kth_positive(arr: list[int], k: int) -> int:
    """
    @param arr: the strictly increasing array of positive integers
    @param k:   the ordinal (1-based) of the missing positive integer to return
    @return:    the k-th missing positive integer
    """
    left, right = 0, len(arr)
    while left < right:
        mid = left + (right - left) // 2
        missing = arr[mid] - (mid + 1)     # missing positives before index mid
        if missing < k:
            left = mid + 1
        else:
            right = mid
    return left + k
```

```rust
impl Solution {
    /// @param arr the strictly increasing array of positive integers
    /// @param k   the ordinal (1-based) of the missing positive integer to return
    /// @return    the k-th missing positive integer
    pub fn find_kth_positive(arr: Vec<i32>, k: i32) -> i32 {
        let (mut left, mut right) = (0usize, arr.len());
        while left < right {
            let mid = left + (right - left) / 2;
            let missing = arr[mid] - (mid as i32 + 1);
            if missing < k {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        left as i32 + k
    }
}
```

## Dry run

**Input:** `arr = [2, 3, 4, 7, 11]`, `k = 5`

```
left=0  right=5  mid=2  missing(2) = 4 - 3 = 1  < 5 -> left=3
left=3  right=5  mid=4  missing(4) = 11 - 5 = 6 ≥ 5 -> right=4
left=3  right=4  mid=3  missing(3) = 7 - 4 = 3  < 5 -> left=4
left=4  right=4  -> return 4 + 5 = 9 ✓
```

Sanity table (the "missing(i)" column is the whole trick):

| i | arr[i] | missing(i) = arr[i] − (i+1) | running missing list |
|---|---|---|---|
| 0 | 2 | 1 | {1} |
| 1 | 3 | 1 | {1} |
| 2 | 4 | 1 | {1} |
| 3 | 7 | 3 | {1, 5, 6} |
| 4 | 11 | 6 | {1, 5, 6, 8, 9} |

The 5th missing is 9 — exactly `i* + k = 4 + 5`. The boundary index 4 is where missing crosses k=5 (6 ≥ 5), and the formula converts the index into the actual value.

**Append case:** `arr = [1, 2, 3, 4]`, `k = 2` — missing counts are all 0, so the loop slides `left` to 4:

```
left=0  right=4  mid=2  missing=0 < 2 -> left=3
left=3  right=4  mid=3  missing=0 < 2 -> left=4
return 4 + 2 = 6 ✓
```

## Complexity

**Time.** The lower-bound search halves an index space of size $n$:

$$
T(n) = O(\log n)
$$

**Space.** $O(1)$.

## Variants & follow-ups

- **Find First And Last Position** ([1.3](find-first-and-last-position.md)) and **Search Insert Position** ([1.18](search-insert-position.md)) — the same lower-bound engine on a different predicate.
- **Interview follow-up:** "Prove missing(i) is monotone." $arr[i+1] > arr[i]$ implies $arr[i+1] - (i+2) \ge arr[i] - (i+1)$, since the index increases by exactly 1 while the value increases by ≥ 1.
- **Interview follow-up:** "What if `k` can exceed `arr.size`?" The exclusive upper bound `right = arr.size` makes the loop terminate with `left == n`, and `n + k` is exactly the k-th missing (all n values are present, so the first missing after them is `n + 1`, the k-th is `n + k`). The formula covers it with zero extra code.
