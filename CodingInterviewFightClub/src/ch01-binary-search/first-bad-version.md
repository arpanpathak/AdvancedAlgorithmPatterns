# 1.8 First Bad Version

> **Source:** [`src/main/kotlin/binarysearch/FirstBadVersion.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/FirstBadVersion.kt)
> **Pattern:** lower bound via an API predicate · **Core page**

## The Problem

You are a product manager leading a team developing a product. The latest version `n` has a bug; a previous version may have introduced it. Once a version is bad, **all subsequent versions are bad**:

```
versions:  1  2  3  4  5  6  7
quality:   G  G  B  B  B  B  B
                  ^
             first bad = 3
```

You have an API `isBadVersion(version)` that returns `true` iff that version is bad. Find the **first bad version**, minimizing the number of API calls.

- Constraints: $1 \le n \le 2^{31} - 1$ — a linear scan of a billion versions is disqualifying; the API-call budget must be logarithmic.

## Intuition

The "badness" predicate $P(v)$ = `isBadVersion(v)` is **monotone by definition**: good, good, ..., good, bad, bad, ..., bad — exactly one flip. This is the textbook instance of the [Binary Search Theorem](pattern-primer.md): find the **first true** in $[1, n]$. Template A from 1.0, verbatim, with the array access replaced by the API call.

This problem is the *canonical* "binary search on a predicate" — no array exists at all. If you can solve this, you can solve any "first true" problem (Koko, Capacity, House Robber IV all reduce to it with a fancier predicate).

## Approach 1 — Linear scan

Call `isBadVersion(i)` for `i = 1..n`, return the first `true`. $O(n)$ API calls — 2 billion in the worst case. The problem exists to teach you not to do this.

## Approach 2 — Binary search on the predicate (optimal)

```kotlin
/**
 * @param n the total number of versions, numbered 1..n
 * @return  the first version that is bad (isBadVersion returns true)
 */
override fun firstBadVersion(n: Int): Int {
    var start = 1
    var end = n

    while (start < end) {
        val mid = start + (end - start) / 2
        when {
            isBadVersion(mid) -> end = mid        // mid is bad -> first bad is at or before mid
            else              -> start = mid + 1  // mid is good -> first bad is strictly after mid
        }
    }
    return start
}
```

```java
public class FirstBadVersion {
    /* Stub of the LeetCode API. In the real problem this is provided. */
    private boolean isBadVersion(int version) { return version >= 3; }

    /**
     * @param n the total number of versions, numbered 1..n
     * @return  the first version that is bad (isBadVersion returns true)
     */
    public int firstBadVersion(int n) {
        int start = 1, end = n;
        while (start < end) {
            int mid = start + (end - start) / 2;   // overflow-safe midpoint
            if (isBadVersion(mid)) {
                end = mid;                        // mid is bad -> first bad is at or before mid
            } else {
                start = mid + 1;                  // mid is good -> first bad is strictly after mid
            }
        }
        return start;
    }
}
```

```cpp
#include <cstdint>

class FirstBadVersion {
    /* Stub of the LeetCode API. In the real problem this is provided. */
    bool isBadVersion(int version) { return version >= 3; }

public:
    /**
     * @param n the total number of versions, numbered 1..n
     * @return  the first version that is bad (isBadVersion returns true)
     */
    int firstBadVersion(int n) {
        int start = 1, end = n;
        while (start < end) {
            int mid = start + (end - start) / 2;
            if (isBadVersion(mid)) {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        return start;
    }
};
```

```python
def first_bad_version(n: int, is_bad: callable) -> int:
    """
    @param n:      the total number of versions, numbered 1..n
    @param is_bad: the oracle API; is_bad(v) is True iff v is bad
    @return:       the first version that is bad
    """
    start, end = 1, n
    while start < end:
        mid = start + (end - start) // 2
        if is_bad(mid):
            end = mid            # mid is bad -> first bad is at or before mid
        else:
            start = mid + 1      # mid is good -> first bad is strictly after mid
    return start
```

```rust
impl Solution {
    /* Stub of the LeetCode API. In the real problem this is provided. */
    fn is_bad_version(version: i32) -> bool { version >= 3 }

    /// @param n the total number of versions, numbered 1..n
    /// @return  the first version that is bad (isBadVersion returns true)
    pub fn first_bad_version(n: i32) -> i32 {
        let (mut start, mut end) = (1i64, n as i64);
        while start < end {
            let mid = start + (end - start) / 2;
            if Self::is_bad_version(mid as i32) {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        start as i32
    }
}
```

> **Rust note:** `start + (end - start) / 2` overflows `i32` for $n = 2^{31}-1$ (mid can exceed `i32::MAX/2` only via the sum — actually `start + (end-start)/2` stays within $[start, end] \subseteq [1, 2^{31}-1]$, which fits — but the *explicit* `(start + end)` form would not). The i64 widening shown here is belt-and-suspenders and keeps the code obviously correct. The **other languages must use the same overflow-safe form**: `left + (right - left) / 2`, never `(left + right) / 2`.

## Dry run

**Input:** `n = 7`, bad versions start at 3 (`isBadVersion(v) = v >= 3`)

```
start=1  end=7  mid=4  isBadVersion(4)=true  -> end=4
start=1  end=4  mid=2  isBadVersion(2)=false -> start=3
start=3  end=4  mid=3  isBadVersion(3)=true  -> end=3
start=3  end=3  -> return 3 ✓
```

**Input:** `n = 5`, everything good (`isBadVersion(v) = false` always)

```
start=1  end=5  mid=3  false -> start=4
start=4  end=5  mid=4  false -> start=5
start=5  end=5  -> return 5
```

The algorithm returns `n` when no version is bad — a sensible "no bug" answer, and exactly what the invariant guarantees (the first true is at the right edge).

## Complexity

**Time.** Each API call halves the range:

$$
T(n) = O(\log n) \text{ API calls}
$$

**Space.** $O(1)$.

## Variants & follow-ups

- **Guess Number Higher Or Lower** ([1.9](guess-number-higher-or-lower.md)) — same halving, but the oracle returns *three* answers (-1/0/1) instead of a boolean, so the loop is the exact-match Template B.
- **Kth Missing Positive Number** ([1.11](kth-missing-positive-number.md)) — "first true" on a *derived* predicate (missing-count ≥ k).
- **Interview follow-up:** "The API is flaky and occasionally lies." Now binary search can converge to the wrong boundary; you'd sample each version multiple times (majority vote) and pay a constant-factor blowup — a nice robustness discussion that shows systems thinking.
- **Interview follow-up:** "What's the minimum number of calls for n = 2^31 − 1?" Exactly $\lceil \log_2(2^{31}-1) \rceil = 31$ calls. Say it with the halving identity: $2^{30} < 2^{31}-1 \le 2^{31}$.
