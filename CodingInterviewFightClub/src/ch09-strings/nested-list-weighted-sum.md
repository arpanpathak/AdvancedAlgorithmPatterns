# 9.37 Nested List Weighted Sum

> **Source**: [`src/main/kotlin/array/dfs/NestedListWeightedSum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dfs/NestedListWeightedSum.kt)
> **Pattern**: depth-weighted recursion · **Core page**

## The Problem

Sum of `value × depth` over a nested list (depth starts at 1).

- Constraints: depth ≤ 50.

## Examples

```
Input:  [[1,1],2,[1,1]]   -> Output: 10   (1×1×2 + 2×1 + 1×1×2)
```

## Intuition — DFS carrying the depth

```kotlin
fun dfs(nested: List<NestedInteger>, depth: Int): Int {
    var sum = 0

    for (element in nested) {
        if (element.isInteger()) {
            sum += element.getInteger() * depth
        } else {
            sum += dfs(element.getList(), depth + 1)
        }
    }
    return sum
}
return dfs(nestedList, 1)
```

## Approach 1 — Depth DFS (the repo's version, optimal)

```kotlin
class NestedListWeightedSum {
    /**
     * @param nestedList nested integers
     * @return           depth-weighted sum
     */
    fun depthSum(nestedList: List<NestedInteger>): Int {
        fun dfs(nested: List<NestedInteger>, depth: Int): Int {
            var sum = 0

            for (element in nested) {
                if (element.isInteger()) {
                    sum += element.getInteger() * depth
                } else {
                    sum += dfs(element.getList(), depth + 1)
                }
            }
            return sum
        }

        return dfs(nestedList, 1)
    }
}
```

```java
public class NestedListWeightedSum {
    private int dfs(List<NestedInteger> list, int depth) {
        int sum = 0;

        for (NestedInteger element : list) {
            sum += element.isInteger()
                ? element.getInteger() * depth
                : dfs(element.getList(), depth + 1);
        }
        return sum;
    }

    /**
     * @param nestedList nested integers
     * @return           depth-weighted sum
     */
    public int depthSum(List<NestedInteger> nestedList) {
        return dfs(nestedList, 1);
    }
}
```

```cpp
#include <vector>

class NestedListWeightedSum {
    int dfs(std::vector<NestedInteger>& list, int depth) {
        int sum = 0;

        for (auto& element : list) {
            sum += element.isInteger()
                ? element.getInteger() * depth
                : dfs(element.getList(), depth + 1);
        }
        return sum;
    }

public:
    /**
     * @param nestedList nested integers
     * @return           depth-weighted sum
     */
    int depthSum(std::vector<NestedInteger>& nestedList) {
        return dfs(nestedList, 1);
    }
};
```

```python
def depth_sum(nested_list: list) -> int:
    """
    @param nested_list: nested integers
    @return:            depth-weighted sum
    """

    def dfs(nested: list, depth: int) -> int:
        total = 0

        for element in nested:
            if element.isInteger():
                total += element.getInteger() * depth
            else:
                total += dfs(element.getList(), depth + 1)

        return total

    return dfs(nested_list, 1)
```

```rust
impl Solution {
    /// @param nested_list nested integers
    /// @return            depth-weighted sum
    pub fn depth_sum(nested_list: Vec<NestedInteger>) -> i32 {
        fn dfs(nested: &Vec<NestedInteger>, depth: i32) -> i32 {
            nested.iter().map(|e| {
                if e.is_integer() { e.get_integer() * depth }
                else { dfs(&e.get_list(), depth + 1) }
            }).sum()
        }
        dfs(&nested_list, 1)
    }
}
```

## Dry run

**Input:** `[[1,1],2,[1,1]]`.

```
depth 1: [1,1]: 1*2 + 1*2 = 4.  2: 2*1 = 2.  [1,1]: 4.
Output: 10 ✓
```

## Complexity

**Time.** Elements once:

$$
T(n) = O(n)
$$

**Space.** Recursion depth:

$$
S(n) = O(d)
$$

## Variants & follow-ups

- **Flatten Nested List Iterator** ([18.5](../ch18-design-caches/flatten-nested-list-iterator.md)) — the iterator sibling.
- **Interview follow-up:** "How would depth-sum (reverse) work?" Multiply by (maxDepth − depth + 1) — a two-pass problem (find max depth, then weight).
