package me.xx2bab.bro.build.release

import me.xx2bab.bro.build.BuildConfig
import org.gradle.api.Project
import java.io.File

object ReleaseMetaData {

    lateinit var project: Project

    fun init(project: Project) {
        ReleaseMetaData.project = project
    }

    fun getVersion(): String {
        return BuildConfig.Versions.broDevVersion
    }

    fun getBuildFiles(): List<File> {
        val pwd = project.rootProject.rootDir.absolutePath
        val res = mutableListOf<File>()
        val broCoreAar = File(arrayOf(pwd, "bro", "build", "outputs", "aar", "bro-release.aar")
                .joinToString(File.separator))
        res.add(broCoreAar)
        val javaProjectNames = arrayOf(
                "bro-annotations",
                "bro-common",
                "bro-compiler",
                "bro-gradle-plugin"
        )
        for (name in javaProjectNames) {
            val jar = File(arrayOf(pwd, name, "build", "libs", name + "-" + getVersion() + ".jar")
                    .joinToString(File.separator))
            res.add(jar)
        }
        return res
    }
}
