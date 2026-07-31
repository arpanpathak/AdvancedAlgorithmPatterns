# 1.9 Guess Number Higher Or Lower

> **Source:** [`src/main/kotlin/binarysearch/GuessNumberHigherOrLower.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/GuessNumberHigherOrLower.kt)
> **Pattern:** exact-match ternary-response search · **Core page**

## The Problem

I pick a secret number in `[1, n]`. You call an API:

```
guess(num):
  -1  ->  num is HIGHER than the secret (you guessed too big)
   1  ->  num is LOWER  than the secret (you guessed too small)
   0  ->  num == secret
```

Find the secret with the fewest calls.

- Constraints: $1 \le n \le 2^{31} - 1$.

## Intuition

This is binary search *before it was binary search* — literally the original "guess a number" game, which is exactly how the technique was described to Knuth's generation. The oracle's three answers partition the candidate space into "too high" / "too low" / "hit", and each non-hit answer **halves** the space:

- `guess(mid) < 0` → secret < mid → `end = mid - 1`
- `guess(mid) > 0` → secret > mid → `start = mid + 1`
- `guess(mid) == 0` → done.

This is **Template B** ([1.0](pattern-primer.md)) — exact match, inclusive bounds, three-way comparison — with the array comparison replaced by the oracle. The difference from [1.8](first-bad-version.md) is that the oracle answers *direction*, not a boolean, so on a hit we return immediately instead of continuing to a boundary.

## Approach 1 — Linear guessing

Guess 1, 2, 3, ... until the oracle says 0. Up to $2^{31}-1$ calls. The problem's whole point is that the oracle's *direction* feedback lets you discard half the space per call.

## Approach 2 — Binary search with the oracle (optimal)

```kotlin
/**
 * @param n the upper bound of the secret range [1, n]
 * @return  the secret number
 */
override fun guessNumber(n: Int): Int {
    var start = 1
    var end = n

    while (start <= end) {
        val mid = start + (end - start) / 2   // overflow-safe midpoint
        val distance = guess(mid)             // -1 too big, 1 too small, 0 hit

        when {
            distance == 0 -> return mid
            distance < 0  -> end = mid - 1    // secret is smaller
            else          -> start = mid + 1  // secret is larger
        }
    }
    return -1   // unreachable for a valid game
}
```

```java
public class GuessNumberHigherOrLower {
    /* Stub of the LeetCode API: returns -1/0/1 as documented. */
    private int guess(int num) { return Integer.compare(6, num); } // secret = 6

    /**
     * @param n the upper bound of the secret range [1, n]
     * @return  the secret number
     */
    public int guessNumber(int n) {
        int start = 1, end = n;
        while (start <= end) {
            int mid = start + (end - start) / 2;
            int distance = guess(mid);
            if (distance == 0) return mid;
            if (distance < 0) end = mid - 1;     // guessed too big
            else start = mid + 1;                // guessed too small
        }
        return -1;
    }
}
```

```cpp
class GuessNumberHigherOrLower {
    /* Stub of the LeetCode API. */
    int guess(int num) { return (6 < num) - (6 > num); }  // secret = 6

public:
    /**
     * @param n the upper bound of the secret range [1, n]
     * @return  the secret number
     */
    int guessNumber(int n) {
        int start = 1, end = n;
        while (start <= end) {
            int mid = start + (end - start) / 2;
            int distance = guess(mid);
            if (distance == 0) return mid;
            if (distance < 0) end = mid - 1;
            else start = mid + 1;
        }
        return -1;
    }
};
```

```python
def guess_number(n: int, guess: callable) -> int:
    """
    @param n:     the upper bound of the secret range [1, n]
    @param guess: the oracle; guess(num) -> -1 (too big), 1 (too small), 0 (hit)
    @return:      the secret number
    """
    start, end = 1, n
    while start <= end:
        mid = start + (end - start) // 2
        distance = guess(mid)
        if distance == 0:
            return mid
        if distance < 0:
            end = mid - 1        # guessed too big
        else:
            start = mid + 1      # guessed too small
    return -1
```

```rust
impl Solution {
    /* Stub of the LeetCode API: secret = 6. */
    fn guess(num: i32) -> i32 { (6 < num) as i32 - (6 > num) as i32 }

    /// @param n the upper bound of the secret range [1, n]
    /// @return  the secret number
    pub fn guess_number(n: i32) -> i32 {
        let (mut start, mut end) = (1i64, n as i64);
        while start <= end {
            let mid = start + (end - start) / 2;
            let distance = Self::guess(mid as i32);
            if distance == 0 {
                return mid as i32;
            }
            if distance < 0 {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }
        -1
    }
}
```

## Dry run

**Input:** `n = 10`, secret = 6.

```
start=1  end=10  mid=5  guess(5)=1  (too small) -> start=6
start=6  end=10  mid=8  guess(8)=-1 (too big)   -> end=7
start=6  end=7   mid=6  guess(6)=0  (hit)       -> return 6 ✓
```

Three calls to find a secret among 10 candidates. With $n = 10^9$, the bound is $\lceil \log_2 10^9 \rceil = 30$ calls — the difference between "guess a billion numbers" and "guess thirty".

## Complexity

**Time.** Each non-hit call halves the range:

$$
T(n) = O(\log n) \text{ oracle calls}
$$

**Space.** $O(1)$.

## Variants & follow-ups

- **First Bad Version** ([1.8](first-bad-version.md)) — boolean oracle; you search a *boundary* rather than an exact value.
- **Find Peak Element** ([1.6](find-peak-element.md)) — the "oracle" is the local slope comparison, and the answer is a position, not a value.
- **Interview follow-up:** "The secret is a *floating-point* number; find it to within $\varepsilon$." Same loop with `mid = (start + end) / 2.0` and a `while (end - start > eps)` condition — complexity becomes $O(\log((R-L)/\varepsilon))$ iterations. The halving math is identical, just with a different "range size."
- **Interview follow-up:** "What's the worst-case number of calls for $n = 2^{31}-1$?" Exactly $\lceil \log_2 2^{31} \rceil = 31$. State it with the identity $2^{31} = 2^{30} \cdot 2$ — the halving argument from [Reference §2](../reference/complexity.md).
