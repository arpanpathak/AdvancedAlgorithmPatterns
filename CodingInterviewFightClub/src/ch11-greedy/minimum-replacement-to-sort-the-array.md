# 11.33 Minimum Replacement To Sort The Array

> **Source**: [`src/main/kotlin/greedy/MinimumReplacementToSortTheArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/MinimumReplacementToSortTheArray.kt)
> **Pattern**: right-to-left split · **Core page**

## The Problem

Min operations splitting elements into positives so the array becomes non-decreasing.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  nums = [3,9,3]   -> Output: 2
Input:  nums = [2,10,20,19,1] -> Output: 47
```

## Intuition — walk right-to-left; split each element to fit under the next

The rightmost stays; each earlier element must become pieces ≤ the current bound — minimal pieces via `ceil(nums[i] / bound)`, minimal bound via `nums[i] / pieces`:

```kotlin
var answer = 0L
val n = nums.size

for (i in n - 2 downTo 0) {
    if (nums[i] <= nums[i + 1]) continue

    val pieces = (nums[i] + nums[i + 1] - 1) / nums[i + 1]
    answer += pieces - 1
    nums[i] = nums[i] / pieces
}
return answer
```

## Approach 1 — Right-to-left split (the repo's version, optimal)

```kotlin
class MinimumReplacementToSortTheArray {
    /**
     * @param nums input array (mutated)
     * @return     min splitting operations
     */
    fun minimumReplacement(nums: IntArray): Long {
        var answer = 0L
        val n = nums.size

        for (i in n - 2 downTo 0) {
            if (nums[i] <= nums[i + 1]) continue

            val pieces = (nums[i] + nums[i + 1] - 1) / nums[i + 1]
            answer += pieces - 1
            nums[i] = nums[i] / pieces
        }
        return answer
    }
}
```

```java
public class MinimumReplacementToSortTheArray {
    /**
     * @param nums input array (mutated)
     * @return     min splitting operations
     */
    public long minimumReplacement(int[] nums) {
        long answer = 0;
        int n = nums.length;

        for (int i = n - 2; i >= 0; i--) {
            if (nums[i] <= nums[i + 1]) continue;

            int pieces = (nums[i] + nums[i + 1] - 1) / nums[i + 1];
            answer += pieces - 1;
            nums[i] = nums[i] / pieces;
        }
        return answer;
    }
}
```

```cpp
#include <vector>

class MinimumReplacementToSortTheArray {
public:
    /**
     * @param nums input array (mutated)
     * @return     min splitting operations
     */
    long long minimumReplacement(std::vector<int>& nums) {
        long long answer = 0;
        int n = nums.size();

        for (int i = n - 2; i >= 0; i--) {
            if (nums[i] <= nums[i + 1]) continue;

            int pieces = (nums[i] + nums[i + 1] - 1) / nums[i + 1];
            answer += pieces - 1;
            nums[i] = nums[i] / pieces;
        }
        return answer;
    }
};
```

```python
def minimum_replacement(nums: list[int]) -> int:
    """
    @param nums: input array (mutated)
    @return:     min splitting operations
    """
    answer = 0

    for i in range(len(nums) - 2, -1, -1):
        if nums[i] <= nums[i + 1]:
            continue

        pieces = (nums[i] + nums[i + 1] - 1) // nums[i + 1]
        answer += pieces - 1
        nums[i] = nums[i] // pieces

    return answer
```

```rust
impl Solution {
    /// @param nums input array (mutated)
    /// @return     min splitting operations
    pub fn minimum_replacement(nums: &mut Vec<i32>) -> i64 {
        let mut answer = 0i64;

        for i in (0..nums.len() - 1).rev() {
            if nums[i] <= nums[i + 1] { continue; }

            let pieces = (nums[i] as i64 + nums[i + 1] as i64 - 1) / nums[i + 1] as i64;
            answer += pieces - 1;
            nums[i] = (nums[i] as i64 / pieces) as i32;
        }
        answer
    }
}
```

## Dry run

**Input:** `nums = [3,9,3]`.

```
i=1 (9): 9 > 3.  pieces = (9+3-1)/3 = 3.  answer 2.  nums[1] = 3.
i=0 (3): 3 <= 3.  done.
Output: 2 ✓  ([3,3,3,3] via splitting 9 into three 3s)
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** In place:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why ceil for pieces and floor for the bound?" pieces must fit nums[i] under nums[i+1] — the minimal piece count is the ceil division; then the largest possible piece (for future) is the floor split.
