# 14.9 Count Of Smaller Numbers After Self

> **Source:** [`src/main/kotlin/tree/fenwick/CountOfSmallerNumberAfterSelf.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/fenwick/CountOfSmallerNumberAfterSelf.kt)
> **Pattern:** coordinate compression + Fenwick · **Core page**

## The Problem

For each `nums[i]`, count how many later elements are **strictly smaller**.

- Constraints: $1 \le n \le 10^5$; values fit in `Int`.

## Examples

```
Input:  nums = [5,2,6,1]   -> Output: [2,1,1,0]
Input:  nums = [-1,-1]     -> Output: [0,0]
```

## Intuition — scan right-to-left; the Fenwick counts *seen* values

Process `nums` from the right. When visiting `nums[i]`, every already-seen (right-of-i) smaller value is `query(rank - 1)` — a Fenwick **prefix sum over value-ranks**:

```
1. Coordinate-compress values to ranks (1..k)     # the map key, since values can be huge/negative
2. bit = FenwickTree(k)
3. for i in lastIndex downTo 0:
       r = rankMap[nums[i]]
       res[i] = bit.query(r - 1)    # how many seen ranks are < r
       bit.update(r, 1)             # now nums[i] is seen
```

**Why right-to-left?** The answer for `i` only depends on *later* elements — scanning backward makes "later" = "already inserted into the BIT". The [6.x](../ch06-graphs/pattern-primer.md) "process in the direction that makes the query trivially answerable" lesson.

**Why coordinate compression?** The Fenwick's indices must be 1..k, but values span `Int`. Ranking (`rankMap[num]` = 1-based position in the sorted unique values, the [10.12](../ch10-hash-tables/rank-transform-of-an-array.md) trick) maps any value into a dense index.

**Why `query(r - 1)`?** Strictly smaller = ranks below `r` — the prefix sum excludes the value itself (ties don't count). The BIT's prefix-sum operation is exactly "how many seen values are ≤ this rank".

## Approach 1 — Nested loops (O(n²))

For each i, scan j > i counting smaller: correct, quadratic.

## Approach 2 — Fenwick with compression (the repo's version, optimal)

```kotlin
class CountOfSmallerNumberAfterSelf {
    /**
     * @param nums input array
     * @return     count of smaller elements to the right for each position
     */
    fun countSmaller(nums: IntArray): List<Int> {
        if (nums.isEmpty()) return emptyList()

        // Step 1: coordinate compression (rank of each unique value)
        val sorted = nums.toTypedArray().sorted()
        val rankMap = HashMap<Int, Int>()
        var rank = 1
        for (num in sorted) {
            if (num !in rankMap) {
                rankMap[num] = rank++
            }
        }

        val bit = FenwickTree(rank)          // the [14.8](segment-tree-and-fenwick.md) engine
        val res = IntArray(nums.size)

        // Step 2: scan right-to-left, counting seen values below this rank
        for (i in nums.lastIndex downTo 0) {
            val r = rankMap[nums[i]]!!
            res[i] = bit.query(r - 1)        // strictly smaller
            bit.update(r, 1)                 // mark this value as seen
        }
        return res.toList()
    }
}
```

```java
import java.util.*;

public class CountOfSmallerNumbersAfterSelf {
    private int[] tree;

    private void update(int i, int delta) {
        while (i < tree.length) { tree[i] += delta; i += i & -i; }
    }

    private int query(int i) {
        int sum = 0;
        while (i > 0) { sum += tree[i]; i -= i & -i; }
        return sum;
    }

    /**
     * @param nums input array
     * @return     count of smaller elements to the right for each position
     */
    public List<Integer> countSmaller(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        Map<Integer, Integer> rank = new HashMap<>();
        int r = 1;
        for (int v : sorted) if (!rank.containsKey(v)) rank.put(v, r++);

        tree = new int[r];
        Integer[] res = new Integer[nums.length];

        for (int i = nums.length - 1; i >= 0; i--) {
            int rk = rank.get(nums[i]);
            res[i] = query(rk - 1);          // strictly smaller
            update(rk, 1);
        }
        return Arrays.asList(res);
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <unordered_map>

class CountOfSmallerNumbersAfterSelf {
    std::vector<int> tree;

    void update(int i, int delta) {
        while (i < (int)tree.size()) { tree[i] += delta; i += i & -i; }
    }

    int query(int i) {
        int sum = 0;
        while (i > 0) { sum += tree[i]; i -= i & -i; }
        return sum;
    }

public:
    /**
     * @param nums input array
     * @return     count of smaller elements to the right for each position
     */
    std::vector<int> countSmaller(std::vector<int>& nums) {
        std::vector<int> sorted = nums;
        std::sort(sorted.begin(), sorted.end());

        std::unordered_map<int, int> rank;
        int r = 1;
        for (int v : sorted) if (!rank.count(v)) rank[v] = r++;

        tree.assign(r, 0);
        std::vector<int> res(nums.size());

        for (int i = (int)nums.size() - 1; i >= 0; i--) {
            int rk = rank[nums[i]];
            res[i] = query(rk - 1);          // strictly smaller
            update(rk, 1);
        }
        return res;
    }
};
```

```python
class Fenwick:
    def __init__(self, size: int):
        self.tree = [0] * (size + 1)

    def update(self, i: int, delta: int) -> None:
        while i < len(self.tree):
            self.tree[i] += delta
            i += i & -i

    def query(self, i: int) -> int:
        total = 0
        while i > 0:
            total += self.tree[i]
            i -= i & -i
        return total


def count_smaller(nums: list[int]) -> list[int]:
    """
    @param nums: input array
    @return:     count of smaller elements to the right for each position
    """
    rank = {v: i + 1 for i, v in enumerate(sorted(set(nums)))}
    bit = Fenwick(len(rank))
    result = [0] * len(nums)

    for i in range(len(nums) - 1, -1, -1):
        r = rank[nums[i]]
        result[i] = bit.query(r - 1)     # strictly smaller
        bit.update(r, 1)

    return result
```

```rust
struct Fenwick {
    tree: Vec<i32>,
}

impl Fenwick {
    fn new(size: usize) -> Self { Self { tree: vec![0; size + 1] } }

    fn update(&mut self, mut i: usize, delta: i32) {
        while i < self.tree.len() { self.tree[i] += delta; i += i & i.wrapping_neg(); }
    }

    fn query(&self, mut i: usize) -> i32 {
        let mut total = 0;
        while i > 0 { total += self.tree[i]; i -= i & i.wrapping_neg(); }
        total
    }
}

impl Solution {
    /// @param nums input array
    /// @return     count of smaller elements to the right for each position
    pub fn count_smaller(nums: Vec<i32>) -> Vec<i32> {
        let mut sorted = nums.clone();
        sorted.sort_unstable();
        sorted.dedup();

        let rank: std::collections::HashMap<i32, usize> =
            sorted.iter().enumerate().map(|(i, &v)| (v, i + 1)).collect();

        let mut bit = Fenwick::new(sorted.len());
        let mut result = vec![0; nums.len()];

        for i in (0..nums.len()).rev() {
            let r = rank[&nums[i]];
            result[i] = bit.query(r - 1);   // strictly smaller
            bit.update(r, 1);
        }
        result
    }
}
```

## Dry run

**Input:** `nums = [5,2,6,1]`.

```
compression: sorted unique [1,2,5,6] -> rank {1:1, 2:2, 5:3, 6:4}.  bit = Fenwick(4)

i=3 (1): r=1.  query(0) = 0.  res[3]=0.  update(1,1).   bit: tree[1]=1, tree[2]=1, tree[4]=1
i=2 (6): r=4.  query(3): tree[3](0)+tree[2](1) = 1.  res[2]=1.  update(4,1): tree[4]=2
i=1 (2): r=2.  query(1): tree[1] = 1.  res[1]=1.  update(2,1): tree[2]=2
i=0 (5): r=3.  query(2): tree[2] = 2.  res[0]=2.  update(3,1): tree[3]=1

Output: [2,1,1,0] ✓
```

The right-to-left scan is the whole story: at `6`, the BIT already holds `{1}` (from the right side) — `query(3)` counts ranks < 4 → 1. At `5`, the BIT holds `{1,2,6}` — `query(2)` counts ranks < 3 → 2. Each `update(r, 1)` inserts the just-processed value for the next (more-left) positions. Ties: `[-1,-1]` → rank 1 for both; `query(0) = 0` each → `[0,0]` ✓.

## Complexity

**Time.** O(log n) per element:

$$
T(n) = O(n \log n)
$$

**Space.** Compression + BIT:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Segment Tree & Fenwick** ([14.8](segment-tree-and-fenwick.md)) — the engine page.
- **Rank Transform** ([10.12](../ch10-hash-tables/rank-transform-of-an-array.md)) — the compression step standalone.
- **Merge Sort** ([14.1](merge-sort.md)) — the divide-and-conquer twin (count during merge).
- **Interview follow-up:** "Why is this O(n log n) and not O(n log V)?" The compression bounds the BIT to `k ≤ n` indices — the log is over *distinct values*, not the value range. Without compression, `nums[i]` up to 10⁹ would need a 10⁹-sized tree; the rank map is what makes it feasible.
