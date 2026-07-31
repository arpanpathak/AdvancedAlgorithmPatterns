# 18.4 Peeking Iterator

> **Source:** [`src/main/kotlin/design/PeekingIterator.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/design/PeekingIterator.kt)
> **Pattern:** one-element buffer · **Core page**

## The Problem

Design an iterator that wraps a plain iterator and adds **`peek()`** — return the next element **without advancing**.

- Constraints: standard iterator semantics; `peek` must be O(1).

## Examples

```
Iterator = [1,2,3];  PeekingIterator it:
it.next() -> 1;  it.peek() -> 2;  it.next() -> 2;  it.next() -> 3;  it.hasNext() -> false
```

## Intuition — "peek" needs the *next value already fetched*

A plain iterator computes the next value lazily — asking "what's next?" without consuming it isn't possible. The wrapper's trick: **always keep the next value in a buffer**, fetched *ahead* of the consumer's position.

```
init:  if inner.hasNext(): nextValue = inner.next()     // prime the buffer
peek(): return nextValue!!                              // read without advancing
next(): val = nextValue; refill the buffer from inner; return val
hasNext(): nextValue != null                            // the buffer IS the answer
```

**Why does the buffer make `peek` O(1)?** The value is fetched once when the *previous* `next()` advanced; `peek` is a single field read. The inner iterator advances at most once per `next()` call — so the wrapper is amortized O(1) and never reads ahead more than one element.

**The null sentinel:** `nextValue = null` means "exhausted". The `init` block primes it; `next()` refills it or nulls it at the end. The classic pitfall — `hasNext()` consulting the *inner* iterator instead of the buffer — would report false before the buffered value was consumed.

**Why wrap instead of building a new iterator?** The pattern generalizes: any "lookahead" iterator (a `SkipIterator`, a `WindowIterator`) is a buffer + refill rule. The `peek` problem is the minimal instance of the *buffered iterator* family.

## Approach 1 — Materialize the whole sequence

Copy all elements into a list with an index: trivial, but breaks laziness (infinite iterators, O(n) memory).

## Approach 2 — One-element lookahead buffer (the repo's version, optimal)

```kotlin
class PeekingIterator(iterator: Iterator<Int>) : Iterator<Int> {
    private val innerIterator = iterator           // capture the wrapped iterator

    private var nextValue: Int? = null             // the buffered "next"

    init {
        // Prime the buffer immediately
        if (innerIterator.hasNext()) {
            nextValue = innerIterator.next()
        }
    }

    /** @return the next element WITHOUT advancing the iterator */
    fun peek(): Int {
        return nextValue!!                         // read the buffer, don't advance
    }

    /** @return the next element and advance */
    override fun next(): Int {
        val current = nextValue

        // Advance the inner iterator to refill the buffer
        if (innerIterator.hasNext()) {
            nextValue = innerIterator.next()
        } else {
            nextValue = null
        }
        return current!!
    }

    /** @return true iff there is a next element */
    override fun hasNext(): Boolean {
        return nextValue != null                   // the buffer IS the answer
    }
}
```

```java
import java.util.Iterator;

public class PeekingIterator implements Iterator<Integer> {
    private final Iterator<Integer> inner;
    private Integer next;                          // the buffered "next"

    /** @param iterator the iterator to wrap */
    public PeekingIterator(Iterator<Integer> iterator) {
        inner = iterator;
        if (inner.hasNext()) next = inner.next();  // prime the buffer
    }

    /** @return the next element WITHOUT advancing the iterator */
    public Integer peek() {
        return next;                               // read the buffer, don't advance
    }

    /** @return the next element and advance */
    @Override
    public Integer next() {
        Integer current = next;
        next = inner.hasNext() ? inner.next() : null;   // refill the buffer
        return current;
    }

    /** @return true iff there is a next element */
    @Override
    public boolean hasNext() {
        return next != null;                       // the buffer IS the answer
    }
}
```

```cpp
#include <iterator>

template <typename It>
class PeekingIterator {
    It inner;                  // wrapped iterator
    typename std::iterator_traits<It>::value_type next;
    bool hasNextValue;

    void refill() {
        if (inner != It{}) {   // (for this design: check the inner's validity / end)
            next = *inner;
            hasNextValue = true;
            ++inner;
        } else {
            hasNextValue = false;
        }
    }

public:
    explicit PeekingIterator(It it) : inner(it) { refill(); }   // prime the buffer

    /** @return the next element WITHOUT advancing the iterator */
    typename std::iterator_traits<It>::value_type peek() const { return next; }

    /** @return the next element and advance */
    typename std::iterator_traits<It>::value_type next() {
        auto v = next;
        refill();                                    // refill the buffer
        return v;
    }

    /** @return true iff there is a next element */
    bool hasNext() const { return hasNextValue; }
};
```

```python
class PeekingIterator:
    """@param iterator: the iterator to wrap"""

    def __init__(self, iterator):
        self.inner = iterator
        self._next = next(iterator, None)     # prime the buffer (None = exhausted)

    def peek(self):
        """@return: the next element WITHOUT advancing the iterator"""
        return self._next                     # read the buffer, don't advance

    def next(self):
        """@return: the next element and advance"""
        current = self._next
        self._next = next(self.inner, None)   # refill the buffer
        return current

    def has_next(self):
        """@return: true iff there is a next element"""
        return self._next is not None         # the buffer IS the answer
```

```rust
struct PeekingIterator<I: Iterator> {
    inner: I,
    next: Option<I::Item>,                    // the buffered "next"
}

impl<I: Iterator> PeekingIterator<I> {
    /// @param iter the iterator to wrap
    fn new(mut iter: I) -> Self {
        PeekingIterator { next: iter.next(), inner: iter }   // prime the buffer
    }

    /// @return the next element WITHOUT advancing the iterator
    fn peek(&self) -> Option<&I::Item> {
        self.next.as_ref()                    // read the buffer, don't advance
    }

    /// @return the next element and advance
    fn next(&mut self) -> Option<I::Item> {
        let current = self.next.take();
        self.next = self.inner.next();        // refill the buffer
        current
    }

    /// @return true iff there is a next element
    fn has_next(&self) -> bool {
        self.next.is_some()                   // the buffer IS the answer
    }
}
```

## Dry run

**Input:** `iterator = [1,2,3]`.

```
init:  nextValue = 1 (primed).

it.next() -> current = 1; refill -> nextValue = 2.  return 1.
it.peek() -> nextValue = 2 (no advance).  inner still positioned after 2.
it.next() -> current = 2; refill -> nextValue = 3.  return 2.
it.next() -> current = 3; refill -> nextValue = null (inner exhausted).  return 3.
it.hasNext() -> nextValue != null? NO -> false ✓
```

The rhythm: `next()` always leaves the *following* value buffered, so `peek()` between any two `next()` calls is a free read. The null sentinel at the end is what makes `hasNext()` truthful — the inner iterator is already exhausted, but the buffered value was still consumable.

## Complexity

**Time.** O(1) per operation, O(1) amortized (the inner advances once per `next`):

$$
T(n) = O(1) \text{ per operation}
$$

**Space.** One buffered element:

$$
S = O(1)
$$

## Variants & follow-ups

- **Flatten Nested List Iterator** ([18.5](flatten-nested-list-iterator.md)) — the same "lazy state" idea with a stack of pending lists instead of a one-element buffer.
- **Skip Iterator / lookahead family** — any "peek ahead by k" iterator is this buffer generalized to a queue of k.
- **Interview follow-up:** "Why buffer instead of tracking the inner iterator and advancing on peek?" Advancing the inner on `peek` would *consume* the element — exactly what peek must not do. The buffer holds the consumed element so the wrapper can return it twice (once from peek, once from next) while the inner advances only once.
