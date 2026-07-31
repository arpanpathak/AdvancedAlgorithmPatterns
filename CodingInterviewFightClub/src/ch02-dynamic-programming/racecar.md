# 2.26 Racecar

> **Source:** [`src/main/kotlin/simulation/Racecar.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/simulation/Racecar.kt)
> **Pattern:** (position, speed) BFS/DP · **Core page**

## The Problem

A car at position 0, speed 1. `A` = accelerate (pos += speed; speed *= 2), `R` = reverse (speed = ±1). Min instructions to reach `target`.

- Constraints: $1 \le target \le 10^4$.

## Examples

```
Input:  target = 3   -> Output: 2   (A A: 0→1→3, speed 4)
Input:  target = 6   -> Output: 5   (A A R A A: 0→1→3→(R: speed -1)→2→4→... hmm)
```
The canonical answer for 6 is 5: `A A A R A` → 0→1→3→7 (speed 8), R (speed -1), A → 6 ✓.

## Intuition — the state is (position, speed); the actions are A and R

BFS over `(pos, speed)` — each `A` moves +speed and doubles; each `R` resets speed to ±1. The repo's memoized DFS bounds the search to a window around the target:

```kotlin
fun dfs(pos: Int, speed: Int): Int {
    if (pos == target) return 0
    if (pos < -10000 || pos > 10000) return Int.MAX_VALUE

    val reverseSpeed = if (speed > 0) -1 else 1
    return 1 + minOf(
        dfs(pos + speed, speed * 2),   // A
        dfs(pos, reverseSpeed)         // R
    )
}
```

**Why the ±10000 window?** Overshooting far past the target is provably useless — reversing near it is always better. The window is the practical pruning (a mathematically-tight bound: overshooting beyond `2×target` never helps).

**Why is `R` state `(pos, reverseSpeed)`?** `R` doesn't move — it only flips the speed to ±1 (sign of the incoming direction). The `A` action does the motion. The [6.1](../ch06-graphs/word-ladder.md) implicit-graph BFS: nodes are `(pos, speed)` pairs, edges are the two commands.

## Approach 1 — BFS over (pos, speed) with a visited set

The implicit-graph BFS: correct, the canonical answer for large targets.

## Approach 2 — Memoized DFS (the repo's version)

```kotlin
class Racecar {
    private val dp = mutableMapOf<Pair<Int, Int>, Int>()
    private var target = 0

    /**
     * @param target target position
     * @return       min instructions (A/R) to reach it
     */
    fun racecar(target: Int): Int {
        this.target = target
        dp.clear()
        return dfs(0, 1)
    }

    private fun dfs(pos: Int, speed: Int): Int {
        val key = Pair(pos, speed)
        return when {
            pos == target -> 0
            pos < -10000 || pos > 10000 -> Int.MAX_VALUE
            key in dp -> dp[key]!!
            else -> {
                val reverseSpeed = if (speed > 0) -1 else 1
                1 + minOf(dfs(pos + speed, speed * 2), dfs(pos, reverseSpeed))
            }
        }.also { dp[key] = it }
    }
}
```

```java
import java.util.*;

public class Racecar {
    private Map<String, Integer> memo = new HashMap<>();
    private int target;

    private int dfs(int pos, int speed) {
        if (pos == target) return 0;
        if (pos < -10000 || pos > 10000) return Integer.MAX_VALUE;

        String key = pos + " " + speed;
        if (memo.containsKey(key)) return memo.get(key);

        int reverseSpeed = speed > 0 ? -1 : 1;
        int result = 1 + Math.min(dfs(pos + speed, speed * 2), dfs(pos, reverseSpeed));
        memo.put(key, result);
        return result;
    }

    /**
     * @param target target position
     * @return       min instructions (A/R) to reach it
     */
    public int racecar(int target) {
        this.target = target;
        return dfs(0, 1);
    }
}
```

```cpp
#include <unordered_map>
#include <string>
#include <algorithm>
#include <climits>

class Racecar {
    std::unordered_map<std::string, int> memo;
    int target;

    int dfs(int pos, int speed) {
        if (pos == target) return 0;
        if (pos < -10000 || pos > 10000) return INT_MAX;

        std::string key = std::to_string(pos) + " " + std::to_string(speed);
        if (memo.count(key)) return memo[key];

        int rs = speed > 0 ? -1 : 1;
        return memo[key] = 1 + std::min(dfs(pos + speed, speed * 2), dfs(pos, rs));
    }

public:
    /**
     * @param target target position
     * @return       min instructions (A/R) to reach it
     */
    int racecar(int target) {
        this->target = target;
        return dfs(0, 1);
    }
};
```

```python
from functools import lru_cache

def racecar(target: int) -> int:
    """
    @param target: target position
    @return:       min instructions (A/R) to reach it
    """
    @lru_cache(None)
    def dfs(pos: int, speed: int) -> int:
        if pos == target:
            return 0
        if pos < -10000 or pos > 10000:
            return float("inf")

        reverse_speed = -1 if speed > 0 else 1
        return 1 + min(dfs(pos + speed, speed * 2), dfs(pos, reverse_speed))

    return dfs(0, 1)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param target target position
    /// @return       min instructions (A/R) to reach it
    pub fn racecar(target: i32) -> i32 {
        fn dfs(pos: i32, speed: i32, target: i32,
               memo: &mut HashMap<(i32, i32), i32>) -> i32 {
            if pos == target { return 0; }
            if pos < -10000 || pos > 10000 { return i32::MAX; }
            if let Some(&v) = memo.get(&(pos, speed)) { return v; }

            let rs = if speed > 0 { -1 } else { 1 };
            let result = 1 + dfs(pos + speed, speed * 2, target, memo)
                .min(dfs(pos, rs, target, memo));
            memo.insert((pos, speed), result);
            result
        }

        dfs(0, 1, target, &mut HashMap::new())
    }
}
```

## Dry run

**Input:** `target = 3`.

```
dfs(0, 1):
  A: dfs(1, 2).    R: dfs(0, -1)
dfs(1, 2): A: dfs(3, 4) = 0 (reached!).  -> 1 + 0 = 1
  So dfs(1,2) = 1.  dfs(0,1) = 1 + min(1, ...) = 2 ✓

Input: target = 6:
dfs(0,1): A: dfs(1,2): A: dfs(3,4): A: dfs(7,8): pos 7 > 6... A: dfs(15,16) (far), R: dfs(7,-1)
  dfs(7,-1): A: dfs(6,-2) = 0.  -> 1.  dfs(7,8): A: 15->... vs R: 7->6: min = 1 + 1 = 2
  dfs(3,4): 1 + min(dfs(7,8)=2, dfs(3,-1): ...) — the winning line: A A A R A = 5 ✓
```

The recursion explores the command tree with memoization collapsing repeated `(pos, speed)` states. The window bound keeps the tree finite — positions beyond ±10000 are pruned as unreachable-in-practice. The `R` action never moves, so the state's speed flips sign and the search continues.

## Complexity

**Time.** States within the window:

$$
T = O(\text{window} \cdot \text{speeds})
$$

**Space.** The memo:

$$
S = O(\text{window} \cdot \text{speeds})
$$

## Variants & follow-ups

- **Word Ladder** ([6.1](../ch06-graphs/word-ladder.md)) — the same implicit-graph BFS framing.
- **Minimum Number Of Refueling Stops** ([11.7](../ch11-greedy/minimum-number-of-refueling-stops.md)) — a stateful-vehicle sibling.
- **Interview follow-up:** "Why is the ±10000 window valid?" A car that overshoots beyond `2×target` must reverse — and any plan overshooting that far can be shortened by reversing earlier. The bound makes the memo finite; the canonical BFS version makes the same cut with a visited set.
