# 7.10 Longest Happy String

> **Source:** [`src/main/kotlin/heap/LongestHappyString.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/heap/LongestHappyString.kt)
> **Pattern:** max-heap with a 2-repeat cap · **Core page**

## The Problem

Given `a, b, c` counts of `'a','b','c'`, build the **longest possible string** with no three identical characters in a row.

- Constraints: $0 \le a, b, c \le 100$.

## Examples

```
Input:  a = 1, b = 1, c = 7   -> Output: "ccaccbcc"   (length 8, no "ccc")
Input:  a = 7, b = 1, c = 0   -> Output: "aabaa"      (length 5, the two b's separate)
```

## Intuition — always take the most frequent letter, but never three in a row

The [11.6](../ch11-greedy/task-scheduler.md)/[11.10](../ch11-greedy/reorganize-string.md) max-heap machine, with a twist: **a letter may appear twice consecutively** (only three is banned). So the greedy is:

```
heap of (letter, count), max by count
lastChar, lastCount (consecutive run of the last char)
while heap not empty:
    (char, count) = poll
    if lastChar == char && lastCount == 2:
        if heap empty: break                    # can't place it: done
        (altChar, altCount) = poll              # take the second-most frequent instead
        append altChar; push altChar back with count-1
        push (char, count) back                 # the blocked char returns
        lastChar = altChar; lastCount = 1
    else:
        append char; push back with count-1
        if lastChar == char: lastCount++ else { lastChar = char; lastCount = 1 }
```

**Why the "second-most frequent" fallback?** When the top letter is already at its 2-run cap, placing it would create "aaa". The next-best letter breaks the run — and the blocked letter goes back into the heap for the next round. This is the [11.10](../ch11-greedy/reorganize-string.md) cooldown logic with `cooldown = 1`-consecutive instead of `k`-apart.

**Why does the max-heap keep it optimal?** The exchange argument ([11.0](pattern-primer.md)): the most frequent remaining letter should be placed whenever legal — deferring it only makes the tail harder. The "place the second-best" detour is the single exception, forced by the 2-cap.

## Approach 1 — Greedy frequency math (O(n) closed form)

The counts-based construction (`maxFreq` vs the rest) — correct but fiddly; the heap below is the general engine.

## Approach 2 — Max-heap with the 2-cap fallback (the repo's version, optimal)

```kotlin
import java.util.*

class LongestHappyString {
    /**
     * @param a count of 'a'
     * @param b count of 'b'
     * @param c count of 'c'
     * @return  the longest happy string
     */
    fun longestDiverseString(a: Int, b: Int, c: Int): String {
        val pq = PriorityQueue<Pair<Char, Int>> { p1, p2 -> p2.second - p1.second }

        if (a > 0) pq.offer('a' to a)
        if (b > 0) pq.offer('b' to b)
        if (c > 0) pq.offer('c' to c)

        return buildString {
            var lastChar = ' '
            var lastCount = 0

            while (pq.isNotEmpty()) {
                val (char, count) = pq.poll()

                // Two consecutive already: must use the second-highest count character
                if (lastChar == char && lastCount == 2) {
                    if (pq.isEmpty()) break

                    val (altChar, altCount) = pq.poll()
                    append(altChar)
                    lastChar = altChar
                    lastCount = 1

                    if (altCount > 1) pq.offer(altChar to altCount - 1)
                    pq.offer(char to count)          // the blocked char returns
                } else {
                    append(char)
                    pq.offer(char to count - 1)

                    if (lastChar == char) lastCount++
                    else { lastChar = char; lastCount = 1 }
                }
            }
        }.toString()
    }
}
```

```java
import java.util.*;

public class LongestHappyString {
    /**
     * @param a count of 'a'
     * @param b count of 'b'
     * @param c count of 'c'
     * @return  the longest happy string
     */
    public String longestDiverseString(int a, int b, int c) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> y[1] - x[1]);   // char, count
        if (a > 0) pq.offer(new int[]{'a', a});
        if (b > 0) pq.offer(new int[]{'b', b});
        if (c > 0) pq.offer(new int[]{'c', c});

        StringBuilder sb = new StringBuilder();
        char last = ' ';
        int run = 0;

        while (!pq.isEmpty()) {
            int[] top = pq.poll();

            if (last == (char) top[0] && run == 2) {
                if (pq.isEmpty()) break;
                int[] alt = pq.poll();                    // second-most frequent
                sb.append((char) alt[0]);
                last = (char) alt[0];
                run = 1;
                if (alt[1] > 1) pq.offer(new int[]{alt[0], alt[1] - 1});
                pq.offer(top);                            // the blocked char returns
            } else {
                sb.append((char) top[0]);
                pq.offer(new int[]{top[0], top[1] - 1});
                if (last == (char) top[0]) run++;
                else { last = (char) top[0]; run = 1; }
            }
        }
        return sb.toString();
    }
}
```

```cpp
#include <queue>
#include <string>

class LongestHappyString {
public:
    /**
     * @param a count of 'a'
     * @param b count of 'b'
     * @param c count of 'c'
     * @return  the longest happy string
     */
    std::string longestDiverseString(int a, int b, int c) {
        auto cmp = [](auto& x, auto& y) { return x.second < y.second; };
        std::priority_queue<std::pair<char, int>,
            std::vector<std::pair<char, int>>, decltype(cmp)> pq(cmp);

        if (a) pq.push({'a', a});
        if (b) pq.push({'b', b});
        if (c) pq.push({'c', c});

        std::string result;
        char last = ' ';
        int run = 0;

        while (!pq.empty()) {
            auto [ch, cnt] = pq.top(); pq.pop();

            if (last == ch && run == 2) {
                if (pq.empty()) break;
                auto [alt, altCnt] = pq.top(); pq.pop();   // second-most frequent
                result += alt;
                last = alt;
                run = 1;
                if (altCnt > 1) pq.push({alt, altCnt - 1});
                pq.push({ch, cnt});                        // the blocked char returns
            } else {
                result += ch;
                pq.push({ch, cnt - 1});
                if (last == ch) run++;
                else { last = ch; run = 1; }
            }
        }
        return result;
    }
};
```

```python
import heapq

def longest_diverse_string(a: int, b: int, c: int) -> str:
    """
    @param a: count of 'a'
    @param b: count of 'b'
    @param c: count of 'c'
    @return:  the longest happy string
    """
    heap = []
    for ch, cnt in (("a", a), ("b", b), ("c", c)):
        if cnt:
            heapq.heappush(heap, (-cnt, ch))

    result = []
    last, run = "", 0

    while heap:
        cnt, ch = heapq.heappop(heap)
        if last == ch and run == 2:
            if not heap:
                break
            alt_cnt, alt = heapq.heappop(heap)   # second-most frequent
            result.append(alt)
            last, run = alt, 1
            if alt_cnt + 1 < 0:
                heapq.heappush(heap, (alt_cnt + 1, alt))
            heapq.heappush(heap, (cnt, ch))      # the blocked char returns
        else:
            result.append(ch)
            if cnt + 1 < 0:
                heapq.heappush(heap, (cnt + 1, ch))
            if last == ch:
                run += 1
            else:
                last, run = ch, 1

    return "".join(result)
```

```rust
use std::cmp::Reverse;
use std::collections::BinaryHeap;

impl Solution {
    /// @param a count of 'a'
    /// @param b count of 'b'
    /// @param c count of 'c'
    /// @return  the longest happy string
    pub fn longest_diverse_string(a: i32, b: i32, c: i32) -> String {
        let mut heap: BinaryHeap<(i32, char)> = BinaryHeap::new();
        for (cnt, ch) in [(a, 'a'), (b, 'b'), (c, 'c')] {
            if cnt > 0 { heap.push((cnt, ch)); }
        }

        let mut result = String::new();
        let (mut last, mut run) = (' ', 0);

        while let Some((cnt, ch)) = heap.pop() {
            if last == ch && run == 2 {
                if heap.is_empty() { break; }
                let (alt_cnt, alt) = heap.pop().unwrap();   // second-most frequent
                result.push(alt);
                last = alt;
                run = 1;
                if alt_cnt > 1 { heap.push((alt_cnt - 1, alt)); }
                heap.push((cnt, ch));                       // the blocked char returns
            } else {
                result.push(ch);
                if cnt > 1 { heap.push((cnt - 1, ch)); }
                if last == ch { run += 1; }
                else { last = ch; run = 1; }
            }
        }
        result
    }
}
```

## Dry run

**Input:** `a = 1, b = 1, c = 7`.

```
heap: c(7), a(1), b(1).  last=' ', run=0
c(7): not capped -> append 'c'.  push c(6).  last='c', run=1
c(6): same char, run 1 < 2 -> append 'c'.  push c(5).  last='c', run=2
c(5): same char, run == 2 -> CAP.  take alt a(1): append 'a'.  push c(5) back.  last='a', run=1
c(5): not capped -> 'c'.  push c(4).  run on 'c' resets to 1
c(4): 'c'.  push c(3).  run=2
c(3): CAP -> alt b(1): append 'b'.  push c(3) back.
c(3): 'c'.  c(2): 'c'.  c(1): CAP -> heap empty? a,b used... heap has c(1): alt needed, heap empty -> break.

result: "ccaccbcc" ✓   (8 chars, no "ccc")
```

The cap dance in action: after "cc", the third c is *blocked* and the heap's second-best (a, then b) breaks the run. The blocked c returns immediately and resumes — producing the maximal "ccac cbcc" shape. The `run` counter only counts *consecutive* same-char placements; any different char resets it.

## Complexity

**Time.** Each placement O(log 3):

$$
T(n) = O(n \log 3) = O(n)
$$

**Space.** The heap + result:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Reorganize String** ([11.10](../ch11-greedy/reorganize-string.md)) — the same heap + cap machine with `cooldown = 1` (no repeats at all).
- **Task Scheduler** ([11.6](../ch11-greedy/task-scheduler.md)) — the cooldown-window family ancestor.
- **Interview follow-up:** "Why is the 'second-most frequent' fallback optimal?" Placing the blocked top letter would create the forbidden triple. Any legal placement must use a different letter; the *most frequent* different letter is the best choice by the exchange argument (it leaves the counts most balanced). If none exists, the string is maximal.
