# 1.21 Apartment Hunting

> **Source:** [`src/main/kotlin/binarysearch/ApartmentHunting.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/binarysearch/ApartmentHunting.kt)
> **Pattern:** binary search + nearest-neighbor · **Gym page**

## The Problem

You're apartment-hunting on a street of `B` blocks. Each block is a map of amenities → boolean (does this block have a gym/school/store?). You need a set of **required amenities**. Define the **score** of a block as the *maximum* distance from it to the nearest block providing each required amenity. Find the block with the **minimum score**. Return its index (or `-1` if any required amenity is missing from the street entirely).

- Constraints: small `B` (≤ 100 in practice), few amenities.

## Examples

```
blocks = [
  {gym: false, school: true,  store: false},
  {gym: true,  school: false, store: false},
  {gym: true,  school: true,  store: false},
  {gym: false, school: true,  store: false},
  {gym: false, school: true,  store: true}
]
requirements = [gym, school, store]

Block 3 is optimal: gym at distance 1, school at 0, store at 1 -> score = max(1, 0, 1) = 1.
```

## Intuition — turn "nearest distance" into a binary search

The naive plan: for each block and each required amenity, scan all blocks to find the nearest one with that amenity. That's $O(B^2 R)$ — fine for tiny inputs, but the *interesting* refinement is:

1. **Precompute, for each amenity, the sorted list of block indices that have it.** (A simple pass over the blocks: $O(B \cdot A)$ where `A` is amenities per block.)
2. **For a query block `b`, the nearest block with a given amenity is the nearest neighbor of `b` in that sorted list** — found by **binary search** on the list ($O(\log K)$ where $K$ = number of blocks with that amenity), checking the two candidates around `b`'s insertion point.

Then the score of `b` is the max over all required amenities of these nearest distances: $O(R \log K)$ per block, $O(B R \log K)$ total. The binary search here is the *classic library function* — the same lower-bound idea from [1.18](search-insert-position.md), used as a subroutine.

The repo also ships a hand-rolled `binarySearch` that returns the *insertion point* as `-(low + 1)` on a miss — the standard C-style convention — which makes the "candidate check" code explicit and self-contained.

## Approach 1 — Brute force (all-pairs scan)

For each block, for each requirement, scan every block. $O(B^2 R)$. Simple, obviously correct; use it to validate the fast version.

## Approach 2 — Amenity lists + binary-search nearest neighbor (optimal)

```kotlin
/**
 * @param blocks      each block maps amenity name -> presence (true/false)
 * @param requirements the amenities the apartment must be near
 * @return            the index of the block minimizing the max distance to every
 *                    required amenity, or -1 if any required amenity is absent citywide
 */
fun findBestBlock(blocks: List<Map<String, Boolean>>, requirements: List<String>): Int {
    // Step 1: amenity -> sorted list of blocks that have it.
    val amenityMap = mutableMapOf<String, MutableList<Int>>()
    blocks.forEachIndexed { index, block ->
        block.forEach { (amenity, present) ->
            if (present) {
                amenityMap.getOrPut(amenity) { mutableListOf() }.add(index)
            }
        }
    }

    // Any missing requirement makes the whole hunt impossible.
    requirements.forEach {
        if (amenityMap[it].isNullOrEmpty()) return -1
    }

    // Step 2: for each block, max over requirements of the nearest distance.
    var bestBlock = -1
    var bestMaxDistance = Int.MAX_VALUE

    blocks.forEachIndexed { index, _ ->
        val maxDistance = requirements
            .map { amenity -> closestDistance(index, amenityMap[amenity]!!) }
            .maxOrNull()!!

        if (maxDistance < bestMaxDistance) {
            bestMaxDistance = maxDistance
            bestBlock = index
        }
    }
    return bestBlock
}

/**
 * @param blockIndex      the block we are scoring
 * @param blocksWithAmenity the sorted list of blocks that have the amenity
 * @return                the minimum distance from blockIndex to a block with the amenity
 */
fun closestDistance(blockIndex: Int, blocksWithAmenity: List<Int>): Int {
    val pos = blocksWithAmenity.binarySearch(blockIndex)   // standard lower-bound search
    return if (pos >= 0) 0                                  // the block itself has it
    else {
        val insertPoint = -pos - 1
        val leftDistance = if (insertPoint > 0) blockIndex - blocksWithAmenity[insertPoint - 1] else Int.MAX_VALUE
        val rightDistance = if (insertPoint < blocksWithAmenity.size) blocksWithAmenity[insertPoint] - blockIndex else Int.MAX_VALUE
        minOf(leftDistance, rightDistance)
    }
}
```

> The other languages use their standard library's binary-search-with-insertion-point. Kotlin's `binarySearch` returns `-insertionPoint - 1` on a miss (same convention as Java's `Collections.binarySearch`), which is exactly what `closestDistance` exploits.

```java
import java.util.*;

public class ApartmentHunting {
    /**
     * @param blocks       each block maps amenity name -> presence
     * @param requirements the amenities the apartment must be near
     * @return             index of the optimal block, or -1 if a requirement is missing citywide
     */
    public int findBestBlock(List<Map<String, Boolean>> blocks, List<String> requirements) {
        Map<String, List<Integer>> amenityMap = new HashMap<>();
        for (int i = 0; i < blocks.size(); i++) {
            for (Map.Entry<String, Boolean> e : blocks.get(i).entrySet()) {
                if (e.getValue()) {
                    amenityMap.computeIfAbsent(e.getKey(), k -> new ArrayList<>()).add(i);
                }
            }
        }
        for (String req : requirements) {
            if (!amenityMap.containsKey(req)) return -1;
        }

        int bestBlock = -1, bestScore = Integer.MAX_VALUE;
        for (int i = 0; i < blocks.size(); i++) {
            int score = 0;
            for (String req : requirements) {
                score = Math.max(score, closestDistance(i, amenityMap.get(req)));
            }
            if (score < bestScore) { bestScore = score; bestBlock = i; }
        }
        return bestBlock;
    }

    /**
     * @param blockIndex the block being scored
     * @param blocksWithAmenity sorted list of blocks having the amenity
     * @return           minimum distance to such a block
     */
    private int closestDistance(int blockIndex, List<Integer> blocksWithAmenity) {
        int pos = Collections.binarySearch(blocksWithAmenity, blockIndex);
        if (pos >= 0) return 0;
        int insertPoint = -pos - 1;
        int left = insertPoint > 0 ? blockIndex - blocksWithAmenity.get(insertPoint - 1) : Integer.MAX_VALUE;
        int right = insertPoint < blocksWithAmenity.size() ? blocksWithAmenity.get(insertPoint) - blockIndex : Integer.MAX_VALUE;
        return Math.min(left, right);
    }
}
```

```cpp
#include <vector>
#include <map>
#include <set>
#include <algorithm>
#include <climits>

class ApartmentHunting {
public:
    /**
     * @param blocks       each block maps amenity name -> presence
     * @param requirements the amenities the apartment must be near
     * @return             index of the optimal block, or -1 if a requirement is missing citywide
     */
    int findBestBlock(const std::vector<std::map<std::string, bool>>& blocks,
                      const std::vector<std::string>& requirements) {
        std::map<std::string, std::vector<int>> amenityMap;
        for (int i = 0; i < (int)blocks.size(); i++) {
            for (const auto& [amenity, present] : blocks[i]) {
                if (present) amenityMap[amenity].push_back(i);
            }
        }
        for (const auto& req : requirements) {
            if (!amenityMap.count(req)) return -1;
        }

        int bestBlock = -1, bestScore = INT_MAX;
        for (int i = 0; i < (int)blocks.size(); i++) {
            int score = 0;
            for (const auto& req : requirements) {
                score = std::max(score, closestDistance(i, amenityMap[req]));
            }
            if (score < bestScore) { bestScore = score; bestBlock = i; }
        }
        return bestBlock;
    }

private:
    /**
     * @param blockIndex       the block being scored
     * @param blocksWithAmenity sorted list of blocks having the amenity
     * @return                 minimum distance to such a block
     */
    int closestDistance(int blockIndex, const std::vector<int>& blocksWithAmenity) {
        auto it = std::lower_bound(blocksWithAmenity.begin(), blocksWithAmenity.end(), blockIndex);
        if (it != blocksWithAmenity.end() && *it == blockIndex) return 0;
        int left = INT_MAX, right = INT_MAX;
        if (it != blocksWithAmenity.begin()) left = blockIndex - *(it - 1);
        if (it != blocksWithAmenity.end())  right = *it - blockIndex;
        return std::min(left, right);
    }
};
```

```python
from bisect import bisect_left

def find_best_block(blocks: list[dict[str, bool]], requirements: list[str]) -> int:
    """
    @param blocks:       each block maps amenity name -> presence
    @param requirements: the amenities the apartment must be near
    @return:             index of the optimal block, or -1 if a requirement is missing citywide
    """
    amenity_map: dict[str, list[int]] = {}
    for index, block in enumerate(blocks):
        for amenity, present in block.items():
            if present:
                amenity_map.setdefault(amenity, []).append(index)

    for req in requirements:
        if req not in amenity_map:
            return -1

    def closest_distance(block_index: int, positions: list[int]) -> int:
        """
        @param block_index: the block being scored
        @param positions:   sorted list of blocks having the amenity
        @return:            minimum distance to such a block
        """
        pos = bisect_left(positions, block_index)
        if pos < len(positions) and positions[pos] == block_index:
            return 0
        left = block_index - positions[pos - 1] if pos > 0 else float("inf")
        right = positions[pos] - block_index if pos < len(positions) else float("inf")
        return min(left, right)

    best_block, best_score = -1, float("inf")
    for index in range(len(blocks)):
        score = max(closest_distance(index, amenity_map[req]) for req in requirements)
        if score < best_score:
            best_score, best_block = score, index
    return best_block
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param blocks       each block maps amenity name -> presence
    /// @param requirements the amenities the apartment must be near
    /// @return             index of the optimal block, or -1 if a requirement is missing citywide
    pub fn find_best_block(
        blocks: Vec<HashMap<String, bool>>,
        requirements: Vec<String>,
    ) -> i32 {
        let mut amenity_map: HashMap<String, Vec<i32>> = HashMap::new();
        for (i, block) in blocks.iter().enumerate() {
            for (amenity, present) in block {
                if *present {
                    amenity_map.entry(amenity.clone()).or_default().push(i as i32);
                }
            }
        }
        for req in &requirements {
            if !amenity_map.contains_key(req) {
                return -1;
            }
        }

        let mut best_block = -1;
        let mut best_score = i32::MAX;
        for i in 0..blocks.len() as i32 {
            let mut score = 0;
            for req in &requirements {
                let positions = &amenity_map[req];
                score = score.max(Self::closest_distance(i, positions));
            }
            if score < best_score {
                best_score = score;
                best_block = i;
            }
        }
        best_block
    }

    /// @param block_index the block being scored
    /// @param positions   sorted list of blocks having the amenity
    /// @return            minimum distance to such a block
    fn closest_distance(block_index: i32, positions: &[i32]) -> i32 {
        match positions.binary_search(&block_index) {
            Ok(_) => 0,
            Err(insert_point) => {
                let left = if insert_point > 0 { block_index - positions[insert_point - 1] } else { i32::MAX };
                let right = if insert_point < positions.len() { positions[insert_point] - block_index } else { i32::MAX };
                left.min(right)
            }
        }
    }
}
```

## Dry run

**Input:** the example blocks above. Step 1 builds the amenity lists:

```
gym    -> [1, 2]
school -> [0, 2, 3, 4]
store  -> [4]
```

Step 2 scores each block (nearest distances per requirement):

| block | gym | school | store | score = max |
|---|---|---|---|---|
| 0 | \|0−1\|=1 | 0 | \|0−4\|=4 | **4** |
| 1 | 0 | \|1−2\|=1 | \|1−4\|=3 | **3** |
| 2 | 0 | 0 | \|2−4\|=2 | **2** |
| 3 | \|3−2\|=1 | 0 | \|3−4\|=1 | **1** ← best |
| 4 | \|4−2\|=2 | 0 | 0 | **2** |

Trace `closestDistance(3, gym = [1, 2])`: binary search for 3 in `[1, 2]` → miss, insertion point 2 → left candidate = `3 − 2 = 1`, no right candidate → 1. ✓

Trace `closestDistance(3, store = [4])`: binary search for 3 → miss, insertion point 0 → right candidate = `4 − 3 = 1` → 1. ✓

Block 3 wins with score 1 — matching the problem's stated answer. The binary search only ever inspects the *two neighbors* of the insertion point, not all blocks.

## Complexity

**Preprocessing:** $O(B \cdot A)$ to build the amenity lists (`A` amenities per block).

**Scoring:** for each of $B$ blocks and $R$ requirements, one binary search over a list of size $K$ (max blocks sharing an amenity):

$$
T(B, R) = O(B \cdot R \cdot \log K), \qquad S = O(B \cdot A)
$$

With $B = 100$, that's ~100 × 3 × 7 ≈ 2100 operations — vs 30,000 for the brute force — and the gap widens as the street grows.

## Variants & follow-ups

- **Best Time To Buy And Sell Stock** (same "min over blocks" pattern? no — the *nearest-neighbor* idea recurs in range-query problems, e.g. finding the closest greater element).
- **Interview follow-up:** "Solve it in $O(B \cdot R)$ with two passes." For each amenity, do a left-to-right pass recording "distance to nearest on the left" and a right-to-left pass for "nearest on the right"; then `score[b] = max over amenities of min(left[b], right[b])`. This *removes* the log factor and is the "optimal" answer interviewers often expect — the binary search version trades a pass for an index structure. Both are worth narrating.
- **Interview follow-up:** "What if requirements are huge?" Precompute `nearest[i][amenity]` for *all* amenities in the two-pass way; queries then become $O(1)$ per block-amenity pair. The binary search version wins when only a small subset of amenities is queried.
