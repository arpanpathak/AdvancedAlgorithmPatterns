# 9.12 Count And Say

> **Source:** [`src/main/kotlin/string/CountAndSay.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/CountAndSay.kt)
> **Pattern:** run-length iteration · **Core page**

## The Problem

`countAndSay(1) = "1"`; each term **describes the previous**: "one 1" → "11", "two 1s" → "21", etc. Return the n-th term.

- Constraints: $1 \le n \le 30$; terms can be long.

## Examples

```
1: "1"
2: "11"      (one 1)
3: "21"      (two 1s)
4: "1211"    (one 2, one 1)
5: "111221"  (one 1, one 2, two 1s)
```

## Intuition — each term is the run-length encoding of the previous

The transformation is mechanical: scan the current term, count each run of identical digits, emit `count + digit`. Repeat `n - 1` times starting from `"1"`:

```kotlin
var result = "1"
repeat(n - 1) {
    result = buildString {
        var count = 1
        for (i in 1 until result.length) {
            if (result[i] == result[i - 1]) count++
            else { append(count).append(result[i - 1]); count = 1 }
        }
        append(count).append(result.last())    // flush the final run
    }
}
return result
```

**Why `repeat(n - 1)`?** The first term is given; each iteration produces the next. The `buildString` is the run-length encoder — the [9.x](../ch09-strings/pattern-primer.md) "read the runs, write the counts" idiom in its purest form.

**Why the explicit `result.last()` flush?** The loop's `else` emits on *change* — the final run never changes, so it must be flushed after the loop. The off-by-one that makes run-length encoding tests interesting.

## Approach 1 — Build the whole sequence (O(n · term))

Recursive or iterative with a helper: exactly this page; the term length grows ~30% per step.

## Approach 2 — Iterative run-length (the repo's version, optimal)

```kotlin
class CountAndSay {
    /**
     * @param n term index (1-based)
     * @return  the n-th count-and-say term
     */
    fun countAndSay(n: Int): String {
        var result = "1"

        repeat(n - 1) {
            val nextSequence = buildString {
                var count = 1
                for (i in 1 until result.length) {
                    if (result[i] == result[i - 1]) {
                        count++
                    } else {
                        append(count).append(result[i - 1])
                        count = 1
                    }
                }
                append(count).append(result.last())    // flush the final run
            }
            result = nextSequence
        }
        return result
    }
}
```

```java
public class CountAndSay {
    /**
     * @param n term index (1-based)
     * @return  the n-th count-and-say term
     */
    public String countAndSay(int n) {
        String result = "1";

        for (int step = 1; step < n; step++) {
            StringBuilder next = new StringBuilder();
            int count = 1;

            for (int i = 1; i < result.length(); i++) {
                if (result.charAt(i) == result.charAt(i - 1)) count++;
                else {
                    next.append(count).append(result.charAt(i - 1));
                    count = 1;
                }
            }
            next.append(count).append(result.charAt(result.length() - 1));   // flush
            result = next.toString();
        }
        return result;
    }
}
```

```cpp
#include <string>

class CountAndSay {
public:
    /**
     * @param n term index (1-based)
     * @return  the n-th count-and-say term
     */
    std::string countAndSay(int n) {
        std::string result = "1";

        for (int step = 1; step < n; step++) {
            std::string next;
            int count = 1;

            for (int i = 1; i < (int)result.size(); i++) {
                if (result[i] == result[i - 1]) count++;
                else {
                    next += std::to_string(count) + result[i - 1];
                    count = 1;
                }
            }
            next += std::to_string(count) + result.back();   // flush the final run
            result = next;
        }
        return result;
    }
};
```

```python
def count_and_say(n: int) -> str:
    """
    @param n: term index (1-based)
    @return:  the n-th count-and-say term
    """
    result = "1"

    for _ in range(n - 1):
        next_seq = []
        count = 1
        for i in range(1, len(result)):
            if result[i] == result[i - 1]:
                count += 1
            else:
                next_seq.append(str(count) + result[i - 1])
                count = 1
        next_seq.append(str(count) + result[-1])    # flush the final run
        result = "".join(next_seq)

    return result
```

```rust
impl Solution {
    /// @param n term index (1-based)
    /// @return  the n-th count-and-say term
    pub fn count_and_say(n: i32) -> String {
        let mut result = "1".to_string();

        for _ in 1..n {
            let mut next = String::new();
            let chars: Vec<char> = result.chars().collect();
            let mut count = 1;

            for i in 1..chars.len() {
                if chars[i] == chars[i - 1] { count += 1; }
                else {
                    next.push_str(&count.to_string());
                    next.push(chars[i - 1]);
                    count = 1;
                }
            }
            next.push_str(&count.to_string());       // flush the final run
            next.push(*chars.last().unwrap());
            result = next;
        }
        result
    }
}
```

## Dry run

**Input:** `n = 4`.

```
result = "1"
step 1: scan "1": final run count=1, digit '1' -> "11"
step 2: scan "11": i=1 same -> count=2.  flush "2"+"1" -> "21"
step 3: scan "21": i=1: '1' != '2' -> emit "1"+"2" -> count=1.  flush "1"+"1" -> "1211"

Output: "1211" ✓
```

Each step is the run-length encoding of the previous term: "21" = one 2 and one 1 → "1211". The flush-after-loop is what emits the *last* run — the `else` branch only fires on a change, so the terminal run needs the explicit `append(count).append(result.last())`.

## Complexity

**Time.** Terms grow ~1.3x per step:

$$
T(n) = O(\text{length of the n-th term})
$$

**Space.** The current and next term:

$$
S = O(\text{term length})
$$

## Variants & follow-ups

- **String Compression** (`string/StringCompression.kt`) — the inverse: encode a string's runs in place.
- **Interview follow-up:** "Why does the term length grow super-linearly?" Each term roughly 1.3× the previous (the count digit adds ~a third), so the 30th term is ~10⁴ characters — still fine for the loop, but the exponential feel is why the problem caps n at 30.
