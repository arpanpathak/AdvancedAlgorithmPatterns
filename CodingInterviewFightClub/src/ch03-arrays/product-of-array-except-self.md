# 3.10 Product Of Array Except Self

> **Source:** [`src/main/kotlin/array/prefixsum/ProductOfArrayExceptSelf.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/ProductOfArrayExceptSelf.kt)
> **Pattern:** prefix/suffix products · **Core page**

## The Problem

Given `nums`, return an array where `answer[i]` = the product of **all elements except `nums[i]`**, **without using division**, in O(n).

- Constraints: $2 \le n \le 10^5$; values fit in `Int`.

## Examples

```
Input:  nums = [1,2,3,4]   -> Output: [24,12,8,6]
Input:  nums = [-1,1,0,-3,3] -> Output: [0,0,9,0,0]
```

## Intuition — `answer[i]` = product of everything left × everything right

The product-except-self decomposes cleanly:

$$
\text{answer}[i] = \left(\prod_{j < i} nums[j]\right) \times \left(\prod_{j > i} nums[j]\right)
$$

Two passes compute it without division: a **left pass** accumulates the running prefix product, a **right pass** multiplies in the running suffix product. Each pass writes into the answer array (or a separate right array), so the final array holds the product of all elements except itself.

**The no-division requirement is the whole problem.** With division it's trivial — `total / nums[i]` — except the zero cases (`[0,0,...]`), which is exactly what the repo's version handles with a zero-count. The *clean* standard answer (what interviews want) is the two-pass prefix/suffix — no division, no zero edge cases, O(1) extra space (reuse the answer array for the prefix, a running variable for the suffix).

**Why does the two-pass work with zeros?** It never divides; every `answer[i]` is literally built from the actual left/right products. `[0,0]`-style inputs fall out correctly by construction — the zero-handling `when` of the division version disappears.

**The repo's division version** is documented here too: it counts zeros (if two or more → all zeros; exactly one → only that position gets the product of the rest; none → plain division). Correct, and a nice "division pitfalls" case study — but the two-pass below is the canonical answer.

## Approach 1 — Division with zero-counting (the repo's version)

`product = product of non-zero elements; zeroes = count of zeros`. Then per index: `zeroes > 1 → 0`; `nums[i] == 0 → product`; else `product / nums[i]`. O(n) time, O(1) space — but division-based, which the problem forbids.

## Approach 2 — Two-pass prefix/suffix products (the clean standard, optimal)

```kotlin
class ProductOfArrayExceptSelf {
    /**
     * @param nums input array
     * @return     answer[i] = product of all elements except nums[i] (no division)
     */
    fun productExceptSelf(nums: IntArray): IntArray {
        val n = nums.size
        val answer = IntArray(n)

        // Left pass: answer[i] = product of everything left of i
        var prefix = 1
        for (i in 0 until n) {
            answer[i] = prefix
            prefix *= nums[i]
        }

        // Right pass: multiply in the product of everything right of i
        var suffix = 1
        for (i in n - 1 downTo 0) {
            answer[i] *= suffix
            suffix *= nums[i]
        }
        return answer
    }
}
```

```java
public class ProductOfArrayExceptSelf {
    /**
     * @param nums input array
     * @return     answer[i] = product of all elements except nums[i] (no division)
     */
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];

        int prefix = 1;                              // left pass
        for (int i = 0; i < n; i++) {
            answer[i] = prefix;
            prefix *= nums[i];
        }

        int suffix = 1;                              // right pass
        for (int i = n - 1; i >= 0; i--) {
            answer[i] *= suffix;
            suffix *= nums[i];
        }
        return answer;
    }
}
```

```cpp
#include <vector>

class ProductOfArrayExceptSelf {
public:
    /**
     * @param nums input array
     * @return     answer[i] = product of all elements except nums[i] (no division)
     */
    std::vector<int> productExceptSelf(std::vector<int>& nums) {
        int n = nums.size();
        std::vector<int> answer(n);

        int prefix = 1;                              // left pass
        for (int i = 0; i < n; i++) {
            answer[i] = prefix;
            prefix *= nums[i];
        }

        int suffix = 1;                              // right pass
        for (int i = n - 1; i >= 0; i--) {
            answer[i] *= suffix;
            suffix *= nums[i];
        }
        return answer;
    }
};
```

```python
def product_except_self(nums: list[int]) -> list[int]:
    """
    @param nums: input array
    @return:     answer[i] = product of all elements except nums[i] (no division)
    """
    n = len(nums)
    answer = [1] * n

    prefix = 1                                 # left pass
    for i in range(n):
        answer[i] = prefix
        prefix *= nums[i]

    suffix = 1                                 # right pass
    for i in range(n - 1, -1, -1):
        answer[i] *= suffix
        suffix *= nums[i]
    return answer
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     answer[i] = product of all elements except nums[i] (no division)
    pub fn product_except_self(nums: Vec<i32>) -> Vec<i32> {
        let n = nums.len();
        let mut answer = vec![1; n];

        let mut prefix = 1;                            // left pass
        for i in 0..n {
            answer[i] = prefix;
            prefix *= nums[i];
        }

        let mut suffix = 1;                            // right pass
        for i in (0..n).rev() {
            answer[i] *= suffix;
            suffix *= nums[i];
        }
        answer
    }
}
```

## Dry run

**Input:** `nums = [1,2,3,4]`.

```
left pass (answer = prefix, then prefix *= nums[i]):
  i=0: answer[0]=1.    prefix=1*1=1
  i=1: answer[1]=1.    prefix=1*2=2
  i=2: answer[2]=2.    prefix=2*3=6
  i=3: answer[3]=6.    prefix=6*4=24
  answer = [1,1,2,6]

right pass (answer[i] *= suffix, then suffix *= nums[i]):
  i=3: answer[3]=6*1=6.    suffix=1*4=4
  i=2: answer[2]=2*4=8.    suffix=4*3=12
  i=1: answer[1]=1*12=12.  suffix=12*2=24
  i=0: answer[0]=1*24=24.  suffix=24*1=24
  answer = [24,12,8,6] ✓
```

The two halves compose exactly: `answer[2] = 2` (product of `[1,2]` on the left) times `4` (product of `[4]` on the right) = 8. The zero case needs no special handling — `[0,0]` yields `[0,0]` because no division ever happens.

## Complexity

**Time.** Two passes:

$$
T(n) = O(n)
$$

**Space.** O(1) extra (the answer array is the output):

$$
S(n) = O(1) \text{ extra}
$$

## Variants & follow-ups

- **The division version** (the repo's `ProductOfArrayExceptSelf.kt`) — `product / nums[i]` with a zero-count `when`: correct, O(1) space, but banned by the problem statement. The zero-counting logic is the useful part — it's the standard "division with zeros" case study.
- **2-D Prefix Sum** (`array/prefixsum/2DPrefixSumImmutable.kt`) — the same "precompute running aggregates, answer queries by combination" idea in two dimensions.
- **Interview follow-up:** "Why is the no-division requirement meaningful?" Division-based answers must special-case zeros (`[0,1]` → `[1,0]`; `[0,0]` → `[0,0]`), and the problem wants the *combinatorial* structure: each answer is genuinely the product of two independent halves. The two-pass version is also division-free in the overflow sense — no `total` that could overflow while `nums[i]` is small.
