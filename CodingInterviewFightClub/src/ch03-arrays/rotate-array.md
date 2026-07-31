# 3.13 Rotate Array

> **Source:** [`src/main/kotlin/array/twopointer/RotateArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/twopointer/RotateArray.kt)
> **Pattern:** reverse, reverse, reverse · **Core page**

## The Problem

Rotate `nums` **right** by `k` steps in place, O(1) space.

- Constraints: $1 \le n \le 10^5$; $k$ can exceed `n`.

## Examples

```
Input:  nums = [1,2,3,4,5,6,7], k = 3   -> Output: [5,6,7,1,2,3,4]
Input:  nums = [-1,-100,3,99], k = 2    -> Output: [3,99,-1,-100]
```

## Intuition — three reverses move every element exactly once

The classic in-place rotation:

```
reverse(nums, 0, n-1)      # whole array:   [7,6,5,4,3,2,1]
reverse(nums, 0, k-1)      # first k:       [5,6,7,4,3,2,1]
reverse(nums, k, n-1)      # the rest:      [5,6,7,1,2,3,4]
```

**Why does this work?** A right-rotation moves the last `k` elements to the front. Reversing the whole array puts them there (in reverse order); reversing the two blocks separately fixes their internal order. Three O(n/2) passes, zero extra space.

**Why `k % n` first?** Rotating by `n` returns the array unchanged — `k % n` is the effective shift. Without the mod, `k = 10⁵` on a small array still works (reverses are O(n)) but the block sizes would misalign when `k > n`.

**The repo's `also`-swap** — `nums[s] = nums[e].also { nums[e] = nums[s] }` is the idiomatic Kotlin exchange inside the reverse helper.

## Approach 1 — Extra array (O(n) space)

Copy `nums[(i - k) mod n]` into a new array: trivial, violates the in-place requirement.

## Approach 2 — Three reverses (the repo's version, optimal)

```kotlin
class RotateArray {
    /**
     * @param nums array to rotate (in place)
     * @param k    steps to rotate right
     */
    fun rotate(nums: IntArray, k: Int) {
        val n = nums.size
        val steps = k % n                    // effective shift

        reverse(nums, 0, n - 1)
        reverse(nums, 0, steps - 1)
        reverse(nums, steps, n - 1)
    }

    private fun reverse(nums: IntArray, start: Int, end: Int) {
        var s = start
        var e = end
        while (s < e) {
            nums[s] = nums[e].also { nums[e] = nums[s] }
            s++
            e--
        }
    }
}
```

```java
public class RotateArray {
    /**
     * @param nums array to rotate (in place)
     * @param k    steps to rotate right
     */
    public void rotate(int[] nums, int k) {
        int n = nums.length;
        k %= n;                              // effective shift

        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
    }

    private void reverse(int[] a, int l, int r) {
        while (l < r) {
            int t = a[l]; a[l] = a[r]; a[r] = t;
            l++; r--;
        }
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class RotateArray {
public:
    /**
     * @param nums array to rotate (in place)
     * @param k    steps to rotate right
     */
    void rotate(std::vector<int>& nums, int k) {
        int n = nums.size();
        k %= n;                              // effective shift

        std::reverse(nums.begin(), nums.end());
        std::reverse(nums.begin(), nums.begin() + k);
        std::reverse(nums.begin() + k, nums.end());
    }
};
```

```python
def rotate(nums: list[int], k: int) -> None:
    """
    @param nums: array to rotate (in place)
    @param k:    steps to rotate right
    """
    n = len(nums)
    k %= n                                  # effective shift

    nums.reverse()
    nums[:k] = reversed(nums[:k])
    nums[k:] = reversed(nums[k:])
```

```rust
impl Solution {
    /// @param nums array to rotate (in place)
    /// @param k    steps to rotate right
    pub fn rotate(nums: &mut Vec<i32>, k: i32) {
        let n = nums.len();
        let k = (k as usize) % n;           // effective shift

        nums.reverse();
        nums[..k].reverse();
        nums[k..].reverse();
    }
}
```

## Dry run

**Input:** `nums = [1,2,3,4,5,6,7]`, `k = 3`.

```
reverse all:  [7,6,5,4,3,2,1]
reverse 0..2: [5,6,7,4,3,2,1]     (the last-3 elements, now in order, at the front)
reverse 3..6: [5,6,7,1,2,3,4]     (the first-4 elements restored)

Output: [5,6,7,1,2,3,4] ✓
```

The three reverses compose: the whole-reverse moves the last `k` to the front (reversed), and the two block-reverses restore internal order. `k = 10` on this array: `10 % 7 = 3` → same result — the mod is what keeps the block boundaries inside the array.

## Complexity

**Time.** Three half-array passes:

$$
T(n) = O(n)
$$

**Space.** In place:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Rotate List** (`linkedlist/RotateList.kt`) — the linked-list twin: find the new tail, rewire pointers (no reverses needed).
- **Reverse Words In A String** ([9.6](../ch09-strings/reverse-words-in-a-string.md)) — the same reverse-reverse-reverse choreography for word order.
- **Interview follow-up:** "Why `k % n`?" Rotating by a full cycle returns the array unchanged — `k % n` is the *effective* displacement. It's also what keeps `reverse(0, k-1)` and `reverse(k, n-1)` as valid (non-empty, in-bounds) blocks for every k.
