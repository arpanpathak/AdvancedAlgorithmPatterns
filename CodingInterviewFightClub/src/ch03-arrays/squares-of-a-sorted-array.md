# 3.14 Squares Of A Sorted Array

> **Source**: [`src/main/kotlin/array/sorting/SquaresOfASortedArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/sorting/SquaresOfASortedArray.kt)
> **Pattern**: two-pointer from the ends · **Core page**

## The Problem

The squares of a sorted array, sorted (the input has negatives).

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  nums = [-4,-1,0,3,10]   -> Output: [0,1,9,16,100]
```

## Intuition — the largest square is at one of the ends

The extremes hold the biggest absolute values — fill the result from the back:

```kotlin
val result = IntArray(nums.size)
var (left, right, index) = listOf(0, nums.lastIndex, nums.lastIndex)

while (left <= right) {
    when {
        abs(nums[left]) > abs(nums[right]) -> { result[index--] = nums[left] * nums[left]; left++ }
        else -> { result[index--] = nums[right] * nums[right]; right-- }
    }
}
return result
```

## Approach 1 — Square then sort (O(n log n))

The lazy version: map, sort.

## Approach 2 — Two-pointer merge (the repo's version, optimal)

```kotlin
class SquaresOfASortedArray {
    /**
     * @param nums sorted array (may have negatives)
     * @return     sorted squares
     */
    fun sortedSquares(nums: IntArray): IntArray {
        val result = IntArray(nums.size)
        var (left, right, index) = listOf(0, nums.lastIndex, nums.lastIndex)

        while (left <= right) {
            when {
                abs(nums[left]) > abs(nums[right]) -> {
                    result[index--] = nums[left] * nums[left]
                    left++
                }
                else -> {
                    result[index--] = nums[right] * nums[right]
                    right--
                }
            }
        }
        return result
    }
}
```

```java
public class SquaresOfASortedArray {
    /**
     * @param nums sorted array (may have negatives)
     * @return     sorted squares
     */
    public int[] sortedSquares(int[] nums) {
        int[] result = new int[nums.length];
        int left = 0, right = nums.length - 1, index = nums.length - 1;

        while (left <= right) {
            if (Math.abs(nums[left]) > Math.abs(nums[right])) {
                result[index--] = nums[left] * nums[left];
                left++;
            } else {
                result[index--] = nums[right] * nums[right];
                right--;
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
     * @param nums sorted array (may have negatives)
     * @return     sorted squares
     */
    std::vector<int> sortedSquares(std::vector<int>& nums) {
        std::vector<int> result(nums.size());
        int left = 0, right = nums.size() - 1, index = nums.size() - 1;

        while (left <= right) {
            if (std::abs(nums[left]) > std::abs(nums[right])) {
                result[index--] = nums[left] * nums[left];
                left++;
            } else {
                result[index--] = nums[right] * nums[right];
                right--;
            }
        }
        return result;
    }
};
```

```python
def sorted_squares(nums: list[int]) -> list[int]:
    """
    @param nums: sorted array (may have negatives)
    @return:     sorted squares
    """
    result = [0] * len(nums)
    left, right = 0, len(nums) - 1

    for index in range(len(nums) - 1, -1, -1):
        if abs(nums[left]) > abs(nums[right]):
            result[index] = nums[left] * nums[left]
            left += 1
        else:
            result[index] = nums[right] * nums[right]
            right -= 1

    return result
```

```rust
impl Solution {
    /// @param nums sorted array (may have negatives)
    /// @return     sorted squares
    pub fn sorted_squares(nums: Vec<i32>) -> Vec<i32> {
        let mut result = vec![0; nums.len()];
        let (mut left, mut right) = (0, nums.len() - 1);

        for index in (0..nums.len()).rev() {
            if nums[left].abs() > nums[right].abs() {
                result[index] = nums[left] * nums[left];
                left += 1;
            } else {
                result[index] = nums[right] * nums[right];
                right -= 1;
            }
        }
        result
    }
}
```

## Dry run

**Input:** `nums = [-4,-1,0,3,10]`.

```
|-4| > |10|? no -> result[4]=100.  |-4| > |3|? yes -> result[3]=16.  |-1| > |3|? no -> result[2]=9.
|-1| > |0|? yes -> result[1]=1.  result[0]=0.
Output: [0,1,9,16,100] ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The result:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why fill from the back?" The squares grow with |value| — the two extremes are always the largest remaining, so filling backward keeps the result sorted with no comparisons beyond the ends.
