fun main() {
    val `মোট_বিষয়` = 4
    val `পূর্বশর্ত_তালিকা` = arrayOf(intArrayOf(1, 0), intArrayOf(2, 1), intArrayOf(3, 2))

    val `ফলাফল` = `ক্রম_নির্ণয়`(`মোট_বিষয়`, `পূর্বশর্ত_তালিকা`)

    if (`ফলাফল`.isNotEmpty()) {
        println("সঠিক ক্রম: ${`ফলাফল`.joinToString(" -> ")}")
    } else {
        println("সাইকেল পাওয়া গেছে!")
    }
}

fun `ক্রম_নির্ণয়`(`মোট_সংখ্যা`: Int, `পূর্বশর্ত_উপাত্ত`: Array<IntArray>): List<Int> {
    val `সংযোগ_চিত্র` = mutableMapOf<Int, MutableList<Int>>()
    val `প্রবেশ_মাত্রা` = IntArray(`মোট_সংখ্যা`)

    `পূর্বশর্ত_উপাত্ত`.forEach { (`কোর্স`, `পূর্বশর্ত`) ->
        `সংযোগ_চিত্র`.getOrPut(`পূর্বশর্ত`) { mutableListOf() }.add(`কোর্স`)
        `প্রবেশ_মাত্রা`[`কোর্স`]++
    }

    val `অপেক্ষমান_সারি` = ArrayDeque(`প্রবেশ_মাত্রা`.indices.filter { `প্রবেশ_মাত্রা`[it] == 0 })

    val `ফলাফল` = buildList {
        while (`অপেক্ষমান_সারি`.isNotEmpty()) {
            val `বর্তমান` = `অপেক্ষমান_সারি`.removeFirst().also { add(it) }
            `সংযোগ_চিত্র`[`বর্তমান`]?.forEach { `প্রতিবেশী` ->
                if (--`প্রবেশ_মাত্রা`[`প্রতিবেশী`] == 0) `অপেক্ষমান_সারি`.add(`প্রতিবেশী`)
            }
        }
    }

    return if (`ফলাফল`.size == `মোট_সংখ্যা`) `ফলাফল` else emptyList()
}
