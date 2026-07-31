# 12.5 Palindrome Partitioning

> **Source:** [`src/main/kotlin/backtracking/PalindromePartitioning.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/backtracking/PalindromePartitioning.kt)
> **Pattern:** prefix pruning · **Core page**

## The Problem

Given a string `s`, return **all possible palindrome partitions** — every way to split `s` into substrings that are each a palindrome.

- Constraints: $1 \le n \le 16$.

## Examples

```
Input:  s = "aab"
Output: [["a","a","b"],["aa","b"]]

Input:  s = "a"
Output: [["a"]]
```

## Intuition — choose the *length of the next piece*, only if it's a palindrome

The partition structure is a sequence of *cut positions*. Backtracking walks the string with a `start` index; at each position the question is **"how long is the next piece?"** — and the pruning is: *only consider a piece that is itself a palindrome*.

```
dfs(start, path):
    if start == len: record path (every char is consumed, all pieces were palindromes)
    for end in start..len-1:
        if s[start..end] is a palindrome:
            path.add(s[start..end])
            dfs(end + 1, path)      # the next piece starts right after this one
            path.removeLast()
```

**Why does "all pieces are palindromes" at the end suffice?** The *only* constraint is per-piece; there's no global constraint on the partition. So a complete partition (every character consumed) where every piece passed the palindrome check at add-time is automatically valid — the pruning *is* the correctness. Contrast with Sudoku, where validity depends on the whole board.

**The palindrome check** (`isPalindrome(s, left, right)`): two pointers from the ends meeting in the middle, $O(\text{piece length})$. At $n \le 16$ the naive check is fine; the standard optimization (precompute `pal[i][j]` in $O(n^2)$) pays off only at larger `n`.

**The decision tree shape:** at each `start` there are up to `n - start` possible piece lengths, but the palindrome filter prunes most of them — that filter is what keeps the tree near the output size.

## Approach 1 — All $2^{n-1}$ partitions, filter palindromes

Enumerate every cut pattern and check each piece: correct, but it builds invalid partitions before discarding them.

## Approach 2 — Palindrome-pruned backtracking (the repo's version, optimal)

```kotlin
class PalindromePartitioning {
    /**
     * @param s input string
     * @return  all partitions of s into palindromic substrings
     */
    fun partition(s: String): List<List<String>> {
        val result = mutableListOf<List<String>>()

        fun isPalindrome(s: String, left: Int, right: Int): Boolean {
            var (l, r) = left to right
            while (l < r) {
                if (s[l++] != s[r--]) return false
            }
            return true
        }

        fun dfs(start: Int, path: MutableList<String>) {
            if (start == s.length) {                 // every character consumed: complete
                result.add(ArrayList(path))
                return
            }
            for (end in start until s.length) {
                if (isPalindrome(s, start, end)) {   // only extend with palindromic pieces
                    path.add(s.substring(start, end + 1))
                    dfs(end + 1, path)               // next piece starts after this one
                    path.removeAt(path.size - 1)     // undo
                }
            }
        }

        dfs(0, mutableListOf())
        return result
    }
}
```

```java
import java.util.*;

public class PalindromePartitioning {
    /**
     * @param s input string
     * @return  all partitions of s into palindromic substrings
     */
    public List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        dfs(0, s, new ArrayList<>(), result);
        return result;
    }

    private void dfs(int start, String s, List<String> path, List<List<String>> result) {
        if (start == s.length()) {                   // every character consumed: complete
            result.add(new ArrayList<>(path));
            return;
        }
        for (int end = start; end < s.length(); end++) {
            if (isPalindrome(s, start, end)) {       // only extend with palindromic pieces
                path.add(s.substring(start, end + 1));
                dfs(end + 1, s, path, result);
                path.remove(path.size() - 1);        // undo
            }
        }
    }

    private boolean isPalindrome(String s, int l, int r) {
        while (l < r) {
            if (s.charAt(l++) != s.charAt(r--)) return false;
        }
        return true;
    }
}
```

```cpp
#include <string>
#include <vector>

class PalindromePartitioning {
    bool isPalindrome(const std::string& s, int l, int r) {
        while (l < r) {
            if (s[l++] != s[r--]) return false;
        }
        return true;
    }

    void dfs(int start, const std::string& s, std::vector<std::string>& path,
             std::vector<std::vector<std::string>>& result) {
        if (start == (int)s.size()) {                // every character consumed: complete
            result.push_back(path);
            return;
        }
        for (int end = start; end < (int)s.size(); end++) {
            if (isPalindrome(s, start, end)) {       // only extend with palindromic pieces
                path.push_back(s.substr(start, end - start + 1));
                dfs(end + 1, s, path, result);
                path.pop_back();                     // undo
            }
        }
    }

public:
    /**
     * @param s input string
     * @return  all partitions of s into palindromic substrings
     */
    std::vector<std::vector<std::string>> partition(std::string s) {
        std::vector<std::vector<std::string>> result;
        std::vector<std::string> path;
        dfs(0, s, path, result);
        return result;
    }
};
```

```python
def partition(s: str) -> list[list[str]]:
    """
    @param s: input string
    @return:  all partitions of s into palindromic substrings
    """
    result = []
    path = []

    def is_palindrome(l: int, r: int) -> bool:
        while l < r:
            if s[l] != s[r]:
                return False
            l += 1
            r -= 1
        return True

    def dfs(start: int) -> None:
        if start == len(s):                # every character consumed: complete
            result.append(path[:])
            return
        for end in range(start, len(s)):
            if is_palindrome(start, end):  # only extend with palindromic pieces
                path.append(s[start:end + 1])
                dfs(end + 1)               # next piece starts after this one
                path.pop()                 # undo

    dfs(0)
    return result
```

```rust
impl Solution {
    /// @param s input string
    /// @return  all partitions of s into palindromic substrings
    pub fn partition(s: String) -> Vec<Vec<String>> {
        let bytes = s.as_bytes();
        let mut result = Vec::new();
        let mut path = Vec::new();

        fn is_palindrome(bytes: &[u8], mut l: usize, mut r: usize) -> bool {
            while l < r {
                if bytes[l] != bytes[r] { return false; }
                l += 1; r -= 1;
            }
            true
        }

        fn dfs(start: usize, bytes: &[u8], path: &mut Vec<String>, result: &mut Vec<Vec<String>>) {
            if start == bytes.len() {            // every character consumed: complete
                result.push(path.clone());
                return;
            }
            for end in start..bytes.len() {
                if is_palindrome(bytes, start, end) {   // only extend with palindromic pieces
                    path.push(String::from_utf8(bytes[start..=end].to_vec()).unwrap());
                    dfs(end + 1, bytes, path, result);
                    path.pop();                  // undo
                }
            }
        }

        dfs(0, bytes, &mut path, &mut result);
        result
    }
}
```

## Dry run

**Input:** `s = "aab"`.

```
dfs(0):
  end=0: "a" is palindrome -> path=["a"].  dfs(1):
    end=1: "a" palindrome -> path=["a","a"].  dfs(2):
      end=2: "b" palindrome -> path=["a","a","b"].  dfs(3): start==len -> record ["a","a","b"].
      undo -> ["a","a"].
    undo -> ["a"].
    end=2: "ab" not palindrome -> skip.
  undo -> [].
  end=1: "aa" is palindrome -> path=["aa"].  dfs(2):
    end=2: "b" palindrome -> path=["aa","b"].  dfs(3): record ["aa","b"].
  undo.

result: [["a","a","b"],["aa","b"]] ✓
```

The pruning is visible at `end=2` of `dfs(1)`: `"ab"` fails the palindrome check and its entire subtree never exists. The cuts are the decision — piece lengths, not characters.

## Complexity

**Time.** Output-sized ($2^{n-1}$ partitions worst case), $O(n)$ copy per leaf, $O(n)$ palindrome checks:

$$
T(n) = O(n \cdot 2^n)
$$

**Space.** Recursion depth plus output:

$$
S(n) = O(n \cdot 2^n)
$$

## Variants & follow-ups

- **Palindrome Partitioning II** (`src/main/kotlin/string/dynamic_programming/PalindromePartitioning_II.kt`) — *minimum* cuts instead of all partitions: the counting version is DP (each cut is a DP transition), not enumeration.
- **Word Break II** (`src/main/kotlin/string/backtracking/WordBreak_II.kt`) — the same "choose the next piece length" skeleton, with dictionary membership instead of palindrome-ness as the filter.
- **Palindrome Partitioning III / IV** — palindromes *after* edits: adds a DP cost per piece; the filter becomes "edits needed ≤ k".
- **Interview follow-up:** "Why is no *global* validity check needed at the leaves?" Unlike Sudoku, a partition's validity is the conjunction of independent per-piece checks — each piece was validated at add-time, so a complete partition is automatically valid. The pruning isn't just an optimization here; it's the correctness proof.
