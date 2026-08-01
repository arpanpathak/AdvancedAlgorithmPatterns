# 11.29 Maximum Swap

> **Source**: [`src/main/kotlin/array/greedy/MaximumSwap.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/greedy/MaximumSwap.kt)
> **Pattern**: last-position max swap · **Core page**

## The Problem

The largest number after **one** swap of two digits.

- Constraints: n < 10⁸.

## Examples

```
Input:  num = 2736   -> Output: 7236   (swap 2 and 7)
Input:  num = 9973   -> Output: 9973
```

## Intuition — swap the leftmost digit with the largest digit to its right

Track each digit's **last** occurrence; scan left to right for the first digit smaller than some digit after it:

```kotlin
fun maximumSwap(num: Int): Int {
    val digits = num.toString().toCharArray()
    val last = IntArray(10)

    for (i in digits.indices) last[digits[i] - '0'] = i

    for (i in digits.indices) {
        for (d in 9 downTo digits[i] - '0' + 1) {
            if (last[d] > i) {
                swap(digits, i, last[d])
                return digits.concatToString().toInt()
            }
        }
    }
    return num
}
```

## Approach 1 — Last-index greedy (the repo's version, optimal)

```kotlin
class MaximumSwap {
    /**
     * @param num input number
     * @return    max after one swap
     */
    fun maximumSwap(num: Int): Int {
        val digits = num.toString().toCharArray()
        val last = IntArray(10)

        for (i in digits.indices) last[digits[i] - '0'] = i

        for (i in digits.indices) {
            for (d in 9 downTo digits[i] - '0' + 1) {
                if (last[d] > i) {
                    val tmp = digits[i]
                    digits[i] = digits[last[d]]
                    digits[last[d]] = tmp

                    return digits.concatToString().toInt()
                }
            }
        }
        return num
    }
}
```

```java
public class MaximumSwap {
    /**
     * @param num input number
     * @return    max after one swap
     */
    public int maximumSwap(int num) {
        char[] digits = String.valueOf(num).toCharArray();
        int[] last = new int[10];

        for (int i = 0; i < digits.length; i++) last[digits[i] - '0'] = i;

        for (int i = 0; i < digits.length; i++) {
            for (int d = 9; d > digits[i] - '0'; d--) {
                if (last[d] > i) {
                    char tmp = digits[i];
                    digits[i] = digits[last[d]];
                    digits[last[d]] = tmp;
                    return Integer.parseInt(new String(digits));
                }
            }
        }
        return num;
    }
}
```

```cpp
#include <string>
#include <vector>

class MaximumSwap {
public:
    /**
     * @param num input number
     * @return    max after one swap
     */
    int maximumSwap(int num) {
        std::string digits = std::to_string(num);
        std::vector<int> last(10, -1);

        for (int i = 0; i < (int)digits.size(); i++) last[digits[i] - '0'] = i;

        for (int i = 0; i < (int)digits.size(); i++) {
            for (int d = 9; d > digits[i] - '0'; d--) {
                if (last[d] > i) {
                    std::swap(digits[i], digits[last[d]]);
                    return std::stoi(digits);
                }
            }
        }
        return num;
    }
};
```

```python
def maximum_swap(num: int) -> int:
    """
    @param num: input number
    @return:    max after one swap
    """
    digits = list(str(num))
    last = {int(d): i for i, d in enumerate(digits)}

    for i, d in enumerate(digits):
        for candidate in range(9, int(d), -1):
            if last.get(candidate, -1) > i:
                j = last[candidate]
                digits[i], digits[j] = digits[j], digits[i]
                return int("".join(digits))

    return num
```

```rust
impl Solution {
    /// @param num input number
    /// @return    max after one swap
    pub fn maximum_swap(num: i32) -> i32 {
        let mut digits: Vec<char> = num.to_string().chars().collect();
        let mut last = vec![0i32; 10];

        for (i, &d) in digits.iter().enumerate() {
            last[d as usize - '0' as usize] = i as i32;
        }

        for i in 0..digits.len() {
            for d in (digits[i] as usize - '0' as usize + 1)..=9 {
                if last[d] > i as i32 {
                    let j = last[d] as usize;
                    digits.swap(i, j);
                    return digits.iter().collect::<String>().parse().unwrap();
                }
            }
        }
        num
    }
}
```

## Dry run

**Input:** `num = 2736`.

```
last: 2→0, 7→1, 3→2, 6→3.
i=0 ('2'): d=9..3: last[7]=1 > 0 -> swap digits[0] with digits[1] -> "7236" ✓
```

## Complexity

**Time.** Digits × 10:

$$
T = O(d)
$$

**Space.** Arrays:

$$
S = O(d)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why the last occurrence?" Swapping with the *rightmost* occurrence of the best digit maximizes the improvement — identical digits to the left would be worse swaps.
