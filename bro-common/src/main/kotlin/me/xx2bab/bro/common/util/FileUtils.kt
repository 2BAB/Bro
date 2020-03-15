package me.xx2bab.bro.common.util

import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.IOException

class FileUtils {
    fun filterIllegalCharsForResFileName(origin: String): String {
        return origin.replace(":", "_")
                .replace(".", "_")
                .replace("-", "_")
    }

    fun readFile(file: File?): String? {
        try {
            if (file == null) {
                return null
            }
            val bufferedReader: BufferedReader = file.bufferedReader()
            return bufferedReader.use { it.readText() }
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    fun writeFile(content: String?, filePath: String?, fileName: String?) {
        if (filePath == null || fileName == null) {
            return
        }
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
            writer.write(content ?: "")
            writer.close()
        } catch (e: IOException) {
            println(e.message)
        }
    }

    companion object {
        val default = FileUtils()
    }
}