# 3.20 Robot Bounded In Circle

> **Source:** [`src/main/kotlin/simulation/RobotBoundedInCircle.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/simulation/RobotBoundedInCircle.kt)
> **Pattern:** direction-state simulation · **Core page**

## The Problem

Robot at origin facing north; `G` moves, `L`/`R` turn 90°. Is its path **bounded** after repeating the instructions forever?

- Constraints: $1 \le |instructions| \le 100$.

## Examples

```
Input:  instructions = "GGLLGG"   -> Output: true   (returns to origin each cycle)
Input:  instructions = "GG"       -> Output: false  (walks away)
Input:  instructions = "GL"       -> Output: true   (square after 4 cycles)
```

## Intuition — after one cycle, bounded iff back at origin OR facing non-north

Repeating the instructions is a *cycle*: after one pass, the robot has moved by some `(dx, dy)` and rotated by some multiple of 90°. The path is bounded iff the net motion over repeated cycles cancels:

```kotlin
var (x, y) = 0 to 0
var dir = 0                                   // 0=N, 1=E, 2=S, 3=W
val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)

for (c in instructions) when (c) {
    'G' -> { x += directions[dir].first; y += directions[dir].second }
    'L' -> dir = (dir + 3) % 4
    'R' -> dir = (dir + 1) % 4
}

return (x == 0 && y == 0) || (dir != 0)
```

**Why `(x, y) == (0,0)` OR `dir != 0`?** 
- Back at origin → each cycle returns → bounded.
- Facing non-north (turned 90°/180°/270°) → the next cycle's displacement is rotated, and after ≤ 4 cycles the displacements sum to zero → bounded.
- Facing north but moved → each cycle adds the same displacement → unbounded.

**Why the `dir` array?** The four compass directions as unit vectors with `(dir ± 1) % 4` — the turn is an index shift, `G` a vector add. The [6.x](../ch06-graphs/pattern-primer.md) "directions as data" idiom.

## Approach 1 — Simulate 4 cycles (position check)

Run the instructions 4 times, check if back at origin: correct, 4× slower, same idea.

## Approach 2 — One cycle + direction test (the repo's version, optimal)

```kotlin
class RobotBoundedInCircle {
    /**
     * @param instructions G/L/R commands
     * @return            true iff the path is bounded
     */
    fun isRobotBounded(instructions: String): Boolean {
        var (x, y) = 0 to 0
        var dir = 0
        val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)   // N E S W

        for (c in instructions) when (c) {
            'G' -> { x += directions[dir].first; y += directions[dir].second }
            'L' -> dir = (dir + 3) % 4
            'R' -> dir = (dir + 1) % 4
        }

        return (x == 0 && y == 0) || (dir != 0)
    }
}
```

```java
public class RobotBoundedInCircle {
    /**
     * @param instructions G/L/R commands
     * @return            true iff the path is bounded
     */
    public boolean isRobotBounded(String instructions) {
        int x = 0, y = 0, dir = 0;                       // 0=N 1=E 2=S 3=W
        int[][] dirs = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (char c : instructions.toCharArray()) {
            if (c == 'G') { x += dirs[dir][0]; y += dirs[dir][1]; }
            else if (c == 'L') dir = (dir + 3) % 4;
            else dir = (dir + 1) % 4;
        }

        return (x == 0 && y == 0) || dir != 0;
    }
}
```

```cpp
#include <string>

class RobotBoundedInCircle {
public:
    /**
     * @param instructions G/L/R commands
     * @return            true iff the path is bounded
     */
    bool isRobotBounded(std::string instructions) {
        int x = 0, y = 0, dir = 0;                       // 0=N 1=E 2=S 3=W
        int dirs[4][2] = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (char c : instructions) {
            if (c == 'G') { x += dirs[dir][0]; y += dirs[dir][1]; }
            else if (c == 'L') dir = (dir + 3) % 4;
            else dir = (dir + 1) % 4;
        }

        return (x == 0 && y == 0) || dir != 0;
    }
};
```

```python
def is_robot_bounded(instructions: str) -> bool:
    """
    @param instructions: G/L/R commands
    @return:             true iff the path is bounded
    """
    x = y = 0
    dir = 0                                  # 0=N 1=E 2=S 3=W
    dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)]

    for c in instructions:
        if c == "G":
            x += dirs[dir][0]
            y += dirs[dir][1]
        elif c == "L":
            dir = (dir + 3) % 4
        else:
            dir = (dir + 1) % 4

    return (x == 0 and y == 0) or dir != 0
```

```rust
impl Solution {
    /// @param instructions G/L/R commands
    /// @return            true iff the path is bounded
    pub fn is_robot_bounded(instructions: String) -> bool {
        let dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)];
        let (mut x, mut y) = (0i32, 0i32);
        let mut dir = 0usize;                 // 0=N 1=E 2=S 3=W

        for c in instructions.chars() {
            match c {
                'G' => { x += dirs[dir].0; y += dirs[dir].1; }
                'L' => dir = (dir + 3) % 4,
                _ => dir = (dir + 1) % 4,
            }
        }

        (x == 0 && y == 0) || dir != 0
    }
}
```

## Dry run

**Input:** `instructions = "GL"`.

```
x=0,y=0,dir=0(N)
'G': move N -> (0,1).  dir=0
'L': dir = (0+3)%4 = 3 (W)

End: x=0,y=1, dir=3 != 0 -> true ✓

Cycle 2: from (0,1) facing W: 'G' -> (-1,1).  'L' -> dir=2 (S)
Cycle 3: 'G' -> (-1,0).  'L' -> dir=1 (E)
Cycle 4: 'G' -> (0,0).  'L' -> dir=0 (N).  Back to origin!
```

The direction test catches what the position check alone misses: after "GL" the robot is at (0,1) — not the origin — but it's facing West, so the next three cycles rotate the displacement (0,1) → (−1,0) → (0,−1) → (1,0), summing to zero. "GG": ends at (0,2) facing N → `dir == 0` and not at origin → false ✓.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Design Circular Robot** (`simulation/`) — the movement-simulation family.
- **Interview follow-up:** "Why does 'facing non-north' guarantee boundedness?" A 90° turn rotates the next cycle's displacement; after 1, 2, or 4 cycles the rotated vectors sum to zero (a 270° turn is just 3×90°). Only the north-facing-with-net-motion case repeats the same displacement forever — the exact `!atOrigin && dir == 0` condition.
