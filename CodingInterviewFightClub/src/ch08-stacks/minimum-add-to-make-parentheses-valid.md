# 8.17 Minimum Add To Make Parentheses Valid

> **Source:** [`src/main/kotlin/stack/MinimumAddtoMakeParenthesesValid.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/MinimumAddtoMakeParenthesesValid.kt)
> **Pattern:** unmatched-counter balance · **Core page**

## The Problem

Min parentheses to add so `s` is valid (properly matched).

- Constraints: n ≤ 1000.

## Examples

```
Input:  s = "())"   -> Output: 1   (add '(')
Input:  s = "((("   -> Output: 3
Input:  s = "()"    -> Output: 0
```

## Intuition — count unmatched opens and stray closes

Two counters: `open` = unmatched `(`, `minCount` = stray `)`. A `(` increments open; a `)` either matches an open (open--) or is stray (minCount++):

```kotlin
s.forEach { ch ->
    when (ch) {
        '(' -> stack.add(ch)                       // or open++
        ')' -> if (stack.isNotEmpty() && stack.last() == '(') stack.removeLast()
               else minCount++                     // stray close
    }
}
return minCount + stack.size
```

**Why the answer is `stray + remaining opens`?** Every unmatched `(` needs a `)`, every stray `)` needs a `(` — the two counters sum to the minimum insertions. The [8.1](valid-parentheses.md) match test, counting instead of just checking.

## Approach 1 — Stack (the repo's version)

Push `(`, pop on matching `)`, count strays; answer = strays + stack size.

## Approach 2 — Two counters (O(1) space)

`open++` on `(`, `open-- or needsClose++` on `)`; answer = `open + needsClose` — the stack's essence without the container.

```kotlin
class MinimumAddtoMakeParenthesesValid {
    /**
     * @param s parentheses string
     * @return  min insertions to make it valid
     */
    fun minAddToMakeValid(s: String): Int {
        var minCount = 0
        val stack = mutableListOf<Char>()

        s.forEach { ch ->
            when (ch) {
                '(' -> stack.add(ch)
                ')' -> if (stack.isNotEmpty() && stack.last() == '(') {
                    stack.removeLast()
                } else {
                    minCount++
                }
            }
        }
        return minCount + stack.size
    }
}
```

```java
public class MinimumAddToMakeParenthesesValid {
    /**
     * @param s parentheses string
     * @return  min insertions to make it valid
     */
    public int minAddToMakeValid(String s) {
        int open = 0, stray = 0;

        for (char c : s.toCharArray()) {
            if (c == '(') open++;
            else if (open > 0) open--;
            else stray++;
        }
        return open + stray;
    }
}
```

```cpp
class MinimumAddToMakeParenthesesValid {
public:
    /**
     * @param s parentheses string
     * @return  min insertions to make it valid
     */
    int minAddToMakeValid(std::string s) {
        int open = 0, stray = 0;

        for (char c : s) {
            if (c == '(') open++;
            else if (open > 0) open--;
            else stray++;
        }
        return open + stray;
    }
};
```

```python
def min_add_to_make_valid(s: str) -> int:
    """
    @param s: parentheses string
    @return:  min insertions to make it valid
    """
    open_ = stray = 0

    for ch in s:
        if ch == "(":
            open_ += 1
        elif open_ > 0:
            open_ -= 1
        else:
            stray += 1

    return open_ + stray
```

```rust
impl Solution {
    /// @param s parentheses string
    /// @return  min insertions to make it valid
    pub fn min_add_to_make_valid(s: String) -> i32 {
        let (mut open, mut stray) = (0, 0);

        for ch in s.chars() {
            if ch == '(' { open += 1; }
            else if open > 0 { open -= 1; }
            else { stray += 1; }
        }
        open + stray
    }
}
```

## Dry run

**Input:** `s = "())"`.

```
'(': open=1.  ')': open=0.  ')': open==0 -> stray=1.
Output: 1 + 0 = 1 ✓

Input: "(((": open=3.  Output: 3 + 0 = 3 ✓
Input: ")(": ')': stray=1.  '(': open=1.  Output: 1 + 1 = 2 ✓  (need "()"+"()" or "()()" inserted)
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** O(1) (counters) / O(n) (stack):

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Minimum Remove To Make Valid** ([8.18](minimum-remove-to-make-valid-parentheses.md)) — remove instead of add: indices get marked.
- **Valid Parentheses** ([8.1](valid-parentheses.md)) — the checking ancestor.
- **Interview follow-up:** "Why do the two counters never overcount?" Each `(` is either matched (open-- later) or left unmatched (counted at the end); each stray `)` is counted once at its occurrence. Every insertion fixes exactly one deficit — the sum is both necessary and sufficient.
