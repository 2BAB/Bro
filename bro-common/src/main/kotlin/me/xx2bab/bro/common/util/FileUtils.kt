package me.xx2bab.bro.common.util

import java.io.*

class FileUtils {
    fun filterIllegalCharsForResFileName(origin: String): String {
        return origin.replace(":", "_")
                .replace(".", "_")
                .replace("-", "_")
    }

    fun readFile(file: File?): String? {
        return try {
            val inputStream: InputStream = FileInputStream(file)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val builder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
            builder.toString()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    fun writeFile(content: String?, filePath: String, fileName: String) {
        val folder = File(filePath)
        if (!folder.exists()) {
            val result = folder.mkdirs()
            if (!result) {
                return
            }
        }
        try {
            val file = File(filePath + File.separator + fileName)
            if (!file.exists()) {
                val result = file.createNewFile()
                if (!result) {
                    return
                }
            }
            val writer = FileWriter(file)
            writer.write(content)
            writer.close()
        } catch (e: IOException) {
            println(e.message)
        }
    }

    companion object {
        val default = FileUtils()
    }
}