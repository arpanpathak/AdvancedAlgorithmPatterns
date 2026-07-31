# 10.27 Snapshot Array

> **Source**: [`src/main/kotlin/array/hashtable/SnapshotArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/SnapshotArray.kt)
> **Pattern**: per-index history logs · **Core page**

## The Problem

`set(i, val)`, `snap()` → id, `get(i, snapId)` returning the value at that snapshot.

- Constraints: n, ops ≤ 5×10⁴.

## Examples

```
["SnapshotArray","set","snap","set","get"]
[[3],[0,5],[],[0,6],[0,0]]
-> [null,null,0,null,5]
```

## Intuition — each index keeps a (snapId → value) log; get = floor lookup

Only *changes* are stored: `set` records `(currentSnapId, val)`; `get` finds the latest entry ≤ the query snap — the `TreeMap.floorEntry`:

```kotlin
private var snapId = 0
private val historyRecords = Array(length) { TreeMap<Int, Int>().apply { put(0, 0) } }

fun set(index: Int, `val`: Int) {
    historyRecords[index][snapId] = `val`
}

fun snap(): Int {
    return snapId++
}

fun get(index: Int, snapId: Int): Int {
    return historyRecords[index].floorEntry(snapId)?.value ?: 0
}
```

**Why logs instead of full copies?** Copying the whole array per snap is O(n·snaps); the per-index change-log is O(changes) total — the [18.x](../ch18-design-caches/pattern-primer.md) design tradeoff (space for time), with the floor lookup answering "what was the last set before snap?".

## Approach 1 — Full array copies per snap (O(n) per snap)

Snapshots as deep copies: correct, heavy.

## Approach 2 — Per-index logs (the repo's version, optimal)

```kotlin
class SnapshotArray(length: Int) {
    private var snapId = 0
    private val historyRecords = Array(length) { TreeMap<Int, Int>().apply { put(0, 0) } }

    /**
     * @param index index to set
     * @param val   value
     */
    fun set(index: Int, `val`: Int) {
        historyRecords[index][snapId] = `val`
    }

    /**
     * @return the new snapshot id
     */
    fun snap(): Int {
        return snapId++
    }

    /**
     * @param index  index to read
     * @param snapId snapshot id
     * @return       value at that snapshot
     */
    fun get(index: Int, snapId: Int): Int {
        return historyRecords[index].floorEntry(snapId)?.value ?: 0
    }
}
```

```java
import java.util.*;

public class SnapshotArray {
    private int snapId = 0;
    private final TreeMap<Integer, Integer>[] history;

    @SuppressWarnings("unchecked")
    public SnapshotArray(int length) {
        history = new TreeMap[length];
        for (int i = 0; i < length; i++) {
            history[i] = new TreeMap<>();
            history[i].put(0, 0);
        }
    }

    /**
     * @param index index to set
     * @param val   value
     */
    public void set(int index, int val) {
        history[index].put(snapId, val);
    }

    /**
     * @return the new snapshot id
     */
    public int snap() {
        return snapId++;
    }

    /**
     * @param index  index to read
     * @param snapId snapshot id
     * @return       value at that snapshot
     */
    public int get(int index, int snapId) {
        Map.Entry<Integer, Integer> entry = history[index].floorEntry(snapId);
        return entry == null ? 0 : entry.getValue();
    }
}
```

```cpp
#include <map>
#include <vector>

class SnapshotArray {
    std::vector<std::map<int, int>> history;
    int snapId = 0;

public:
    SnapshotArray(int length) : history(length) {
        for (auto& h : history) h[0] = 0;
    }

    /**
     * @param index index to set
     * @param val   value
     */
    void set(int index, int val) {
        history[index][snapId] = val;
    }

    /**
     * @return the new snapshot id
     */
    int snap() {
        return snapId++;
    }

    /**
     * @param index  index to read
     * @param snapId snapshot id
     * @return       value at that snapshot
     */
    int get(int index, int snapId) {
        auto it = history[index].upper_bound(snapId);
        if (it == history[index].begin()) return 0;
        return std::prev(it)->second;
    }
};
```

```python
from bisect import bisect_right

class SnapshotArray:
    def __init__(self, length: int):
        self.logs = [[(0, 0)] for _ in range(length)]   # (snap, value)
        self.snap_id = 0

    def set(self, index: int, val: int) -> None:
        self.logs[index].append((self.snap_id, val))

    def snap(self) -> int:
        self.snap_id += 1
        return self.snap_id - 1

    def get(self, index: int, snap_id: int) -> int:
        logs = self.logs[index]
        i = bisect_right(logs, (snap_id, float("inf"))) - 1
        return logs[i][1]
```

```rust
use std::collections::BTreeMap;

struct SnapshotArray {
    history: Vec<BTreeMap<i32, i32>>,
    snap_id: i32,
}

impl SnapshotArray {
    fn new(length: i32) -> Self {
        let mut history = Vec::new();
        for _ in 0..length {
            let mut map = BTreeMap::new();
            map.insert(0, 0);
            history.push(map);
        }
        Self { history, snap_id: 0 }
    }

    /// @param index index to set
    /// @param val   value
    fn set(&mut self, index: i32, val: i32) {
        self.history[index as usize].insert(self.snap_id, val);
    }

    /// @return the new snapshot id
    fn snap(&mut self) -> i32 {
        self.snap_id += 1;
        self.snap_id - 1
    }

    /// @param index  index to read
    /// @param snap_id snapshot id
    /// @return       value at that snapshot
    fn get(&self, index: i32, snap_id: i32) -> i32 {
        *self.history[index as usize]
            .range(..=snap_id)
            .next_back()
            .map(|(_, v)| v)
            .unwrap_or(&0)
    }
}
```

## Dry run

**Input:** the example.

```
set(0, 5): log[0] = [(0,5)].
snap(): returns 0, snapId=1.
set(0, 6): log[0] = [(0,5),(1,6)].
get(0, 0): floor(0) = 5 ✓
```

## Complexity

**Time.** O(log changes) per op:

$$
T = O(\log C)
$$

**Space.** The logs:

$$
S = O(\text{total changes})
$$

## Variants & follow-ups

- **Design A Stack With Increment Operations** ([18.7](../ch18-design-caches/design-a-stack-with-increment-operations.md)) — the lazy-history design family.
- **Interview follow-up:** "Why not copy the array per snap?" Snaps ≤ 5×10⁴ with n ≤ 5×10⁴ — copies blow to 2.5×10⁹ cells. The per-index logs store only *changes*, and the floor lookup answers any snapshot in O(log changes).
