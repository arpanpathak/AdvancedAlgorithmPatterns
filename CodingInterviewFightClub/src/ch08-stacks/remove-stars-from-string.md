# 8.24 Remove Stars From String

> **Source**: [`src/main/kotlin/stack/RemoveStarsFromString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/RemoveStarsFromString.kt)
> **Pattern**: stack with star-erase · **Core page**

## The Problem

Each `*` erases the nearest non-star to its left.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  s = "leet**cod*e"   -> Output: "lecoe"
```

## Intuition — push chars; a star pops the last

```kotlin
val stack = mutableListOf<Char>()

for (ch in s) {
    if (ch == '*' && stack.isNotEmpty()) stack.removeLast()
    else if (ch != '*') stack.add(ch)
}
return stack.joinToString("")
```

## Approach 1 — Stack erasure (the repo's version, optimal)

```kotlin
class RemoveStarsFromString {
    /**
     * @param s input string
     * @return  star-processed string
     */
    fun removeStars(s: String): String {
        val stack = mutableListOf<Char>()

        for (ch in s) {
            if (ch == '*' && stack.isNotEmpty()) stack.removeLast()
            else if (ch != '*') stack.add(ch)
        }
        return stack.joinToString("")
    }
}
```

```java
public class RemoveStarsFromString {
    /**
     * @param s input string
     * @return  star-processed string
     */
    public String removeStars(String s) {
        StringBuilder sb = new StringBuilder();

        for (char ch : s.toCharArray()) {
            if (ch == '*') {
                if (sb.length() > 0) sb.setLength(sb.length() - 1);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
```

```cpp
#include <string>

class RemoveStarsFromString {
public:
    /**
     * @param s input string
     * @return  star-processed string
     */
    std::string removeStars(std::string s) {
        std::string result;

        for (char ch : s) {
            if (ch == '*') {
                if (!result.empty()) result.pop_back();
            } else {
                result += ch;
            }
        }
        return result;
    }
};
```

```python
def remove_stars(s: str) -> str:
    """
    @param s: input string
    @return:  star-processed string
    """
    stack = []

    for ch in s:
        if ch == "*":
            if stack:
                stack.pop()
        else:
            stack.append(ch)

    return "".join(stack)
```

```rust
impl Solution {
    /// @param s input string
    /// @return  star-processed string
    pub fn remove_stars(s: String) -> String {
        let mut result: Vec<char> = Vec::new();

        for ch in s.chars() {
            if ch == '*' { result.pop(); }
            else { result.push(ch); }
        }
        result.into_iter().collect()
    }
}
```

## Reading the code — what's actually happening

```kotlin
val stack = mutableListOf<Char>()
for (ch in s) {
    if (ch == '*' && stack.isNotEmpty()) stack.removeLast()
    else if (ch != '*') stack.add(ch)
}
return stack.joinToString("")
```

A star erases "the nearest non-star to its left" — and "nearest left" is exactly what a **stack** gives you: the most recently pushed character sits on top, so erasing the nearest left character is just a `pop`.

- **`stack.add(ch)` pushes every ordinary character.** The stack grows left to right, so its top is always the most recently *surviving* character — precisely "the nearest non-star to the left" of any future position.
- **`stack.removeLast()` is the erasure.** On a `'*'`, the character it cancels is the current top of the stack: the nearest surviving character to the left. Popping it is O(1). The `stack.isNotEmpty()` guard prevents popping an empty stack (e.g. a leading `'*'` — a star with nothing to erase just does nothing).
- **Why a stack and not a plain scan?** The erasure can be *non-adjacent* — in `"le**t"`, the second `'*'` erases the `'e'` that's two positions left, after the first star already erased `'t'`. An in-place scan would need to track "which characters are still alive" — the stack *is* that tracking, in the natural order.
- **The final `joinToString("")` rebuilds the string from the survivors.** The stack's contents, in order, are exactly the answer — no reversal needed since we only ever push and pop at the end.

Trace `"leet**cod*e"`: push `l,e,e,t` → `*` pops `t` → `*` pops `e` → push `c,o,d` → `*` pops `d` → push `e`. Stack: `l,e,c,o,e` → `"lecoe"` ✓.

## Dry run

**Input:** `s = "leet**cod*e"`.

```
l e e t -> * pop t -> * pop e -> c o d -> * pop d -> e.
Output: "lecoe" ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The stack:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Remove All Adjacent Duplicates** ([8.16](remove-all-adjacent-duplicates.md)) — the adjacent-pair sibling.
- **Interview follow-up:** "Why a stack and not an in-place scan?" The star erases *non-adjacent* leftward characters — the stack preserves the exact "nearest survivor" semantics.
