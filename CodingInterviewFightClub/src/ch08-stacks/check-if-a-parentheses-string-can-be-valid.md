# 8.22 Check If A Parentheses String Can Be Valid

> **Source**: [`src/main/kotlin/string/CheckifaParenthesesStringCanBeValid.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/CheckifaParenthesesStringCanBeValid.kt)
> **Pattern**: balance-range sweep · **Core page**

## The Problem

`locked[i] == '1'` fixes `s[i]`; `'0'` means the char can flip. Can the string be valid?

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  s = "))()))", locked = "010100"   -> Output: true
Input:  s = "()()", locked = "0000"       -> Output: true
Input:  s = ")", locked = "0"             -> Output: false
```

## Intuition — the balance is a *range*, not a number

With flexible chars, the open-balance at each prefix is an interval `[minOpen, maxOpen]` — a locked `(` raises both, a locked `)` lowers both, a flexible char either raises or lowers:

```kotlin
if (s.length % 2 != 0) return false      // odd length can never balance

var openCount = 0                        // (the repo's left-to-right pass)
for (i in 0 until s.length) {
    if (s[i] == '(' || (s[i] == ')' && locked[i] == '0'))
        openCount++                       // can be treated as open
    else openCount--                      // must be close

    if (openCount < 0) return false
}
// + a symmetric right-to-left pass for the closable side
```

**Why two passes?** The left pass guarantees enough opens; a mirror pass (right-to-left, counting closes) guarantees enough closes. Both must hold — the [8.17](minimum-add-to-make-parentheses-valid.md) counters with flexibility.

## Approach 1 — Balance-range sweep (the canonical, optimal)

Track `[minBalance, maxBalance]`; locked chars shift both; flexible widen the range:

```kotlin
var minBalance = 0
var maxBalance = 0

for (i in s.indices) {
    if (locked[i] == '1') {
        if (s[i] == '(') { minBalance++; maxBalance++ }
        else { minBalance--; maxBalance-- }
    } else {
        minBalance--       // treat as ')'
        maxBalance++       // treat as '('
    }

    if (maxBalance < 0) return false
    if (minBalance < 0) minBalance = 0     // prefix can't go negative
}
return minBalance == 0
```

**Why the clamp?** A valid prefix's balance is ≥ 0 — a negative min just means "we could have used a flexible char differently"; the range [0, max] is what remains feasible.

## Approach 2 — Two passes (the repo's version)

Left-to-right count of open-capable; right-to-left count of close-capable — the classic alternative.

```kotlin
class CheckifaParenthesesStringCanBeValid {
    /**
     * @param s      parentheses string
     * @param locked '1' fixed, '0' flexible
     * @return       true iff the string can be valid
     */
    fun canBeValid(s: String, locked: String): Boolean {
        if (s.length % 2 != 0) return false

        var openCount = 0
        for (i in 0 until s.length) {
            if (s[i] == '(' || (s[i] == ')' && locked[i] == '0'))
                openCount++
            else openCount--

            if (openCount < 0) return false
        }

        var closeCount = 0
        for (i in s.length - 1 downTo 0) {
            if (s[i] == ')' || (s[i] == '(' && locked[i] == '0'))
                closeCount++
            else closeCount--

            if (closeCount < 0) return false
        }
        return true
    }
}
```

```java
public class CheckIfAParenthesesStringCanBeValid {
    /**
     * @param s      parentheses string
     * @param locked '1' fixed, '0' flexible
     * @return       true iff the string can be valid
     */
    public boolean canBeValid(String s, String locked) {
        if (s.length() % 2 != 0) return false;

        int open = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(' || (s.charAt(i) == ')' && locked.charAt(i) == '0')) open++;
            else open--;

            if (open < 0) return false;
        }

        int close = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == ')' || (s.charAt(i) == '(' && locked.charAt(i) == '0')) close++;
            else close--;

            if (close < 0) return false;
        }
        return true;
    }
}
```

```cpp
#include <string>

class CheckIfAParenthesesStringCanBeValid {
public:
    /**
     * @param s      parentheses string
     * @param locked '1' fixed, '0' flexible
     * @return       true iff the string can be valid
     */
    bool canBeValid(std::string s, std::string locked) {
        if (s.size() % 2 != 0) return false;

        int open = 0;
        for (int i = 0; i < (int)s.size(); i++) {
            if (s[i] == '(' || (s[i] == ')' && locked[i] == '0')) open++;
            else open--;

            if (open < 0) return false;
        }

        int close = 0;
        for (int i = s.size() - 1; i >= 0; i--) {
            if (s[i] == ')' || (s[i] == '(' && locked[i] == '0')) close++;
            else close--;

            if (close < 0) return false;
        }
        return true;
    }
};
```

```python
def can_be_valid(s: str, locked: str) -> bool:
    """
    @param s:      parentheses string
    @param locked: '1' fixed, '0' flexible
    @return:       true iff the string can be valid
    """
    if len(s) % 2 != 0:
        return False

    open_count = 0
    for i, ch in enumerate(s):
        if ch == "(" or (ch == ")" and locked[i] == "0"):
            open_count += 1
        else:
            open_count -= 1

        if open_count < 0:
            return False

    close_count = 0
    for i in range(len(s) - 1, -1, -1):
        if s[i] == ")" or (s[i] == "(" and locked[i] == "0"):
            close_count += 1
        else:
            close_count -= 1

        if close_count < 0:
            return False

    return True
```

```rust
impl Solution {
    /// @param s      parentheses string
    /// @param locked '1' fixed, '0' flexible
    /// @return       true iff the string can be valid
    pub fn can_be_valid(s: String, locked: String) -> bool {
        let (sb, lb) = (s.as_bytes(), locked.as_bytes());
        if sb.len() % 2 != 0 { return false; }

        let mut open = 0;
        for i in 0..sb.len() {
            if sb[i] == b'(' || (sb[i] == b')' && lb[i] == b'0') { open += 1; }
            else { open -= 1; }

            if open < 0 { return false; }
        }

        let mut close = 0;
        for i in (0..sb.len()).rev() {
            if sb[i] == b')' || (sb[i] == b'(' && lb[i] == b'0') { close += 1; }
            else { close -= 1; }

            if close < 0 { return false; }
        }
        true
    }
}
```

## Dry run

**Input:** `s = "))()))", locked = "010100"`.

```
left pass: i0 ')': locked 0 -> open=1.  i1 ')': locked 1 -> open=0.  i2 '(': open=1.
  i3 ')': locked 0 -> open=2.  i4 ')': locked 1 -> open=1.  i5 ')': locked 0 -> open=2.  OK.
right pass: i5 ')': locked 0 -> close=1.  i4 ')': locked 1 -> close=2.  i3 ')': 0 -> 3.
  i2 '(': locked 1 -> close=2.  i1 ')': 1 -> 3.  i0 ')': 0 -> 4.  OK.
Output: true ✓   (flip the flexible chars: "()(())" works)
```

The two passes prove both directions: every prefix has enough "could-be-opens" and every suffix enough "could-be-closes" — the classic greedy characterization of flexible matching. Odd length is the instant rejection (balanced strings are even).

## Complexity

**Time.** Two passes:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Minimum Add To Make Valid** ([8.17](minimum-add-to-make-parentheses-valid.md)) — the counting ancestor.
- **Interview follow-up:** "Why must both passes succeed?" A prefix short of opens OR a suffix short of closes is fatal — the flexible chars can't fix a deficit they don't cover. The two conditions are necessary and jointly sufficient (each flexible char can be assigned consistently).
