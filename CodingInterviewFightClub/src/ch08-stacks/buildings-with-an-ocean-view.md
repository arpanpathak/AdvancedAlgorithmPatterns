# 8.27 Buildings With An Ocean View

> **Source**: [`src/main/kotlin/stack/BuildingsWithAnOceanView.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/BuildingsWithAnOceanView.kt)
> **Pattern**: right-to-left running max · **Core page**

## The Problem

Indices of buildings with no taller-or-equal building to their right.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  heights = [4,2,3,1]   -> Output: [0,2,3]
```

## Intuition — walk right-to-left; a building sees the ocean iff it's a new maximum

```kotlin
val result = mutableListOf<Int>()

for (i in heights.indices.reversed()) {
    if (result.isEmpty() || heights[i] > heights[result.last()]) {
        result.add(i)
    }
}
return result.reversed().toIntArray()
```

## Approach 1 — Right-to-left max (the repo's version, optimal)

```kotlin
class BuildingsWithAnOceanView {
    /**
     * @param heights building heights
     * @return        indices with an ocean view
     */
    fun findBuildings(heights: IntArray): IntArray {
        val result = mutableListOf<Int>()

        for (i in heights.indices.reversed()) {
            if (result.isEmpty() || heights[i] > heights[result.last()]) {
                result.add(i)
            }
        }
        return result.reversed().toIntArray()
    }
}
```

```java
import java.util.*;

public class BuildingsWithAnOceanView {
    /**
     * @param heights building heights
     * @return        indices with an ocean view
     */
    public int[] findBuildings(int[] heights) {
        List<Integer> list = new ArrayList<>();
        int max = 0;

        for (int i = heights.length - 1; i >= 0; i--) {
            if (heights[i] > max) {
                list.add(i);
                max = heights[i];
            }
        }

        Collections.reverse(list);
        return list.stream().mapToInt(Integer::intValue).toArray();
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class BuildingsWithAnOceanView {
public:
    /**
     * @param heights building heights
     * @return        indices with an ocean view
     */
    std::vector<int> findBuildings(std::vector<int>& heights) {
        std::vector<int> result;
        int max = 0;

        for (int i = heights.size() - 1; i >= 0; i--) {
            if (heights[i] > max) {
                result.push_back(i);
                max = heights[i];
            }
        }

        std::reverse(result.begin(), result.end());
        return result;
    }
};
```

```python
def find_buildings(heights: list[int]) -> list[int]:
    """
    @param heights: building heights
    @return:        indices with an ocean view
    """
    result = []
    max_height = 0

    for i in range(len(heights) - 1, -1, -1):
        if heights[i] > max_height:
            result.append(i)
            max_height = heights[i]

    return result[::-1]
```

```rust
impl Solution {
    /// @param heights building heights
    /// @return        indices with an ocean view
    pub fn find_buildings(heights: Vec<i32>) -> Vec<i32> {
        let mut result = Vec::new();
        let mut max_height = 0;

        for i in (0..heights.len()).rev() {
            if heights[i] > max_height {
                result.push(i as i32);
                max_height = heights[i];
            }
        }

        result.reverse();
        result
    }
}
```

## Reading the code — what's actually happening

```kotlin
val result = mutableListOf<Int>()
for (i in heights.indices.reversed()) {
    if (result.isEmpty() || heights[i] > heights[result.last()]) {
        result.add(i)
    }
}
return result.reversed().toIntArray()
```

Stand on the **rightmost** building: it always has an ocean view (nothing to its right). Walk leftward from there, and a building has a view **iff it's taller than every building already seen** — because those are the buildings between it and the ocean.

- **`heights.indices.reversed()` walks right to left.** The ocean is on the right, so we judge buildings from the ocean side inward — each building's view depends only on what's *already* been judged (everything to its right).
- **`result.last()` is a clever running-max trick.** `result` collects the indices of view-having buildings *in decreasing index order*. Its last element is the *leftmost* view-having building so far — which is the *tallest* building to the right (any taller building to the right would itself have a view and be in the list... more precisely, the leftmost one with a view is the max of the right side). Comparing `heights[i] > heights[result.last()]` is therefore "am I taller than everything to my right?" — the exact view condition.
- **`result.add(i)` records a view-building; the empty check lets the rightmost building in** (`result.isEmpty()` → add unconditionally, since nothing blocks it).
- **`result.reversed()` fixes the order.** The loop produced indices from right to left (`[3,2,0]`); the answer must be ascending (`[0,2,3]`), so one reversal at the end.

Trace `[4,2,3,1]`: `i=3` (1) → add 3; `i=2` (3) > 1 → add 2; `i=1` (2) not > 3 → skip; `i=0` (4) > 3 → add 0. `[3,2,0]` reversed → `[0,2,3]` ✓.

## Dry run

**Input:** `heights = [4,2,3,1]`.

```
i=3 (1): > 0 -> add 3, max 1.  i=2 (3): > 1 -> add 2, max 3.  i=1 (2): not > 3.  i=0 (4): > 3 -> add 0.
result [3,2,0] reversed -> [0,2,3] ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The result:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Daily Temperatures** ([8.3](daily-temperatures.md)) — the next-greater sibling.
- **Interview follow-up:** "Why is the running max enough?" A building's view is blocked by the *first* taller-or-equal building — equivalently, it must exceed everything to its right, i.e. be a suffix maximum.
