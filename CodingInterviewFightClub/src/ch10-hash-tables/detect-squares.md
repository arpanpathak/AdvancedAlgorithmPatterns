# 10.26 Detect Squares

> **Source**: [`src/main/kotlin/math/DetectSquares.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/DetectSquares.kt)
> **Pattern**: diagonal-completion counting · **Core page**

## The Problem

`add(point)` and `count(point)` = the number of axis-aligned squares with `point` as a corner.

- Constraints: ≤ 3000 ops; coords ≤ 1000.

## Examples

```
["DetectSquares","add","add","add","count","count","add","count"]
[[],[[3,10]],[[11,2]],[[3,2]],[[11,10]],[[14,8]],[[11,2]],[[11,10]]]
-> [null,null,null,null,1,0,null,2]
```

## Intuition — for each stored point on the same diagonal, the other two corners are forced

`count(p)` iterates the stored points. A square with corner `p` and stored `(px, py)` on a diagonal has `|x-px| == |y-py| = d`; the other corners are `(x, py)` and `(px, y)` — multiply their counts:

```kotlin
fun count(point: IntArray): Int {
    val (x, y) = point
    var squareCount = 0

    pointCount.forEach { (p, count) ->
        val (px, py) = p
        if (x != px && y != py) {
            val d1 = abs(x - px)
            val d2 = abs(y - py)
            if (d1 == d2) {     // a diagonal partner
                squareCount += count *
                    (pointCount[x to py] ?: 0) *
                    (pointCount[px to y] ?: 0)
            }
        }
    }
    return squareCount
}
```

**Why diagonal pairing?** An axis-aligned square's opposite corners share a diagonal — given one pair `(p, (px,py))` on a diagonal, the other two corners `(x, py)` and `(px, y)` are *forced*. Counting their multiplicities completes the square count.

## Approach 1 — Brute force over corner triples (O(n³))

Try all 3-point combinations: correct, slow.

## Approach 2 — Diagonal pairing (the repo's version, optimal)

```kotlin
class DetectSquares() {
    private val pointCount = mutableMapOf<Pair<Int, Int>, Int>()

    /**
     * @param point point to add
     */
    fun add(point: IntArray) {
        val key = point[0] to point[1]
        pointCount[key] = pointCount.getOrDefault(key, 0) + 1
    }

    /**
     * @param point query corner
     * @return      number of squares with this corner
     */
    fun count(point: IntArray): Int {
        val (x, y) = point
        var squareCount = 0

        pointCount.forEach { (p, count) ->
            val (px, py) = p
            if (x != px && y != py) {
                val d1 = abs(x - px)
                val d2 = abs(y - py)

                if (d1 == d2) {
                    squareCount += count *
                        (pointCount[x to py] ?: 0) *
                        (pointCount[px to y] ?: 0)
                }
            }
        }
        return squareCount
    }
}
```

```java
import java.util.*;

public class DetectSquares {
    private final Map<String, Integer> counts = new HashMap<>();

    private String key(int x, int y) { return x + "," + y; }

    /**
     * @param point point to add
     */
    public void add(int[] point) {
        counts.put(key(point[0], point[1]), counts.getOrDefault(key(point[0], point[1]), 0) + 1);
    }

    /**
     * @param point query corner
     * @return      number of squares with this corner
     */
    public int count(int[] point) {
        int x = point[0], y = point[1];
        int total = 0;

        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            String[] parts = e.getKey().split(",");
            int px = Integer.parseInt(parts[0]), py = Integer.parseInt(parts[1]);

            if (x != px && y != py && Math.abs(x - px) == Math.abs(y - py)) {
                total += e.getValue()
                       * counts.getOrDefault(key(x, py), 0)
                       * counts.getOrDefault(key(px, y), 0);
            }
        }
        return total;
    }
}
```

```cpp
#include <map>
#include <cmath>

class DetectSquares {
    std::map<std::pair<int, int>, int> counts;

public:
    /**
     * @param point point to add
     */
    void add(std::vector<int>& point) {
        counts[{point[0], point[1]}]++;
    }

    /**
     * @param point query corner
     * @return      number of squares with this corner
     */
    int count(std::vector<int>& point) {
        int x = point[0], y = point[1];
        int total = 0;

        for (auto& [p, c] : counts) {
            auto [px, py] = p;

            if (x != px && y != py && std::abs(x - px) == std::abs(y - py)) {
                total += c * counts[{x, py}] * counts[{px, y}];
            }
        }
        return total;
    }
};
```

```python
from collections import defaultdict

class DetectSquares:
    def __init__(self):
        self.counts = defaultdict(int)

    def add(self, point: list[int]) -> None:
        self.counts[tuple(point)] += 1

    def count(self, point: list[int]) -> int:
        x, y = point
        total = 0

        for (px, py), c in self.counts.items():
            if x != px and y != py and abs(x - px) == abs(y - py):
                total += c * self.counts[(x, py)] * self.counts[(px, y)]

        return total
```

```rust
use std::collections::HashMap;

struct DetectSquares {
    counts: HashMap<(i32, i32), i32>,
}

impl DetectSquares {
    fn new() -> Self { Self { counts: HashMap::new() } }

    /// @param point point to add
    fn add(&mut self, point: Vec<i32>) {
        *self.counts.entry((point[0], point[1])).or_insert(0) += 1;
    }

    /// @param point query corner
    /// @return      number of squares with this corner
    fn count(&self, point: Vec<i32>) -> i32 {
        let (x, y) = (point[0], point[1]);
        let mut total = 0;

        for (&(px, py), &c) in &self.counts {
            if x != px && y != py && (x - px).abs() == (y - py).abs() {
                total += c
                    * self.counts.get(&(x, py)).copied().unwrap_or(0)
                    * self.counts.get(&(px, y)).copied().unwrap_or(0);
            }
        }
        total
    }
}
```

## Dry run

**Input:** `add(3,10), add(11,2), add(3,2), count(11,10)`.

```
count(11,10): stored (3,2): |11-3|=8, |10-2|=8 -> diagonal pair!
  other corners: (11,2) count 1, (3,10) count 1.  total += 1*1*1 = 1.
stored (3,10): x == 11? no, y == 10? yes -> skip (same row).  (11,2): x same -> skip.
Output: 1 ✓
```

The axis-alignment means opposite corners differ in both coordinates by the same amount — the diagonal test. The multiplication `c × corner1 × corner2` counts all combinations of the multiplicities.

## Complexity

**Time.** Stored points per count:

$$
T(n) = O(n) \text{ per count}
$$

**Space.** The counts map:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why iterate stored points instead of coordinates?" The map iteration is O(unique points) — with ≤ 3000 ops it's the practical bound. A coordinate-bucketed variant (group by x) is the same idea with better constants.
