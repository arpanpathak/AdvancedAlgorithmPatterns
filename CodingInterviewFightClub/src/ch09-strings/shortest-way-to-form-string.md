# 9.33 Shortest Way To Form String

> **Source**: [`src/main/kotlin/string/greedy/ShortestWayToFormAString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/greedy/ShortestWayToFormAString.kt)
> **Pattern**: greedy subsequence scan · **Core page**

## The Problem

Min copies of `source` (as subsequences) to form `target`, or -1.

- Constraints: lengths ≤ 1000.

## Examples

```
Input:  source = "abc", target = "abcbc"   -> Output: 2
Input:  source = "abc", target = "acdbc"   -> Output: -1
```

## Intuition — greedily match target chars against repeated source scans

```kotlin
val sourceSet = source.toSet()
target.forEach {
    if (!sourceSet.contains(it)) return -1
}

var count = 0
var targetIndex = 0

while (targetIndex < target.length) {
    count++
    var sourceIndex = 0

    while (targetIndex < target.length && sourceIndex < source.length) {
        if (source[sourceIndex] == target[targetIndex]) {
            targetIndex++
        }
        sourceIndex++
    }
}
return count
```

## Approach 1 — Greedy repeated scans (the repo's version, optimal)

```kotlin
class ShortestWayToFormAString {
    /**
     * @param source source string
     * @param target target string
     * @return       min subsequence copies or -1
     */
    fun shortestWay(source: String, target: String): Int {
        val sourceSet = source.toSet()

        target.forEach {
            if (!sourceSet.contains(it)) return -1
        }

        var count = 0
        var targetIndex = 0

        while (targetIndex < target.length) {
            count++
            var sourceIndex = 0

            while (targetIndex < target.length && sourceIndex < source.length) {
                if (source[sourceIndex] == target[targetIndex]) {
                    targetIndex++
                }
                sourceIndex++
            }
        }
        return count
    }
}
```

```java
public class ShortestWayToFormString {
    /**
     * @param source source string
     * @param target target string
     * @return       min subsequence copies or -1
     */
    public int shortestWay(String source, String target) {
        boolean[] present = new boolean[26];
        for (char c : source.toCharArray()) present[c - 'a'] = true;

        for (char c : target.toCharArray()) {
            if (!present[c - 'a']) return -1;
        }

        int count = 0, ti = 0;

        while (ti < target.length()) {
            count++;
            int si = 0;

            while (ti < target.length() && si < source.length()) {
                if (source.charAt(si) == target.charAt(ti)) ti++;
                si++;
            }
        }
        return count;
    }
}
```

```cpp
#include <string>
#include <unordered_set>

class ShortestWayToFormString {
public:
    /**
     * @param source source string
     * @param target target string
     * @return       min subsequence copies or -1
     */
    int shortestWay(std::string source, std::string target) {
        std::unordered_set<char> chars(source.begin(), source.end());

        for (char c : target) {
            if (!chars.count(c)) return -1;
        }

        int count = 0, ti = 0;

        while (ti < (int)target.size()) {
            count++;
            int si = 0;

            while (ti < (int)target.size() && si < (int)source.size()) {
                if (source[si] == target[ti]) ti++;
                si++;
            }
        }
        return count;
    }
};
```

```python
def shortest_way(source: str, target: str) -> int:
    """
    @param source: source string
    @param target: target string
    @return:       min subsequence copies or -1
    """
    if not set(target).issubset(set(source)):
        return -1

    count = 0
    ti = 0

    while ti < len(target):
        count += 1
        si = 0

        while ti < len(target) and si < len(source):
            if source[si] == target[ti]:
                ti += 1
            si += 1

    return count
```

```rust
impl Solution {
    /// @param source source string
    /// @param target target string
    /// @return       min subsequence copies or -1
    pub fn shortest_way(source: String, target: String) -> i32 {
        let s: Vec<char> = source.chars().collect();
        let t: Vec<char> = target.chars().collect();
        let chars: std::collections::HashSet<char> = s.iter().copied().collect();

        if t.iter().any(|c| !chars.contains(c)) { return -1; }

        let mut count = 0;
        let mut ti = 0;

        while ti < t.len() {
            count += 1;
            let mut si = 0;

            while ti < t.len() && si < s.len() {
                if s[si] == t[ti] { ti += 1; }
                si += 1;
            }
        }
        count
    }
}
```

## Dry run

**Input:** `source = "abc", target = "abcbc"`.

```
copy 1: scan abc: a(0)✓ b(1)✓ c(2)✓ b(3)✓ -> target 4.
copy 2: scan abc: c(4)✓ -> 5.
Output: 2 ✓
```

## Complexity

**Time.** Scans per copy:

$$
T(n, m) = O(m \cdot n)
$$

**Space.** The set:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Is Subsequence** ([9.20](is-subsequence.md)) — the single-copy test.
- **Interview follow-up:** "Why is the greedy exact?" Each copy greedily consumes the longest prefix of target — any optimal solution uses at least as many copies because each copy can consume at most that much.
