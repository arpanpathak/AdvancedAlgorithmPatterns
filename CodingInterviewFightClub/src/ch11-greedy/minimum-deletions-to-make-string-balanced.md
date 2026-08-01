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
