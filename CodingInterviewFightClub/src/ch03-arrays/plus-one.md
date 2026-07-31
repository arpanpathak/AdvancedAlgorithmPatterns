# 3.25 Plus One

> **Source**: [`src/main/kotlin/math/PlusOne.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/PlusOne.kt)
> **Pattern**: digit carry from the right · **Core page**

## The Problem

Add one to a big integer represented as a digit array.

- Constraints: n ≤ 100.

## Examples

```
Input:  digits = [1,2,3]   -> Output: [1,2,4]
Input:  digits = [9,9,9]   -> Output: [1,0,0,0]
```

## Intuition — the carry stops at the first non-9

Scan from the right: a digit < 9 just increments and returns; a 9 becomes 0 and carries:

```kotlin
for (i in digits.lastIndex downTo 0) {
    if (digits[i] < 9) {
        digits[i]++
        return digits
    }
    digits[i] = 0
}
return intArrayOf(1) + digits     // all 9s: [9,9,9] -> [1,0,0,0]
```

## Approach 1 — Convert to number (overflow!)

Parse and add: breaks for long arrays.

## Approach 2 — In-place carry (the repo's version, optimal)

```kotlin
class PlusOne {
    /**
     * @param digits digit array
     * @return      digits + 1
     */
    fun plusOne(digits: IntArray): IntArray {
        for (i in digits.lastIndex downTo 0) {
            if (digits[i] < 9) {
                digits[i]++
                return digits
            }
            digits[i] = 0
        }
        return intArrayOf(1) + digits
    }
}
```

```java
public class PlusOne {
    /**
     * @param digits digit array
     * @return      digits + 1
     */
    public int[] plusOne(int[] digits) {
        for (int i = digits.length - 1; i >= 0; i--) {
            if (digits[i] < 9) {
                digits[i]++;
                return digits;
            }
            digits[i] = 0;
        }

        int[] result = new int[digits.length + 1];
        result[0] = 1;
        return result;
    }
}
```

```cpp
#include <vector>

class PlusOne {
public:
    /**
     * @param digits digit array
     * @return      digits + 1
     */
    std::vector<int> plusOne(std::vector<int>& digits) {
        for (int i = digits.size() - 1; i >= 0; i--) {
            if (digits[i] < 9) {
                digits[i]++;
                return digits;
            }
            digits[i] = 0;
        }

        digits.insert(digits.begin(), 1);
        return digits;
    }
};
```

```python
def plus_one(digits: list[int]) -> list[int]:
    """
    @param digits: digit array
    @return:       digits + 1
    """
    for i in range(len(digits) - 1, -1, -1):
        if digits[i] < 9:
            digits[i] += 1
            return digits
        digits[i] = 0

    return [1] + digits
```

```rust
impl Solution {
    /// @param digits digit array
    /// @return      digits + 1
    pub fn plus_one(mut digits: Vec<i32>) -> Vec<i32> {
        for i in (0..digits.len()).rev() {
            if digits[i] < 9 {
                digits[i] += 1;
                return digits;
            }
            digits[i] = 0;
        }

        digits.insert(0, 1);
        digits
    }
}
```

## Dry run

**Input:** `digits = [9,9,9]`.

```
i=2: 9 -> 0.  i=1: 9 -> 0.  i=0: 9 -> 0.  loop ends -> [1,0,0,0] ✓
Input: [1,2,3]: i=2: 3 -> 4.  return [1,2,4] ✓
```

## Complexity

**Time.** O(n) worst (all 9s), O(1) typical:

$$
T(n) = O(n)
$$

**Space.** O(1) or O(n) for the all-9s case:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Add Two Numbers** ([4.7](../ch04-linked-lists/add-two-numbers.md)) — the digit-carry family.
- **Interview follow-up:** "Why does the loop return early so often?" Only a 9 propagates a carry — a non-9 digit absorbs the increment locally and the carry dies. The all-9s case is the only one needing the extra leading 1.
