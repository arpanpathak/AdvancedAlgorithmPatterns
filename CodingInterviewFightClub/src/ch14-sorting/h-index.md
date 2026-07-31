# 14.5 H-Index

> **Source:** [`src/main/kotlin/sorting/HIndex.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sorting/HIndex.kt)
> **Pattern:** sort + scan · **Core page**

## The Problem

Given an array `citations` where `citations[i]` is the citation count of paper `i`, return the **h-index**: the largest `h` such that **at least `h` papers have at least `h` citations**.

- Constraints: $1 \le n \le 5000$; $0 \le citations[i] \le 1000$.

## Examples

```
Input:  citations = [3,0,6,1,5]   -> Output: 3   (papers with >= 3 citations: 3,6,5)
Input:  citations = [1,3,1]       -> Output: 1
```

## Intuition — sort descending, then "rank vs citations" is the check

Sort descending. Now `citations[i]` is the citation count of the `i+1`-th most-cited paper, and the h-index definition becomes a single scan:

> `h` is the largest value where `citations[i] >= i + 1` still holds.

Walk the sorted array; the first position where `citations[i] < i + 1` breaks the run — and the answer is `i` (that many papers met the bar). If no break, every paper clears the bar, and the answer is `n`.

**Why does the sorted scan capture the definition?** "At least h papers with ≥ h citations" — in descending order, that's "the first h papers all have ≥ h citations". The scan finds the largest such h by checking the boundary position where the requirement fails: papers `0..i-1` have ≥ i citations, paper `i` doesn't.

**Why not test all h?** You *could* binary search h or count frequencies (the counting variant, $O(n + \text{max citation})$). The sort-then-scan is the simplest correct shape; the counting version is the "no sort needed" optimization when citations are bounded (≤ 1000 here, so `O(n + 1000)` counting beats `O(n log n)`).

## Approach 1 — Count frequencies (O(n + maxC))

`count[c]` = papers with exactly c citations; walk from max down accumulating papers ≥ h: $O(n + \text{maxC})$, no sort. The "values are bounded" optimization worth mentioning.

## Approach 2 — Sort descending + scan (the repo's version, optimal)

```kotlin
class HIndex {
    /**
     * @param citations citations[i] = citation count of paper i
     * @return         the h-index
     */
    fun hIndex(citations: IntArray): Int {
        // Step 1: Sort the citations in descending order
        citations.sortDescending()

        // Step 2: Find the h-index
        for (i in citations.indices) {
            // The current index represents the number of papers.
            // Check if the current citation count is >= index + 1.
            if (citations[i] < i + 1) {
                return i                              // papers 0..i-1 met the bar
            }
        }
        return citations.size                         // every paper met the bar
    }
}
```

```java
import java.util.*;

public class HIndex {
    /**
     * @param citations citations[i] = citation count of paper i
     * @return         the h-index
     */
    public int hIndex(int[] citations) {
        Integer[] sorted = Arrays.stream(citations).boxed()
                .sorted(Collections.reverseOrder()).toArray(Integer[]::new);  // descending

        for (int i = 0; i < sorted.length; i++) {
            if (sorted[i] < i + 1) return i;          // papers 0..i-1 met the bar
        }
        return sorted.length;                         // every paper met the bar
    }
}
```

```cpp
#include <algorithm>
#include <vector>

class HIndex {
public:
    /**
     * @param citations citations[i] = citation count of paper i
     * @return         the h-index
     */
    int hIndex(std::vector<int>& citations) {
        std::sort(citations.begin(), citations.end(), std::greater<int>());   // descending

        for (int i = 0; i < (int)citations.size(); i++) {
            if (citations[i] < i + 1) return i;       // papers 0..i-1 met the bar
        }
        return citations.size();                      // every paper met the bar
    }
};
```

```python
def h_index(citations: list[int]) -> int:
    """
    @param citations: citations[i] = citation count of paper i
    @return:          the h-index
    """
    citations.sort(reverse=True)             # descending

    for i, c in enumerate(citations):
        if c < i + 1:
            return i                         # papers 0..i-1 met the bar
    return len(citations)                    # every paper met the bar
```

```rust
impl Solution {
    /// @param citations citations[i] = citation count of paper i
    /// @return         the h-index
    pub fn h_index(citations: Vec<i32>) -> i32 {
        let mut citations = citations;
        citations.sort_unstable_by(|a, b| b.cmp(a));   // descending

        for (i, &c) in citations.iter().enumerate() {
            if c < (i + 1) as i32 {
                return i as i32;             // papers 0..i-1 met the bar
            }
        }
        citations.len() as i32               // every paper met the bar
    }
}
```

## Dry run

**Input:** `citations = [3,0,6,1,5]`.

```
sorted descending: [6,5,3,1,0]
i=0: 6 >= 1 ok.  i=1: 5 >= 2 ok.  i=2: 3 >= 3 ok.  i=3: 1 >= 4? NO -> return 3 ✓
```

Check the definition against the answer: h=3 means "≥3 papers with ≥3 citations" — papers with citations 6,5,3 (three of them) ✓. And h=4 fails: only 3 papers have ≥4 citations. The first failed check (`1 < 4`) is exactly where the definition stops holding.

## Complexity

**Time.** Sort dominates:

$$
T(n) = O(n \log n)
$$

**Space.** In-place sort:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Counting version** — with citations ≤ 1000, count frequencies and walk backward accumulating: $O(n + \text{maxC})$, no sort. Mention when values are bounded.
- **H-Index II** — the *sorted* input version: binary search for the boundary in $O(\log n)$.
- **Interview follow-up:** "Why is the boundary check `citations[i] < i + 1` the whole problem?" After sorting descending, the condition "the first i papers have ≥ i citations" is checked at exactly one position — paper i is the first one *failing* the bar, so the count of passing papers is i. The sort converts a counting question into a boundary scan.
