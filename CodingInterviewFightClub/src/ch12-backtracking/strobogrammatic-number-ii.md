# 12.17 Strobogrammatic Number II

> **Source**: [`src/main/kotlin/backtracking/Strobogrammatic_Number_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/backtracking/Strobogrammatic_Number_II.kt)
> **Pattern**: mirrored digit pairing · **Core page**

## The Problem

All **length-n** numbers that read the same upside-down (rotated 180°).

- Constraints: n ≤ 14.

## Examples

```
Input:  n = 2   -> Output: ["11","69","88","96"]
```

## Intuition — build from the outside in with the rotation pairs

The rotation pairs: 0↔0, 1↔1, 6↔9, 8↔8, 9↔6. A length-n strobogrammatic has the pair at both ends and a length-(n-2) one inside:

```kotlin
val pairs = listOf("0" to "0", "1" to "1", "6" to "9", "8" to "8", "9" to "6")

fun generateStrobogrammatic(currentLength: Int): List<String> {
    if (currentLength == 0) return listOf("")
    if (currentLength == 1) return listOf("0", "1", "8")

    val result = mutableListOf<String>()

    for ((left, right) in pairs) {
        if (currentLength == n && left == "0") continue    // no leading zero

        for (inner in generateStrobogrammatic(currentLength - 2)) {
            result.add(left + inner + right)
        }
    }
    return result
}
```

**Why the leading-zero skip?** Only the outermost position forbids '0' — inner zeros are fine. The `currentLength == n` check is the outermost frame.

## Approach 1 — Recursive pairing (the repo's version, optimal)

```kotlin
class Strobogrammatic_Number_II {
    /**
     * @param n target length
     * @return  all length-n strobogrammatic numbers
     */
    fun findStrobogrammatic(n: Int): List<String> {
        val pairs = listOf("0" to "0", "1" to "1", "6" to "9", "8" to "8", "9" to "6")

        fun generateStrobogrammatic(currentLength: Int): List<String> {
            if (currentLength == 0) return listOf("")
            if (currentLength == 1) return listOf("0", "1", "8")

            val result = mutableListOf<String>()

            for ((left, right) in pairs) {
                if (currentLength == n && left == "0") continue

                for (inner in generateStrobogrammatic(currentLength - 2)) {
                    result.add(left + inner + right)
                }
            }
            return result
        }

        return generateStrobogrammatic(n)
    }
}
```

```java
import java.util.*;

public class StrobogrammaticNumberII {
    private static final String[][] PAIRS = {
        {"0", "0"}, {"1", "1"}, {"6", "9"}, {"8", "8"}, {"9", "6"}
    };

    private List<String> generate(int n, int current) {
        if (current == 0) return Arrays.asList("");
        if (current == 1) return Arrays.asList("0", "1", "8");

        List<String> result = new ArrayList<>();
        for (String[] pair : PAIRS) {
            if (current == n && pair[0].equals("0")) continue;

            for (String inner : generate(n, current - 2)) {
                result.add(pair[0] + inner + pair[1]);
            }
        }
        return result;
    }

    /**
     * @param n target length
     * @return  all length-n strobogrammatic numbers
     */
    public List<String> findStrobogrammatic(int n) {
        return generate(n, n);
    }
}
```

```cpp
#include <string>
#include <vector>

class StrobogrammaticNumberII {
    std::vector<std::pair<std::string, std::string>> pairs = {
        {"0", "0"}, {"1", "1"}, {"6", "9"}, {"8", "8"}, {"9", "6"}
    };

    std::vector<std::string> generate(int n, int current) {
        if (current == 0) return {""};
        if (current == 1) return {"0", "1", "8"};

        std::vector<std::string> result;
        for (auto& [l, r] : pairs) {
            if (current == n && l == "0") continue;

            for (auto& inner : generate(n, current - 2)) {
                result.push_back(l + inner + r);
            }
        }
        return result;
    }

public:
    /**
     * @param n target length
     * @return  all length-n strobogrammatic numbers
     */
    std::vector<std::string> findStrobogrammatic(int n) {
        return generate(n, n);
    }
};
```

```python
def find_strobogrammatic(n: int) -> list[str]:
    """
    @param n: target length
    @return:  all length-n strobogrammatic numbers
    """
    pairs = [("0", "0"), ("1", "1"), ("6", "9"), ("8", "8"), ("9", "6")]

    def generate(length: int) -> list[str]:
        if length == 0:
            return [""]
        if length == 1:
            return ["0", "1", "8"]

        result = []
        for left, right in pairs:
            if length == n and left == "0":
                continue

            for inner in generate(length - 2):
                result.append(left + inner + right)

        return result

    return generate(n)
```

```rust
impl Solution {
    /// @param n target length
    /// @return  all length-n strobogrammatic numbers
    pub fn find_strobogrammatic(n: i32) -> Vec<String> {
        let pairs = [("0", "0"), ("1", "1"), ("6", "9"), ("8", "8"), ("9", "6")];

        fn generate(n: i32, current: i32, pairs: &[(&str, &str)]) -> Vec<String> {
            if current == 0 { return vec![String::new()]; }
            if current == 1 { return vec!["0".into(), "1".into(), "8".into()]; }

            let mut result = Vec::new();
            for &(l, r) in pairs {
                if current == n && l == "0" { continue; }

                for inner in generate(n, current - 2, pairs) {
                    result.push(format!("{}{}{}", l, inner, r));
                }
            }
            result
        }

        generate(n, n, &pairs)
    }
}
```

## Dry run

**Input:** `n = 2`.

```
generate(2): pairs: ("0","0") skipped (leading zero).  ("1","1"): inner generate(0) = [""] -> "11".
  ("6","9") -> "69".  ("8","8") -> "88".  ("9","6") -> "96".
Output: ["11","69","88","96"] ✓
```

## Complexity

**Time.** O(5^{n/2}) strings:

$$
T(n) = O(5^{n/2})
$$

**Space.** The result:

$$
S(n) = O(5^{n/2})
$$

## Variants & follow-ups

- **Strobogrammatic Number** — the single-number check (I).
- **Interview follow-up:** "Why is the middle base length 1 → 0,1,8?" The center digit must self-rotate — only 0, 1, 8 survive a 180° turn. The length-0 base wraps even lengths; the length-1 base handles odd.
