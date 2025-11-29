package graph

class MinimumGeneticMutations {
    fun minMutation(startGene: String, endGene: String, bank: Array<String>): Int {
        val bases = listOf('A', 'C', 'G', 'T')
        val geneBank = bank.toMutableSet()
        if (endGene !in geneBank) return -1

        val queue = ArrayDeque<String>().apply { add(startGene) }
        var mutations = 0

        fun getNeighbors(gene: String): List<String> {
            val neighbors = mutableListOf<String>()
            val arr = gene.toCharArray()

            for (i in arr.indices) {
                for (base in bases) {
                    if (base != gene[i]) {
                        arr[i] = base
                        neighbors.add(String(arr))
                    }
                }
                arr[i] = gene[i]
            }
            return neighbors
        }

        while (queue.isNotEmpty()) {
            repeat(queue.size) {
                val current = queue.removeFirst()
                if (current == endGene) return mutations

                for (neighbor in getNeighbors(current)) {
                    if (neighbor in geneBank) {
                        geneBank.remove(neighbor)
                        queue.add(neighbor)
                    }
                }
            }
            mutations++
        }

        return -1
    }
}