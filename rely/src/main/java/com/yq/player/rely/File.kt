package com.yq.player.rely

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import com.yq.player.rely.keeps.io.StorageType
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.text.DecimalFormat
import java.util.concurrent.ExecutionException

const val CONTENT_PREFIX = "content"
const val FILE_PREFIX = "file"
const val KB = 1024
const val MB = 1024L * KB
const val GB = 1024L * MB

val JPEG = ".jpg"
val GIF = ".gif"
val PNG = ".png"
val BMP = ".bmp"
val WebP = ".webp"
val IMAGE by lazy { arrayOf(JPEG, GIF, PNG, BMP, WebP) }
val AVI = ".avi"
val _3GPP = ".3gp"
val MPEG_4 = ".mp4"
val MPEG_TS = ".ts"
val WebM = ".webm"
val Matroska = ".mkv"
val VIDEO by lazy { arrayOf(MPEG_4, AVI, _3GPP, MPEG_TS, WebM, Matroska) }
val AAC = ".aac"
val FLAC = ".flac"
val MP3 = ".mp3"
val MID = ".mid"
val XMF = ".xmf"
val MXMF = ".mxmf"
val RTTTL = ".rtttl"
val RTX = ".rtx"
val OTA = ".ota"
val iMelody = ".imy"
val Ogg = ".ogg"
val WAVE = ".wav"
val AUDIO by lazy { arrayOf(AAC, FLAC, MP3) }
val MEDIA by lazy { arrayOf<String>().plus(VIDEO).plus(IMAGE).plus(AUDIO) }
val NORMAL by lazy { arrayOf<String>() }

/**
 * Discription:[getAllFileType,常见文件头信息]
 */
val fileTypeMap by lazy {
    mutableMapOf<String, String>().apply {
        this["ffd8ffe000104a464946"] = JPEG //JPEG (jpg)
        this["89504e470d0a1a0a0000"] = PNG //PNG (png)
        this["47494638396126026f01"] = GIF //GIF (gif)
        this["49492a00227105008037"] = ".tif" //TIFF (tif)
        this["424d228c010000000000"] = BMP //16色位图(bmp)
        this["424d8240090000000000"] = BMP //24位位图(bmp)
        this["424d8e1b030000000000"] = BMP //256色位图(bmp)
        this["41433130313500000000"] = ".dwg" //CAD (dwg)
        this["3c21444f435459504520"] = ".html" //HTML (html)
        this["3c21646f637479706520"] = ".htm" //HTM (htm)
        this["48544d4c207b0d0a0942"] = ".css" //css
        this["696b2e71623d696b2e71"] = ".js" //js
        this["7b5c727466315c616e73"] = ".rtf" //Rich Text Format (rtf)
        this["38425053000100000000"] = ".psd" //Photoshop (psd)
        this["46726f6d3a203d3f6762"] = ".eml" //Email [Outlook Express 6] (eml)
        this["d0cf11e0a1b11ae10000"] = ".doc" //MS Excel 注意：word、msi 和 excel的文件头一样
        this["d0cf11e0a1b11ae10000"] = ".vsd" //Visio 绘图
        this["5374616E64617264204A"] = ".mdb" //MS Access (mdb)
        this["252150532D41646F6265"] = ".ps"
        this["255044462d312e350d0a"] = ".pdf" //Adobe Acrobat (pdf)
        this["2e524d46000000120001"] = ".rmvb" //rmvb/rm相同
        this["464c5601050000000900"] = ".flv" //flv与f4v相同
        this["00000020667479706d70"] = MPEG_4
        this["49443303000000002176"] = MP3
        this["000001ba210001000180"] = ".mpg" //
        this["3026b2758e66cf11a6d9"] = ".wmv" //wmv与asf相同
        this["52494646e27807005741"] = WAVE //Wave (wav)
        this["52494646d07d60074156"] = AVI
        this["4d546864000000060001"] = ".mid" //MIDI (mid)
        this["504b0304140000000800"] = ".zip"
        this["526172211a0700cf9073"] = ".rar"
        this["235468697320636f6e66"] = ".ini"
        this["504b03040a0000000000"] = ".jar"
        this["4d5a9000030000000400"] = ".exe"//可执行文件
        this["3c25402070616765206c"] = ".jsp"//jsp文件
        this["4d616e69666573742d56"] = ".mf"//MF文件
        this["3c3f786d6c2076657273"] = ".xml"//xml文件
        this["494e5345525420494e54"] = ".sql"//xml文件
        this["7061636b616765207765"] = ".java"//java文件
        this["406563686f206f66660d"] = ".bat"//bat文件
        this["1f8b0800000000000000"] = ".gz"//gz文件
        this["6c6f67346a2e726f6f74"] = ".properties"//bat文件
        this["cafebabe0000002e0041"] = ".class"//bat文件
        this["49545346030000006000"] = ".chm"//bat文件
        this["04000000010000001300"] = ".mxp"//bat文件
        this["504b0304140006000800"] = ".docx"//docx文件
        this["d0cf11e0a1b11ae10000"] = ".wps"//WPS文字wps、表格et、演示dps都是一样的
        this["6431303a637265617465"] = ".torrent"

        this["6D6F6F76"] = ".mov" //Quicktime (mov)
        this["FF575043"] = ".wpd" //WordPerfect (wpd)
        this["CFAD12FEC5FD746F"] = ".dbx" //Outlook Express (dbx)
        this["2142444E"] = ".pst" //Outlook (pst)
        this["AC9EBD8F"] = ".qdf" //Quicken (qdf)
        this["E3828596"] = ".pwl" //Windows Password (pwl)
        this["2E7261FD"] = ".ram" //Real Audio (ram)
    }
}

val storageType: StorageType
    get() = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
        StorageType.EXTERNAL
    } else {
        StorageType.INTERNAL
    }

val df = DecimalFormat("0.00")//格式化小数
@JvmOverloads
fun Long.formateSize(andUnit: Boolean = true): String {
    return when {
        this / GB >= 1 -> //如果当前Byte的值大于等于1GB
            df.format((this / GB.toFloat()).toDouble()) + if (andUnit) " GB" else ""
        this / MB >= 1 -> //如果当前Byte的值大于等于1MB
            df.format((this / MB.toFloat()).toDouble()) + if (andUnit) " MB" else ""
        this / KB >= 1 -> //如果当前Byte的值大于等于1KB
            df.format((this / KB.toFloat()).toDouble()) + if (andUnit) " KB" else ""
        else -> toString() + if (andUnit) " B" else ""
    }
}

inline fun File.copyAndRename(
    destDir: File, createNewPrefix: (prefix: String, suffix: String) -> String = { _, _ ->
        "${System.currentTimeMillis().toString().md5}_${Math.random()}"
    }
): File? {
    val suffixIndex = this.name.lastIndexOf(".")
    val prefix = if (suffixIndex > 0) this.name.substring(0, suffixIndex) else this.name
    val suffix = if (suffixIndex > 0) this.name.substring(suffixIndex, this.name.length) else ""
    val newPrefix = createNewPrefix(prefix, suffix)
    val destFile = File(destDir, "$newPrefix$suffix")
    return try {
        FileUtils.copyFile(this, destFile)
        destFile
    } catch (e: Throwable) {
        null
    }
}

//统计目录大小的方法
val File?.sizeOf: Long
    get() {
        if (this == null) {
            return 0
        }
        var size: Long = 0
        if (this.exists())
            if (this.isFile) {
                //如果是文件，获取文件大小累加
                size += this.length()
            } else if (this.isDirectory) {
                //获取目录中的文件及子目录信息
                val f1 = this.listFiles()
                for (i in f1.indices) {
                    //调用递归遍历f1数组中的每一个对象
                    size += f1[i].sizeOf
                }
            }
        return size
    }

fun Context.cache(name: String): File? {
    val result = cache.absolutePath + File.separator + name
    try {
        val file = File(result)
        if (file.exists() || file.mkdirs())
            return file
    } catch (e: ExecutionException) {
        d("$result create failed", "CACHE_PATH")
    } catch (e: InterruptedException) {
        d("$result create failed", "CACHE_PATH")
    }
    return null
}

val Context.cache
    get() = File(if (isExternal) externalCacheDir!!.path else cacheDir.path)

fun Context.file(name: String? = null): File {
    val fileDir =
        (if (isExternal)
            getExternalFilesDir(name)
        else if (!name.isNullOrEmpty())
            File(filesDir, name)
        else
            filesDir) ?: return filesDir
    if (!fileDir.exists())
        fileDir.mkdirs()
    return fileDir
}

val isExternal: Boolean
    get() = storageType == StorageType.EXTERNAL

/**
 * 得到上传文件的文件头
 *
 * @param src
 * @return
 */
val ByteArray?.hexString: String?
    get() {
        val stringBuilder = StringBuilder()
        if (this == null || this.isEmpty()) {
            return null
        }
        for (i in this.indices) {
            val v: Int = this[i].toInt() and 0xFF
            val hv = Integer.toHexString(v)
            if (hv.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(hv)
        }
        return stringBuilder.toString()
    }


val String.fileType: String get() = File(this).fileType

val String.type: String
    get() {
        if (!TextUtils.isEmpty(this)) {
            val of = this.lastIndexOf(".")
            if (of >= 0) {
                return this.substring(of, this.length).toLowerCase()
            }
        }
        return ""
    }

/**
 * 根据制定文件的文件头判断其文件类型
 *
 * @param filePaht
 * @return
 */
val File.fileType: String
    get() {
        if (!exists() || isDirectory)
            return "Not a separate file"
        var res: String? = null
        try {
            val `is` = FileInputStream(this)
            val b = ByteArray(10)
            `is`.read(b, 0, b.size)
            val fileCode = b.hexString

            if (fileTypeMap.containsKey(fileCode))
                return fileTypeMap[fileCode]!!
            //这种方法在字典的头代码不够位数的时候可以用但是速度相对慢一点
            val keyIter = fileTypeMap.keys.iterator()
            while (keyIter.hasNext()) {
                val key = keyIter.next()
                if (key.toLowerCase().startsWith(fileCode!!.toLowerCase()) || fileCode.toLowerCase().startsWith(key.toLowerCase())) {
                    res = fileTypeMap[key]
                    break
                }
            }
        } catch (e: FileNotFoundException) {
            w(e)
        } catch (e: IOException) {
            w(e)
        }

        return if (res.isNullOrEmpty()) name.type else res
    }

/**
 *   删除文件夹
 *   @param folderPath 文件夹完整绝对路径
 */
fun String.delFolder() {
    try {
        delAllFile() //删除完里面所有内容
        File(this).delete() //删除空文件夹
    } catch (e: Exception) {
        w(e)
    }

}

/**
 * 删除指定文件夹下所有文件
 * @param path 文件夹完整绝对路径
 */
fun String.delAllFile(): Boolean {
    var flag = false
    val file = File(this)
    if (!file.exists()) {
        return flag
    }
    if (!file.isDirectory) {
        return flag
    }
    val tempList = file.list()
    var temp: File?
    for (i in tempList.indices) {
        temp = if (this.endsWith(File.separator)) {
            File(this + tempList[i])
        } else {
            File(this + File.separator + tempList[i])
        }
        if (temp.isFile) {
            temp.delete()
        }
        if (temp.isDirectory) {
            with("$this/${tempList[i]}") {
                delAllFile()//先删除文件夹里面的文件
                delFolder()//再删除空文件夹
            }
            flag = true
        }
    }
    return flag
}

val File.fileName: String
    get() {
        return if (absolutePath.isNullOrEmpty()) "" else absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1)
    }


val File.fileExt: String
    get() {
        return if (fileName.isNullOrEmpty()) "" else fileName.substring(fileName.lastIndexOf(".") + 1)
    }