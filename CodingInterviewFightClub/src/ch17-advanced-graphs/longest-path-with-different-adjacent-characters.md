# 17.15 Longest Path With Different Adjacent Characters

> **Source**: [`src/main/kotlin/tree/LongestPathWithDifferentAdjacentCharacters.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/LongestPathWithDifferentAdjacentCharacters.kt)
> **Pattern**: tree DP with a character constraint · **Core page**

## The Problem

The longest path in a tree where **no two adjacent nodes share a character** (the path may pass through any nodes).

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  parent = [-1,0,0,1,1,2], s = "abacbe"
Output: 3   (e.g. 1→3: a→c or 0→1→4: a→a? no... the answer path is 3)
```

## Intuition — post-order chains with a char-match filter

The [5.21](../ch05-trees/longest-univalue-path.md) post-order shape, inverted: a child's chain is usable only if **its char differs** from the node's:

```kotlin
fun dfs(node: Int): Int {
    var maxDepth = 1

    for (child in children[node]) {
        val childDepth = dfs(child)

        if (s[child] != s[node]) {          // adjacent chars must differ
            // candidate through this node: maxDepth-so-far + childDepth
            maxLength = maxOf(maxLength, maxDepth + childDepth)
            maxDepth = maxOf(maxDepth, childDepth + 1)
        }
    }
    return maxDepth
}
```

**Why `maxDepth + childDepth` as the bend candidate?** The best path through `node` combines the best chain from one usable child with the best from another — the [5.4](../ch05-trees/binary-tree-maximum-path-sum.md) two-branch max.

**Why `maxDepth` updated before use?** The first child establishes the baseline; each subsequent child's candidate pairs with the *best-so-far* chain — the [5.4](../ch05-trees/binary-tree-maximum-path-sum.md) ordering discipline.

## Approach 1 — BFS from every node (O(n²))

Treat as a graph, BFS per start: correct, slow.

## Approach 2 — Post-order chain DP (the repo's version, optimal)

```kotlin
class LongestPathWithDifferentAdjacentCharacters {
    /**
     * @param parent parent array (-1 = root)
     * @param s      node characters
     * @return       longest path with differing adjacent chars
     */
    fun longestPath(parent: IntArray, s: String): Int {
        val children = Array(parent.size) { mutableListOf<Int>() }
        for (i in 1 until parent.size) {
            children[parent[i]].add(i)
        }

        var maxLength = 1

        fun dfs(node: Int): Int {
            var maxDepth = 1

            for (child in children[node]) {
                val childDepth = dfs(child)

                if (s[child] != s[node]) {
                    maxLength = maxOf(maxLength, maxDepth + childDepth)
                    maxDepth = maxOf(maxDepth, childDepth + 1)
                }
            }
            return maxDepth
        }

        dfs(0)
        return maxLength
    }
}
```

```java
import java.util.*;

public class LongestPathWithDifferentAdjacentCharacters {
    private List<Integer>[] children;
    private String s;
    private int best = 1;

    private int dfs(int node) {
        int maxDepth = 1;

        for (int child : children[node]) {
            int childDepth = dfs(child);

            if (s.charAt(child) != s.charAt(node)) {
                best = Math.max(best, maxDepth + childDepth);
                maxDepth = Math.max(maxDepth, childDepth + 1);
            }
        }
        return maxDepth;
    }

    /**
     * @param parent parent array (-1 = root)
     * @param s      node characters
     * @return       longest path with differing adjacent chars
     */
    public int longestPath(int[] parent, String s) {
        this.s = s;
        int n = parent.length;
        children = new ArrayList[n];
        for (int i = 0; i < n; i++) children[i] = new ArrayList<>();

        for (int i = 1; i < n; i++) children[parent[i]].add(i);

        dfs(0);
        return best;
    }
}
```

```cpp
#include <vector>
#include <string>
#include <algorithm>

class LongestPathWithDifferentAdjacentCharacters {
    std::vector<std::vector<int>> children;
    std::string s;
    int best = 1;

    int dfs(int node) {
        int maxDepth = 1;

        for (int child : children[node]) {
            int childDepth = dfs(child);

            if (s[child] != s[node]) {
                best = std::max(best, maxDepth + childDepth);
                maxDepth = std::max(maxDepth, childDepth + 1);
            }
        }
        return maxDepth;
    }

public:
    /**
     * @param parent parent array (-1 = root)
     * @param s      node characters
     * @return       longest path with differing adjacent chars
     */
    int longestPath(std::vector<int>& parent, std::string s) {
        this->s = s;
        children.assign(parent.size(), {});

        for (int i = 1; i < (int)parent.size(); i++) children[parent[i]].push_back(i);

        dfs(0);
        return best;
    }
};
```

```python
def longest_path(parent: list[int], s: str) -> int:
    """
    @param parent: parent array (-1 = root)
    @param s:      node characters
    @return:       longest path with differing adjacent chars
    """
    children = [[] for _ in range(len(parent))]
    for i in range(1, len(parent)):
        children[parent[i]].append(i)

    best = 1

    def dfs(node: int) -> int:
        nonlocal best
        max_depth = 1

        for child in children[node]:
            child_depth = dfs(child)

            if s[child] != s[node]:
                best = max(best, max_depth + child_depth)
                max_depth = max(max_depth, child_depth + 1)

        return max_depth

    dfs(0)
    return best
```

```rust
impl Solution {
    /// @param parent parent array (-1 = root)
    /// @param s      node characters
    /// @return       longest path with differing adjacent chars
    pub fn longest_path(parent: Vec<i32>, s: String) -> i32 {
        let n = parent.len();
        let mut children = vec![Vec::new(); n];
        for i in 1..n { children[parent[i] as usize].push(i); }

        let bytes: Vec<char> = s.chars().collect();
        let mut best = 1;

        fn dfs(node: usize, children: &Vec<Vec<usize>>, s: &Vec<char>, best: &mut i32) -> i32 {
            let mut max_depth = 1;

            for &child in &children[node] {
                let child_depth = dfs(child, children, s, best);

                if s[child] != s[node] {
                    *best = (*best).max(max_depth + child_depth);
                    max_depth = max_depth.max(child_depth + 1);
                }
            }
            max_depth
        }

        dfs(0, &children, &bytes, &mut best);
        best
    }
}
```

## Dry run

**Input:** `parent = [-1,0,0,1,1,2], s = "abacbe"`.

```
children: 0:[1,2], 1:[3,4], 2:[5]
dfs(3): leaf -> 1.  dfs(4): leaf -> 1.
dfs(1): child 3 'c' != 'b' -> best = max(1, 1+1) = 2.  maxDepth = 2.
        child 4 'e' != 'b' -> best = max(2, 2+1) = 3.  maxDepth = 3.
dfs(5): leaf -> 1.
dfs(2): child 5 'e' != 'a' -> best = max(3, 1+1) = 3.  maxDepth = 2.
dfs(0): child 1 'b' != 'a' -> best = max(3, 1+3) = 4?  Hmm — the known answer for this input
        is 3.  The path 1→0→2: chars b-a-e: adjacent b/a differ, a/e differ — length 3 (nodes 1,0,2).
        Wait, the parent array: 0's children are 1 and 2.  child 1: 'b' != 'a' ✓:
          best = max(1, 1 + dfs(1)=3) = 4?  But dfs(1) = 3 means a chain of 3 nodes from 1
          (1→4: b→e is 2; 1→3: b→c is 2; the max single-direction chain is 2, not 3!).

recompute dfs(1): children 3 ('c'), 4 ('e').
  child 3: 'c' != 'b' -> best=max(1, 1+1)=2.  maxDepth = max(1, 1+1) = 2.
  child 4: 'e' != 'b' -> best=max(2, 2+1)=3.  maxDepth = max(2, 2+1) = 3.
  dfs(1) = 3?  That's a STRAIGHT chain through 1 using ONE child at a time — maxDepth 3 means
  1 + the best child chain (2) = 3 nodes: e.g. 1→4 and nothing else is 2... 
  Hmm, maxDepth = childDepth + 1 where childDepth is the child's straight chain.  dfs(4) = 1,
  so 1 + 1 = 2.  Wait — child 4's dfs = 1, so maxDepth after child 4 = max(2, 1+1) = 2, NOT 3.
  Let me redo: child 3: childDepth=1 -> maxDepth = 1+1 = 2.  child 4: childDepth=1 ->
  best = max(2, 2+1) = 3 (the bend 3→1→4 = 3 nodes!).  maxDepth = max(2, 1+1) = 2.
  dfs(1) = 2.

dfs(0): child 1: childDepth 2 -> best = max(1, 1+2) = 3.  maxDepth = 3.
        child 2: childDepth 2 ('a' vs child 5 'e' differs) -> best = max(3, 3+2) = 5?
        WAIT: 0's child 2: s[2] = 'a', s[0] = 'a' — EQUAL -> the whole branch is SKIPPED!
        (the `if s[child] != s[node]` guard blocks it).

Output: best = 3 ✓
```

The char guard is decisive: 0 and its child 2 both have 'a' — that edge can never be in a valid path, so 2's whole subtree is skipped at 0 (but still counted *within itself*, where its internal chains found length 2). The best is the 3-node bend `3→1→4` (c-b-e).

## Complexity

**Time.** One post-order:

$$
T(n) = O(n)
$$

**Space.** Children + recursion:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Binary Tree Maximum Path Sum** ([5.4](../ch05-trees/binary-tree-maximum-path-sum.md)) — the global-best/bend-vs-straight engine this page adapts.
- **Longest Univalue Path** ([5.21](../ch05-trees/longest-univalue-path.md)) — the *same*-char twin of this *different*-char constraint.
- **Interview follow-up:** "Why does the guard skip the child entirely for the bend?" An invalid edge can't be part of any path — the child's chain is unusable *through this node*. But the child's own internal chains were already counted in its dfs; skipping only blocks the connection, which is exactly the constraint.
