# 1.19 Single Element In A Sorted Array

> **Source:** [`src/main/kotlin/binarysearch/SingleElementInASortedArray.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/binarysearch/SingleElementInASortedArray.kt)
> **Pattern:** parity-based search · **Gym page**

## The Problem

You are given a **sorted** array where every element appears **exactly twice**, except one element that appears **exactly once**. Find that single element. Must run in $O(\log n)$ and use $O(1)$ space.

- Constraints: $1 \le n \le 10^5$, `n` is odd.

## Examples

```
Input:  nums = [1, 1, 2, 3, 3, 4, 4, 8, 8]
Output: 2

Input:  nums = [3, 3, 7, 7, 10, 11, 11]
Output: 10
```

## Intuition — pairs line up, until they don't

Before the single element, the array reads as **perfect pairs** starting at even indices:

```
index:  0  1  2  3  4  5  6  7  8
nums:   1  1  2  3  3  4  4  8  8
        └─┘  └────┘  └────┘  └────┘   (pairs before the lone 2 at index 2)
               ^
        first pair that is BROKEN = (index 2, 3) -> the lone element is at index 2
```

After the single element, every pair starts at an **odd** index:

```
index:  0  1  2  3  4  5  6
nums:   3  3  7  7  10 11 11
                    └──┘  └──┘        (pairs after the lone 10 at index 4)
```

So the invariant is: **for every index `i` strictly before the lone element, `nums[2i] == nums[2i+1]`; at and after it, the pair structure is shifted by one.** We can test the "healthy pair" property at any even position `e`:

$$
P(e) = (nums[e] == nums[e+1])
$$

$P$ is `true` before the single element and `false` from the single element onward — **monotone**! Find the first `false` (Template A), and the answer is `nums[firstFalse * 2]`... more concretely, with the classic trick: force `mid` to be **even**, then:

- if `nums[mid] == nums[mid + 1]` → pairs intact at `mid` → lone element is to the **right** → `low = mid + 2`,
- else → the lone element is at `mid` or to its **left** → `high = mid`.

Forcing `mid` even (`if (mid % 2 == 1) mid--`) guarantees we only ever compare the *first* member of a potential pair with its partner. The loop converges to the lone element's position.

## Approach 1 — XOR everything

`XOR` of all elements: paired values cancel ($x \oplus x = 0$), leaving the lone value. $O(n)$ time, $O(1)$ space. Correct — and the *fastest constant* — but it doesn't use the sorted structure, so the $O(\log n)$ requirement fails.

## Approach 2 — Parity binary search (optimal)

```kotlin
/**
 * @param nums the sorted array where every value repeats twice except one
 * @return     the value that appears exactly once
 */
fun singleNonDuplicate(nums: IntArray): Int {
    var low = 0
    var high = nums.size - 1

    while (low < high) {
        var mid = low + (high - low) / 2
        // Force mid to be even so we always compare the first element of a pair.
        if (mid % 2 == 1) {
            mid--
        }

        if (nums[mid] == nums[mid + 1]) {
            low = mid + 2      // pair intact -> lone element is to the right
        } else {
            high = mid         // pair broken -> lone element is at or left of mid
        }
    }
    return nums[low]
}
```

```java
public class SingleElementInSortedArray {
    /**
     * @param nums the sorted array where every value repeats twice except one
     * @return     the value that appears exactly once
     */
    public int singleNonDuplicate(int[] nums) {
        int low = 0, high = nums.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (mid % 2 == 1) mid--;               // make mid even
            if (nums[mid] == nums[mid + 1]) {
                low = mid + 2;                     // pair intact -> right side
            } else {
                high = mid;                        // pair broken -> at or left
            }
        }
        return nums[low];
    }
}
```

```cpp
#include <vector>

class SingleElementInSortedArray {
public:
    /**
     * @param nums the sorted array where every value repeats twice except one
     * @return     the value that appears exactly once
     */
    int singleNonDuplicate(const std::vector<int>& nums) {
        int low = 0, high = (int)nums.size() - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (mid % 2 == 1) mid--;
            if (nums[mid] == nums[mid + 1]) low = mid + 2;
            else high = mid;
        }
        return nums[low];
    }
};
```

```python
def single_non_duplicate(nums: list[int]) -> int:
    """
    @param nums: the sorted array where every value repeats twice except one
    @return:     the value that appears exactly once
    """
    low, high = 0, len(nums) - 1
    while low < high:
        mid = low + (high - low) // 2
        if mid % 2 == 1:
            mid -= 1                       # make mid even
        if nums[mid] == nums[mid + 1]:
            low = mid + 2                  # pair intact -> right side
        else:
            high = mid                     # pair broken -> at or left
    return nums[low]
```

```rust
impl Solution {
    /// @param nums the sorted array where every value repeats twice except one
    /// @return     the value that appears exactly once
    pub fn single_non_duplicate(nums: Vec<i32>) -> i32 {
        let (mut low, mut high) = (0usize, nums.len() - 1);
        while low < high {
            let mut mid = low + (high - low) / 2;
            if mid % 2 == 1 {
                mid -= 1;                  // make mid even
            }
            if nums[mid] == nums[mid + 1] {
                low = mid + 2;             // pair intact -> right side
            } else {
                high = mid;                // pair broken -> at or left
            }
        }
        nums[low]
    }
}
```

## Dry run

**Input:** `nums = [1, 1, 2, 3, 3, 4, 4, 8, 8]`

```
low=0  high=8  mid=4  even -> compare nums[4]=3 vs nums[5]=4  -> 3 != 4 BROKEN -> high=4
low=0  high=4  mid=2  even -> compare nums[2]=2 vs nums[3]=3  -> 2 != 3 BROKEN -> high=2
low=0  high=2  mid=1  odd -> mid=0 -> compare nums[0]=1 vs nums[1]=1 -> 1 == 1 INTACT -> low=2
low=2  high=2  -> return nums[2] = 2 ✓
```

Every broken pair tells us the lone element is at `mid` or to its left; every intact pair pushes us right. The parity trick (`mid--` when odd) means we *always* compare the left member of a pair with its partner — without it, comparing `nums[odd]` with `nums[odd+1]` would be comparing *across* a boundary (two different pairs), and the test would be meaningless.

**Input:** `nums = [3, 3, 7, 7, 10, 11, 11]`

```
low=0  high=6  mid=3  odd -> mid=2 -> compare nums[2]=7 vs nums[3]=7 -> INTACT -> low=4
low=4  high=6  mid=5  odd -> mid=4 -> compare nums[4]=10 vs nums[5]=11 -> BROKEN -> high=4
low=4  high=4  -> return nums[4] = 10 ✓
```

## Complexity

**Time.** Each iteration halves the range; the parity fix is $O(1)$:

$$
T(n) = O(\log n)
$$

**Space.** $O(1)$.

## Variants & follow-ups

- **Find Minimum In Rotated Sorted Array** ([1.5](find-minimum-in-rotated-sorted-array.md)) — same "monotone property on indices" style; here the property is pair health, there it's "which run am I in".
- **XOR approach** — always mention it as the constant-time-optimization alternative when the array needn't be sorted; interviewers love hearing both.
- **Interview follow-up:** "Why must `n` be odd?" Because $n = 2t + 1$: `t` pairs plus the lone element. The parity argument (`mid` even ⇒ pair boundary) relies on it.
- **Interview follow-up:** "Now values can repeat more than twice (e.g. triples)." The pair-parity argument collapses; you'd need a different predicate (compare against `nums[0]` runs) — a good place to *stop* and say "the structure is gone".
