package string

class ValidateIPAddressBetterImplementation {
    fun validIPAddress(queryIP: String): String = when {
        isValidIPv4(queryIP) -> "IPv4"
        isValidIPv6(queryIP) -> "IPv6"
        else -> "Neither"
    }

    private fun isValidIPv4(ip: String): Boolean {
        val segments = ip.split('.')
        if (segments.size != 4) return false

        return segments.all {
            it.isNotEmpty() &&
                    it.length <= 3 &&
                    it.all(Char::isDigit) &&
                    (it.length == 1 || it.first() != '0') &&
                    (it.toIntOrNull() in 0..255)
        }
    }

    private fun isValidIPv6(ip: String): Boolean {
        val segments = ip.split(':')
        if (segments.size != 8) return false

        return segments.all {
            it.length in 1..4 && it.all { char ->
                char in '0'..'9' || char in 'a'..'f' || char in 'A'..'F'
            }
        }
    }
}
