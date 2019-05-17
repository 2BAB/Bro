package me.xx2bab.bro.gradle.utils

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import me.xx2bab.bro.common.Constants

class BuildUtils {

    static String getApplicationInfoFilePath(Project project, String variantName) {
        return getBroBuildPath(project) + File.separator + variantName + File.separator + Constants.APPLICATION_INFO_FILE_NAME
    }

    static File getApplicationInfoFile(Project project, String variantName) {
        return new File(getApplicationInfoFilePath(project, variantName))
    }

    static String getApplicationPublicFilePath(Project project) {
        return getBroBuildPath(project) + File.separator + Constants.APPLICATION_PUBLIC_FILE_NAME
    }

    static File getApplicationPublicFile(Project project) {
        return new File(getApplicationPublicFilePath(project))
    }

    static String getApplicationIdsFilePath(Project project) {
        return getBroBuildPath(project) + File.separator + Constants.APPLICATION_IDS_FILE_NAME
    }

    static File getApplicationIdsFile(Project project) {
        return new File(getApplicationIdsFilePath(project))
    }

    static String getApplicationResBundleFilePath(Project project) {
        return getBroBuildPath(project) + File.separator + Constants.APPLICATION_RES_BUNDLE_FILE_NAME
    }

    static File getApplicationResBundleFile(Project project) {
        return new File(getApplicationResBundleFilePath(project))
    }


    static String getBroBuildPath(Project project) {
        return project.buildDir.absolutePath + File.separator + "bro"
    }

    static File getBroBuildDir(Project project) {
        return new File(getBroBuildPath(project))
    }

    static void mkdirBroBuildDir(Project project) {
        project.tasks['preBuild'].doLast {
            File file = getBroBuildDir(project)
            if (!file.exists()) {
                boolean re = file.mkdirs()
                if (!re) {
                    BroGradleLogger.e("Bro Gradle Plugin Error: Make Build Dir Failed")
                }
            }
        }
    }

    static void zipFiles(String archivePath, File... files) {
        File archive = new File(archivePath)
        if (archive.exists()) {
            archive.delete()
        }
        new AntBuilder().zip(destFile: archivePath) {
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

    static void unzipFile(Project project, String zipFilePath, String outputDirPath) {
        project.copy {
            from project.zipTree(zipFilePath)
            into outputDirPath
        }
    }

    static void copyFiles(String targetPath, File... files) {
        File target = new File(targetPath)
        if (!target.exists()) {
            boolean mkdirResult = target.mkdirs()
            if (!mkdirResult) {
                BroGradleLogger.e("mkdirs for ${targetPath} is failed!")
            }
        }
        files.each { file ->
            if (file.exists() && file.isFile()) {
                FileUtils.copyFile(file, new File(target.absolutePath + File.separator + file.name))
            }
        }
    }
}
