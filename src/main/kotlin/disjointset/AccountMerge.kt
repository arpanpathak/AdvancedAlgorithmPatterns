package disjointset


class AccountMerge {
    class UnionFind<T> {
        data class Node<T>(var parent: T, var rank: Int)

        private val nodes = mutableMapOf<T, Node<T>>()

        fun add(x: T) {
            nodes.putIfAbsent(x, Node(x, 0))
        }

        fun find(x: T): T {
            val node = nodes[x] ?: throw IllegalAccessException("Value $x not found")

            if (node.parent != x)
                node.parent = find(node.parent)

            return node.parent
        }

        fun union(x: T, y: T) {
            val rootX = find(x)
            val rootY = find(y)
            if (rootX != rootY) {
                val nodeX = nodes[rootX]!!
                val nodeY = nodes[rootY]!!
                when {
                    nodeX.rank > nodeY.rank -> nodeY.parent = rootX
                    nodeX.rank < nodeY.rank -> nodeX.parent = rootY
                    else -> {
                        nodeY.parent = rootX
                        nodeX.rank++
                    }
                }
            }
        }
    }

    fun accountsMerge(accounts: List<List<String>>): List<List<String>> {
        val emailToName = mutableMapOf<String, String>()
        val uf = UnionFind<String>()

        // Add emails to Union-Find, map them to their owner's name, and union them in one pass
        accounts.forEach { account ->
            val name = account.first()
            account.drop(1).let { emails ->
                val firstEmail = emails.first()
                emails.forEach { email ->
                    emailToName[email] = name
                    uf.add(email)
                    uf.union(firstEmail, email)
                }
            }
        }

        // Group emails by their root parent
        val components = emailToName.keys.groupBy { uf.find(it) }

        // Construct the result
        return components.values.map { emails ->
            emails.sorted().let { sortedEmails ->
                listOf(emailToName[sortedEmails.first()]!!) + sortedEmails
            }
        }
    }
}