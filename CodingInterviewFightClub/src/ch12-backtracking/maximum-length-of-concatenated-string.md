# 12.20 Maximum Length Of Concatenated String With Unique Characters

> **Source**: [`src/main/kotlin/string/MaximumLengthofaConcatenatedStringwithUniqueCharacters.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/MaximumLengthofaConcatenatedStringwithUniqueCharacters.kt)
> **Pattern**: bitmask backtracking · **Core page**

## The Problem

The longest concatenation of strings with all-unique characters.

- Constraints: n ≤ 16.

## Examples

```
Input:  arr = ["un","iq","ue"]   -> Output: 4   ("un" + "iq" = "uniq")
```

## Intuition — prune strings with internal duplicates; backtrack over masks

Each string becomes a bitmask; concatenating is an OR — legal iff disjoint:

```kotlin
val uniqueStrings = arr.filter { it.toCharArray().toSet().size == it.length }

fun backtrack(index: Int, currentMask: Int, currentLength: Int) {
    if (index == uniqueStrings.size) {
        maxLen = maxOf(maxLen, currentLength)
        return
    }

    // skip
    backtrack(index + 1, currentMask, currentLength)

    // take (if disjoint)
    val mask = uniqueStrings[index].fold(0) { acc, c -> acc or (1 shl (c - 'a')) }
    if ((currentMask and mask) == 0) {
        backtrack(index + 1, currentMask or mask, currentLength + uniqueStrings[index].length)
    }
}
```

## Approach 1 — Bitmask backtracking (the repo's version, optimal)

```kotlin
class MaximumLengthofaConcatenatedStringwithUniqueCharacters {
    /**
     * @param arr strings
     * @return    longest unique-char concatenation
     */
    fun maxLength(arr: List<String>): Int {
        var maxLen = 0
        val uniqueStrings = arr.filter { it.toCharArray().toSet().size == it.length }

        fun backtrack(index: Int, currentMask: Int, currentLength: Int) {
            if (index == uniqueStrings.size) {
                if (currentLength > maxLen) maxLen = currentLength
                return
            }

            val word = uniqueStrings[index]
            var mask = 0
            for (ch in word) mask = mask or (1 shl (ch - 'a'))

            backtrack(index + 1, currentMask, currentLength)

            if ((currentMask and mask) == 0) {
                backtrack(index + 1, currentMask or mask, currentLength + word.length)
            }
        }

        backtrack(0, 0, 0)
        return maxLen
    }
}
```

```java
public class MaximumLengthOfConcatenatedString {
    private int best = 0;

    private void backtrack(String[] arr, int index, int mask, int length) {
        if (index == arr.length) {
            best = Math.max(best, length);
            return;
        }

        backtrack(arr, index + 1, mask, length);

        int m = 0;
        boolean ok = true;
        for (char c : arr[index].toCharArray()) {
            int bit = 1 << (c - 'a');
            if ((m & bit) != 0) { ok = false; break; }
            m |= bit;
        }

        if (ok && (mask & m) == 0) {
            backtrack(arr, index + 1, mask | m, length + arr[index].length());
        }
    }

    /**
     * @param arr strings
     * @return    longest unique-char concatenation
     */
    public int maxLength(List<String> arr) {
        best = 0;
        backtrack(arr.toArray(new String[0]), 0, 0, 0);
        return best;
    }
}
```

```cpp
#include <vector>
#include <string>
#include <algorithm>

class MaximumLengthOfConcatenatedString {
    int best = 0;

    void backtrack(std::vector<std::string>& arr, int index, int mask, int length) {
        if (index == (int)arr.size()) {
            best = std::max(best, length);
            return;
        }

        backtrack(arr, index + 1, mask, length);

        int m = 0;
        bool ok = true;
        for (char c : arr[index]) {
            int bit = 1 << (c - 'a');
            if (m & bit) { ok = false; break; }
            m |= bit;
        }

        if (ok && !(mask & m)) {
            backtrack(arr, index + 1, mask | m, length + arr[index].size());
        }
    }

public:
    /**
     * @param arr strings
     * @return    longest unique-char concatenation
     */
    int maxLength(std::vector<std::string>& arr) {
        best = 0;
        backtrack(arr, 0, 0, 0);
        return best;
    }
};
```

```python
def max_length(arr: list[str]) -> int:
    """
    @param arr: strings
    @return:    longest unique-char concatenation
    """
    best = 0

    def backtrack(index: int, mask: int, length: int) -> None:
        nonlocal best
        if index == len(arr):
            best = max(best, length)
            return

        backtrack(index + 1, mask, length)

        m = 0
        ok = True
        for ch in arr[index]:
            bit = 1 << (ord(ch) - ord("a"))
            if m & bit:
                ok = False
                break
            m |= bit

        if ok and (mask & m) == 0:
            backtrack(index + 1, mask | m, length + len(arr[index]))

    backtrack(0, 0, 0)
    return best
```

```rust
impl Solution {
    /// @param arr strings
    /// @return    longest unique-char concatenation
    pub fn max_length(arr: Vec<String>) -> i32 {
        fn backtrack(arr: &Vec<String>, index: usize, mask: i32, length: i32, best: &mut i32) {
            if index == arr.len() {
                *best = (*best).max(length);
                return;
            }

            backtrack(arr, index + 1, mask, length, best);

            let mut m = 0;
            let mut ok = true;
            for c in arr[index].chars() {
                let bit = 1 << (c as i32 - 'a' as i32);
                if m & bit != 0 { ok = false; break; }
                m |= bit;
            }

            if ok && mask & m == 0 {
                backtrack(arr, index + 1, mask | m, length + arr[index].len() as i32, best);
            }
        }

        let mut best = 0;
        backtrack(&arr, 0, 0, 0, &mut best);
        best
    }
}
```

## Dry run

**Input:** `arr = ["un","iq","ue"]`.

```
skip all: 0.  take "un" (mask u|n, len 2).  + "iq" (disjoint): len 4.  + "ue"? u collides -> skip.
Other branches: "iq"+"ue" = 4.  "un"+"ue" collides.
best = 4 ✓
```

## Complexity

**Time.** 2^n branches:

$$
T(n) = O(2^n)
$$

**Space.** Recursion:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Subsets II** ([12.12](subsets-ii.md)) — the include/exclude ancestor.
- **Interview follow-up:** "Why the mask OR test?" Characters are unique iff the masks are disjoint — a single AND decides legality, making each branch O(1) after the per-string mask.
