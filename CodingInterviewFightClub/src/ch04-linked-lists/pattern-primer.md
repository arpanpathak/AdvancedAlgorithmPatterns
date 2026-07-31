# 4.0 Pattern Primer — Pointer Choreography

Linked lists punish careless pointer handling and reward a small set of moves. Master these five and most list problems become assembly.

## Move 1 — The dummy node

When the head can change (deletion, insertion, merging), create a sentinel:

```
dummy -> head
result = dummy (hold on to it)
... operate on dummy.next ...
return dummy.next
```

The dummy kills every "what if head is null / head must be removed / list is empty?" special case. Used in [4.3](merge-two-sorted-lists.md), [4.4](remove-nth-node-from-end.md).

## Move 2 — The tortoise and the hare

`slow` moves 1 step, `fast` moves 2:

- **Cycle detection:** they meet iff a cycle exists ([4.2](linked-list-cycle.md)).
- **Middle node:** when `fast` ends, `slow` is the middle.
- **Nth from the end:** run `fast` ahead by n, then walk both — when `fast` ends, `slow` is the target ([4.4](remove-nth-node-from-end.md)).

## Move 3 — Recursion as "rewire after the subproblem"

To reverse a list, recurse to the tail first, then rewire on the way back:

```
reverse(head):
    if head.next is null: return head
    newHead = reverse(head.next)
    head.next.next = head      # point the successor back at us
    head.next = null           # we become the new tail
    return newHead
```

The recursion *defers* the pointer work until the call returns, which makes "reverse from here to the end" a one-liner. The iterative version ([4.1](reverse-linked-list.md)) does the same rewiring in a loop with `prev` — same semantics, no stack.

## Move 4 — The two-pointer offset (k-skip)

For "kth from the end" or "rotate by k", advance one pointer by k first, then walk both in lockstep. The offset IS the answer — no length computation, no backtracking.

## Move 5 — Compare-and-advance merge

Merging sorted lists: walk both heads, always take the smaller, append to a tail. Because both inputs are sorted, the "smallest remaining" is always one of the two heads — $O(1)$ per step, no rescanning ([4.3](merge-two-sorted-lists.md)). This is the primitive behind Merge K Sorted Lists and even mergesort itself.

## Complexity intuition

Every move above is $O(n)$ with $O(1)$ extra space (except recursion, which costs $O(n)$ stack). The list structure gives you *no random access* — that's the whole reason these dances exist, and also why "find the middle" is $O(n)$ here but $O(1)$ in an array. Always state that contrast when asked.
