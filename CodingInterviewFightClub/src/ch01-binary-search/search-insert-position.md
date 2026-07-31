# 1.18 Search Insert Position

> **Source:** [`src/main/kotlin/binarysearch/SearchInsertionPosition.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/binarysearch/SearchInsertionPosition.kt)
> **Pattern:** lower bound · **Core page** (this is the "hello world" of binary search)

## The Problem

Given a **sorted** array of **distinct** integers and a `target`, return the index where `target` is found, **or** the index where it *would be inserted* to keep the array sorted.

- Constraints: $1 \le n \le 10^4$.

## Examples

```
Input:  nums = [1, 3, 5, 6], target = 5  -> Output: 2   (found at index 2)
Input:  nums = [1, 3, 5, 6], target = 2  -> Output: 1   (would sit between 1 and 3)
Input:  nums = [1, 3, 5, 6], target = 7  -> Output: 4   (would be appended)
Input:  nums = [1, 3, 5, 6], target = 0  -> Output: 0   (would be prepended)
```

## Intuition

The required answer is exactly **the first index where `nums[i] >= target`** — the *lower bound* of `target`:

- if `target` exists, that index is where it is (found),
- if not, that index is the insertion point (the first element bigger than it).

This is **Template A** ([1.0](pattern-primer.md)) with predicate $P(i) = nums[i] \ge target$ over the *inclusive* range $[0, n]$, where index `n` represents "beyond the end" (always true — an imaginary sentinel). The loop converges to the first true, and returning it answers both cases with one mechanism.

The subtle bit: the search space must include `n` (append case). That's why `right` starts at `nums.size`, not `nums.size - 1`.

## Approach 1 — Linear scan

Walk the array until `nums[i] >= target`. $O(n)$. Fine for $n \le 10^4$, but this problem is the canonical *building block* (it's the insertion step of binary insertion sort, and the find step of many range structures), so the $O(\log n)$ version is the one that matters.

## Approach 2 — Lower-bound binary search (optimal)

```kotlin
/**
 * @param nums   the sorted array of distinct integers
 * @param target the value to find or insert
 * @return       the index of target if present, else the index where it would be inserted
 */
fun searchInsert(nums: IntArray, target: Int): Int {
    var start = 0
    var end = nums.size            // EXCLUSIVE upper bound: index n is the "append" sentinel

    while (start < end) {
        val mid = start + (end - start) / 2
        when {
            nums[mid] == target -> return mid
            nums[mid] > target  -> end = mid        // target would go before mid
            else                -> start = mid + 1  // target goes after mid
        }
    }
    return start
}
```

```java
public class SearchInsertPosition {
    /**
     * @param nums   the sorted array of distinct integers
     * @param target the value to find or insert
     * @return       the index of target if present, else the index where it would be inserted
     */
    public int searchInsert(int[] nums, int target) {
        int start = 0, end = nums.length;           // end is exclusive (append sentinel)
        while (start < end) {
            int mid = start + (end - start) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] > target) end = mid;
            else start = mid + 1;
        }
        return start;
    }
}
```

```cpp
#include <vector>

class SearchInsertPosition {
public:
    /**
     * @param nums   the sorted array of distinct integers
     * @param target the value to find or insert
     * @return       the index of target if present, else the index where it would be inserted
     */
    int searchInsert(const std::vector<int>& nums, int target) {
        int start = 0, end = (int)nums.size();      // end is exclusive
        while (start < end) {
            int mid = start + (end - start) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] > target) end = mid;
            else start = mid + 1;
        }
        return start;
    }
};
```

```python
def search_insert(nums: list[int], target: int) -> int:
    """
    @param nums:   the sorted array of distinct integers
    @param target: the value to find or insert
    @return:       the index of target if present, else the index where it would be inserted
    """
    start, end = 0, len(nums)         # end is exclusive (append sentinel)
    while start < end:
        mid = start + (end - start) // 2
        if nums[mid] == target:
            return mid
        if nums[mid] > target:
            end = mid
        else:
            start = mid + 1
    return start
```

```rust
impl Solution {
    /// @param nums   the sorted array of distinct integers
    /// @param target the value to find or insert
    /// @return       the index of target if present, else the index where it would be inserted
    pub fn search_insert(nums: Vec<i32>, target: i32) -> i32 {
        let (mut start, mut end) = (0usize, nums.len());   // end is exclusive
        while start < end {
            let mid = start + (end - start) / 2;
            if nums[mid] == target {
                return mid as i32;
            }
            if nums[mid] > target {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        start as i32
    }
}
```

## Dry run

**Input:** `nums = [1, 3, 5, 6]`, `target = 2`

```
start=0  end=4  mid=2  nums[2]=5 > 2 -> end=2
start=0  end=2  mid=1  nums[1]=3 > 2 -> end=1
start=0  end=1  mid=0  nums[0]=1 < 2 -> start=1
start=1  end=1  -> return 1 ✓   (between 1 and 3)
```

**Input:** `nums = [1, 3, 5, 6]`, `target = 7`

```
start=0  end=4  mid=2  nums[2]=5 < 7 -> start=3
start=3  end=4  mid=3  nums[3]=6 < 7 -> start=4
start=4  end=4  -> return 4 ✓   (append; the sentinel index was reached without ever reading out of bounds)
```

**Input:** `nums = [1, 3, 5, 6]`, `target = 0`

```
start=0  end=4  mid=2  nums[2]=5 > 0 -> end=2
start=0  end=2  mid=1  nums[1]=3 > 0 -> end=1
start=0  end=1  mid=0  nums[0]=1 > 0 -> end=0
start=0  end=0  -> return 0 ✓   (prepend)
```

Notice the loop *never reads* `nums[end]` — that's what makes the exclusive bound safe: the append case (`target=7`) terminates with `start == end == 4` before any out-of-bounds access.

## Complexity

**Time.**

$$
T(n) = \lceil \log_2(n+1) \rceil = O(\log n)
$$

**Space.** $O(1)$.

## Variants & follow-ups

- **Find First And Last Position** ([1.3](find-first-and-last-position.md)) — two lower-bound searches (one for `>= target`, one for `> target`), same machinery.
- **Kth Missing Positive Number** ([1.11](kth-missing-positive-number.md)) — a lower bound on a *derived* array (`arr[i] - i - 1` = missing count).
- **Interview follow-up:** "Change the code to find the *last* index where `nums[i] <= target`." Swap the branches (`start = mid` when true, with care for the infinite-loop trap) — the mirror-image of this page. Knowing both directions = owning the pattern.
- **Interview follow-up:** "Why doesn't `end` ever go below `start`?" The exclusive bound + `start = mid + 1` guarantee strict progress; the only way to loop forever would be `start = mid` with `end = start + 1`, which this template never does.
