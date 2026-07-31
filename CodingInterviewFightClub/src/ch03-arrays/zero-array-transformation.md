# 3.32 Zero Array Transformation

> **Source**: [`src/main/kotlin/array/prefixsum/ZeroArrayTransformation_I.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/ZeroArrayTransformation_I.kt)
> **Pattern**: difference array + feasibility sweep · **Core page**

## The Problem

Can `queries [l, r]` (each decrementing `[l, r]` by 1) zero out `nums`? Queries can be used at most once.

- Constraints: n, q ≤ 10⁵.

## Examples

```
Input:  nums = [1,0,1], queries = [[0,2]]   -> Output: true  (one query covers all)
Input:  nums = [4,3,2,1], queries = [[1,3],[0,2]]  -> Output: false
```

## Intuition — the difference array counts coverage; every cell must be fully covered

Each query adds 1 to `[l, r]`. The **difference array** computes the coverage count per index in O(n+q); `nums[i]` can be zeroed iff `coverage[i] >= nums[i]`:

```kotlin
val diff = IntArray(n + 1)
for ((l, r) in queries) {
    diff[l] += 1
    if (r + 1 < n) diff[r + 1] -= 1
}

var total = 0
for (i in 0 until n) {
    total += diff[i]
    if (total < nums[i]) return false
}
return true
```

**Why the difference array?** Range updates (+1 on `[l, r]`) become two point updates; the prefix sweep materializes the coverage. The [3.15](find-pivot-index.md) prefix-sum machinery in range-update form.

## Approach 1 — Apply each query (O(qn))

Simulate: correct, slow.

## Approach 2 — Difference array (the repo's version, optimal)

```kotlin
class ZeroArrayTransformation_I {
    /**
     * @param nums    target array
     * @param queries [l, r] decrement ranges
     * @return        true iff nums can be zeroed
     */
    fun isZeroArray(nums: IntArray, queries: Array<IntArray>): Boolean {
        val n = nums.size
        val diff = IntArray(n + 1)

        for (query in queries) {
            val (l, r) = query
            diff[l] += 1
            if (r + 1 < n) {
                diff[r + 1] -= 1
            }
        }

        var total = 0
        for (i in 0 until n) {
            total += diff[i]
            if (total < nums[i]) return false
        }
        return true
    }
}
```

```java
public class ZeroArrayTransformation {
    /**
     * @param nums    target array
     * @param queries [l, r] decrement ranges
     * @return        true iff nums can be zeroed
     */
    public boolean isZeroArray(int[] nums, int[][] queries) {
        int n = nums.length;
        int[] diff = new int[n + 1];

        for (int[] q : queries) {
            diff[q[0]]++;
            if (q[1] + 1 < n) diff[q[1] + 1]--;
        }

        int total = 0;
        for (int i = 0; i < n; i++) {
            total += diff[i];
            if (total < nums[i]) return false;
        }
        return true;
    }
}
```

```cpp
#include <vector>

class ZeroArrayTransformation {
public:
    /**
     * @param nums    target array
     * @param queries [l, r] decrement ranges
     * @return        true iff nums can be zeroed
     */
    bool isZeroArray(std::vector<int>& nums, std::vector<std::vector<int>>& queries) {
        int n = nums.size();
        std::vector<int> diff(n + 1, 0);

        for (auto& q : queries) {
            diff[q[0]]++;
            if (q[1] + 1 < n) diff[q[1] + 1]--;
        }

        int total = 0;
        for (int i = 0; i < n; i++) {
            total += diff[i];
            if (total < nums[i]) return false;
        }
        return true;
    }
};
```

```python
def is_zero_array(nums: list[int], queries: list[list[int]]) -> bool:
    """
    @param nums:    target array
    @param queries: [l, r] decrement ranges
    @return:        true iff nums can be zeroed
    """
    n = len(nums)
    diff = [0] * (n + 1)

    for l, r in queries:
        diff[l] += 1
        if r + 1 < n:
            diff[r + 1] -= 1

    total = 0
    for i in range(n):
        total += diff[i]
        if total < nums[i]:
            return False
    return True
```

```rust
impl Solution {
    /// @param nums    target array
    /// @param queries [l, r] decrement ranges
    /// @return        true iff nums can be zeroed
    pub fn is_zero_array(nums: Vec<i32>, queries: Vec<Vec<i32>>) -> bool {
        let n = nums.len();
        let mut diff = vec![0; n + 1];

        for q in &queries {
            diff[q[0] as usize] += 1;
            if (q[1] as usize) + 1 < n { diff[q[1] as usize + 1] -= 1; }
        }

        let mut total = 0;
        for i in 0..n {
            total += diff[i];
            if total < nums[i] { return false; }
        }
        true
    }
}
```

## Dry run

**Input:** `nums = [1,0,1], queries = [[0,2]]`.

```
diff: [0] += 1, diff[3]? r+1 = 3 >= n -> no end decrement.  diff = [1,0,0,0]
sweep: i=0: total=1 >= 1 ✓.  i=1: 1 >= 0 ✓.  i=2: 1 >= 1 ✓.
Output: true ✓

Input: nums = [4,3,2,1], queries = [[1,3],[0,2]]: coverage: idx0:1, idx1:2, idx2:2, idx3:1.
  4 > 1 -> false ✓
```

## Complexity

**Time.** Two passes:

$$
T(n, q) = O(n + q)
$$

**Space.** The diff array:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Subarray Sums Divisible By K** ([10.11](../ch10-hash-tables/subarray-sums-divisible-by-k.md)) — the prefix-sum family.
- **Range Addition** — the identical diff-array technique's classic name.
- **Interview follow-up:** "Why does `coverage >= nums[i]` decide feasibility?" Each query decrements every cell it covers by exactly 1 — the total decrements available at index i equal its coverage count. Applying any subset of queries can't exceed the full coverage; the sweep's prefix sum IS that coverage.
