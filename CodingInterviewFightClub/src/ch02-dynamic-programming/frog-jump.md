# 2.8 Frog Jump

> **Source:** [`src/main/kotlin/dynamic_programming/FrogJump.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/dynamic_programming/FrogJump.kt) · [`FrogJumpTopDown.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/dynamic_programming/FrogJumpTopDown.kt)
> **Pattern:** set-valued DP state · **Gym page**

## The Problem

A frog is crossing a river on stones. `stones[i]` is the position of the i-th stone (strictly increasing, starting at 0). The frog starts on stone 0 and must land on the last stone.

Rule: if the frog's **last jump** was `k` units, its next jump must be exactly `k-1`, `k`, or `k+1` units (and `k > 0`). The first jump is always exactly 1 unit. Can the frog make it?

- Constraints: $2 \le n \le 2000$, positions up to $2^{31}-1$.

## Examples

```
Input:  stones = [0, 1, 3, 5, 6, 8, 12, 17]
Output: true
Explanation: 0→1 (k=1), 1→3 (k=2), 3→5 (k=2), 5→8 (k=3), 8→12 (k=4), 12→17 (k=5)

Input:  stones = [0, 1, 2, 3, 4, 8, 9, 11]
Output: false
Explanation: from 4 the reachable jumps are k-1,k,k+1 of the jump that arrived;
             the gap to 8 can't be made under the rule.
```

## Intuition — the state is *"what jumps can land me here?"*

The tricky part: a position alone doesn't determine the future — the **last jump size `k`** matters (it constrains the next jump). So the state must be a pair:

$$
\text{state} = (\text{stone position } p, \text{last jump } k)
$$

There are two classic encodings, and the repo ships **both**:

**Bottom-up (FrogJump.kt):** `stoneMap[p]` = the **set of jump sizes `k`** with which the frog can *arrive* at position `p`. Propagate:

```
for each stone p, for each k in stoneMap[p]:
    for step in {k-1, k, k+1} (step > 0):
        if p + step is a stone:  add step to stoneMap[p + step]
```

`stoneMap` values are sets → the "state space" is the set of reachable (position, jump) pairs. Each pair is processed once → $O(n^2)$ total (each stone holds at most $O(n)$ jumps).

**Top-down (FrogJumpTopDown.kt):** `solve(pos, k)` = "can I reach the last stone from `pos`, given last jump `k`?" — memoized over the `(pos, k)` pairs. Same state space, recursion-first style.

**Why sets, not a boolean?** Two frogs can reach the same stone with *different* last jumps, and those different `k`s lead to different futures. A single boolean "reachable" discards exactly the information the rule needs. The set is the honest state.

## Approach 1 — Bottom-up with reachable-jump sets (the repo's first version)

```kotlin
/**
 * @param stones the positions of the stones, strictly increasing, starting at 0
 * @return       true iff the frog can reach the last stone under the k-1/k/k+1 rule
 */
fun canCross(stones: IntArray): Boolean {
    // The very first jump is fixed: 0 -> 1.
    if (stones[1] != 1) return false

    // Map: stone position -> set of jump sizes 'k' that can land on this stone.
    val stoneMap = mutableMapOf<Int, MutableSet<Int>>()
    stones.forEach { stone -> stoneMap[stone] = mutableSetOf() }
    stoneMap[0]?.add(0)   // the "jump" that arrives at the start

    for (stone in stones) {
        for (k in stoneMap[stone]!!) {
            for (step in k - 1..k + 1) {
                if (step > 0) {
                    val nextStone = stone + step
                    if (stoneMap.containsKey(nextStone)) {   // O(1) stone lookup
                        stoneMap[nextStone]?.add(step)
                    }
                }
            }
        }
    }
    return stoneMap[stones.last()]?.isNotEmpty() ?: false
}
```

## Approach 2 — Top-down memoized recursion (the repo's second version)

```kotlin
/**
 * @param stones the positions of the stones, strictly increasing, starting at 0
 * @return       true iff the frog can reach the last stone under the k-1/k/k+1 rule
 */
fun canCross(stones: IntArray): Boolean {
    val stoneSet = stones.toSet()               // O(1) membership tests
    val cache = mutableMapOf<Pair<Int, Int>, Boolean>()

    /**
     * @param pos the current stone position
     * @param k   the size of the last jump used to reach pos
     * @return    true iff the last stone is reachable from (pos, k)
     */
    fun isValidJump(pos: Int, nextJump: Int) = nextJump > 0 && (pos + nextJump) in stoneSet

    fun solve(pos: Int, k: Int): Boolean =
        cache.getOrPut(Pair(pos, k)) {
            pos == stones.last() || (k - 1..k + 1).any { nextJump ->
                isValidJump(pos, nextJump) && solve(pos + nextJump, nextJump)
            }
        }

    return solve(0, 0)
}
```

```java
import java.util.*;

public class FrogJump {
    /**
     * @param stones the positions of the stones, strictly increasing, starting at 0
     * @return       true iff the frog can reach the last stone under the k-1/k/k+1 rule
     */
    public boolean canCross(int[] stones) {
        Map<Integer, Set<Integer>> stoneMap = new HashMap<>();
        for (int s : stones) stoneMap.put(s, new HashSet<>());
        stoneMap.get(0).add(0);                        // "arrival" jump at the start

        for (int stone : stones) {
            for (int k : stoneMap.get(stone)) {
                for (int step = k - 1; step <= k + 1; step++) {
                    if (step > 0 && stoneMap.containsKey(stone + step)) {
                        stoneMap.get(stone + step).add(step);
                    }
                }
            }
        }
        return !stoneMap.get(stones[stones.length - 1]).isEmpty();
    }
}
```

```cpp
#include <vector>
#include <unordered_map>
#include <unordered_set>

class FrogJump {
public:
    /**
     * @param stones the positions of the stones, strictly increasing, starting at 0
     * @return       true iff the frog can reach the last stone under the k-1/k/k+1 rule
     */
    bool canCross(const std::vector<int>& stones) {
        std::unordered_map<int, std::unordered_set<int>> stoneMap;
        for (int s : stones) stoneMap[s];
        stoneMap[0].insert(0);

        for (int stone : stones) {
            for (int k : stoneMap[stone]) {
                for (int step = k - 1; step <= k + 1; step++) {
                    if (step > 0 && stoneMap.count(stone + step)) {
                        stoneMap[stone + step].insert(step);
                    }
                }
            }
        }
        return !stoneMap[stones.back()].empty();
    }
};
```

```python
def can_cross(stones: list[int]) -> bool:
    """
    @param stones: the positions of the stones, strictly increasing, starting at 0
    @return:       True iff the frog can reach the last stone under the k-1/k/k+1 rule
    """
    stone_map: dict[int, set[int]] = {s: set() for s in stones}
    stone_map[0].add(0)                        # "arrival" jump at the start

    for stone in stones:
        for k in list(stone_map[stone]):
            for step in (k - 1, k, k + 1):
                if step > 0 and (stone + step) in stone_map:
                    stone_map[stone + step].add(step)
    return bool(stone_map[stones[-1]])
```

```rust
use std::collections::{HashMap, HashSet};

impl Solution {
    /// @param stones the positions of the stones, strictly increasing, starting at 0
    /// @return       true iff the frog can reach the last stone under the k-1/k/k+1 rule
    pub fn can_cross(stones: Vec<i32>) -> bool {
        let mut stone_map: HashMap<i32, HashSet<i32>> =
            stones.iter().map(|&s| (s, HashSet::new())).collect();
        stone_map.get_mut(&0).unwrap().insert(0);       // "arrival" jump at the start

        for &stone in &stones {
            let jumps: Vec<i32> = stone_map[&stone].iter().cloned().collect();
            for k in jumps {
                for step in (k - 1)..=(k + 1) {
                    if step > 0 && stone_map.contains_key(&(stone + step)) {
                        stone_map.get_mut(&(stone + step)).unwrap().insert(step);
                    }
                }
            }
        }
        !stone_map[stones.last().unwrap()].is_empty()
    }
}
```

## Dry run

**Input:** `stones = [0, 1, 3, 5, 6, 8, 12, 17]`. Bottom-up propagation:

```
init:       stoneMap[0] = {0}
stone 0:    k=0 -> steps 1 -> land on 1    => stoneMap[1] = {1}
stone 1:    k=1 -> steps 1,2 -> land on 2?(no), 3  => stoneMap[3] = {2}
stone 3:    k=2 -> steps 1,2,3 -> land on 4?(no),5,6 => stoneMap[5]={2}, stoneMap[6]={3}
stone 5:    k=2 -> steps 1,2,3 -> land on 6,7?(no),8 => stoneMap[6]={3,2}, stoneMap[8]={3}
stone 6:    k=3 -> steps 2,3,4 -> land on 8,9?(no),10? => stoneMap[8]={3,2}
            k=2 -> steps 1,2,3 -> land on 7,8,9 -> stoneMap[8]={3,2} (2 already there)
stone 8:    k=3 -> steps 2,3,4 -> land on 10?,11?,12 => stoneMap[12]={4}
            k=2 -> steps 1,2,3 -> land on 9,10,11 -> nothing new
stone 12:   k=4 -> steps 3,4,5 -> land on 15?,16?,17 => stoneMap[17]={5}  ✓
stone 17:   stoneMap[17] = {5} non-empty -> true ✓
```

The answer emerges from watching a single jump size thread through the stones: 0→1 (k=1), 1→3 (k=2), 3→5 (k=2), 5→8 (k=3), 8→12 (k=4), 12→17 (k=5). Note stone 6 gets *two* jumps ({2, 3}) — the set is essential, because the k=3 arrival is what later enables the 8→12 jump.

**Why the O(1) stone lookup matters:** positions are sparse (gaps of any size), so `stoneMap.containsKey` avoids scanning. Without it, "is there a stone at p+step" would be $O(n)$ per probe.

## Complexity

**Time.** Each stone holds up to $O(n)$ jumps; each jump spawns 3 probes:

$$
T(n) = O(n^2) \quad \text{(each (stone, jump) pair processed once)}
$$

**Space.** The map holds one set per stone: $O(n^2)$ worst case.

## Variants & follow-ups

- **House Robber / classic jump games** — different constraints, no jump-size memory; those are simpler 1D DPs. The *memory of the last jump* is what makes this state 2D.
- **Interview follow-up:** "Why can't we use a boolean reachable[] array?" Because `reachable[p]` doesn't record *how* you arrived; the next jump depends on the arrival jump. Two arrivals with different `k` have different futures — the set IS the state.
- **Interview follow-up:** "Can the top-down version skip the memo and still work?" Only for tiny inputs; without memoization `solve(pos, k)` is exponential (each call branches 3 ways and the same `(pos, k)` recurs across many paths). The memo is what makes it $O(n^2)$.
