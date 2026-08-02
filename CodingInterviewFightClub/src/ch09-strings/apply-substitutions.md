# 9.39 Apply Substitutions (Topological Resolution)

> **Source:** [`src/main/kotlin/graph/topological_sort/ApplySubstitutions.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/topological_sort/ApplySubstitutions.kt) · [`src/main/kotlin/string/ApplySubstitutions.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/ApplySubstitutions.kt)
> **Pattern:** dependency graph + Kahn's topological sort · **Core page**

## The Problem

You have `replacements = [[key, value], ...]` where each `value` may contain placeholders of the form `%X%` referring to *other* keys. The final `text` may also contain placeholders. Substitute every placeholder with its key's value, **recursively resolving dependencies** — a value may itself contain placeholders that must be expanded first.

- Constraints: replacement count and string lengths up to $10^5$; placeholders are single characters `%A%`, `%B%`, ...

## Examples

```
replacements = [["A", "abc%B%"], ["B", "xy"]], text = "%A%"
-> "abcxy"   (A expands to "abc%B%", then %B% expands to "xy")

replacements = [["X", "%Y%z"], ["Y", "w%X%"]], text = "%X%"   (cycle!)
-> dependency cycle: X needs Y, Y needs X — no valid full expansion.
```

## Intuition — this is a dependency graph; resolve it in topological order

The naive fix is to keep substituting until nothing changes — but a *cycle* of dependencies (A needs B, B needs A) makes that loop forever. The right frame:

> Each key is a **node**; a placeholder `%D%` inside key `K`'s value is an **edge** `D → K` ("D must be resolved before K"). Resolving every key correctly = processing keys in **topological order** — and a cycle shows up as keys that never reach in-degree 0 (see Kahn's algorithm, [6.3](../ch06-graphs/course-schedule-ii.md)).

Once every key's value is fully resolved (all its `%D%`s replaced with *already-resolved* expansions), the final `text` is a simple one-pass substitution — no recursion needed, because the values are already "flat".

**Why is topological order the correct resolution order?** A placeholder inside a value must be replaced by that key's *final* value — which is only known after its own placeholders are gone. So "resolve the dependencies first, then the dependents" is exactly Kahn's algorithm: repeatedly process keys with no unresolved dependencies left, then decrement the in-degrees of the keys that depend on them.

**Cycle detection falls out for free.** Keys trapped in a cycle never reach in-degree 0, so they're never processed — their values stay unresolved, and the final text keeps their placeholders verbatim (or you can flag it). One algorithm, two answers.

## Approach 1 — Iterate-until-stable substitution (too slow, loops forever on cycles)

Keep scanning `text` and replacing `%X%` with `map[X]` until a pass changes nothing. Works on acyclic inputs but is O(n · depth) and diverges on cycles — no detection.

## Approach 2 — Dependency graph + Kahn's topological sort (the repo's version, optimal)

```kotlin
import java.util.*

class ApplySubstitutions {
    fun applySubstitutions(replacements: List<List<String>>, text: String): String {
        val map = replacements.associate { it[0] to it[1] }.toMutableMap()
        val adjList = mutableMapOf<String, MutableList<String>>().withDefault { mutableListOf() }
        val inDegree = mutableMapOf<String, Int>().withDefault { 0 }

        // Build the dependency graph: for each placeholder %D% inside key's value,
        // record the edge D -> key ("D must resolve before key").
        for ((key, value) in replacements) {
            var i = 0
            while (i < value.length) {
                if (i + 2 < value.length && value[i] == '%' && value[i + 2] == '%') {
                    val depKey = value[i + 1].toString()
                    adjList[depKey]?.add(key)
                    inDegree[key] = inDegree.getOrDefault(key, 0) + 1
                    i += 3
                } else {
                    i++
                }
            }
        }

        // Kahn's algorithm: process keys with zero unresolved dependencies
        val queue: Queue<String> = LinkedList()
        for ((key, degree) in inDegree) {
            if (degree == 0) queue.add(key)
        }

        while (queue.isNotEmpty()) {
            val node = queue.poll()
            val resolvedValue = StringBuilder()

            var i = 0
            while (i < map[node]!!.length) {
                if (i + 2 < map[node]!!.length && map[node]!![i] == '%' && map[node]!![i + 2] == '%') {
                    val depKey = map[node]!![i + 1].toString()
                    resolvedValue.append(map[depKey] ?: "%$depKey%")   // dep already resolved
                    i += 3
                } else {
                    resolvedValue.append(map[node]!![i])
                    i++
                }
            }

            map[node] = resolvedValue.toString()

            // The node is now fully resolved — unblock its dependents
            for (dependent in adjList[node]!!) {
                inDegree[dependent] = inDegree[dependent]!! - 1
                if (inDegree[dependent] == 0) queue.add(dependent)
            }
        }

        // Final text: one pass, everything is flat now
        fun resolveFinalText(s: String): String {
            val sb = StringBuilder()
            var i = 0
            while (i < s.length) {
                if (i + 2 < s.length && s[i] == '%' && s[i + 2] == '%') {
                    val key = s[i + 1].toString()
                    sb.append(map[key] ?: "%$key%")
                    i += 3
                } else {
                    sb.append(s[i])
                    i++
                }
            }
            return sb.toString()
        }

        return resolveFinalText(text)
    }
}
```

```python
from collections import deque, defaultdict

def apply_substitutions(replacements, text):
    value = {k: v for k, v in replacements}
    adj = defaultdict(list)
    indeg = defaultdict(int)

    def deps(s):
        out = []
        i = 0
        while i + 2 < len(s):
            if s[i] == '%' and s[i + 2] == '%':
                out.append(s[i + 1]); i += 3
            else:
                i += 1
        return out

    for k, v in replacements:
        for d in deps(v):
            adj[d].append(k)
            indeg[k] += 1

    q = deque(k for k, v in replacements if indeg[k] == 0)
    while q:
        node = q.popleft()
        for d in deps(value[node]):
            value[node] = value[node].replace(f"%{d}%", value.get(d, f"%{d}%"))
        for dep in adj[node]:
            indeg[dep] -= 1
            if indeg[dep] == 0:
                q.append(dep)

    out, i = [], 0
    while i < len(text):
        if i + 2 < len(text) and text[i] == '%' and text[i + 2] == '%':
            out.append(value.get(text[i + 1], f"%{text[i + 1]}%")); i += 3
        else:
            out.append(text[i]); i += 1
    return "".join(out)
```

```java
import java.util.*;

class ApplySubstitutions {
    /**
     * @param replacements [key, value] pairs; values may contain %X% placeholders
     * @param text         the string to substitute into
     * @return             text with all placeholders fully resolved
     */
    public String applySubstitutions(List<List<String>> replacements, String text) {
        Map<String, String> value = new HashMap<>();
        Map<String, List<String>> adj = new HashMap<>();
        Map<String, Integer> indeg = new HashMap<>();
        for (List<String> r : replacements) {
            value.put(r.get(0), r.get(1));
            indeg.putIfAbsent(r.get(0), 0);
            adj.computeIfAbsent(r.get(0), k -> new ArrayList<>());
        }
        for (List<String> r : replacements) {
            String v = r.get(1);
            for (int i = 0; i + 2 < v.length(); i++) {
                if (v.charAt(i) == '%' && v.charAt(i + 2) == '%') {
                    String dep = String.valueOf(v.charAt(i + 1));
                    adj.computeIfAbsent(dep, k -> new ArrayList<>()).add(r.get(0));
                    indeg.put(r.get(0), indeg.get(r.get(0)) + 1);
                }
            }
        }

        Queue<String> q = new LinkedList<>();
        for (Map.Entry<String, Integer> e : indeg.entrySet())
            if (e.getValue() == 0) q.add(e.getKey());

        while (!q.isEmpty()) {
            String node = q.poll();
            String v = value.get(node);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < v.length(); i++) {
                if (i + 2 < v.length() && v.charAt(i) == '%' && v.charAt(i + 2) == '%') {
                    String dep = String.valueOf(v.charAt(i + 1));
                    sb.append(value.getOrDefault(dep, "%" + dep + "%"));
                    i += 2;
                } else {
                    sb.append(v.charAt(i));
                }
            }
            value.put(node, sb.toString());
            for (String dep : adj.getOrDefault(node, List.of())) {
                indeg.put(dep, indeg.get(dep) - 1);
                if (indeg.get(dep) == 0) q.add(dep);
            }
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (i + 2 < text.length() && text.charAt(i) == '%' && text.charAt(i + 2) == '%') {
                String key = String.valueOf(text.charAt(i + 1));
                out.append(value.getOrDefault(key, "%" + key + "%"));
                i += 2;
            } else {
                out.append(text.charAt(i));
            }
        }
        return out.toString();
    }
}
```

## Reading the code — what's actually happening

The code is three phases; keep them separate in your head:

1. **Graph construction.** The first `while` loop scans every replacement value for `%D%` patterns. Each hit records `adjList[D] += key` (a *reverse* edge: "key depends on D") and bumps `inDegree[key]`. Keys with no dependencies get in-degree 0. This is exactly the `prereq -> course` edge convention from [6.3](../ch06-graphs/course-schedule-ii.md), mirrored.
2. **Kahn's resolution.** The queue starts with all in-degree-0 keys. When a key `node` is dequeued, **all of its dependencies are already resolved** (that's the topological invariant!), so we can flatten its value with a placeholder scan — every `%D%` becomes `map[D]`, which is final. After flattening, we decrement the in-degree of every key that *depends on* `node`; the moment one reaches 0, it's unblocked and joins the queue.
3. **Final text pass.** After the queue drains, every *acyclic* key holds a flat string. The last function scans `text` once, replacing placeholders from the now-final map. Keys stuck in a cycle never entered the queue, so `map[key]` still holds `%D%`-laden text — the fallback `map[depKey] ?: "%$depKey%"` handles unknown/cyclic references without crashing.

**Why the `string/ApplySubstitutions.kt` variant exists:** it's the *recursive* version — `resolve` keeps calling itself while the result changes. That's correct for acyclic inputs and much shorter, but it can't detect cycles and can recurse deeply. The topological version is the "interview-grade" answer: linear time, explicit cycle handling, and it demonstrates that you recognize a dependency graph when you see one.

## Dry run

**Input:** `replacements = [["A", "abc%B%"], ["B", "xy"]], text = "%A%"`.

```
Graph build:
  A's value "abc%B%" contains %B% -> edge B -> A, inDegree[A] = 1
  B's value "xy" has no placeholders -> inDegree[B] = 0

Kahn:
  queue = [B]
  process B: value[B] = "xy" (already flat).  dependents of B: [A].
    inDegree[A] = 0 -> queue = [A]
  process A: value[A] = "abc" + value[B] = "abcxy".  no dependents.

Final text "%A%": one placeholder -> map[A] = "abcxy"
Output: "abcxy" ✓
```

A cycle case: `[["X","%Y%z"],["Y","w%X%"]]`. Both get in-degree 1, the queue is empty, nothing is processed, and `text = "%X%"` resolves to `"%X%"` (unchanged) — the cycle is visible as "keys never processed".

## Complexity

**Time.** Each placeholder is scanned a constant number of times total: O(total length of all values + length of text).

$$
T(n) = O\left(\sum |value_i| + |text|\right)
$$

**Space.** The graph and maps:

$$
S(n) = O\left(\sum |value_i|\right)
$$

## Variants & follow-ups

- **Course Schedule / Course Schedule II** ([6.3](../ch06-graphs/course-schedule-ii.md)) — the identical engine: Kahn's algorithm over a dependency graph. This page is that problem with string "courses" and a flattening step on top.
- **Iterate-until-stable substitution** — the recursive sibling in `string/ApplySubstitutions.kt`: simpler, no cycle detection. Mention both in an interview and say *why* you'd pick the topological version at scale.
- **Expression DAGs / build systems** — "make"-style tools resolve targets from sources in dependency order; the same Kahn's pass is what `tsc --build`-style tools use to order compilations.
- **Interview follow-up:** "What if a cycle exists?" Kahn's leaves cycle keys at in-degree > 0 — count the unprocessed keys or check the queue drained fully. That single check turns this into cycle detection, exactly like Course Schedule.
