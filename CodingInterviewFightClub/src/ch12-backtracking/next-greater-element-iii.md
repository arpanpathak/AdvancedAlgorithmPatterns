# 12.16 Next Greater Element III

> **Source**: [`src/main/kotlin/array/Combinatorics/NextGreaterElement_III.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/Combinatorics/NextGreaterElement_III.kt)
> **Pattern**: next-permutation in digits · **Core page**

## The Problem

The smallest number > `n` using the **same digits** (or -1).

- Constraints: n < 2³¹.

## Examples

```
Input:  n = 12     -> Output: 21
Input:  n = 21     -> Output: -1
Input:  n = 1999999999 -> Output: -1? no: 1999999999's next permutation exceeds int -> -1
```

## Intuition — the [12.5](next-permutation.md) algorithm on the digit array

Find the first descent from the right; swap with the next-larger digit; reverse the suffix:

```kotlin
val digits = n.toString().toCharArray()

var i = digits.size - 2
while (i >= 0 && digits[i] >= digits[i + 1]) i--      // the descent
if (i < 0) return -1                                   // already max permutation

var j = digits.size - 1
while (digits[j] <= digits[i]) j--                     // the next-larger digit
swap(digits, i, j)

digits.reverse(i + 1, digits.size)                     // smallest suffix

val result = digits.concatToString().toLong()
return if (result > Int.MAX_VALUE) -1 else result.toInt()
```

**Why the descent + swap + reverse?** The next permutation's classic three steps ([12.5](next-permutation.md) verbatim) — the suffix after the swap is descending, and reversing it yields the smallest arrangement.

## Approach 1 — Next-permutation on digits (the repo's version, optimal)

```kotlin
class NextGreaterElement_III {
    /**
     * @param n input number
     * @return  next number with the same digits, or -1
     */
    fun nextGreaterElement(n: Int): Int {
        val digits = n.toString().toCharArray()

        var i = digits.size - 2
        while (i >= 0 && digits[i] >= digits[i + 1]) i--
        if (i < 0) return -1

        var j = digits.size - 1
        while (digits[j] <= digits[i]) j--

        val tmp = digits[i]
        digits[i] = digits[j]
        digits[j] = tmp

        digits.reverse(i + 1, digits.size)

        val result = digits.concatToString().toLong()
        return if (result > Int.MAX_VALUE) -1 else result.toInt()
    }
}
```

```java
public class NextGreaterElementIII {
    /**
     * @param n input number
     * @return  next number with the same digits, or -1
     */
    public int nextGreaterElement(int n) {
        char[] digits = String.valueOf(n).toCharArray();

        int i = digits.length - 2;
        while (i >= 0 && digits[i] >= digits[i + 1]) i--;
        if (i < 0) return -1;

        int j = digits.length - 1;
        while (digits[j] <= digits[i]) j--;

        char tmp = digits[i];
        digits[i] = digits[j];
        digits[j] = tmp;

        reverse(digits, i + 1, digits.length - 1);

        long result = Long.parseLong(new String(digits));
        return result > Integer.MAX_VALUE ? -1 : (int) result;
    }

    private void reverse(char[] a, int l, int r) {
        while (l < r) {
            char t = a[l];
            a[l++] = a[r];
            a[r--] = t;
        }
    }
}
```

```cpp
#include <string>
#include <algorithm>
#include <climits>

class NextGreaterElementIII {
public:
    /**
     * @param n input number
     * @return  next number with the same digits, or -1
     */
    int nextGreaterElement(int n) {
        std::string digits = std::to_string(n);

        int i = digits.size() - 2;
        while (i >= 0 && digits[i] >= digits[i + 1]) i--;
        if (i < 0) return -1;

        int j = digits.size() - 1;
        while (digits[j] <= digits[i]) j--;

        std::swap(digits[i], digits[j]);
        std::reverse(digits.begin() + i + 1, digits.end());

        long result = std::stol(digits);
        return result > INT_MAX ? -1 : (int)result;
    }
};
```

```python
def next_greater_element(n: int) -> int:
    """
    @param n: input number
    @return:  next number with the same digits, or -1
    """
    digits = list(str(n))

    i = len(digits) - 2
    while i >= 0 and digits[i] >= digits[i + 1]:
        i -= 1
    if i < 0:
        return -1

    j = len(digits) - 1
    while digits[j] <= digits[i]:
        j -= 1

    digits[i], digits[j] = digits[j], digits[i]
    digits[i + 1:] = reversed(digits[i + 1:])

    result = int("".join(digits))
    return result if result <= 2**31 - 1 else -1
```

```rust
impl Solution {
    /// @param n input number
    /// @return  next number with the same digits, or -1
    pub fn next_greater_element(n: i32) -> i32 {
        let mut digits: Vec<char> = n.to_string().chars().collect();

        let mut i = digits.len() as i32 - 2;
        while i >= 0 && digits[i as usize] >= digits[(i + 1) as usize] { i -= 1; }
        if i < 0 { return -1; }

        let mut j = digits.len() as i32 - 1;
        while digits[j as usize] <= digits[i as usize] { j -= 1; }

        digits.swap(i as usize, j as usize);
        digits[(i + 1) as usize..].reverse();

        let result: i64 = digits.into_iter().collect::<String>().parse().unwrap();
        if result > i32::MAX as i64 { -1 } else { result as i32 }
    }
}
```

## Dry run

**Input:** `n = 12`.

```
digits [1,2].  i: 1 >= 2? no -> i=0.  j: 2 <= 1? no -> j=1.
swap(0,1) -> [2,1].  reverse(1..) -> [2,1].  result 21 ✓

Input: n = 21: i: 2 >= 1 -> i=0.  1 >= 2? no -> i=-1 -> return -1 ✓
Input: n = 1999999999: next is 9199999999 > Int.MAX -> -1 ✓
```

## Complexity

**Time.** Digit scan + reverse:

$$
T = O(\log n)
$$

**Space.** The digit array:

$$
S = O(\log n)
$$

## Variants & follow-ups

- **Next Permutation** ([12.5](next-permutation.md)) — the exact algorithm this page reuses.
- **Interview follow-up:** "Why does the int overflow check come after computing?" The next permutation may exceed 2³¹−1 (e.g. 1999999999 → 9199999999) — computing in Long and clamping is the honest overflow test.
