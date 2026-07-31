# 14.4 Largest Number

> **Source:** [`src/main/kotlin/sorting/LargestNumber.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sorting/LargestNumber.kt)
> **Pattern:** custom comparator · **Core page**

## The Problem

Given a list of non-negative integers, arrange them to form the **largest possible number** (returned as a string). The result may be huge — that's why it's a string.

- Constraints: $1 \le n \le 100$; values ≤ $10^9$.

## Examples

```
Input:  nums = [10,2]      -> Output: "210"    (21 > 12)
Input:  nums = [3,30,34,5,9] -> Output: "9534330"
Input:  nums = [0,0]       -> Output: "0"      (not "00")
```

## Intuition — sort by "which order makes the bigger concatenation?"

The core question is a **pairwise rule**: for two numbers `a` and `b`, which goes first — `"a+b"` or `"b+a"`? The rule "larger concatenation first" is a *comparator*, and the array sorted by it is the answer:

```kotlin
strNums.sortWith { a, b -> (b + a).compareTo(a + b) }
```

`b+a > a+b` means `b` should come before `a` — the sort descending by concatenation. The elegant part: **this comparator is transitive** (if `ab ≥ ba` and `bc ≥ cb` then `ac ≥ ca`), so a plain sort with it is correct — no checking pairs after sorting. (The transitivity is the nontrivial fact; interviewers may ask you to justify it — it follows from comparing `a·10^{len(b)} + b` against `b·10^{len(a)} + a`.)

**Why strings and not integers?** The concatenated number can have up to ~1000 digits — far beyond any integer type. Comparing as strings (same length, so lexicographic = numeric) is the only way.

**The all-zeros trap:** `[0,0]` sorted produces `"00"`, but the answer is `"0"`. The repo's check — if the *first* sorted element is `"0"`, every element is `"0"` (since `"0"` is the largest... wait, is it?) — let me think: with the comparator, `"0"` sorts last unless everything is `"0"`. Hmm — actually with descending-by-concatenation, `"0"` comes *after* any positive number (`"a0" > "0a"` for `a > 0`), so the first element is `"0"` only if all are `"0"`. So `strNums[0] == "0"` ⟺ all zeros → return `"0"`. One check handles the whole class.

## Approach 1 — Generate all permutations (too slow)

$n!$ orderings at $n = 100$ — the reason this problem is "sort with a custom comparator", not "brute force".

## Approach 2 — Custom-comparator sort (the repo's version, optimal)

```kotlin
class LargestNumber {
    /**
     * @param nums non-negative integers
     * @return     the largest number formed by arranging them, as a string
     */
    fun largestNumber(nums: IntArray): String {
        val strNums = nums.map { it.toString() }.toTypedArray()

        // Sort descending by concatenation: "b+a" > "a+b" means b first
        strNums.sortWith { a, b -> (b + a).compareTo(a + b) }

        // If the largest number is "0", return "0"  (all elements were zero)
        if (strNums[0] == "0") {
            return "0"
        }
        return strNums.joinToString("")
    }
}
```

```java
import java.util.*;

public class LargestNumber {
    /**
     * @param nums non-negative integers
     * @return     the largest number formed by arranging them, as a string
     */
    public String largestNumber(int[] nums) {
        String[] strs = new String[nums.length];
        for (int i = 0; i < nums.length; i++) strs[i] = String.valueOf(nums[i]);

        // Sort descending by concatenation: "b+a" > "a+b" means b first
        Arrays.sort(strs, (a, b) -> (b + a).compareTo(a + b));

        if (strs[0].equals("0")) return "0";     // all elements were zero
        return String.join("", strs);
    }
}
```

```cpp
#include <algorithm>
#include <string>
#include <vector>

class LargestNumber {
public:
    /**
     * @param nums non-negative integers
     * @return     the largest number formed by arranging them, as a string
     */
    std::string largestNumber(std::vector<int>& nums) {
        std::vector<std::string> strs;
        for (int x : nums) strs.push_back(std::to_string(x));

        // Sort descending by concatenation: "b+a" > "a+b" means b first
        std::sort(strs.begin(), strs.end(),
                  [](const std::string& a, const std::string& b) { return a + b > b + a; });

        if (strs[0] == "0") return "0";         // all elements were zero
        std::string result;
        for (auto& s : strs) result += s;
        return result;
    }
};
```

```python
def largest_number(nums: list[int]) -> str:
    """
    @param nums: non-negative integers
    @return:     the largest number formed by arranging them, as a string
    """
    from functools import cmp_to_key

    strs = [str(x) for x in nums]
    # Sort descending by concatenation: "b+a" > "a+b" means b first
    strs.sort(key=cmp_to_key(lambda a, b: -1 if a + b > b + a else 1 if a + b < b + a else 0))

    if strs[0] == "0":
        return "0"                               # all elements were zero
    return "".join(strs)
```

```rust
impl Solution {
    /// @param nums non-negative integers
    /// @return     the largest number formed by arranging them, as a string
    pub fn largest_number(nums: Vec<i32>) -> String {
        let mut strs: Vec<String> = nums.iter().map(|x| x.to_string()).collect();

        // Sort descending by concatenation: "b+a" > "a+b" means b first
        strs.sort_by(|a, b| {
            let ab = format!("{a}{b}");
            let ba = format!("{b}{a}");
            ba.cmp(&ab)
        });

        if strs[0] == "0" { return "0".to_string(); }   // all elements were zero
        strs.concat()
    }
}
```

## Dry run

**Input:** `nums = [3,30,34,5,9]`.

```
as strings: ["3","30","34","5","9"]

sort with comparator (b+a vs a+b):
  compare "3","30": "330" vs "303" -> "330" > "303" -> "3" before "30"
  compare "3","34": "343" vs "334" -> "343" > "334" -> "34" before "3"
  ...full order: ["9","5","34","3","30"]

join: "9" + "5" + "34" + "3" + "30" = "9534330" ✓
```

The pairwise rule drives the whole order: `"3"` vs `"30"` — `330 > 303`, so `3` leads, giving `"330"` not `"303"`. Every adjacent pair in the final order obeys the same rule, and by transitivity the global arrangement is optimal — no post-sort pair checking needed.

## Complexity

**Time.** Sort with $O(1)$ string-compare each:

$$
T(n) = O(n \log n) \cdot O(L), \quad L = \text{max digit length}
$$

**Space.** The string array:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Custom-comparator sort family** — "arrange to maximize/minimize X" problems are almost always this pattern: define the pairwise rule, sort by it, justify transitivity.
- **Smallest Number (mirror)** — flip the comparator (`a+b < b+a`): same skeleton, opposite goal.
- **Interview follow-up:** "Why does sorting by a pairwise rule give the *global* optimum?" Because the concatenation order is transitive: if `a` must precede `b` and `b` must precede `c`, then `a` must precede `c` — so the pairwise rule is a total order and the sort result is optimal. Proving transitivity is the rigorous part (it follows from the numeric interpretation `a·10^len(b) + b`); stating it unprompted is the depth signal.
