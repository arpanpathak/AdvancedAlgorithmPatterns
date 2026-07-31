# 1.14 Random Pick With Weight

> **Source:** [`src/main/kotlin/binarysearch/RandomPickWithWeight.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/binarysearch/RandomPickWithWeight.kt)
> **Pattern:** prefix sums + binary search · **Gym page**

## The Problem

Design a structure initialized with an array of positive weights `w`. `pickIndex()` must return a random index `i` with probability proportional to `w[i]`:

$$
P(i) = \frac{w[i]}{\sum_j w[j]}
$$

- Constraints: $1 \le w.length \le 10^4$, $1 \le w[i] \le 10^5$, many calls to `pickIndex`.

## Examples

```
w = [1, 3]
P(0) = 1/4, P(1) = 3/4.  pickIndex() should return 1 about 75% of the time.
```

## Intuition — the "roulette wheel" made of line segments

Draw each weight as a segment on a number line: weight 1 occupies `[0, 1)`, weight 3 occupies `[1, 4)`. Pick a uniform random point in `[0, total)`. The segment that contains the point is your answer:

```
w = [1, 3]     total = 4
| 0 |   1    |   2    |   3    |
|seg0|      seg1 (length 3)      |
    ^ random point -> index 1
```

The **prefix sums** `[1, 4]` record the *right ends* of the segments. Finding which segment contains `r` = "find the first prefix sum `> r`" — and because prefix sums are **sorted**, that's a binary search. 

Careful with the boundary convention: `Random.nextInt(total)` returns $r \in [0, total)$ (exclusive of total). With prefix `P = [1, 4]`, the condition `P[mid] > r` (strictly greater) maps:
- $r \in [0, 1)$ → first prefix > r is `1` → index 0 (segment 0, length 1) ✓
- $r \in [1, 4)$ → first prefix > r is `4` → index 1 (segment 1, length 3) ✓

This is **Template A** ([1.0](pattern-primer.md)) — "first true" for the predicate `prefix[mid] > r`.

## Approach — prefix sums + binary search

```kotlin
/**
 * @param w the positive weights; index i must be picked with probability w[i] / sum(w)
 */
class RandomPickWithWeight(w: IntArray) {
    private val prefixSum = IntArray(w.size) { 0 }
    private val totalSum: Int

    init {
        for (i in w.indices) {
            prefixSum[i] = if (i > 0) prefixSum[i - 1] + w[i] else w[i]
        }
        totalSum = prefixSum.last()
    }

    /**
     * @return a random index i with probability proportional to w[i]
     */
    fun pickIndex(): Int {
        // Uniform point in [0, totalSum). nextInt(exclusive) is exactly this.
        val randomPick = Random.nextInt(totalSum)
        var (start, end) = 0 to w.size

        while (start < end) {
            val mid = start + (end - start) / 2
            when {
                prefixSum[mid] > randomPick -> end = mid    // segment containing r is at or left
                else                        -> start = mid + 1
            }
        }
        return start
    }
}
```

```java
import java.util.concurrent.ThreadLocalRandom;

public class RandomPickWithWeight {
    private final int[] prefixSum;
    private final int totalSum;

    /**
     * @param w the positive weights; index i must be picked with probability w[i] / sum(w)
     */
    public RandomPickWithWeight(int[] w) {
        prefixSum = new int[w.length];
        for (int i = 0; i < w.length; i++) {
            prefixSum[i] = (i > 0 ? prefixSum[i - 1] : 0) + w[i];
        }
        totalSum = prefixSum[prefixSum.length - 1];
    }

    /**
     * @return a random index i with probability proportional to w[i]
     */
    public int pickIndex() {
        int r = ThreadLocalRandom.current().nextInt(totalSum);   // [0, totalSum)
        int start = 0, end = prefixSum.length;
        while (start < end) {
            int mid = start + (end - start) / 2;
            if (prefixSum[mid] > r) end = mid;
            else start = mid + 1;
        }
        return start;
    }
}
```

```cpp
#include <vector>
#include <random>

class RandomPickWithWeight {
    std::vector<int> prefixSum;
    int totalSum;
    std::mt19937 gen{std::random_device{}()};

public:
    /**
     * @param w the positive weights; index i must be picked with probability w[i] / sum(w)
     */
    RandomPickWithWeight(const std::vector<int>& w) {
        prefixSum.resize(w.size());
        int acc = 0;
        for (int i = 0; i < (int)w.size(); i++) {
            acc += w[i];
            prefixSum[i] = acc;
        }
        totalSum = acc;
    }

    /**
     * @return a random index i with probability proportional to w[i]
     */
    int pickIndex() {
        std::uniform_int_distribution<int> dist(0, totalSum - 1);
        int r = dist(gen);                       // [0, totalSum)
        int start = 0, end = (int)prefixSum.size();
        while (start < end) {
            int mid = start + (end - start) / 2;
            if (prefixSum[mid] > r) end = mid;
            else start = mid + 1;
        }
        return start;
    }
};
```

```python
import random

class RandomPickWithWeight:
    """
    @param w: the positive weights; index i must be picked with probability w[i] / sum(w)
    """
    def __init__(self, w: list[int]) -> None:
        self.prefix_sum: list[int] = []
        acc = 0
        for weight in w:
            acc += weight
            self.prefix_sum.append(acc)
        self.total = acc

    """
    @return: a random index i with probability proportional to w[i]
    """
    def pick_index(self) -> int:
        r = random.randrange(self.total)          # [0, total)
        start, end = 0, len(self.prefix_sum)
        while start < end:
            mid = start + (end - start) // 2
            if self.prefix_sum[mid] > r:
                end = mid
            else:
                start = mid + 1
        return start
```

```rust
use rand::Rng;

struct Solution {
    prefix_sum: Vec<i32>,
    total: i32,
}

impl Solution {
    /// @param w the positive weights; index i must be picked with probability w[i] / sum(w)
    fn new(w: Vec<i32>) -> Self {
        let mut prefix_sum = Vec::with_capacity(w.len());
        let mut acc = 0;
        for weight in w {
            acc += weight;
            prefix_sum.push(acc);
        }
        let total = acc;
        Self { prefix_sum, total }
    }

    /// @return a random index i with probability proportional to w[i]
    fn pick_index(&self) -> i32 {
        let r = rand::thread_rng().gen_range(0..self.total);   // [0, total)
        let (mut start, mut end) = (0usize, self.prefix_sum.len());
        while start < end {
            let mid = start + (end - start) / 2;
            if self.prefix_sum[mid] > r {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        start as i32
    }
}
```

> **Rust note:** requires the `rand` crate. The code above uses `gen_range(0..self.total)` which matches `nextInt(total)`'s exclusive-upper-bound semantics exactly.

## Dry run

**Input:** `w = [1, 3]` → `prefixSum = [1, 4]`, `total = 4`.

Say the RNG draws `r = 2`:

```
start=0  end=2  mid=1  prefix[1]=4 > 2 -> end=1
start=0  end=1  mid=0  prefix[0]=1 > 2? NO -> start=1
start=1  end=1  -> return 1 ✓   (segment 1, which spans [1, 4))
```

Now enumerate **all** four possible draws to see the distribution:

| r | prefix[0]=1 > r? | first prefix > r | returned index |
|---|---|---|---|
| 0 | yes | 1 | 0 |
| 1 | no | 4 | 1 |
| 2 | no | 4 | 1 |
| 3 | no | 4 | 1 |

Index 0 is returned for exactly 1 of 4 draws; index 1 for 3 of 4 — **exactly the weights**. The roulette wheel is exact.

## Complexity

**Construction:** one pass to build prefix sums — $O(n)$ time, $O(n)$ space.

**pickIndex:** one halving search over $n$ prefix entries:

$$
T_{\text{pick}} = O(\log n), \qquad S = O(n)
$$

## Variants & follow-ups

- **Random Pick Index** (streaming version) — reservoir sampling, when the array is too big to prefix-sum; see `src/main/kotlin/probability/`.
- **Interview follow-up:** "Why `nextInt(total)` and not `nextInt(total + 1)`?" The exclusive bound keeps `r` inside `[0, total)`, matching the segment layout `[prefix[i-1], prefix[i])`; an inclusive bound would need an extra case for `r == total`.
- **Interview follow-up:** "What if weights are huge (sum overflows Int)?" Use `Long` prefix sums — the binary search is unchanged; the overflow is the only thing that breaks, and it breaks at construction, not at pick time.
