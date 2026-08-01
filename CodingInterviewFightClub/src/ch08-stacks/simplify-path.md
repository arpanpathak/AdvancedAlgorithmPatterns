# 8.23 Simplify Path

> **Source**: [`src/main/kotlin/string/stack/SimplifyPath.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/stack/SimplifyPath.kt)
> **Pattern**: token-stack path resolution · **Core page**

## The Problem

Canonicalize an absolute Unix path (`..`, `.`, `//`).

- Constraints: n ≤ 3000.

## Examples

```
Input:  path = "/home//foo/"     -> Output: "/home/foo"
Input:  path = "/a/./b/../../c/" -> Output: "/c"
```

## Intuition — split on '/', push names, pop on '..'

```kotlin
val tokens = path.split("/")
val stack = mutableListOf<String>()

for (token in tokens) {
    when (token) {
        "", "." -> {}
        ".." -> if (stack.isNotEmpty()) stack.removeLast()
        else -> stack.add(token)
    }
}
return "/" + stack.joinToString("/")
```

## Approach 1 — Token-stack (the repo's version, optimal)

```kotlin
class SimplifyPath {
    /**
     * @param path absolute path
     * @return     canonical path
     */
    fun simplifyPath(path: String): String {
        val tokens = path.split("/")
        val stack = mutableListOf<String>()

        for (token in tokens) {
            when (token) {
                "", "." -> {}
                ".." -> if (stack.isNotEmpty()) stack.removeLast()
                else -> stack.add(token)
            }
        }
        return "/" + stack.joinToString("/")
    }
}
```

```java
import java.util.*;

public class SimplifyPath {
    /**
     * @param path absolute path
     * @return     canonical path
     */
    public String simplifyPath(String path) {
        Deque<String> stack = new ArrayDeque<>();

        for (String token : path.split("/")) {
            if (token.isEmpty() || token.equals(".")) continue;
            if (token.equals("..")) {
                if (!stack.isEmpty()) stack.pop();
            } else {
                stack.push(token);
            }
        }

        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) sb.append("/").append(stack.pollLast());
        return sb.length() == 0 ? "/" : sb.toString();
    }
}
```

```cpp
#include <string>
#include <sstream>
#include <vector>

class SimplifyPath {
public:
    /**
     * @param path absolute path
     * @return     canonical path
     */
    std::string simplifyPath(std::string path) {
        std::vector<std::string> stack;
        std::string token;
        std::stringstream ss(path);

        while (std::getline(ss, token, '/')) {
            if (token.empty() || token == ".") continue;

            if (token == "..") {
                if (!stack.empty()) stack.pop_back();
            } else {
                stack.push_back(token);
            }
        }

        std::string result;
        for (auto& s : stack) result += "/" + s;
        return result.empty() ? "/" : result;
    }
};
```

```python
def simplify_path(path: str) -> str:
    """
    @param path: absolute path
    @return:     canonical path
    """
    stack = []

    for token in path.split("/"):
        if token in ("", "."):
            continue
        if token == "..":
            if stack:
                stack.pop()
        else:
            stack.append(token)

    return "/" + "/".join(stack)
```

```rust
impl Solution {
    /// @param path absolute path
    /// @return     canonical path
    pub fn simplify_path(path: String) -> String {
        let mut stack: Vec<&str> = Vec::new();

        for token in path.split('/') {
            match token {
                "" | "." => {}
                ".." => { stack.pop(); }
                _ => stack.push(token),
            }
        }

        let result = stack.join("/");
        if result.is_empty() { "/".to_string() } else { format!("/{}", result) }
    }
}
```

## Dry run

**Input:** `path = "/a/./b/../../c/"`.

```
tokens: "", a, ., b, .., .., c, "".
stack: [a] -> [a,b] -> pop -> [] -> [c].
Output: "/c" ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The stack:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does `..` pop the last segment?" `..` cancels the *immediately preceding* directory — the LIFO stack is exactly the path's segment history.
