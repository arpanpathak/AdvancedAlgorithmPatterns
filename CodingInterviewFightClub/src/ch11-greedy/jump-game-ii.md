# 11.2 Jump Game II

> **Source:** [`src/main/kotlin/greedy/JumpGame_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/JumpGame_II.kt)
> **Pattern:** frontier + jump count · **Core page**

## The Problem

You start at index 0 of `nums` (`nums[i]` = max jump length). Return the **minimum number of jumps** to reach the last index. (It is guaranteed reachable.)

- Constraints: $1 \le n \le 10^4$; $0 \le nums[i] \le 1000$.

## Examples

```
Input:  nums = [2,3,1,1,4]    -> Output: 2   (0 -> 1 -> 4)
Input:  nums = [2,3,0,1,4]    -> Output: 2   (0 -> 1 -> 4)
```

## Intuition — jumps as *layers* of the reachable frontier

[11.1](jump-game.md) asked "can we reach the end?" — one frontier variable. This problem adds "in how few jumps?" — and the answer is **the frontier's layer structure**: 

- With **0 jumps** you can reach `[0, 0]`.
- The set reachable with **at most 1 jump** is `[0, max(nums[0..0] + i)]`... more precisely, from every position in the current layer, you extend the frontier.
- Each jump *expands* the reachable frontier; the minimum number of jumps is the number of expansions until the frontier covers `n - 1`.

This is BFS over a compressed graph (the [level-fencing](../ch05-trees/binary-tree-level-order-traversal.md) idea from Chapter 5 applied to indices!): `currentEnd` is the end of the current "level" (reachable with `jumps` jumps), `farthest` is the end of the next level (reachable with `jumps + 1`). Each time `i` passes `currentEnd`, a new level begins — `jumps++`, and the level boundary jumps to `farthest`.

**Why is "extend as far as possible" optimal?** Every position in the *next* layer is reachable from somewhere in the *current* layer; jumping from the position that extends farthest maximizes the next layer. A jump that lands "short" can only reach a subset of what the farthest jump reaches — so the greedy jump is never worse (the staying-ahead argument again). The `i >= currentEnd` trigger is exactly "we've exhausted the current layer, all of its positions have contributed their reach, so the next jump is forced."

**Why loop to `lastIndex` (exclusive)?** The final index doesn't need to *extend* anything — arriving at it is the goal. The repo loops `0 until nums.lastIndex`, so a jump that *reaches* the end still gets counted by the layer trigger, and no extra jump is counted for extending beyond it.

## Approach 1 — BFS over all edges (too slow)

Build an implicit graph (`i -> i+1..i+nums[i]`) and BFS for the shortest path: $O(n^2)$ edges. The layer trick below computes the same distances without materializing any edges.

## Approach 2 — Layer tracking (the repo's version, optimal)

```kotlin
class JumpGame_II {
    /**
     * @param nums nums[i] = max jump length from index i
     * @return     minimum number of jumps to reach the last index
     */
    fun jump(nums: IntArray): Int {
        var (jumps, currentEnd, farthest) = listOf(0, 0, 0)

        for (i in 0 until nums.lastIndex) {
            farthest = maxOf(farthest, i + nums[i])   // best reach within this layer

            if (i >= currentEnd) {                    // current layer exhausted: jump!
                jumps++
                currentEnd = farthest                 // the new layer's boundary
            }
        }
        return jumps
    }
}
```

```java
public class JumpGameII {
    /**
     * @param nums nums[i] = max jump length from index i
     * @return     minimum number of jumps to reach the last index
     */
    public int jump(int[] nums) {
        int jumps = 0, currentEnd = 0, farthest = 0;

        for (int i = 0; i < nums.length - 1; i++) {
            farthest = Math.max(farthest, i + nums[i]);   // best reach within this layer

            if (i == currentEnd) {                        // current layer exhausted: jump!
                jumps++;
                currentEnd = farthest;                     // the new layer's boundary
            }
        }
        return jumps;
    }
}
```

```cpp
#include <vector>

class JumpGameII {
public:
    /**
     * @param nums nums[i] = max jump length from index i
     * @return     minimum number of jumps to reach the last index
     */
    int jump(std::vector<int>& nums) {
        int jumps = 0, currentEnd = 0, farthest = 0;

        for (int i = 0; i < (int)nums.size() - 1; i++) {
            farthest = std::max(farthest, i + nums[i]);   // best reach within this layer

            if (i == currentEnd) {                        // current layer exhausted: jump!
                jumps++;
                currentEnd = farthest;                     // the new layer's boundary
            }
        }
        return jumps;
    }
};
```

```python
def jump(nums: list[int]) -> int:
    """
    @param nums: nums[i] = max jump length from index i
    @return:     minimum number of jumps to reach the last index
    """
    jumps = current_end = farthest = 0

    for i, jump in enumerate(nums[:-1]):       # last index need not extend
        farthest = max(farthest, i + jump)     # best reach within this layer

        if i == current_end:                   # current layer exhausted: jump!
            jumps += 1
            current_end = farthest             # the new layer's boundary
    return jumps
```

```rust
impl Solution {
    /// @param nums nums[i] = max jump length from index i
    /// @return     minimum number of jumps to reach the last index
    pub fn jump(nums: Vec<i32>) -> i32 {
        let (mut jumps, mut current_end, mut farthest) = (0, 0usize, 0usize);

        for i in 0..nums.len() - 1 {           // last index need not extend
            farthest = farthest.max(i + nums[i] as usize);   // best reach within this layer

            if i == current_end {              // current layer exhausted: jump!
                jumps += 1;
                current_end = farthest;        // the new layer's boundary
            }
        }
        jumps
    }
}
```

## Dry run

**Input:** `nums = [2,3,1,1,4]`.

```
jumps=0, currentEnd=0, farthest=0

i=0: farthest = max(0, 0+2) = 2.  i == currentEnd(0)? yes -> jumps=1, currentEnd=2
i=1: farthest = max(2, 1+3) = 4.  i == currentEnd(2)? no
i=2: farthest = max(4, 2+1) = 4.  i == currentEnd(2)? yes -> jumps=2, currentEnd=4
i=3: farthest = max(4, 3+1) = 4.  i == currentEnd(4)? no

Output: 2 ✓
```

Read the layers: jump 1 covers `[1,2]` (indices reachable in one hop), jump 2 covers `[3,4]`. The trigger `i == currentEnd` fires exactly when we walk off the edge of the current layer — which is BFS's "level fence" ([5.2](../ch05-trees/binary-tree-level-order-traversal.md)) in disguise: the indices are the queue, the levels are the jump counts.

## Complexity

**Time.** Single pass:

$$
T(n) = O(n)
$$

**Space.** Three variables:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Jump Game** ([11.1](jump-game.md)) — the reachability-only version; this page is that frontier plus a counter.
- **Minimum Number Of Refueling Stops** ([11.7](minimum-number-of-refueling-stops.md)) — the same "how far can I get with k resources" skeleton, but the choice of *where* to spend each resource is deferred to a max-heap instead of forced greedily.
- **Gas Station / circular routes** — "can a full circuit be completed?" is a frontier argument over cumulative gas; the greedy start-position choice is the classic follow-up.
- **Interview follow-up:** "Why does counting jumps as *layer expansions* give the minimum?" Each expansion is the *smallest* number of additional jumps that reaches strictly farther — you can't cover layer `k+1` without jumping at least once from layer `k`, so the number of layers to reach the end is a lower bound, and the greedy frontier achieves it.
