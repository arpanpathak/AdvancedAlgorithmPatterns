# 11.12 Can Place Flowers

> **Source:** [`src/main/kotlin/array/greedy/CanPlaceFlowers.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/greedy/CanPlaceFlowers.kt)
> **Pattern:** greedy with boundary-safe adjacency · **Core page**

## The Problem

Given `flowerbed` (0 empty, 1 planted), can we plant `n` flowers with **no two adjacent**?

- Constraints: $1 \le$ bed ≤ 2×10⁴; values 0/1.

## Examples

```
Input:  flowerbed = [1,0,0,0,1], n = 1   -> Output: true
Input:  flowerbed = [1,0,0,0,1], n = 2   -> Output: false
```

## Intuition — plant at every legal spot, count, bail early

A spot is plantable iff it's empty and both neighbors are empty (the edges treat the out-of-bounds side as empty). Greedily plant the first legal spot — planting early never hurts, since a later plant can only be *more* constrained:

```kotlin
for (i in indices):
    val isLeftEmpty = (i == 0 || flowerbed[i-1] == 0)
    val isRightEmpty = (i == lastIndex || flowerbed[i+1] == 0)
    if (flowerbed[i] == 0 && isLeftEmpty && isRightEmpty) {
        flowers++
        flowerbed[i] = 1            # plant: mark it so neighbors skip
    }
    if (flowers >= n) return true
return false
```

**Why is marking (`flowerbed[i] = 1`) necessary?** The planted spot must block its *neighbors* from planting — otherwise `[0,0,0]` would count 3 plants (illegal). The in-place mark is the visited-set; the [11.0](pattern-primer.md) greedy "commit and move on".

**Why the boundary `i == 0 || ...` elvis?** The edges have only one neighbor; the `||` treats the missing side as empty — no sentinel padding needed.

## Approach 1 — Check every triple (scan-only, no mutation)

Look at `i-1, i, i+1` without planting: also correct, but needs care with the window sliding past already-counted spots.

## Approach 2 — Greedy plant-and-mark (the repo's version, optimal)

```kotlin
class CanPlaceFlowers {
    /**
     * @param flowerbed 0=empty, 1=planted
     * @param n          flowers to plant
     * @return          true iff n flowers can be planted non-adjacently
     */
    fun canPlaceFlowers(flowerbed: IntArray, n: Int): Boolean {
        var flowers = 0

        for (i in 0 until flowerbed.size) {
            val isLeftEmpty = (i == 0 || flowerbed[i - 1] == 0)
            val isRightEmpty = (i == flowerbed.lastIndex || flowerbed[i + 1] == 0)

            if (flowerbed[i] == 0 && isLeftEmpty && isRightEmpty) {
                flowers++
                flowerbed[i] = 1            // plant: block the neighbors
            }
            if (flowers >= n) return true   // early exit
        }
        return false
    }
}
```

```java
public class CanPlaceFlowers {
    /**
     * @param flowerbed 0=empty, 1=planted
     * @param n          flowers to plant
     * @return          true iff n flowers can be planted non-adjacently
     */
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        int planted = 0;

        for (int i = 0; i < flowerbed.length; i++) {
            boolean left = i == 0 || flowerbed[i - 1] == 0;
            boolean right = i == flowerbed.length - 1 || flowerbed[i + 1] == 0;

            if (flowerbed[i] == 0 && left && right) {
                planted++;
                flowerbed[i] = 1;            // plant: block the neighbors
            }
            if (planted >= n) return true;   // early exit
        }
        return false;
    }
}
```

```cpp
#include <vector>

class CanPlaceFlowers {
public:
    /**
     * @param flowerbed 0=empty, 1=planted
     * @param n          flowers to plant
     * @return          true iff n flowers can be planted non-adjacently
     */
    bool canPlaceFlowers(std::vector<int>& flowerbed, int n) {
        int planted = 0;

        for (int i = 0; i < (int)flowerbed.size(); i++) {
            bool left = i == 0 || flowerbed[i - 1] == 0;
            bool right = i == (int)flowerbed.size() - 1 || flowerbed[i + 1] == 0;

            if (flowerbed[i] == 0 && left && right) {
                planted++;
                flowerbed[i] = 1;            // plant: block the neighbors
            }
            if (planted >= n) return true;   // early exit
        }
        return false;
    }
};
```

```python
def can_place_flowers(flowerbed: list[int], n: int) -> bool:
    """
    @param flowerbed: 0=empty, 1=planted
    @param n:          flowers to plant
    @return:          true iff n flowers can be planted non-adjacently
    """
    planted = 0

    for i in range(len(flowerbed)):
        left = i == 0 or flowerbed[i - 1] == 0
        right = i == len(flowerbed) - 1 or flowerbed[i + 1] == 0

        if flowerbed[i] == 0 and left and right:
            planted += 1
            flowerbed[i] = 1                # plant: block the neighbors

        if planted >= n:
            return True                     # early exit
    return False
```

```rust
impl Solution {
    /// @param flowerbed 0=empty, 1=planted
    /// @param n          flowers to plant
    /// @return          true iff n flowers can be planted non-adjacently
    pub fn can_place_flowers(flowerbed: Vec<i32>, n: i32) -> bool {
        let mut bed = flowerbed;
        let mut planted = 0;

        for i in 0..bed.len() {
            let left = i == 0 || bed[i - 1] == 0;
            let right = i == bed.len() - 1 || bed[i + 1] == 0;

            if bed[i] == 0 && left && right {
                planted += 1;
                bed[i] = 1;                          // plant: block the neighbors
            }
            if planted >= n { return true; }         // early exit
        }
        false
    }
}
```

## Dry run

**Input:** `flowerbed = [1,0,0,0,1]`, `n = 2`.

```
i=0 (1): not empty -> skip.
i=1 (0): left = bed[0]=1 -> not empty -> skip.
i=2 (0): left = bed[1]=0 ✓, right = bed[3]=0 ✓ -> plant.  planted=1.  bed=[1,0,1,0,1].
i=3 (0): left = bed[2]=1 -> not empty -> skip.   (the mark at i=2 blocked it — correct!)
i=4 (1): skip.

planted=1 < n=2 -> false ✓
```

The in-place mark does the real work: after planting at i=2, the neighbor checks at i=1 and i=3 both see a `1` and correctly skip — without the mark, `[0,0,0]` would triple-count. With `n = 1` the early exit fires at i=2 → true.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** In place:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **House Robber** ([2.17](../ch02-dynamic-programming/house-robber.md)) — the *maximize* version of the same no-adjacent constraint (DP instead of greedy, because values matter).
- **Interview follow-up:** "Why is greedy safe here (no DP needed)?" Planting at the first legal spot never reduces the total: a later spot's legality depends only on already-decided neighbors, and the mark keeps the count honest. The no-adjacent constraint has no "value" axis, so the local choice is globally optimal — the [11.0](pattern-primer.md) exchange argument at its simplest.
