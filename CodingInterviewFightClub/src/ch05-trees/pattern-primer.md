# 5.0 Pattern Primer — The Recursive Data Structure

A binary tree is defined by its own shape:

```
tree(node) = node + tree(node.left) + tree(node.right)
```

That self-reference is why recursion is the *native* language of trees, and why almost every tree solution has the same skeleton:

```kotlin
fun solve(node: TreeNode?): Something {
    if (node == null) return baseCase           // 1. base case
    val left = solve(node.left)                  // 2. recurse left
    val right = solve(node.right)                // 3. recurse right
    return combine(node, left, right)            // 4. combine at the root
}
```

The whole art is choosing what `combine` does — and that's decided by the **traversal order**.

## The four traversals (and when each one shines)

| Order | Visit sequence | Use when |
|---|---|---|
| Pre-order | node, left, right | *constructing* trees; serialization; copying |
| In-order | left, node, right | BSTs (produces sorted order); validation |
| Post-order | left, right, node | *destructive* computations: depth, LCA, path sums — you need the children's answers before the parent's |
| Level-order (BFS) | by depth | shortest paths, "per level" outputs, complete-tree checks |

**The rule of thumb:** if the answer at a node depends on the answers of its children, use **post-order** (max depth, diameter, max path sum, LCA — everything in this chapter's core set). If it depends on *ancestors*, pass state down (path-sum prefix, BST bounds). If it depends on neither, any order works — pick the simplest.

## The two implementation styles

**Recursion** mirrors the structure — but the call stack costs $O(h)$, and a skewed tree of $10^5$ nodes overflows the stack. **Iteration** with an explicit stack/queue costs $O(h)$ heap memory and never overflows. Interviews: write recursion first (correctness), offer the iterative version when the interviewer asks about stack depth.

## The "global best" idiom

Many tree optimizations (diameter, max path sum, max width) need a running global answer *while* the recursion computes per-node values. Two clean ways:

1. A `var ans` captured by the recursive function (the repo's style for Max Path Sum).
2. A single-element holder returned alongside, or a `Pair` return.

The per-node function returns the *partial* value (usable by the parent); the global captures the *complete* candidate (possibly bending through this node). The distinction — **"what can I hand my parent" vs "what's the best answer seen so far"** — is the single most common interview trap in tree DP, and [5.4](binary-tree-maximum-path-sum.md) exists to drill it.

## Complexity intuition

Every traversal visits each node $O(1)$ times → $O(n)$ time. Space is $O(h)$ (recursion stack or explicit stack) or $O(w)$ for BFS (queue, $w$ = max width, up to $n/2$). The "height" $h$ ranges from $\log n$ (balanced) to $n$ (skewed) — always state both bounds.
