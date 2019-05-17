package me.xx2bab.bro.gradle.utils

import me.xx2bab.bro.common.Constants
import org.apache.commons.io.FileUtils
import org.gradle.api.AntBuilder
import org.gradle.api.Project
import org.gradle.internal.impldep.bsh.commands.dir
import java.io.File

object BuildUtils {

    fun getApplicationInfoFilePath(project: Project, variantName: String): String {
        return getBroBuildPath(project) + File.separator + variantName + File.separator + Constants.APPLICATION_INFO_FILE_NAME
    }

    fun getApplicationInfoFile(project: Project, variantName: String): File {
        return File(getApplicationInfoFilePath(project, variantName))
    }

    fun getApplicationPublicFilePath(project: Project): String {
        return getBroBuildPath(project) + File.separator + Constants.APPLICATION_PUBLIC_FILE_NAME
    }

    fun getApplicationPublicFile(project: Project): File {
        return File(getApplicationPublicFilePath(project))
    }

    fun getApplicationIdsFilePath(project: Project): String {
        return getBroBuildPath(project) + File.separator + Constants.APPLICATION_IDS_FILE_NAME
    }

    fun getApplicationIdsFile(project: Project): File {
        return File(getApplicationIdsFilePath(project))
    }

    fun getApplicationResBundleFilePath(project: Project): String {
        return getBroBuildPath(project) + File.separator + Constants.APPLICATION_RES_BUNDLE_FILE_NAME
    }

    fun getApplicationResBundleFile(project: Project): File {
        return File(getApplicationResBundleFilePath(project))
    }


    fun getBroBuildPath(project: Project): String {
        return project.buildDir.absolutePath + File.separator + "bro"
    }

    fun getBroBuildDir(project: Project): File {
        return File(getBroBuildPath(project))
    }

    fun mkdirBroBuildDir(project: Project) {
        project.tasks['preBuild'].doLast {
            val file = getBroBuildDir(project)
            if (!file.exists()) {
                val re = file.mkdirs()
                if (!re) {
                    BroGradleLogger.e("Bro Gradle Plugin Error: Make Build Dir Failed")
                }
            }
        }
    }

    fun void zipFiles(archivePath: String , File... files)
    {
        val archive = File(archivePath)
        if (archive.exists()) {
            archive.delete()
        }
        AntBuilder().zip(destFile: archivePath) {
            for (File file : files) {
            if (file.exists()) {
                if (file.isFile()) {
                    fileset(file: file)
                } else if (file.isDirectory()) {
                    fileset(dir: file)
                }
            }
        }
        }
    }

    fun unzipFile(project: Project, zipFilePath: String, outputDirPath: String) {
        project.copy {
            from project . zipTree (zipFilePath)
            into outputDirPath
        }
    }

    fun copyFiles(targetPath: String, File... files)
    {
        val target = File(targetPath)
        if (!target.exists()) {
            boolean mkdirResult = target . mkdirs ()
            if (!mkdirResult) {
                BroGradleLogger.e("mkdirs for ${targetPath} is failed!")
            }
        }
        files.each { file ->
            if (file.exists() && file.isFile()) {
                FileUtils.copyFile(file, File(target.absolutePath + File.separator + file.name))
            }
        }
    }
}
