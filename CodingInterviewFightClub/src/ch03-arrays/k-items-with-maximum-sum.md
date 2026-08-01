# 3.49 K Items With Maximum Sum

> **Source**: [`src/main/kotlin/array/greedy/KItemsWithMaximumSum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/greedy/KItemsWithMaximumSum.kt)
> **Pattern**: greedy pick order · **Core page**

## The Problem

Pick exactly `k` items from `numOnes` 1s, `numZeros` 0s, `numNegOnes` -1s to maximize the sum.

- Constraints: counts ≤ 10⁹; k ≤ counts.

## Examples

```
Input:  numOnes = 3, numZeros = 2, numNegOnes = 0, k = 2   -> Output: 2
Input:  numOnes = 3, numZeros = 2, numNegOnes = 1, k = 4   -> Output: 2
```

## Intuition — take the 1s first, then 0s, then -1s

```kotlin
return when {
    k <= numOnes -> k
    k <= numOnes + numZeros -> numOnes
    else -> numOnes - (k - numOnes - numZeros)
}
```

## Approach 1 — Greedy three-way (the repo's version, optimal)

```kotlin
class KItemsWithMaximumSum {
    /**
     * @param numOnes    count of 1s
     * @param numZeros   count of 0s
     * @param numNegOnes count of -1s
     * @param k          items to pick
     * @return           max sum
     */
    fun kItemsWithMaximumSum(numOnes: Int, numZeros: Int, numNegOnes: Int, k: Int): Int {
        return when {
            k <= numOnes -> k
            k <= numOnes + numZeros -> numOnes
            else -> numOnes - (k - numOnes - numZeros)
        }
    }
}
```

```java
public class KItemsWithMaximumSum {
    /**
     * @param numOnes    count of 1s
     * @param numZeros   count of 0s
     * @param numNegOnes count of -1s
     * @param k          items to pick
     * @return           max sum
     */
    public int kItemsWithMaximumSum(int numOnes, int numZeros, int numNegOnes, int k) {
        if (k <= numOnes) return k;
        if (k <= numOnes + numZeros) return numOnes;
        return numOnes - (k - numOnes - numZeros);
    }
}
```

```cpp
class KItemsWithMaximumSum {
public:
    /**
     * @param numOnes    count of 1s
     * @param numZeros   count of 0s
     * @param numNegOnes count of -1s
     * @param k          items to pick
     * @return           max sum
     */
    int kItemsWithMaximumSum(int numOnes, int numZeros, int numNegOnes, int k) {
        if (k <= numOnes) return k;
        if (k <= numOnes + numZeros) return numOnes;
        return numOnes - (k - numOnes - numZeros);
    }
};
```

```python
def k_items_with_maximum_sum(num_ones: int, num_zeros: int, num_neg_ones: int, k: int) -> int:
    """
    @param num_ones:    count of 1s
    @param num_zeros:   count of 0s
    @param num_neg_ones: count of -1s
    @param k:           items to pick
    @return:            max sum
    """
    if k <= num_ones:
        return k
    if k <= num_ones + num_zeros:
        return num_ones
    return num_ones - (k - num_ones - num_zeros)
```

```rust
impl Solution {
    /// @param num_ones     count of 1s
    /// @param num_zeros    count of 0s
    /// @param num_neg_ones count of -1s
    /// @param k            items to pick
    /// @return             max sum
    pub fn k_items_with_maximum_sum(num_ones: i32, num_zeros: i32, num_neg_ones: i32, k: i32) -> i32 {
        if k <= num_ones { k }
        else if k <= num_ones + num_zeros { num_ones }
        else { num_ones - (k - num_ones - num_zeros) }
    }
}
```

## Dry run

**Input:** `numOnes=3, numZeros=2, numNegOnes=1, k=4`.

```
k=4 > 3 -> not first.  k <= 5 -> numOnes = 3.
Output: 3 ✓  (pick three 1s and one 0)
```

## Complexity

**Time.** O(1):

$$
T = O(1)
$$

**Space.** O(1):

$$
S = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why is the greedy order forced?" Values are 1 > 0 > -1 — any non-greedy pick trades a 1 for a smaller value, strictly worse.
