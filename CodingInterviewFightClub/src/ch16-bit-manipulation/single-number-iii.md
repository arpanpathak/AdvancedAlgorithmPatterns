# 16.4 Single Number III

> **Source:** [`src/main/kotlin/bitset/SingleNumber3.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/bitset/SingleNumber3.kt) (the repo's file has a bug on the lowbit line; the corrected version is below)
> **Pattern:** XOR + lowbit split · **Core page**

## The Problem

Every element appears **twice** except **two** elements, which appear once. Find both.

- Constraints: $2 \le n \le 3 \times 10^4$; linear time, constant space.

## Examples

```
Input:  nums = [1,2,1,3,2,5]   -> Output: [3,5]  (either order)
```

## Intuition — XOR finds the XOR of the two; the lowbit separates them

XOR everything: the pairs cancel, leaving `xorSum = a ⊕ b` where `a, b` are the two singletons. Now `a ≠ b`, so `xorSum ≠ 0` — **they differ in at least one bit**. Pick one differing bit (the **rightmost set bit** of `xorSum`):

$$
\text{lowbit} = xorSum \,\&\, -xorSum
$$

Every number is now in exactly one of two groups: those with that bit set, those without. Crucially, **`a` and `b` land in different groups** (they differ at that bit) while every *pair* stays together (both copies have the same bits). XOR each group separately:

```
a = 0; b = 0
for num in nums:
    if num and lowbit != 0: a = a xor num
    else:                   b = b xor num
```

Group 1's XOR = `a` (pairs cancel, `b` is not in the group), group 2's XOR = `b`. Two passes, $O(n)$, $O(1)$ space.

**The repo bug (noted):** the file writes `val rightmostSetBit = xorSum and xorSum` — that's just `xorSum`, not the lowbit — which would mis-split whenever `a ⊕ b` has more than one set bit. The correct two's-complement idiom is `xorSum and -xorSum` (or `xorSum and (xorSum.inv() + 1)`). The book shows the corrected line; a `> **Repo note:**` style flag is in order.

**Why is the lowbit the right pick?** Any bit where `a` and `b` differ works; the lowest one is a one-line computation and is guaranteed nonzero (since `xorSum ≠ 0`). "Pick any differing bit" is the reasoning; the lowbit is the implementation.

## Approach 1 — Hash set (O(n) space)

Add/remove each element; two remain. Correct, but not constant space.

## Approach 2 — XOR + lowbit partition (the repo's version, corrected, optimal)

```kotlin
class SingleNumber3 {
    /**
     * @param nums every element appears twice except two
     * @return     the two elements that appear once
     */
    fun singleNumber(nums: IntArray): IntArray {
        var xorSum = 0
        for (num in nums) {
            xorSum = xorSum.xor(num)               // a xor b (pairs cancel)
        }

        // Rightmost set bit of a xor b — where a and b differ.
        // (The repo file writes `xorSum and xorSum`, which is a bug: it must be `-xorSum`.)
        val rightmostSetBit = xorSum and -xorSum

        var (a, b) = listOf(0, 0)
        for (num in nums) {
            if (num and rightmostSetBit != 0) {
                a = a.xor(num)                     // group with the bit set
            } else {
                b = b.xor(num)                     // group without it
            }
        }
        return intArrayOf(a, b)
    }
}
```

```java
public class SingleNumberIII {
    /**
     * @param nums every element appears twice except two
     * @return     the two elements that appear once
     */
    public int[] singleNumber(int[] nums) {
        int xor = 0;
        for (int num : nums) xor ^= num;           // a xor b

        int lowbit = xor & -xor;                   // rightmost set bit — where a and b differ
        int a = 0, b = 0;
        for (int num : nums) {
            if ((num & lowbit) != 0) a ^= num;     // group with the bit set
            else b ^= num;                         // group without it
        }
        return new int[]{a, b};
    }
}
```

```cpp
#include <vector>

class SingleNumberIII {
public:
    /**
     * @param nums every element appears twice except two
     * @return     the two elements that appear once
     */
    std::vector<int> singleNumber(std::vector<int>& nums) {
        int xorSum = 0;
        for (int num : nums) xorSum ^= num;        // a xor b

        int lowbit = xorSum & -xorSum;             // rightmost set bit — where a and b differ
        int a = 0, b = 0;
        for (int num : nums) {
            if (num & lowbit) a ^= num;            // group with the bit set
            else b ^= num;                         // group without it
        }
        return {a, b};
    }
};
```

```python
def single_number(nums: list[int]) -> list[int]:
    """
    @param nums: every element appears twice except two
    @return:     the two elements that appear once
    """
    xor = 0
    for num in nums:
        xor ^= num                          # a xor b

    lowbit = xor & -xor                     # rightmost set bit — where a and b differ
    a = b = 0
    for num in nums:
        if num & lowbit:
            a ^= num                        # group with the bit set
        else:
            b ^= num                        # group without it
    return [a, b]
```

```rust
impl Solution {
    /// @param nums every element appears twice except two
    /// @return     the two elements that appear once
    pub fn single_number(nums: Vec<i32>) -> Vec<i32> {
        let xor = nums.iter().fold(0, |acc, x| acc ^ x);   // a xor b

        let lowbit = xor & -xor;                           // rightmost set bit
        let mut a = 0;
        let mut b = 0;
        for &num in &nums {
            if num & lowbit != 0 { a ^= num; }             // group with the bit set
            else { b ^= num; }                             // group without it
        }
        vec![a, b]
    }
}
```

## Dry run

**Input:** `nums = [1,2,1,3,2,5]`.

```
xor pass: 1^2^1^3^2^5 = 3^5 = 6 (binary 110).
lowbit = 6 & -6 = 6 & 2 = 2 (binary 010)   # bit 1 is where 3 (011) and 5 (101) differ

partition by bit 1 set:
  with bit 1 set:  2 (010), 3 (011), 2 (010)  -> xor = 2^3^2 = 3
  without bit 1:   1 (001), 1 (001), 5 (101)  -> xor = 1^1^5 = 5

Output: [3, 5] ✓
```

The magic is visible in the partition: the two copies of `2` both land in the "bit set" group and cancel, while `3` and `5` — the two singletons — are separated by the very bit where they differ. Each group's XOR ends up being exactly one singleton.

## Complexity

**Time.** Two passes:

$$
T(n) = O(n)
$$

**Space.** Three variables:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Single Number** ([16.3](single-number.md)) — one singleton; the first pass of this algorithm without the split.
- **Single Number II** — the mod-3 counting generalization.
- **Two Missing Numbers / find-the-differing-bit family** — the lowbit split is the universal "separate by a distinguishing bit" tool.
- **Interview follow-up:** "Why does the lowbit split keep every pair together?" Both copies of a number have identical bits, so they always land in the same group and cancel; the two singletons differ *at the chosen bit*, so they land in different groups and each survives alone. The correctness is entirely in that one observation.
