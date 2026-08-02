# 17.19 How Many Rectangles Overlap (Sweep Line)

> **Source:** [`src/main/kotlin/math/geometry/HowManyRectanglesOverlapSweepLine.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/geometry/HowManyRectanglesOverlapSweepLine.kt) · [`src/main/kotlin/math/geometry/interval/HowManyRectangleOverlapsIntervalTree.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/geometry/interval/HowManyRectangleOverlapsIntervalTree.kt) · [`src/main/kotlin/math/geometry/interval/RectangeOverlapCountTreeSet.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/geometry/interval/RectangeOverlapCountTreeSet.kt)
> **Pattern:** sweep line + BST range query · **Core page**

## The Problem

Given `n` axis-aligned rectangles (each `[bottomX, bottomY, topX, topY]`), count the number of **pairs that overlap with positive area** (touching edges don't count).

- Constraints: integer coordinates; `n` up to $10^5$ — the quadratic pair check is the thing to kill.

## Examples

```
rectangles:
  A = [0, 5, 5, 10]
  B = [3, 3, 7, 7]
  C = [6, 2, 10, 6]
  D = [15, 10, 20, 15]

Overlapping pairs: A∩B (yes), B∩C (yes), A∩C (no — A ends at x=5, C starts at x=6)
Output: 2
```

## Intuition — the vertical sweep line turns "rectangles" into "interval overlaps"

The O(n²) brute force compares every pair. The sweep-line insight borrowed from [7.8](../ch07-heaps/the-skyline-problem.md):

> Imagine a vertical line moving left to right. A rectangle is **active** while the line is between its left and right edges. Two rectangles overlap **iff** there is a moment when both are active **and** their vertical intervals intersect.

So instead of checking pairs directly, we process **events** — left edges (rectangle enters) and right edges (rectangle exits) — sorted by x. At any left-edge event, the rectangle's vertical interval `[bottomY, topY]` is compared against the *currently active* rectangles' intervals. Every active rectangle whose y-interval intersects the new one is one overlapping pair.

**Why is comparing against active rectangles enough?** If two rectangles overlap, their x-intervals overlap too — so when the second one's left edge arrives, the first one is still active (its right edge hasn't been processed yet). The pair is caught exactly once, at the later rectangle's START event. Counting pairs at START events, never at END events, avoids double-counting.

**Making the y-comparison fast:** the active set is kept sorted by `bottomY` in a balanced BST (`TreeSet` in Kotlin/Java). For a new rectangle `R`, every potentially-overlapping active rectangle must have `bottomY < R.topY` (if an active rectangle's bottom is at or above R's top, their y-intervals can't intersect). A BST *range query* (`headSet` — "everything with bottomY below this") retrieves those candidates without scanning the whole active set.

## Approach 1 — Brute force: check every pair (O(n²))

For each pair, test the axis-separation condition from [3.31](../ch03-arrays/rectangle-overlap.md): `aX1 < bX2 && bX1 < aX2 && aY1 < bY2 && bY1 < aY2`. Correct, but $n = 10^5$ makes it $10^{10}$ comparisons — hopeless.

## Approach 2 — Sweep line with a BST active set (the repo's version, optimal)

```kotlin
data class Rectangle(
    val bottomX: Int,
    val bottomY: Int,
    val topX: Int,
    val topY: Int
)

enum class EventType(val value: Int) {
    START(1),   // sweep line enters a rectangle
    END(-1)     // sweep line exits a rectangle
}

data class SweepEvent(
    val x: Int,
    val rect: Rectangle,
    val type: EventType
) : Comparable<SweepEvent> {
    override fun compareTo(other: SweepEvent): Int {
        if (this.x != other.x) return this.x.compareTo(other.x)
        return other.type.value.compareTo(this.type.value)   // START before END at same x
    }
}

private fun isYOverlap(r1: Rectangle, r2: Rectangle): Boolean {
    // Non-overlap in Y: one is entirely below/above the other
    val isNonOverlappingY = (r1.topY <= r2.bottomY || r1.bottomY >= r2.topY)
    return !isNonOverlappingY
}

fun countOverlappingPairsSweepLine(rectangles: List<Rectangle>): Int {
    if (rectangles.size < 2) return 0

    // Timeline: each rectangle contributes a START (left edge) and END (right edge)
    val events = rectangles.flatMap { rect ->
        listOf(
            SweepEvent(rect.bottomX, rect, EventType.START),
            SweepEvent(rect.topX, rect, EventType.END)
        )
    }.sorted()

    var overlapCount = 0

    // Active rectangles sorted by bottomY — supports range queries
    val activeRects = sortedSetOf<Rectangle>(compareBy { it.bottomY })

    for (event in events) {
        when (event.type) {
            EventType.START -> {
                val currentRect = event.rect

                // Range query: only rectangles with bottomY < currentRect.topY
                // can possibly overlap from below
                val potentialFromBelow = activeRects.headSet(
                    Rectangle(0, currentRect.topY, 0, 0)   // dummy for comparison
                )

                overlapCount += potentialFromBelow.count { activeRect ->
                    activeRect.topY > currentRect.bottomY &&
                            isYOverlap(currentRect, activeRect)
                }

                activeRects.add(currentRect)
            }
            EventType.END -> {
                activeRects.remove(event.rect)
            }
        }
    }
    return overlapCount
}
```

```python
from bisect import insort
from dataclasses import dataclass

@dataclass
class Rectangle:
    bottomX: int; bottomY: int; topX: int; topY: int

def count_overlapping_pairs_sweep_line(rects):
    # events: (x, type, rect) with type START before END on equal x
    events = []
    for r in rects:
        events.append((r.bottomX, 1, r))   # START
        events.append((r.topX, -1, r))     # END
    events.sort(key=lambda e: (e[0], -e[1]))

    def y_overlap(r1, r2):
        return not (r1.topY <= r2.bottomY or r1.bottomY >= r2.topY)

    active = []   # sorted by bottomY (kept with insort)
    count = 0
    for x, typ, r in events:
        if typ == 1:
            # candidates: active rects with bottomY < r.topY
            for a in active:
                if a.bottomY >= r.topY:
                    break
                if a.topY > r.bottomY and y_overlap(r, a):
                    count += 1
            insort(active, r, key=lambda a: a.bottomY)
        else:
            active.remove(r)
    return count
```

```java
import java.util.*;

class CountOverlappingPairs {
    record Rect(int bx, int by, int tx, int ty) {}

    private boolean yOverlap(Rect a, Rect b) {
        return !(a.ty() <= b.by() || a.by() >= b.ty());
    }

    /**
     * @param rectangles axis-aligned rects [bottomX, bottomY, topX, topY]
     * @return           number of positive-area overlapping pairs
     */
    public int countOverlappingPairs(int[][] rectangles) {
        List<int[]> events = new ArrayList<>();   // {x, type, bx, by, tx, ty}
        for (int[] r : rectangles) {
            events.add(new int[]{r[0], 1, r[0], r[1], r[2], r[3]});   // START
            events.add(new int[]{r[2], -1, r[0], r[1], r[2], r[3]});  // END
        }
        events.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : b[1] - a[1]);

        TreeSet<int[]> active = new TreeSet<>((a, b) -> a[1] != b[1] ? a[1] - b[1]
                : (a[3] != b[3] ? a[3] - b[3] : a[2] - b[2]));
        int count = 0;

        for (int[] e : events) {
            if (e[1] == 1) {   // START
                Rect cur = new Rect(e[2], e[3], e[4], e[5]);
                for (int[] a : active.headSet(new int[]{0, cur.ty(), 0, 0, 0, 0}, false)) {
                    Rect ar = new Rect(a[2], a[3], a[4], a[5]);
                    if (ar.ty() > cur.by() && yOverlap(cur, ar)) count++;
                }
                active.add(e);
            } else {           // END
                active.remove(e);
            }
        }
        return count;
    }
}
```

## Reading the code — what's actually happening

1. **Event generation.** Each rectangle becomes two events: `START` at `bottomX` (its left edge) and `END` at `topX` (its right edge). The comparator sorts by x first — and at *equal x*, START sorts before END (`other.type.value.compareTo(this.type.value)` with START=1, END=-1 means START comes first). Why? Two rectangles sharing a left edge both become active together, and a rectangle ending exactly where another begins shouldn't count as overlapping — the ordering makes that boundary exact.
2. **The START branch is where counting happens.** `activeRects.headSet(dummy)` returns every active rectangle whose `bottomY < currentRect.topY` — the BST's `headSet` is a *range query*, O(log n + k) instead of scanning all actives. Then the `count` filters the candidates: `activeRect.topY > currentRect.bottomY` (the mirror condition — only rectangles whose top is above our bottom can intersect) plus the explicit `isYOverlap` check. Every hit is one overlapping pair.
3. **`activeRects.add(currentRect)`** makes the new rectangle active for all future events to its right.
4. **The END branch just removes the rectangle.** No counting here — pairs were already counted at the later rectangle's START. This is what prevents double-counting.
5. **The dummy `Rectangle(0, currentRect.topY, 0, 0)`** exists only because `headSet` compares whole objects by `bottomY`; the dummy carries the threshold value `currentRect.topY` and nothing else matters.

**Why the y-range query prunes correctly:** an active rectangle with `bottomY >= currentRect.topY` sits entirely *above* the new rectangle — their y-intervals can't intersect (touch doesn't count, matching `isYOverlap`'s strict inequalities). Every candidate below that line is at least *potentially* overlapping, and the final `topY > bottomY` + `isYOverlap` filter confirms it. The interval-tree and TreeSet variants in the repo are the same idea with different range-query machinery.

## Dry run

**Input:** rectangles A=[0,5,5,10], B=[3,3,7,7], C=[6,2,10,6], D=[15,10,20,15].

```
Events sorted by x (START before END at equal x):
  x=0  START A
  x=3  START B
  x=5  END A
  x=6  START C
  x=7  END B
  x=10 END C
  x=15 START D
  x=20 END D

x=0  START A: active empty -> 0.                add A.
x=3  START B: headSet(topY=7) = {A}.  A.topY=10 > 3 and yOverlap(A,B)? [5,10] vs [3,7] -> yes.  count=1.  add B.
x=5  END A: remove A.
x=6  START C: headSet(topY=6) = {B}.  B.topY=7 > 2 and yOverlap(B,C)? [3,7] vs [2,6] -> yes.  count=2.  add C.
x=7  END B / x=10 END C / x=15 START D: D's headSet is empty (active empty).
Output: 2 ✓
```

Notice A and C never meet: when C starts at x=6, A already ended at x=5 — the sweep's ordering guarantees they were never simultaneously active.

## Complexity

**Time.** Each of the 2n events does O(log n) BST work plus O(k) candidate checks, where k is the (small) candidate set:

$$
T(n) = O(n \log n + \text{pairs})
$$

**Space.** The event list and active set:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Rectangle Area II** ([3.33](../ch03-arrays/rectangle-area-ii.md)) — the same sweep line, but *measuring* union area instead of counting pairs; the active set tracks y-intervals with a segment tree.
- **The Skyline Problem** ([7.8](../ch07-heaps/the-skyline-problem.md)) — the sweep-line ancestor this page's events are modeled on.
- **Count overlaps of intervals (1-D)** — the same sweep over a single axis: sort starts/ends, keep a running active count; the 1-D warm-up for this page.
- **The repo's alternative implementations** — `HowManyRectangleOverlapsIntervalTree.kt` (an interval tree answering y-range queries directly) and `RectangeOverlapCountTreeSet.kt` (a hand-rolled ordered set with the same range-query idea). Same algorithm, different range-query organs — worth reading side by side.
- **Interview follow-up:** "Why don't we double-count?" Every overlapping pair {X, Y} is counted exactly once: at the START event of whichever rectangle starts *later* (their x-intervals overlap, so the earlier one is still active). END events never count. The invariant "count only at START" is the whole anti-double-count argument.
