# 10.11 Subarray Sums Divisible By K

> **Source:** [`src/main/kotlin/array/prefixsum/SubArraySumsDivisibleByK.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/SubArraySumsDivisibleByK.kt)
> **Pattern:** prefix-sum remainders with `floorMod` · **Core page**

## The Problem

Given `nums` and `k`, count the **number of contiguous subarrays** whose sum is divisible by `k`.

- Constraints: $1 \le n \le 3 \times 10^4$; values may be **negative**; $1 \le k \le 10^4$.

## Examples

```
Input:  nums = [4,5,0,-2,-3,1], k = 5   -> Output: 7
Input:  nums = [5], k = 9               -> Output: 0
```

## Intuition — same as [10.8](subarray-sum-equals-k.md), with *remainders* instead of exact sums

`sum(i..j) % k == 0 ⟺ prefix[j] % k == prefix[i-1] % k`. So count pairs of prefix sums with **equal remainders** — the frequency map keyed by `prefix % k` instead of the raw prefix:

```
remFreq = {0: 1}              # the empty prefix (remainder 0)
sum = 0; count = 0
for num in nums:
    sum += num
    rem = Math.floorMod(sum, k)          # NOT sum % k!
    count += remFreq[rem]                # earlier prefixes with the same remainder
    remFreq[rem]++
```

**Why `floorMod` and not `%`?** Kotlin/Java's `%` returns a *negative* remainder for negative sums (`-1 % 5 == -1`) — but remainders must live in `[0, k)`, because `-1` and `4` are the *same* residue class mod 5. `Math.floorMod(-1, 5) == 4` normalizes them. This is the page's central gotcha — every language must use its floor-mod (`%` in Python/Rust is already floored; Java/Kotlin/C++ need the explicit fix).

**Why does "same remainder" ⟺ "difference divisible by k"?** `a ≡ b (mod k)` iff `k | (a - b)` — and the subarray sum is exactly a difference of prefix sums. The map counts earlier prefixes with the matching residue; each one forms a valid subarray ending here.

**The `{0: 1}` seed** — the empty prefix has remainder 0, so a prefix that's itself divisible by k (`rem == 0`) counts as "one subarray from index 0".

## Approach 1 — All subarrays (O(n^2))

Sum every window, check `% k`: correct, quadratic.

## Approach 2 — Remainder-frequency map (the repo's version, optimal)

```kotlin
class SubArraySumsDivisibleByK {
    /**
     * @param A input array (may be negative)
     * @param K divisor
     * @return  number of contiguous subarrays with sum divisible by K
     */
    fun subarraysDivByK(A: IntArray, K: Int): Int {
        var ans = 0
        var sum = 0
        val hm = mutableMapOf<Int, Int>()
        hm[0] = 1                              // the empty prefix (remainder 0)

        for (num in A) {
            sum += num
            val rem = Math.floorMod(sum, K)    // normalize negative remainders!
            ans += hm.getOrDefault(rem, 0)
            hm[rem] = hm.getOrDefault(rem, 0) + 1
        }
        return ans
    }
}
```

```java
import java.util.*;

public class SubarraySumsDivisibleByK {
    /**
     * @param nums input array (may be negative)
     * @param k    divisor
     * @return     number of contiguous subarrays with sum divisible by k
     */
    public int subarraysDivByK(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        freq.put(0, 1);                              // the empty prefix (remainder 0)

        int sum = 0, count = 0;
        for (int num : nums) {
            sum += num;
            int rem = Math.floorMod(sum, k);         // normalize negative remainders!
            count += freq.getOrDefault(rem, 0);
            freq.merge(rem, 1, Integer::sum);
        }
        return count;
    }
}
```

```cpp
#include <unordered_map>
#include <vector>

class SubarraySumsDivisibleByK {
public:
    /**
     * @param nums input array (may be negative)
     * @param k    divisor
     * @return     number of contiguous subarrays with sum divisible by k
     */
    int subarraysDivByK(std::vector<int>& nums, int k) {
        std::unordered_map<int, int> freq;
        freq[0] = 1;                                 // the empty prefix (remainder 0)

        int sum = 0, count = 0;
        for (int num : nums) {
            sum += num;
            int rem = ((sum % k) + k) % k;           // normalize negative remainders!
            count += freq[rem];
            freq[rem]++;
        }
        return count;
    }
};
```

```python
def subarrays_div_by_k(nums: list[int], k: int) -> int:
    """
    @param nums: input array (may be negative)
    @param k:    divisor
    @return:     number of contiguous subarrays with sum divisible by k
    """
    freq = {0: 1}                    # the empty prefix (remainder 0)
    count = 0
    total = 0

    for num in nums:
        total += num
        rem = total % k              # Python's % is already floored
        count += freq.get(rem, 0)
        freq[rem] = freq.get(rem, 0) + 1
    return count
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param nums input array (may be negative)
    /// @param k    divisor
    /// @return     number of contiguous subarrays with sum divisible by k
    pub fn subarrays_div_by_k(nums: Vec<i32>, k: i32) -> i32 {
        let mut freq: HashMap<i32, i32> = HashMap::new();
        freq.insert(0, 1);                   // the empty prefix (remainder 0)

        let (mut count, mut total) = (0, 0);
        for num in nums {
            total += num;
            let rem = total.rem_euclid(k);   // Rust's euclid modulo: non-negative
            count += *freq.get(&rem).unwrap_or(&0);
            *freq.entry(rem).or_insert(0) += 1;
        }
        count
    }
}
```

## Dry run

**Input:** `nums = [4,5,0,-2,-3,1]`, `k = 5`.

```
freq = {0:1}, sum=0, count=0
num=4:  sum=4.  rem=floorMod(4,5)=4.  count += freq[4]=0.  freq[4]=1
num=5:  sum=9.  rem=4.               count += 1 -> 1.       freq[4]=2
num=0:  sum=9.  rem=4.               count += 2 -> 3.       freq[4]=3
num=-2: sum=7.  rem=floorMod(7,5)=2. count += 0.            freq[2]=1
num=-3: sum=4.  rem=4.               count += 3 -> 6.       freq[4]=4
num=1:  sum=5.  rem=0.               count += freq[0]=1 -> 7.  freq[0]=2

Output: 7 ✓
```

The `floorMod` in action: at `sum = -1` (if it appeared), `floorMod(-1, 5) = 4` — *not* `-1` — so it joins the residue-4 family where the true divisible-pairs live. The trace shows the counting: each new prefix with remainder `r` pairs with *every* earlier prefix that had `r` — three earlier `4`s at `num=0` contribute 3 windows, and the final `rem=0` pairs with the seeded empty prefix.

## Complexity

**Time.** One pass with O(1) map ops:

$$
T(n) = O(n)
$$

**Space.** The remainder map:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Subarray Sum Equals K** ([10.8](subarray-sum-equals-k.md)) — exact-sum sibling; this page swaps the key from `sum` to `sum % k`.
- **Continuous Subarray Sum** (`array/prefixsum/ContinuousSubarraySum.kt`) — "exists a length-≥2 subarray divisible by k": store *first* occurrence indices instead of counts.
- **Make Sum Divisible By P** (`google/`) — remove the *shortest* subarray to fix divisibility: the remainder map tracking earliest positions.
- **Interview follow-up:** "Why is `%` on negatives a bug here?" `-1 % 5 == -1` in JVM/C++ — a different bucket than `4`, even though `-1 ≡ 4 (mod 5)`. `floorMod` (or `((x % k) + k) % k`) folds both into `[0, k)`, which is the *only* domain where "same remainder ⟺ difference divisible by k" holds.
