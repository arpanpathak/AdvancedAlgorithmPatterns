# 4.6 Find The Duplicate Number

> **Source:** [`src/main/kotlin/array/cycle/FindTheDuplicateNumber.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/cycle/FindTheDuplicateNumber.kt)
> **Pattern:** Floyd's cycle on an implicit graph · **Core page**

## The Problem

Given `nums` of length `n + 1` where every value is in `[1, n]`, exactly **one number repeats** (any number of times). Find it, **without modifying the array**, in O(1) extra space.

- Constraints: $1 \le n \le 10^5$; exactly one duplicate.

## Examples

```
Input:  nums = [1,3,4,2,2]   -> Output: 2
Input:  nums = [3,1,3,4,2]   -> Output: 3
```

## Intuition — `nums[i]` is a *pointer*, and the duplicate is a cycle entry

The constraints are the giveaway: values in `[1, n]` and indices `[0, n]` mean **`nums[i]` is a valid index** — so `i -> nums[i]` defines a functional graph. Since one value repeats, **two different indices point to the same value** — and that value is the *entry of a cycle* in this graph. Finding the duplicate = finding the cycle entry, which is exactly **Floyd's algorithm** from [4.2](linked-list-cycle.md)/[4.5](linked-list-cycle-ii.md), applied to array indexing instead of list pointers:

```
slow = nums[0]; fast = nums[0]
phase 1: slow = nums[slow]; fast = nums[nums[fast]]   // meet inside the cycle
phase 2: slow = nums[0]; walk both one step until equal  // the meeting point = cycle entry
```

**Why is the cycle entry the duplicate?** Index 0 is never pointed to (values are ≥ 1), so the walk from 0 must eventually enter a cycle. The entry node `x` is pointed to by two different nodes (that's what makes it a cycle entry on a functional graph with a duplicate value) — and `x` is a *value* in the array: the duplicate. Floyd's two phases find it in O(1) space with no mutation.

**Why can't we use a hash set?** The O(1)-space constraint bans it (and mutation bans sorting). Floyd is the answer the constraints are engineered for — the same "implicit graph + Floyd" trick as [1.17](../ch01-binary-search/search-in-rotated-sorted-array.md)'s cousins.

## Approach 1 — Hash set / sort (space or mutation)

Mark seen values (O(n) space) or sort (mutates): both violate the constraints.

## Approach 2 — Floyd on the implicit graph (the repo's version, optimal)

```kotlin
class FindTheDuplicateNumber {
    /**
     * @param nums length n+1, values in [1, n], exactly one duplicate
     * @return     the duplicate value
     */
    fun findDuplicate(nums: IntArray): Int {
        var slow = nums[0]
        var fast = nums[0]

        // Phase 1: Detect intersection point (inside the cycle)
        while (true) {
            slow = nums[slow]
            fast = nums[nums[fast]]
            if (slow == fast) break
        }

        // Phase 2: Find the entry to the cycle (the duplicate value)
        slow = nums[0]
        while (slow != fast) {
            slow = nums[slow]
            fast = nums[fast]
        }
        return slow
    }
}
```

```java
public class FindTheDuplicateNumber {
    /**
     * @param nums length n+1, values in [1, n], exactly one duplicate
     * @return     the duplicate value
     */
    public int findDuplicate(int[] nums) {
        int slow = nums[0], fast = nums[0];

        while (true) {                        // phase 1: meet inside the cycle
            slow = nums[slow];
            fast = nums[nums[fast]];
            if (slow == fast) break;
        }

        slow = nums[0];                       // phase 2: walk to the cycle entry
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;
    }
}
```

```cpp
#include <vector>

class FindTheDuplicateNumber {
public:
    /**
     * @param nums length n+1, values in [1, n], exactly one duplicate
     * @return     the duplicate value
     */
    int findDuplicate(std::vector<int>& nums) {
        int slow = nums[0], fast = nums[0];

        while (true) {                        // phase 1: meet inside the cycle
            slow = nums[slow];
            fast = nums[nums[fast]];
            if (slow == fast) break;
        }

        slow = nums[0];                       // phase 2: walk to the cycle entry
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;
    }
};
```

```python
def find_duplicate(nums: list[int]) -> int:
    """
    @param nums: length n+1, values in [1, n], exactly one duplicate
    @return:     the duplicate value
    """
    slow = fast = nums[0]

    while True:                            # phase 1: meet inside the cycle
        slow = nums[slow]
        fast = nums[nums[fast]]
        if slow == fast:
            break

    slow = nums[0]                         # phase 2: walk to the cycle entry
    while slow != fast:
        slow = nums[slow]
        fast = nums[fast]
    return slow
```

```rust
impl Solution {
    /// @param nums length n+1, values in [1, n], exactly one duplicate
    /// @return     the duplicate value
    pub fn find_duplicate(nums: Vec<i32>) -> i32 {
        let mut slow = nums[0] as usize;
        let mut fast = nums[0] as usize;

        loop {                             // phase 1: meet inside the cycle
            slow = nums[slow] as usize;
            fast = nums[nums[fast] as usize] as usize;
            if slow == fast { break; }
        }

        slow = nums[0] as usize;           // phase 2: walk to the cycle entry
        while slow != fast {
            slow = nums[slow] as usize;
            fast = nums[fast] as usize;
        }
        slow as i32
    }
}
```

## Dry run

**Input:** `nums = [1,3,4,2,2]` (indices 0..4; the graph is `0->1, 1->3, 2->4, 3->2, 4->2`).

```
phase 1: slow = fast = nums[0] = 1
  slow=nums[1]=3,  fast=nums[nums[1]]=nums[3]=2
  slow=nums[3]=2,  fast=nums[nums[2]]=nums[4]=2   -> meet at 2 (inside the cycle)
phase 2: slow = nums[0] = 1, fast stays 2
  slow=nums[1]=3,  fast=nums[2]=2
  slow=nums[3]=2,  fast=nums[2]=2                 -> meet at 2

Output: 2 ✓
```

The graph view: `0 -> 1 -> 3 -> 2 -> 4 -> 2` — the arrow `4 -> 2` closes the cycle at value 2, and index 3 also points at 2, so value 2 is the duplicate. Phase 1 finds any point on the cycle; phase 2 walks from index 0 and the meeting point at equal speed, meeting exactly at the cycle entry — the duplicated value.

## Complexity

**Time.** Two linear walks:

$$
T(n) = O(n)
$$

**Space.** Two variables (no mutation, no set):

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Linked List Cycle II** ([4.5](linked-list-cycle-ii.md)) — the exact same two phases over *pointer* traversal; this page is the array-indexed twin.
- **Binary-search alternative** — count elements ≤ mid: O(n log n), also valid (no mutation) — the "counting beats Floyd" contrast when the duplicate is dense.
- **Interview follow-up:** "Why does the meeting point of phase 1 not directly give the answer?" It's *some* node inside the cycle — the duplicate is the cycle's *entry*, which the equal-speed phase-2 walk isolates. The two phases are the whole algorithm: detect a cycle member, then hunt the entry.
