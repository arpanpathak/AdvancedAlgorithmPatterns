# 18.16 Design Circular Queue

> **Source**: [`src/main/kotlin/queueu/dequeue/DesignACircularQueue.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/queueu/dequeue/DesignACircularQueue.kt)
> **Pattern**: ring buffer with modular arithmetic · **Core page**

## The Problem

A fixed-size queue reusing space: `enQueue`, `deQueue`, `Front`, `Rear`, `isEmpty`, `isFull`.

- Constraints: k ≤ 1000.

## Examples

```
["MyCircularQueue","enQueue","enQueue","enQueue","enQueue","Rear","isFull","deQueue","enQueue","Rear"]
[[3],[1],[2],[3],[4],[],[],[],[4],[]]
-> [null,true,true,true,false,3,true,true,true,4]
```

## Intuition — an array + front/rear/size with `% capacity`

```kotlin
class MyCircularQueue(k: Int) {
    private val queue = IntArray(k)
    private var front = 0
    private var rear = 0
    private var size = 0
    private val capacity = k

    fun enQueue(value: Int): Boolean {
        if (isFull()) return false
        queue[rear] = value
        rear = (rear + 1) % capacity     // wrap
        size++
        return true
    }

    fun deQueue(): Boolean {
        if (isEmpty()) return false
        front = (front + 1) % capacity   // wrap
        size--
        return true
    }

    fun Front(): Int = queue[front]
    fun Rear(): Int = queue[(rear - 1 + capacity) % capacity]
    fun isEmpty() = size == 0
    fun isFull() = size == capacity
}
```

**Why `% capacity`?** The ring wraps: after the last slot, `rear` returns to 0. The modular arithmetic IS the circularity — no shifting ever.

**Why track `size` separately?** `front == rear` is ambiguous (empty vs full) with a ring — the size counter disambiguates. The [7.9](../ch07-heaps/design-hit-counter.md) design-family state discipline.

## Approach 1 — Array with shifting (O(n) dequeue)

Shift everything left on pop: correct, slow.

## Approach 2 — Ring buffer (the repo's version, optimal)

```kotlin
class MyCircularQueue(k: Int) {
    private val queue = IntArray(k)
    private var front = 0
    private var rear = 0
    private var size = 0
    private val capacity = k

    /**
     * @param value value to enqueue
     * @return      false if full
     */
    fun enQueue(value: Int): Boolean {
        if (isFull()) return false
        queue[rear] = value
        rear = (rear + 1) % capacity
        size++
        return true
    }

    /**
     * @return false if empty
     */
    fun deQueue(): Boolean {
        if (isEmpty()) return false
        front = (front + 1) % capacity
        size--
        return true
    }

    fun Front(): Int = queue[front]
    fun Rear(): Int = queue[(rear - 1 + capacity) % capacity]
    fun isEmpty(): Boolean = size == 0
    fun isFull(): Boolean = size == capacity
}
```

```java
public class MyCircularQueue {
    private final int[] queue;
    private int front = 0, rear = 0, size = 0;

    public MyCircularQueue(int k) {
        queue = new int[k];
    }

    /**
     * @param value value to enqueue
     * @return      false if full
     */
    public boolean enQueue(int value) {
        if (isFull()) return false;
        queue[rear] = value;
        rear = (rear + 1) % queue.length;
        size++;
        return true;
    }

    /**
     * @return false if empty
     */
    public boolean deQueue() {
        if (isEmpty()) return false;
        front = (front + 1) % queue.length;
        size--;
        return true;
    }

    public int Front() { return queue[front]; }

    public int Rear() { return queue[(rear - 1 + queue.length) % queue.length]; }

    public boolean isEmpty() { return size == 0; }

    public boolean isFull() { return size == queue.length; }
}
```

```cpp
#include <vector>

class MyCircularQueue {
    std::vector<int> queue;
    int front = 0, rear = 0, size = 0;

public:
    MyCircularQueue(int k) : queue(k) {}

    /**
     * @param value value to enqueue
     * @return      false if full
     */
    bool enQueue(int value) {
        if (isFull()) return false;
        queue[rear] = value;
        rear = (rear + 1) % queue.size();
        size++;
        return true;
    }

    /**
     * @return false if empty
     */
    bool deQueue() {
        if (isEmpty()) return false;
        front = (front + 1) % queue.size();
        size--;
        return true;
    }

    int Front() { return queue[front]; }

    int Rear() { return queue[(rear - 1 + queue.size()) % queue.size()]; }

    bool isEmpty() { return size == 0; }

    bool isFull() { return size == (int)queue.size(); }
};
```

```python
class MyCircularQueue:
    def __init__(self, k: int):
        self.queue = [0] * k
        self.front = self.rear = self.size = 0

    def en_queue(self, value: int) -> bool:
        if self.is_full():
            return False
        self.queue[self.rear] = value
        self.rear = (self.rear + 1) % len(self.queue)
        self.size += 1
        return True

    def de_queue(self) -> bool:
        if self.is_empty():
            return False
        self.front = (self.front + 1) % len(self.queue)
        self.size -= 1
        return True

    def front(self) -> int:
        return self.queue[self.front]

    def rear(self) -> int:
        return self.queue[(self.rear - 1) % len(self.queue)]

    def is_empty(self) -> bool:
        return self.size == 0

    def is_full(self) -> bool:
        return self.size == len(self.queue)
```

```rust
struct MyCircularQueue {
    queue: Vec<i32>,
    front: usize,
    rear: usize,
    size: usize,
}

impl MyCircularQueue {
    fn new(k: i32) -> Self {
        Self { queue: vec![0; k as usize], front: 0, rear: 0, size: 0 }
    }

    /// @param value value to enqueue
    /// @return      false if full
    fn en_queue(&mut self, value: i32) -> bool {
        if self.is_full() { return false; }
        self.queue[self.rear] = value;
        self.rear = (self.rear + 1) % self.queue.len();
        self.size += 1;
        true
    }

    /// @return false if empty
    fn de_queue(&mut self) -> bool {
        if self.is_empty() { return false; }
        self.front = (self.front + 1) % self.queue.len();
        self.size -= 1;
        true
    }

    fn front(&self) -> i32 { self.queue[self.front] }

    fn rear(&self) -> i32 { self.queue[(self.rear + self.queue.len() - 1) % self.queue.len()] }

    fn is_empty(&self) -> bool { self.size == 0 }

    fn is_full(&self) -> bool { self.size == self.queue.len() }
}
```

## Dry run

**Input:** `k = 3`, `enQueue(1); enQueue(2); enQueue(3); enQueue(4); Rear; isFull; deQueue; enQueue(4); Rear`.

```
en 1: rear 0 -> queue[0]=1, rear=1.  size 1.
en 2: queue[1]=2, rear=2.  en 3: queue[2]=3, rear=0 (wrapped!).  size 3.
en 4: isFull -> false.
Rear: queue[(0-1+3)%3] = queue[2] = 3 ✓
isFull: true ✓
deQueue: front=1.  size 2.
en 4: queue[0]=4 (the freed slot), rear=1.  size 3.
Rear: queue[(1-1+3)%3] = queue[0] = 4 ✓
```

The wrap is visible: after en 3, `rear` returns to 0, and the next en writes into the slot *dequeued* earlier — the ring reuses space. The `% capacity` everywhere is the circularity; `size` keeps empty/full distinct.

## Complexity

**Time.** O(1) per op:

$$
T = O(1)
$$

**Space.** The fixed array:

$$
S = O(k)
$$

## Variants & follow-ups

- **Design Hit Counter** ([7.9](../ch07-heaps/design-hit-counter.md)) — the ring's bucket variant.
- **Interview follow-up:** "Why `size` and not `front == rear`?" In a ring, `front == rear` means *either* empty *or* full — the two states are indistinguishable without a counter (or a wasted slot). The size counter is the standard disambiguator.
