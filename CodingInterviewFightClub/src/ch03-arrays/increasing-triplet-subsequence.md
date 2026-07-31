# 3.21 Increasing Triplet Subsequence

> **Source:** [`src/main/kotlin/array/greedy/IncreasingTripletSequence.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/greedy/IncreasingTripletSequence.kt)
> **Pattern:** two running minima · **Core page**

## The Problem

Is there `i < j < k` with `nums[i] < nums[j] < nums[k]`? O(n) time, O(1) space.

- Constraints: $1 \le n \le 5 \times 10^5$.

## Examples

```
Input:  nums = [1,2,3,4,5]   -> Output: true   (1 < 2 < 3)
Input:  nums = [5,4,3,2,1]   -> Output: false
Input:  nums = [2,1,5,0,4,6] -> Output: true   (1 < 4 < 6)
```

## Intuition — track the two smallest prefixes, then wait for a third

The LIS-with-length-3 greedy: maintain `smallest` (the best `i`-candidate) and `secondSmallest` (the best `j`-candidate). A number bigger than both completes the triplet:

```kotlin
var (smallest, secondSmallest) = Pair(Int.MAX_VALUE, Int.MAX_VALUE)

for (num in nums) {
    when {
        num <= smallest -> smallest = num
        num <= secondSmallest -> secondSmallest = num
        else -> return true       // num > both: i < j < k found
    }
}
return false
```

**Why is this correct (not just greedy)?** `smallest` and `secondSmallest` are the *smallest possible* pair seen so far — whenever a later `num` beats both, it beats *any* pair, forming a valid triplet. The [2.19](../ch02-dynamic-programming/longest-increasing-subsequence.md) patience-sorting tail for length exactly 3.

**Why `<=` not `<`?** Strictly increasing is required; `num <= smallest` refreshes the minimum (a smaller or equal `i`-candidate is never worse), and `<= secondSmallest` updates the `j`-candidate. Equality refreshes but never falsely completes.

## Approach 1 — LIS full DP (O(n log n))

Run the [2.19](../ch02-dynamic-programming/longest-increasing-subsequence.md) patience sort and check tails ≥ 3: correct, overkill.

## Approach 2 — Two running minima (the repo's version, optimal)

```kotlin
class IncreasingTripletSequence {
    /**
     * @param nums input array
     * @return     true iff an increasing triplet exists
     */
    fun increasingTriplet(nums: IntArray): Boolean {
        var (smallest, secondSmallest) = Pair(Int.MAX_VALUE, Int.MAX_VALUE)

        for (num in nums) {
            when {
                num <= smallest -> smallest = num
                num <= secondSmallest -> secondSmallest = num
                else -> return true
            }
        }
        return false
    }
}
```

```java
public class IncreasingTripletSubsequence {
    /**
     * @param nums input array
     * @return     true iff an increasing triplet exists
     */
    public boolean increasingTriplet(int[] nums) {
        int smallest = Integer.MAX_VALUE, second = Integer.MAX_VALUE;

        for (int num : nums) {
            if (num <= smallest) smallest = num;
            else if (num <= second) second = num;
            else return true;
        }
        return false;
    }
}
```

```cpp
#include <vector>
#include <climits>

class IncreasingTripletSubsequence {
public:
    /**
     * @param nums input array
     * @return     true iff an increasing triplet exists
     */
    bool increasingTriplet(std::vector<int>& nums) {
        int smallest = INT_MAX, second = INT_MAX;

        for (int num : nums) {
            if (num <= smallest) smallest = num;
            else if (num <= second) second = num;
            else return true;
        }
        return false;
    }
};
```

```python
def increasing_triplet(nums: list[int]) -> bool:
    """
    @param nums: input array
    @return:     true iff an increasing triplet exists
    """
    smallest = second = float("inf")

    for num in nums:
        if num <= smallest:
            smallest = num
        elif num <= second:
            second = num
        else:
            return True
    return False
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     true iff an increasing triplet exists
    pub fn increasing_triplet(nums: Vec<i32>) -> bool {
        let (mut smallest, mut second) = (i32::MAX, i32::MAX);

        for num in nums {
            if num <= smallest { smallest = num; }
            else if num <= second { second = num; }
            else { return true; }
        }
        false
    }
}
```

## Dry run

**Input:** `nums = [2,1,5,0,4,6]`.

```
smallest=MAX, second=MAX
2:  2 <= MAX -> smallest=2
1:  1 <= 2 -> smallest=1
5:  5 > 1, 5 <= MAX -> second=5
0:  0 <= 1 -> smallest=0          (refresh: never hurts)
4:  4 > 0, 4 <= 5 -> second=4
6:  6 > 0 && 6 > 4 -> return true ✓   (triplet 0 < 4 < 6)
```

The refresh subtlety: `0` replacing `smallest` doesn't break the second — the invariant is "smallest ≤ second, both as small as possible, in order". The triplet found is `0 < 4 < 6` (indices 3 < 4 < 5) — but note the *pair* that enabled it was `1 < 5` (indices 1 < 2), refreshed by `0` and `4` along the way. `[5,4,3,2,1]`: every number refreshes `smallest`, `second` never updates → false ✓.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Two scalars:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Longest Increasing Subsequence** ([2.19](../ch02-dynamic-programming/longest-increasing-subsequence.md)) — the general version; this page is its length-3 special case.
- **Longest Increasing Path** (`grid/`) — the 2-D cousin.
- **Interview follow-up:** "Why does refreshing `smallest` with a smaller number never hurt?" Any triplet that used the old `smallest` is *still* valid — but a smaller `smallest` makes future triplets *easier*. The invariant "smallest and secondSmallest are the lexicographically smallest ordered pair seen" is preserved by the two `<=` branches, and the `else` proves a triplet.
