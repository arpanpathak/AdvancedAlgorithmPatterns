# 18.23 Random Pick Index

> **Source**: [`src/main/kotlin/array/random/RandomPickIndex.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/random/RandomPickIndex.kt)
> **Pattern**: index buckets / reservoir · **Core page**

## The Problem

`pick(target)` returns a **uniformly random** index where `nums[i] == target`.

- Constraints: n ≤ 2×10⁴; ≤ 10⁴ calls.

## Examples

```
["Solution","pick","pick","pick"]
[[[1,2,3,3,3]],[3],[1],[3]]
-> [null,2/3/4 with prob 1/3,0,2/3/4 with prob 1/3]
```

## Intuition — bucket the indices per value; pick from the bucket

The repo's map-based version: `targetIndices[value] = list of indices`, and `pick` chooses uniformly within the list. The [18.22](linked-list-random-node.md) reservoir is the memory-light alternative:

```kotlin
class RandomPickIndex(nums: IntArray) {
    private val targetIndices = mutableMapOf<Int, MutableList<Int>>()

    init {
        for (i in nums.indices) {
            val num = nums[i]
            targetIndices.getOrPut(num) { mutableListOf() }.add(i)
        }
    }

    fun pick(target: Int): Int {
        val indices = targetIndices[target]!!
        return indices[Random.nextInt(indices.size)]
    }
}
```

**Why the buckets?** Each target's occurrences are pre-grouped — `pick` is a single random draw. Space O(n), pick O(1); the reservoir trades the map for O(1) space and O(n) pick ([18.22](linked-list-random-node.md) tradeoff).

## Approach 1 — Index buckets (the repo's version, optimal for many picks)

## Approach 2 — Reservoir (O(1) space, the streaming version)

Scan and keep the i-th match with probability 1/count — identical uniformity.

```kotlin
import java.util.*

class RandomPickIndex(nums: IntArray) {
    private val targetIndices = mutableMapOf<Int, MutableList<Int>>()

    init {
        for (i in nums.indices) {
            val num = nums[i]
            if (!targetIndices.containsKey(num)) {
                targetIndices[num] = mutableListOf()
            }
            targetIndices[num]?.add(i)
        }
    }

    /**
     * @param target search value
     * @return       a uniformly random matching index
     */
    fun pick(target: Int): Int {
        val indices = targetIndices[target]!!
        return indices[Random.nextInt(indices.size)]
    }
}
```

```java
import java.util.*;

public class RandomPickIndex {
    private final Map<Integer, List<Integer>> map = new HashMap<>();

    public RandomPickIndex(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            map.computeIfAbsent(nums[i], k -> new ArrayList<>()).add(i);
        }
    }

    /**
     * @param target search value
     * @return       a uniformly random matching index
     */
    public int pick(int target) {
        List<Integer> indices = map.get(target);
        return indices.get(new Random().nextInt(indices.size()));
    }
}
```

```cpp
#include <vector>
#include <unordered_map>
#include <cstdlib>

class RandomPickIndex {
    std::unordered_map<int, std::vector<int>> map;

public:
    RandomPickIndex(std::vector<int>& nums) {
        for (int i = 0; i < (int)nums.size(); i++) map[nums[i]].push_back(i);
    }

    /**
     * @param target search value
     * @return       a uniformly random matching index
     */
    int pick(int target) {
        auto& indices = map[target];
        return indices[rand() % indices.size()];
    }
};
```

```python
import random

class Solution:
    def __init__(self, nums: list[int]):
        self.map = {}
        for i, num in enumerate(nums):
            self.map.setdefault(num, []).append(i)

    def pick(self, target: int) -> int:
        indices = self.map[target]
        return random.choice(indices)
```

```rust
use std::collections::HashMap;
use rand::Rng;

struct Solution {
    map: HashMap<i32, Vec<i32>>,
}

impl Solution {
    fn new(nums: Vec<i32>) -> Self {
        let mut map: HashMap<i32, Vec<i32>> = HashMap::new();
        for (i, num) in nums.into_iter().enumerate() {
            map.entry(num).or_default().push(i as i32);
        }
        Self { map }
    }

    /// @param target search value
    /// @return       a uniformly random matching index
    fn pick(&self, target: i32) -> i32 {
        let indices = &self.map[&target];
        indices[rand::thread_rng().gen_range(0..indices.len())]
    }
}
```

## Dry run

**Input:** `nums = [1,2,3,3,3]`, `pick(3)`.

```
map: {1:[0], 2:[1], 3:[2,3,4]}.
pick(3): random draw from [2,3,4] — each with prob 1/3 ✓
```

## Complexity

**Time.** Pick O(1) (bucket) / O(n) (reservoir):

$$
T = O(1)
$$

**Space.** The map:

$$
S = O(n)
$$

## Variants & follow-ups

- **Random Pick With Weight** ([18.24](random-pick-with-weight.md)) — the weighted version: prefix sums + binary search.
- **Linked List Random Node** ([18.22](linked-list-random-node.md)) — the reservoir twin.
- **Interview follow-up:** "Bucket vs reservoir?" Buckets: O(1) pick, O(n) space. Reservoir: O(n) pick, O(1) space — and it handles *unknown* array lengths. Pick per call-count: many picks favor buckets, one-shot favors the reservoir.
