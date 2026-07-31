# 16.5 Maximum XOR Of Two Numbers

> **Source:** [`src/main/kotlin/bitset/MaximumXorOfTwoNumsInArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/bitset/MaximumXorOfTwoNumsInArray.kt)
> **Pattern:** binary trie · **Core page**

## The Problem

Given an array of integers, return the **maximum XOR** of any two elements.

- Constraints: $1 \le n \le 2 \times 10^5$; values fit in 32-bit Int.

## Examples

```
Input:  nums = [3,10,5,25,2,8]   -> Output: 28   (5 XOR 25 = 11100)
Input:  nums = [14,70,53,83,49,91,36,80,92,51,66,70] -> Output: 127
```

## Intuition — greedily maximize the XOR bit by bit

XOR's bits are independent: a bit of `x XOR y` is 1 iff the numbers differ at that bit. To *maximize* the XOR, you want the **highest bits to differ** — a per-bit greedy decision, working from the most significant bit down. The structure that answers "does any number start with the *opposite* bit at this position?" is a **binary trie** — the [Chapter 13](../ch13-tries/index.md) trie with exactly two children per node (0 and 1).

**Build:** insert every number's 32-bit representation (MSB first) into the trie.

**Query for each number:** walk its bits from bit 31 down; at each bit, *prefer the opposite child* (`curBit xor 1`) — if it exists, take it and add `1 shl i` to the running XOR sum; otherwise take the same-bit child. The running sum accumulates the maximum XOR achievable *with some partner in the trie*.

**Why is the per-bit greedy correct?** A higher bit's contribution ($2^i$) exceeds the sum of all lower bits combined ($2^i - 1$). So maximizing bit 31 first, then bit 30, etc., is provably optimal — the classic "greedy with powers of two" argument. The trie's role is making each "does the opposite exist?" query $O(1)$ per bit instead of $O(n)$.

## Approach 1 — All pairs (O(n^2))

`max over i<j of nums[i] xor nums[j]`: correct, but $n = 2 \times 10^5$ makes it hopeless.

## Approach 2 — Binary trie (the repo's version, optimal)

```kotlin
class MaximumXorOfTwoNumsInArray {
    data class Trie(val children: Array<Trie?> = arrayOfNulls(2))   // bit 0, bit 1

    /**
     * @param nums input array
     * @return     maximum XOR of any two elements
     */
    fun findMaximumXOR(nums: IntArray): Int {
        val root = buildTrie(nums)

        var max = Int.MIN_VALUE
        for (num in nums) {
            var curNode = root
            var curSum = 0

            for (i in 31 downTo 0) {                 // MSB first
                val curBit = if ((1 shl i and num) != 0) 1 else 0

                // Prefer the opposite bit: it makes this XOR bit 1
                if (curNode.children[curBit xor 1] != null) {
                    curSum += (1 shl i)
                    curNode = curNode.children[curBit xor 1]!!
                } else {
                    curNode = curNode.children[curBit]!!
                }
            }
            max = maxOf(max, curSum)
        }
        return max
    }

    private fun buildTrie(nums: IntArray): Trie {
        val root = Trie()

        for (num in nums) {
            var ptr = root
            for (i in 31 downTo 0) {
                val currBit = if ((1 shl i and num) != 0) 1 else 0
                if (ptr.children[currBit] == null) {
                    ptr.children[currBit] = Trie()
                }
                ptr = ptr.children[currBit]!!
            }
        }
        return root
    }
}
```

```java
public class MaximumXorOfTwoNumbers {
    private static class Trie {
        Trie[] children = new Trie[2];               // bit 0, bit 1
    }

    /**
     * @param nums input array
     * @return     maximum XOR of any two elements
     */
    public int findMaximumXOR(int[] nums) {
        Trie root = new Trie();
        for (int num : nums) {                       // build: MSB first
            Trie node = root;
            for (int i = 31; i >= 0; i--) {
                int bit = (num >>> i) & 1;
                if (node.children[bit] == null) node.children[bit] = new Trie();
                node = node.children[bit];
            }
        }

        int max = 0;
        for (int num : nums) {                       // query: prefer the opposite bit
            Trie node = root;
            int cur = 0;
            for (int i = 31; i >= 0; i--) {
                int bit = (num >>> i) & 1;
                if (node.children[bit ^ 1] != null) {   // opposite exists: take it
                    cur |= (1 << i);
                    node = node.children[bit ^ 1];
                } else {
                    node = node.children[bit];
                }
            }
            max = Math.max(max, cur);
        }
        return max;
    }
}
```

```cpp
#include <vector>

class MaximumXorOfTwoNumbers {
    struct Trie {
        Trie* children[2] = {nullptr, nullptr};
    };

public:
    /**
     * @param nums input array
     * @return     maximum XOR of any two elements
     */
    int findMaximumXOR(std::vector<int>& nums) {
        Trie* root = new Trie();
        for (int num : nums) {                       // build: MSB first
            Trie* node = root;
            for (int i = 31; i >= 0; i--) {
                int bit = (num >> i) & 1;
                if (!node->children[bit]) node->children[bit] = new Trie();
                node = node->children[bit];
            }
        }

        int max = 0;
        for (int num : nums) {                       // query: prefer the opposite bit
            Trie* node = root;
            int cur = 0;
            for (int i = 31; i >= 0; i--) {
                int bit = (num >> i) & 1;
                if (node->children[bit ^ 1]) {       // opposite exists: take it
                    cur |= (1 << i);
                    node = node->children[bit ^ 1];
                } else {
                    node = node->children[bit];
                }
            }
            max = std::max(max, cur);
        }
        return max;
    }
};
```

```python
class Trie:
    def __init__(self):
        self.children = [None, None]         # bit 0, bit 1

def find_maximum_xor(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     maximum XOR of any two elements
    """
    root = Trie()
    for num in nums:                         # build: MSB first
        node = root
        for i in range(31, -1, -1):
            bit = (num >> i) & 1
            if not node.children[bit]:
                node.children[bit] = Trie()
            node = node.children[bit]

    best = 0
    for num in nums:                         # query: prefer the opposite bit
        node = root
        cur = 0
        for i in range(31, -1, -1):
            bit = (num >> i) & 1
            if node.children[bit ^ 1]:       # opposite exists: take it
                cur |= 1 << i
                node = node.children[bit ^ 1]
            else:
                node = node.children[bit]
        best = max(best, cur)
    return best
```

```rust
#[derive(Default)]
struct Trie {
    children: [Option<Box<Trie>>; 2],       // bit 0, bit 1
}

impl Solution {
    /// @param nums input array
    /// @return     maximum XOR of any two elements
    pub fn find_maximum_xor(nums: Vec<i32>) -> i32 {
        let mut root = Trie::default();
        for &num in &nums {                  // build: MSB first
            let mut node = &mut root;
            for i in (0..32).rev() {
                let bit = ((num >> i) & 1) as usize;
                node = node.children[bit].get_or_insert_with(Default::default);
            }
        }

        let mut best = 0;
        for &num in &nums {                  // query: prefer the opposite bit
            let mut node = &root;
            let mut cur = 0;
            for i in (0..32).rev() {
                let bit = ((num >> i) & 1) as usize;
                if let Some(opp) = &node.children[bit ^ 1] {   // opposite exists: take it
                    cur |= 1 << i;
                    node = opp;
                } else {
                    node = node.children[bit].as_ref().unwrap();
                }
            }
            best = best.max(cur);
        }
        best
    }
}
```


## Dry run

**Input:** `nums = [3,10,5,25,2,8]` (trace truncated to 5 low bits).

```
trie paths: 3=00011, 10=01010, 5=00101, 25=11001, 2=00010, 8=01000

query num=5 (00101) — the optimal partner is 25 (11001), XOR = 11100 = 28:
  bit4: 5 has 0 -> want 1: exists (25) -> cur += 16.  node on the {1,...} path
  bit3: 5 has 0 -> want 1: exists (25) -> cur += 8.   node on {11,...}
  bit2: 5 has 1 -> want 0: exists (25 has 0) -> cur += 4.  node on {110,...}
  bit1: 5 has 0 -> want 1: 25 has 0 -> absent -> take 0-child.  cur += 0.
  bit0: 5 has 1 -> want 0: 25 has 1 -> absent -> take 1-child.  cur += 0.
  cur = 28 ✓   (the max over all queries)
```

The greedy's correctness is the "powers of two dominate" argument: bit 4's 16 outweighs everything below (max 15), so locking in the opposite child at bit 4 — when it exists — is unconditionally right. The trie makes each "does the opposite exist?" a single child lookup.

## Complexity

**Time.** 32-bit walk per element, build + query:

$$
T(n) = O(32n) = O(n)
$$

**Space.** The trie (≤ 32·n nodes worst case):

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Maximum XOR With An Element From Array (queries)** — the same trie with a value-limit filter per query (a per-node min).
- **Trie family** — [13.1](../ch13-tries/implement-trie.md) is this structure with 26 children; here the alphabet is {0,1}.
- **Interview follow-up:** "Why does per-bit greediness give the global max?" Each bit's contribution ($2^i$) is greater than the sum of all lower bits combined ($2^i - 1$), so deciding the highest bit first is never a local mistake — the greedy choice at bit `i` dominates any choice made below it. This is the standard exchange argument for lexicographic maximization.
