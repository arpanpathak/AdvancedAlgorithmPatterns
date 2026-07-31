# 3.14 Squares Of A Sorted Array

> **Source:** [`src/main/kotlin/array/sorting/SquaresOfASortedArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/sorting/SquaresOfASortedArray.kt)
> **Pattern:** two pointers from the extremes · **Core page**

## The Problem

Given `nums` sorted (may contain negatives), return a sorted array of the **squares** of every element — O(n) time.

- Constraints: $1 \le n \le 10^4$; values fit in `Int`.

## Examples

```
Input:  nums = [-4,-1,0,3,10]   -> Output: [0,1,9,16,100]
Input:  nums = [-7,-3,2,3,11]   -> Output: [4,9,9,49,121]
```

## Intuition — the largest square is at one of the *ends*

Squares remove the sign, so the largest square is `max(|nums[left]|, |nums[right]|)` — always an extreme. Fill the result **backward** from the largest, comparing the two ends:

```
left = 0, right = last, index = last
while left <= right:
    if |nums[left]| > |nums[right]|: result[index--] = nums[left]²; left++
    else:                            result[index--] = nums[right]²; right--
```

**Why backward fill?** The extremes produce the *largest* squares, which belong at the *end* of the sorted result — filling from the back matches the values' natural order. A forward fill would need to know the smallest-square side, which is the messy middle.

**Why compare absolutes?** `|nums[left]| > |nums[right]|` picks the larger square without computing both products twice — the [3.1](two-sum-ii.md) two-pointer from the ends, with magnitude as the comparator. The repo's `when` branches on the same test.

## Approach 1 — Square then sort (O(n log n))

`map { it*it }.sorted()`: correct, but ignores that the input is already sorted.

## Approach 2 — Two pointers from the extremes (the repo's version, optimal)

```kotlin
import kotlin.math.abs

class SquaresOfASortedArray {
    /**
     * @param nums sorted array (may contain negatives)
     * @return     sorted squares
     */
    fun sortedSquares(nums: IntArray): IntArray {
        val result = IntArray(nums.size)

        var (left, right, index) = listOf(0, nums.lastIndex, nums.lastIndex)

        while (left <= right) {
            when {
                abs(nums[left]) > abs(nums[right]) -> result[index--] = nums[left] * nums[left++]
                else -> result[index--] = nums[right] * nums[right--]
            }
        }
        return result
    }
}
```

```java
public class SquaresOfASortedArray {
    /**
     * @param nums sorted array (may contain negatives)
     * @return     sorted squares
     */
    public int[] sortedSquares(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        int left = 0, right = n - 1, index = n - 1;

        while (left <= right) {
            if (Math.abs(nums[left]) > Math.abs(nums[right])) {
                result[index--] = nums[left] * nums[left++];
            } else {
                result[index--] = nums[right] * nums[right--];
            }
        }
        return result;
    }
}
```

```cpp
#include <vector>
#include <cstdlib>

class SquaresOfASortedArray {
public:
    /**
     * @param nums sorted array (may contain negatives)
     * @return     sorted squares
     */
    std::vector<int> sortedSquares(std::vector<int>& nums) {
        int n = nums.size();
        std::vector<int> result(n);
        int left = 0, right = n - 1, index = n - 1;

        while (left <= right) {
            if (std::abs(nums[left]) > std::abs(nums[right])) {
                result[index--] = nums[left] * nums[left++];
            } else {
                result[index--] = nums[right] * nums[right--];
            }
        }
        return result;
    }
};
```

```python
def sorted_squares(nums: list[int]) -> list[int]:
    """
    @param nums: sorted array (may contain negatives)
    @return:     sorted squares
    """
    n = len(nums)
    result = [0] * n
    left, right, index = 0, n - 1, n - 1

    while left <= right:
        if abs(nums[left]) > abs(nums[right]):
            result[index] = nums[left] ** 2
            left += 1
        else:
            result[index] = nums[right] ** 2
            right -= 1
        index -= 1
    return result
```

```rust
impl Solution {
    /// @param nums sorted array (may contain negatives)
    /// @return     sorted squares
    pub fn sorted_squares(nums: Vec<i32>) -> Vec<i32> {
        let n = nums.len();
        let mut result = vec![0; n];
        let (mut left, mut right, mut index) = (0usize, n - 1, n - 1);

        while left <= right {
            if nums[left].abs() > nums[right].abs() {
                result[index] = nums[left] * nums[left];
                left += 1;
            } else {
                result[index] = nums[right] * nums[right];
                right -= 1;
            }
            if index == 0 { break; }
            index -= 1;
        }
        result
    }
}
```

## Dry run

**Input:** `nums = [-4,-1,0,3,10]`.

```
left=0 (-4), right=4 (10), index=4.  |−4| < |10| -> result[4] = 100.  right=3.
left=0 (-4), right=3 (3).   |−4| > |3|  -> result[3] = 16.   left=1.
left=1 (-1), right=3 (3).   |−1| < |3|  -> result[2] = 9.    right=2.
left=1 (-1), right=2 (0).   |−1| > |0|  -> result[1] = 1.    left=2.
left=2 (0),  right=2 (0).   equal -> else: result[0] = 0.    right=1.

Output: [0,1,9,16,100] ✓
```

The two-pointer "who has the bigger absolute value" picks the next-largest square every step — the `|−4| vs |3|` comparison at step 2 is the whole algorithm: despite being on the left, `−4`'s square beats the right end's. The backward fill places each winner in its final sorted slot.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The result array:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Two Sum II / Three Sum** ([3.1](two-sum-ii.md), [3.2](three-sum.md)) — the same two-pointer-from-ends choreography.
- **Rotate Array** ([3.13](rotate-array.md)) — the reverse-based in-place sibling.
- **Interview follow-up:** "Why does comparing absolutes at the ends give the sorted squares?" Squaring folds negatives onto the positive axis; the largest squares must come from the extremes, and consuming an extreme shrinks the candidate set to the *next* extremes — a monotone two-pointer that visits every element once.
