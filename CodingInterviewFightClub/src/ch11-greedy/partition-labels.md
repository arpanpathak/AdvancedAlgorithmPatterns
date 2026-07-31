# 11.24 Partition Labels

> **Source**: [`src/main/kotlin/sliding_window/PartitionLabels.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sliding_window/PartitionLabels.kt)
> **Pattern**: last-occurrence partition · **Core page**

## The Problem

Partition `s` into as many parts as possible so each char appears in only one part.

- Constraints: n ≤ 500.

## Examples

```
Input:  s = "ababcbacadefegdehijhklij"   -> Output: [9,7,8]
Input:  s = "eccbbbbdec"                 -> Output: [10]
```

## Intuition — a partition ends where its chars' last occurrences peak

`lastSeenAt[ch]` = the last index. Scan; `maxLastIndex` = the max last-occurrence in the current part; when `index == maxLastIndex`, the part is complete:

```kotlin
val lastSeenAt = mutableMapOf<Char, Int>().apply {
    s.forEachIndexed { index, char -> this[char] = index }
}
var (startIndex, maxLastIndex) = Pair(0, 0)

s.forEachIndexed { index, char ->
    maxLastIndex = maxOf(maxLastIndex, lastSeenAt[char]!!)

    if (index == maxLastIndex) {
        partitionLengths.add(index - startIndex + 1)
        startIndex = index + 1
    }
}
return partitionLengths
```

**Why the peak test?** A char appearing later forces the partition to extend to its last occurrence — the running max is the current part's required extent. When the index reaches it, no char in the part appears later — a valid cut.

## Approach 1 — Greedy last-occurrence (the repo's version, optimal)

```kotlin
class PartitionLabels {
    /**
     * @param s input string
     * @return  partition lengths
     */
    fun partitionLabels(s: String): List<Int> {
        val partitionLengths = mutableListOf<Int>()
        val lastSeenAt = mutableMapOf<Char, Int>().apply {
            s.forEachIndexed { index, char -> this[char] = index }
        }

        var (startIndex, maxLastIndex) = Pair(0, 0)

        s.forEachIndexed { index, char ->
            maxLastIndex = maxOf(maxLastIndex, lastSeenAt[char]!!)

            if (index == maxLastIndex) {
                partitionLengths.add(index - startIndex + 1)
                startIndex = index + 1
            }
        }
        return partitionLengths
    }
}
```

```java
import java.util.*;

public class PartitionLabels {
    /**
     * @param s input string
     * @return  partition lengths
     */
    public List<Integer> partitionLabels(String s) {
        int[] last = new int[26];
        for (int i = 0; i < s.length(); i++) last[s.charAt(i) - 'a'] = i;

        List<Integer> result = new ArrayList<>();
        int start = 0, maxLast = 0;

        for (int i = 0; i < s.length(); i++) {
            maxLast = Math.max(maxLast, last[s.charAt(i) - 'a']);

            if (i == maxLast) {
                result.add(i - start + 1);
                start = i + 1;
            }
        }
        return result;
    }
}
```

```cpp
#include <string>
#include <vector>
#include <array>
#include <algorithm>

class PartitionLabels {
public:
    /**
     * @param s input string
     * @return  partition lengths
     */
    std::vector<int> partitionLabels(std::string s) {
        std::array<int, 26> last{};
        for (int i = 0; i < (int)s.size(); i++) last[s[i] - 'a'] = i;

        std::vector<int> result;
        int start = 0, maxLast = 0;

        for (int i = 0; i < (int)s.size(); i++) {
            maxLast = std::max(maxLast, last[s[i] - 'a']);

            if (i == maxLast) {
                result.push_back(i - start + 1);
                start = i + 1;
            }
        }
        return result;
    }
};
```

```python
def partition_labels(s: str) -> list[int]:
    """
    @param s: input string
    @return:  partition lengths
    """
    last = {ch: i for i, ch in enumerate(s)}

    result = []
    start = max_last = 0

    for i, ch in enumerate(s):
        max_last = max(max_last, last[ch])

        if i == max_last:
            result.append(i - start + 1)
            start = i + 1

    return result
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param s input string
    /// @return  partition lengths
    pub fn partition_labels(s: String) -> Vec<i32> {
        let bytes: Vec<char> = s.chars().collect();
        let mut last: HashMap<char, usize> = HashMap::new();
        for (i, &ch) in bytes.iter().enumerate() { last.insert(ch, i); }

        let mut result = Vec::new();
        let (mut start, mut max_last) = (0, 0);

        for (i, &ch) in bytes.iter().enumerate() {
            max_last = max_last.max(last[&ch]);

            if i == max_last {
                result.push((i - start + 1) as i32);
                start = i + 1;
            }
        }
        result
    }
}
```

## Dry run

**Input:** `s = "ababcbacadefegdehijhklij"`.

```
last: a=8, b=5, c=7, d=14, e=15, f=11, g=13, h=19, i=22, j=23, k=20, l=21.
scan: a(0): max=8.  b(1): 8.  a(2): 8.  b(3): 8.  c(4): 8.  b(5): 8.  a(6): 8.  c(7): 8.
  a(8): 8 == index -> cut [0,8] len 9.  start=9.
  d(9): max=14.  e(10): 15.  f(11): 15.  g(12): 15.  e(13): 15.  d(14): 15.  e(15): 15 -> cut len 7.
  h(16): 19.  i(17): 22.  j(18): 23.  h(19): 23.  k(20): 23.  l(21): 23.  i(22): 23.  j(23): 23 -> len 8.
Output: [9,7,8] ✓
```

## Complexity

**Time.** Two passes:

$$
T(n) = O(n)
$$

**Space.** The last map:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why is this maximal?" Every cut happens at the earliest point where no char in the part recurs later — cutting later would merge parts (fewer), cutting earlier would split a char's occurrences (invalid). The peak test is both necessary and sufficient.
