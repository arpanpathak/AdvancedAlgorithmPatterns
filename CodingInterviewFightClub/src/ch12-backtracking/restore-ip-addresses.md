# 12.6 Restore IP Addresses

> **Source:** [`src/main/kotlin/backtracking/RestoreIPAddresses.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/backtracking/RestoreIPAddresses.kt)
> **Pattern:** segment-length pruning · **Core page**

## The Problem

Given a string `s` of digits, return all possible valid IP addresses obtainable by inserting `'.'` — each of the 4 segments must be `0..255` with no leading zeros (unless the segment is `"0"` itself).

- Constraints: $1 \le n \le 20$; digits only.

## Examples

```
Input:  s = "25525511135"
Output: ["255.255.11.135","255.255.111.35"]

Input:  s = "0000"
Output: ["0.0.0.0"]

Input:  s = "101023"
Output: ["1.0.10.23","1.0.102.3","10.1.0.23","10.10.2.3","101.0.2.3"]
```

## Intuition — exactly 4 pieces, each 1-3 digits, each valid

The IP grammar fixes the shape: **four segments**, each of length 1..3 (a segment can't exceed 3 digits, and there must be enough digits left for the rest). Backtracking places segments left to right:

```
dfs(i, path):                 # i = position in s, path = segments so far
    if path.size == 4:
        if i == len: record (all digits consumed, exactly 4 segments)
        return                # 4 segments reached but digits remain -> invalid, prune
    for len in 1..3:
        if i + len <= len(s):
            segment = s[i..i+len)
            if segment is valid (no leading zero, value <= 255):
                path.add(segment); dfs(i + len, path); path.removeLast()
```

The two pruning layers:

1. **The 4-segment cap** — once `path.size == 4`, either we're done (`i == len`, record) or we've failed (leftover digits — the string would need a 5th segment). This is what prunes branches like splitting `"255255..."` into `2,5,5,2` and still having digits left.
2. **The per-segment validity** — length 1..3, no leading zeros, value ≤ 255. The leading-zero rule is the one people forget: `"01"` is invalid even though it's in range.

**Why is the loop bounded by 3?** A segment is 1, 2, or 3 digits — anything longer exceeds 255. The bound is the *grammar* speaking: fixed shape → fixed branching factor → tiny tree.

## Approach 1 — Four nested loops

For every quadruple of cut positions, validate: works, but it's the "unrolled" version of the same search with more special cases.

## Approach 2 — Backtracking with segment pruning (the repo's version, optimal)

```kotlin
class RestoreIPAddresses {
    /**
     * @param s digit string
     * @return  all valid IP addresses obtainable by inserting dots
     */
    fun restoreIpAddresses(s: String): List<String> {
        val result = mutableListOf<String>()

        fun dfs(i: Int, path: MutableList<String>) {
            if (path.size == 4) {                        // four segments placed
                if (i == s.length) {                     // ...and all digits used: complete
                    result.add(path.joinToString("."))
                }
                return                                   // 4 segments but digits remain: prune
            }

            for (len in 1..3) {                          // segments are 1-3 digits
                if (i + len <= s.length) {
                    val segment = s.substring(i, i + len)

                    // Skip invalid segments
                    if ((segment.length > 1 && segment[0] == '0') || segment.toInt() > 255) continue

                    path.add(segment)
                    dfs(i + len, path)
                    path.removeAt(path.size - 1)         // undo
                }
            }
        }

        dfs(0, mutableListOf())
        return result
    }
}
```

```java
import java.util.*;

public class RestoreIPAddresses {
    /**
     * @param s digit string
     * @return  all valid IP addresses obtainable by inserting dots
     */
    public List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        dfs(0, s, new ArrayList<>(), result);
        return result;
    }

    private void dfs(int i, String s, List<String> path, List<String> result) {
        if (path.size() == 4) {                          // four segments placed
            if (i == s.length()) {                       // ...and all digits used: complete
                result.add(String.join(".", path));
            }
            return;                                      // 4 segments but digits remain: prune
        }

        for (int len = 1; len <= 3; len++) {
            if (i + len > s.length()) continue;
            String seg = s.substring(i, i + len);

            if ((seg.length() > 1 && seg.charAt(0) == '0')      // leading zero
                || Integer.parseInt(seg) > 255) continue;       // out of range

            path.add(seg);
            dfs(i + len, s, path, result);
            path.remove(path.size() - 1);                // undo
        }
    }
}
```

```cpp
#include <string>
#include <vector>

class RestoreIPAddresses {
    void dfs(int i, const std::string& s, std::vector<std::string>& path,
             std::vector<std::string>& result) {
        if (path.size() == 4) {                          // four segments placed
            if (i == (int)s.size()) {                    // ...and all digits used: complete
                result.push_back(path[0] + "." + path[1] + "." + path[2] + "." + path[3]);
            }
            return;                                      // 4 segments but digits remain: prune
        }

        for (int len = 1; len <= 3; len++) {
            if (i + len > (int)s.size()) continue;
            std::string seg = s.substr(i, len);

            if ((seg.size() > 1 && seg[0] == '0') || std::stoi(seg) > 255) continue;

            path.push_back(seg);
            dfs(i + len, s, path, result);
            path.pop_back();                             // undo
        }
    }

public:
    /**
     * @param s digit string
     * @return  all valid IP addresses obtainable by inserting dots
     */
    std::vector<std::string> restoreIpAddresses(std::string s) {
        std::vector<std::string> result;
        std::vector<std::string> path;
        dfs(0, s, path, result);
        return result;
    }
};
```

```python
def restore_ip_addresses(s: str) -> list[str]:
    """
    @param s: digit string
    @return:  all valid IP addresses obtainable by inserting dots
    """
    result = []
    path = []

    def dfs(i: int) -> None:
        if len(path) == 4:                   # four segments placed
            if i == len(s):                  # ...and all digits used: complete
                result.append(".".join(path))
            return                           # 4 segments but digits remain: prune

        for length in range(1, 4):           # segments are 1-3 digits
            if i + length <= len(s):
                seg = s[i:i + length]
                if (len(seg) > 1 and seg[0] == "0") or int(seg) > 255:
                    continue                 # leading zero or out of range
                path.append(seg)
                dfs(i + length)
                path.pop()                   # undo

    dfs(0)
    return result
```

```rust
impl Solution {
    /// @param s digit string
    /// @return  all valid IP addresses obtainable by inserting dots
    pub fn restore_ip_addresses(s: String) -> Vec<String> {
        let mut result = Vec::new();
        let mut path: Vec<&str> = Vec::new();

        fn dfs<'a>(i: usize, s: &'a str, path: &mut Vec<&'a str>, result: &mut Vec<String>) {
            if path.len() == 4 {                     // four segments placed
                if i == s.len() {                    // ...and all digits used: complete
                    result.push(path.join("."));
                }
                return;                              // 4 segments but digits remain: prune
            }

            for len in 1..=3 {
                if i + len > s.len() { continue; }
                let seg = &s[i..i + len];
                if (seg.len() > 1 && seg.starts_with('0')) || seg.parse::<i32>().unwrap() > 255 {
                    continue;                        // leading zero or out of range
                }
                path.push(seg);
                dfs(i + len, s, path, result);
                path.pop();                          // undo
            }
        }

        dfs(0, &s, &mut path, &mut result);
        result
    }
}
```

## Dry run

**Input:** `s = "25525511135"`.

```
dfs(0): segments possible: "2","25","255"
  "2" -> path=["2"].  dfs(1):
    "5","55","552"?  "552" > 255 skip.  "5" -> path=["2","5"].  dfs(2):
      "5","52","525"? skip.  "5" -> path=["2","5","5"].  dfs(3): path.size==4? no (3).  ...
      "52" -> path=["2","5","52"].  dfs(4):
        need 1 more segment from "51135": "5" -> path=[..,"5"] -> dfs: 4 segments, digits remain -> prune.
        "51" -> path=[..,"51"] -> digits remain -> prune.  "511" > 255 skip.
      ... no way to finish with 4 segments -> dead end
  "25" -> path=["25"].  dfs(2):
    "5" -> path=["25","5"].  dfs(3):
      "5" -> path=["25","5","5"].  dfs(4):
        "5","51","511" -> "5": path=["25","5","5","5"]: 4 segments, i=5 < len -> prune.
        "51": path=["25","5","5","51"]: prune.  "511" > 255 skip.
        -> dead end
      "52" -> path=["25","5","52"].  dfs(5):
        remaining "51135": "5" -> 4 segs, digits remain -> prune.  "51" -> prune. "511" skip.
        -> dead end
    "52" -> path=["25","52"].  dfs(4):
      "5" -> path=["25","52","5"].  dfs(5):
        "5" -> path=["25","52","5","5"] -> prune (digits remain: "135")
        "51" -> prune.  "511" skip.  dead end
      "52" -> path=["25","52","52"].  dfs(6):
        "5" -> prune.  "51" -> prune.  "511" skip.  dead end
    "525" skip (>255)
  "255" -> path=["255"].  dfs(3):
    "2" -> path=["255","2"].  dfs(4):
      "5" -> path=["255","2","5"].  dfs(5):
        "1" -> path=["255","2","5","1"].  4 segments, i=6 < 11 -> prune
        "11" -> path=["255","2","5","11"]. prune.  "111" -> prune.
        dead end
      "55" -> path=["255","2","55"].  dfs(6):
        "1" -> prune.  "11" -> prune.  "111" -> prune.  dead end
    "25" -> path=["255","25"].  dfs(5):
      "5" -> path=["255","25","5"].  dfs(6):
        "1" -> path=[..,"1"] -> 4 segs, i=7 < 11 -> prune.
        "11" -> prune.  "111" -> prune.  dead end
      "51" -> path=["255","25","51"].  dfs(7):
        "1" -> prune.  "13" -> prune.  "135" -> prune.  dead end
      "511" > 255 skip
    "255" -> path=["255","255"].  dfs(6):
      "1" -> path=["255","255","1"].  dfs(7):
        "1" -> path=[..,"1"] -> prune.  "13" -> prune.  "135" -> prune.  dead end
      "11" -> path=["255","255","11"].  dfs(8):
        "1" -> path=["255","255","11","1"] -> 4 segs, i=9 < 11 -> prune
        "13" -> path=[..,"13"] -> prune.  "135" -> prune.  dead end
      "111" -> path=["255","255","111"].  dfs(9):
        "3" -> path=["255","255","111","3"] -> 4 segs, i=10 < 11 -> prune
        "35" -> prune.  dead end
    "2551" > 255 skip

result: ["255.255.11.135","255.255.111.35"] ✓
```

The trace (abridged) shows the pruning rhythm: branches that reach 4 segments with digits remaining die instantly, and every out-of-range or leading-zero segment is skipped at the source. The tree is tiny because the grammar is rigid.

## Complexity

**Time.** At most $3^4$ branches (bounded shape — 4 segments, ≤3 lengths each):

$$
T(n) = O(1) \quad \text{(constant — the shape is fixed)}
$$

**Space.** Path + result:

$$
S(n) = O(1) \text{ extra}
$$

## Variants & follow-ups

- **Validate IP Address** ([9.7](../ch09-strings/validate-ip-address.md)) — the *checking* direction of the same segment grammar; this page generates what that page validates.
- **Decode Ways / Restore expressions** — the same "choose the next piece length 1..k, validate, recurse" skeleton with different validity rules.
- **Expression Add Operators** (`src/main/kotlin/backtracking/ExpressionAndAddOperators.kt`) — the same cut-based backtracking with arithmetic state (a running value + the last operand for precedence) — the deep end of this family.
- **Interview follow-up:** "Why does the 4-segment cap prune so much?" The grammar fixes the *number* of segments, so any branch that hits 4 segments early has only one way to finish (consume everything) — and if digits remain, the branch is dead. That single check, plus the 1..3 length bound, keeps the tree at ≤ 3^4 nodes instead of $2^{n-1}$.
