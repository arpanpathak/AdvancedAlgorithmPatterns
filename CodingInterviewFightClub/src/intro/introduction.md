# Coding Interview Fight Club

> *Everything we do in the computer is an algorithm, and every algorithm leans on data structures. Mostly, these data structures are already implemented by smart open-source developers working at big tech companies — but the *reasoning* that picks the right one, and the *proof* that it runs fast enough, is yours alone. This book trains exactly that.*

This is a **from-scratch, multi-language guide** to the 660+ algorithm solutions living in this repository (`src/main/kotlin/`). It is not a list of answers. It is a **training camp**: for every problem you will find

- a precise **problem statement** and worked examples,
- the **intuition** — *why* a pattern works, not just *that* it works,
- **multiple approaches** — from the brutal brute force to the elegant optimum,
- the same solution in **five languages** (Kotlin, Java, C++, Python, Rust), every method annotated with `@param` / `@return` so you can read the contract at a glance,
- a hand-traced **dry run** (tables, stack traces, recursion trees) so you can *watch* the algorithm execute,
- **time and space complexity** backed by real math — summations, recurrences, and the master theorem — not hand-waved "O(n log n) trust me".

## Why another interview book?

Because most books teach you *solutions*; interviews punish candidates who can only reproduce them. The difference between a pass and a fail is usually not *knowing the trick* — it is being able to **derive the trick under pressure** and **argue about its cost** without pausing. Every section here is written to be *derivable*: the intuition comes first, the code is a consequence of the intuition, and the complexity analysis is a proof you could deliver out loud in an interview room.

## How the repository maps to this book

| Book chapter | Source directory | Problems |
|---|---|---|
| 1. Binary Search | `src/main/kotlin/binarysearch/` | 22 |
| 2. Dynamic Programming | `src/main/kotlin/dynamic_programming/`, `array/dp/`, `graph/dp/` | … |
| 3. Arrays, Two Pointers & Sliding Window | `src/main/kotlin/array/`, `sliding_window/` | … |
| … | … | … |

Every chapter page names its source files, so you can jump from the book to the code and back.

## Conventions used throughout the book

- **Math** is rendered with MathJax: `$O(\log n)$` renders inline, `$$...$$` renders centered.
- **Code tabs**: adjacent code blocks are grouped into Kotlin / Java / C++ / Python / Rust tabs automatically. Click to switch; hover a block for a **Copy** button.
- **Dry runs** appear in monospace panels (`.dryrun`) so multi-column traces line up perfectly.
- **Source links**: every page carries an *edit on GitHub* link in the toolbar — typos and improvements are one click away.

Turn the page and start the fight.
