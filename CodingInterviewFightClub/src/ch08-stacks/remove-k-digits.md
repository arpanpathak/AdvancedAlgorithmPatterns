# 8.7 Remove K Digits

> **Source:** [`src/main/kotlin/stack/RemoveKDigits.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/RemoveKDigits.kt)
> **Pattern:** monotonic stack + greedy · **Core page**

## The Problem

Given a string `num` (no leading zeros) and an integer `k`, remove `k` digits so that the remaining number is the **smallest possible**. Return it as a string (no leading zeros; `"0"` if empty).

- Constraints: $1 \le n \le 10^5$; $0 \le k \le n$.

## Examples

```
Input:  num = "1432219", k = 3
Output: "1219"     (remove 4, 3, 2 — the three "peaks")

Input:  num = "10200", k = 1
Output: "200"      (removing the 1 — the leading digit — beats removing anything else)

Input:  num = "10", k = 2
Output: "0"
```

## Intuition — "big digits on the left are the enemy"

A number's magnitude is decided by its *leftmost* digits. Removing a digit from the left reduces the value far more than removing one from the right — so the greedy rule is:

> **Scan left to right. Whenever the current digit is smaller than the previous kept digit, deleting the previous one shrinks the number more than any other single deletion — do it, if deletions remain.**

That's a **monotonic-stack** invariant: keep the stack of kept digits *increasing* (smallest first), and each new digit pops every larger digit above it — up to `k` pops total. The popped digits are exactly the "peaks" that made the number big. This is the same while-pop engine as [8.3](daily-temperatures.md) and [8.5](largest-rectangle-in-histogram.md), but the stack holds *digits*, not indices, and the invariant is *increasing*.

Three post-pass details (each one is a classic bug):

1. **Not enough pops** — a strictly increasing number like `"12345"` never triggers a pop; remove from the *end* (`dropLast(remaining)`).
2. **Leading zeros** — `"10200"` with the 1 removed leaves `"0200"` → strip leading zeros.
3. **Empty result** — everything removed → return `"0"`.

## Approach 1 — Try all combinations (too slow)

Choose which k of n digits to remove: $O(\binom{n}{k})$ — hopeless at $n = 10^5$.

## Approach 2 — Monotonic stack (the repo's version, optimal)

```kotlin
class RemoveKDigits {
    /**
     * @param num digit string (no leading zeros)
     * @param k   how many digits to remove
     * @return    smallest number obtainable after removing k digits
     */
    fun removeKdigits(num: String, k: Int): String {
        if (k >= num.length) return "0"

        val stack = ArrayDeque<Char>()
        var remaining = k

        for (digit in num) {
            // Pop larger kept digits while a smaller digit can replace them
            while (remaining > 0 && stack.isNotEmpty() && stack.last() > digit) {
                --remaining
                stack.removeLast()
            }
            stack.addLast(digit)
        }

        return stack.joinToString("")
            .dropLast(remaining)               // increasing tail: trim from the end
            .dropWhile { it == '0' }           // strip leading zeros
            .ifEmpty { "0" }                   // everything removed
    }
}
```

```java
import java.util.*;

public class RemoveKDigits {
    /**
     * @param num digit string (no leading zeros)
     * @param k   how many digits to remove
     * @return    smallest number obtainable after removing k digits
     */
    public String removeKdigits(String num, int k) {
        if (k >= num.length()) return "0";

        Deque<Character> stack = new ArrayDeque<>();
        int remaining = k;

        for (char c : num.toCharArray()) {
            while (remaining > 0 && !stack.isEmpty() && stack.peek() > c) {
                remaining--;                   // pop larger kept digits
                stack.pop();
            }
            stack.push(c);
        }

        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) sb.append(stack.pollLast());   // stack is reversed
        String s = sb.toString();

        if (remaining > 0) s = s.substring(0, s.length() - remaining);  // trim increasing tail
        s = s.replaceFirst("^0+", "");         // strip leading zeros
        return s.isEmpty() ? "0" : s;
    }
}
```

```cpp
#include <deque>
#include <string>

class RemoveKDigits {
public:
    /**
     * @param num digit string (no leading zeros)
     * @param k   how many digits to remove
     * @return    smallest number obtainable after removing k digits
     */
    std::string removeKdigits(std::string num, int k) {
        if (k >= (int)num.size()) return "0";

        std::string st;                        // kept digits; increasing invariant
        int remaining = k;

        for (char c : num) {
            while (remaining > 0 && !st.empty() && st.back() > c) {
                remaining--;                   // pop larger kept digits
                st.pop_back();
            }
            st.push_back(c);
        }

        if (remaining > 0) st.resize(st.size() - remaining);   // trim increasing tail

        int start = 0;
        while (start < (int)st.size() && st[start] == '0') start++;  // strip leading zeros
        std::string result = st.substr(start);
        return result.empty() ? "0" : result;
    }
};
```

```python
def remove_kdigits(num: str, k: int) -> str:
    """
    @param num: digit string (no leading zeros)
    @param k:   how many digits to remove
    @return:    smallest number obtainable after removing k digits
    """
    if k >= len(num):
        return "0"

    stack = []                               # kept digits; increasing invariant
    remaining = k

    for c in num:
        while remaining > 0 and stack and stack[-1] > c:
            remaining -= 1                   # pop larger kept digits
            stack.pop()
        stack.append(c)

    if remaining > 0:                        # trim increasing tail
        stack = stack[:-remaining]

    return "".join(stack).lstrip("0") or "0"
```

```rust
impl Solution {
    /// @param num digit string (no leading zeros)
    /// @param k   how many digits to remove
    /// @return    smallest number obtainable after removing k digits
    pub fn remove_kdigits(num: String, k: i32) -> String {
        if k as usize >= num.len() { return "0".to_string(); }

        let mut stack: Vec<char> = Vec::new();      // kept digits; increasing invariant
        let mut remaining = k;

        for c in num.chars() {
            while remaining > 0 && !stack.is_empty() && *stack.last().unwrap() > c {
                remaining -= 1;                     // pop larger kept digits
                stack.pop();
            }
            stack.push(c);
        }

        stack.truncate(stack.len() - remaining as usize);   // trim increasing tail

        let s: String = stack.into_iter().skip_while(|&c| c == '0').collect(); // strip zeros
        if s.is_empty() { "0".to_string() } else { s }
    }
}
```

## Dry run

**Input:** `num = "1432219"`, `k = 3`.

```
stack = [], remaining = 3
'1' -> push.                          stack=[1]
'4' -> 4 > 1? no pop (invariant ok).  stack=[1,4]
'3' -> 4 > 3 -> pop 4 (rem=2). 3 > 1? no. push 3.   stack=[1,3]
'2' -> 3 > 2 -> pop 3 (rem=1). 2 > 1? no. push 2.   stack=[1,2]
'2' -> 2 > 2? no (equal kept).  push 2.             stack=[1,2,2]
'1' -> 2 > 1 -> pop 2 (rem=0). 2 > 1 -> pop 2.      stack=[1]
       rem=0 stops further pops. push 1.            stack=[1,1]
'9' -> push 9.                                       stack=[1,1,9]

result: "1219" ✓   (the three popped digits 4,3,2 were exactly the "peaks")
```

Now the leading-zero case: `num = "10200"`, `k = 1`.

```
'1' -> push.                     stack=[1]
'0' -> 1 > 0 -> pop 1 (rem=0). push 0.   stack=[0]
'2','0','0' -> rem=0, no pops. push all. stack=[0,2,0,0]
trim: none.  strip leading zeros: "200" ✓  (had we not popped the 1, we'd get "0200" -> "200" anyway,
but for "10", k=1, NOT popping the 1 gives "0" vs popping gives "0" — the greedy pop is what
guarantees the *smallest* in general)
```

The greedy proof in one line: at every pop, the current digit replaces a *larger* digit in a *more significant* position — so that single swap shrinks the number no matter what happens later. Doing it greedily, left to right, up to k times, is optimal by exchange argument.

## Complexity

**Time.** Each digit pushed once, popped at most once:

$$
T(n) = O(n)
$$

**Space.** The stack:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Remove Duplicate Letters / Smallest Subsequence Of Distinct Characters** (`src/main/kotlin/stack/RemoveDuplicateLetters.kt`) — the same "monotonic increasing stack" greedy, with a *must-keep-once* constraint (each letter must appear; the pop guard uses remaining counts).
- **Sum Of Subarray Minimums** (`src/main/kotlin/stack/SumOfSubArrayMinimum.kt`) — the increasing-stack invariant counting contributions instead of minimizing one number.
- **Interview follow-up:** "Why is the invariant *increasing*, not decreasing?" Decreasing keeps big digits at the front — the opposite of what we want. Increasing means every kept digit is no larger than the one after it, so deleting from the *end* (when pops are unused) also removes the largest remaining digits. The invariant encodes the goal directly.
