# 9.23 Rank Teams By Votes

> **Source**: [`src/main/kotlin/sorting/RankTeamsByVote.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sorting/RankTeamsByVote.kt)
> **Pattern**: position-frequency sort · **Core page**

## The Problem

Rank teams by votes: each vote ranks all teams; compare position counts, then alphabetical.

- Constraints: votes ≤ 1000; teams ≤ 26.

## Examples

```
Input:  votes = ["ABC","ACB","ABC","ACB","ACB"]   -> Output: "ACB"
Input:  votes = ["WXYZ","XYZW"]                   -> Output: "XWYZ"
```

## Intuition — each team gets a position-count vector; sort by it

`map[team][pos]` = how many votes placed the team at `pos`. Sort teams by the vectors lexicographically (descending), then by name:

```kotlin
val map = mutableMapOf<Char, IntArray>()
val l = votes[0].length

for (vote in votes) {
    for (i in vote.indices) {
        val c = vote[i]
        map.putIfAbsent(c, IntArray(l))
        map[c]!![i]++
    }
}

return map.keys.toList().sortedWith { a, b ->
    // compare position counts from 0 upward, then the char
    ...
}.joinToString("")
```

**Why the vector?** "More first-place votes wins" generalizes: compare first-place counts, then second, ... — the vector comparison IS the rule. Ties resolve alphabetically.

## Approach 1 — Vector sort (the repo's version, optimal)

```kotlin
class RankTeamsByVote {
    /**
     * @param votes ranked votes
     * @return      final team order
     */
    fun rankTeams(votes: Array<String>): String {
        val map = mutableMapOf<Char, IntArray>()
        val l = votes[0].length

        for (vote in votes) {
            for (i in vote.indices) {
                val c = vote[i]
                map.putIfAbsent(c, IntArray(l))
                map[c]!![i]++
            }
        }

        return map.keys.toList()
            .sortedWith { a, b ->
                for (i in 0 until l) {
                    if (map[a]!![i] != map[b]!![i]) return@sortedWith map[b]!![i] - map[a]!![i]
                }
                a - b
            }
            .joinToString("")
    }
}
```

```java
import java.util.*;

public class RankTeamsByVotes {
    /**
     * @param votes ranked votes
     * @return      final team order
     */
    public String rankTeams(String[] votes) {
        int n = votes[0].length();
        int[][] count = new int[26][n];
        boolean[] present = new boolean[26];

        for (String vote : votes) {
            for (int i = 0; i < n; i++) {
                int c = vote.charAt(i) - 'A';
                present[c] = true;
                count[c][i]++;
            }
        }

        List<Character> teams = new ArrayList<>();
        for (int i = 0; i < 26; i++) if (present[i]) teams.add((char) ('A' + i));

        teams.sort((a, b) -> {
            for (int i = 0; i < n; i++) {
                if (count[a - 'A'][i] != count[b - 'A'][i])
                    return count[b - 'A'][i] - count[a - 'A'][i];
            }
            return a - b;
        });

        StringBuilder sb = new StringBuilder();
        for (char c : teams) sb.append(c);
        return sb.toString();
    }
}
```

```cpp
#include <vector>
#include <string>
#include <algorithm>

class RankTeamsByVotes {
public:
    /**
     * @param votes ranked votes
     * @return      final team order
     */
    std::string rankTeams(std::vector<std::string>& votes) {
        int n = votes[0].size();
        int count[26][26] = {};
        bool present[26] = {};

        for (auto& vote : votes) {
            for (int i = 0; i < n; i++) {
                int c = vote[i] - 'A';
                present[c] = true;
                count[c][i]++;
            }
        }

        std::string teams;
        for (int i = 0; i < 26; i++) if (present[i]) teams += (char)('A' + i);

        std::sort(teams.begin(), teams.end(), [&](char a, char b) {
            for (int i = 0; i < n; i++) {
                if (count[a - 'A'][i] != count[b - 'A'][i])
                    return count[a - 'A'][i] > count[b - 'A'][i];
            }
            return a < b;
        });

        return teams;
    }
};
```

```python
def rank_teams(votes: list[str]) -> str:
    """
    @param votes: ranked votes
    @return:      final team order
    """
    n = len(votes[0])
    counts = {ch: [0] * n for ch in set("".join(votes))}

    for vote in votes:
        for i, ch in enumerate(vote):
            counts[ch][i] += 1

    return "".join(sorted(counts, key=lambda ch: (-counts[ch][i] for i in range(n)) and ch))
```

```rust
impl Solution {
    /// @param votes ranked votes
    /// @return      final team order
    pub fn rank_teams(votes: Vec<String>) -> String {
        let n = votes[0].len();
        let mut counts: std::collections::HashMap<char, Vec<i32>> = std::collections::HashMap::new();

        for vote in &votes {
            for (i, ch) in vote.chars().enumerate() {
                counts.entry(ch).or_insert(vec![0; n])[i] += 1;
            }
        }

        let mut teams: Vec<char> = counts.keys().copied().collect();
        teams.sort_by(|&a, &b| {
            let (va, vb) = (&counts[&a], &counts[&b]);
            for i in 0..n {
                if va[i] != vb[i] { return vb[i].cmp(&va[i]); }
            }
            a.cmp(&b)
        });

        teams.into_iter().collect()
    }
}
```

## Dry run

**Input:** `votes = ["ABC","ACB","ABC","ACB","ACB"]`.

```
A: [5,0,0].  B: [0,2,3].  C: [0,3,2].
sort: A(5,0,0) first.  B vs C: pos0 tie 0.  pos1: C 3 > B 2 -> C before B.
Output: "ACB" ✓
```

## Complexity

**Time.** Votes × sort:

$$
T(v, n) = O(v \cdot n + n \log n)
$$

**Space.** The counts:

$$
S = O(26 \cdot n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why is the vector comparison the whole rule?" The voting rule "more early-position votes wins, then alphabetically" IS lexicographic comparison of the count vectors — first-place counts decide, then second, etc. The sort comparator encodes the rule verbatim.
