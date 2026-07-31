# 9.7 Validate IP Address

> **Source:** [`src/main/kotlin/string/ValidateIPAddress.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/string/ValidateIPAddress.kt)
> **Pattern:** segment validation · **Core page**

## The Problem

Given a string `queryIP`, return `"IPv4"`, `"IPv6"`, or `"Neither"` depending on which IP format it is (if any).

- Constraints: `queryIP` consists of English letters, digits, and `'.'` / `':'`; length up to 100.

## Examples

```
Input:  "172.16.254.1"              -> "IPv4"
Input:  "2001:0db8:85a3:0:0:8A2E:0370:7334" -> "IPv6"
Input:  "256.256.256.256"           -> "Neither"   (256 > 255)
Input:  "01.01.01.01"               -> "Neither"   (leading zeros)
Input:  "1e1.4.5.6"                 -> "Neither"   (not all digits)
```

## Intuition — two grammars, each with a list of traps

Validation problems are really **grammar checks**: split into segments, and verify every segment against the rules. The skill is not the algorithm — it's *enumerating the traps* without being asked. For IPv4:

1. exactly **4** segments, split on `'.'`;
2. each segment **non-empty** (a leading/trailing dot creates an empty segment);
3. **all digits** (no `"1e1"`);
4. **no leading zeros** (length 1, or first char != `'0'`); 
5. **value ≤ 255** — and the value must fit in an `Int` first (a 10-digit segment overflows; the repo catches `NumberFormatException`).

For IPv6:

1. exactly **8** segments, split on `':'`;
2. each segment **1–4 characters**;
3. each character a **hex digit** — digit, or `a`–`f` (case-insensitive).

**Why check "starts/ends with separator" explicitly?** `"1.2.3.4."` splits into `["1","2","3","4",""]` — the empty last segment *is* caught by the non-empty check, so the explicit `startsWith/endsWith` guard is redundant there — but for IPv6 the `char in 'a'..'f'` check on an empty segment would pass vacuously, so the repo guards both. (This is the kind of "which split behavior bites which format" reasoning interviewers probe.)

**The order of checks matters:** cheap checks (count, empty, digit-ness) run before expensive/fallible ones (numeric value). The repo's `all { }` chain evaluates left to right, so a bad segment fails early.

## Approach 1 — Regex (compact, but write-only)

`^((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)\.){3}(...)$` — correct, but an interview answer that reads like incantation. The segment checker below states every rule as a line.

## Approach 2 — Split and validate segments (the repo's version, optimal)

```kotlin
class ValidateIPAddress {
    /**
     * @param queryIP candidate IP string
     * @return        "IPv4", "IPv6", or "Neither"
     */
    fun validIPAddress(queryIP: String): String {
        return when {
            isValidIPv4(queryIP) -> "IPv4"
            isValidIPv6(queryIP) -> "IPv6"
            else -> "Neither"
        }
    }

    private fun isValidIPv4(ip: String): Boolean {
        if (ip.startsWith('.') || ip.endsWith('.')) return false

        val segments = ip.split('.')
        if (segments.size != 4) return false

        return segments.all { segment ->
            segment.isNotEmpty() &&                      // no empty segments
                segment.all { it.isDigit() } &&          // digits only
                (segment.length == 1 || segment[0] != '0') &&   // no leading zeros
                segment.length <= 3 &&                   // at most 3 digits
                try {
                    segment.toInt() in 0..255            // value range (also catches overflow)
                } catch (e: NumberFormatException) {
                    false
                }
        }
    }

    private fun isValidIPv6(ip: String): Boolean {
        if (ip.startsWith(':') || ip.endsWith(':')) return false

        val segments = ip.split(':')
        if (segments.size != 8) return false

        return segments.all { segment ->
            segment.length in 1..4 &&                    // 1-4 chars per group
                segment.all { char ->
                    char.isDigit() || char.lowercaseChar() in 'a'..'f'   // hex digits
                }
        }
    }
}
```

```java
public class ValidateIPAddress {
    /**
     * @param queryIP candidate IP string
     * @return        "IPv4", "IPv6", or "Neither"
     */
    public String validIPAddress(String queryIP) {
        if (isIPv4(queryIP)) return "IPv4";
        if (isIPv6(queryIP)) return "IPv6";
        return "Neither";
    }

    private boolean isIPv4(String ip) {
        String[] parts = ip.split("\\.", -1);          // -1 keeps trailing empty segments
        if (parts.length != 4) return false;

        for (String p : parts) {
            if (p.isEmpty() || p.length() > 3) return false;
            if (p.length() > 1 && p.charAt(0) == '0') return false;   // no leading zeros
            for (char c : p.toCharArray()) if (!Character.isDigit(c)) return false;
            try {
                if (Integer.parseInt(p) > 255) return false;
            } catch (NumberFormatException e) {
                return false;                          // overflow (very long segment)
            }
        }
        return true;
    }

    private boolean isIPv6(String ip) {
        String[] parts = ip.split(":", -1);
        if (parts.length != 8) return false;

        for (String p : parts) {
            if (p.length() < 1 || p.length() > 4) return false;
            for (char c : p.toLowerCase().toCharArray()) {
                boolean hex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f');
                if (!hex) return false;
            }
        }
        return true;
    }
}
```

```cpp
#include <cctype>
#include <string>
#include <vector>

class ValidateIPAddress {
    bool isIPv4(const std::string& ip) {
        std::vector<std::string> parts;
        std::string cur;
        for (char c : ip) {
            if (c == '.') { parts.push_back(cur); cur.clear(); }
            else cur += c;
        }
        parts.push_back(cur);
        if (parts.size() != 4) return false;

        for (auto& p : parts) {
            if (p.empty() || p.size() > 3) return false;
            if (p.size() > 1 && p[0] == '0') return false;        // no leading zeros
            int value = 0;
            for (char c : p) {
                if (!std::isdigit(c)) return false;
                value = value * 10 + (c - '0');
            }
            if (value > 255) return false;
        }
        return true;
    }

    bool isIPv6(const std::string& ip) {
        std::vector<std::string> parts;
        std::string cur;
        for (char c : ip) {
            if (c == ':') { parts.push_back(cur); cur.clear(); }
            else cur += c;
        }
        parts.push_back(cur);
        if (parts.size() != 8) return false;

        for (auto& p : parts) {
            if (p.empty() || p.size() > 4) return false;
            for (char c : p) {
                bool hex = std::isdigit(c) || (std::tolower(c) >= 'a' && std::tolower(c) <= 'f');
                if (!hex) return false;
            }
        }
        return true;
    }

public:
    /**
     * @param queryIP candidate IP string
     * @return        "IPv4", "IPv6", or "Neither"
     */
    std::string validIPAddress(std::string queryIP) {
        if (isIPv4(queryIP)) return "IPv4";
        if (isIPv6(queryIP)) return "IPv6";
        return "Neither";
    }
};
```

```python
def valid_ip_address(query_ip: str) -> str:
    """
    @param query_ip: candidate IP string
    @return:         "IPv4", "IPv6", or "Neither"
    """
    def is_ipv4(ip: str) -> bool:
        parts = ip.split(".")
        if len(parts) != 4:
            return False
        for p in parts:
            if not p or not p.isdigit() or (len(p) > 1 and p[0] == "0"):
                return False
            if int(p) > 255:                 # int() also handles absurd lengths
                return False
        return True

    def is_ipv6(ip: str) -> bool:
        parts = ip.split(":")
        if len(parts) != 8:
            return False
        for p in parts:
            if not (1 <= len(p) <= 4):
                return False
            if not all(c.isdigit() or c.lower() in "abcdef" for c in p):
                return False
        return True

    if is_ipv4(query_ip):
        return "IPv4"
    if is_ipv6(query_ip):
        return "IPv6"
    return "Neither"
```

```rust
impl Solution {
    /// @param query_ip candidate IP string
    /// @return         "IPv4", "IPv6", or "Neither"
    pub fn valid_ip_address(query_ip: String) -> String {
        fn is_ipv4(ip: &str) -> bool {
            let parts: Vec<&str> = ip.split('.').collect();
            if parts.len() != 4 { return false; }
            for p in parts {
                if p.is_empty() || p.len() > 3 { return false; }
                if p.len() > 1 && p.starts_with('0') { return false; }   // no leading zeros
                if !p.bytes().all(|b| b.is_ascii_digit()) { return false; }
                if p.parse::<i32>().map_or(true, |v| v > 255) { return false; }
            }
            true
        }

        fn is_ipv6(ip: &str) -> bool {
            let parts: Vec<&str> = ip.split(':').collect();
            if parts.len() != 8 { return false; }
            for p in parts {
                if p.is_empty() || p.len() > 4 { return false; }
                if !p.bytes().all(|b| b.is_ascii_hexdigit()) { return false; }
            }
            true
        }

        if is_ipv4(&query_ip) { "IPv4".to_string() }
        else if is_ipv6(&query_ip) { "IPv6".to_string() }
        else { "Neither".to_string() }
    }
}
```

## Dry run

**Input:** a spread of cases.

```
"172.16.254.1":  4 segments, all 1-3 digits, no leading zeros, all <= 255 -> IPv4 ✓
"2001:0db8:85a3:0:0:8A2E:0370:7334":  8 segments, each 1-4 hex chars (uppercase A-E fine) -> IPv6 ✓

"256.256.256.256":  segment "256" -> toInt = 256 > 255 -> fail -> Neither ✓
"01.01.01.01":      segment "01": length 2 and starts with '0' -> fail -> Neither ✓
"1e1.4.5.6":        segment "1e1": not all digits -> fail -> Neither ✓
"1.2.3.4.":         split -> ["1","2","3","4",""]: 5 segments -> fail -> Neither ✓
"2001:0db8:85a3::8A2E:0370:7334":  "::" -> empty segment -> fail -> Neither ✓
```

The two traps worth verbalizing: `"01.01.01.01"` passes a naive "numeric value" check (1 is in range!) — only the explicit leading-zero rule catches it — and `"256..."` passes the digit and length checks but fails the range. Each rule exists because some *other* rule passes it alone.

## Complexity

**Time.** Two splits, each segment scanned once:

$$
T(n) = O(n)
$$

**Space.** The segment list:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Validate IP Address (better implementation)** (`src/main/kotlin/string/ValidateIPAddressBetterImplementation.kt`) — the repo's alternative pass: single-segment scanners with explicit character walks instead of `split`, trading code length for zero allocation.
- **Valid Number** (`src/main/kotlin/string/ValidNumber.kt`) — the same grammar-checking muscle on numeric literals (signs, decimals, exponents) — a classic "enumerate the edge cases" problem.
- **Excel Sheet Column Number / Detect Capital** (`src/main/kotlin/string/ExcelSheetToColumnNumber.kt`) — character-to-value conversions with the same "each position must satisfy a rule" loop.
- **Interview follow-up:** "Why not just regex?" A regex states the grammar in one line but hides the failure reasons and is easy to get subtly wrong (leading zeros, empty segments, overflow). The segment checker is debuggable — each rule is a line you can point to — and every rule maps to a test case. Interviews reward the explicit version.
