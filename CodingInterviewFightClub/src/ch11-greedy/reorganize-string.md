# 11.10 Reorganize String

> **Source:** [`src/main/kotlin/google/SongShuffle.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/google/SongShuffle.kt) (the playlist-shuffle framing) · also the classic *Reorganize String*
> **Pattern:** max-heap + cooldown window · **Core page**

## The Problem

Given a string `s`, rearrange it so **no two adjacent characters are equal** (return `""` if impossible). The playlist framing: shuffle songs so the same artist never plays twice in a row.

- Constraints: $1 \le n \le 500$; lowercase letters.

## Examples

```
Input:  s = "aab"   -> Output: "aba"
Input:  s = "aaab"  -> Output: ""   (three a's can't be separated by one b)
```

## Intuition — always schedule the *most frequent remaining* character, with a one-step cooldown

This is the [11.6](task-scheduler.md) simulation engine, tuned for adjacency instead of `n`-apart:

1. **Max-heap of (character, remaining count)** — the next character is always the most frequent remaining one (scheduling it last minimizes the "cluster at the end" failure the notes warn about).
2. **A cooldown window** of size `k` (here 1 — just "not the immediately previous character"): after scheduling a character, it can't be scheduled again until it leaves the window.

```
heap: all (char, count), max by count
result = []; waitQueue = deque()
while heap not empty:
    current = heap.poll()                  # most frequent remaining
    result += current.char; current.count--
    waitQueue.offerLast(current)
    if waitQueue.size > k:                 # cooldown expired
        released = waitQueue.pollFirst()
        if released.count > 0: heap.offer(released)
return result if its length == s.length else ""
```

**Why the max-heap?** Scheduling the most frequent character first is the [11.0](pattern-primer.md) "defend the local choice" move — the exchange argument: if a valid arrangement exists, one with the most-frequent character first also exists (swap it into the front). The heap is what makes the greedy pick O(log n) per step.

**Why the cooldown queue?** The *previous* character must not be re-picked immediately — the queue defers a character for one round, exactly like [11.6](task-scheduler.md)'s cooldown of `n`. With `k = 1`, the rule "no two adjacent equal" is enforced structurally: the released character can't come back until the next pick.

**The impossibility check:** if a character's count exceeds `(n + 1) / 2`, no rearrangement exists (pigeonhole: you need a separator between every pair). The heap version detects it by running out — the result is shorter than `s`.

## Approach 1 — Sort-and-weave / backtracking

Interleave sorted halves: fails on many distributions; backtracking is exponential.

## Approach 2 — Max-heap + cooldown window (the repo's version, optimal)

```kotlin
import java.util.*

data class Song(val artist: String, val title: String)

/**
 * @param playlist list of songs
 * @param k        minimum gap between same-artist songs
 * @return         a shuffled playlist with the gap enforced, or a shorter list if impossible
 */
fun shufflePlaylist(playlist: List<Song>, k: Int = 1): List<Song> {
    val result = mutableListOf<Song>()

    // 1. Group songs by artist, in FIFO order per artist
    val artistMap = playlist.groupBy { it.artist }
        .mapValues { (_, songs) -> ArrayDeque(songs) }


    // Pigeonhole: the most frequent artist needs a separator between every pair
    val maxCount = artistMap.values.maxOf { it.size }
    if (maxCount > (playlist.size + 1) / 2) return emptyList()   // impossible
    // 2. Max-heap of artists, keyed by remaining song count
    val maxHeap = PriorityQueue<String>(compareByDescending { artistMap[it]?.size ?: 0 })
    maxHeap.addAll(artistMap.keys)

    // 3. Cooldown window: enforces the gap of k between same-artist songs
    val waitQueue: Deque<String> = ArrayDeque()

    while (maxHeap.isNotEmpty()) {
        val currentArtist = maxHeap.poll()                 // most songs remaining: schedule now
        artistMap[currentArtist]?.pollFirst()?.let { result.add(it) }

        waitQueue.offerLast(currentArtist)
        if (waitQueue.size > k) {                          // cooldown expired for the front
            val releasedArtist = waitQueue.pollFirst()
            if (artistMap[releasedArtist]?.isNotEmpty() == true) {
                maxHeap.offer(releasedArtist)              // back in contention
            }
        }
    }
    return result
}
```

```java
import java.util.*;

public class ReorganizeString {
    /**
     * @param s input string
     * @return  rearrangement with no equal adjacent characters, or ""
     */
        int maxCount = 0;
        for (int c : count) maxCount = Math.max(maxCount, c);
        if (maxCount > (s.length() + 1) / 2) return "";    // pigeonhole: impossible
    public String reorganizeString(String s) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) count[c - 'a']++;

        PriorityQueue<Character> heap = new PriorityQueue<>(
            (a, b) -> count[b - 'a'] != count[a - 'a']
                    ? count[b - 'a'] - count[a - 'a'] : a - b);   // max by count
        for (char c = 'a'; c <= 'z'; c++) if (count[c - 'a'] > 0) heap.offer(c);

        StringBuilder result = new StringBuilder();
        ArrayDeque<Character> wait = new ArrayDeque<>();

        while (!heap.isEmpty()) {
            char c = heap.poll();                        // most frequent remaining
            result.append(c);
            count[c - 'a']--;

            wait.offerLast(c);
            if (wait.size() > 1) {                       // cooldown of 1 expired
                char released = wait.pollFirst();
                if (count[released - 'a'] > 0) heap.offer(released);
            }
        }
        return result.length() == s.length() ? result.toString() : "";   // impossible check
    }
}
```

```cpp
#include <algorithm>
#include <queue>
#include <string>

class ReorganizeString {
public:
    /**
     * @param s input string
     * @return  rearrangement with no equal adjacent characters, or ""
     */
    std::string reorganizeString(std::string s) {
        std::vector<int> count(26, 0);
        int maxCount = *std::max_element(count.begin(), count.end());
        if (maxCount > (int)(s.size() + 1) / 2) return "";   // pigeonhole: impossible
        for (char c : s) count[c - 'a']++;

        auto cmp = [&](char a, char b) { return count[a - 'a'] < count[b - 'a']; };
        std::priority_queue<char, std::vector<char>, decltype(cmp)> heap(cmp);
        for (char c = 'a'; c <= 'z'; c++) if (count[c - 'a'] > 0) heap.push(c);

        std::string result;
        std::queue<char> wait;

        while (!heap.empty()) {
            char c = heap.top(); heap.pop();             // most frequent remaining
            result += c;
            count[c - 'a']--;

            wait.push(c);
            if (wait.size() > 1) {                       // cooldown of 1 expired
                char released = wait.front(); wait.pop();
                if (count[released - 'a'] > 0) heap.push(released);
            }
        }
        return result.size() == s.size() ? result : "";  // impossible check
    }
};
```

```python
import heapq

def reorganize_string(s: str) -> str:
    """
    @param s: input string
    @return:  rearrangement with no equal adjacent characters, or ""
    """
    count = {}
    for c in s:
        count[c] = count.get(c, 0) + 1

    if max(count.values()) > (len(s) + 1) // 2:
        return ""                             # pigeonhole: impossible

    heap = [(-cnt, c) for c, cnt in count.items()]   # max-heap by count (negated)
    heapq.heapify(heap)
    wait = []                                 # cooldown window

    result = []
    while heap:
        neg_cnt, c = heapq.heappop(heap)      # most frequent remaining
        result.append(c)

        wait.append((neg_cnt + 1, c))         # one copy consumed
        if len(wait) > 1:                     # cooldown of 1 expired
            released = wait.pop(0)
            if released[0] < 0:
                heapq.heappush(heap, released)

    return "".join(result) if len(result) == len(s) else ""   # safety net
```

```rust
use std::cmp::Reverse;
use std::collections::{BinaryHeap, HashMap, VecDeque};

impl Solution {
    /// @param s input string
    /// @return  rearrangement with no equal adjacent characters, or ""
    pub fn reorganize_string(s: String) -> String {
        let mut count: HashMap<char, i32> = HashMap::new();
        for c in s.chars() { *count.entry(c).or_insert(0) += 1; }

        if *count.values().max().unwrap() > (s.len() as i32 + 1) / 2 {
            return String::new();                            // pigeonhole: impossible
        }

        let mut heap: BinaryHeap<(i32, Reverse<char>)> = count.into_iter()
            .map(|(c, n)| (n, Reverse(c))).collect();        // max by count
        let mut wait: VecDeque<(i32, Reverse<char>)> = VecDeque::new();
        let mut result = String::new();

        while let Some((n, Reverse(c))) = heap.pop() {
            result.push(c);                                  // most frequent remaining
            wait.push_back((n - 1, Reverse(c)));
            if wait.len() > 1 {                              // cooldown of 1 expired
                let released = wait.pop_front().unwrap();
                if released.0 > 0 { heap.push(released); }
            }
        }
        if result.len() == s.len() { result } else { String::new() }   // safety net
    }
}
```

## Dry run

**Input:** `s = "aab"` (the playlist: artist A twice, artist B once).

```
counts: {a:2, b:1}.  pigeonhole: 2 > (3+1)/2 = 2? no -> proceed.
heap: [(2,a),(1,b)] (max by count).  wait = []

pop (2,a) -> result="a".  wait=[a(1)].  size 1, not > 1.
pop (1,b) -> result="ab". wait=[a(1),b(0)].  size 2 > 1 -> release 'a' (1 left) -> heap.
pop (1,a) -> result="aba". wait=[b(0),a(0)].  size 2 > 1 -> release 'b' (0 left) -> gone.
heap empty.  result "aba" == len 3 -> Output: "aba" ✓
```

Now `s = "aaab"`: counts `{a:3, b:1}`; the pigeonhole check `3 > (4+1)/2 = 2` fires **before any scheduling** → return `""` ✓. Without that check, the cooldown greedy would emit `"abaa"` — adjacent `a`s at the tail — and the length check alone would *not* catch it (4 == 4). The upfront count bound is the correctness, not a nicety.

## Complexity

**Time.** Each character scheduled once; heap ops O(log n):

$$
T(n) = O(n \log n)
$$

**Space.** The heap + cooldown queue:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Task Scheduler** ([11.6](task-scheduler.md)) — the same max-heap + cooldown machine with a *count* of idles instead of a rearrangement; this page's `k = 1` is the minimal cooldown.
- **Rearrange String K Distance Apart** — `k > 1`: the cooldown window widens; the same engine.
- **Interview follow-up:** "Why does the pigeonhole bound `(n+1)/2` decide impossibility?" The most frequent character needs a non-identical neighbor on both sides — with `c` copies, you need `c - 1` separators and `c - 1` must fit among the `n - c` other characters: `c - 1 <= n - c` ⟺ `c <= (n+1)/2`. Exceeding that is provably impossible, regardless of the greedy.
