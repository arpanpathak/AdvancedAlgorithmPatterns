# Big-O, Complexity & The Math Behind It

Every runtime claim in this book reduces to a handful of mathematical facts. Learn these once and complexity analysis stops being guesswork.

## 1. What Big-O actually is

We say $f(n) = O(g(n))$ if there exist constants $c > 0$ and $n_0 \ge 0$ such that for all $n \ge n_0$:

$$
f(n) \le c \cdot g(n)
$$

Intuitively: *beyond some input size, $f$ never grows faster than $g$ (up to a constant).* Constants and lower-order terms are invisible to Big-O — that is a feature, not a bug: it isolates the **growth rate**, which is what survives a scale-up from your laptop to Google's fleet.

The sibling notations you will meet:

- $\Omega(g)$ — lower bound ($f$ grows *at least* as fast as $g$),
- $\Theta(g)$ — tight bound (both hold),
- $o(g)$ — strictly slower growth.

**The dominant term rule.** When a function is a sum of terms, only the fastest-growing term matters:

$$
5n^3 + 42n^2 + 7n + 100 = \Theta(n^3)
$$

**The base of a logarithm doesn't matter (in Big-O).** $\log_2 n = \frac{\log_{10} n}{\log_{10} 2}$, and the factor $1/\log_{10}2$ is a constant, so $\log_2 n = \Theta(\log_{10} n)$. That is why we write plain $\log n$.

## 2. The exponential-logarithmic duality you must internalize

Binary search works because the exponential function and the logarithm are inverses:

- An exponential *doubling* process (each step halves the search space) takes $\log_2 n$ steps to shrink $n$ to 1.
- A linear scan takes $n$ steps to do the same.

**Concretely:** $\log_2(10^6) \approx 20$, $\log_2(10^9) \approx 30$. If $n = 10^9$ and your algorithm is $O(\log n)$, you perform ~30 operations. If it is $O(n)$, you perform a billion. *This* is the entire difference binary search makes — 30 versus 1,000,000,000.

The key identity, used constantly in this book's proofs:

$$
\log_2 n = k \iff 2^k = n
$$

So halving an array of size $n$ exactly $k$ times reaches size $n / 2^k$, which equals 1 when $k = \log_2 n$.

## 3. Summations: the three you'll actually use

**Arithmetic series** (why nested loops over $i < j$ cost $O(n^2)$):

$$
1 + 2 + 3 + \cdots + n = \frac{n(n+1)}{2} = \Theta(n^2)
$$

*Proof sketch:* pair $1$ with $n$, $2$ with $n-1$, ... each pair sums to $n+1$, and there are $n/2$ pairs.

**Geometric series** (why doubling/halving sums to a constant factor):

$$
1 + 2 + 4 + \cdots + 2^k = 2^{k+1} - 1
$$

For $|r| < 1$: $\displaystyle \sum_{i=0}^{\infty} r^i = \frac{1}{1 - r}$. This is why a single "while" loop that halves something still only costs $O(\log n)$ even though you revisit it: $n + n/2 + n/4 + \cdots = 2n = O(n)$.

**Harmonic series** (why "for each divisor" loops cost $O(n \log n)$):

$$
1 + \frac12 + \frac13 + \cdots + \frac1n \le 1 + \ln n = O(\log n)
$$

## 4. Recurrences and the Master Theorem

Recursive algorithms are analyzed via recurrences. The workhorse is the **Master Theorem**. For

$$
T(n) = a\,T\!\left(\frac{n}{b}\right) + f(n)
$$

with $a \ge 1$, $b > 1$, compare $f(n)$ against $n^{\log_b a}$:

| Case | Condition | Solution |
|---|---|---|
| 1 | $f(n) = O(n^{\log_b a - \varepsilon})$ | $T(n) = \Theta(n^{\log_b a})$ |
| 2 | $f(n) = \Theta(n^{\log_b a})$ | $T(n) = \Theta(n^{\log_b a} \log n)$ |
| 3 | $f(n) = \Omega(n^{\log_b a + \varepsilon})$ and $a f(n/b) \le c f(n)$ | $T(n) = \Theta(f(n))$ |

**Worked examples from this book:**

- **Binary search:** $T(n) = T(n/2) + O(1)$. Here $a=1, b=2$, so $n^{\log_2 1} = n^0 = 1$, and $f(n) = 1 = \Theta(n^0)$ → **Case 2**: $T(n) = \Theta(\log n)$.
- **Merge sort / "binary search + linear feasibility"** (Koko-style problems): $T(n) = T(n/2) + O(n)$ → $a=1$, $n^{\log_2 1}=1$, $f(n) = n$ is polynomially larger → **Case 3**: $T(n) = \Theta(n)$. Wait — the *feasibility check* is $O(n)$ and binary search runs it $\log R$ times, so total is $O(n \log R)$; the Master Theorem here describes the *recursive structure*, which for Koko is iterative, not recursive — see §6.
- **Closest Subsequence Sum** (Chapter 1.22): splitting in half gives $T(n) = 2T(n/2) + O(n)$ → $a=2$, $n^{\log_2 2} = n$, $f(n) = n$ → **Case 2**: $T(n) = \Theta(n \log n)$. But the DFS subset enumeration dominates: $2^{n/2}$ per half. Real bound: $O(2^{n/2})$.

## 5. Common growth rates, ranked

| Order | Name | Typical source | $n = 10^6$ |
|---|---|---|---|
| $O(1)$ | constant | hash lookup, arithmetic | 1 |
| $O(\log n)$ | logarithmic | binary search, balanced tree ops | ~20 |
| $O(\sqrt n)$ | square root | primality, split tricks | 1000 |
| $O(n)$ | linear | single scan | $10^6$ |
| $O(n \log n)$ | linearithmic | sorting, divide & conquer | $2 \times 10^7$ |
| $O(n^2)$ | quadratic | nested loops over all pairs | $10^{12}$ |
| $O(2^n)$ | exponential | enumerating subsets | hopeless |
| $O(n!)$ | factorial | enumerating permutations | hopeless |

Rule of thumb from constraints: $n \le 10^5$ → $O(n \log n)$ acceptable; $n \le 10^3$ → $O(n^2)$ acceptable; $n \le 20$ → exponential acceptable.

## 6. The "binary search over an answer" complexity formula

Many problems in Chapter 1 (Koko, Capacity-to-Ship, House Robber IV) don't binary search over an *array* — they binary search over a **range of possible answers** $[L, R]$, checking each candidate with a linear predicate $P$ that costs $O(f(n))$. The total is:

$$
T(n) = O\!\left(f(n) \cdot \log_2(R - L)\right)
$$

The $\log$ comes from the halving argument of §2 (the search space shrinks from $R - L$ to 1 in $\log_2(R-L)$ steps), and the $f(n)$ factor is paid per step. This single formula covers Koko ($O(n \log R)$), Capacity-to-Ship ($O(n \log S)$), and House Robber IV ($O(n \log V)$).

## 7. Space complexity is just "how much memory do we allocate"

- **Iterative binary search:** $O(1)$ auxiliary (a few pointers).
- **Recursion:** each live stack frame holds its locals, so depth $d$ costs $O(d)$. A balanced binary search recursion of depth $\log n$ costs $O(\log n)$; a linear recursion of depth $n$ costs $O(n)$.
- **DP tables:** a $m \times n$ table is $\Theta(mn)$; rolling arrays collapse it to $O(\min(m,n))$.

## 8. Amortized analysis (one idea, huge payoff)

Some operations are occasionally expensive but cheap *on average*. The classic example: a dynamically growing array that doubles when full. Resizing happens at sizes $1, 2, 4, \dots, 2^k$ costing $1, 2, 4, \dots, 2^k$ respectively. By the geometric-series identity:

$$
1 + 2 + 4 + \cdots + 2^k = 2^{k+1} - 1 = O(n)
$$

over $n$ insertions, i.e. **$O(1)$ amortized per insertion**. This is why `ArrayList`/`vector`/`StringBuilder` appends are "O(1)" in interviews, and it appears again in the *amortized* analyses of caches and splay-like structures in Chapter 12.
