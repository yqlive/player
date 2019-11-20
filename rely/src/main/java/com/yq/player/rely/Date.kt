package com.yq.player.rely

import com.yq.player.rely.keeps.DateUnit
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

const val MILLISECOND = 1L
const val SECOND = 1000L
const val MINUTE = 60000L
const val HOUR = 3600000L
const val DAY = 86400000L
const val TIME_OF_DATE_DEFAULT = "yyyy-MM-dd"
const val TIME_OF_DATE_ZH = "yyyy年MM月dd日"
const val TIME_OF_TIME_ZH = "yyyy年MM月dd日 HH时mm分 "
const val TIME_OF_HOUR_DEFAULT = "HH:mm:ss"
const val TIME_OF_HOUR_HALFDAY = "HH:mm a"
const val TIME_OF_FULL_DEFAULT = "yyyy-MM-dd HH:mm:ss"
const val TIME_OF_FULL = "yyyy-MM-dd HH:mm:ss ssssss"
const val TIME_OF_FULL_NULL = "yyyyMMddHHmmss"
val WEEK = arrayOf("天", "一", "二", "三", "四", "五", "六")
private const val XING_QI = "星期"

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
            w(var5)
        }

        return result
    } else {
        return null
    }
}

fun Date.format(pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"): String {
    val df = SimpleDateFormat(pattern)
    return df.format(this)
}

fun Long.forDate(pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") = this.toDate().format(pattern)

fun String.forDate(
    pattern: String = TIME_OF_FULL_DEFAULT,
    sourcePattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
): String? {
    return if (!this.trim { it <= ' ' }.isEmpty()) {
        var dd = SimpleDateFormat(pattern)
        try {
            val parse = dd.parse(this)
            dd = SimpleDateFormat(sourcePattern)
            dd.format(parse)
        } catch (var5: ParseException) {
            w(var5)
            null
        }

    } else {
        null
    }
}

fun Long.toDate() = Date(this)

val currentTimeMillis: Long
    get() = System.currentTimeMillis()

val currentTime: Date
    get() = Date(System.currentTimeMillis())

val String.currentTime: String
    get() = Date(System.currentTimeMillis()).format(this)


fun Long.formartMillisForZH_cn(): String {
    val fmtTime = StringBuilder()
    val second = this / 1000L
    if (second < 60L) {
        fmtTime.append(second.toString() + "秒钟")
    } else if (second < 3600L) {
        fmtTime.append((second / 60L).toString() + "分钟")
        if (second % 60L != 0L) {
            fmtTime.append((second % 60L).toString() + "秒钟")
        }
    } else {
        var mod: Long
        if (second < 86400L) {
            fmtTime.append((second / 3600L).toString() + "小时")
            mod = second % 3600L
            if (mod != 0L) {
                fmtTime.append((mod / 60L).toString() + "分钟")
                mod %= 60L
                if (mod != 0L) {
                    fmtTime.append(mod.toString() + "秒钟")
                }
            }
        } else {
            fmtTime.append((second / 86400L).toString() + "天")
            mod = second % 86400L
            if (mod != 0L) {
                fmtTime.append((mod / 3600L).toString() + "小时")
                mod = second % 3600L
                if (mod != 0L) {
                    fmtTime.append((mod / 60L).toString() + "分钟")
                    mod %= 60L
                    if (mod != 0L) {
                        fmtTime.append(mod.toString() + "秒钟")
                    }
                }
            }
        }
    }

    return fmtTime.toString()
}

fun Int.toWeek(): String {
    val c = Calendar.getInstance()
    c.timeZone = TimeZone.getTimeZone("GMT+8:00")
    var weekNum = c.get(7) + this
    if (weekNum > 7) {
        weekNum -= 7
    }

    return WEEK[weekNum - 1]
}

@Suppress("DEPRECATION")
fun Date.daysBetween(date: Date = currentTime): Int {
    val cal = Calendar.getInstance()
    cal.time = this
    val time1 = cal.timeInMillis
    cal.time = date
    val time2 = cal.timeInMillis
    var betweenDays = (time2 - time1) / 86400000L
    if (betweenDays == 0L) {
        val day1 = this.day
        val day2 = date.day
        betweenDays = (day2 - day1).toLong()
    }

    return Integer.parseInt(betweenDays.toString())
}

fun Date.millisBetween(date: Date = currentTime): Long {
    val cal = Calendar.getInstance()
    cal.time = this
    val time1 = cal.timeInMillis
    cal.time = date
    val time2 = cal.timeInMillis
    return time2 - time1
}


//日期加法，根据日期单位加减
fun Date.additionDate(value: Int, dateUnit: DateUnit): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    val values = cal.get(dateUnit.code)
    cal.set(dateUnit.code, values + value)
    return cal.time
}

//日期减法，根据日期单位加减
fun Date.subtraction(value: Int, dateUnit: DateUnit): Date {
    return this.additionDate(value * -1, dateUnit)
}

inline val Date.ofMonth: Int
    get() {
        val cal = Calendar.getInstance()
        cal.time = this
        return cal.get(Calendar.MONTH)
    }
inline val Date.ofDay: Int
    get() {
        val cal = Calendar.getInstance()
        cal.time = this
        return cal.get(Calendar.DAY_OF_MONTH)
    }
inline val Date.ofWeek: Int
    get() {
        val cal = Calendar.getInstance()
        cal.time = this
        return cal.get(Calendar.DAY_OF_WEEK)
    }

inline val Calendar.ofMonth: Int
    get() {
        return get(Calendar.MONTH)
    }
inline val Calendar.ofDay: Int
    get() {
        return get(Calendar.DAY_OF_MONTH)
    }
inline val Calendar.ofWeek: Int
    get() {
        return get(Calendar.DAY_OF_WEEK)
    }


fun Date.isSameDay(date2: Date): Boolean {
    val cal1 = Calendar.getInstance()
    cal1.time = this
    val cal2 = Calendar.getInstance()
    cal2.time = date2
    return cal1.isSameDay(cal2)
}

fun Calendar.isSameDay(cal2: Calendar): Boolean {
    return get(Calendar.ERA) == cal2.get(Calendar.ERA) && get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && get(
        Calendar.DAY_OF_YEAR
    ) == cal2.get(Calendar.DAY_OF_YEAR)
}

 fun Date.isSameTime(date2: Date): Boolean {
    val cal1 = Calendar.getInstance()
    cal1.time = this
    val cal2 = Calendar.getInstance()
    cal2.time = date2
    return cal1.isSameTime(cal2)
}

 fun Calendar.isSameTime(cal2: Calendar): Boolean {
    return get(Calendar.ERA) == cal2.get(Calendar.ERA)
            && get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
            && get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
            && get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY)
            && get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE)
}

//日期加法，日期单位为天
operator fun Date.plus(value: Int): Date {

    return this.additionDate(value, DateUnit.DAY)
}

//日期减法，日期单位为天
operator fun Date.minus(value: Int): Date {
    return this.subtraction(value, DateUnit.DAY)
}

//日期加法，日期单位为天
operator fun Date.inc(): Date {
    return this.additionDate(1, DateUnit.DAY)
}

//日期减法，日期单位为天
operator fun Date.dec(): Date {
    return this.subtraction(1, DateUnit.DAY)
}

