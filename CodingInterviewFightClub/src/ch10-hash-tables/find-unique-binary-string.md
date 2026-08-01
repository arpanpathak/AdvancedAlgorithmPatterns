# 10.28 Find Unique Binary String

> **Source**: [`src/main/kotlin/string/FindUniqueBinaryString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/FindUniqueBinaryString.kt)
> **Pattern**: Cantor diagonal · **Core page**

## The Problem

A binary string of length n **not** among `nums` (n given strings).

- Constraints: n ≤ 16.

## Examples

```
Input:  nums = ["01","10"]   -> Output: "00"  (or "11")
Input:  nums = ["00","01"]   -> Output: "10"  (or "11")
```

## Intuition — the diagonal: flip the i-th char of the i-th string

There are 2ⁿ strings but only n given — the **diagonalization** builds one guaranteed-missing string: `result[i] = flip(nums[i][i])`:

```kotlin
val n = nums.size
val sb = StringBuilder()

for (i in 0 until n) {
    sb.append(if (nums[i][i] == '0') '1' else '0')
}
return sb.toString()
```

**Why is it guaranteed unique?** If `result` equaled `nums[k]`, then at position k: `result[k] = flip(nums[k][k]) ≠ nums[k][k]` — contradiction. The [12.x](../ch12-backtracking/pattern-primer.md) enumeration-proof pattern without any search.

## Approach 1 — Set membership + brute force (backtracking)

Generate candidates until one isn't in the set: correct, heavier.

## Approach 2 — Cantor diagonal (the repo's version, optimal)

```kotlin
class FindUniqueBinaryString {
    /**
     * @param nums n binary strings of length n
     * @return     a binary string not in nums
     */
    fun findDifferentBinaryString(nums: Array<String>): String {
        val n = nums.size
        val sb = StringBuilder()

        for (i in 0 until n) {
            sb.append(if (nums[i][i] == '0') '1' else '0')
        }
        return sb.toString()
    }
}
```

```java
public class FindUniqueBinaryString {
    /**
     * @param nums n binary strings of length n
     * @return     a binary string not in nums
     */
    public String findDifferentBinaryString(String[] nums) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < nums.length; i++) {
            sb.append(nums[i].charAt(i) == '0' ? '1' : '0');
        }
        return sb.toString();
    }
}
```

```cpp
#include <string>
#include <vector>

class FindUniqueBinaryString {
public:
    /**
     * @param nums n binary strings of length n
     * @return     a binary string not in nums
     */
    std::string findDifferentBinaryString(std::vector<std::string>& nums) {
        std::string result;

        for (int i = 0; i < (int)nums.size(); i++) {
            result += nums[i][i] == '0' ? '1' : '0';
        }
        return result;
    }
};
```

```python
def find_different_binary_string(nums: list[str]) -> str:
    """
    @param nums: n binary strings of length n
    @return:     a binary string not in nums
    """
    return "".join("1" if nums[i][i] == "0" else "0" for i in range(len(nums)))
```

```rust
impl Solution {
    /// @param nums n binary strings of length n
    /// @return     a binary string not in nums
    pub fn find_different_binary_string(nums: Vec<String>) -> String {
        nums.iter()
            .enumerate()
            .map(|(i, s)| if s.as_bytes()[i] == b'0' { '1' } else { '0' })
            .collect()
    }
}
```

## Dry run

**Input:** `nums = ["01","10"]`.

```
i=0: nums[0][0]='0' -> '1'.  i=1: nums[1][1]='0' -> '1'.
Output: "11" ✓  (not in {"01","10"})
```

## Complexity

**Time.** n flips:

$$
T(n) = O(n)
$$

**Space.** The result:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does the diagonal always work?" The pigeonhole principle: n strings can't cover 2ⁿ — but the diagonal goes further, *constructing* the missing one by disagreeing with each given string at its own diagonal position (Cantor's argument).
