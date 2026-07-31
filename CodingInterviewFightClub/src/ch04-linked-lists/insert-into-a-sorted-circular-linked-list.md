# 4.13 Insert Into A Sorted Circular Linked List

> **Source:** [`src/main/kotlin/linkedlist/InsertIntoASortedCircularLinkedList.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/linkedlist/InsertIntoASortedCircularLinkedList.kt)
> **Pattern:** circular boundary insertion · **Core page**

## The Problem

Insert a value into a **sorted circular** linked list (any node given; may be null).

- Constraints: n ≤ 5×10⁴; list sorted ascending.

## Examples

```
Input:  head = [3,4,1], insertVal = 2   -> Output: [3,4,1,2]
Input:  head = [], insertVal = 1        -> Output: [1] (self-loop)
Input:  head = [1,1,1], insertVal = 0   -> Output: [0,1,1,1]
```

## Intuition — three insertion spots: mid-run, the wrap, or anywhere

Walking the circle, insert when:
1. `current.val <= insertVal <= current.next.val` — the **normal** sorted gap;
2. `current.val > current.next.val` — the **wrap point** (max → min): insert here if `insertVal ≥ current.val` (it's the new max) or `insertVal ≤ current.next.val` (new min);
3. otherwise, after a full lap (all-equal list) — insert anywhere.

```kotlin
var current: Node? = head
do {
    when {
        current?.`val`!! <= insertVal && insertVal <= current?.next?.`val`!! -> {
            newNode.next = current?.next
            current?.next = newNode
            return head
        }
        current?.`val`!! > current?.next?.`val`!! -> {   // the wrap
            if (insertVal >= current?.`val`!! || insertVal <= current?.next?.`val`!!) {
                newNode.next = current?.next
                current?.next = newNode
                return head
            }
        }
    }
    current = current?.next
} while (current != head)

// full lap with no gap: all values equal -> insert anywhere
newNode.next = head?.next
head?.next = newNode
return head
```

**Why the `do-while`?** The list is circular — the loop must run at least once and stop when it returns to the head. A `while` would skip the first check.

**Why is the wrap the special case?** At the max→min boundary, the sorted order "wraps" — a value larger than the max or smaller than the min belongs exactly there. The two `||` conditions cover both.

## Approach 1 — Scan with the three cases (the repo's version, optimal)

```kotlin
class InsertIntoASortedCircularLinkedList {
    class Node(var `val`: Int) {
        var next: Node? = null
    }

    /**
     * @param head      any node of the sorted circular list (or null)
     * @param insertVal value to insert
     * @return          a node of the updated list
     */
    fun insert(head: Node?, insertVal: Int): Node? {
        val newNode = Node(insertVal)
        if (head == null) return newNode.apply { next = newNode }

        var current: Node? = head
        do {
            when {
                current?.`val`!! <= insertVal && insertVal <= current?.next?.`val`!! -> {
                    newNode.next = current?.next
                    current?.next = newNode
                    return head
                }
                current?.`val`!! > current?.next?.`val`!! -> {
                    if (insertVal >= current?.`val`!! || insertVal <= current?.next?.`val`!!) {
                        newNode.next = current?.next
                        current?.next = newNode
                        return head
                    }
                }
            }
            current = current?.next
        } while (current != head)

        newNode.next = head?.next        // all-equal: any insertion point
        head?.next = newNode
        return head
    }
}
```

```java
public class InsertIntoASortedCircularLinkedList {
    static class Node {
        int val;
        Node next;
        Node(int v) { val = v; }
    }

    /**
     * @param head      any node of the sorted circular list (or null)
     * @param insertVal value to insert
     * @return          a node of the updated list
     */
    public Node insert(Node head, int insertVal) {
        Node newNode = new Node(insertVal);
        if (head == null) { newNode.next = newNode; return newNode; }

        Node cur = head;
        do {
            if (cur.val <= insertVal && insertVal <= cur.next.val) {
                newNode.next = cur.next;
                cur.next = newNode;
                return head;
            }
            if (cur.val > cur.next.val) {                    // the wrap
                if (insertVal >= cur.val || insertVal <= cur.next.val) {
                    newNode.next = cur.next;
                    cur.next = newNode;
                    return head;
                }
            }
            cur = cur.next;
        } while (cur != head);

        newNode.next = head.next;                            // all-equal
        head.next = newNode;
        return head;
    }
}
```

```cpp
class InsertIntoASortedCircularLinkedList {
    struct Node {
        int val;
        Node* next;
        Node(int v) : val(v), next(nullptr) {}
    };

public:
    /**
     * @param head      any node of the sorted circular list (or null)
     * @param insertVal value to insert
     * @return          a node of the updated list
     */
    Node* insert(Node* head, int insertVal) {
        Node* newNode = new Node(insertVal);
        if (!head) { newNode->next = newNode; return newNode; }

        Node* cur = head;
        do {
            if (cur->val <= insertVal && insertVal <= cur->next->val) {
                newNode->next = cur->next;
                cur->next = newNode;
                return head;
            }
            if (cur->val > cur->next->val) {                 // the wrap
                if (insertVal >= cur->val || insertVal <= cur->next->val) {
                    newNode->next = cur->next;
                    cur->next = newNode;
                    return head;
                }
            }
            cur = cur->next;
        } while (cur != head);

        newNode->next = head->next;                          // all-equal
        head->next = newNode;
        return head;
    }
};
```

```python
def insert(head: "Optional[Node]", insert_val: int) -> "Optional[Node]":
    """
    @param head:       any node of the sorted circular list (or null)
    @param insert_val: value to insert
    @return:           a node of the updated list
    """
    new_node = Node(insert_val)
    if not head:
        new_node.next = new_node
        return new_node

    cur = head
    while True:
        if cur.val <= insert_val <= cur.next.val:
            new_node.next = cur.next
            cur.next = new_node
            return head

        if cur.val > cur.next.val:            # the wrap
            if insert_val >= cur.val or insert_val <= cur.next.val:
                new_node.next = cur.next
                cur.next = new_node
                return head

        cur = cur.next
        if cur is head:
            break

    new_node.next = head.next                 # all-equal
    head.next = new_node
    return head
```

```rust
impl Solution {
    /// @param head      any node of the sorted circular list (or null)
    /// @param insert_val value to insert
    /// @return          a node of the updated list
    pub fn insert(head: Option<Rc<RefCell<Node>>>, insert_val: i32) -> Option<Rc<RefCell<Node>>> {
        let new_node = Rc::new(RefCell::new(Node::new(insert_val)));

        let head = match head {
            None => { new_node.borrow_mut().next = Some(new_node.clone()); return Some(new_node); }
            Some(h) => h,
        };

        let mut cur = head.clone();
        loop {
            let (cur_val, next_val, next) = {
                let c = cur.borrow();
                let nx = c.next.clone().unwrap();
                (c.val, nx.borrow().val, nx)
            };

            if (cur_val <= insert_val && insert_val <= next_val)
                || (cur_val > next_val && (insert_val >= cur_val || insert_val <= next_val)) {
                new_node.borrow_mut().next = Some(next.clone());
                cur.borrow_mut().next = Some(new_node.clone());
                return Some(head);
            }

            cur = next;
            if Rc::ptr_eq(&cur, &head) { break; }
        }

        new_node.borrow_mut().next = head.borrow().next.clone();
        head.borrow_mut().next = Some(new_node);
        Some(head)
    }
}
```

## Dry run

**Input:** `head = [3,4,1]` (circular), `insertVal = 2`.

```
cur=3: 3 <= 2? no.  3 > 4? no.  cur=4.
cur=4: 4 <= 2? no.  4 > 1 (wrap!) && (2 >= 4? no || 2 <= 1? no) -> not here.  cur=1.
cur=1: 1 <= 2 <= 3? YES -> insert 2 between 1 and 3.  [3,4,1,2] ✓

Input: [3,4,1], insertVal = 5: cur=4: wrap 4>1 && (5 >= 4 YES) -> insert after 4: [3,4,5,1] ✓
Input: [3,4,1], insertVal = 0: cur=4: wrap && (0 <= 1 YES) -> insert between 4 and 1: [3,4,0,1] ✓
```

The wrap case handles the max and min inserts in one place; the normal gap handles everything between. An all-equal list never finds a gap or wrap — the loop completes a lap and the fallback inserts anywhere, keeping the circle intact.

## Complexity

**Time.** At most one lap:

$$
T(n) = O(n)
$$

**Space.** The new node:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Insert Into A Sorted List** — the non-circular twin (no wrap case).
- **Interview follow-up:** "Why must the wrap check come before continuing?" The wrap point is the only place the sorted order breaks — a value that belongs at the end/beginning can only be placed there. Checking it each step (not just once) is safe: the wrap occurs exactly once per lap, and any qualifying value must land at it.
