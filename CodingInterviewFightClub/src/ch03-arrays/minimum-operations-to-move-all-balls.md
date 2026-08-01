# 3.50 Minimum Operations To Move All Balls To Each Box

> **Source**: [`src/main/kotlin/array/prefixsum/Minimum NumberofOperationstoMoveAllBallstoEachBox.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/Minimum%20NumberofOperationstoMoveAllBallstoEachBox.kt)
> **Pattern**: two-pass running cost · **Core page**

## The Problem

For each box, the total moves to bring every ball to it.

- Constraints: n ≤ 2000.

## Examples

```
Input:  boxes = "110"   -> Output: [1,1,3]
```

## Intuition — accumulate costs from the left, then the right

A left-to-right pass computes the cost of balls *to the left*; the mirrored pass adds the right side:

```kotlin
val n = boxes.length
val result = IntArray(n) { 0 }

var (leftMoves, leftCount) = 0 to 0
for (i in 0 until n) {
    result[i] += leftMoves
    if (boxes[i] == '1') leftCount++
    leftMoves += leftCount
}

var (rightMoves, rightCount) = 0 to 0
for (i in n - 1 downTo 0) {
    result[i] += rightMoves
    if (boxes[i] == '1') rightCount++
    rightMoves += rightCount
}
return result
```

## Approach 1 — Two-pass running cost (the repo's version, optimal)

```kotlin
class MinimumNumberofOperationstoMoveAllBallstoEachBox {
    /**
     * @param boxes binary string
     * @return      moves per box
     */
    fun minOperations(boxes: String): IntArray {
        val n = boxes.length
        val result = IntArray(n) { 0 }

        var (leftMoves, leftCount) = 0 to 0
        for (i in 0 until n) {
            result[i] += leftMoves
            if (boxes[i] == '1') leftCount++
            leftMoves += leftCount
        }

        var (rightMoves, rightCount) = 0 to 0
        for (i in n - 1 downTo 0) {
            result[i] += rightMoves
            if (boxes[i] == '1') rightCount++
            rightMoves += rightCount
        }
        return result
    }
}
```

```java
public class MinimumOperationsToMoveAllBalls {
    /**
     * @param boxes binary string
     * @return      moves per box
     */
    public int[] minOperations(String boxes) {
        int n = boxes.length();
        int[] result = new int[n];

        int moves = 0, count = 0;
        for (int i = 0; i < n; i++) {
            result[i] += moves;
            if (boxes.charAt(i) == '1') count++;
            moves += count;
        }

        moves = 0;
        count = 0;
        for (int i = n - 1; i >= 0; i--) {
            result[i] += moves;
            if (boxes.charAt(i) == '1') count++;
            moves += count;
        }
        return result;
    }
}
```

```cpp
#include <string>
#include <vector>

class MinimumOperationsToMoveAllBalls {
public:
    /**
     * @param boxes binary string
     * @return      moves per box
     */
    std::vector<int> minOperations(std::string boxes) {
        int n = boxes.size();
        std::vector<int> result(n, 0);

        int moves = 0, count = 0;
        for (int i = 0; i < n; i++) {
            result[i] += moves;
            if (boxes[i] == '1') count++;
            moves += count;
        }

        moves = 0;
        count = 0;
        for (int i = n - 1; i >= 0; i--) {
            result[i] += moves;
            if (boxes[i] == '1') count++;
            moves += count;
        }
        return result;
    }
};
```

```python
def min_operations(boxes: str) -> list[int]:
    """
    @param boxes: binary string
    @return:      moves per box
    """
    n = len(boxes)
    result = [0] * n

    moves = count = 0
    for i in range(n):
        result[i] += moves
        if boxes[i] == "1":
            count += 1
        moves += count

    moves = count = 0
    for i in range(n - 1, -1, -1):
        result[i] += moves
        if boxes[i] == "1":
            count += 1
        moves += count

    return result
```

```rust
impl Solution {
    /// @param boxes binary string
    /// @return      moves per box
    pub fn min_operations(boxes: String) -> Vec<i32> {
        let bytes: Vec<char> = boxes.chars().collect();
        let n = bytes.len();
        let mut result = vec![0; n];

        let (mut moves, mut count) = (0, 0);
        for i in 0..n {
            result[i] += moves;
            if bytes[i] == '1' { count += 1; }
            moves += count;
        }

        let (mut moves, mut count) = (0, 0);
        for i in (0..n).rev() {
            result[i] += moves;
            if bytes[i] == '1' { count += 1; }
            moves += count;
        }
        result
    }
}
```

## Dry run

**Input:** `boxes = "110"`.

```
left pass: i=0: +0.  count 1.  moves 1.  i=1: +1.  count 2.  moves 3.  i=2: +3.  count 2.
  result [0,1,3].
right pass: i=2: +0.  count 0? boxes[2]='0'.  moves 0.  i=1: +0.  count 1.  moves 1.  i=0: +1.
  result [1,1,3].  Output: [1,1,3] ✓
```

## Complexity

**Time.** Two passes:

$$
T(n) = O(n)
$$

**Space.** The result:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does `moves += count` accumulate correctly?" Moving right, each new left-ball adds one move per step for *every* subsequent box — the running count's increment is the total added distance.
