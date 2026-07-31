# 9.21 String Compression

> **Source**: [`src/main/kotlin/string/StringCompression.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/StringCompression.kt)
> **Pattern**: run-length in place · **Core page**

## The Problem

Compress `chars` in place (run-length): `aabbccc` → `a2b2c3`; return the new length.

- Constraints: n ≤ 2000.

## Examples

```
Input:  chars = ["a","a","b","b","c","c","c"]   -> Output: 6 ("a2b2c3")
Input:  chars = ["a"]                            -> Output: 1
```

## Intuition — a write pointer + a run counter

Scan with a read pointer; count the run; write the char (and count if > 1) at the write pointer:

```kotlin
var writeIndex = 0
var i = 0

while (i < chars.size) {
    val ch = chars[i]
    var count = 0

    while (i < chars.size && chars[i] == ch) { i++; count++ }

    chars[writeIndex++] = ch
    if (count > 1) {
        for (digit in count.toString()) chars[writeIndex++] = digit
    }
}
return writeIndex
```

**Why the write pointer?** In-place compression overwrites earlier slots as it goes — the write pointer always trails (or equals) the read pointer, so overwriting is safe. The [3.18](../ch03-arrays/remove-duplicates-from-sorted-array.md) write-pointer discipline.

## Approach 1 — String builder + rebuild (the repo's style)

Correct but not in-place.

## Approach 2 — In-place run-length (optimal)

```kotlin
class StringCompression {
    /**
     * @param chars character array
     * @return      compressed length (chars mutated in place)
     */
    fun compress(chars: CharArray): Int {
        var writeIndex = 0
        var i = 0

        while (i < chars.size) {
            val ch = chars[i]
            var count = 0

            while (i < chars.size && chars[i] == ch) {
                i++
                count++
            }

            chars[writeIndex++] = ch
            if (count > 1) {
                for (digit in count.toString()) {
                    chars[writeIndex++] = digit
                }
            }
        }
        return writeIndex
    }
}
```

```java
public class StringCompression {
    /**
     * @param chars character array
     * @return      compressed length (chars mutated in place)
     */
    public int compress(char[] chars) {
        int write = 0, i = 0;

        while (i < chars.length) {
            char ch = chars[i];
            int count = 0;

            while (i < chars.length && chars[i] == ch) { i++; count++; }

            chars[write++] = ch;
            if (count > 1) {
                for (char d : Integer.toString(count).toCharArray()) chars[write++] = d;
            }
        }
        return write;
    }
}
```

```cpp
#include <vector>
#include <string>

class StringCompression {
public:
    /**
     * @param chars character array
     * @return      compressed length (chars mutated in place)
     */
    int compress(std::vector<char>& chars) {
        int write = 0, i = 0;

        while (i < (int)chars.size()) {
            char ch = chars[i];
            int count = 0;

            while (i < (int)chars.size() && chars[i] == ch) { i++; count++; }

            chars[write++] = ch;
            if (count > 1) {
                for (char d : std::to_string(count)) chars[write++] = d;
            }
        }
        return write;
    }
};
```

```python
def compress(chars: list[str]) -> int:
    """
    @param chars: character array
    @return:      compressed length (chars mutated in place)
    """
    write = i = 0

    while i < len(chars):
        ch = chars[i]
        count = 0

        while i < len(chars) and chars[i] == ch:
            i += 1
            count += 1

        chars[write] = ch
        write += 1
        if count > 1:
            for d in str(count):
                chars[write] = d
                write += 1

    return write
```

```rust
impl Solution {
    /// @param chars character array
    /// @return      compressed length (chars mutated in place)
    pub fn compress(chars: &mut Vec<char>) -> i32 {
        let mut write = 0;
        let mut i = 0;

        while i < chars.len() {
            let ch = chars[i];
            let mut count = 0;

            while i < chars.len() && chars[i] == ch { i += 1; count += 1; }

            chars[write] = ch;
            write += 1;
            if count > 1 {
                for d in count.to_string().chars() {
                    chars[write] = d;
                    write += 1;
                }
            }
        }
        write as i32
    }
}
```

## Dry run

**Input:** `chars = ["a","a","b","b","c","c","c"]`.

```
run 'a' x2: write 'a', '2'.  chars[0..1] = a,2.  write=2.
run 'b' x2: write 'b', '2'.  write=4.
run 'c' x3: write 'c', '3'.  write=6.

Output: 6, chars[0..5] = "a2b2c3" ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** In place:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Remove Duplicates From Sorted Array** ([3.18](../ch03-arrays/remove-duplicates-from-sorted-array.md)) — the write-pointer ancestor.
- **Interview follow-up:** "Why is the overwrite safe?" The write pointer never exceeds the read pointer (each run writes ≤ its length), so compressed prefixes never clobber unread input.
