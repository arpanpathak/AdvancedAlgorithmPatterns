# 16.3 Single Number

> **Source:** [`src/main/kotlin/bitset/SingleNumber.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/bitset/SingleNumber.kt)
> **Pattern:** XOR cancellation · **Core page**

## The Problem

Every element appears **twice** except one. Find that single element.

- Constraints: $1 \le n \le 3 \times 10^4$; linear time and **constant extra space** required.

## Examples

```
Input:  nums = [2,2,1]         -> Output: 1
Input:  nums = [4,1,2,1,2]     -> Output: 4
```

## Intuition — XOR everything; pairs vanish, the singleton survives

The identity from the [primer](pattern-primer.md):

$$
x \oplus x = 0, \qquad x \oplus 0 = x
$$

XOR is commutative and associative, so XORing the whole array cancels every pair and leaves exactly the element that appears once:

```
xorSum = 0
for num in nums: xorSum = xorSum xor num
return xorSum
```

**Why is this the "constant space" answer the problem demands?** A hash-map approach needs $O(n)$ space; sorting needs $O(n \log n)$ time (and mutates). XOR is $O(n)$ time, $O(1)$ space, no mutation — it *is* the constraint-aware answer. The "pair cancellation" phrasing is the one to say out loud.

**Why does it work with interleaved pairs?** `[4,1,2,1,2]`: `4⊕1⊕2⊕1⊕2` — associativity lets us regroup as `4 ⊕ (1⊕1) ⊕ (2⊕2) = 4 ⊕ 0 ⊕ 0 = 4`. The order never matters.

## Approach 1 — Hash map / set (O(n) space)

Count or collect-and-remove: correct, but violates the constant-space requirement.

## Approach 2 — XOR everything (the repo's version, optimal)

```kotlin
class SingleNumber {
    /**
     * @param nums every element appears twice except one
     * @return     the element that appears once
     */
    fun singleNumber(nums: IntArray): Int {
        var xorSum = 0
        nums.forEach { xorSum = xorSum xor it }   // pairs cancel, singleton survives
        return xorSum
    }
}
```

```java
public class SingleNumber {
    /**
     * @param nums every element appears twice except one
     * @return     the element that appears once
     */
    public int singleNumber(int[] nums) {
        int xor = 0;
        for (int num : nums) xor ^= num;          // pairs cancel, singleton survives
        return xor;
    }
}
```

```cpp
#include <vector>

class SingleNumber {
public:
    /**
     * @param nums every element appears twice except one
     * @return     the element that appears once
     */
    int singleNumber(std::vector<int>& nums) {
        int xorSum = 0;
        for (int num : nums) xorSum ^= num;       // pairs cancel, singleton survives
        return xorSum;
    }
};
```

```python
def single_number(nums: list[int]) -> int:
    """
    @param nums: every element appears twice except one
    @return:     the element that appears once
    """
    xor = 0
    for num in nums:
        xor ^= num                   # pairs cancel, singleton survives
    return xor
```

```rust
impl Solution {
    /// @param nums every element appears twice except one
    /// @return     the element that appears once
    pub fn single_number(nums: Vec<i32>) -> i32 {
        nums.iter().fold(0, |acc, x| acc ^ x)   // pairs cancel, singleton survives
    }
}
```

## Dry run

**Input:** `nums = [4,1,2,1,2]`.

```
xor = 0
4  -> xor = 0 ^ 4  = 4
1  -> xor = 4 ^ 1  = 5
2  -> xor = 5 ^ 2  = 7
1  -> xor = 7 ^ 1  = 6
2  -> xor = 6 ^ 2  = 4

Output: 4 ✓
```

The intermediate values look like noise, but associativity is the proof: regrouping as `4 ⊕ (1⊕1) ⊕ (2⊕2) = 4 ⊕ 0 ⊕ 0 = 4` shows exactly why the answer emerges. XOR as "addition without carries" is why the pairs vanish regardless of interleaving.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** One variable:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Single Number II** — every element appears *three* times except one: the bit-level counting version (count each bit mod 3) — the "XOR generalizes to modular counting" insight.
- **Single Number III** ([16.4](single-number-iii.md)) — two singletons: XOR + lowbit split.
- **Missing Number** — XOR the array with all indices: the missing value emerges from the same cancellation.
- **Interview follow-up:** "Why does this satisfy the constant-space requirement when a hash map doesn't?" The XOR accumulates the answer *in place* — one integer holds the entire state, because pair cancellation needs no bookkeeping about which elements were seen. The problem's space constraint is the giveaway that a bit-level identity (not a data structure) is the intended solution.
