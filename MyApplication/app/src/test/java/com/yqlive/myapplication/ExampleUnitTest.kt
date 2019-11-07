package com.yqlive.myapplication

import org.junit.Test
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)
        updateView("2019-11-15".toDate() ?: Date(), "2019-11-20".toDate() ?: Date())
    }

    fun updateView(start: Date, end: Date, selected: Date = start) {
        val calendar = Calendar.getInstance()
        calendar.time = start
        var week = calendar.get(Calendar.DAY_OF_WEEK) - 1

        val startDay = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.time = selected
        var endDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val endCal = Calendar.getInstance()
        endCal.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), endDay)

        if (endCal.time > end) {
            endCal.time = end
            endDay = endCal.get(Calendar.DATE)
        }
        print("${endCal.time.format("yyyy-MM-dd")} endDay=$endDay")

        val days = arrayListOf<String>()
        for (i in 0 until week) {
            days.add("-1")
        }
        for (i in startDay..endDay) {
            days.add("$i")
        }
//        print(" $week - $startDate - $actualMaximum")
        days.forEachIndexed { index, s ->
            print("$s ")
            if ((index + 1) % 7 == 0)
                println()
        }
    }

    fun Date.format(pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"): String {
        val df = SimpleDateFormat(pattern)
        return df.format(this)
    }

    fun String.toDate(): Date? {
        if (!this.trim { it <= ' ' }.isEmpty()) {
            var result: Date? = null
            var parse = this.replaceFirst("^[0-9]{4}([^0-9]?)".toRegex(), "yyyy$1")
            parse = parse.replaceFirst("^[0-9]{2}([^0-9]?)".toRegex(), "yy$1")
            parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)".toRegex(), "$1MM$2")
            parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}( ?)".toRegex(), "$1dd$2")
            parse = parse.replaceFirst("( )[0-9]{1,2}([^0-9]?)".toRegex(), "$1HH$2")
            parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)".toRegex(), "$1mm$2")
            parse = parse.replaceFirst("([^0-9]?)[0-9]{1,2}([^0-9]?)".toRegex(), "$1ss$2")
            val format = SimpleDateFormat(parse)

            try {
                result = format.parse(this)
            } catch (var5: ParseException) {
//                var5.w()
            }

            return result
        } else {
            return null
        }
    }

}
