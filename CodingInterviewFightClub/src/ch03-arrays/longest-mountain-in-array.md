# 3.41 Longest Mountain In Array

> **Source**: [`src/main/kotlin/array/twopointer/LongestMountainInArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/twopointer/LongestMountainInArray.kt)
> **Pattern**: peak-expansion scan · **Core page**

## The Problem

The longest "mountain" (strict up then strict down), or 0.

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  arr = [2,1,4,7,3,2,5]   -> Output: 5  (1,4,7,3,2)
```

## Intuition — find peaks, expand both sides

A peak has `arr[i-1] < arr[i] > arr[i+1]` — expand left/right while strictly decreasing from the peak:

```kotlin
var maxLength = 0
var i = 1

while (i < arr.size - 1) {
    if (arr[i - 1] < arr[i] && arr[i] > arr[i + 1]) {
        var left = i - 1
        var right = i + 1

        while (left > 0 && arr[left - 1] < arr[left]) left--
        while (right < arr.size - 1 && arr[right] > arr[right + 1]) right++

        maxLength = maxOf(maxLength, right - left + 1)
        i = right
    } else {
        i++
    }
}
return maxLength
```

## Approach 1 — Peak expansion (the repo's version, optimal)

```kotlin
class LongestMountainInArray {
    /**
     * @param arr input array
     * @return    longest mountain length
     */
    fun longestMountain(arr: IntArray): Int {
        var maxLength = 0
        var i = 1

        while (i < arr.size - 1) {
            if (arr[i - 1] < arr[i] && arr[i] > arr[i + 1]) {
                var left = i - 1
                var right = i + 1

                while (left > 0 && arr[left - 1] < arr[left]) left--
                while (right < arr.size - 1 && arr[right] > arr[right + 1]) right++

                maxLength = maxOf(maxLength, right - left + 1)
                i = right
            } else {
                i++
            }
        }
        return maxLength
    }
}
```

```java
public class LongestMountainInArray {
    /**
     * @param arr input array
     * @return    longest mountain length
     */
    public int longestMountain(int[] arr) {
        int best = 0, i = 1;

        while (i < arr.length - 1) {
            if (arr[i - 1] < arr[i] && arr[i] > arr[i + 1]) {
                int left = i - 1, right = i + 1;

                while (left > 0 && arr[left - 1] < arr[left]) left--;
                while (right < arr.length - 1 && arr[right] > arr[right + 1]) right++;

                best = Math.max(best, right - left + 1);
                i = right;
            } else {
                i++;
            }
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class LongestMountainInArray {
public:
    /**
     * @param arr input array
     * @return    longest mountain length
     */
    int longestMountain(std::vector<int>& arr) {
        int best = 0, i = 1;

        while (i < (int)arr.size() - 1) {
            if (arr[i - 1] < arr[i] && arr[i] > arr[i + 1]) {
                int left = i - 1, right = i + 1;

                while (left > 0 && arr[left - 1] < arr[left]) left--;
                while (right < (int)arr.size() - 1 && arr[right] > arr[right + 1]) right++;

                best = std::max(best, right - left + 1);
                i = right;
            } else {
                i++;
            }
        }
        return best;
    }
};
```

```python
def longest_mountain(arr: list[int]) -> int:
    """
    @param arr: input array
    @return:    longest mountain length
    """
    best = 0
    i = 1

    while i < len(arr) - 1:
        if arr[i - 1] < arr[i] and arr[i] > arr[i + 1]:
            left, right = i - 1, i + 1

            while left > 0 and arr[left - 1] < arr[left]:
                left -= 1
            while right < len(arr) - 1 and arr[right] > arr[right + 1]:
                right += 1

            best = max(best, right - left + 1)
            i = right
        else:
            i += 1

    return best
```

```rust
impl Solution {
    /// @param arr input array
    /// @return    longest mountain length
    pub fn longest_mountain(arr: Vec<i32>) -> i32 {
        let n = arr.len();
        let mut best = 0;
        let mut i = 1;

        while i + 1 < n {
            if arr[i - 1] < arr[i] && arr[i] > arr[i + 1] {
                let (mut left, mut right) = (i - 1, i + 1);

                while left > 0 && arr[left - 1] < arr[left] { left -= 1; }
                while right + 1 < n && arr[right] > arr[right + 1] { right += 1; }

                best = best.max(right - left + 1);
                i = right;
            } else {
                i += 1;
            }
        }
        best as i32
    }
}
```

## Dry run

**Input:** `arr = [2,1,4,7,3,2,5]`.

```
i=1: 2>1? no (1<2? no).  i=2: arr[1]=1 < 4 < 7 > 3: peak at 4.
  left: 1<4? expand to index 1 (1): arr[0]=2 > 1 stop.  left=1.
  right: 7>3 expand to 5? arr[3]=7 > arr[4]=3 -> 4; arr[4]=3 > arr[5]=2 -> 5; arr[5]=2 < arr[6]=5 stop.
  best = 5-1+1 = 5.  i=5.
i=5: arr[5]=2 < arr[6]=5? peak? arr[4]=3 > 2 no.  i=6 done.
Output: 5 ✓
```

## Complexity

**Time.** Each element visited once (skips):

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Peak Index In A Mountain Array** — the single-peak special case.
- **Interview follow-up:** "Why skip to `right` after a mountain?" The next peak can't be inside the just-measured mountain (its slopes are strictly monotone) — the `i = right` jump keeps the scan linear.
