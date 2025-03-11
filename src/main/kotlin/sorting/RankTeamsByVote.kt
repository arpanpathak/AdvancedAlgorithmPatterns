package sorting

class RankTeamsByVote {
    fun rankTeams(votes: Array<String>): String {
        val map = mutableMapOf<Char, IntArray>()
        val l = votes[0].length

        for (vote in votes) {
            for (i in vote.indices) {
                val c = vote[i]
                map.putIfAbsent(c, IntArray(l))
                map[c]!![i]++
            }
        }

        val list = map.keys.toList().sortedWith { a, b ->
            for (i in 0 until l) {
                if (map[a]!![i] != map[b]!![i]) {
                    return@sortedWith map[b]!![i] - map[a]!![i]
                }
            }
            a - b
        }

        return list.joinToString("")
    }
}
