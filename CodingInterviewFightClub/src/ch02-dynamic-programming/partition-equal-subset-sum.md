# 2.6 Partition Equal Subset Sum

> **Source:** [`src/main/kotlin/dynamic_programming/PartitionEqualSubsetSum.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/dynamic_programming/PartitionEqualSubsetSum.kt)
> **Pattern:** subset-sum reachability (boolean knapsack) · **Core page**

## The Problem

Given a non-empty array `nums` of positive integers, can you partition it into two subsets with **equal sums**?

- Constraints: $1 \le n \le 200$, $1 \le nums[i] \le 100$ → total sum ≤ 20,000.

## Examples

```
Input:  nums = [1, 5, 11, 5]   -> true   (partition {1,5,5} and {11}; both sum to 11)
Input:  nums = [1, 2, 3, 5]    -> false  (total 11 is odd, impossible)
Input:  nums = [1, 2, 5]       -> false  (even total 8, but no subset sums to 4)
```

## Intuition — reduce to subset-sum

Two observations collapse the problem:

1. **The target is forced.** If the total sum $S$ is odd, an equal split is impossible → `false` immediately. Otherwise both halves must sum to $S/2$.
2. **The question becomes:** does *any* subset of `nums` sum to exactly $S/2$? (The other half is whatever's left — automatically $S/2$.)

That's **subset-sum**, which is 0/1 knapsack with `value == weight` and a boolean question. State:

$$
dp[s] = \text{can some subset of the items seen so far sum to exactly } s
$$

Recurrence (per item `x`, descending over sums — the 0/1 discipline from [2.4](zero-one-knapsack.md)):

$$
dp[s] = dp[s] \;\lor\; dp[s - x]
$$

meaning "reachable before (skip x)" or "reachable by taking x (from state s−x)". Base: `dp[0] = true` (empty subset sums to 0). Answer: `dp[target]`.

**Why descending?** Exactly the 0/1 argument: reading `dp[s - x]` in descending order guarantees it reflects *previous* items only, so each number is used at most once. (Try ascending and `nums = [1, 1]`, target 1 → you'd get `true` — the bug of double-using.)

## Approach 1 — Brute force

Enumerate all $2^n$ subsets, check sums. $2^{200}$ — the largest number most people will ever see in an interview setting. DP is the *only* reasonable answer.

## Approach 2 — Top-down memoized DFS (the repo's first version)

```kotlin
/**
 * @param nums the array of positive integers
 * @return     true iff nums can be split into two subsets of equal sum
 */
fun canPartition(nums: IntArray): Boolean {
    val sum = nums.sum()
    if (sum % 2 != 0) return false
    val target = sum / 2

    // memo[i][s] = -1 unknown, 0 false, 1 true
    val memo = Array(nums.size) { IntArray(target + 1) { -1 } }

    /**
     * @param i          the current item index
     * @param currentSum the running sum of the chosen subset
     * @return           true iff a subset of nums[i..] can reach `target` from currentSum
     */
    fun dfs(i: Int, currentSum: Int): Boolean = when {
        currentSum == target -> true
        i == nums.size      -> false
        memo[i][currentSum] != -1 -> memo[i][currentSum] == 1
        else -> (
            dfs(i + 1, currentSum + nums[i]) ||   // take nums[i]
            dfs(i + 1, currentSum)                // skip nums[i]
        ).also { memo[i][currentSum] = if (it) 1 else 0 }
    }
    return dfs(0, 0)
}
```

## Approach 3 — Bottom-up boolean knapsack (optimal)

```kotlin
/**
 * @param nums the array of positive integers
 * @return     true iff nums can be split into two subsets of equal sum
 */
fun canPartitionBottomUp(nums: IntArray): Boolean {
    val sum = nums.sum()
    if (sum % 2 != 0) return false

    val target = sum / 2
    val dp = BooleanArray(target + 1).apply { this[0] = true }   // empty subset sums to 0

    for (num in nums) {
        for (s in target downTo num) {          // DESCENDING: 0/1 usage of each number
            dp[s] = dp[s] || dp[s - num]
        }
    }
    return dp[target]
}
```

```java
public class PartitionEqualSubsetSum {
    /**
     * @param nums the array of positive integers
     * @return     true iff nums can be split into two subsets of equal sum
     */
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int x : nums) sum += x;
        if (sum % 2 != 0) return false;

        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;                                    // empty subset sums to 0

        for (int num : nums) {
            for (int s = target; s >= num; s--) {        // descending: 0/1 semantics
                dp[s] = dp[s] || dp[s - num];
            }
        }
        return dp[target];
    }
}
```

```cpp
#include <vector>

class PartitionEqualSubsetSum {
public:
    /**
     * @param nums the array of positive integers
     * @return     true iff nums can be split into two subsets of equal sum
     */
    bool canPartition(const std::vector<int>& nums) {
        int sum = 0;
        for (int x : nums) sum += x;
        if (sum % 2 != 0) return false;

        int target = sum / 2;
        std::vector<bool> dp(target + 1, false);
        dp[0] = true;

        for (int num : nums) {
            for (int s = target; s >= num; s--) {        // descending: 0/1 semantics
                dp[s] = dp[s] || dp[s - num];
            }
        }
        return dp[target];
    }
};
```

```python
def can_partition(nums: list[int]) -> bool:
    """
    @param nums: the array of positive integers
    @return:     True iff nums can be split into two subsets of equal sum
    """
    total = sum(nums)
    if total % 2 != 0:
        return False

    target = total // 2
    dp = [False] * (target + 1)
    dp[0] = True                                 # empty subset sums to 0

    for num in nums:
        for s in range(target, num - 1, -1):     # descending: 0/1 semantics
            dp[s] = dp[s] or dp[s - num]
    return dp[target]
```

```rust
impl Solution {
    /// @param nums the array of positive integers
    /// @return     true iff nums can be split into two subsets of equal sum
    pub fn can_partition(nums: Vec<i32>) -> bool {
        let sum: i32 = nums.iter().sum();
        if sum % 2 != 0 {
            return false;
        }
        let target = (sum / 2) as usize;
        let mut dp = vec![false; target + 1];
        dp[0] = true;                                    // empty subset sums to 0

        for num in nums {
            let mut s = target;
            while s >= num as usize {                    // descending: 0/1 semantics
                dp[s] = dp[s] || dp[s - num as usize];
                s -= 1;
            }
        }
        dp[target]
    }
}
```

## Dry run

**Input:** `nums = [1, 5, 11, 5]`. `sum = 22`, `target = 11`.

```
dp (booleans over sums 0..11):
initial:        T F F F F F F F F F F F
after 1:        T T F F F F F F F F F F      (1 reachable)
after 5:        T T F F F F T T F F F F      (5 and 6 reachable)
after 11:       T T F F F F T T F F F T      (11 reachable -> dp[11]=true already!)
after 5 (2nd):  T T F F F T T T T T T T      (5,6,7,8,10,11 reachable)
Answer: dp[11] = true ✓  (subset {11} or {1,5,5})
```

Trace the interesting update (second `5`, `s = 11`):

```
s=11: dp[11] = dp[11] || dp[6] = true || true  -> stays true (already reachable via {11})
s=10: dp[10] = dp[10] || dp[5] = false || true -> true   ({5,5})
```

**Why descending matters (the bug to dodge):** with `nums = [1, 1]` and `target = 1`, an ascending loop would do: `s=1: dp[1] = dp[1] || dp[0] = true` — fine so far; but for `target = 2` (nums=[1,1], sum=2, target=1 — no, target is 1)... take `nums=[2,2]`, target=2: descending: s=2: dp[2]=dp[0]=true (one 2). ✓. Ascending: s=2: dp[2] = dp[0] = true — but then... still only one pass through nums, so it's fine for one item per outer iteration. The real contamination: `nums=[2]`, target=4? Not applicable (sum 2). The classic failure: `nums=[1,1]`, sum=2, target=1: both loops give true correctly. Better example: `nums = [2, 4]`, target=3: nothing reaches 3 → false either way. Hmm — the descending requirement is really about *within one outer iteration*: `nums=[2, 2, 4]`, target = 4: descending: after first 2: dp[2]=T. After second 2: s=4: dp[4]=dp[2](from first 2 only)=T... wait descending from target: s=4: dp[4]||dp[2]=T → dp[4]=true. But that used BOTH 2s (2+2=4)! And that's *correct* — each number used once, and there are two 2s. OK: the classic contamination example is `nums = [1, 1]` with target = 1 via *ascending within the same item* — but each outer iteration is one item, and ascending within one item: for `num=1, target=1`: s=1: dp[1]=dp[1]||dp[0]=true. Only one item processed, correct. The actual failure needs TWO copies of the same value: `nums=[1,1,1]`, target=3: ascending never reuses the *same* item because each item is one outer pass... 

Hold on — the real issue: ascending reuses the same item within one outer pass. E.g. `nums = [2]`, `target = 4`: descending: s=4: dp[4]=dp[2]=false; s=3..2: dp[2]=dp[0]=true. Result dp[4]=false ✓ (one 2 can't make 4). Ascending: s=2: dp[2]=true; s=3: dp[3]=dp[1]=false; s=4: dp[4]=dp[2]=true ← WRONG! The same single item 2 was used twice. That's the correct bug example: `canPartition([2])` with sum=2, target=1 — not applicable. Since target = sum/2 and the array must be non-empty with sum even, `[2]` gives target 1, and descending/ascending both give false. Hmm, target = sum/2 ≤ sum - min... for `[2, 2]`, sum=4, target=2: descending: after first 2: dp[2]=true. after second 2: s=2: dp[2]=dp[2]||dp[0]=true. ✓. ascending: same result. The contamination needs target > sum of all items... but target = half the sum, so if all items are < target, multiple items are needed, and within ONE item ascending can self-stack: `[2, 2, 2, 2]` sum=8 target=4. Descending: after 1st 2: dp[2]=T. 2nd: s=4: dp[4]=dp[2]=T (uses both 2s — correct). s=3,2: dp[2] stays. 3rd: s=4: dp[4] already T. ✓ true (2+2). Ascending with 1st 2: s=2: T; s=3: dp[3]=dp[1]=F; s=4: dp[4]=dp[2]=T ← TRUE but with only ONE item?! [2] can't make 4 with one copy. But wait the array has four 2s, so dp[4]=true is correct anyway. Ugh — the clean demonstration needs an array where ascending gives true but the correct answer is false. `nums=[2]` target=1 → no. `nums=[2,2]` target=2 → both true. `nums=[2,4]` sum=6 target=3 → no. `nums=[2,2,2]` sum=6 target=3 → no reachable sum 3. `nums=[2,2,2,2]`? true. `nums=[3,3]` target=3: ascending: s=3: dp[3]=dp[0]=T ✓ true. `nums=[4,4]` target=4: true. It seems for even-total arrays the ascending bug rarely flips the answer on the full array... The real flips: `nums=[2,4,6]` sum=12 target=6: descending: after 2: dp[2]; after 4: dp[4],dp[6]=dp[2] → T (2+4) ✓. ascending: after 2: dp[2],dp[3]?no, dp[4]=dp[2]→T! Wait ascending with item 2 alone reaches dp[4] via dp[2] (self-stack): dp[2]=T (s=2), s=3: dp[3]=dp[1]=F, s=4: dp[4]=dp[2]=T → "4 reachable" with only the item 2 (twice)! Then after 4: s=6: dp[6]=dp[2]=T → still true (2+4 correct). Hmm answer stays true.

The canonical counterexample where ascending breaks the FINAL answer: need an array where target is reachable only by double-using a single item and no valid combination exists. sum even, target=sum/2. E.g. `nums=[2, 2]` — no. `nums=[1, 5]` sum=6 target=3: ascending item 1: s=1: dp[1]=T, s=2: dp[2]=dp[1]=T, s=3: dp[3]=dp[2]=T → "3 reachable" — but [1,5] can't make 3! dp[3]=true → wrong TRUE! Descending item 1: s=3: dp[3]=dp[2]=F, s=2: dp[2]=dp[1]=F, s=1: dp[1]=dp[0]=T → dp[3]=false ✓, then item 5: s=3: dp[3]=dp[3]||dp[-2] → false. Final false ✓ correct. 

So `nums=[1,5]` is the clean example. I'll use that in the dry run as the "descending vs ascending" demonstration.

Let me fix the dry-run section in the page I'm about to write — I haven't written it yet. Good. I'll include nums=[1,5] example.<｜end▁of▁thinking｜>

<｜｜DSML｜｜tool_calls>
<｜｜DSML｜｜invoke name="write">
<｜｜DSML｜｜parameter name="content" string="true"># 2.6 Partition Equal Subset Sum

> **Source:** [`src/main/kotlin/dynamic_programming/PartitionEqualSubsetSum.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/dynamic_programming/PartitionEqualSubsetSum.kt)
> **Pattern:** subset-sum reachability (boolean knapsack) · **Core page**

## The Problem

Given a non-empty array `nums` of positive integers, can you partition it into two subsets with **equal sums**?

- Constraints: $1 \le n \le 200$, $1 \le nums[i] \le 100$ → total sum ≤ 20,000.

## Examples

```
Input:  nums = [1, 5, 11, 5]   -> true   (partition {1,5,5} and {11}; both sum to 11)
Input:  nums = [1, 2, 3, 5]    -> false  (total 11 is odd, impossible)
Input:  nums = [1, 2, 5]       -> false  (even total 8, but no subset sums to 4)
```

## Intuition — reduce to subset-sum

Two observations collapse the problem:

1. **The target is forced.** If the total sum $S$ is odd, an equal split is impossible → `false` immediately. Otherwise both halves must sum to $S/2$.
2. **The question becomes:** does *any* subset of `nums` sum to exactly $S/2$? (The other half is whatever's left — automatically $S/2$.)

That's **subset-sum**, which is 0/1 knapsack with `value == weight` and a boolean question. State:

$$
dp[s] = \text{can some subset of the items seen so far sum to exactly } s
$$

Recurrence (per item `x`, descending over sums — the 0/1 discipline from [2.4](zero-one-knapsack.md)):

$$
dp[s] = dp[s] \;\lor\; dp[s - x]
$$

meaning "reachable before (skip x)" or "reachable by taking x (from state s−x)". Base: `dp[0] = true` (empty subset sums to 0). Answer: `dp[target]`.

**Why descending?** Exactly the 0/1 argument: reading `dp[s - x]` in descending order guarantees it reflects *previous* items only, so each number is used at most once. The dry run below shows the catastrophic result of ascending order.

## Approach 1 — Brute force

Enumerate all $2^n$ subsets, check sums. $2^{200}$ — astronomically impossible. DP is the *only* reasonable answer.

## Approach 2 — Top-down memoized DFS (the repo's first version)

```kotlin
/**
 * @param nums the array of positive integers
 * @return     true iff nums can be split into two subsets of equal sum
 */
fun canPartition(nums: IntArray): Boolean {
    val sum = nums.sum()
    if (sum % 2 != 0) return false
    val target = sum / 2

    // memo[i][s] = -1 unknown, 0 false, 1 true
    val memo = Array(nums.size) { IntArray(target + 1) { -1 } }

    /**
     * @param i          the current item index
     * @param currentSum the running sum of the chosen subset
     * @return           true iff a subset of nums[i..] can reach `target` from currentSum
     */
    fun dfs(i: Int, currentSum: Int): Boolean = when {
        currentSum == target -> true
        i == nums.size      -> false
        memo[i][currentSum] != -1 -> memo[i][currentSum] == 1
        else -> (
            dfs(i + 1, currentSum + nums[i]) ||   // take nums[i]
            dfs(i + 1, currentSum)                // skip nums[i]
        ).also { memo[i][currentSum] = if (it) 1 else 0 }
    }
    return dfs(0, 0)
}
```

## Approach 3 — Bottom-up boolean knapsack (optimal)

```kotlin
/**
 * @param nums the array of positive integers
 * @return     true iff nums can be split into two subsets of equal sum
 */
fun canPartitionBottomUp(nums: IntArray): Boolean {
    val sum = nums.sum()
    if (sum % 2 != 0) return false

    val target = sum / 2
    val dp = BooleanArray(target + 1).apply { this[0] = true }   // empty subset sums to 0

    for (num in nums) {
        for (s in target downTo num) {          // DESCENDING: 0/1 usage of each number
            dp[s] = dp[s] || dp[s - num]
        }
    }
    return dp[target]
}
```

```java
public class PartitionEqualSubsetSum {
    /**
     * @param nums the array of positive integers
     * @return     true iff nums can be split into two subsets of equal sum
     */
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int x : nums) sum += x;
        if (sum % 2 != 0) return false;

        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;                                    // empty subset sums to 0

        for (int num : nums) {
            for (int s = target; s >= num; s--) {        // descending: 0/1 semantics
                dp[s] = dp[s] || dp[s - num];
            }
        }
        return dp[target];
    }
}
```

```cpp
#include <vector>

class PartitionEqualSubsetSum {
public:
    /**
     * @param nums the array of positive integers
     * @return     true iff nums can be split into two subsets of equal sum
     */
    bool canPartition(const std::vector<int>& nums) {
        int sum = 0;
        for (int x : nums) sum += x;
        if (sum % 2 != 0) return false;

        int target = sum / 2;
        std::vector<bool> dp(target + 1, false);
        dp[0] = true;

        for (int num : nums) {
            for (int s = target; s >= num; s--) {        // descending: 0/1 semantics
                dp[s] = dp[s] || dp[s - num];
            }
        }
        return dp[target];
    }
};
```

```python
def can_partition(nums: list[int]) -> bool:
    """
    @param nums: the array of positive integers
    @return:     True iff nums can be split into two subsets of equal sum
    """
    total = sum(nums)
    if total % 2 != 0:
        return False

    target = total // 2
    dp = [False] * (target + 1)
    dp[0] = True                                 # empty subset sums to 0

    for num in nums:
        for s in range(target, num - 1, -1):     # descending: 0/1 semantics
            dp[s] = dp[s] or dp[s - num]
    return dp[target]
```

```rust
impl Solution {
    /// @param nums the array of positive integers
    /// @return     true iff nums can be split into two subsets of equal sum
    pub fn can_partition(nums: Vec<i32>) -> bool {
        let sum: i32 = nums.iter().sum();
        if sum % 2 != 0 {
            return false;
        }
        let target = (sum / 2) as usize;
        let mut dp = vec![false; target + 1];
        dp[0] = true;                                    // empty subset sums to 0

        for num in nums {
            let mut s = target;
            while s >= num as usize {                    // descending: 0/1 semantics
                dp[s] = dp[s] || dp[s - num as usize];
                s -= 1;
            }
        }
        dp[target]
    }
}
```

## Dry run

**Input:** `nums = [1, 5, 11, 5]`. `sum = 22`, `target = 11`.

```
dp (booleans over sums 0..11):
initial:        T F F F F F F F F F F F
after 1:        T T F F F F F F F F F F      (1 reachable)
after 5:        T T F F F F T T F F F F      (5 and 6 reachable)
after 11:       T T F F F F T T F F F T      (11 reachable!)
after 5 (2nd):  T T F F F T T T T T T T      (5,6,7,8,10,11 all reachable)
Answer: dp[11] = true ✓  (subset {11}, or {1,5,5})
```

Trace the interesting update (second `5`, `s = 10`):

```
s=10: dp[10] = dp[10] || dp[5] = false || true -> true   ({5,5} — the two 5s, each used once)
```

**Now the "why descending" demonstration** — `nums = [1, 5]`, `sum = 6`, `target = 3`:

| loop order | after item 1 (x=1) | after item 5 | dp[3] |
|---|---|---|---|
| descending | s=3: dp[3]\|\|dp[2]=F; s=2: dp[2]\|\|dp[1]=F; s=1: dp[1]\|\|dp[0]=**T** | s=3: dp[3]\|\|dp[-2] → stays **F** | **false ✓** |
| ascending  | s=1: dp[1]=T; s=2: dp[2]=dp[1]=**T**; s=3: dp[3]=dp[2]=**T** ← the single `1` used 3 times! | … | **true ✗** |

In ascending order, item `1` "reaches" sum 3 by stacking itself three times — a subset sum that doesn't exist with 0/1 semantics. Descending order prevents the self-stack, exactly as in [2.4](zero-one-knapsack.md).

## Complexity

**Time.** One pass per item over the sum axis:

$$
T(n, S) = O\!\left(n \cdot \frac{S}{2}\right) = O(nS)
$$

With $n = 200$, $S = 20{,}000$: $\approx 2 \times 10^6$ operations.

**Space.** $O(S/2)$ for the boolean array (or $O(nS/2)$ for the memoized version).

## Variants & follow-ups

- **[2.4](zero-one-knapsack.md)** — the general value-maximizing knapsack this reduces from.
- **Target Sum** (`src/main/kotlin/array/dp/TargetSum.kt`) — "count subsets reaching a signed target": a one-line transform to subset-sum counting.
- **Partition Array Into Two Arrays To Minimize Sum Difference** (`src/main/kotlin/array/dp/`) — the *minimization* twin: find the reachable sum closest to $S/2$.
- **Interview follow-up:** "Why can we ignore values > target?" They can never be part of a subset summing to ≤ target; skipping them is free (the `s >= num` loop bound already handles it).
- **Interview follow-up:** "This is a *decision* problem; is there a bitset trick?" Yes — `dp |= dp << num` on a bitset of length $S/2$ gives $O(nS/64)$ word-level parallelism. Naming it shows depth, but only after the plain DP is solid.
