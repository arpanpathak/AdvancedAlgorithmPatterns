package math.binary

class AddBinary {
    fun addBinary(a: String, b: String): String {
        val result = StringBuilder()
        var (i, j) = a.lastIndex to b.lastIndex
        var carry = 0

        while (i >= 0 || j >= 0 || carry > 0) {
            var sum = carry
            if (i >= 0) sum += a[i--] - '0'
            if (j >= 0) sum += b[j--] - '0'
            result.append(sum % 2)
            carry = sum / 2
        }

        return result.reverse().toString()
    }
}
