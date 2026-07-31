# 11.23 Count Collisions On A Road

> **Source**: [`src/main/kotlin/simulation/CountCollisionsOnARoad.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/simulation/CountCollisionsOnARoad.kt) (a stub in the repo — the canonical greedy below)
> **Pattern**: directional sweep · **Core page**

## The Problem

Cars on a road moving R (right) or L (left); on collision both stop. Count all cars that collide (directly or in a pile-up).

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  directions = "RLRSLL"   -> Output: 5
Input:  directions = "LLRR"     -> Output: 0
Input:  directions = "SSR"      -> Output: 0? no — "SSR": R at the end never collides -> 0... wait "SSR": S S R, R moves right off -> 0? Actually the known: "SSR" -> 0? Hmm no: an R at the end moves off the road -> 0.
```

## Intuition — the first non-L segment collides; everything in it counts

A car collides iff it's in the **first contiguous block after the leading Ls** — the leading Ls move away left (safe); once a car is stopped (S), everything behind it piles up:

```kotlin
val chars = directions.toCharArray()
var collisions = 0
var i = 0

while (i < chars.size && chars[i] == 'L') i++      // leading Ls escape

var hasStopped = false
for (j in i until chars.size) {
    when {
        chars[j] == 'R' -> hasStopped = false        // hmm — R moving right...
        ...
    }
}
```

The canonical solution: find the first non-L index; then count every char from there that is not a leading... Actually the clean version:

```kotlin
// skip leading Ls, skip trailing Rs: the middle must collide
var left = 0
while (left < n && directions[left] == 'L') left++
var right = n - 1
while (right >= 0 && directions[right] == 'R') right--

if (left >= right) return 0
return right - left + 1 - (count of 'S' in [left, right])
```

**Why leading-Ls and trailing-Rs are safe?** Leading Ls move left off the road (nothing in front); trailing Rs move right off. Every car *between* them faces an opposing direction somewhere — all collide. The [11.0](pattern-primer.md) boundary-scan greedy.

## Approach 1 — Boundary exclusion (the canonical, optimal)

```kotlin
class CountCollisionsOnARoad {
    /**
     * @param directions car directions (R, L, S)
     * @return           number of colliding cars
     */
    fun countCollisions(directions: String): Int {
        val n = directions.length

        var left = 0
        while (left < n && directions[left] == 'L') left++    // leading Ls escape

        var right = n - 1
        while (right >= 0 && directions[right] == 'R') right--  // trailing Rs escape

        if (left >= right) return 0

        var collisions = 0
        for (i in left..right) {
            if (directions[i] != 'S') collisions++    // S cars don't move: no collision of their own... 
        }
        // correction: every non-S car in [left, right] collides (R hits something ahead,
        // L hits something behind) — the count is (right - left + 1) - S_count.
        return collisions
    }
}
```

```java
public class CountCollisionsOnARoad {
    /**
     * @param directions car directions (R, L, S)
     * @return           number of colliding cars
     */
    public int countCollisions(String directions) {
        int n = directions.length();

        int left = 0;
        while (left < n && directions.charAt(left) == 'L') left++;

        int right = n - 1;
        while (right >= 0 && directions.charAt(right) == 'R') right--;

        if (left > right) return 0;

        int collisions = 0;
        for (int i = left; i <= right; i++) {
            if (directions.charAt(i) != 'S') collisions++;
        }
        return collisions;
    }
}
```

```cpp
#include <string>

class CountCollisionsOnARoad {
public:
    /**
     * @param directions car directions (R, L, S)
     * @return           number of colliding cars
     */
    int countCollisions(std::string directions) {
        int n = directions.size();

        int left = 0;
        while (left < n && directions[left] == 'L') left++;

        int right = n - 1;
        while (right >= 0 && directions[right] == 'R') right--;

        if (left > right) return 0;

        int collisions = 0;
        for (int i = left; i <= right; i++) {
            if (directions[i] != 'S') collisions++;
        }
        return collisions;
    }
};
```

```python
def count_collisions(directions: str) -> int:
    """
    @param directions: car directions (R, L, S)
    @return:           number of colliding cars
    """
    n = len(directions)

    left = 0
    while left < n and directions[left] == "L":
        left += 1

    right = n - 1
    while right >= 0 and directions[right] == "R":
        right -= 1

    if left > right:
        return 0

    return sum(1 for i in range(left, right + 1) if directions[i] != "S")
```

```rust
impl Solution {
    /// @param directions car directions (R, L, S)
    /// @return           number of colliding cars
    pub fn count_collisions(directions: String) -> i32 {
        let bytes: Vec<char> = directions.chars().collect();
        let n = bytes.len();

        let mut left = 0;
        while left < n && bytes[left] == 'L' { left += 1; }

        let mut right = n - 1;
        while right > 0 && bytes[right] == 'R' { right -= 1; }

        if left >= right { return 0; }

        (left..=right).filter(|&i| bytes[i] != 'S').count() as i32
    }
}
```

## Dry run

**Input:** `directions = "RLRSLL"`.

```
left: skip leading Ls? first char R -> left=0.  right: from the end, skip Rs? last is L -> right=5.
middle [0,5]: non-S cars: R,L,R,L,L = 5.
Output: 5 ✓  (the R collides with L, pile-up catches R,S?,L,L — S stays stopped, counts? 
  the official answer: 5 colliding cars of 6 — the first R hits the L, the pile stops R,L,S and 
  the two trailing Ls hit the stopped pile = 5.  S itself is not counted (it never moves).)

Input: "LLRR": left skips 2 Ls -> left=2.  right skips 2 Rs -> right=1.  left > right -> 0 ✓
```

## Complexity

**Time.** Two boundary scans + middle:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Asteroid Collision** ([8.13](../ch08-stacks/asteroid-collision.md)) — the stack version of directional collisions.
- **Interview follow-up:** "Why are the excluded boundaries exactly safe?" A leading L has nothing in front (it exits left); a trailing R has nothing behind (it exits right). Every car between faces at least one opposing mover in the segment — it must collide. The S cars in the middle never move but the pile hits them... the count excludes S (stationary cars don't collide *on their own*, but they block others — the formula counts the moving cars that hit the pile).
