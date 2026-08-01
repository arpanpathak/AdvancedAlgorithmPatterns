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
| 5.11 | Recover Binary Search Tree | in-order swap detection | $O(n)$ | [→](recover-binary-search-tree.md) |
| 5.12 | All Nodes Distance K | parent map + 3-dir DFS | $O(n)$ | [→](all-nodes-distance-k.md) |
| 5.13 | Binary Tree ZigZag | level fence + addFirst | $O(n)$ | [→](zigzag-level-order.md) |
| 5.14 | Count Good Nodes | running-max DFS | $O(n)$ | [→](count-good-nodes.md) |
| 5.15 | Populating Next Right Pointers | O(1)-space level threading | $O(n)$ | [→](populating-next-right-pointers.md) |
| 5.16 | Delete Node In A BST | successor splice | $O(h)$ | [→](delete-node-in-a-bst.md) |
| 5.17 | Find Largest Value Per Row | BFS level max | $O(n)$ | [→](find-largest-value-in-each-tree-row.md) |
| 5.18 | Sum Root To Leaf Numbers | carry-down DFS | $O(n)$ | [→](sum-root-to-leaf-numbers.md) |
| 5.19 | Recover A Tree From Preorder | depth-guided rebuild | $O(n)$ | [→](recover-a-tree-from-preorder.md) |
| 5.20 | Range Sum Of BST | pruned traversal | $O(h+k)$ | [→](range-sum-of-bst.md) |
| 5.21 | Longest Univalue Path | post-order chains | $O(n)$ | [→](longest-univalue-path.md) |
| 5.22 | Leaf-Similar Trees | leaf-sequence compare | $O(n)$ | [→](leaf-similar-trees.md) |
| 5.24 | Inorder Successor In BST | successor-memory walk | $O(h)$ | [→](inorder-successor.md) |
| 5.25 | House Robber III | two-state tree DP | $O(n)$ | [→](house-robber-iii.md) |
| 5.26 | Level Order Traversal II | BFS fence + reverse | $O(n)$ | [→](binary-tree-level-order-traversal-ii.md) |
| 5.27 | Unique Binary Search Trees | Catalan DP | $O(n^2)$ | [→](unique-binary-search-trees.md) |
| 5.28 | Unique Binary Search Trees II | Cartesian tree generation | $O(C_n)$ | [→](unique-binary-search-trees-ii.md) |
| 5.29 | Lowest Common Ancestor III | parent-pointer climb | $O(d_p+d_q)$ | [→](lowest-common-ancestor-iii.md) |
| 5.30 | Step-By-Step Directions | LCA + path strings | $O(n)$ | [→](step-by-step-directions.md) |
## The rest of the tree/ directory

`src/main/kotlin/tree/` is a forest: `bst/` (Recover BST, BST Iterator, Inorder Successor, Delete Node, Unique BSTs, My Calendar…), `bfs/` (Level Order II, Right Side View, Largest Value Per Row, Completeness, Averages…), `segment/` (Segment Tree, Dynamic Segment Tree, Iterative Segment Tree…), `fenwick/` (Fenwick Tree, Range Sum Query 2D Mutable…), `mst/` (Prim's & Kruskal's on points), `interval/`, plus standalone classics (Diameter, Path Sum II/III, Zigzag, Vertical Order, Boundary, Symmetric, Construct from Pre+In / In+Post, Populate Next Right, All Nodes Distance K, Serialize N-ary, Maximum Product of Split, Count Good Nodes, etc.).

New pages are appended to the table above as they're written.
