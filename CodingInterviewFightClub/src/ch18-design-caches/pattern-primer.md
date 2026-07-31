# 18.0 Pattern Primer — Composing Structures

Design problems are *complexity contracts*: "support these operations at O(1)". No single structure satisfies them; the skill is the **composition**. Four recurring composites:

## Map + order (LRU)

"Get and put in O(1)" is a hash map. "Evict the *least recently used*" adds *recency order* — and the trick is the **LinkedHashMap with access-order**: a hash map whose entries form a doubly-linked list, re-linked to the tail on every access. `removeEldestEntry` auto-evicts the head. The hand-rolled equivalent (hash map key→node + doubly linked list) is the interview answer when the language lacks it.

## Count + buckets (LFU)

LFU needs "evict the least *frequently* used, tie-break LRU". The composition is **three maps**: `val` → value, `val` → frequency, `freq` → ordered set of keys (LinkedHashSet). A get/push bumps the key into the next frequency bucket; eviction pops the first key of the *minimum* frequency bucket. The min-frequency counter is the only stateful bookkeeping — O(1) because buckets move in one step.

## Random access + O(1) delete (GetRandom)

Arrays give O(1) random index; maps give O(1) membership. The join is **swap-remove**: deleting an element swaps it with the *last* element, then pops — O(1) with no holes. The map keeps value → index so the swap can be located. The invariant "list is exactly the map's keys, compactly" is the whole design.

## Lazy state (iterators and the increment stack)

Iterators hold *deferred* state: the [peeking iterator](peeking-iterator.md) buffers one element ahead; the [flattened iterator](flatten-nested-list-iterator.md) keeps a stack of not-yet-flattened lists. The [increment stack](design-a-stack-with-increment-operations.md) delays bulk increments in an `increments` array, *carrying* the increment down one slot at pop — O(1) amortized by never touching the whole stack.

## The design checklist

1. **What's the contract?** — write the ops and their required complexities.
2. **Which structure gives each op its O(1)?** — map for membership, list for order, array for index.
3. **How do the structures stay consistent?** — the invariant each op must preserve (e.g., "the list contains exactly the map keys, in recency order").
4. **What's the eviction/cleanup rule?** — LRU head, LFU min-bucket first, swap-remove, buffer refill.

State the invariant out loud; the code then writes itself.
