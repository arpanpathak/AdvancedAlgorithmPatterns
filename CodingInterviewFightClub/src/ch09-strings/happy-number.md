# 9.26 Happy Number

> **Source**: [`src/main/kotlin/math/HappyNumber.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/HappyNumber.kt)
> **Pattern**: digit-square cycle detection · **Core page**

## The Problem

Repeatedly replace n with the sum of its digits' squares — does it reach 1?

- Constraints: 32-bit.

## Examples

```
Input:  n = 19   -> Output: true   (19→82→68→100→1)
Input:  n = 2    -> Output: false
```

## Intuition — the sequence always cycles; a set detects it

```kotlin
fun next(n: Int): Int {
    var totalSum = 0
    var num = n

    while (num > 0) {
        val digit = num % 10
        num /= 10
        totalSum += digit * digit
    }
    return totalSum
}
```

The [4.2](../ch04-linked-lists/linked-list-cycle.md) Floyd machinery works too — the value map is an implicit graph.

## Approach 1 — Set-based detection (the repo's version, optimal)

```kotlin
class HappyNumber {
    /**
     * @param n input number
     * @return  true iff happy
     */
    fun isHappy(n: Int): Boolean {
        val seen = mutableSetOf<Int>()
        var num = n

        while (num != 1 && num !in seen) {
            seen.add(num)
            num = next(num)
        }
        return num == 1
    }

    private fun next(n: Int): Int {
        var totalSum = 0
        var num = n

        while (num > 0) {
            val digit = num % 10
            num /= 10
            totalSum += digit * digit
        }
        return totalSum
    }
}
```

```java
import java.util.*;

public class HappyNumber {
    private int next(int n) {
        int sum = 0;
        while (n > 0) {
            int d = n % 10;
            n /= 10;
            sum += d * d;
        }
        return sum;
    }

    /**
     * @param n input number
     * @return  true iff happy
     */
    public boolean isHappy(int n) {
        Set<Integer> seen = new HashSet<>();

        while (n != 1 && seen.add(n)) {
            n = next(n);
        }
        return n == 1;
    }
}
```

```cpp
#include <unordered_set>

class HappyNumber {
    int next(int n) {
        int sum = 0;
        while (n > 0) {
            int d = n % 10;
            n /= 10;
            sum += d * d;
        }
        return sum;
    }

public:
    /**
     * @param n input number
     * @return  true iff happy
     */
    bool isHappy(int n) {
        std::unordered_set<int> seen;

        while (n != 1 && !seen.count(n)) {
            seen.insert(n);
            n = next(n);
        }
        return n == 1;
    }
};
```

```python
def is_happy(n: int) -> bool:
    """
    @param n: input number
    @return:  true iff happy
    """
    def next_num(num: int) -> int:
        return sum(int(d) ** 2 for d in str(num))

    seen = set()
    while n != 1 and n not in seen:
        seen.add(n)
        n = next_num(n)

    return n == 1
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param n input number
    /// @return  true iff happy
    pub fn is_happy(n: i32) -> bool {
        fn next(mut num: i32) -> i32 {
            let mut sum = 0;
            while num > 0 {
                let d = num % 10;
                num /= 10;
                sum += d * d;
            }
            sum
        }

        let mut seen = HashSet::new();
        let mut num = n;

        while num != 1 && seen.insert(num) {
            num = next(num);
        }
        num == 1
    }
}
```

## Dry run

**Input:** `n = 19`.

```
19 -> 82 -> 68 -> 100 -> 1 -> true ✓
Input: 2: 2 -> 4 -> 16 -> 37 -> 58 -> 89 -> 145 -> 42 -> 20 -> 4 (cycle) -> false ✓
```

## Complexity

**Time.** Cycle length bounded:

$$
T = O(\log n)
$$

**Space.** The set:

$$
S = O(\log n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does Floyd's slow/fast also work?" The digit-square map is a functional graph — every orbit is a lollipop; the two-pointer detects the cycle without the set.
