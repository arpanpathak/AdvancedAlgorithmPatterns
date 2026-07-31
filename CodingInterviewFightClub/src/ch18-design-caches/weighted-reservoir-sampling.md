# 18.8 Weighted Reservoir Sampling (A-Res)

> **Source:** the *Coding Interview Fight Club* notes (Weighted Stream Sampling for Recommendation Training); the repo's `probability/ReservoirSampling.kt` covers the unweighted case
> **Pattern:** randomized keys + min-heap of size k · **Core page**

## The Problem

Given an unbounded stream of events each with a **weight** `w`, maintain a fixed-size reservoir of `k` events so that the probability an event is in the final sample is **proportional to its weight**.

- Constraints: O(log k) per event; O(k) space regardless of stream length.

## Examples

```
stream: Scroll(1), Scroll(1), CLICK(100), Scroll(1), CLICK(100), Scroll(1), Purchase(500), k = 2
Result: the two high-weight events dominate the reservoir (CLICK_E, Purchase_G in the notes' run)
```

## Intuition — replace "uniform coin flips" with *weighted keys*, and keep the k largest

Unweighted reservoir sampling keeps the k largest random keys. **A-Res (Algorithm A-Res)** does the same, but the key is **weight-inflated**:

$$
K_i = U_i^{1 / w_i}, \qquad U_i \in (0, 1)
$$

Raising a uniform number to the power `1/w` pushes it **toward 1 as w grows** — a weight-100 event's key is almost always ~1 (near-certainly in the top-k), while a weight-1 event's key is uniform (rarely in the top-k). The selection probability ends up exactly proportional to weight.

**The min-heap of size k:** fill it with the first k events; for each later event, if its key exceeds the heap's *minimum* key, **replace** that minimum. The heap is "the k best keys so far" — its min is the admission threshold. (Compare [7.1](../ch07-heaps/top-k-frequent-elements.md)'s keep-top-k-by-heap shape; the "key" here is random.)

**Why `u.pow(1.0 / weight)`?** The math: `P(K_i > x) = x^{w_i}`; the probability that event i has the *largest* key among a set ends up `w_i / Σw` — exactly weight-proportional. The [16.0](../ch16-bit-manipulation/pattern-primer.md) "exponent as a scale" intuition: `1/w` is the weight's inverse stretch.

**The unweighted case** (`probability/ReservoirSampling.kt`) is the `w = 1` special case: a uniform random index replaces with probability `1/count` — the classic Algorithm R.

## Approach 1 — Collect the whole stream, sample by weight (O(N) space)

Weighted `randomChoice` at the end: exact, but violates "unbounded stream, O(k) space".

## Approach 2 — A-Res: keys + min-heap of k (the notes' version, optimal)

```kotlin
import java.util.*
import kotlin.math.pow

// The item, its assigned weight (W), and the calculated key (K)
data class WeightedEvent(val name: String, val weight: Int, val key: Double)

/**
 * Implements Weighted Reservoir Sampling (Algorithm A-Res).
 *
 * @param stream sequence of (item, weight) events
 * @param k      reservoir size
 * @return       the sampled items
 */
fun weightedReservoirSample(stream: Sequence<Pair<String, Int>>, k: Int): List<String> {
    // Min-heap: keeps the k items with the LARGEST keys; the top is the admission threshold
    val reservoir = PriorityQueue<WeightedEvent>(k) { a, b -> a.key.compareTo(b.key) }
    val random = Random()

    for ((item, weight) in stream) {
        // K_i = U_i ^ (1 / W_i) — weight-inflated random key
        val key = random.nextDouble().pow(1.0 / weight)

        if (reservoir.size < k) {
            reservoir.offer(WeightedEvent(item, weight, key))       // fill the reservoir
        } else {
            val leastDesirableEvent = reservoir.peek()              // smallest key in the reservoir
            if (key > leastDesirableEvent.key) {
                reservoir.poll()                                    // evict the weakest key
                reservoir.offer(WeightedEvent(item, weight, key))   // admit the stronger one
            }
        }
    }
    return reservoir.map { it.name }
}
```

```java
import java.util.*;

public class WeightedReservoirSampling {
    private record Event(String name, int weight, double key) {}

    /**
     * @param stream events as (item, weight)
     * @param k      reservoir size
     * @return       the sampled items
     */
    public List<String> sample(List<Map.Entry<String, Integer>> stream, int k) {
        PriorityQueue<Event> reservoir = new PriorityQueue<>(k, Comparator.comparingDouble(Event::key));
        Random random = new Random();

        for (Map.Entry<String, Integer> e : stream) {
            double key = Math.pow(random.nextDouble(), 1.0 / e.getValue());   // K_i = U^(1/w)

            if (reservoir.size() < k) {
                reservoir.offer(new Event(e.getKey(), e.getValue(), key));    // fill
            } else if (key > reservoir.peek().key()) {                        // beat the threshold
                reservoir.poll();                                             // evict the weakest
                reservoir.offer(new Event(e.getKey(), e.getValue(), key));    // admit
            }
        }

        return reservoir.stream().map(Event::name).toList();
    }
}
```

```cpp
#include <cmath>
#include <queue>
#include <random>
#include <string>
#include <vector>

class WeightedReservoirSampling {
    struct Event {
        std::string name;
        int weight;
        double key;
        bool operator>(const Event& o) const { return key > o.key; }
    };

public:
    /**
     * @param stream events as (item, weight)
     * @param k      reservoir size
     * @return       the sampled items
     */
    std::vector<std::string> sample(std::vector<std::pair<std::string, int>>& stream, int k) {
        std::priority_queue<Event, std::vector<Event>, std::greater<Event>> reservoir;
        std::random_device rd;
        std::mt19937 gen(rd());
        std::uniform_real_distribution<double> dist(0.0, 1.0);

        for (auto& [item, weight] : stream) {
            double key = std::pow(dist(gen), 1.0 / weight);    // K_i = U^(1/w)

            if ((int)reservoir.size() < k) {
                reservoir.push({item, weight, key});           // fill
            } else if (key > reservoir.top().key) {            // beat the threshold
                reservoir.pop();                               // evict the weakest
                reservoir.push({item, weight, key});           // admit
            }
        }

        std::vector<std::string> result;
        while (!reservoir.empty()) { result.push_back(reservoir.top().name); reservoir.pop(); }
        return result;
    }
};
```

```python
import heapq
import math
import random


def weighted_reservoir_sample(stream, k: int) -> list[str]:
    """
    @param stream: events as (item, weight)
    @param k:      reservoir size
    @return:       the sampled items
    """
    reservoir = []          # min-heap of (-key, item): keeps the k LARGEST keys
    for item, weight in stream:
        key = random.random() ** (1.0 / weight)      # K_i = U^(1/w)

        if len(reservoir) < k:
            heapq.heappush(reservoir, (-key, item))  # fill the reservoir
        elif key > -reservoir[0][0]:                 # beat the threshold (min key)
            heapq.heapreplace(reservoir, (-key, item))   # evict weakest, admit

    return [item for _, item in reservoir]
```

```rust
use std::cmp::Ordering;
use std::collections::BinaryHeap;

#[derive(PartialEq, Clone, Copy)]
struct Event<'a> { name: &'a str, weight: i32, key: f64 }

impl<'a> Eq for Event<'a> {}

impl<'a> PartialOrd for Event<'a> { fn partial_cmp(&self, o: &Self) -> Option<Ordering> { Some(self.cmp(o)) } }

impl<'a> Ord for Event<'a> {
    // Rust's BinaryHeap is max; invert so the SMALLEST key sits on top (min-heap behavior)
    fn cmp(&self, o: &Self) -> Ordering { o.key.partial_cmp(&self.key).unwrap() }
}

impl Solution {
    /// @param stream events as (item, weight)
    /// @param k      reservoir size
    /// @return       the sampled items
    pub fn weighted_reservoir_sample<'a>(stream: Vec<(&'a str, i32)>, k: usize) -> Vec<&'a str> {
        let mut reservoir: BinaryHeap<Event<'a>> = BinaryHeap::new();

        for (name, weight) in stream {
            let key = rand::random::<f64>().powf(1.0 / weight as f64);   // K_i = U^(1/w)

            if reservoir.len() < k {
                reservoir.push(Event { name, weight, key });             // fill
            } else if key > reservoir.peek().unwrap().key {              // beat the threshold
                reservoir.pop();                                         // evict the weakest
                reservoir.push(Event { name, weight, key });             // admit
            }
        }
        reservoir.iter().map(|e| e.name).collect()
    }
}
```

## Dry run

**Input:** the notes' stream — `Scroll_A(1), Scroll_B(1), CLICK_C(100), Scroll_D(1), CLICK_E(100), Scroll_F(1), Purchase_G(500)`, `k = 2`. (Keys are random; the trace shows the *shape* of one representative run.)

```
reservoir (min-heap of the 2 largest keys):

Scroll_A (w=1): key ~ U      -> reservoir = [Scroll_A]          (fill)
Scroll_B (w=1): key ~ U      -> reservoir = [Scroll_A, Scroll_B]
CLICK_C  (w=100): key ~ U^0.01 ≈ 0.9999 -> beats min (0.0034)  -> evict Scroll_B, admit CLICK_C
Scroll_D (w=1): key ~ 0.8872 -> beats min? yes vs 0.0034 -> evict Scroll_A... (random)
CLICK_E  (w=100): key ≈ 1.0 -> admitted
Purchase_G (w=500): key = U^0.002 ≈ 1.0 -> beats min -> admitted (evicts the weakest click)

Result: the high-weight events dominate; Scrolls almost never survive the threshold ✓
```

The weight-inflation in action: a weight-100 key is `U^0.01`, which is ≥ 0.99 with probability 0.63 — it *almost always* clears any threshold a weight-1 event set. That's the "probability proportional to weight" guarantee made structural: the keys rank the events, and the heap keeps the top-k keys.

## Complexity

**Time.** O(log k) heap ops per event:

$$
T(N) = O(N \log k)
$$

**Space.** The reservoir only:

$$
S = O(k)
$$

## Variants & follow-ups

- **ReservoirSampling (unweighted)** (`probability/ReservoirSampling.kt`) — Algorithm R: replace with probability `1/count`; the `w = 1` special case.
- **Random Pick With Weight** ([1.14](../ch01-binary-search/random-pick-with-weight.md)) — offline weighted choice via prefix sums + binary search; the "you can see the whole array" version.
- **Interview follow-up:** "Why does `K = U^(1/w)` give weight-proportional probability?" `P(K_i > K_j)` for two events works out to `w_i / (w_i + w_j)` — the weight ratio. The min-heap keeps exactly the events with the k largest keys, and by that probability law, each event's chance of being in the final k is its weight share of the stream.
