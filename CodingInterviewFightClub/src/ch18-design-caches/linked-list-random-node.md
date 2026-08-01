# 18.22 Linked List Random Node

> **Source**: [`src/main/kotlin/probability/LinkedListRandomNode.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/probability/LinkedListRandomNode.kt)
> **Pattern**: reservoir sampling · **Core page**

## The Problem

`getRandom()` returns a uniformly random node's value — **without knowing the length**.

- Constraints: n ≤ 10⁴; ≤ 10⁴ calls.

## Examples

```
["Solution","getRandom","getRandom","getRandom"]
[[[1,2,3]],[],[],[]]
-> [null,1/2/3 each with prob 1/3]
```

## Intuition — reservoir sampling: keep the i-th node with probability 1/i

Walk the list once; for the i-th node, replace the stored value with probability 1/i — every position ends up equally likely:

```kotlin
fun getRandom(): Int {
    var (count, result) = 0 to 0
    var ptr = head

    while (ptr != null) {
        count++

        if (Random.nextInt(count) == 0) {
            result = ptr.`val`
        }
        ptr = ptr.next
    }
    return result
}
```

**Why the 1/i rule?** At step i, the new node survives with prob 1/i; each earlier survivor keeps its slot with prob (1 − 1/i)... the telescoping product gives every node exactly 1/n. The [18.x](../ch18-design-caches/weighted-reservoir-sampling.md) reservoir engine — the [10.26](../ch10-hash-tables/detect-squares.md) "stream without length" trick.

## Approach 1 — Count then random index (two passes)

Find n, pick a random index, walk again: correct, two passes.

## Approach 2 — Reservoir (the repo's version, optimal, one pass)

```kotlin
import java.util.*

class LinkedListRandomNode(private val head: ListNode?) {
    /**
     * @return a uniformly random node value
     */
    fun getRandom(): Int {
        var (count, result) = 0 to 0
        var ptr = head

        while (ptr != null) {
            count++

            if (Random.nextInt(count) == 0) {
                result = ptr.`val`
            }
            ptr = ptr.next
        }
        return result
    }
}
```

```java
import java.util.*;

public class LinkedListRandomNode {
    private final ListNode head;

    public LinkedListRandomNode(ListNode head) { this.head = head; }

    /**
     * @return a uniformly random node value
     */
    public int getRandom() {
        int count = 0, result = 0;
        ListNode ptr = head;

        while (ptr != null) {
            count++;
            if (new Random().nextInt(count) == 0) result = ptr.val;
            ptr = ptr.next;
        }
        return result;
    }
}
```

```cpp
#include <cstdlib>

class LinkedListRandomNode {
    ListNode* head;

public:
    LinkedListRandomNode(ListNode* head) : head(head) {}

    /**
     * @return a uniformly random node value
     */
    int getRandom() {
        int count = 0, result = 0;
        ListNode* ptr = head;

        while (ptr) {
            count++;
            if (rand() % count == 0) result = ptr->val;
            ptr = ptr->next;
        }
        return result;
    }
};
```

```python
import random

class LinkedListRandomNode:
    def __init__(self, head):
        self.head = head

    def get_random(self) -> int:
        count = 0
        result = 0
        ptr = self.head

        while ptr:
            count += 1
            if random.randint(0, count - 1) == 0:
                result = ptr.val
            ptr = ptr.next

        return result
```

```rust
use rand::Rng;

struct Solution {
    head: Option<Box<ListNode>>,
}

impl Solution {
    fn new(head: Option<Box<ListNode>>) -> Self { Self { head } }

    /// @return a uniformly random node value
    fn get_random(&self) -> i32 {
        let mut count = 0;
        let mut result = 0;
        let mut ptr = &self.head;

        while let Some(node) = ptr {
            count += 1;
            if rand::thread_rng().gen_range(0..count) == 0 { result = node.val; }
            ptr = &node.next;
        }
        result
    }
}
```

## Dry run

**Input:** list [1,2,3].

```
count=1: keep 1 (prob 1).  count=2: replace with 2 (prob 1/2).  count=3: replace with 3 (prob 1/3).
P(1 survives) = 1 * 1/2 * 2/3 = 1/3.  P(2) = 1/2 * 2/3 = 1/3.  P(3) = 1/3.  Uniform ✓
```

## Complexity

**Time.** One pass per call:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Random Pick Index** ([18.23](random-pick-index.md)) — the same reservoir for array indices.
- **Weighted Reservoir Sampling** ([18.x](../ch18-design-caches/weighted-reservoir-sampling.md)) — the generalization with weights.
- **Interview follow-up:** "Why can't you just pick a random index?" The length is unknown without a first pass; the reservoir's single pass replaces the counting pass — the [18.x](../ch18-design-caches/pattern-primer.md) streaming-random discipline.
