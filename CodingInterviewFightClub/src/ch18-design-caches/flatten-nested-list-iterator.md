# 18.5 Flatten Nested List Iterator

> **Source:** [`src/main/kotlin/stack/FlattenNestedListIterator.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/FlattenNestedListIterator.kt)
> **Pattern:** stack of nested lists · **Core page**

## The Problem

Design an iterator that flattens a nested list of integers **lazily**: `NestedInteger` is either an `Int` or a `List<NestedInteger>`. `next()` must return the next integer, `hasNext()` report whether one remains.

- Constraints: the nesting can be arbitrarily deep; total elements ≤ $5 \times 10^4$.

## Examples

```
Input:  [[1,1],2,[1,1]]   -> next(): 1,1,2,1,1
Input:  [1,[4,[6]]]       -> next(): 1,4,6
```

## Intuition — a stack of *not-yet-flattened* sublists; expand on demand

The lazy flattening keeps a stack of `NestedInteger`s, with **hasNext() doing the work**: as long as the top is a *list*, pop it and push its elements (in reverse, so the first is on top). When the top is an integer, we're ready. `next()` pops that integer.

```
init:  push all items in reverse order (so item[0] is on top)
hasNext():
    while stack not empty and top is a list:
        pop the list; push its elements in reverse
    return stack not empty        # the top is now an integer (or stack is empty)
next(): return stack.pop() as Int   # hasNext guaranteed the top is an integer
```

**Why does pushing in reverse work?** A stack pops top-first; pushing `[a,b,c]` as `c,b,a` makes `a` come out first — preserving the original order. The "reverse the sublist, then push" step is the standard stack-as-queue trick.

**Why is `hasNext` doing the flattening (not the constructor)?** Lazy evaluation: a huge nested structure is *not* materialized until the iterator is actually consumed. `hasNext` expands only the top of the stack, one level at a time — the amortized cost per element is O(1) because each nested item is pushed and popped once.

**Why not flatten eagerly?** Eager flattening (recursively collect all ints into a list) is simpler but O(total) upfront memory and breaks laziness — for streaming or huge inputs, the stack version wins. The repo's `array.dfs.NestedInteger` is the input type; the iterator wraps it.

## Approach 1 — Eager flatten with recursion

Collect all integers into a list in the constructor, then index through: correct, but O(n) upfront and not lazy.

## Approach 2 — Stack with on-demand expansion (the repo's version, optimal)

```kotlin
class NestedIterator(nestedList: List<NestedInteger>) {
    private val stack = ArrayDeque<NestedInteger>()

    init {
        // Initialize the stack with the nested list, reversed so we can pop from the top
        for (item in nestedList.reversed()) {
            stack.addLast(item)
        }
    }

    /** @return the next integer (hasNext guarantees the top is an integer) */
    fun next(): Int {
        return stack.removeLast().getInteger()!!
    }

    /** @return true iff another integer remains; expands nested lists on demand */
    fun hasNext(): Boolean {
        // Make sure the top of the stack is an integer; if not, pop the nested list
        while (stack.isNotEmpty() && !stack.last().isInteger()) {
            val current = stack.removeLast()
            val nestedList = current.getList()
            // Push the elements of the nested list in reverse order
            for (item in nestedList!!.reversed()) {
                stack.addLast(item)
            }
        }
        return stack.isNotEmpty() && stack.last().isInteger()
    }
}
```

```java
import java.util.*;

public class NestedIterator implements Iterator<Integer> {
    private final Deque<NestedInteger> stack = new ArrayDeque<>();

    /** @param nestedList the nested list to flatten */
    public NestedIterator(List<NestedInteger> nestedList) {
        for (int i = nestedList.size() - 1; i >= 0; i--) stack.push(nestedList.get(i));
    }

    /** @return the next integer (hasNext guarantees the top is an integer) */
    @Override
    public Integer next() {
        return stack.pop().getInteger();
    }

    /** @return true iff another integer remains; expands nested lists on demand */
    @Override
    public boolean hasNext() {
        while (!stack.isEmpty() && !stack.peek().isInteger()) {
            List<NestedInteger> list = stack.pop().getList();
            for (int i = list.size() - 1; i >= 0; i--) stack.push(list.get(i));
        }
        return !stack.isEmpty();
    }
}
```

```cpp
#include <vector>

// NestedInteger interface (problem-defined):
//   bool isInteger(); int getInteger(); vector<NestedInteger>& getList();

class NestedIterator {
    std::vector<NestedInteger> stack;

    void pushReverse(const std::vector<NestedInteger>& list) {
        for (auto it = list.rbegin(); it != list.rend(); ++it) stack.push_back(*it);
    }

public:
    /** @param nestedList the nested list to flatten */
    NestedIterator(std::vector<NestedInteger>& nestedList) { pushReverse(nestedList); }

    /** @return the next integer (hasNext guarantees the top is an integer) */
    int next() {
        int v = stack.back().getInteger();
        stack.pop_back();
        return v;
    }

    /** @return true iff another integer remains; expands nested lists on demand */
    bool hasNext() {
        while (!stack.empty() && !stack.back().isInteger()) {
            auto list = stack.back().getList();
            stack.pop_back();
            pushReverse(list);
        }
        return !stack.empty();
    }
};
```

```python
class NestedIterator:
    """@param nested_list: the nested list to flatten"""

    def __init__(self, nested_list):
        self.stack = list(reversed(nested_list))   # reversed so the first item is on top

    def next(self) -> int:
        """@return: the next integer (hasNext guarantees the top is an integer)"""
        return self.stack.pop().get_integer()

    def has_next(self) -> bool:
        """@return: true iff another integer remains; expands nested lists on demand"""
        while self.stack and not self.stack[-1].is_integer():
            nested = self.stack.pop().get_list()
            self.stack.extend(reversed(nested))    # push in reverse to preserve order
        return bool(self.stack)
```

```rust
// Illustrative: the NestedInteger enum is problem-defined; the iterator's
// stack-with-reverse-push logic is identical to the Kotlin/Java/C++ blocks.
impl Solution {
    // (No standalone type; see the Kotlin/Java/C++ implementations for the pattern.)
}
```

## Dry run

**Input:** `[[1,1],2,[1,1]]` — items: L[1,1], 2, L[1,1].

```
init: stack = [L[1,1], 2, L[1,1]] (top = L[1,1])

hasNext(): top is a list -> pop L[1,1], push 1,1 (reversed).  stack = [1, 1, 2, L[1,1]].
  top is integer -> true.  next() -> 1.   stack = [1, 2, L[1,1]]
hasNext(): top is integer -> true.  next() -> 1.   stack = [2, L[1,1]]
hasNext(): true.  next() -> 2.   stack = [L[1,1]]
hasNext(): top is a list -> pop, push 1,1.  stack = [1, 1].  true.  next() -> 1.  stack = [1]
hasNext(): true.  next() -> 1.   stack = []
hasNext(): false ✓
```

The expansion is strictly on-demand: the second `L[1,1]` stays *unexpanded* until the iterator reaches it — nothing is flattened upfront. Each nested item is popped once and pushed once, which is the O(1)-amortized claim.

## Complexity

**Time.** Each item pushed and popped once:

$$
T(n) = O(1) \text{ amortized per operation}
$$

**Space.** The stack (worst case holds all items):

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Peeking Iterator** ([18.4](peeking-iterator.md)) — the one-element-buffer member of the lazy-iterator family.
- **Mini Parser / Nested List Weight Sum** — the same nesting processed recursively (DFS) instead of lazily.
- **Interview follow-up:** "Why is `hasNext` the right place to expand?" `next()` must be O(1) and the API guarantees `next()` is only called after `hasNext()` returned true — so the flattening work belongs in `hasNext`, where it's amortized over the traversal. Expanding in the constructor would trade laziness (and memory) for nothing.
