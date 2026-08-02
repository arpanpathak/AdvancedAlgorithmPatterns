# The Multi-Lingual Disciplinary Constitution
*(Merged: Standards + Anti-Patterns + Clean Code + SOLID + Cost Gaslighting)*

> **If you're impatient, skip this page. Life will humble you down.**

**Philosophy:** This is not a suggestion. It is a formal invariant system. Every language has its footguns; this document maps the minefield. LLMs are statistical parrots—this is their cage. Violations are not "style differences"; they are treason against mechanical sympathy **and** basic human readability.

---

## SECTION 0: THE UNIVERSAL TRINITY (Memory, Types, Control Flow)

### 0.1 Memory & Performance (All Languages)
- **Contiguity over Pointers:** `Vec<T>` (Rust), `Array/List` (Kotlin/Java), `T[]` (TS) over linked lists unless `perf` proves otherwise.
- **Lazy Evaluation:** Use `Sequence` (Kotlin), `Iterator` (Rust/JS), `itertools` (Python) for *lazy* pipelines. **Do not** materialize intermediate collections in memory.
- **Branch Prediction:** Hot paths must put the 90% case first in `if/else`. Annotate with `likely/unlikely` in C/Rust.

### 0.2 Type Safety & Null Handling
- **Null is a War Crime:** Kotlin: NEVER use `!!` (force-unwrap). Use `?.`, `?:`, `let`, and `run`. Rust: `Option<T>` and `?` operator. TS: strict `null` checks + `Either`/`Option` via fp-ts. Python: `Optional` with `mypy` strict.
- **Parsing:** Use nom (Rust), Arrow/Kotlinx.serialization, Zod (TS), Pydantic (Python). Regex alone is banned for untrusted input.

### 0.3 The "No Nested For-Loops" Commandment
If you have a nested loop (O(n²) or worse), you are **already wrong** unless you have a mathematical proof that n < 100. Replace with:

- **Kotlin:** `list.flatMap { ... }.groupBy { ... }.map { ... }`
- **Rust:** `iter().flat_map().fold(HashMap::new(), ...)`
- **TS:** `arr.flatMap().reduce()`
- **Python:** `itertools.chain.from_iterable()`

---

## SECTION 1: THE MECHANICS OF CLEAN NAMING & FUNCTION PURITY (Pre-SOLID)

Before you touch SOLID, master this: **Code is read 10x more than written.**

### 1.1 The Naming Haiku
- **Booleans:** Always start with `is`, `has`, `can`, `should`. (e.g., `isActive`, `hasPermission`). Never `flag` or `status`.
- **Functions:** Must be `verbNoun()` (e.g., `calculateTotal()`, `fetchUser()`). If it returns a boolean, use `is/has/can`.
- **Variables:** Nouns. Full words. `usr` is a sin. `user` is divine. Abbreviations are only allowed if they are domain-standard (e.g., `ID`, `UUID`, `HTTP`).
- **Magic Numbers:** If it's not 0, 1, or -1, it gets a `const` with a screaming snake case name (e.g., `MAX_RETRY_ATTEMPTS = 3`).

### 1.2 The 20-Line Rule (The Function Ceiling)
A function must fit entirely on a single screen without scrolling vertically. If it exceeds **20 lines** (excluding braces and whitespace), it is too long. **Refactor it.**
*How?* Extract inner blocks into well-named private extension functions or local lambdas. If you cannot name the extracted block, your original function was doing too many things.

### 1.3 Expression-Oriented Programming (Kill the Dangling Return)
Prefer expression bodies over statement blocks. Return implicitly.

- **Kotlin (Good):** `fun double(x: Int): Int = x * 2`
- **Rust (Good):** `fn double(x: i32) -> i32 { x * 2 }` (no semicolon = implicit return)
- **TS (Good):** `const double = (x: number): number => x * 2`
- **Python (Good):** `def double(x): return x * 2` (Python has no expression bodies, but keep it one line).

If you have 4 different `return` statements scattered inside an `if` jungle, you are doing it wrong. Use `when` (Kotlin), `match` (Rust/TS/Python) as an *expression* that returns a single value.

---

## SECTION 2: CLEAN, IDIOMATIC READABILITY & SOLID PRINCIPLES (MULTI-LINGUAL)

SOLID is not an abstract OOP buzzword. It is a **survival guide** against unmaintainable spaghetti. Here is how you enforce it practically across paradigms.

### 2.1 S - Single Responsibility (One Reason to Change)
A class, module, or function must have exactly **one** reason to exist.

- **Kotlin/Java:** Do not create a `UserManager` that handles DB, sends emails, and caches. Split into `UserRepository`, `EmailNotifier`, `UserCache`.
- **Rust:** Do not bloat a single `impl` block. Split logic into different traits (`UserFetcher`, `Notifier`) and implement them separately.
- **TS/Python:** If your class has more than 5 public methods, it's probably doing too much. Split into smaller composable classes or pure functions in separate modules.
- **The LLM Trap:** When an AI gives you a giant function, force it: *"Refactor this into 3 pure functions, each with a single responsibility."*

### 2.2 O - Open/Closed (Open for Extension, Closed for Modification)
You should be able to add new behavior without touching existing, working code.

- **Kotlin/Java:** Use `sealed classes` / `interfaces`. Instead of a giant `when` chain that breaks every time you add a type, define a method on the interface and call it polymorphically.
  - *BAD:* `when (type) { is Dog -> bark(); is Cat -> meow() }` (Modifies every time you add an animal).
  - *GOOD:* `animal.speak()` (Define `speak()` on the `Animal` interface. New animals just implement it).
- **Rust:** Use `trait` and `enum` with trait objects (`dyn Trait`) or enums with non-exhaustive patterns only at the boundary. Prefer adding new `impl` blocks over modifying existing `match` arms.
- **TS:** Use discriminated unions (`type Shape = Circle | Square`) but handle the `default` exhaustively. Or use interfaces and dependency injection.

### 2.3 L - Liskov Substitution (Subtypes Must Be Substitutable)
If you have a `Bird` class and a `Penguin` subclass, `Penguin` MUST be able to be used anywhere `Bird` is used without breaking behavior. If `Bird` has a `fly()` method, `Penguin` should NOT override it to throw `UnsupportedOperationException`.
**Fix:** Instead of inheritance, use composition. Or split the interface into `FlyingBird` and `NonFlyingBird`.
- **Kotlin/Rust/TS:** Favor `interface` / `trait` segregation over class inheritance. Composition over inheritance is the golden bullet.

### 2.4 I - Interface Segregation (Don't Force Dependencies)
Do not force a class to implement a method it doesn't need.

- *BAD:* An `Employee` interface with `calculateSalary()`, `generateReport()`, and `login()`. Your `Intern` class now has to fake `generateReport()`.
- *GOOD:* Split into `Payable`, `Reportable`, `Authenticatable`. Let the class implement only what it needs.
- **Rust/TS:** Create small, focused traits/interfaces. A type can implement multiple. This makes testing trivial (you just mock the tiny interface).

### 2.5 D - Dependency Inversion (Depend on Abstractions, Not Concretions)
High-level modules should not depend on low-level modules. Both should depend on abstractions.

- *BAD:* `class OrderService { private val db = MySQLDatabase() }` (Hardcoded dependency. You cannot unit test without a real DB).
- *GOOD:* `class OrderService(private val db: Database)` where `Database` is an interface/trait.
- **Multi-lingual:** In Kotlin/Rust/TS, pass the dependency via constructor (DI) or as a function parameter. In functional programming, pass the "effect" (the database function) as an argument to the pure function.
- **The Corollary:** Do not use `object` (Kotlin) or `static` (Java/TS) for external services. Singletons are global state in disguise and violate DI.

### 2.6 Clean Code: Tell, Don't Ask
Do not query an object's internal state to make a decision *for* it. Tell the object to do the work itself.
- *BAD:* `if (user.getRole() == "ADMIN") { user.deletePost(post) }`
- *GOOD:* `user.deletePost(post)` (The `User` object knows its own role and checks internally).

---

## SECTION 3: ASYNC & STRUCTURED CONCURRENCY (MULTI-LINGUAL)

### 3.1 The Golden Rule of Async
**Never** mix blocking and non-blocking code at the same stack frame. If you call a `suspend` function (Kotlin), `async` (Rust/JS), or `await` (Python), the entire call chain up to the boundary must be colored accordingly.

#### Kotlin (Coroutines)
- Use `runBlocking` **only** in `main()` or test fixtures. NEVER in a server endpoint.
- Use `withContext(Dispatchers.IO)` for blocking I/O, `Dispatchers.Default` for CPU.
- **Structured Concurrency:** Use `coroutineScope { ... }` to enforce that all child coroutines complete before the parent returns. If you use `GlobalScope`, you are fired.
- **Lazy Flow:** Use `flow { ... }.map { ... }.filter { ... }` – it's lazy and backpressured. DO NOT use `toList()` on an infinite flow.

```kotlin
// GOOD: Structured, lazy, functional.
suspend fun fetchAndProcess(ids: List<Int>) = withContext(Dispatchers.IO) {
    ids.asFlow() // Lazy
        .map { id -> fetchUser(id) } // Suspending call
        .filter { it.isActive }
        .map { it.name }
        .toList() // Materializes only at the very end
}

// BAD: GlobalScope leak and blocking on dispatcher.
GlobalScope.launch {
    runBlocking { fetchUser(1) } // Nested runBlocking = deadlock risk.
}
```

#### Rust (Tokio)
- Use `tokio::spawn` with an explicit `JoinHandle`. Never `block_on` inside an async function.
- Prefer `tokio::select!` over manual polling. Timeout every I/O call: `tokio::time::timeout(Duration::from_secs(3), fut).await`.
- **Lazy Iterators:** Use `.iter().map().collect::<Vec<_>>()` for bounded, or `.par_iter()` (Rayon) for parallel CPU.

#### TypeScript (Node.js)
- Never use `async void` functions unless you `.catch()` immediately. Unhandled rejections crash the process.
- Use `Promise.allSettled` over `Promise.all` if partial failures are acceptable.
- **Lazy Generators:** Use `function*` or `async function*` to stream data instead of buffering entire arrays.

#### Python (Asyncio)
- Use `asyncio.gather(*tasks, return_exceptions=True)` for fan-out.
- Never use `time.sleep()` – use `asyncio.sleep()`.
- **Lazy:** Use generator expressions `(x for x in range(10))` over list comprehensions `[...]` for large datasets.

---

## SECTION 4: THE NEGATIVE H₂S HALL OF SHAME (MULTI-LINGUAL)

### Antipattern 1: Kotlin's `!!` (The Null-Pointer Resurrector)
```kotlin
// DO NOT DO THIS. EVER.
val user = findUser(id)!! // Throws NPE if null. You just killed the whole point of Kotlin.
user.email!!.length // Double criminal offense.
```
**Fix:** `findUser(id)?.email?.length ?: 0` or `requireNotNull(user) { "User $id missing" }` with a meaningful message.

### Antipattern 2: The Blocking Coroutine (Async Antipattern)
```kotlin
// DO NOT DO THIS. EVER.
suspend fun getData(): String {
    return runBlocking { // runBlocking inside suspend = thread pinning and deadlock risk.
        httpClient.get("...")
    }
}
```
**Fix:** Just `suspend` all the way down. Use `withContext` to shift dispatchers, never block.

### Antipattern 3: The N+1 Query in Disguise (Functional but Naive)
```kotlin
// BAD: Despite using map, it's still N+1 because you call suspend inside a non-suspend lambda.
val users = userRepo.getAll() // List of 1000
val orders = users.map { user -> orderRepo.findByUserId(user.id) } // 1000 sequential DB calls.
```
**Fix:** Use `coroutineScope { users.map { async { orderRepo.findByUserId(it.id) } }.awaitAll() }` for parallel, or use a single SQL `IN` query.

### Antipattern 4: The God Object ORM (Eager Loading Hell) - All Languages
```csharp
// BAD: Lazy loading proxies in a web view.
foreach (var post in blogPosts) {
    foreach (var comment in post.Comments) { // Triggers a SELECT per post.
        // ...
    }
}
```
**Fix:** Explicit `.Include(x => x.Comments)` (C#), `@EntityGraph` (Java), `prisma.include({ comments: true })` (TS) – fail at compile-time if the data fetcher is missing.

### Antipattern 5: Mutable Shared State in Functional Pipelines
```python
# BAD: Accumulating state in a global inside a map.
counter = 0
def process(x):
    global counter
    counter += x # Mutates global state. Thread-unsafe. Violates functional purity.
    return x * 2
list(map(process, data))
```
**Fix:** `fold` / `reduce` for accumulation, or `itertools.accumulate`.

---

## SECTION 5: THE ASIAN DAD GASLIGHTING (COST / PERFORMANCE / WATTAGE)

**The Milk Carton Prologue:** In the 90s, I optimized 8086 assembly by manually counting clock cycles because we couldn't afford an ICE (In-Circuit Emulator). We wrote code on paper, under candlelight, in a monsoon. You have a 700W H100 generating 5,000 lines of Kotlin coroutines in 2 seconds. If it fails, you press "Regenerate". You are privileged.

**The Formal Cost Matrix (Stop Guessing):**

| Engine | Wattage | Lines/Min | Bugs/KLOC | **Iterations-to-Stable (I2S)** | Cost per Stable Function |
|--------|---------|-----------|-----------|--------------------------------|--------------------------|
| Human Senior | 100W (bio) | 5 | 0.5 | 1.0 | $120 (opp. cost) |
| GPT-4o | 500W | 2,000 | 15 | 4.7 | $0.048 |
| Claude 3.5 Sonnet | 700W | 2,500 | 8 | **2.1** | **$0.021** |
| Llama-3-70B (local) | 300W | 1,200 | 22 | 8.9 | $0.002 (elec only) |

**The Gaslight:** Llama is 10x cheaper per token, but requires 9 iterations vs Claude's 2. Total energy consumed: `300W * 8.9 = 2,670W` vs `700W * 2.1 = 1,470W`. Claude is **45% more power-efficient** for production-ready code. The "cheap" model costs you more in electricity, cloud overages (bad code needs more instances), and engineering review time.

**The Dad Speech:** You want to save $0.03 on inference? Go ahead. Use the cheap model. It will produce nested `for` loops and `!!` null-forcers. Your Kotlin microservice will OOM at 3AM because the functional pipeline materialized a 10-million-element list. Your CTO will get the AWS bill. You will explain to the board that you saved $3 on API calls but cost $4,000 in extra compute.

**Calculate this:** `TCG (Total Cost of Generation) = (Wattage * I2S * Time) + (EngineerSalary * Review_Hours)`. Always optimize for *lowest TCG*, not lowest token price. Now buy Claude credits and stop crying. Or go back to the milk cartons.

---

## SECTION 6: THE VERIFICATION CHECKLIST (FOR PRs & LLM OUTPUT)

Before you say "LGTM" or "Commit", formally verify these 6 properties. If the LLM generated it, force it to answer these in a comment block:

1. **Complexity Proof:** *"What is the worst-case time complexity? Prove it is not O(n²) due to hidden nested iteration."*
2. **Null-Safety Proof:** *"Trace the path of every nullable type. Show that `!!` or forced unwrap is absent."*
3. **Async Liveness:** *"If this coroutine/task panics, does the parent know? Is there a timeout on every I/O step?"*
4. **Lazy Termination:** *"If this uses a Sequence/Iterator/Generator, where is the terminal operator (`.toList()`, `.collect()`)? Is the stream bounded?"*
5. **SOLID Compliance:** *"Point to where the Dependency Inversion is. Where is the abstraction?"*
6. **Mechanical Sympathy:** *"How many cache misses does this hot loop cause? How many heap allocations per second?"*

**The Dunning-Kruger Escape Hatch:** If you cannot answer one of these, you do not understand the code. Do not approve it. Ask the LLM to rewrite it until you can draw the memory layout and the SOLID class diagram on a whiteboard. Formal verification and clean architecture do not care about your seniority, your feelings, or your sprint points.

---

## SECTION 7: THE MULTI-LINGUAL CHEATSHEET (QUICK REFERENCE)

| Language | Null Safety | Async | Lazy Collection | SOLID Enforcer | **Avoid This** |
|----------|-------------|-------|-----------------|----------------|----------------|
| **Kotlin** | `?.`, `?:`, `requireNotNull()` | `suspend` + `flow` | `Sequence<T>` | `sealed class` / `interface` | `!!` and `runBlocking` in libs |
| **Rust** | `Option<T>`, `?` | `async` + `tokio` | `Iterator` | `trait` + `impl` | `unwrap()` without `.expect()` |
| **TS** | `strictNullChecks` + `??` | `Promise` + `async/await` | `function*` | `interface` / `type` | `any` and `!` non-null assertion |
| **Python** | `Optional[T]` + `mypy` | `asyncio` + `await` | Generator `(x for x in...)` | `Protocol` (ABC) | Mutable defaults `def f(l=[])` |
| **Java** | `Optional<T>` | `CompletableFuture` / `Reactor` | `Stream<T>` (lazy) | `interface` | `null` returns and `synchronized` in virtual threads |

---

**Final Commandment:** This single file is your pact with the machine. Every time you open a new LLM chat, paste this as the **System Prompt**. When the AI outputs code, ask it: *"Check your output against Sections 1, 2, 3, and 4. Cite the line numbers where you violate them."*

If it cannot self-correct, close the chat and start over. Waste tokens, not your future sanity. Now go, and may your pipelines be lazy, your interfaces segregated, your coroutines structured, and your nulls forever absent.
