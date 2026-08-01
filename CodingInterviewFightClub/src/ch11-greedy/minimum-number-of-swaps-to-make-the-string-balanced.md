# 11.30 Minimum Number Of Swaps To Make The String Balanced

> **Source**: [`src/main/kotlin/array/greedy/MinimumNumberofSwapstoMaketheStringBalanced.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/greedy/MinimumNumberofSwapstoMaketheStringBalanced.kt)
> **Pattern**: imbalance counting · **Core page**

## The Problem

Min swaps (any two chars) to balance `[`/`]` (n/2 of each).

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  s = "][]["   -> Output: 1
Input:  s = "]]][[[" -> Output: 2
```

## Intuition — track the max imbalance; each swap fixes two brackets

Scan; the running `]`-excess peaks at the max imbalance; each swap removes 2 of it:

```kotlin
var imbalance = 0
var maxImbalance = 0

for (char in s) {
    if (char == '[') imbalance--
    else imbalance++

    maxImbalance = maxOf(maxImbalance, imbalance)
}
return (maxImbalance + 1) / 2
```

## Approach 1 — Imbalance counting (the repo's version, optimal)

```kotlin
class MinimumNumberofSwapstoMaketheStringBalanced {
    /**
     * @param s bracket string
     * @return   min swaps
     */
    fun minSwaps(s: String): Int {
        var imbalance = 0
        var maxImbalance = 0

        for (char in s) {
            if (char == '[') imbalance--
            else imbalance++

            maxImbalance = maxOf(maxImbalance, imbalance)
        }
        return (maxImbalance + 1) / 2
    }
}
```

```java
public class MinimumNumberOfSwapsToMakeTheStringBalanced {
    /**
     * @param s bracket string
     * @return   min swaps
     */
    public int minSwaps(String s) {
        int imbalance = 0, maxImbalance = 0;

        for (char c : s.toCharArray()) {
            if (c == '[') imbalance--;
            else imbalance++;

            maxImbalance = Math.max(maxImbalance, imbalance);
        }
        return (maxImbalance + 1) / 2;
    }
}
```

```cpp
#include <string>
#include <algorithm>

class MinimumNumberOfSwapsToMakeTheStringBalanced {
public:
    /**
     * @param s bracket string
     * @return   min swaps
     */
    int minSwaps(std::string s) {
        int imbalance = 0, maxImbalance = 0;

        for (char c : s) {
            if (c == '[') imbalance--;
            else imbalance++;

            maxImbalance = std::max(maxImbalance, imbalance);
        }
        return (maxImbalance + 1) / 2;
    }
};
```

```python
def min_swaps(s: str) -> int:
    """
    @param s: bracket string
    @return:  min swaps
    """
    imbalance = max_imbalance = 0

    for ch in s:
        imbalance += 1 if ch == "]" else -1
        max_imbalance = max(max_imbalance, imbalance)

    return (max_imbalance + 1) // 2
```

```rust
impl Solution {
    /// @param s bracket string
    /// @return   min swaps
    pub fn min_swaps(s: String) -> i32 {
        let (mut imbalance, mut max_imbalance) = (0, 0);

        for c in s.chars() {
            imbalance += if c == ']' { 1 } else { -1 };
            max_imbalance = max_imbalance.max(imbalance);
        }
        (max_imbalance + 1) / 2
    }
}
```

## Dry run

**Input:** `s = "]]][[["`.

```
]: 1.  ]: 2.  ]: 3 (max).  [: 2.  [: 1.  [: 0.
(3+1)/2 = 2 ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does one swap fix two excesses?" Swapping a stray `]` with a trailing `[` corrects both — halving the peak imbalance gives the exact count.
