# 11.1 Jump Game

> **Source:** [`src/main/kotlin/greedy/JumpGame.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/JumpGame.kt)
> **Pattern:** reachable-frontier tracking · **Core page**

## The Problem

You start at index 0 of an array `nums` where `nums[i]` is your maximum jump length from `i`. Return `true` if you can reach the **last index**.

- Constraints: $1 \le n \le 10^4$; $0 \le nums[i] \le 10^5$.

## Examples

```
Input:  nums = [2,3,1,1,4]    -> Output: true   (0 -> 1 -> 4, or 0 -> 2 -> 3 -> 4)
Input:  nums = [3,2,1,0,4]    -> Output: false  (every path dies at the 0 at index 3)
```

## Intuition — "how far can I still reach?" is the only question

The naive DP "can I reach index `i`?" is $O(n^2)$. The greedy realization: **you never need to know *how* you reach a position — only how far the positions you've reached can extend.** All reachable positions form a *contiguous prefix* `[0, maxReach]` (if you can reach `i`, you can reach everything before it), so the whole state is one number:

- start with `maxReach = 0`;
- for each index `i <= maxReach` (only positions you can actually reach), extend `maxReach = max(maxReach, i + nums[i])`;
- the moment `maxReach >= n - 1`, return `true`.

If the loop ever runs out of indices `i <= maxReach` without reaching the end, the frontier is stuck — return `false`.

**Why is the frontier contiguous?** To reach index `i`, you hop through indices `< i`; every hop is to a position between your current position and your jump limit — so reaching `i` implies every index before it was reachable too. No gaps, hence one variable.

**The staying-ahead argument** (the [primer's](pattern-primer.md) proof shape): after processing index `i`, our `maxReach` is ≥ the reachable frontier of *any* strategy that has processed `i` — because we always take the maximum extension. If the greedy frontier stalls, every other strategy's frontier stalls too; if greedy reaches the end, so would... well, greedy *is* the max, so it's optimal. One variable, provably complete.

## Approach 1 — DP (O(n^2))

`reachable[i] = any reachable j < i with j + nums[j] >= i`: correct, but quadratic and it computes far more than the question needs.

## Approach 2 — Frontier tracking (the repo's version, optimal)

```kotlin
class JumpGame {
    /**
     * @param nums nums[i] = max jump length from index i
     * @return     true iff index n-1 is reachable from index 0
     */
    fun canJump(nums: IntArray): Boolean {
        var maxIndex = 0
        var i = 0

        while (i <= maxIndex) {                      // only visit reachable positions
            maxIndex = maxOf(maxIndex, i + nums[i])  // extend the frontier

            if (maxIndex >= nums.size - 1) {
                return true
            }
            i++
        }
        return false                                 // frontier stuck: unreachable
    }
}
```

```java
public class JumpGame {
    /**
     * @param nums nums[i] = max jump length from index i
     * @return     true iff index n-1 is reachable from index 0
     */
    public boolean canJump(int[] nums) {
        int maxReach = 0;
        for (int i = 0; i <= maxReach && i < nums.length; i++) {
            maxReach = Math.max(maxReach, i + nums[i]);
            if (maxReach >= nums.length - 1) return true;
        }
        return false;
    }
}
```

```cpp
#include <vector>

class JumpGame {
public:
    /**
     * @param nums nums[i] = max jump length from index i
     * @return     true iff index n-1 is reachable from index 0
     */
    bool canJump(std::vector<int>& nums) {
        int maxReach = 0;
        for (int i = 0; i <= maxReach && i < (int)nums.size(); i++) {
            maxReach = std::max(maxReach, i + nums[i]);
            if (maxReach >= (int)nums.size() - 1) return true;
        }
        return false;
    }
};
```

```python
def can_jump(nums: list[int]) -> bool:
    """
    @param nums: nums[i] = max jump length from index i
    @return:     true iff index n-1 is reachable from index 0
    """
    max_reach = 0
    for i, jump in enumerate(nums):
        if i > max_reach:            # index i is unreachable
            return False
        max_reach = max(max_reach, i + jump)
        if max_reach >= len(nums) - 1:
            return True
    return True
```

```rust
impl Solution {
    /// @param nums nums[i] = max jump length from index i
    /// @return     true iff index n-1 is reachable from index 0
    pub fn can_jump(nums: Vec<i32>) -> bool {
        let mut max_reach = 0usize;
        for (i, &jump) in nums.iter().enumerate() {
            if i > max_reach { return false; }       // index i is unreachable
            max_reach = max_reach.max(i + jump as usize);
            if max_reach >= nums.len() - 1 { return true; }
        }
        true
    }
}
```

## Dry run

**Input:** `nums = [2,3,1,1,4]`.

```
maxReach = 0
i=0: i <= 0.  maxReach = max(0, 0+2) = 2.   2 >= 4? no.
i=1: i <= 2.  maxReach = max(2, 1+3) = 4.   4 >= 4? YES -> true ✓
```

Now the failing case `nums = [3,2,1,0,4]`:

```
maxReach = 0
i=0: maxReach = max(0, 0+3) = 3.   3 >= 4? no.
i=1: maxReach = max(3, 1+2) = 3.
i=2: maxReach = max(3, 2+1) = 3.
i=3: maxReach = max(3, 3+0) = 3.   3 >= 4? no.
i=4: i > maxReach (4 > 3) -> the loop condition i <= maxIndex fails -> exit -> false ✓
```

The second trace is the interesting one: `maxReach` *stalls* at 3 — every reachable index can only extend to 3 — so index 4 is forever outside the frontier. One number, and the whole failure mode is visible.

## Complexity

**Time.** Single pass:

$$
T(n) = O(n)
$$

**Space.** One variable:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Jump Game II** ([11.2](jump-game-ii.md)) — the same frontier with a *counter*: minimum jumps = number of frontier expansions.
- **Jump Game III** — reachability with *both* directions (`i ± nums[i]`): the frontier argument breaks (positions are no longer contiguous), so BFS on the implicit graph is the answer — a good "when greedy stops being enough" example.
- **Frog Jump** (`src/main/kotlin/dynamic_programming/FrogJumpTopDown.kt`) — jump lengths are a *set*, not a max; that turns the greedy max into a full DP over states.
- **Interview follow-up:** "Why is the frontier always `[0, maxReach]` and not scattered?" Every hop lands *between* the current index and its jump limit, so reaching `i` forces reaching all of `[0, i]`. No gaps means one variable suffices — which is the entire reason this problem is $O(n)$ and not $O(n^2)$.
