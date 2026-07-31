# 16.10 Number Of Steps To Reduce A Number In Binary Representation To One

> **Source**: [`src/main/kotlin/bitset/Number of Steps to ReduceaANumberInBinaryRepresentationtoOne.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/bitset/Number%20of%20Steps%20to%20ReduceaANumberInBinaryRepresentationtoOne.kt)
> **Pattern**: bit-level simulation with carry · **Core page**

## The Problem

`numSteps(s)` — steps to reduce the binary number `s` to 1: if even divide by 2, if odd add 1.

- Constraints: length ≤ 500.

## Examples

```
Input:  s = "1101"   -> Output: 6   (13 -> 14 -> 7 -> 8 -> 4 -> 2 -> 1)
Input:  s = "10"     -> Output: 1
```

## Intuition — simulate from the least-significant bit, carrying the +1

Scan right-to-left; each bit decides the step count. An odd bit (1 + carry) needs **add 1 then divide by 2 = 2 steps** and propagates a carry; an even bit just divides = 1 step:

```kotlin
var steps = 0
var carry = 0

for (i in s.length - 1 downTo 1) {
    val digit = (s[i] - '0') + carry
    if (digit % 2 == 1) {
        steps += 2      // add 1 (makes it even) + divide by 2
        carry = 1       // the +1 propagates
    } else {
        steps += 1      // just divide by 2
    }
}
return steps
```

**Why skip index 0?** The loop ends when the number is 1 — the most significant bit is the final "1", not processed. The carry never reaches it (a leading 1 with carry would be 2 → 1 after one divide... the known answer handles this: if the final digit becomes 2 via carry, one more step... the repo's version stops at index 1; the standard LeetCode solution adds `steps + carry` for the leading bit).

**Why `digit % 2` instead of `s[i]`?** The +1 from a previous carry flips parity — `digit` is the *effective* bit. The carry is the whole subtlety.

## Approach 1 — Big-integer simulation

Parse and loop: works, but the problem's point is bit manipulation.

## Approach 2 — Carry-aware bit scan (the repo's version, optimal)

```kotlin
class `Number of Steps to ReduceaANumberInBinaryRepresentationtoOne` {
    /**
     * @param s binary number string
     * @return  steps to reduce to 1
     */
    fun numSteps(s: String): Int {
        var steps = 0
        var carry = 0

        for (i in s.length - 1 downTo 1) {
            val digit = (s[i] - '0') + carry

            if (digit % 2 == 1) {
                steps += 2
                carry = 1
            } else {
                steps += 1
            }
        }
        return steps + carry      // the leading bit: +1 if a carry reached it
    }
}
```

```java
public class NumberOfStepsToReduceANumberInBinaryRepresentationToOne {
    /**
     * @param s binary number string
     * @return  steps to reduce to 1
     */
    public int numSteps(String s) {
        int steps = 0, carry = 0;

        for (int i = s.length() - 1; i > 0; i--) {
            int digit = (s.charAt(i) - '0') + carry;

            if (digit % 2 == 1) { steps += 2; carry = 1; }
            else steps += 1;
        }
        return steps + carry;
    }
}
```

```cpp
#include <string>

class NumberOfStepsToReduceANumberInBinaryRepresentationToOne {
public:
    /**
     * @param s binary number string
     * @return  steps to reduce to 1
     */
    int numSteps(std::string s) {
        int steps = 0, carry = 0;

        for (int i = s.size() - 1; i > 0; i--) {
            int digit = (s[i] - '0') + carry;

            if (digit % 2 == 1) { steps += 2; carry = 1; }
            else steps += 1;
        }
        return steps + carry;
    }
};
```

```python
def num_steps(s: str) -> int:
    """
    @param s: binary number string
    @return:  steps to reduce to 1
    """
    steps = carry = 0

    for ch in reversed(s[1:]):
        digit = int(ch) + carry

        if digit % 2 == 1:
            steps += 2          # add 1 (even) + divide by 2
            carry = 1
        else:
            steps += 1          # divide by 2

    return steps + carry
```

```rust
impl Solution {
    /// @param s binary number string
    /// @return  steps to reduce to 1
    pub fn num_steps(s: String) -> i32 {
        let bytes: Vec<char> = s.chars().collect();
        let mut steps = 0;
        let mut carry = 0;

        for i in (1..bytes.len()).rev() {
            let digit = (bytes[i] as i32 - '0' as i32) + carry;

            if digit % 2 == 1 {
                steps += 2;     // add 1 (even) + divide by 2
                carry = 1;
            } else {
                steps += 1;     // divide by 2
            }
        }
        steps + carry
    }
}
```

## Dry run

**Input:** `s = "1101"` (13).

```
i=3 '1': digit 1+0 = 1 odd -> steps=2, carry=1.
i=2 '0': digit 0+1 = 1 odd -> steps=4, carry=1.
i=1 '1': digit 1+1 = 2 even -> steps=5.
return steps + carry = 5 + 1 = 6 ✓

(13 -> +1 = 14 (1) -> /2 = 7 (2) -> +1 = 8 (3) -> /2 = 4 (4) -> /2 = 2 (5) -> /2 = 1 (6))
```

The carry chain is the simulation's memory: the `0` at index 2 becomes odd via the carry from index 3 — a plain bit scan would undercount. The `steps + carry` at the end accounts for the leading bit becoming 2 (one more divide).

## Complexity

**Time.** One pass over bits:

$$
T(L) = O(L)
$$

**Space.** Constants:

$$
S(L) = O(1)
$$

## Variants & follow-ups

- **Number Of 1 Bits** ([16.1](number-of-1-bits.md)) — the bit-counting sibling.
- **Interview follow-up:** "Why 2 steps for an odd effective bit?" An odd number adds 1 (becoming even) then divides — two operations. The carry models the add's propagation; the loop's `digit % 2` handles a carry-turned-even bit correctly.
