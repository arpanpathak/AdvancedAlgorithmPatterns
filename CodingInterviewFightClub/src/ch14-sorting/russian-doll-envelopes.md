# 14.6 Russian Doll Envelopes

> **Source:** [`src/main/kotlin/sorting/RussianDollEnvelope.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sorting/RussianDollEnvelope.kt)
> **Pattern:** sort + LIS · **Core page**

## The Problem

An envelope `[w, h]` fits inside another if **both** width and height are strictly smaller. Given envelopes, return the maximum number that can be nested.

- Constraints: $1 \le n \le 10^5$; values fit in `Int`.

## Examples

```
Input:  envelopes = [[5,4],[6,4],[6,7],[2,3]]
Output: 3    ([2,3] -> [5,4] -> [6,7])

Input:  envelopes = [[1,1],[1,1],[1,1]]
Output: 1    (equal dimensions can't nest)
```

## Intuition — the comparator turns 2-D nesting into 1-D LIS

Nesting is a *partial order* on pairs: `(w1,h1) < (w2,h2)` iff both components are strictly smaller. The trick that makes it solvable:

**Sort by width ascending; when widths tie, sort by height *descending*.** After this sort, nesting can only proceed left-to-right (widths never decrease), and the "which height sequence can nest?" question becomes: find the **longest increasing subsequence (LIS) of heights** — because the width order is already handled by the sort, and the descending-height tie-break guarantees equal-width envelopes can't both be chosen (equal width + equal height would violate strict nesting; descending heights make equal-width pairs non-increasing).

The remaining task is LIS length — done with the **patience-sorting** trick (a `TreeSet` of "tails"): for each height, replace the smallest tail ≥ height (the repo's `ceiling` + remove + add); the set's size at the end is the LIS length. Each height processed once with $O(\log n)$ set operations.

**Why descending height on ties?** Consider `[5,4],[6,4],[6,7]`: with widths sorted and ties ascending, `[6,4],[6,7]` would both be eligible in the height LIS — but two envelopes with the same width can't nest. Sorting ties by height *descending* (`[6,7]` before `[6,4]`) makes the LIS see heights `7,4` — non-increasing, so the tie pair can never be taken together. The comparator *is* the constraint encoding.

## Approach 1 — Sort + naive LIS (O(n^2))

Sort, then `dp[i] = 1 + max(dp[j] for j<i with h[j] < h[i])`: correct, but $O(n^2)$ dies at $n = 10^5$.

## Approach 2 — Sort + patience-sorting LIS (the repo's version, optimal)

```kotlin
import java.util.*

class RussianDollEnvelope {
    /**
     * @param envelopes [width, height] pairs
     * @return         max number of nestable envelopes
     */
    fun maxEnvelopes(envelopes: Array<IntArray>): Int {
        // Sort by width ascending, height descending on ties
        envelopes.sortWith { a, b ->
            when {
                a[0] != b[0] -> a[0] - b[0]    // width ascending
                else -> b[1] - a[1]            // height descending (prevents equal-width nesting)
            }
        }

        // TreeSet keeps the "tails" of increasing height subsequences; size = LIS length
        val treeSet = TreeSet<Int>()

        for ((_, height) in envelopes) {
            // Find the smallest element >= height, replace it (patience sorting)
            val ceilingHeight = treeSet.ceiling(height)
            if (ceilingHeight != null) {
                treeSet.remove(ceilingHeight)
            }
            treeSet.add(height)
        }
        return treeSet.size
    }
}
```

```java
import java.util.*;

public class RussianDollEnvelopes {
    /**
     * @param envelopes [width, height] pairs
     * @return         max number of nestable envelopes
     */
    public int maxEnvelopes(int[][] envelopes) {
        Arrays.sort(envelopes, (a, b) -> a[0] != b[0]
                ? a[0] - b[0]                 // width ascending
                : b[1] - a[1]);               // height descending (prevents equal-width nesting)

        TreeSet<Integer> tails = new TreeSet<>();
        for (int[] e : envelopes) {
            Integer ceil = tails.ceiling(e[1]);      // patience sorting: replace smallest tail >= h
            if (ceil != null) tails.remove(ceil);
            tails.add(e[1]);
        }
        return tails.size();
    }
}
```

```cpp
#include <algorithm>
#include <set>
#include <vector>

class RussianDollEnvelopes {
public:
    /**
     * @param envelopes [width, height] pairs
     * @return         max number of nestable envelopes
     */
    int maxEnvelopes(std::vector<std::vector<int>>& envelopes) {
        std::sort(envelopes.begin(), envelopes.end(),
                  [](const auto& a, const auto& b) {
                      return a[0] != b[0] ? a[0] < b[0]      // width ascending
                                          : a[1] > b[1];     // height descending
                  });

        std::vector<int> tails;                              // patience-sorting tails
        for (auto& e : envelopes) {
            auto it = std::lower_bound(tails.begin(), tails.end(), e[1]);
            if (it == tails.end()) tails.push_back(e[1]);    // extends the longest sequence
            else *it = e[1];                                 // replaces a tail
        }
        return tails.size();
    }
};
```

```python
import bisect

def max_envelopes(envelopes: list[list[int]]) -> int:
    """
    @param envelopes: [width, height] pairs
    @return:          max number of nestable envelopes
    """
    envelopes.sort(key=lambda e: (e[0], -e[1]))    # width asc, height desc on ties

    tails = []
    for _, h in envelopes:
        i = bisect.bisect_left(tails, h)           # patience sorting
        if i == len(tails):
            tails.append(h)                        # extends the longest sequence
        else:
            tails[i] = h                           # replaces a tail
    return len(tails)
```

```rust
impl Solution {
    /// @param envelopes [width, height] pairs
    /// @return         max number of nestable envelopes
    pub fn max_envelopes(mut envelopes: Vec<Vec<i32>>) -> i32 {
        envelopes.sort_by(|a, b| a[0].cmp(&b[0]).then(b[1].cmp(&a[1])));  // width asc, height desc

        let mut tails: Vec<i32> = Vec::new();
        for e in &envelopes {
            match tails.binary_search(&e[1]) {     // patience sorting
                Ok(i) => tails[i] = e[1],
                Err(i) => {
                    if i == tails.len() { tails.push(e[1]); }   // extends the longest sequence
                    else { tails[i] = e[1]; }                  // replaces a tail
                }
            }
        }
        tails.len() as i32
    }
}
```

## Dry run

**Input:** `envelopes = [[5,4],[6,4],[6,7],[2,3]]`.

```
sort (width asc, height desc on ties): [[2,3],[5,4],[6,7],[6,4]]
heights in order: 3, 4, 7, 4

patience-sorting tails:
  h=3: no tail >= 3 -> append.  tails=[3]
  h=4: no tail >= 4 -> append.  tails=[3,4]
  h=7: append.                  tails=[3,4,7]
  h=4: smallest tail >= 4 is 4 -> replace.  tails=[3,4,7]

LIS length = 3 ✓   (the sequence 3,4,7 = [2,3]->[5,4]->[6,7])
```

The tie-break is doing its job at `[6,7]` vs `[6,4]`: heights arrive as `7` then `4`. If the tie sorted height *ascending* (`4` before `7`), the LIS would happily take both — but two envelopes with width 6 can't nest. Descending order makes the pair non-increasing, so the LIS can use at most one.

## Complexity

**Time.** Sort, then $O(\log n)$ per envelope:

$$
T(n) = O(n \log n)
$$

**Space.** The tails structure:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Longest Increasing Subsequence** — this page without the envelopes: the patience-sorting tail-set *is* the LIS algorithm. The repo's `dynamic_programming/` folder has the DP flavor.
- **Width-only nesting (1-D)** — sort + one scan: the 1-D warm-up that shows why 2-D needs the LIS machinery.
- **Interview follow-up:** "Why does the descending-height tie-break make equal-width envelopes incomparable?" The LIS requires *strictly increasing* heights; sorted ties arrive as decreasing heights, so no increasing subsequence can contain two equal-width envelopes — exactly the strictness the nesting rule demands. The comparator silently encodes the constraint.
