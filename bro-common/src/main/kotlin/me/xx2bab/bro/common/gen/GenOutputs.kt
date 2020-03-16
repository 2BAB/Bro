package me.xx2bab.bro.common.gen

import me.xx2bab.bro.common.Constants
import java.io.File
import java.util.*

data class GenOutputs(val appPackageName: String, // The package name of the app.
                      var appAptGenDirectory: File, // The directory for placing new java class generated by the generator.
                      var broBuildDirectory: File // The directory for placing other building artifacts like docs, etc.
) {
    companion object {
        fun generateClassNameForImplementation(clazz: Class<*>): String {
            val className = clazz.canonicalName.split(".").toTypedArray()
            val builder = StringBuilder()
            builder.append(className[0])
            for (i in 1 until className.size) {
                builder.append(className[i].substring(0, 1).toUpperCase(Locale.US))
                        .append(className[i].substring(1))
            }
            return builder.toString() + Constants.GEN_CLASS_SUFFIX
        }
    }

}