# 1.19 Sqrt(x)

> **Source:** [`src/main/kotlin/math/Sqrt.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/Sqrt.kt)
> **Pattern:** binary search on the answer · **Core page**

## The Problem

`mySqrt(x)` — the integer square root: the largest `r` with `r² ≤ x`, without floating point.

- Constraints: $0 \le x \le 2^{31} - 1$.

## Examples

```
Input:  x = 4   -> Output: 2
Input:  x = 8   -> Output: 2   (2² = 4 ≤ 8 < 3² = 9)
```

## Intuition — binary search the answer in [1, x]

The answer is a *number*, monotone in `r² ≤ x` — the [1.0](pattern-primer.md) "binary search over the answer" shape: search `r` in `[1, x]`, comparing `r²` to `x`:

```kotlin
var (left, right) = 1 to x
while (left <= right) {
    val mid = left + (right - left) / 2
    val square = mid.toLong() * mid        // Long: mid² can overflow Int

    when {
        square < x -> left = mid + 1
        square > x -> right = mid - 1
        else -> return mid
    }
}
return right                               // largest r with r² <= x
```

**Why `toLong()`?** `46341² > Int.MAX_VALUE` — squaring `mid` in `Int` overflows and corrupts the comparison. The `Long` cast is the [1.x](../ch01-binary-search/pattern-primer.md) overflow hygiene.

**Why return `right`?** The loop exits with `left > right` — `right` is the last candidate whose square was ≤ x (the exact match returns early). The `[1, x]` range with the `<`/`>` branches pins `right` at the floor.

## Approach 1 — Linear scan (O(√x))

Square every integer up to the answer: correct, slow.

## Approach 2 — Binary search on the answer (the repo's version, optimal)

```kotlin
class Sqrt {
    /**
     * @param x non-negative integer
     * @return  integer square root (floor)
     */
    fun mySqrt(x: Int): Int {
        if (x < 2) return x

        var (left, right) = 1 to x
        while (left <= right) {
            val mid = left + (right - left) / 2
            val square = mid.toLong() * mid

            when {
                square < x -> left = mid + 1
                square > x -> right = mid - 1
                else -> return mid
            }
        }
        return right
    }
}
```

```java
public class Sqrt {
    /**
     * @param x non-negative integer
     * @return  integer square root (floor)
     */
    public int mySqrt(int x) {
        if (x < 2) return x;

        int left = 1, right = x;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            long square = (long) mid * mid;      // Long: mid² can overflow

            if (square < x) left = mid + 1;
            else if (square > x) right = mid - 1;
            else return mid;
        }
        return right;
    }
}
```

```cpp
class Sqrt {
public:
    /**
     * @param x non-negative integer
     * @return  integer square root (floor)
     */
    int mySqrt(int x) {
        if (x < 2) return x;

        int left = 1, right = x;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            long long square = (long long) mid * mid;   // Long: mid² can overflow

            if (square < x) left = mid + 1;
            else if (square > x) right = mid - 1;
            else return mid;
        }
        return right;
    }
};
```

```python
def my_sqrt(x: int) -> int:
    """
    @param x: non-negative integer
    @return:  integer square root (floor)
    """
    if x < 2:
        return x

    left, right = 1, x
    while left <= right:
        mid = left + (right - left) // 2
        square = mid * mid

        if square < x:
            left = mid + 1
        elif square > x:
            right = mid - 1
        else:
            return mid
    return right
```

```rust
impl Solution {
    /// @param x non-negative integer
    /// @return  integer square root (floor)
    pub fn my_sqrt(x: i32) -> i32 {
        if x < 2 { return x; }

        let mut left = 1i64;
        let mut right = x as i64;
        while left <= right {
            let mid = left + (right - left) / 2;
            let square = mid * mid;          // i64: mid² can overflow i32

            if square < x as i64 { left = mid + 1; }
            else if square > x as i64 { right = mid - 1; }
            else { return mid as i32; }
        }
        right as i32
    }
}
```

## Dry run

**Input:** `x = 8`.

```
left=1, right=8
mid=4: 16 > 8 -> right=3
mid=2: 4 < 8 -> left=3
mid=3: 9 > 8 -> right=2
left=3 > right=2 -> exit.  return right = 2 ✓
```

The comparison walks the invariant: `left` stays ≤ √x (square too small), `right` stays ≥ √x (square too big). At exit they cross with `right` as the last "small enough" candidate — exactly the floor. `x = 9`: mid=5 (25>9 → r=4), mid=2 (4<9 → l=3), mid=3 (9==9 → return 3) ✓.

## Complexity

**Time.** Halving the search range:

$$
T(x) = O(\log x)
$$

**Space.** Scalars:

$$
S(x) = O(1)
$$

## Variants & follow-ups

- **Find Peak Element** ([1.6](find-peak-element.md)) — the same search-over-values family.
- **Split Array Largest Sum** ([1.20](split-array-largest-sum.md)) — "binary search the answer" on a different monotone predicate.
- **Interview follow-up:** "Why `mid.toLong() * mid` and not `mid * mid`?" `46341² = 2147488281 > Int.MAX_VALUE` — the product silently wraps in `Int`. The Long cast is the [1.x](../ch01-binary-search/pattern-primer.md) overflow lesson: the check happens *before* the arithmetic, not after.
