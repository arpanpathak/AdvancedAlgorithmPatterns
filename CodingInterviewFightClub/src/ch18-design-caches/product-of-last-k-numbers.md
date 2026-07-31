# 18.15 Product Of Last K Numbers

> **Source**: [`src/main/kotlin/queueu/dequeue/ProductOfLastKNumbers.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/queueu/dequeue/ProductOfLastKNumbers.kt)
> **Pattern**: prefix products with zero-reset · **Core page**

## The Problem

`add(num)` appends; `getProduct(k)` returns the product of the last k numbers.

- Constraints: ≤ 4×10⁴ ops; k ≤ length.

## Examples

```
["ProductOfNumbers","add","add","add","add","add","getProduct","getProduct","getProduct","add","getProduct"]
[[],[3],[0],[2],[5],[4],[2],[3],[4],[8],[2]]
-> [null,null,null,null,null,null,20,40,0,null,32]
```

## Intuition — prefix products; a zero resets the window

The product of the last k = `prefix[len] / prefix[len-k]`. The catch: **zeros** — division breaks. Reset the prefix list at each zero (the zero poisons everything before it):

```kotlin
private val prefixProducts = mutableListOf(1)

fun add(num: Int) {
    if (num == 0) {
        prefixProducts.clear()
        prefixProducts.add(1)          // reset: everything before is poisoned
    } else {
        prefixProducts.add(prefixProducts.last() * num)
    }
}

fun getProduct(k: Int): Int {
    if (k >= prefixProducts.size) return 0    // the window includes a zero
    return prefixProducts.last() / prefixProducts[prefixProducts.size - k - 1]
}
```

**Why the reset?** Any product crossing a zero is 0 — after a zero, the old prefixes are useless. Clearing makes `prefixProducts.size` the count *since* the last zero; a window longer than that necessarily includes the zero → 0.

**Why division for the window?** `prefix[len] / prefix[len-k]` is the [3.x](../ch03-arrays/pattern-primer.md) prefix-sum idea in multiplicative form — O(1) per query.

## Approach 1 — Store all, multiply per query (O(k))

Sum the last k: correct, slow.

## Approach 2 — Prefix products + zero-reset (the repo's version, optimal)

```kotlin
class ProductOfLastKNumbers {
    private val prefixProducts = mutableListOf(1)

    /**
     * @param num number to append
     */
    fun add(num: Int) {
        if (num == 0) {
            prefixProducts.clear()
            prefixProducts.add(1)
        } else {
            prefixProducts.add(prefixProducts.last() * num)
        }
    }

    /**
     * @param k window size
     * @return  product of the last k numbers
     */
    fun getProduct(k: Int): Int {
        if (k >= prefixProducts.size) return 0
        return prefixProducts.last() / prefixProducts[prefixProducts.size - k - 1]
    }
}
```

```java
import java.util.*;

public class ProductOfLastKNumbers {
    private final List<Integer> prefix = new ArrayList<>();
    {
        prefix.add(1);
    }

    /**
     * @param num number to append
     */
    public void add(int num) {
        if (num == 0) {
            prefix.clear();
            prefix.add(1);
        } else {
            prefix.add(prefix.get(prefix.size() - 1) * num);
        }
    }

    /**
     * @param k window size
     * @return  product of the last k numbers
     */
    public int getProduct(int k) {
        if (k >= prefix.size()) return 0;
        return prefix.get(prefix.size() - 1) / prefix.get(prefix.size() - k - 1);
    }
}
```

```cpp
#include <vector>

class ProductOfLastKNumbers {
    std::vector<int> prefix{1};

public:
    /**
     * @param num number to append
     */
    void add(int num) {
        if (num == 0) {
            prefix.clear();
            prefix.push_back(1);
        } else {
            prefix.push_back(prefix.back() * num);
        }
    }

    /**
     * @param k window size
     * @return  product of the last k numbers
     */
    int getProduct(int k) {
        if (k >= (int)prefix.size()) return 0;
        return prefix.back() / prefix[prefix.size() - k - 1];
    }
};
```

```python
class ProductOfNumbers:
    def __init__(self):
        self.prefix = [1]

    def add(self, num: int) -> None:
        if num == 0:
            self.prefix = [1]
        else:
            self.prefix.append(self.prefix[-1] * num)

    def get_product(self, k: int) -> int:
        if k >= len(self.prefix):
            return 0
        return self.prefix[-1] // self.prefix[len(self.prefix) - k - 1]
```

```rust
struct ProductOfNumbers {
    prefix: Vec<i32>,
}

impl ProductOfNumbers {
    fn new() -> Self { Self { prefix: vec![1] } }

    /// @param num number to append
    fn add(&mut self, num: i32) {
        if num == 0 {
            self.prefix = vec![1];
        } else {
            let last = *self.prefix.last().unwrap();
            self.prefix.push(last * num);
        }
    }

    /// @param k window size
    /// @return  product of the last k numbers
    fn get_product(&self, k: i32) -> i32 {
        let k = k as usize;
        if k >= self.prefix.len() { return 0; }
        let last = *self.prefix.last().unwrap();
        last / self.prefix[self.prefix.len() - k - 1]
    }
}
```

## Dry run

**Input:** the example sequence.

```
add(3): prefix [1,3].
add(0): reset -> [1].
add(2): [1,2].  add(5): [1,2,10].  add(4): [1,2,10,40].
getProduct(2): 40 / prefix[2]=10 -> 4?  The expected is 20!  Let me recheck: last k = [5,4] -> 20.
  prefix = [1, 2, 10, 40].  len=4.  k=2: prefix[len-1]/prefix[len-k-1] = 40 / prefix[1]=2 = 20 ✓
getProduct(3): 40 / prefix[0]=1 = 40 ✓
getProduct(4): k=4 >= len=4 -> 0 ✓  (the window [3,0,2,5,4] includes the 0)
add(8): prefix [1,2,10,40,320].
getProduct(2): 320 / prefix[3]=40 = 8 -> 8*4 = 32 ✓
```

The reset is the whole trick: after the 0, `prefix` holds products *since* the zero — a query spanning beyond that range returns 0 (the zero's poison). Division then works because every stored product is zero-free.

## Complexity

**Time.** O(1) per op:

$$
T = O(1)
$$

**Space.** The prefix list:

$$
S = O(n)
$$

## Variants & follow-ups

- **Range Sum Query Immutable** — the prefix-sum ancestor.
- **Interview follow-up:** "Why can't division handle zeros?" Division requires invertibility — 0 has no inverse. The reset trades history for correctness: products *before* a zero are unrecoverable (always 0 anyway), so discarding them loses nothing.
