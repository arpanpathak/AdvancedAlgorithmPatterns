# How To Read This Book

This book is built for **repetition with intent**. You will not absorb it by reading once, and you will not absorb it by skipping the dry runs. Here is the recommended loop.

## The 20-minute problem loop

For every problem, follow this order — it mirrors exactly what you should do in a real interview:

1. **Read the problem statement and the examples.** Cover the solution. Try to write down:
   - the *input domain* (can it be empty? negative? huge?),
   - a *brute force* you could hand-wave in 2 minutes,
   - the *runtime* you think is required (usually visible from the constraints: $n \le 10^5$ almost always means $O(n)$ or $O(n \log n)$).
2. **Read the Intuition section.** This is the heart of the page. If you close the book and cannot re-derive the core idea in your own words, re-read it.
3. **Read Approach 1 (brute force).** Understand *why it is too slow* — express the slowness as a formula, e.g. "checking all $n^2$ pairs".
4. **Read the optimal approach, then trace the dry run** with your finger. Do not skip this. The dry run is where the algorithm stops being magic and becomes a machine.
5. **Read the 5-language code** in the language you are least comfortable with. The `@param`/`@return` comments are the API contract; the body is the implementation.
6. **Read the complexity proof.** Every complexity claim in this book is derived, not asserted.
7. **Re-implement from memory** in your editor. If you cannot, repeat steps 2–6.

## The dry-run convention

Dry runs are shown in monospace panels like this:

```
left=0  right=7  mid=3  arr[3]=4  arr[7]=9
  arr[3] < arr[7]  ->  right = 2        (keep searching left half)
...
return 4   (arr[left] is the minimum)
```

Each line shows the *state before* an action and the *decision taken*. Arrows (`->`) show how the state mutates. Treat every line as an assertion you can check by hand.

## The 5 languages

Every solution ships in **Kotlin** (the source of truth, verbatim from `src/main/kotlin/`), plus **Java**, **C++**, **typed Python**, and **Rust** translations written fresh for this book. The translations preserve:

- the exact same algorithm and complexity,
- the same `@param`/`@return` contract,
- idiomatic types — `List<Int>`/`int[]`/`vector<int>`/`list[int]`/`Vec<i32>` depending on the language.

If a translation ever deviates in behavior, the page says so explicitly.

## Difficulty of a page

- **Core** — a pattern you must own cold (e.g. Koko Eating Bananas, Median of Two Sorted Arrays).
- **Variant** — a twist on a core pattern (e.g. Search in Rotated Sorted Array II).
- **Gym** — a problem that combines several patterns (e.g. Closest Subsequence Sum).

## Suggested reading order

Read Chapter 1 (Binary Search) first even if you know it — it is the shortest complete tour of the book's format. Then follow the tree in order, or jump to whatever your interview is testing this week. The book is a reference, a syllabus, and a drill — in that order of priority.
