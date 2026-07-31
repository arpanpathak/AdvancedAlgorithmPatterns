# 3.16 Shuffle An Array

> **Source:** [`src/main/kotlin/google/ShuffleWithRandomness.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/google/ShuffleWithRandomness.kt) (+ `SongShuffle.kt` — the playlist variant)
> **Pattern:** Fisher–Yates in place · **Core page**

## The Problem

`reset()` returns the original array; `shuffle()` returns a **uniformly random permutation**.

- Constraints: each permutation equally likely; O(n) per call.

## Examples

```
["Solution","shuffle","reset","shuffle"]
[[[1,2,3]],[],[],[]]
-> [[3,1,2],[1,2,3],[1,3,2]]   (any permutation, uniformly)
```

## Intuition — walk the array, swap each position with a random *later* position

The Fisher–Yates shuffle: for `i` from `n-1` down to 1, pick `j` uniformly in `[0, i]` and swap `nums[i] ↔ nums[j]`. Every permutation is equally likely because each position's final occupant is chosen from the remaining pool with uniform probability:

```kotlin
val rand = Random()
for (i in lastIndex downTo 1) {
    val j = rand.nextInt(i + 1)      // uniform in [0, i]
    nums[i] = nums[j].also { nums[j] = nums[i] }
}
```

**Why `nextInt(i + 1)` and not `nextInt(n)`?** The classic bug: picking from the *whole* array biases the shuffle (early positions get more chances). Restricting to `[0, i]` ensures each of the `n!` permutations has exactly probability `1/n!`.

**Why the repo's playlist variant is the same idea with a twist** — `ShuffleWithRandomness.kt` shuffles *per-artist* queues then random-picks artists with a cooldown: the interview answer to "shuffle a playlist so no artist repeats". Fisher–Yates is the engine; the eligible-pool is the constraint layer.

## Approach 1 — Copy and sort with random keys (O(n log n))

Attach random keys, sort: correct distribution, log-factor slower and memory-heavy.

## Approach 2 — Fisher–Yates in place (the repo's engine, optimal)

```kotlin
import java.util.Random

class Solution(private val original: IntArray) {
    private val rand = Random()

    /**
     * @return the original array
     */
    fun reset(): IntArray = original.clone()

    /**
     * @return a uniformly random permutation
     */
    fun shuffle(): IntArray {
        val nums = original.clone()

        for (i in nums.lastIndex downTo 1) {
            val j = rand.nextInt(i + 1)        // uniform in [0, i]
            nums[i] = nums[j].also { nums[j] = nums[i] }
        }
        return nums
    }
}
```

```java
import java.util.*;

public class Solution {
    private final int[] original;
    private final Random rand = new Random();

    public Solution(int[] nums) { original = nums.clone(); }

    /**
     * @return the original array
     */
    public int[] reset() { return original.clone(); }

    /**
     * @return a uniformly random permutation
     */
    public int[] shuffle() {
        int[] a = original.clone();

        for (int i = a.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);       // uniform in [0, i]
            int t = a[i]; a[i] = a[j]; a[j] = t;
        }
        return a;
    }
}
```

```cpp
#include <vector>
#include <random>

class Solution {
    std::vector<int> original;
    std::mt19937 rng;

public:
    Solution(std::vector<int>& nums) : original(nums), rng(std::random_device{}()) {}

    /**
     * @return the original array
     */
    std::vector<int> reset() { return original; }

    /**
     * @return a uniformly random permutation
     */
    std::vector<int> shuffle() {
        std::vector<int> a = original;

        for (int i = (int)a.size() - 1; i > 0; i--) {
            std::uniform_int_distribution<int> dist(0, i);
            int j = dist(rng);                 // uniform in [0, i]
            std::swap(a[i], a[j]);
        }
        return a;
    }
};
```

```python
import random

class Solution:
    """@param nums: the array to shuffle and reset"""

    def __init__(self, nums: list[int]):
        self.original = nums[:]

    def reset(self) -> list[int]:
        return self.original[:]

    def shuffle(self) -> list[int]:
        a = self.original[:]
        for i in range(len(a) - 1, 0, -1):
            j = random.randint(0, i)        # uniform in [0, i]
            a[i], a[j] = a[j], a[i]
        return a
```

```rust
use rand::Rng;

struct Solution {
    original: Vec<i32>,
}

impl Solution {
    fn new(nums: Vec<i32>) -> Self { Self { original: nums } }

    /// @return the original array
    fn reset(&self) -> Vec<i32> { self.original.clone() }

    /// @return a uniformly random permutation
    fn shuffle(&self) -> Vec<i32> {
        let mut a = self.original.clone();
        let mut rng = rand::thread_rng();

        for i in (1..a.len()).rev() {
            let j = rng.gen_range(0..=i);   // uniform in [0, i]
            a.swap(i, j);
        }
        a
    }
}
```

## Dry run

**Input:** `nums = [1,2,3]`. One shuffle path:

```
i=2: j = random in [0,2].  say j=0.  swap a[2]↔a[0] -> [3,2,1]
i=1: j = random in [0,1].  say j=1.  swap a[1]↔a[1] -> [3,2,1]  (no-op)
Output: [3,2,1]
```

Uniformity check: for `[1,2,3]` there are `3! = 6` outcomes. Fisher–Yates assigns each outcome probability `1/3 · 1/2 · 1 = 1/6` — the `j` choices at `i=2` (3 options) × `i=1` (2 options) × `i=0` (1 option) multiply to 6 equally likely leaves. `reset()` returns `[1,2,3]` regardless.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The saved original + clone:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **ShuffleWithRandomness.kt / SongShuffle.kt** (the repo) — playlist shuffling with per-artist variety: Fisher–Yates per bucket + an eligible-artist pool with cooldown.
- **K Closest Points To Origin** ([14.3](../ch14-sorting/k-closest-points-to-origin.md)) — randomness in a different role (quickselect pivot).
- **Interview follow-up:** "Why is picking `j` in `[0, i]` (not `[0, n)`) essential?" With `[0, n)` the early positions bias — the first swap's candidate pool is the whole array, but later positions' odds shift. The shrinking range is what makes each of the n! orders equiprobable; `rand.nextInt(i + 1)` is the entire correctness argument.
