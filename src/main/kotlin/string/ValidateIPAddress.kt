package string

class ValidateIPAddress {
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
            segment.isNotEmpty() &&
                    segment.all { it.isDigit() } &&
                    (segment.length == 1 || segment[0] != '0') &&
                    segment.length <= 3 &&
                    try {
                        segment.toInt() in 0..255
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
            segment.length in 1..4 &&
                    segment.all { char ->
                        char.isDigit() || char.lowercaseChar() in 'a'..'f'
                    }
        }
    }
}
