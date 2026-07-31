# 5.23 Construct Quad Tree

> **Source**: [`src/main/kotlin/geo/quadtree/ConstructQuadTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/geo/quadtree/ConstructQuadTree.kt)
> **Pattern**: recursive quadrant split · **Core page**

## The Problem

Build the quad-tree of an n×n grid (n a power of 2): each node is uniform or splits into four quadrants.

- Constraints: n ≤ 64, power of 2.

## Examples

```
Input:  grid = [[0,1],[1,0]]
Output: a root split into four leaf quadrants (0,1,1,0)
```

## Intuition — uniform? leaf. Else split into four quadrants

`buildTree(grid, row, col, size)` checks uniformity; if mixed, recurse on the four quadrants of half the size:

```kotlin
fun construct(grid: Array<IntArray>): Node? {
    return buildTree(grid, 0, 0, grid.size)
}

fun buildTree(grid, row, col, size): Node {
    if (isUniform(grid, row, col, size)) {
        return Node(grid[row][col] == 1, true)     // leaf
    }

    val half = size / 2
    return Node(
        val = true, isLeaf = false,
        topLeft = buildTree(grid, row, col, half),
        topRight = buildTree(grid, row, col + half, half),
        bottomLeft = buildTree(grid, row + half, col, half),
        bottomRight = buildTree(grid, row + half, col + half, half)
    )
}
```

**Why the `(row, col, size)` frame?** Each recursion describes a square by its top-left corner and side length — the quadrant split is `(row, col+half)`, `(row+half, col)`, etc. The [5.5](construct-binary-tree-from-preorder-and-inorder.md) reconstruction discipline with coordinates.

**Why `isUniform` first?** The tree is built top-down: a uniform square is a leaf; a mixed one splits. The repo's `isUniform` uses an early-exit scan (no allocation).

## Approach 1 — Recursive quadrant split (the repo's version, optimal)

```kotlin
class ConstructQuadTree {
    data class Node(
        var `val`: Boolean,
        var isLeaf: Boolean,
        var topLeft: Node? = null, var topRight: Node? = null,
        var bottomLeft: Node? = null, var bottomRight: Node? = null
    )

    /**
     * @param grid n x n grid (n a power of 2)
     * @return     quad-tree root
     */
    fun construct(grid: Array<IntArray>): Node? {
        return buildTree(grid, 0, 0, grid.size)
    }

    private fun isUniform(grid: Array<IntArray>, row: Int, col: Int, size: Int): Boolean {
        if (size == 0) return true

        val firstVal = grid[row][col]
        for (r in row until row + size) {
            for (c in col until col + size) {
                if (grid[r][c] != firstVal) return false
            }
        }
        return true
    }

    private fun buildTree(grid: Array<IntArray>, row: Int, col: Int, size: Int): Node {
        if (isUniform(grid, row, col, size)) {
            return Node(grid[row][col] == 1, true)
        }

        val half = size / 2
        return Node(
            true, false,
            buildTree(grid, row, col, half),
            buildTree(grid, row, col + half, half),
            buildTree(grid, row + half, col, half),
            buildTree(grid, row + half, col + half, half)
        )
    }
}
```

```java
public class ConstructQuadTree {
    static class Node {
        public boolean val, isLeaf;
        public Node topLeft, topRight, bottomLeft, bottomRight;

        public Node(boolean val, boolean isLeaf) { this.val = val; this.isLeaf = isLeaf; }
    }

    private boolean uniform(int[][] grid, int row, int col, int size) {
        int first = grid[row][col];
        for (int r = row; r < row + size; r++)
            for (int c = col; c < col + size; c++)
                if (grid[r][c] != first) return false;
        return true;
    }

    private Node build(int[][] grid, int row, int col, int size) {
        if (uniform(grid, row, col, size)) {
            return new Node(grid[row][col] == 1, true);
        }

        int h = size / 2;
        return new Node(true, false,
            build(grid, row, col, h),
            build(grid, row, col + h, h),
            build(grid, row + h, col, h),
            build(grid, row + h, col + h, h));
    }

    /**
     * @param grid n x n grid (n a power of 2)
     * @return     quad-tree root
     */
    public Node construct(int[][] grid) {
        return build(grid, 0, 0, grid.length);
    }
}
```

```cpp
class ConstructQuadTree {
    bool uniform(std::vector<std::vector<int>>& grid, int row, int col, int size) {
        int first = grid[row][col];
        for (int r = row; r < row + size; r++)
            for (int c = col; c < col + size; c++)
                if (grid[r][c] != first) return false;
        return true;
    }

    Node* build(std::vector<std::vector<int>>& grid, int row, int col, int size) {
        if (uniform(grid, row, col, size)) {
            return new Node(grid[row][col] == 1, true);
        }

        int h = size / 2;
        return new Node(true, false,
            build(grid, row, col, h),
            build(grid, row, col + h, h),
            build(grid, row + h, col, h),
            build(grid, row + h, col + h, h));
    }

public:
    /**
     * @param grid n x n grid (n a power of 2)
     * @return     quad-tree root
     */
    Node* construct(std::vector<std::vector<int>>& grid) {
        return build(grid, 0, 0, grid.size());
    }
};
```

```python
class Node:
    def __init__(self, val, is_leaf, tl=None, tr=None, bl=None, br=None):
        self.val = val
        self.is_leaf = is_leaf
        self.top_left, self.top_right = tl, tr
        self.bottom_left, self.bottom_right = bl, br


def construct(grid: list[list[int]]) -> "Node":
    """
    @param grid: n x n grid (n a power of 2)
    @return:     quad-tree root
    """
    def uniform(row, col, size):
        first = grid[row][col]
        return all(grid[r][c] == first
                   for r in range(row, row + size)
                   for c in range(col, col + size))

    def build(row, col, size):
        if uniform(row, col, size):
            return Node(grid[row][col] == 1, True)

        h = size // 2
        return Node(True, False,
                    build(row, col, h),
                    build(row, col + h, h),
                    build(row + h, col, h),
                    build(row + h, col + h, h))

    return build(0, 0, len(grid))
```

```rust
impl Solution {
    /// @param grid n x n grid (n a power of 2)
    /// @return     quad-tree root
    pub fn construct(grid: Vec<Vec<i32>>) -> Option<Rc<RefCell<Node>>> {
        fn uniform(grid: &Vec<Vec<i32>>, row: usize, col: usize, size: usize) -> bool {
            let first = grid[row][col];
            (row..row + size).all(|r| (col..col + size).all(|c| grid[r][c] == first))
        }

        fn build(grid: &Vec<Vec<i32>>, row: usize, col: usize, size: usize) -> Option<Rc<RefCell<Node>>> {
            if uniform(grid, row, col, size) {
                return Some(Rc::new(RefCell::new(Node::new(grid[row][col] == 1, true))));
            }

            let h = size / 2;
            let node = Rc::new(RefCell::new(Node::new(true, false)));
            let mut n = node.borrow_mut();
            n.top_left = build(grid, row, col, h);
            n.top_right = build(grid, row, col + h, h);
            n.bottom_left = build(grid, row + h, col, h);
            n.bottom_right = build(grid, row + h, col + h, h);
            drop(n);
            Some(node)
        }

        build(&grid, 0, 0, grid.len())
    }
}
```

## Dry run

**Input:** `grid = [[0,1],[1,0]]` (2×2, half = 1).

```
build(0,0,2): uniform? (0,0)=0, (0,1)=1 -> no.  split:
  TL build(0,0,1): uniform (just 0) -> leaf 0.
  TR build(0,1,1): leaf 1.  BL build(1,0,1): leaf 1.  BR build(1,1,1): leaf 0.
root: val=true, isLeaf=false, quadrants [0,1,1,0] ✓
```

## Complexity

**Time.** Each cell scanned in uniformity checks (amortized O(n²)):

$$
T(n) = O(n^2)
$$

**Space.** The tree:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **The Skyline Problem** ([7.8](../ch07-heaps/the-skyline-problem.md)) — the divide-and-conquer sweep sibling.
- **Interview follow-up:** "Why power-of-2?" The four-way split needs equal halves — a non-power-of-2 size breaks the recursive halving. The problem guarantees it; the `(row, col, size)` frame keeps the split arithmetic exact.
