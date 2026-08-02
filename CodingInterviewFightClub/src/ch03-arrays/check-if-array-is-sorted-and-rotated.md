# 3.42 Check If Array Is Sorted And Rotated

> **Source**: [`src/main/kotlin/array/CheckkIfArrayIsSortedAndRotated.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/CheckkIfArrayIsSortedAndRotated.kt)
> **Pattern**: descent-count test · **Core page**

## The Problem

Is `nums` a sorted array rotated at some pivot (duplicates allowed)?

- Constraints: n ≤ 100.

## Examples

```
Input:  nums = [3,4,5,1,2]   -> Output: true
Input:  nums = [2,1,3,4]     -> Output: false
```

## Intuition — a sorted-rotated array has at most ONE descent

```kotlin
var count = 0
val n = nums.size

for (i in 0 until n) {
    if (nums[i] > nums[(i + 1) % n]) count++
    if (count > 1) return false
}
return true
```

**Why the modulo wrap?** The pivot is the single place where `nums[i] > nums[i+1]` — the wrap also checks the last→first boundary (a fully sorted array has zero descents).

## Approach 1 — Descent count (the repo's version, optimal)

```kotlin
class CheckkIfArrayIsSortedAndRotated {
    /**
     * @param nums input array
     * @return     true iff sorted and rotated
     */
    fun check(nums: IntArray): Boolean {
        var count = 0
        val n = nums.size

        for (i in 0 until n) {
            if (nums[i] > nums[(i + 1) % n]) {
                count++
            }
        }
        return count <= 1
    }
}
```

```java
public class CheckIfArrayIsSortedAndRotated {
    /**
     * @param nums input array
     * @return     true iff sorted and rotated
     */
    public boolean check(int[] nums) {
        int count = 0;
        int n = nums.length;

        for (int i = 0; i < n; i++) {
            if (nums[i] > nums[(i + 1) % n]) count++;
        }
        return count <= 1;
    }
}
```

```cpp
#include <vector>

class CheckIfArrayIsSortedAndRotated {
public:
    /**
     * @param nums input array
     * @return     true iff sorted and rotated
     */
    bool check(std::vector<int>& nums) {
        int count = 0;
        int n = nums.size();

        for (int i = 0; i < n; i++) {
            if (nums[i] > nums[(i + 1) % n]) count++;
        }
        return count <= 1;
    }
};
```

```python
def check(nums: list[int]) -> bool:
    """
    @param nums: input array
    @return:     true iff sorted and rotated
    """
    count = sum(1 for i in range(len(nums)) if nums[i] > nums[(i + 1) % len(nums)])
    return count <= 1
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     true iff sorted and rotated
    pub fn check(nums: Vec<i32>) -> bool {
        let n = nums.len();
        let count = (0..n).filter(|&i| nums[i] > nums[(i + 1) % n]).count();
        count <= 1
    }
}
```

## Reading the code — what's actually happening

```kotlin
var count = 0
val n = nums.size
for (i in 0 until n) {
    if (nums[i] > nums[(i + 1) % n]) {
        count++
    }
}
return count <= 1
```

Picture the array wrapped into a circle — `nums[n-1]` sits right next to `nums[0]`. A sorted-rotated array read around that circle is *almost* perfectly increasing, except at exactly one place: the pivot, where the big values end and the small ones begin.

- **`nums[i] > nums[i+1]` detects a "descent" — a drop in the circle.** In a sorted-rotated array, the only drop happens at the pivot (`5 > 1` in `[3,4,5,1,2]`). Everywhere else the values climb or stay equal.
- **`(i + 1) % n` closes the circle.** For `i = n-1`, the "next" element is `nums[0]`, not an out-of-bounds index. This wrap is what makes a *fully sorted* array (pivot at position 0, e.g. `[1,2,3,4]`) count as valid: `4 > 1`? No — zero descents, `count = 0`.
- **`count <= 1` is the shape test.** Zero descents = already sorted (rotated by a full lap). One descent = sorted with a genuine pivot. Two or more descents means the circular order is broken in multiple places — like `[2,1,3,4]` (`2>1` at index 0, then `4>2` across the wrap) — and no single rotation can fix it. Duplicates are handled automatically since `>` (strict) ignores equal neighbors.

Trace `[3,4,5,1,2]`: `3>4` no, `4>5` no, `5>1` **yes** (count 1), `1>2` no, `2>3` (wrap) no → `count=1` → `true` ✓.

## Dry run

**Input:** `nums = [3,4,5,1,2]`.

```
3>4? no.  4>5? no.  5>1? yes (1).  1>2? no.  2>3 (wrap)? no.
count = 1 <= 1 -> true ✓
Input: [2,1,3,4]: 2>1 (1).  1>3 no.  3>4 no.  4>2 wrap (2).  count=2 -> false ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Find Minimum In Rotated Sorted Array** ([1.x](../ch01-binary-search/pattern-primer.md)) — the pivot hunt.
- **Interview follow-up:** "Why does ≤ 1 descents characterize the shape?" A sorted-rotated array is sorted except at the pivot — exactly one descent. Fully sorted (pivot at 0) has zero. Two or more descents means the cyclic order is broken.
