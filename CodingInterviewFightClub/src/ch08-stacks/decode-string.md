# 8.8 Decode String

> **Source:** [`src/main/kotlin/string/stack/DecodeString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/stack/DecodeString.kt)
> **Pattern:** recursion with a shared index (or two stacks) · **Core page**

## The Problem

Given an encoded string like `"3[a]2[bc]"`, decode it: `k[...]` means the content repeated `k` times. The encoding can nest (`"3[a2[c]]"`).

- Constraints: input is well-formed; `k` fits in `Int`; digits ≥ 1.

## Examples

```
Input:  s = "3[a]2[bc]"   -> Output: "aaabcbc"
Input:  s = "3[a2[c]]"    -> Output: "accaccacc"
Input:  s = "2[abc]3[cd]ef" -> Output: "abcabccdcdcdef"
```

## Intuition — `k[` opens a recursive subproblem; the shared index is the stack

The string is a grammar: `string := (digit '[' string ']' | char)*`. Two clean implementations:

1. **Recursion with a shared `index`** (the repo's version): the top-level loop consumes chars; on a digit it parses `k`, skips `[`, **recursively decodes the inside**, skips `]`, and repeats the result `k` times. The shared `index` plays the stack's role — the recursion *is* the stack frame for nested brackets.
2. **Two explicit stacks** (count stack + string stack): push counts and partial strings on `[`, pop-and-repeat on `]`. The iterative sibling.

**Why does the recursive version need a shared index?** The inner call must *continue* the same scan — a by-value index would restart the inner decode at the beginning. `index` as a class field (the repo's `private var index = 0`) is the shared cursor; the `]` stop condition (`s[index] != ']'`) returns control to the caller, which consumes the `]` and repeats.

**The `k` parse** — multi-digit counts: `k = k * 10 + (digit - '0')` accumulates until a non-digit. The recursion boundary: digits may not be inside brackets (the input is well-formed), so a letter after `]` resumes the top-level loop naturally.

## Approach 1 — Two stacks (iterative)

`countStack` + `strStack`; on `[` push both, on `]` pop and repeat: same O(result) complexity, no recursion. The classic interview alternative.

## Approach 2 — Recursive with shared index (the repo's version, optimal)

```kotlin
class DecodeString {
    private var index = 0

    /**
     * @param s encoded string
     * @return  decoded string
     */
    fun decodeString(s: String): String {
        val result = StringBuilder()

        while (index < s.length && s[index] != ']') {
            val ch = s[index]
            when {
                !ch.isDigit() -> {                 // plain character: copy it
                    result.append(ch)
                    index++
                }
                else -> {                          // digit: parse k, recurse into the brackets
                    var k = 0
                    while (index < s.length && s[index].isDigit()) {
                        k = k * 10 + (s[index++] - '0')
                    }
                    index++                        // skip '['
                    val nested = decodeString(s)   // recursively decode the inside
                    index++                        // skip ']'

                    repeat(k) { result.append(nested) }
                }
            }
        }
        return result.toString()
    }
}
```

```java
public class DecodeString {
    private int index = 0;

    /**
     * @param s encoded string
     * @return  decoded string
     */
    public String decodeString(String s) {
        StringBuilder result = new StringBuilder();

        while (index < s.length() && s.charAt(index) != ']') {
            char ch = s.charAt(index);
            if (!Character.isDigit(ch)) {            // plain character: copy it
                result.append(ch);
                index++;
            } else {                                 // digit: parse k, recurse into the brackets
                int k = 0;
                while (index < s.length() && Character.isDigit(s.charAt(index))) {
                    k = k * 10 + (s.charAt(index++) - '0');
                }
                index++;                             // skip '['
                String nested = decodeString(s);     // recursively decode the inside
                index++;                             // skip ']'

                result.append(nested.repeat(k));
            }
        }
        return result.toString();
    }
}
```

```cpp
#include <string>

class DecodeString {
    std::string s;
    int i = 0;

    std::string decode() {
        std::string result;
        while (i < (int)s.size() && s[i] != ']') {
            if (!std::isdigit(s[i])) {               // plain character: copy it
                result += s[i++];
            } else {                                 // digit: parse k, recurse into the brackets
                int k = 0;
                while (i < (int)s.size() && std::isdigit(s[i])) k = k * 10 + (s[i++] - '0');
                i++;                                 // skip '['
                std::string nested = decode();       // recursively decode the inside
                i++;                                 // skip ']'
                while (k--) result += nested;
            }
        }
        return result;
    }

public:
    /**
     * @param str encoded string
     * @return    decoded string
     */
    std::string decodeString(std::string str) {
        s = str;
        return decode();
    }
};
```

```python
class DecodeString:
    """@param s: encoded string"""

    def __init__(self):
        self.index = 0

    def decode_string(self, s: str) -> str:
        """@return: decoded string"""
        result = []

        while self.index < len(s) and s[self.index] != "]":
            ch = s[self.index]
            if not ch.isdigit():                 # plain character: copy it
                result.append(ch)
                self.index += 1
            else:                                # digit: parse k, recurse into the brackets
                k = 0
                while self.index < len(s) and s[self.index].isdigit():
                    k = k * 10 + int(s[self.index]); self.index += 1
                self.index += 1                  # skip '['
                nested = self.decode_string(s)   # recursively decode the inside
                self.index += 1                  # skip ']'
                result.append(nested * k)

        return "".join(result)
```

```rust
impl Solution {
    /// @param s encoded string
    /// @return  decoded string
    pub fn decode_string(s: String) -> String {
        let bytes: Vec<u8> = s.bytes().collect();
        let mut i = 0usize;

        fn decode(bytes: &Vec<u8>, i: &mut usize) -> String {
            let mut result = String::new();
            while *i < bytes.len() && bytes[*i] != b']' {
                if !bytes[*i].is_ascii_digit() {     // plain character: copy it
                    result.push(bytes[*i] as char);
                    *i += 1;
                } else {                             // digit: parse k, recurse into the brackets
                    let mut k = 0;
                    while *i < bytes.len() && bytes[*i].is_ascii_digit() {
                        k = k * 10 + (bytes[*i] - b'0') as usize;
                        *i += 1;
                    }
                    *i += 1;                         // skip '['
                    let nested = decode(bytes, i);   // recursively decode the inside
                    *i += 1;                         // skip ']'
                    result.push_str(&nested.repeat(k));
                }
            }
            result
        }

        decode(&bytes, &mut i)
    }
}
```

## Dry run

**Input:** `s = "3[a2[c]]"`.

```
decodeString():
  index=0: '3' is digit -> k = 3.  index=2 (past '[').  recurse:
    index=2: 'a' -> copy.  result="a".  index=3.
    index=3: '2' is digit -> k=2.  index=5 (past '[').  recurse:
      index=5: 'c' -> copy.  result="c".  index=6.
      index=6: ']' -> stop.  return "c".
    index=6: skip ']' -> index=7.  repeat "c" 2 times -> "acc".  result="a"+"cc"="acc".
    index=7: ']' -> stop.  return "acc".
  index=7: skip ']' -> index=8.  repeat "acc" 3 times -> "accaccacc".

Output: "accaccacc" ✓
```

The recursion is the bracket stack: each `[` enters a frame, each `]` exits it — with the shared index carrying the cursor across frames. The innermost `"c"` is decoded first and multiplied outward, which is exactly the nesting semantics.

## Complexity

**Time.** Each decoded character is appended once, and each `k`-repeat writes k copies:

$$
T(n) = O(\text{result length})
$$

**Space.** Recursion depth (bracket nesting) + result:

$$
S(n) = O(\text{nesting depth} + \text{result length})
$$

## Variants & follow-ups

- **Simplify Path / Remove All Adjacent Duplicates** (`string/stack/`) — the stack-string family; Decode String is the nested member.
- **Two-stack iterative version** — countStack + stringStack: same complexity, no recursion (the interview "translate recursion to stacks" drill).
- **Interview follow-up:** "Why does a shared index work but a passed index not?" The inner call must continue the *same* scan — a by-value index would re-scan from the bracket's start on every return. The field/closure index is the single cursor all frames share; each frame's `while` condition (`s[index] != ']'`) is what hands control back to the caller cleanly.
