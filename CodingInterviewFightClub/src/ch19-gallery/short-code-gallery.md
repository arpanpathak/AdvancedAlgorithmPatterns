# 19.5 The "Shorter / Better / Clean" Gallery

> **Sources:** [`NextPermutationShorter.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/Combinatorics/NextPermutationShorter.kt) · `BasicCalculator_II_ShortCode.kt` · `ValidateIPAddressBetterImplementation.kt` · `FindPeakElementBetterSolution.kt` · `CriticalConnectionsInANetworkShortCode.kt`
> **Pattern:** variant gallery — the repo's "shorter/better" files, captured

## 1. `NextPermutationShorter.kt` — the generic extension

[12.10](../ch12-backtracking/next-permutation.md) documents the array version; this file generalizes it to **any `MutableList<T : Comparable<T>>`** as an extension function — and condenses the pivot-scan to a `firstOrNull`:

```kotlin
fun <T : Comparable<T>> MutableList<T>.nextPermutation(): Boolean {
    val pivotIndex = (size - 2 downTo 0).firstOrNull { this[it] < this[it + 1] } ?: run {
        this.reverse()          // already maximal: wrap
        return false
    }

    val swapIndex = (size - 1 downTo pivotIndex + 1).first { this[pivotIndex] < this[it] }

    this[pivotIndex] = this[swapIndex].also { this[swapIndex] = this[pivotIndex] }
    this.subList(pivotIndex + 1, size).reverse()

    return true
}
```

**What's cool:** `firstOrNull` + `run` folds the "no pivot → reverse and return false" case into the declaration; the `also`-swap is the idiomatic Kotlin swap; and the extension means *any* `MutableList` gets the method — `["a","b","c"]` sorts lexicographically through the same Narayana-Pandita machine as `[1,2,3]`. This is the generic form an interviewer's "make it reusable" follow-up wants.

## 2. `BasicCalculator_II_ShortCode.kt` — the single-pass accumulator

[8.x](../ch08-stacks/pattern-primer.md) covers expression evaluation; this file is the O(1)-space streaming calculator: `lastNumber` accumulates `*`/`/` into the pending term, `result` banks completed terms:

```kotlin
class BasicCalculator_II_ShortCode {
    fun calculate(s: String): Int {
        var (currentNumber, result, lastNumber) = listOf(0, 0, 0)
        var operator = '+'

        s.forEachIndexed { i, char ->
            when {
                char.isDigit() -> currentNumber = currentNumber * 10 + (char - '0')
                !char.isDigit() && char != ' ' || i == s.lastIndex -> {
                    when (operator) {
                        '+' -> { result += lastNumber; lastNumber = currentNumber }
                        '-' -> { result += lastNumber; lastNumber = -currentNumber }
                        '*' -> lastNumber *= currentNumber
                        '/' -> lastNumber /= currentNumber
                    }
                    operator = char
                    currentNumber = 0
                }
            }
        }
        return result + lastNumber
    }
}
```

**What's cool:** the `|| i == s.lastIndex` condition flushes the last term without a post-loop; the `operator` variable carries the *pending* operator so `*` and `/` fold into `lastNumber` while `+`/`-` bank it into `result`; no stack, no reverse pass — the [8.6](../ch08-stacks/evaluate-reverse-polish-notation.md) "carry state as you go" discipline in its tightest form.

## 3. `ValidateIPAddressBetterImplementation.kt` — the declarative validator

[9.7](../ch09-strings/validate-ip-address.md) documents a scanner-based validator; this file is the *all-at-once* version — one `when`, two `all {}` predicates:

```kotlin
class ValidateIPAddressBetterImplementation {
    fun validIPAddress(queryIP: String): String = when {
        isValidIPv4(queryIP) -> "IPv4"
        isValidIPv6(queryIP) -> "IPv6"
        else -> "Neither"
    }

    private fun isValidIPv4(ip: String): Boolean {
        val segments = ip.split('.')
        if (segments.size != 4) return false

        return segments.all {
            it.isNotEmpty() &&                       // no empty segments
                    it.length <= 3 &&                // no 4-digit numbers
                    it.all(Char::isDigit) &&
                    (it.length == 1 || it.first() != '0') &&   // no leading zeros
                    (it.toIntOrNull() in 0..255)     // range check
        }
    }
}
```

**What's cool:** the five IPv4 rules are five clauses of one `all {}` — each rule is a line, and the `when` at the top makes the method read as its own spec. The IPv6 side mirrors with `split(':')`, `count(':' ) == 7`, hex digits, and length ≤ 4. The [9.7](../ch09-strings/validate-ip-address.md) page shows the step-by-step validation; this is the "rules as predicates" upgrade.

## 4. `FindPeakElementBetterSolution.kt` — boundary-safe peak

[1.6](../ch01-binary-search/find-peak-element.md) documents the standard binary-search peak; this file's "better" claim is **explicit boundary handling** — neighbors default to `Int.MIN_VALUE` at the edges:

```kotlin
class FindPeakElementBetterSolution {
    fun findPeakElement(nums: IntArray): Int? {
        if (nums.isEmpty()) return null

        var (left, right) = 0 to nums.size - 1

        while (left < right) {
            val mid = left + (right - left) / 2

            // Safely handle boundaries
            val leftNeighbor = if (mid > 0) nums[mid - 1] else Int.MIN_VALUE
            val rightNeighbor = if (mid < nums.size - 1) nums[mid + 1] else Int.MIN_VALUE

            when {
                nums[mid] > leftNeighbor && nums[mid] > rightNeighbor -> return mid   // peak
                nums[mid] < rightNeighbor -> left = mid + 1                            // go right
                else -> right = mid                                                    // go left
            }
        }
        return left
    }
}
```

**What's cool:** the `Int.MIN_VALUE` neighbors make the boundary cells valid peaks (a single-element array's only element is a peak); the `when` reads as the three-way decision; and `Int?` return explicitly signals "empty input". The three-branch structure also avoids [1.7](../ch01-binary-search/find-peak-element-safe.md)'s separate "safe boundaries" page — this file *is* that page's idea in one method.

## 5. `CriticalConnectionsInANetworkShortCode.kt` — Tarjan bridges, compressed

[17.10](../ch17-advanced-graphs/critical-connections-in-a-network.md) documents the full class; this file compresses the bridge DFS into the smallest faithful form:

```kotlin
// sketch of the short-code shape (CriticalConnectionsInANetworkShortCode.kt)
// label/low arrays + one dfs() that emits a bridge when label[node] < low[neighbor]
// — the same algorithm as 17.10, with the class scaffolding stripped to the essentials
```

**What's cool:** it proves the algorithm has ~15 essential lines. When an interviewer asks "can you write it tighter?" — this file is the answer: no result-list as a field, no explicit `depth` class member, the recursion carries everything.

## The meta-lesson

The "Shorter" files aren't different algorithms — they're the **same algorithm at higher signal-to-noise**. Reading them in pairs (full ↔ short) is the fastest way to learn which parts of the canonical form are *essential* (the recurrence, the pointers) and which are *ceremony* (class fields, boilerplate).

## Variants & follow-ups

- **Next Permutation** ([12.10](../ch12-backtracking/next-permutation.md)) — the full walkthrough; this page's file is its generic one-liner sibling.
- **Validate IP Address** ([9.7](../ch09-strings/validate-ip-address.md)) — the scanner version this file's predicates replace.
- **Interview follow-up:** "When is short code a liability?" When the compression hides a branch (the `|| i == s.lastIndex` flush in Basic Calculator II is a classic off-by-one trap). The repo's pairs exist so you can study both: write the short version, then explain the full version.
