# 10.23 Largest Time For Given Digits

> **Source**: [`src/main/kotlin/microsoft/ValidTime.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/microsoft/ValidTime.kt)
> **Pattern**: permutation search with validity filter · **Core page**

## The Problem

The **largest** valid `HH:MM` from four digits (each used once), or "".

- Constraints: 4 digits 0-9.

## Examples

```
Input:  [1,2,3,4]   -> Output: "23:41"
Input:  [5,5,5,5]   -> Output: ""
```

## Intuition — try all permutations; keep the max valid time

The repo's `ValidTime` counts valid permutations; the classic problem wants the **largest** — same permutation machinery, different reducer:

```kotlin
var best = ""
val seen = BooleanArray(4)

fun backtrack(current: String) {
    if (current.length == 4) {
        val hours = current.substring(0, 2).toInt()
        val minutes = current.substring(2, 4).toInt()
        if (hours in 0..23 && minutes in 0..59 && current > best) best = current
        return
    }
    for (i in digits.indices) {
        if (!seen[i]) { seen[i] = true; backtrack(current + digits[i]); seen[i] = false }
    }
}
```

**Why string comparison for "largest"?** `HHMM` as a 4-digit string — lexicographic order on equal-length strings IS numeric order. The [12.0](../ch12-backtracking/pattern-primer.md) permutation engine with a validity gate.

## Approach 1 — Generate all 4! permutations, filter, max (the repo family, optimal)

```kotlin
class LargestTimeForGivenDigits {
    private val digits = intArrayOf(0, 0, 0, 0)
    private val used = BooleanArray(4)
    private var best = ""

    /**
     * @param arr four digits
     * @return    largest valid HH:MM or ""
     */
    fun largestTimeFromDigits(arr: IntArray): String {
        for (i in 0 until 4) digits[i] = arr[i]
        used.fill(false)
        best = ""
        backtrack("")
        return best
    }

    private fun backtrack(current: String) {
        if (current.length == 4) {
            val hours = current.substring(0, 2).toInt()
            val minutes = current.substring(2, 4).toInt()

            if (hours in 0..23 && minutes in 0..59 && current > best) {
                best = current
            }
            return
        }

        for (i in 0 until 4) {
            if (!used[i]) {
                used[i] = true
                backtrack(current + digits[i])
                used[i] = false
            }
        }
    }
}
```

```java
public class LargestTimeForGivenDigits {
    private int[] digits = new int[4];
    private boolean[] used = new boolean[4];
    private String best = "";

    private void backtrack(String cur) {
        if (cur.length() == 4) {
            int h = Integer.parseInt(cur.substring(0, 2));
            int m = Integer.parseInt(cur.substring(2, 4));

            if (h <= 23 && m <= 59 && cur.compareTo(best) > 0) best = cur;
            return;
        }

        for (int i = 0; i < 4; i++) {
            if (!used[i]) {
                used[i] = true;
                backtrack(cur + digits[i]);
                used[i] = false;
            }
        }
    }

    /**
     * @param arr four digits
     * @return    largest valid HH:MM or ""
     */
    public String largestTimeFromDigits(int[] arr) {
        digits = arr.clone();
        used = new boolean[4];
        best = "";
        backtrack("");
        return best;
    }
}
```

```cpp
#include <string>
#include <vector>

class LargestTimeForGivenDigits {
    std::vector<int> digits;
    std::vector<bool> used;
    std::string best;

    void backtrack(std::string cur) {
        if (cur.size() == 4) {
            int h = std::stoi(cur.substr(0, 2));
            int m = std::stoi(cur.substr(2, 2));

            if (h <= 23 && m <= 59 && cur > best) best = cur;
            return;
        }

        for (int i = 0; i < 4; i++) {
            if (!used[i]) {
                used[i] = true;
                backtrack(cur + std::to_string(digits[i]));
                used[i] = false;
            }
        }
    }

public:
    /**
     * @param arr four digits
     * @return    largest valid HH:MM or ""
     */
    std::string largestTimeFromDigits(std::vector<int>& arr) {
        digits = arr;
        used.assign(4, false);
        best = "";
        backtrack("");
        return best;
    }
};
```

```python
def largest_time_from_digits(arr: list[int]) -> str:
    """
    @param arr: four digits
    @return:    largest valid HH:MM or ""
    """
    best = ""
    used = [False] * 4

    def backtrack(cur: str) -> None:
        nonlocal best
        if len(cur) == 4:
            h, m = int(cur[:2]), int(cur[2:])
            if h <= 23 and m <= 59 and cur > best:
                best = cur
            return

        for i in range(4):
            if not used[i]:
                used[i] = True
                backtrack(cur + str(arr[i]))
                used[i] = False

    backtrack("")
    return best
```

```rust
impl Solution {
    /// @param arr four digits
    /// @return    largest valid HH:MM or ""
    pub fn largest_time_from_digits(arr: Vec<i32>) -> String {
        let mut best = String::new();
        let mut used = vec![false; 4];

        fn backtrack(arr: &Vec<i32>, used: &mut Vec<bool>, cur: &mut String, best: &mut String) {
            if cur.len() == 4 {
                let h: i32 = cur[0..2].parse().unwrap();
                let m: i32 = cur[2..4].parse().unwrap();

                if h <= 23 && m <= 59 && cur > best {
                    *best = cur.clone();
                }
                return;
            }

            for i in 0..4 {
                if !used[i] {
                    used[i] = true;
                    let d = arr[i].to_string();
                    cur.push_str(&d);
                    backtrack(arr, used, cur, best);
                    cur.truncate(cur.len() - 1);
                    used[i] = false;
                }
            }
        }

        backtrack(&arr, &mut used, &mut String::new(), &mut best);
        best
    }
}
```

## Dry run

**Input:** `arr = [1,2,3,4]`.

```
permutations: "1234" (12:34 valid, best), "1243" (12:43 valid, better), "1324" (13:24),
"1342" (13:42), "1423" (14:23), "1432" (14:32), "2134" (21:34), "2143" (21:43),
"2314" (23:14), "2341" (23:41 valid — best!), ...  "2413" (24:13 invalid: 24 hours).

Output: "23:41" ✓
```

The filter `h <= 23 && m <= 59` prunes the invalid permutations; the string `>` keeps the lexicographically largest — which for fixed-length strings is the numerically largest time. `[5,5,5,5]`: every permutation is "55:55" — invalid → best stays "" ✓.

## Complexity

**Time.** 4! permutations:

$$
T = O(4!) = O(24) = O(1)
$$

**Space.** The backtrack:

$$
S = O(4) = O(1)
$$

## Variants & follow-ups

- **Restore IP Addresses** ([12.6](../ch12-backtracking/restore-ip-addresses.md)) — the same permutation/filter pattern with segment rules.
- **Interview follow-up:** "Why not greedy per digit?" The hour constraint (0-23) is *positional* — the largest possible first digit (2) forces constraints on the second (0-3). Greedy fails on `[2,0,6,6]` (26:xx invalid → 20:66 invalid → the answer is actually 06:26... no wait, 20:66 invalid, the answer is ""). The brute-force permutation handles the coupling exactly.
