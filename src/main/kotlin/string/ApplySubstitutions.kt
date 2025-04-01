package string

class ApplySubstitutions {
    fun applySubstitutions(replacements: List<List<String>>, text: String): String {
        val map = mutableMapOf<String, String>()

        for ((key, value) in replacements) {
            map[key] = value
        }

        fun resolve(s: String): String {
            val sb = StringBuilder()
            var i = 0

            while (i < s.length) {
                if (i + 2 < s.length && s[i] == '%' && s[i + 2] == '%') {
                    val key = s[i + 1].toString()
                    sb.append(map[key] ?: "%$key%")  // Replace if found, else keep original
                    i += 3  // Move past the placeholder
                } else {
                    sb.append(s[i])
                    i++
                }
            }

            val result = sb.toString()
            return if (result == s) result else resolve(result)  // Continue resolving if changed
        }

        return resolve(text)
    }
}
