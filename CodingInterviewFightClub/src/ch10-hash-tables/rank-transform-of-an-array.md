# 10.12 Rank Transform Of An Array

> **Source:** [`src/main/kotlin/array/hashtable/RankTransformOfAnArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/RankTransformOfAnArray.kt)
> **Pattern:** sort + first-occurrence map · **Core page**

## The Problem

Replace each element with its **rank** (1-based, smallest = 1); equal elements share a rank.

- Constraints: $1 \le n \le 10^5$; values fit in `Int`.

## Examples

```
Input:  arr = [40,10,20,30]        -> Output: [4,1,2,3]
Input:  arr = [100,100,100]        -> Output: [1,1,1]
```

## Intuition — ranks are just "position in the sorted unique order"

Sorting the array gives the order; the rank of a value is its **1-based index in the deduped sort**. One map from value → rank, built with the `getOrPut` first-occurrence trick:

```
sorted = arr.sorted()
rankMap = {}
rank = 1
for num in sorted: rankMap.getOrPut(num) { rank++ }   // only the FIRST copy consumes a rank
return arr.map { rankMap[it]!! }
```

**Why `getOrPut(num) { rank++ }`?** The lambda runs *only when the key is absent* — so duplicates share a rank automatically (the second `100` finds the existing entry and never increments). This is the "first occurrence wins" idiom ([10.10](first-missing-positive.md)'s marking has the same spirit).

**Why sort + map and not a TreeMap?** Both work; the sorted-array + HashMap is O(n log n) and dead simple. A TreeMap would also do O(n log n) but with more machinery.

## Approach 1 — Sort + rank map (the repo's version, optimal)

```kotlin
class RankTransformOfAnArray {
    /**
     * @param arr input array
     * @return    rank of each element (equal elements share a rank)
     */
    fun arrayRankTransform(arr: IntArray): IntArray {
        val sortedArr = arr.sorted()

        val rankMap = mutableMapOf<Int, Int>()
        var rank = 1
        for (num in sortedArr) {
            rankMap.getOrPut(num) { rank++ }      // first occurrence only
        }

        return arr.map { rankMap[it]!! }.toIntArray()
    }
}
```

```java
import java.util.*;

public class RankTransformOfAnArray {
    /**
     * @param arr input array
     * @return    rank of each element (equal elements share a rank)
     */
    public int[] arrayRankTransform(int[] arr) {
        int[] sorted = arr.clone();
        Arrays.sort(sorted);

        Map<Integer, Integer> rank = new HashMap<>();
        int r = 1;
        for (int num : sorted) {
            if (!rank.containsKey(num)) rank.put(num, r++);   // first occurrence only
        }

        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) result[i] = rank.get(arr[i]);
        return result;
    }
}
```

```cpp
#include <algorithm>
#include <unordered_map>
#include <vector>

class RankTransformOfAnArray {
public:
    /**
     * @param arr input array
     * @return    rank of each element (equal elements share a rank)
     */
    std::vector<int> arrayRankTransform(std::vector<int>& arr) {
        auto sorted = arr;
        std::sort(sorted.begin(), sorted.end());

        std::unordered_map<int, int> rank;
        int r = 1;
        for (int num : sorted) {
            if (!rank.count(num)) rank[num] = r++;   // first occurrence only
        }

        std::vector<int> result(arr.size());
        for (int i = 0; i < (int)arr.size(); i++) result[i] = rank[arr[i]];
        return result;
    }
};
```

```python
def array_rank_transform(arr: list[int]) -> list[int]:
    """
    @param arr: input array
    @return:    rank of each element (equal elements share a rank)
    """
    rank = {}
    r = 1
    for num in sorted(arr):
        if num not in rank:
            rank[num] = r          # first occurrence only
            r += 1
    return [rank[num] for num in arr]
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param arr input array
    /// @return    rank of each element (equal elements share a rank)
    pub fn array_rank_transform(arr: Vec<i32>) -> Vec<i32> {
        let mut sorted = arr.clone();
        sorted.sort_unstable();

        let mut rank: HashMap<i32, i32> = HashMap::new();
        let mut r = 1;
        for num in sorted {
            rank.entry(num).or_insert_with(|| { let v = r; r += 1; v });   // first occurrence only
        }
        arr.iter().map(|n| rank[n]).collect()
    }
}
```

## Dry run

**Input:** `arr = [40,10,20,30]`.

```
sorted = [10,20,30,40]
rankMap: 10 -> 1 (rank becomes 2).  20 -> 2.  30 -> 3.  40 -> 4.

map back: 40->4, 10->1, 20->2, 30->3.

Output: [4,1,2,3] ✓
```

With duplicates (`[100,100,100]`): `sorted = [100,100,100]`; the `getOrPut` runs the lambda only on the first `100` (rank 1) — the next two find the entry and never increment. Output `[1,1,1]` ✓.

## Complexity

**Time.** Sort + map:

$$
T(n) = O(n \log n)
$$

**Space.** The rank map:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Unique Number Of Occurrences** ([10.13](unique-number-of-occurrences.md)) — the same "first-occurrence map" counting family, checking distinctness of counts instead.
- **H-Index** ([14.5](../ch14-sorting/h-index.md)) — ranking by citations, the sorting cousin.
- **Interview follow-up:** "Why does `getOrPut(num) { rank++ }` assign the same rank to duplicates?" The `rank++` expression evaluates only when the key is missing — the *side effect* is gated by absence. That single idiom replaces the two-line `if (!containsKey) put(num, rank++)`, and it's the pattern the whole problem turns on.
