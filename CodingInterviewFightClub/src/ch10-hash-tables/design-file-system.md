# 10.32 Design File System

> **Source**: [`src/main/kotlin/hashtable/DesignFileSystem.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/hashtable/DesignFileSystem.kt)
> **Pattern**: trie-lite path map · **Core page**

## The Problem

`createPath(path, value)` (parent must exist) and `get(path)`.

- Constraints: ≤ 10⁴ calls.

## Examples

```
["FileSystem","createPath","get","createPath","get"]
[[],["/a",1],["/a"],["/a/b",2],["/a/b"]]
-> [null,true,1,true,2]
```

## Intuition — a map of paths; creation needs the parent present

```kotlin
fun createPath(path: String, value: Int): Boolean {
    if (path in fileSystem || path == "/") return false

    val parentPath = path.substringBeforeLast("/")

    if (parentPath != "" && parentPath !in fileSystem) return false

    fileSystem[path] = value
    return true
}

fun get(path: String): Int = fileSystem[path] ?: -1
```

## Approach 1 — Path map (the repo's version, optimal)

```kotlin
class DesignFileSystem {
    private val fileSystem = mutableMapOf<String, Int>()

    /**
     * @param path  absolute path
     * @param value assigned value
     * @return      true if created
     */
    fun createPath(path: String, value: Int): Boolean {
        if (path in fileSystem || path == "/") return false

        val parentPath = path.substringBeforeLast("/")

        if (parentPath != "" && parentPath !in fileSystem) return false

        fileSystem[path] = value
        return true
    }

    /**
     * @param path absolute path
     * @return     value or -1
     */
    fun get(path: String): Int = fileSystem[path] ?: -1
}
```

```java
import java.util.*;

public class FileSystem {
    private final Map<String, Integer> map = new HashMap<>();

    /**
     * @param path  absolute path
     * @param value assigned value
     * @return      true if created
     */
    public boolean createPath(String path, int value) {
        if (map.containsKey(path) || path.equals("/")) return false;

        int lastSlash = path.lastIndexOf('/');
        String parent = path.substring(0, lastSlash);

        if (!parent.isEmpty() && !map.containsKey(parent)) return false;

        map.put(path, value);
        return true;
    }

    /**
     * @param path absolute path
     * @return     value or -1
     */
    public int get(String path) {
        return map.getOrDefault(path, -1);
    }
}
```

```cpp
#include <string>
#include <unordered_map>

class FileSystem {
    std::unordered_map<std::string, int> map;

public:
    /**
     * @param path  absolute path
     * @param value assigned value
     * @return      true if created
     */
    bool createPath(std::string path, int value) {
        if (map.count(path) || path == "/") return false;

        size_t pos = path.rfind('/');
        std::string parent = path.substr(0, pos);

        if (!parent.empty() && !map.count(parent)) return false;

        map[path] = value;
        return true;
    }

    /**
     * @param path absolute path
     * @return     value or -1
     */
    int get(std::string path) {
        auto it = map.find(path);
        return it == map.end() ? -1 : it->second;
    }
};
```

```python
class FileSystem:
    def __init__(self):
        self.map = {}

    def create_path(self, path: str, value: int) -> bool:
        if path in self.map or path == "/":
            return False

        parent = path.rsplit("/", 1)[0]
        if parent and parent not in self.map:
            return False

        self.map[path] = value
        return True

    def get(self, path: str) -> int:
        return self.map.get(path, -1)
```

```rust
use std::collections::HashMap;

struct FileSystem {
    map: HashMap<String, i32>,
}

impl FileSystem {
    fn new() -> Self { Self { map: HashMap::new() } }

    /// @param path  absolute path
    /// @param value assigned value
    /// @return      true if created
    fn create_path(&mut self, path: String, value: i32) -> bool {
        if self.map.contains_key(&path) || path == "/" { return false; }

        let parent = path.rsplit_once('/').map(|(p, _)| p.to_string()).unwrap_or_default();
        if !parent.is_empty() && !self.map.contains_key(&parent) { return false; }

        self.map.insert(path, value);
        true
    }

    /// @param path absolute path
    /// @return     value or -1
    fn get(&self, path: String) -> i32 {
        self.map.get(&path).copied().unwrap_or(-1)
    }
}
```

## Dry run

**Input:** the example.

```
create /a: parent "" -> ok.  get /a: 1.
create /a/b: parent /a exists -> ok.  get /a/b: 2 ✓
create /a: exists -> false ✓
```

## Complexity

**Time.** O(path length) per op:

$$
T = O(L)
$$

**Space.** The map:

$$
S = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why the parent check?" The problem's contract requires parent-first creation — the map lookup enforces it in O(1), unlike a real trie.
