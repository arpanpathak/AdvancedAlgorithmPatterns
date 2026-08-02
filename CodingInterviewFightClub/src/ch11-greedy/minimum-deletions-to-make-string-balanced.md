# 11.32 Minimum Deletions To Make String Balanced

> **Source**: [`src/main/kotlin/greedy/MinimumDeletionsToMakeStringBalanced.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/MinimumDeletionsToMakeStringBalanced.kt)
> **Pattern**: running b-count · **Core page**

## The Problem

Min deletions so no 'a' appears after a 'b'.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  s = "aababbab"   -> Output: 2
Input:  s = "bbaaaaabb"  -> Output: 2
```

## Intuition — every 'a' after some 'b' must go; count the violations

Track the running `b` count; an 'a' after b's is a deletion:

```kotlin
var deletions = 0
var bCount = 0

for (ch in s) {
    if (ch == 'b') {
        bCount++
    } else {
        deletions = minOf(deletions + 1, bCount)   // delete this a, or delete all b's so far
    }
}
return deletions
```

**Why the min?** At each 'a', either delete the 'a' (cost +1) or delete every earlier 'b' (cost bCount) — the running min is the optimal so far.

## Approach 1 — Running b-count (the repo's version, optimal)

```kotlin
class MinimumDeletionsToMakeStringBalanced {
    /**
     * @param s 'a'/'b' string
     * @return  min deletions
     */
    fun minimumDeletions(s: String): Int {
        val n = s.length
        var deletions = 0
        var bCount = 0

        for (ch in s) {
            if (ch == 'b') {
                bCount++
            } else {
                deletions = minOf(deletions + 1, bCount)
            }
        }
        return deletions
    }
}
```

```java
public class MinimumDeletionsToMakeStringBalanced {
    /**
     * @param s 'a'/'b' string
     * @return  min deletions
     */
    public int minimumDeletions(String s) {
        int deletions = 0, bCount = 0;

        for (char c : s.toCharArray()) {
            if (c == 'b') bCount++;
            else deletions = Math.min(deletions + 1, bCount);
        }
        return deletions;
    }
}
```

```cpp
#include <string>
#include <algorithm>

class MinimumDeletionsToMakeStringBalanced {
public:
    /**
     * @param s 'a'/'b' string
     * @return  min deletions
     */
    int minimumDeletions(std::string s) {
        int deletions = 0, bCount = 0;

        for (char c : s) {
            if (c == 'b') bCount++;
            else deletions = std::min(deletions + 1, bCount);
        }
        return deletions;
    }
};
```

```python
def minimum_deletions(s: str) -> int:
    """
    @param s: 'a'/'b' string
    @return:  min deletions
    """
    deletions = 0
    b_count = 0

    for ch in s:
        if ch == "b":
            b_count += 1
        else:
            deletions = min(deletions + 1, b_count)

    return deletions
```

```rust
impl Solution {
    /// @param s 'a'/'b' string
    /// @return  min deletions
    pub fn minimum_deletions(s: String) -> i32 {
        let (mut deletions, mut b_count) = (0, 0);

        for ch in s.chars() {
            if ch == 'b' { b_count += 1; }
            else { deletions = (deletions + 1).min(b_count); }
        }
        deletions
    }
}
```

## Reading the code — what's actually happening

```kotlin
var deletions = 0
var bCount = 0
for (ch in s) {
    if (ch == 'b') {
        bCount++
    } else {
        deletions = minOf(deletions + 1, bCount)
    }
}
return deletions
```

The goal: after deletions, no `'a'` may appear after any `'b'` — the string must look like `bbb...aaa...`. Reading left to right, the moment we see an `'a'` *after* some `'b'`, a violation exists and something must be deleted. Two counters capture everything:

- **`bCount` counts the `b`s seen so far.** Every `'b'` in the prefix is a potential "bad influence": if an `'a'` shows up later, each of those `b`s is a *reason* the string is unbalanced — the `'a'` after them violates the order.
- **On an `'a'`, we face a two-way choice.** Either delete *this* `'a'` (cost 1 more deletion, `deletions + 1`), or delete *every `b` seen so far* (cost `bCount`), making this `'a'` legal. `minOf(deletions + 1, bCount)` picks the cheaper option *for this prefix*.
- **Why does the min compose into a global optimum?** This is a classic DP-in-disguise: `deletions` always holds the minimum deletions to balance the prefix *ending at the current character*. When the next `'a'` arrives, the only new decision is whether to kill it or kill the `b`s before it — and since the prefix was already optimally fixed, taking the min extends the optimum. No backtracking needed.
- **Why not count `'a'`s after `b`s?** A simpler-looking "count violations" scan would need to know *which* side to delete from — deleting a `b` vs deleting an `'a'` have different costs depending on context. The min-of-two trick collapses that choice into one number.

Trace `"aababbab"`: `a,a` fine (no b's yet) → `b` (b=1) → `a`: min(1,1)=1 (delete this a or the first b) → `b,b` (b=3) → `a`: min(2,3)=2 → `b` (b=4). Answer 2 ✓.

## Dry run

**Input:** `s = "aababbab"`.

```
a: 0.  a: 0.  b: b=1.  a: min(1,1)=1.  b: b=2.  b: b=3.  a: min(2,3)=2.  b: b=4.
Output: 2 ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does the min decision work?" The choice at each 'a' is local (delete it vs delete all prior b's) — the running min composes to the global optimum.
