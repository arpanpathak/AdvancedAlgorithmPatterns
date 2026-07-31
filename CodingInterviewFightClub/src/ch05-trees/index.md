# Chapter 5 — Trees

> **Source:** `src/main/kotlin/tree/` (71 files — the biggest folder after arrays and graphs)
>
> **Master idea:** trees are *recursive data structures* — the root is a node whose children are trees. Almost every tree problem is "solve the left, solve the right, combine at the root" with a traversal order chosen deliberately.
>
> **Prerequisites:** recursion, plus the two-pointer basics from [Chapter 3](../ch03-arrays/index.md) (for the iterative versions).

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 5.1 | Maximum Depth Of Binary Tree | post-order recursion | $O(n)$ | [→](maximum-depth-of-binary-tree.md) |
| 5.2 | Binary Tree Level Order Traversal | BFS with level fencing | $O(n)$ | [→](binary-tree-level-order-traversal.md) |
| 5.3 | Lowest Common Ancestor | post-order "found?" propagation | $O(n)$ | [→](lowest-common-ancestor.md) |
| 5.4 | Binary Tree Maximum Path Sum | post-order with a global best | $O(n)$ | [→](binary-tree-maximum-path-sum.md) |
| 5.5 | Serialize And Deserialize Binary Tree | pre-order + sentinels | $O(n)$ | [→](serialize-and-deserialize-binary-tree.md) |
| 5.6 | Binary Tree Inorder Traversal (Iterative) | explicit stack | $O(n)$ | [→](binary-tree-inorder-traversal-iterative.md) |

| 5.7 | Construct Tree From Preorder And Inorder | index map + range recursion | $O(n)$ | [→](construct-binary-tree-from-preorder-and-inorder.md) |
| 5.8 | Binary Tree Right Side View | DFS first-per-level | $O(n)$ | [→](binary-tree-right-side-view.md) |
| 5.9 | Path Sum III | prefix sums on a tree | $O(n)$ | [→](path-sum-iii.md) |
| 5.10 | Diameter Of Binary Tree | post-order height + global best | $O(n)$ | [→](diameter-of-binary-tree.md) |
## The rest of the tree/ directory

`src/main/kotlin/tree/` is a forest: `bst/` (Recover BST, BST Iterator, Inorder Successor, Delete Node, Unique BSTs, My Calendar…), `bfs/` (Level Order II, Right Side View, Largest Value Per Row, Completeness, Averages…), `segment/` (Segment Tree, Dynamic Segment Tree, Iterative Segment Tree…), `fenwick/` (Fenwick Tree, Range Sum Query 2D Mutable…), `mst/` (Prim's & Kruskal's on points), `interval/`, plus standalone classics (Diameter, Path Sum II/III, Zigzag, Vertical Order, Boundary, Symmetric, Construct from Pre+In / In+Post, Populate Next Right, All Nodes Distance K, Serialize N-ary, Maximum Product of Split, Count Good Nodes, etc.).

New pages are appended to the table above as they're written.
