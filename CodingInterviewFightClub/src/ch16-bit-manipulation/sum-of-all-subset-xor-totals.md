# 16.6 Sum Of All Subset XOR Totals

> **Source:** [`src/main/kotlin/bitset/SumOfAllSubsetXorTotal.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/bitset/SumOfAllSubsetXorTotal.kt)
> **Pattern:** bit-count formula · **Core page**

## The Problem

Given an array of integers, return the **sum of the XOR totals of every subset** (the XOR of an empty subset is 0).

- Constraints: $1 \le n \le 12$; values ≤ 20 bits.

## Examples

```
Input:  nums = [1,3]         -> Output: 6   (subsets: 0, 1, 3, 1^3=2; sum 6)
Input:  nums = [5,1,6]       -> Output: 28
```

## Intuition — don't enumerate subsets; count per bit

There are $2^n$ subsets — enumerating them at $n = 12$ is fine, but the *insight* version is one formula. For each **bit position**, ask: "how many subset XOR totals have this bit set?"

> If *any* element has bit `i` set, then **exactly half** of all subsets — $2^{n-1}$ — have bit `i` set in their XOR.

Why: pick one element `e` with bit `i` set. Partition all subsets into pairs `(S, S ∪ {e})`. Exactly one of each pair has bit `i` set in its XOR (adding `e` flips it). So the bit contributes:

$$
\text{bitValue} \times 2^{n-1}
$$

for every bit set in *any* element. The OR of all elements collects the "any element has this bit" condition, and the shift multiplies by $2^{n-1}$:

```kotlin
var result = 0
for (num in nums) result = result or num    // which bits appear at all
return result shl (nums.size - 1)           // each such bit is set in 2^(n-1) subsets
```

**Why `shl (n-1)`?** Setting a bit at position `i` in `result` means the answer includes $2^i \cdot 2^{n-1} = 2^{i+n-1}$ — which is exactly `result` (with bit `i` set) shifted left by `n-1`. One shift does all bits at once.

## Approach 1 — Enumerate all 2^n subsets (O(2^n))

Recursive include/exclude accumulating XORs: correct, and the brute-force baseline this formula replaces.

## Approach 2 — The per-bit count formula (the repo's version, optimal)

```kotlin
class SumOfAllSubsetXorTotal {
    /**
     * @param nums input array
     * @return     sum of XOR totals over all subsets
     */
    fun subsetXORSum(nums: IntArray): Int {
        var result = 0
        // Capture each bit that is set in any of the elements
        for (num in nums) {
            result = result or num
        }
        // Each such bit is set in exactly 2^(n-1) subset XOR totals
        return result shl (nums.size - 1)
    }
}
```

```java
public class SumOfAllSubsetXorTotals {
    /**
     * @param nums input array
     * @return     sum of XOR totals over all subsets
     */
    public int subsetXORSum(int[] nums) {
        int result = 0;
        for (int num : nums) result |= num;        // which bits appear at all
        return result << (nums.length - 1);        // each is set in 2^(n-1) subsets
    }
}
```

```cpp
#include <vector>

class SumOfAllSubsetXorTotals {
public:
    /**
     * @param nums input array
     * @return     sum of XOR totals over all subsets
     */
    int subsetXORSum(std::vector<int>& nums) {
        int result = 0;
        for (int num : nums) result |= num;        // which bits appear at all
        return result << (nums.size() - 1);        // each is set in 2^(n-1) subsets
    }
};
```

```python
def subset_xor_sum(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     sum of XOR totals over all subsets
    """
    result = 0
    for num in nums:
        result |= num                  # which bits appear at all
    return result << (len(nums) - 1)   # each is set in 2^(n-1) subsets
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     sum of XOR totals over all subsets
    pub fn subset_xor_sum(nums: Vec<i32>) -> i32 {
        let result = nums.iter().fold(0, |acc, x| acc | x);   // which bits appear at all
        result << (nums.len() - 1)                            // each is set in 2^(n-1) subsets
    }
}
```

## Reading the code — what's actually happening

```kotlin
var result = 0
for (num in nums) {
    result = result or num
}
return result shl (nums.size - 1)
```

- **The `for` loop builds the "union of all bits" with OR.** For each element, `result or num` turns on every bit that appears in *any* element. Bits that never appear in any number can never be set in any subset's XOR, so they contribute nothing — ORing them all is the cheap way to ask "which bit positions matter at all?"
- **`shl (nums.size - 1)` multiplies by $2^{n-1}$ — for every bit at once.** Suppose bit `i` made it into `result`. It contributes `2^i × 2^(n-1) = 2^(i + n - 1)` to the total, because exactly half of the $2^n$ subsets have that bit set in their XOR. Shifting `result` left by `n-1` places adds exactly `n-1` zeros after every set bit — which is precisely multiplying each bit's contribution by $2^{n-1}$. One shift handles all bits simultaneously.
- **Why exactly half of the subsets?** Pick one element `e` that carries bit `i`, and pair every subset `S` with `S ∪ {e}`. XORing `e` into a subset flips bit `i`, so exactly one member of each pair has the bit set. The pairs partition all $2^n$ subsets → $2^n / 2 = 2^{n-1}$.

With `nums = [1, 3]` (bits 0 and 1): `result = 3`, `n-1 = 1`, answer `3 << 1 = 6` — matching the enumeration `0 + 1 + 3 + 2 = 6`. Two bits, two contributions of $2^{1} \cdot 2^{0}$ and $2^{1} \cdot 2^{1}$, and the shift did both at once.

## Dry run

**Input:** `nums = [1,3]` (binary 01, 11), `n = 2`.

```
OR = 1 | 3 = 3 (bits 0 and 1 both appear).
answer = 3 << (2 - 1) = 3 << 1 = 6 ✓
```

Verify by enumeration: subsets `{}`(0), `{1}`(1), `{3}`(3), `{1,3}`(1^3=2); sum = 0+1+3+2 = 6 ✓. Bit 0 is set in the XOR of `{1}` and `{1,3}` — 2 of the 4 subsets = $2^{n-1}$. Same for bit 1 (`{3}` and `{1,3}`). Each bit contributes $2^{n-1}$ times its value.

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

- **Subset sum / combination-sum family** — the enumeration versions ([12.1](../ch12-backtracking/subsets.md)) are the brute-force baseline this formula replaces.
- **Counting-bits / per-bit contribution pattern** — "sum over subsets" problems often decompose per bit; this is the cleanest instance.
- **Interview follow-up:** "Why exactly $2^{n-1}$ subsets per set bit?" Pair each subset `S` with `S ∪ {e}` where `e` is one fixed element carrying the bit. Exactly one of each pair has the bit set in its XOR (adding `e` flips it), and the pairs partition all $2^n$ subsets — so the count is $2^n / 2 = 2^{n-1}$.
