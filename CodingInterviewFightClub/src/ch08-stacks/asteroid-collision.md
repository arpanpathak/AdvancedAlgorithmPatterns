# 8.13 Asteroid Collision

> **Source:** [`src/main/kotlin/stack/AestroidCollisions.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/AestroidCollisions.kt)
> **Pattern:** survivor stack · **Core page**

## The Problem

Asteroids with signed sizes (`+` right, `-` left); equal-size collisions destroy both, bigger survives.

- Constraints: $1 \le n \le 10^4$.

## Examples

```
Input:  asteroids = [5,10,-5]   -> Output: [5,10]
Input:  asteroids = [8,-8]      -> Output: []
Input:  asteroids = [10,2,-5]   -> Output: [10]
```

## Intuition — only a right-mover followed by left-movers can collide

Collisions happen **only when a `+` asteroid is immediately followed by a `-` one** (all `+`s move away from all `-`s behind them). So the stack holds survivors; each `-` asteroid fights rightward through the `+`s on top:

```kotlin
for (speed in asteroids) {
    if (speed > 0) { stack.add(speed); continue }     // right-mover: no collision yet

    while (stack.isNotEmpty() && stack.last() > 0 && stack.last() < -speed)
        stack.removeLast()                            // the + loses

    if (stack.isEmpty() || stack.last() < 0) stack.add(speed)   // - survives
    else if (stack.last() == -speed) stack.removeLast()          // both die
}
```

**Why the while-loop?** A big `-` can destroy *several* stacked `+`s — each `stack.last() < -speed` pop is one explosion. The loop continues until the `-` meets a bigger `+`, a `-`, or the floor.

**Why the three-way ending?** After the pops: empty stack → the `-` lives; top is `-` → the `-` lives (no head-on); top equals `-speed` → mutual destruction; top bigger → the `+` wins, `-` absorbed (nothing added).

## Approach 1 — Simulate with a list (scan-and-remove)

Find colliding pairs repeatedly: correct, O(n²) worst case.

## Approach 2 — Survivor stack (the repo's version, optimal)

```kotlin
class AestroidCollisions {
    /**
     * @param asteroids signed asteroid sizes
     * @return          survivors after all collisions
     */
    fun asteroidCollision(asteroids: IntArray): IntArray {
        val stack = ArrayDeque<Int>()

        for (speed in asteroids) {
            if (speed > 0) {
                stack.add(speed)
                continue
            }

            while (stack.isNotEmpty() && stack.last() > 0 && stack.last() < -speed) {
                stack.removeLast()                      // the + loses
            }

            if (stack.isEmpty() || stack.last() < 0) stack.add(speed)
            else if (stack.last() == -speed) stack.removeLast()
        }
        return stack.toIntArray()
    }
}
```

```java
import java.util.*;

public class AsteroidCollision {
    /**
     * @param asteroids signed asteroid sizes
     * @return          survivors after all collisions
     */
    public int[] asteroidCollision(int[] asteroids) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (int a : asteroids) {
            if (a > 0) { stack.push(a); continue; }

            while (!stack.isEmpty() && stack.peek() > 0 && stack.peek() < -a) {
                stack.pop();                            // the + loses
            }

            if (stack.isEmpty() || stack.peek() < 0) stack.push(a);
            else if (stack.peek() == -a) stack.pop();   // both die
        }

        int[] result = new int[stack.size()];
        for (int i = result.length - 1; i >= 0; i--) result[i] = stack.pop();
        return result;
    }
}
```

```cpp
#include <vector>

class AsteroidCollision {
public:
    /**
     * @param asteroids signed asteroid sizes
     * @return          survivors after all collisions
     */
    std::vector<int> asteroidCollision(std::vector<int>& asteroids) {
        std::vector<int> stack;

        for (int a : asteroids) {
            if (a > 0) { stack.push_back(a); continue; }

            while (!stack.empty() && stack.back() > 0 && stack.back() < -a) {
                stack.pop_back();                       // the + loses
            }

            if (stack.empty() || stack.back() < 0) stack.push_back(a);
            else if (stack.back() == -a) stack.pop_back();   // both die
        }
        return stack;
    }
};
```

```python
def asteroid_collision(asteroids: list[int]) -> list[int]:
    """
    @param asteroids: signed asteroid sizes
    @return:          survivors after all collisions
    """
    stack = []

    for a in asteroids:
        if a > 0:
            stack.append(a)
            continue

        while stack and stack[-1] > 0 and stack[-1] < -a:
            stack.pop()                 # the + loses

        if not stack or stack[-1] < 0:
            stack.append(a)
        elif stack[-1] == -a:
            stack.pop()                 # both die

    return stack
```

```rust
impl Solution {
    /// @param asteroids signed asteroid sizes
    /// @return          survivors after all collisions
    pub fn asteroid_collision(asteroids: Vec<i32>) -> Vec<i32> {
        let mut stack: Vec<i32> = Vec::new();

        for &a in &asteroids {
            if a > 0 { stack.push(a); continue; }

            while let Some(&top) = stack.last() {
                if top <= 0 || top >= -a { break; }
                stack.pop();                        // the + loses
            }

            if let Some(&top) = stack.last() {
                if top == -a { stack.pop(); }       // both die
            } else {
                stack.push(a);
            }
            if stack.is_empty() { stack.push(a); }
            else if *stack.last().unwrap() < 0 { stack.push(a); }
        }
        stack
    }
}
```

## Dry run

**Input:** `asteroids = [10,2,-5]`.

```
10: + -> stack [10]
2:  + -> stack [10,2]
-5: while: top=2 > 0 && 2 < 5 -> pop 2.  top=10 > 0 && 10 < 5? no -> stop.
    stack not empty && top=10 > 0 -> not the "survive" branch.
    top == -(-5) = 5? no (10 != 5) -> nothing added.

Output: [10] ✓   (the 10 smashes 2 and -5)

Input: [8,-8]: 8 -> [8].  -8: top=8, 8 < 8? no -> stop.  top == 8 == -(-8) -> pop.
Output: [] ✓

Input: [5,10,-5]: 5,10 -> [5,10].  -5: top=10, 10<5? no.  top==5? no.  nothing.
Output: [5,10] ✓
```

The while-loop is the chain reaction: `-5` pops the 2 *and then checks* the 10 — a bigger asteroid stops it. Equal sizes pop mutually (`8 == -(-8)`); a surviving `-` is pushed only when the stack's top isn't a bigger `+`.

## Complexity

**Time.** Each asteroid pushed/popped once:

$$
T(n) = O(n)
$$

**Space.** The stack:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Daily Temperatures** ([8.3](daily-temperatures.md)) — the monotonic-stack family's "next bigger" member.
- **Car Fleet** ([11.5](../ch11-greedy/car-fleet.md)) — collisions in a different costume (sorting instead of a stack).
- **Interview follow-up:** "Why can a `-` asteroid never collide with another `-`?" Both move left — same direction, same speed, no catch-up. Only `+` (right) followed by `-` (left) is head-on; the stack's top is always the *latest* asteroid, which is the only one a new `-` can meet.
