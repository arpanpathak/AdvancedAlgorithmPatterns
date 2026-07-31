# 19.13 The Map-Key Gallery — Creative Hash Keys

> **Sources:** [`GroupAnagrams.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/GroupAnagrams.kt) · `heap/TopKFrequentElements.kt` · `quicksort/TopKFrequentElements.kt` · `RankTransformOfAnArray.kt` · `UniqueNumberOfOccurences.kt`
> **Pattern:** variant gallery — what you put in the key decides the algorithm

## The gallery thesis

A hash map is only as good as its **key**. These files show five different key designs for counting/grouping problems — each a slightly different answer to "what identifies an equivalence class?"

## 1. `GroupAnagrams.kt` — the `List<Int>` key

[9.2](../ch09-strings/group-anagrams.md) uses a frequency-String key (`"1#2#0#..."`). This file uses the **frequency list itself** as the key — `count.toList()` — relying on `List<Int>`'s structural equality:

```kotlin
class GroupAnagrams {
    fun groupAnagrams(strs: Array<String>): List<List<String>> {
        val anagramMap = mutableMapOf<List<Int>, MutableList<String>>()  // List<Int> as the key!
        for (word in strs) {
            val count = IntArray(26)
            word.forEach { count[it - 'a']++ }
            anagramMap.getOrPut(count.toList()) { mutableListOf() }.add(word)
        }
        return anagramMap.values.toList()
    }
}
```

**What's cool:** no string serialization — the `IntArray` is converted to a `List<Int>` whose `equals`/`hashCode` are structural (contents, not identity). `getOrPut(count.toList()) { ... }` folds create-and-add into one call. The [9.2](../ch09-strings/group-anagrams.md) String-key is debuggable (printable); this key is zero-encoding.

## 2. `heap/TopKFrequentElements.kt` — the `getOrPut` count one-liner

The [7.1](../ch07-heaps/top-k-frequent-elements.md) heap algorithm, with the frequency build compressed:

```kotlin
class TopKFrequentElements {
    fun topKFrequent(nums: IntArray, k: Int): IntArray {
        val freqMap = mutableMapOf<Int, Int>()
        nums.forEach { freqMap[it] = freqMap.getOrPut(it) { 0 } + 1 }

        // Min-heap keeping the k most frequent
        val minHeap = PriorityQueue<Int> { a, b -> freqMap[a]!! - freqMap[b]!! }

        for (num in freqMap.keys) {
            minHeap.offer(num)
            if (minHeap.size > k) minHeap.poll()   // evict the least frequent
        }
        return minHeap.toIntArray()
    }
}
```

**What's cool:** `freqMap[it] = freqMap.getOrPut(it) { 0 } + 1` — the whole "increment or initialize" in one expression (same idiom as `UniqueNumberOfOccurences.kt`'s `map[num] = map.getOrPut(num) { 1 } + 1`). The min-heap with a **frequency comparator** is the [7.1](../ch07-heaps/top-k-frequent-elements.md) keep-top-k shape.

## 3. `quicksort/TopKFrequentElements.kt` — the quickselect twin

The same problem via **randomized partition on the unique keys** — the [14.7](../ch14-sorting/top-k-frequent-elements-quickselect.md) engine:

```kotlin
class TopKFrequentElements {
    private val map = HashMap<Int, Int>()

    fun topKFrequent(nums: IntArray, k: Int): IntArray {
        nums.forEach { map[it] = map.getOrPut(it) { 0 } + 1 }

        val uniqueNums = map.keys.toIntArray()
        var start = 0
        var end = uniqueNums.size - 1

        while (start < end) {
            val partitionIndex = partition(uniqueNums, start, end)
            when {
                partitionIndex < k - 1 -> start = partitionIndex + 1
                partitionIndex > k - 1 -> end = partitionIndex - 1
                else -> break
            }
        }
        return uniqueNums.copyOfRange(0, k)
    }

    // Randomized quick partition on FREQUENCY, not value...
    private fun partition(nums: IntArray, start: Int, end: Int): Int {
        val randomIndex = Random.nextInt(start, end + 1)
        // ...swap, partition by map[nums[i]] vs map[pivot]...
    }
}
```

**What's cool:** the map is built once; then the **frequencies** are the partition key (not the values) — `map[nums[i]]` in the partition's comparison. Heap gives O(n log k); quickselect gives O(n) average ([14.7](../ch14-sorting/top-k-frequent-elements-quickselect.md) compares them).

## 4. `RankTransformOfAnArray.kt` — the `getOrPut` rank trick

Ranking = "sort, then assign 1-based rank on first occurrence" — and `getOrPut` makes the first-occurrence check a side effect:

```kotlin
class RankTransformOfAnArray {
    fun arrayRankTransform(arr: IntArray): IntArray {
        val sortedArr = arr.sorted()

        val rankMap = mutableMapOf<Int, Int>()
        var rank = 1
        for (num in sortedArr) {
            rankMap.getOrPut(num) { rank++ }     // only the FIRST occurrence consumes a rank
        }

        return arr.map { rankMap[it]!! }.toIntArray()
    }
}
```

**What's cool:** `getOrPut(num) { rank++ }` — the lambda runs *only when the key is absent*, so duplicates share a rank automatically. The `map { rankMap[it]!! }` projection is the final transform. The imperative "if (!containsKey) put(num, rank++)" is one call.

## 5. `UniqueNumberOfOccurences.kt` — the set-size check

"Are all frequencies distinct?" — build the frequency map, then compare set-size to map-size:

```kotlin
class UniqueNumberOfOccurences {
    fun uniqueOccurrences(arr: IntArray): Boolean {
        val map = mutableMapOf<Int, Int>()
        for (num in arr) {
            map[num] = map.getOrPut(num) { 1 } + 1
        }
        return map.values.toSet().size == map.size
    }
}
```

**What's cool:** `map.values.toSet().size == map.size` — if any frequency repeats, the set shrinks. The whole "distinctness" question is one equality test after the `getOrPut` count build.

## The key-design taxonomy

| File | Key | Equivalence class | Main page |
|---|---|---|---|
| GroupAnagrams | `List<Int>` (frequency) | anagram | [9.2](../ch09-strings/group-anagrams.md) |
| TopK heap | — (frequency comparator) | ordering by count | [7.1](../ch07-heaps/top-k-frequent-elements.md) |
| TopK quickselect | frequency partition key | ordering by count | [14.7](../ch14-sorting/top-k-frequent-elements-quickselect.md) |
| RankTransform | value → rank (first-occurrence) | sorted order | — |
| UniqueOccurrences | value → count | distinctness of counts | — |

## Variants & follow-ups

- **Group Anagrams** ([9.2](../ch09-strings/group-anagrams.md)) — the String-key version this page's `List<Int>` key replaces.
- **Top K Frequent Elements** ([7.1](../ch07-heaps/top-k-frequent-elements.md), [14.7](../ch14-sorting/top-k-frequent-elements-quickselect.md)) — the two engines' full pages.
- **Interview follow-up:** "When is a `List` key safe?" When the element type has structural equality (Int/String — yes; arrays — no, identity!). `IntArray.toList()` is the safe conversion; using the raw `IntArray` as a key would compare by reference and break the map entirely — the silent killer this file's `.toList()` avoids.
