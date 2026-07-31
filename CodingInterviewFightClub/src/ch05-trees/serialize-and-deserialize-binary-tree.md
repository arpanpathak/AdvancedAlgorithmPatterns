# 5.5 Serialize And Deserialize Binary Tree

> **Source:** [`src/main/kotlin/tree/SerializeAndDeserializeABinaryTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/SerializeAndDeserializeABinaryTree.kt)
> **Pattern:** pre-order + sentinels · **Core page**

## The Problem

Design an algorithm to **serialize** a binary tree to a string and **deserialize** that string back into the original tree. Any format is allowed as long as it round-trips.

- Constraints: $0 \le n \le 10^4$; values fit in a 32-bit `Int`.

## Examples

```
Input:       1
            / \
           2   3
              / \
             4   5
serialize   -> "1,2,null,null,3,4,null,null,5,null,null,"
deserialize -> the same tree

Input:  root = []          -> serialize -> ""    -> deserialize -> []
```

## Intuition — pre-order with explicit "nothing here" markers

The core problem: an in-order or level-order string alone can't be reconstructed *uniquely* (many trees share the same sequence). The fix is **pre-order + sentinels**: visit node, left, right — and emit a marker (`null`) for every missing child. Now the string is an unambiguous *recipe*: every non-null token is followed by exactly two slots (its left and right subtrees), and the `null` tokens terminate the recursion.

Why pre-order and not post-order? Post-order also round-trips with sentinels, but pre-order puts the *root first* — deserialization reads tokens left to right and builds the tree top-down in the same order it was written, which is the most natural mental model. (Post-order needs the root *last*, so you'd build children first — same algorithm, mirrored.)

**The mutual recursion trick:** serialization appends `val,` then recurses into left, then right; deserialization consumes the first token, and *its* left/right children are exactly the next tokens consumed by the recursive calls. The two functions are mirror images — `serialize` is pre-order *writing*, `deserialize` is pre-order *reading*.

## Approach 1 — Level-order (BFS) encoding

Serialize level by level with a queue, marking missing nodes: `[1,2,3,null,null,4,5]`. Correct and compact, but deserialization needs an index walk over a queue of *tokens* — the fence logic from [5.2](binary-tree-level-order-traversal.md) with more moving parts.

## Approach 2 — Recursive pre-order + sentinels (the repo's version, optimal)

```kotlin
class Codec() {
    // Encodes a tree to a single string.
    /**
     * @param root the root of the binary tree
     * @return     pre-order string with "null," sentinels for missing children
     */
    fun serialize(root: TreeNode?): String {
        val serializedTree = StringBuilder()

        fun dfs(node: TreeNode?) {
            if (node == null) {
                serializedTree.append("null,")
                return
            }
            serializedTree.append("${node.`val`},")
            dfs(node.left)
            dfs(node.right)
        }
        dfs(root)
        return serializedTree.toString()
    }

    // Decodes the encoded string back to a tree.
    /**
     * @param data the string produced by serialize
     * @return     the reconstructed tree root
     */
    fun deserialize(data: String): TreeNode? {
        val nodes = data.split(",").toMutableList()

        fun dfsDeserialize(): TreeNode? {
            if (nodes.isEmpty()) return null
            val value = nodes.removeAt(0)
            if (value == "null") return null

            return TreeNode(value.toInt()).apply {
                left = dfsDeserialize()
                right = dfsDeserialize()
            }
        }
        return dfsDeserialize()
    }
}
```

```java
import java.util.*;

public class Codec {
    /**
     * @param root the root of the binary tree
     * @return     pre-order string with "null," sentinels for missing children
     */
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        dfs(root, sb);
        return sb.toString();
    }

    private void dfs(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("null,");
            return;
        }
        sb.append(node.val).append(",");
        dfs(node.left, sb);
        dfs(node.right, sb);
    }

    /**
     * @param data the string produced by serialize
     * @return     the reconstructed tree root
     */
    public TreeNode deserialize(String data) {
        Queue<String> tokens = new LinkedList<>(Arrays.asList(data.split(",")));
        return build(tokens);
    }

    private TreeNode build(Queue<String> tokens) {
        String token = tokens.poll();
        if (token == null || token.equals("null")) return null;
        TreeNode node = new TreeNode(Integer.parseInt(token));
        node.left = build(tokens);
        node.right = build(tokens);
        return node;
    }
}
```

```cpp
#include <string>
#include <sstream>
#include <queue>

class Codec {
    /**
     * @param node  current subtree root
     * @param out   string stream being appended to
     */
    void dfs(TreeNode* node, std::ostringstream& out) {
        if (node == nullptr) {
            out << "null,";
            return;
        }
        out << node->val << ",";
        dfs(node->left, out);
        dfs(node->right, out);
    }

public:
    /**
     * @param root the root of the binary tree
     * @return     pre-order string with "null," sentinels for missing children
     */
    std::string serialize(TreeNode* root) {
        std::ostringstream out;
        dfs(root, out);
        return out.str();
    }

    /**
     * @param data the string produced by serialize
     * @return     the reconstructed tree root
     */
    TreeNode* deserialize(std::string data) {
        std::queue<std::string> tokens;
        std::istringstream in(data);
        std::string token;
        while (std::getline(in, token, ',')) tokens.push(token);
        return build(tokens);
    }

    TreeNode* build(std::queue<std::string>& tokens) {
        std::string token = tokens.front();
        tokens.pop();
        if (token == "null") return nullptr;
        TreeNode* node = new TreeNode(std::stoi(token));
        node->left = build(tokens);
        node->right = build(tokens);
        return node;
    }
};
```

```python
class Codec:
    """
    @param root: the root of the binary tree
    @return:     pre-order string with "null," sentinels for missing children
    """
    def serialize(self, root: TreeNode | None) -> str:
        parts: list[str] = []

        def dfs(node: TreeNode | None) -> None:
            if node is None:
                parts.append("null")
                return
            parts.append(str(node.val))
            dfs(node.left)
            dfs(node.right)

        dfs(root)
        return ",".join(parts)

    """
    @param data: the string produced by serialize
    @return:     the reconstructed tree root
    """
    def deserialize(self, data: str) -> TreeNode | None:
        tokens = data.split(",")
        index = 0

        def build() -> TreeNode | None:
            nonlocal index
            token = tokens[index]
            index += 1
            if token == "null":
                return None
            node = TreeNode(int(token))
            node.left = build()
            node.right = build()
            return node

        return build()
```

```rust
use std::cell::RefCell;
use std::rc::Rc;

impl Codec {
    /// @param root the root of the binary tree
    /// @return     pre-order string with "null," sentinels for missing children
    pub fn serialize(&self, root: Option<Rc<RefCell<TreeNode>>>) -> String {
        let mut parts = Vec::new();
        Self::dfs(&root, &mut parts);
        parts.join(",")
    }

    fn dfs(node: &Option<Rc<RefCell<TreeNode>>>, parts: &mut Vec<String>) {
        match node {
            None => parts.push("null".to_string()),
            Some(n) => {
                let n = n.borrow();
                parts.push(n.val.to_string());
                Self::dfs(&n.left, parts);
                Self::dfs(&n.right, parts);
            }
        }
    }

    /// @param data the string produced by serialize
    /// @return     the reconstructed tree root
    pub fn deserialize(&self, data: String) -> Option<Rc<RefCell<TreeNode>>> {
        let mut tokens: VecDeque<String> = data.split(',').map(str::to_string).collect();
        Self::build(&mut tokens)
    }

    fn build(tokens: &mut VecDeque<String>) -> Option<Rc<RefCell<TreeNode>>> {
        let token = tokens.pop_front()?;
        if token == "null" {
            return None;
        }
        let node = Rc::new(RefCell::new(TreeNode::new(token.parse().ok()?)));
        node.borrow_mut().left = Self::build(tokens);
        node.borrow_mut().right = Self::build(tokens);
        Some(node)
    }
}
```

## Dry run

**Input:** the tree above. Serialize:

```
dfs(1):   append "1,"   -> "1,"
  dfs(2): append "2,"   -> "1,2,"
    dfs(null)  -> "null,"  -> "1,2,null,"
    dfs(null)  -> "null,"  -> "1,2,null,null,"
  dfs(3): append "3,"   -> "1,2,null,null,3,"
    dfs(4): append "4," -> "1,2,null,null,3,4,"
      dfs(null) -> "null,"
      dfs(null) -> "null,"
    dfs(5): append "5," -> "...,5,"
      dfs(null) -> "null,"
      dfs(null) -> "null,"
Output: "1,2,null,null,3,4,null,null,5,null,null," ✓
```

Deserialize (token stream consumed left to right, root first):

```
pop "1"   -> node 1,  left = build() ...
  pop "2"   -> node 2, left=build() -> pop "null" -> null
                        right=build() -> pop "null" -> null
  pop "3"   -> node 3, left=build() ...
    pop "4" -> node 4, left=null, right=null
    pop "5" -> node 5, left=null, right=null
-> returns 1 with the full tree restored ✓
```

Count the sentinels: the tree has 5 nodes, so it has 10 null children — and the string has exactly 10 `null` tokens. `tokens = 2n + 1` is the invariant that makes the stream self-terminating.

## Complexity

**Time.** Each node touched once in each direction (plus $O(n)$ tokenization):

$$
T(n) = O(n)
$$

**Space.** The string is $O(n)$; the recursion stack is $O(h)$ on top:

$$
S(n) = O(n) \text{ output} + O(h) \text{ stack}, \quad h \in [\log n, n]
$$

## Variants & follow-ups

- **Serialize N-ary Tree** (`src/main/kotlin/tree/SerializeAndDeserializeNArrayTree.kt`) — pre-order plus a *children-count* token per node, so the reader knows how many subtrees to expect.
- **Construct Binary Tree From Preorder And Inorder** (`src/main/kotlin/tree/ConstructBinaryTreeFromPreorderAndInOrderTraversal.kt`) — no sentinels needed: the *two* traversals disambiguate each other (pre-order gives roots, in-order splits left/right).
- **Recover A Tree From Preorder Traversal** (`src/main/kotlin/tree/RecoverATreeFromPreOrderTraversal.kt`) — pre-order with *depth markers* (`"1-2--3"`); the dashes encode where each node hangs.
- **Interview follow-up:** "Can we drop the trailing comma?" Yes — but the tokenizer must then handle empty tokens; the trailing comma keeps `split` trivial. Not a correctness issue, a taste issue.
- **Interview follow-up:** "Why not just JSON/level-order?" Any self-describing format works; level-order with sentinels is equally valid but needs the queue-fence dance from [5.2](binary-tree-level-order-traversal.md) on the read side. Pre-order's recursion makes write and read literally the same function shape.
