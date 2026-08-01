# 18.24 Random Pick With Weight

> **Source**: [`src/main/kotlin/binarysearch/RandomPickWithWeight.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/RandomPickWithWeight.kt)
> **Pattern**: prefix sums + binary search · **Core page**

## The Problem

`pickIndex()` returns an index with probability proportional to `w[i]`.

- Constraints: n ≤ 5×10⁴; ≤ 10⁴ calls.

## Examples

```
["Solution","pickIndex","pickIndex","pickIndex"]
[[[1,3]],[],[],[]]
-> [null,1 with prob 3/4,1 with prob 3/4,0 with prob 1/4]
```

## Intuition — lay the weights on a number line; random r lands on an index

`prefixSum[i]` = cumulative weight; a uniform `r` in `[1, total]` maps via binary search to the first prefix ≥ r:

```kotlin
class RandomPickWithWeight(w: IntArray) {
    private val prefixSum = IntArray(w.size) { 0 }
    private val totalSum: Int

    init {
        for (i in w.indices) {
            prefixSum[i] = if (i > 0) prefixSum[i - 1] + w[i] else w[i]
        }
        totalSum = prefixSum.last()
    }

    fun pickIndex(): Int {
        val r = Random.nextInt(totalSum) + 1     // 1..total
        var left = 0
        var right = prefixSum.lastIndex

        while (left < right) {                   // lower bound
            val mid = left + (right - left) / 2
            if (prefixSum[mid] < r) left = mid + 1
            else right = mid
        }
        return left
    }
}
```

**Why the prefix + bisect?** Index i's "segment" is `(prefix[i-1], prefix[i]]` — length w[i]. The [1.0](../ch01-binary-search/pattern-primer.md) lower-bound search finds the segment containing a uniform draw, making the probability proportional to the segment length.

## Approach 1 — Prefix sums + lower bound (the repo's version, optimal)

```kotlin
import java.util.*

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
     * @return an index proportional to its weight
     */
    fun pickIndex(): Int {
        val r = Random.nextInt(totalSum) + 1

        var left = 0
        var right = prefixSum.lastIndex

        while (left < right) {
            val mid = left + (right - left) / 2
            if (prefixSum[mid] < r) left = mid + 1
            else right = mid
        }
        return left
    }
}
```

```java
import java.util.*;

public class RandomPickWithWeight {
    private final int[] prefix;
    private final Random random = new Random();

    public RandomPickWithWeight(int[] w) {
        prefix = new int[w.length];
        prefix[0] = w[0];
        for (int i = 1; i < w.length; i++) prefix[i] = prefix[i - 1] + w[i];
    }

    /**
     * @return an index proportional to its weight
     */
    public int pickIndex() {
        int r = random.nextInt(prefix[prefix.length - 1]) + 1;

        int left = 0, right = prefix.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (prefix[mid] < r) left = mid + 1;
            else right = mid;
        }
        return left;
    }
}
```

```cpp
#include <vector>
#include <cstdlib>

class RandomPickWithWeight {
    std::vector<int> prefix;
    int total = 0;

public:
    RandomPickWithWeight(std::vector<int>& w) {
        for (int weight : w) {
            total += weight;
            prefix.push_back(total);
        }
    }

    /**
     * @return an index proportional to its weight
     */
    int pickIndex() {
        int r = rand() % total + 1;

        int left = 0, right = prefix.size() - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (prefix[mid] < r) left = mid + 1;
            else right = mid;
        }
        return left;
    }
};
```

```python
import bisect
import random

class Solution:
    def __init__(self, w: list[int]):
        self.prefix = []
        total = 0
        for weight in w:
            total += weight
            self.prefix.append(total)

    def pick_index(self) -> int:
        r = random.randint(1, self.prefix[-1])
        return bisect.bisect_left(self.prefix, r)
```

```rust
use rand::Rng;

struct Solution {
    prefix: Vec<i32>,
}

impl Solution {
    fn new(w: Vec<i32>) -> Self {
        let mut prefix = Vec::with_capacity(w.len());
        let mut total = 0;
        for weight in w {
            total += weight;
            prefix.push(total);
        }
        Self { prefix }
    }

    /// @return an index proportional to its weight
    fn pick_index(&self) -> i32 {
        let total = *self.prefix.last().unwrap();
        let r = rand::thread_rng().gen_range(1..=total);

        let mut left = 0;
        let mut right = self.prefix.len() - 1;
        while left < right {
            let mid = left + (right - left) / 2;
            if self.prefix[mid] < r { left = mid + 1; } else { right = mid; }
        }
        left as i32
    }
}
```

## Dry run

**Input:** `w = [1,3]`.

```
prefix = [1,4].  total = 4.
r=1 -> bisect_left([1,4],1) = 0 (prob 1/4).
r=2,3,4 -> index 1 (prob 3/4).
Output: index 1 with probability 3/4 ✓
```

## Complexity

**Time.** O(log n) per pick:

$$
T = O(\log n)
$$

**Space.** The prefix array:

$$
S = O(n)
$$

## Variants & follow-ups

- **Random Pick Index** ([18.23](random-pick-index.md)) — uniform (all weights 1).
- **Interview follow-up:** "Why `r` in `[1, total]` and not `[0, total)`?" The segments are half-open `(prev, cur]` — drawing 1..total keeps each index's mass exactly w[i] with no off-by-one at zero.
