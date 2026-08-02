# 11.13 Destroying Asteroids

> **Source:** [`src/main/kotlin/greedy/DestroyingAsteroids.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/DestroyingAsteroids.kt)
> **Pattern:** sort + accumulate · **Core page**

## The Problem

Starting with `mass`, destroy asteroids in any order: an asteroid ≤ current mass is destroyed and adds its mass; if none can be destroyed, fail.

- Constraints: asteroids ≤ 10⁵; mass fits in `Long`.

## Examples

```
Input:  mass = 10, asteroids = [3,9,19,5,21]   -> Output: true   (small to large: 10→13→22→41→62)
Input:  mass = 5,  asteroids = [4,4,9,16]      -> Output: false  (5→9→13→22: can't take 16... wait 22 >= 16 ✓ -> true? no: 5+4=9, +4=13, +9=22, +16=38 -> true)
```
Let me re-check: `mass = 5, asteroids = [4,4,9,16]`: take 4 (9), take 4 (13), take 9 (22), take 16 (38) → true. A false case: `mass = 3, asteroids = [3,9,2]`: take 3 (6), take 2 (8), can't take 9 → false? Wait 6+2=8 < 9 → false ✓.

## Intuition — eat the smallest first; the mass only grows

The greedy is forced: to maximize future ability, consume the **smallest** asteroid you can — every choice that works leaves you with at least as much mass as any other order:

```kotlin
var currentMass = mass.toLong()
asteroids.sort()

for (asteroidMass in asteroids) {
    if (asteroidMass > currentMass) return false     // stuck
    currentMass += asteroidMass                       // eat it
}
return true
```

**Why sorting is the greedy?** The [11.0](pattern-primer.md) exchange argument: if an order works, sorting it ascending also works — each step's mass is ≥ the unsorted order's mass at that point (you've eaten no heavier asteroids earlier). So ascending is the *most permissive* order; if it fails, all orders fail.

**Why `toLong()`?** `mass` grows by up to 10⁵ × 10⁵ — the running total overflows `Int`. The Long cast is the [1.x](../ch01-binary-search/pattern-primer.md) hygiene.

## Approach 1 — Try all orders (exponential)

Permutation search: correct, absurd.

## Approach 2 — Sort and eat (the repo's version, optimal)

```kotlin
class DestroyingAsteroids {
    /**
     * @param mass      starting mass
     * @param asteroids asteroid masses
     * @return          true iff all can be destroyed
     */
    fun asteroidsDestroyed(mass: Int, asteroids: IntArray): Boolean {
        var currentMass = mass.toLong()
        asteroids.sort()

        for (asteroidMass in asteroids) {
            if (asteroidMass > currentMass) return false
            currentMass += asteroidMass
        }
        return true
    }
}
```

```java
import java.util.*;

public class DestroyingAsteroids {
    /**
     * @param mass      starting mass
     * @param asteroids asteroid masses
     * @return          true iff all can be destroyed
     */
    public boolean asteroidsDestroyed(int mass, int[] asteroids) {
        long current = mass;
        Arrays.sort(asteroids);

        for (int a : asteroids) {
            if (a > current) return false;
            current += a;
        }
        return true;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class DestroyingAsteroids {
public:
    /**
     * @param mass      starting mass
     * @param asteroids asteroid masses
     * @return          true iff all can be destroyed
     */
    bool asteroidsDestroyed(int mass, std::vector<int>& asteroids) {
        long long current = mass;
        std::sort(asteroids.begin(), asteroids.end());

        for (int a : asteroids) {
            if (a > current) return false;
            current += a;
        }
        return true;
    }
};
```

```python
def asteroids_destroyed(mass: int, asteroids: list[int]) -> bool:
    """
    @param mass:      starting mass
    @param asteroids: asteroid masses
    @return:          true iff all can be destroyed
    """
    current = mass
    for a in sorted(asteroids):
        if a > current:
            return False
        current += a
    return True
```

```rust
impl Solution {
    /// @param mass      starting mass
    /// @param asteroids asteroid masses
    /// @return          true iff all can be destroyed
    pub fn asteroids_destroyed(mass: i32, mut asteroids: Vec<i32>) -> bool {
        let mut current: i64 = mass as i64;
        asteroids.sort_unstable();

        for a in asteroids {
            if a as i64 > current { return false; }
            current += a as i64;
        }
        true
    }
}
```

## Reading the code — what's actually happening

```kotlin
var currentMass = mass.toLong()
asteroids.sort()
for (asteroidMass in asteroids) {
    if (asteroidMass > currentMass) return false
    currentMass += asteroidMass
}
return true
```

Think of it as Pac-Man: your ship can only eat what's smaller than or equal to it, and every meal makes it bigger. The question is whether an eating order exists that clears the whole belt — and the answer is to **always eat the smallest thing in sight**.

- **`asteroids.sort()` is the greedy in one call.** Eating ascending guarantees that at every step we face the *easiest possible* asteroid. Why is this safe? Exchange argument: if *any* ordering works, the ascending ordering works too — because after `k` steps, ascending order has consumed the `k` smallest asteroids, so its mass is at least as large as any other order's mass at that point. A bigger mass can only make the next asteroid easier to eat. So ascending is the "most permissive" order.
- **`asteroidMass > currentMass` is the stuck test.** If even the smallest remaining asteroid is too big, no order can help — every other asteroid is even bigger, and mass never decreases. Fail immediately.
- **`currentMass += asteroidMass` is the growth rule.** Successfully eating an asteroid adds its full mass to ours. Since mass only grows, the check `asteroidMass > currentMass` is a *monotone* condition — once we pass an asteroid, the threshold for the next one is only higher.
- **Why `toLong()`?** The running total can reach `10⁵ × 10⁵ = 10¹⁰`, which overflows a 32-bit `Int`. Widening once at the start keeps every subsequent `+=` safe.

Trace `mass = 10, [3,9,19,5,21]`: sorted `[3,5,9,19,21]`; mass goes 10 → 13 → 18 → 27 → 46 → 67; every asteroid was ≤ current mass → `true` ✓.

## Dry run

**Input:** `mass = 10`, `asteroids = [3,9,19,5,21]`.

```
sorted: [3,5,9,19,21].  current = 10
3: 3 <= 10 -> current = 13
5: 5 <= 13 -> current = 18
9: 9 <= 18 -> current = 27
19: 19 <= 27 -> current = 46
21: 21 <= 46 -> current = 67

Output: true ✓

Input: mass = 3, asteroids = [3,9,2]: sorted [2,3,9].  current=3.
2 -> 5.  3 -> 8.  9 > 8 -> false ✓
```

The exchange argument in action: eating 2 then 3 (instead of 3 then 2) yields 8 — the maximal mass before facing 9. Any other order reaches ≤ 8 there, so failing on ascending order proves no order works. The mass monotone-increases, so the "stuck" test is one comparison per step.

## Complexity

**Time.** Sort dominates:

$$
T(n) = O(n \log n)
$$

**Space.** In-place sort:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Can Place Flowers** ([11.12](can-place-flowers.md)) — the same "local greedy is globally optimal" proof shape.
- **Minimum Time To Make Rope Colorful** (`array/greedy/MinimumTimeToMakeRopeColorful.kt`) — the adjacent-conflict greedy with costs.
- **Interview follow-up:** "Why is ascending the only order to test?" If *some* order succeeds, the ascending order succeeds too — at every step its mass is ≥ any other order's (it has eaten no heavier asteroid earlier). So ascending is the *most likely* to succeed; failure there is decisive. This "sort = the greedy champion" argument is the [11.0](pattern-primer.md) core.
