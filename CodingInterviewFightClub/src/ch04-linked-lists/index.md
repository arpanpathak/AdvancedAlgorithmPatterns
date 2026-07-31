# Chapter 4 — Linked Lists

> **Source:** `src/main/kotlin/linkedlist/`
>
> **Master idea:** linked lists are *pointer choreography*. Every hard-looking problem is one of a handful of moves — dummy nodes, two-pointer runs, or recursion — applied twice.
>
> **Prerequisites:** know what a `ListNode` is (`val` + `next`). Everything else is taught here.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 4.1 | Reverse Linked List | recursion + iteration | $O(n)$ | [→](reverse-linked-list.md) |
| 4.2 | Linked List Cycle | Floyd's tortoise & hare | $O(n)$ | [→](linked-list-cycle.md) |
| 4.3 | Merge Two Sorted Lists | dummy node + two pointers | $O(n+m)$ | [→](merge-two-sorted-lists.md) |
| 4.4 | Remove Nth Node From End | dummy node + offset pointers | $O(n)$ | [→](remove-nth-node-from-end.md) |
| 4.5 | Linked List Cycle II | Floyd's with entry-point math | $O(n)$ | [→](linked-list-cycle-ii.md) |

| 4.6 | Find The Duplicate Number | Floyd on an implicit graph | $O(n)$ | [→](find-the-duplicate-number.md) |
| 4.7 | Add Two Numbers | digit-wise carry | $O(n)$ | [→](add-two-numbers.md) |
| 4.8 | Middle Of The Linked List | slow-fast pointers | $O(n)$ | [→](middle-of-the-linked-list.md) |
| 4.9 | Palindrome Linked List | middle + reverse + compare | $O(n)$ | [→](palindrome-linked-list.md) |
| 4.10 | Copy List With Random Pointer | node-map deep copy | $O(n)$ | [→](copy-list-with-random-pointer.md) |
| 4.11 | Swap Nodes In Pairs | dummy-head rewire | $O(n)$ | [→](swap-nodes-in-pairs.md) |
| 4.12 | Reverse Nodes In K Groups | block reversal | $O(n)$ | [→](reverse-nodes-in-k-groups.md) |
| 4.13 | Insert Into A Sorted Circular List | circular boundary insert | $O(n)$ | [→](insert-into-a-sorted-circular-linked-list.md) |
| 4.14 | Maximum Twin Sum | middle + reverse + pair | $O(n)$ | [→](maximum-twin-sum.md) |
## The rest of the linkedlist/ directory

`src/main/kotlin/linkedlist/` holds 20+ more: Palindrome, Middle Node, Odd-Even, Swap Nodes in Pairs, Reverse Nodes in K Groups, Rotate List, Merge K Sorted Lists (heap + iterative), Add Two Numbers, Copy List with Random Pointer, Intersection of Two Linked Lists, Insert Into a Sorted Circular List, Maximum Twin Sum, and more. New pages land in the table above as they're written; the rest are cataloged in the repository's own tree.
