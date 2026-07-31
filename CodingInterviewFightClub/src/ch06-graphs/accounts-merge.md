# 6.15 Accounts Merge

> **Source:** [`src/main/kotlin/disjointset/AccountMerge.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/disjointset/AccountMerge.kt)
> **Pattern:** Union-Find over emails · **Core page**

## The Problem

Given accounts `[name, email1, email2, ...]`, merge accounts sharing an email. Return `[name, sorted unique emails]` per merged account.

- Constraints: accounts ≤ 1000; emails ≤ 10 per account.

## Examples

```
Input:  accounts = [["John","johnsmith@mail.com","john_newyork@mail.com"],
                    ["John","johnsmith@mail.com","john00@mail.com"],
                    ["Mary","mary@mail.com"],
                    ["John","johnnybravo@mail.com"]]
Output: [["John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"],
         ["Mary","mary@mail.com"],
         ["John","johnnybravo@mail.com"]]
```

## Intuition — emails are the nodes; accounts union them

Two accounts belong together iff they *share an email*. So: **each email is a Union-Find node; every email of an account is unioned with the account's first email.** Then group emails by their root:

```
emailToName = {}          # email -> owner name (first seen)
uf = UnionFind()

for (name, *emails) in accounts:
    for email in emails:
        emailToName[email] = name
        uf.union(emails[0], email)     # link all of this account's emails

groups = {}               # root -> sorted emails
for email in emailToName:
    groups[uf.find(email)].append(email)

return [[emailToName[root], *sorted(emails)] for root, emails in groups]
```

**Why union with the account's *first* email?** It's the account's representative — every email in the account gets connected through it, so all accounts sharing any email converge to the same root. The [6.6](min-cost-to-connect-all-points.md) Union-Find engine, nodes = emails.

**Why the owner name from the first sighting?** The problem guarantees the same name per merged account; storing it when first seen avoids the `union`-by-name dance. `find(email)` returns the representative email; its stored name is the account's.

## Approach 1 — BFS/DFS over an email graph (adjacency + traversal)

Build email→email edges, traverse components: correct, more machinery than needed.

## Approach 2 — Union-Find over emails (the repo's version, optimal)

```kotlin
class AccountMerge {
    class UnionFind<T> {
        data class Node<T>(var parent: T, var rank: Int)

        private val nodes = mutableMapOf<T, Node<T>>()

        fun add(x: T) {
            nodes.putIfAbsent(x, Node(x, 0))
        }

        fun find(x: T): T {
            val node = nodes[x] ?: throw IllegalAccessException("Value $x not found")
            if (node.parent != x) {
                node.parent = find(node.parent)          // path compression
            }
            return node.parent
        }

        fun union(x: T, y: T) {
            val rootX = find(x)
            val rootY = find(y)
            if (rootX != rootY) {
                val nodeX = nodes[rootX]!!
                val nodeY = nodes[rootY]!!
                when {
                    nodeX.rank > nodeY.rank -> nodeY.parent = rootX   // union by rank
                    nodeX.rank < nodeY.rank -> nodeX.parent = rootY
                    else -> {
                        nodeY.parent = rootX
                        nodeX.rank++
                    }
                }
            }
        }
    }

    /**
     * @param accounts [name, email...] lists
     * @return        merged accounts with sorted unique emails
     */
    fun accountsMerge(accounts: List<List<String>>): List<List<String>> {
        val emailToName = mutableMapOf<String, String>()
        val uf = UnionFind<String>()

        accounts.forEach { account ->
            val name = account[0]
            val firstEmail = account[1]

            account.drop(1).forEach { email ->
                emailToName[email] = name
                uf.add(firstEmail)
                uf.add(email)
                uf.union(firstEmail, email)      // link this account's emails
            }
        }

        val groups = mutableMapOf<String, MutableList<String>>()
        emailToName.keys.forEach { email ->
            groups.getOrPut(uf.find(email)) { mutableListOf() }.add(email)
        }

        return groups.values.map { emails ->
            listOf(emailToName[emails[0]]!!) + emails.sorted()
        }
    }
}
```

```java
import java.util.*;

public class AccountsMerge {
    private int[] parent, rank;

    private int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
        return parent[x];
    }

    private void union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return;
        if (rank[rx] < rank[ry]) parent[rx] = ry;          // union by rank
        else if (rank[rx] > rank[ry]) parent[ry] = rx;
        else { parent[ry] = rx; rank[rx]++; }
    }

    /**
     * @param accounts [name, email...] lists
     * @return        merged accounts with sorted unique emails
     */
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        Map<String, Integer> emailToId = new HashMap<>();
        Map<String, String> emailToName = new HashMap<>();
        parent = new int[10001];
        rank = new int[10001];
        for (int i = 0; i < 10001; i++) parent[i] = i;

        int id = 0;
        for (List<String> account : accounts) {
            String name = account.get(0);
            String first = account.get(1);

            for (int i = 1; i < account.size(); i++) {
                String email = account.get(i);
                emailToName.put(email, name);
                if (!emailToId.containsKey(email)) emailToId.put(email, id++);
                union(emailToId.get(first), emailToId.get(email));
            }
        }

        Map<Integer, List<String>> groups = new HashMap<>();
        for (String email : emailToName.keySet()) {
            int root = find(emailToId.get(email));
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(email);
        }

        List<List<String>> result = new ArrayList<>();
        for (List<String> emails : groups.values()) {
            Collections.sort(emails);
            List<String> merged = new ArrayList<>();
            merged.add(emailToName.get(emails.get(0)));
            merged.addAll(emails);
            result.add(merged);
        }
        return result;
    }
}
```

```cpp
#include <string>
#include <unordered_map>
#include <vector>
#include <algorithm>

class AccountsMerge {
    std::vector<int> parent, rank;

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
        return parent[x];
    }

    void unite(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return;
        if (rank[rx] < rank[ry]) parent[rx] = ry;
        else if (rank[rx] > rank[ry]) parent[ry] = rx;
        else { parent[ry] = rx; rank[rx]++; }
    }

public:
    /**
     * @param accounts [name, email...] lists
     * @return        merged accounts with sorted unique emails
     */
    std::vector<std::vector<std::string>> accountsMerge(
            std::vector<std::vector<std::string>>& accounts) {
        parent.resize(10001);
        rank.resize(10001, 0);
        for (int i = 0; i < 10001; i++) parent[i] = i;

        std::unordered_map<std::string, int> emailToId;
        std::unordered_map<std::string, std::string> emailToName;
        int id = 0;

        for (auto& account : accounts) {
            for (int i = 1; i < (int)account.size(); i++) {
                emailToName[account[i]] = account[0];
                if (!emailToId.count(account[i])) emailToId[account[i]] = id++;
                unite(emailToId[account[1]], emailToId[account[i]]);
            }
        }

        std::unordered_map<int, std::vector<std::string>> groups;
        for (auto& [email, _] : emailToName) {
            groups[find(emailToId[email])].push_back(email);
        }

        std::vector<std::vector<std::string>> result;
        for (auto& [_, emails] : groups) {
            std::sort(emails.begin(), emails.end());
            emails.insert(emails.begin(), emailToName[emails[0]]);
            result.push_back(emails);
        }
        return result;
    }
};
```

```python
class UnionFind:
    def __init__(self):
        self.parent = {}
        self.rank = {}

    def add(self, x):
        if x not in self.parent:
            self.parent[x] = x
            self.rank[x] = 0

    def find(self, x):
        if self.parent[x] != x:
            self.parent[x] = self.find(self.parent[x])   # path compression
        return self.parent[x]

    def union(self, x, y):
        rx, ry = self.find(x), self.find(y)
        if rx == ry:
            return
        if self.rank[rx] < self.rank[ry]:
            self.parent[rx] = ry
        elif self.rank[rx] > self.rank[ry]:
            self.parent[ry] = rx
        else:
            self.parent[ry] = rx
            self.rank[rx] += 1


def accounts_merge(accounts: list[list[str]]) -> list[list[str]]:
    """
    @param accounts: [name, email...] lists
    @return:        merged accounts with sorted unique emails
    """
    uf = UnionFind()
    email_to_name = {}

    for name, *emails in accounts:
        for email in emails:
            email_to_name[email] = name
            uf.add(email)
            uf.union(emails[0], email)      # link this account's emails

    groups = {}
    for email in email_to_name:
        groups.setdefault(uf.find(email), []).append(email)

    return [[email_to_name[emails[0]], *sorted(emails)] for emails in groups.values()]
```

```rust
use std::collections::HashMap;

struct UnionFind {
    parent: HashMap<String, String>,
    rank: HashMap<String, usize>,
}

impl UnionFind {
    fn new() -> Self { Self { parent: HashMap::new(), rank: HashMap::new() } }

    fn add(&mut self, x: String) {
        self.parent.entry(x.clone()).or_insert(x);
        self.rank.entry(x).or_insert(0);
    }

    fn find(&mut self, x: String) -> String {
        let p = self.parent.get(&x).unwrap().clone();
        if p != x {
            let root = self.find(p);
            self.parent.insert(x, root.clone());
            root
        } else {
            x
        }
    }

    fn union(&mut self, x: String, y: String) {
        let rx = self.find(x);
        let ry = self.find(y);
        if rx == ry { return; }
        let (rrx, rry) = (self.rank[&rx], self.rank[&ry]);
        if rrx < rry { self.parent.insert(rx, ry); }
        else if rrx > rry { self.parent.insert(ry, rx); }
        else { self.parent.insert(ry, rx.clone()); self.rank.insert(rx, rrx + 1); }
    }
}

impl Solution {
    /// @param accounts [name, email...] lists
    /// @return        merged accounts with sorted unique emails
    pub fn accounts_merge(accounts: Vec<Vec<String>>) -> Vec<Vec<String>> {
        let mut uf = UnionFind::new();
        let mut email_to_name: HashMap<String, String> = HashMap::new();

        for account in &accounts {
            let name = account[0].clone();
            for email in account.iter().skip(1) {
                email_to_name.insert(email.clone(), name.clone());
                uf.add(email.clone());
                uf.union(account[1].clone(), email.clone());
            }
        }

        let mut groups: HashMap<String, Vec<String>> = HashMap::new();
        for email in email_to_name.keys() {
            groups.entry(uf.find(email.clone())).or_default().push(email.clone());
        }

        let mut result = Vec::new();
        for mut emails in groups.into_values() {
            emails.sort();
            let name = email_to_name[&emails[0]].clone();
            let mut merged = vec![name];
            merged.append(&mut emails);
            result.push(merged);
        }
        result
    }
}
```

## Dry run

**Input:** the 4-account example above.

```
Union-Find over emails (each account links its emails through its first):
acc0: johnsmith@mail.com — john_newyork@mail.com   (root: johnsmith@mail.com)
acc1: johnsmith@mail.com — john00@mail.com         (johnsmith already a root)
acc2: mary@mail.com alone
acc3: johnnybravo@mail.com alone

Components: {johnsmith, john_newyork, john00}, {mary}, {johnnybravo}

Groups by find(): johnsmith@mail.com -> [john_newyork, johnsmith, john00] -> sorted
Output: [["John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"],
         ["Mary","mary@mail.com"],
         ["John","johnnybravo@mail.com"]] ✓
```

The union through the shared email is the merge: acc0 and acc1 converge because both connect to `johnsmith@mail.com` — Union-Find's transitive closure does the "sharing an email means same account" logic automatically. The rank + path-compression keep it near-O(1) per op.

## Complexity

**Time.** Union-Find near-linear:

$$
T(E, \alpha) = O(E \cdot \alpha(E))
$$

**Space.** Maps for parent/name/groups:

$$
S(E) = O(E)
$$

## Variants & follow-ups

- **Redundant Connection** ([6.11](redundant-connection.md)) — the same Union-Find engine finding the cycle edge.
- **The Earliest Moment Everyone Became Friends** ([6.10](the-earliest-moment-everyone-became-friends.md)) — time-ordered unions.
- **Interview follow-up:** "Why are emails the nodes and not accounts?" The merge rule is "shares an email" — emails are the *edges' endpoints*; unioning them per account connects the whole account. Making accounts the nodes would need email→account edges and a second traversal — the email-node framing is the [6.6](min-cost-to-connect-all-points.md) "choose the natural node" lesson.
