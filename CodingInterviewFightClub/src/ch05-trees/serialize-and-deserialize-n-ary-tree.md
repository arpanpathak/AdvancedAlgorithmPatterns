# 5.36 Serialize And Deserialize N-ary Tree

> **Source:** [`src/main/kotlin/tree/SerializeAndDeserializeNArrayTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/SerializeAndDeserializeNArrayTree.kt)
> **Pattern:** preorder with child-count encoding · **Core page**

## The Problem

Design `serialize(root)` / `deserialize(data)` for an **N-ary tree** — each node has any number of children. Serialize to a single string; deserialize must reconstruct the identical tree.

- Constraints: node values fit in an `Int`; any number of children per node; tree sizes up to $10^4$.

## Examples

```
Tree:
        1
      / | \
     2  3  4
    / \    |
   5   6   7

serialize  -> "1:3,2:2,5:0,6:0,3:0,4:1,7:0"
deserialize -> identical tree
```

## Intuition — preorder + "how many children" makes the shape unambiguous

A binary tree's shape is implied by `null` markers (see [5.5](serialize-and-deserialize-binary-tree.md)). An N-ary tree has *no* fixed child count — so the marker becomes a **count**:

> For every node, write `value:childCount`, then recursively write its children. The `childCount` tells the deserializer exactly how many subtrees to parse next — no nulls, no ambiguity.

Why does this work? The format is *self-delimiting*: after reading `"1:3"`, the parser knows it must consume exactly 3 child subtrees before `1`'s encoding is complete. Recursion does the rest — each `parse()` call reads one node's `value:count`, then loops `count` times calling `parse()` again. The string is fully consumed exactly when the tree is fully rebuilt.

**The comma separator** splits tokens; each token is `"value:childCount"`. This is the preorder of the tree with degree information appended — the "encoding" equivalent of the [5.5](serialize-and-deserialize-binary-tree.md) `null`-marker trick, generalized.

## Approach 1 — Level-order with child counts (also works)

BFS with counts per node: same idea, queue-based. Preorder is shorter to write and matches the repo.

## Approach 2 — Preorder + child-count (the repo's version, optimal)

```kotlin
class SerializeAndDeserializeNArrayTree {
    class Node(var `val`: Int) {
        var children: List<Node?> = listOf()
    }

    class Codec {
        // Encodes a tree to a single string.
        fun serialize(root: Node?): String = when (root) {
            null -> ""
            else -> buildString {
                fun dfs(node: Node?) {
                    node?.let {
                        append("${it.`val`}:${it.children.size}")
                        if (it.children.isNotEmpty()) append(",")
                        it.children.forEachIndexed { index, child ->
                            dfs(child)
                            if (index < it.children.size - 1) append(",")
                        }
                    }
                }
                dfs(root)
            }
        }

        // Decodes your encoded data to a tree.
        fun deserialize(data: String): Node? {
            if (data.isEmpty()) return null

            val tokens = data.split(",")
            var index = 0

            fun parse(): Node? {
                if (index >= tokens.size) return null

                val (valueStr, childCountStr) = tokens[index++].split(":")
                val node = Node(valueStr.toInt())

                node.children = List(childCountStr.toInt()) { parse() }

                return node
            }

            return parse()
        }
    }
}
```

```python
class Node:
    def __init__(self, val, children=None):
        self.val = val
        self.children = children if children is not None else []

def serialize(root):
    if not root:
        return ""
    parts = []
    def dfs(node):
        parts.append(f"{node.val}:{len(node.children)}")
        for child in node.children:
            dfs(child)
    dfs(root)
    return ",".join(parts)

def deserialize(data):
    if not data:
        return None
    tokens = data.split(",")
    idx = 0
    def parse():
        nonlocal idx
        val_s, count_s = tokens[idx].split(":")
        idx += 1
        node = Node(int(val_s))
        node.children = [parse() for _ in range(int(count_s))]
        return node
    return parse()
```

```java
import java.util.*;

class SerializeAndDeserializeNaryTree {
    static class Node {
        public int val;
        public List<Node> children = new ArrayList<>();
        public Node(int val) { this.val = val; }
    }

    static class Codec {
        /**
         * @param root n-ary tree root
         * @return     "val:count,val:count,..." preorder encoding
         */
        public String serialize(Node root) {
            if (root == null) return "";
            StringBuilder sb = new StringBuilder();
            dfs(root, sb);
            return sb.toString();
        }

        private void dfs(Node node, StringBuilder sb) {
            sb.append(node.val).append(':').append(node.children.size());
            for (Node child : node.children) {
                sb.append(',');
                dfs(child, sb);
            }
        }

        /**
         * @param data serialized string
         * @return     reconstructed n-ary tree
         */
        public Node deserialize(String data) {
            if (data.isEmpty()) return null;
            String[] tokens = data.split(",");
            int[] idx = {0};
            return parse(tokens, idx);
        }

        private Node parse(String[] tokens, int[] idx) {
            String[] parts = tokens[idx[0]++].split(":");
            Node node = new Node(Integer.parseInt(parts[0]));
            int count = Integer.parseInt(parts[1]);
            for (int i = 0; i < count; i++) node.children.add(parse(tokens, idx));
            return node;
        }
    }
}
```

## Reading the code — what's actually happening

**Serialization (top-down):**

1. **`append("${it.val}:${it.children.size}")` writes the node's header.** One token per node: the value, a colon, and the child count. That count is the entire "shape information" — it replaces the `null`s a binary-tree encoding needs.
2. **The recursive `dfs(child)` calls write children immediately after their parent's header.** This is preorder: parent, then each child subtree in order. The `forEachIndexed` comma logic just separates sibling tokens — a comma goes between every pair of tokens (the header of each child, and before each child subtree).
3. **An empty tree serializes to `""`** — the null-input branch. Deserialization must check `data.isEmpty()` before splitting (splitting `""` would yield `[""]`).

**Deserialization (bottom-up, and the elegant part):**

4. **`tokens[index++].split(":")` reads one header** and advances the global cursor. Destructuring `(valueStr, childCountStr)` unpacks the two fields.
5. **`List(childCountStr.toInt()) { parse() }` is the recursion-with-a-cursor.** It creates exactly `count` children by calling `parse()` that many times. Each `parse()` call *itself* reads a header and recursively builds *its* children — so the count acts as a self-delimiting contract: after `"1:3"`, the next three tokens' subtrees belong to node 1, no more and no less.
6. **The shared `index` is what makes it work.** It's a cursor into the token list, mutated by every recursive call. Because preorder writes children immediately after parents, the cursor is always at the right place when `parse()` is called — no backtracking, no lookahead.

Trace `"1:3,2:2,5:0,6:0,3:0,4:1,7:0"`: parse reads `1:3` → three children needed → parse → `2:2` → two children → parse → `5:0` (leaf) → parse → `6:0` (leaf) → node 2 done → parse → `3:0` (leaf) → parse → `4:1` → one child → parse → `7:0` (leaf) → node 4 done → node 1 done. Every token consumed, tree rebuilt exactly.

## Complexity

**Time.** Every node visited once in each direction:

$$
T(n) = O(n)
$$

**Space.** The recursion depth (tree height) plus the token list:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Serialize And Deserialize Binary Tree** ([5.5](serialize-and-deserialize-binary-tree.md)) — the binary ancestor; `null` markers vs. child counts is the exact upgrade this page makes.
- **Serialize And Deserialize N-ary (LeetCode 428)** — the same encoding, but the problem asks for `1,3,2,2,5,0,6,0,3,0,4,1,7,0` style (counts inline, no colons) — same logic, different delimiters.
- **Preorder with degree (Prüfer-like thinking)** — the child-count trick generalizes: any rooted tree can be encoded by a traversal plus per-node degree, which is the backbone of tree canonicalization problems.
- **Interview follow-up:** "Why do we need the count at all — can't we infer it?" Without counts, the parser can't know when one child subtree ends and the next begins (children have variable sizes). The count makes every subtree self-delimiting — that's the same reason binary-tree encodings use `null` markers. Say that and the design is justified.
