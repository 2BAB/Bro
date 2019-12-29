package me.xx2bab.bro.gradle.util

import com.android.build.gradle.AppPlugin
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object BuildUtils {

    private const val dummyAppModuleContent = "package me.xx2bab.bro.dummy;\n" +
            "\n" +
            "import android.content.Context;\n" +
            "\n" +
            "import java.util.Set;\n" +
            "\n" +
            "import me.xx2bab.bro.annotations.BroModule;\n" +
            "import me.xx2bab.bro.common.IBroApi;\n" +
            "import me.xx2bab.bro.common.IBroModule;\n" +
            "\n" +
            "@BroModule\n" +
            "public class BroDummyAppModule implements IBroModule {\n" +
            "    @Override\n" +
            "    public Set<Class<? extends IBroApi>> getLaunchDependencies() {\n" +
            "        return null;\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void onCreate(Context context) {\n" +
            "\n" +
            "    }\n" +
            "}\n"

//    fun getApplicationInfoFilePath(project: Project, variantName: String): String {
//        return getBroBuildPath(project) + File.separator + variantName + File.separator + Constants.APPLICATION_INFO_FILE_NAME
//    }
//
//    fun getApplicationInfoFile(project: Project, variantName: String): File {
//        return File(getApplicationInfoFilePath(project, variantName))
//    }
//
//    fun getApplicationPublicFilePath(project: Project): String {
//        return getBroBuildPath(project) + File.separator + Constants.APPLICATION_PUBLIC_FILE_NAME
//    }
//
//    fun getApplicationPublicFile(project: Project): File {
//        return File(getApplicationPublicFilePath(project))
//    }
//
//    fun getApplicationIdsFilePath(project: Project): String {
//        return getBroBuildPath(project) + File.separator + Constants.APPLICATION_IDS_FILE_NAME
//    }
//
//    fun getApplicationIdsFile(project: Project): File {
//        return File(getApplicationIdsFilePath(project))
//    }
//
//    fun getApplicationResBundleFilePath(project: Project): String {
//        return getBroBuildPath(project) + File.separator + Constants.APPLICATION_RES_BUNDLE_FILE_NAME
//    }
//
//    fun getApplicationResBundleFile(project: Project): File {
//        return File(getApplicationResBundleFilePath(project))
//    }

    fun getBroBuildPath(project: Project): String {
        return project.buildDir.absolutePath + File.separator + "bro"
    }

    fun getBroBuildDir(project: Project): File {
        return File(getBroBuildPath(project))
    }

    fun mkdirBroBuildDir(project: Project) {
        project.tasks.getByPath("preBuild").doLast {
            val file = getBroBuildDir(project)
            if (!file.exists()) {
                val re = file.mkdirs()
                if (!re) {
                    BroGradleLogger.e("Bro Gradle Plugin Error: Make Build Dir Failed")
                }
            }
        }
    }

    fun zipFiles(archivePath: String, vararg files: File) {
        val archive = File(archivePath)
        if (archive.exists()) {
            archive.delete()
        }
        ZipOutputStream(BufferedOutputStream(FileOutputStream(archivePath))).use { out ->
            for (file in files) {
                FileInputStream(file).use { fi ->
                    BufferedInputStream(fi).use { origin ->
                        val entry = ZipEntry(file.name)
                        out.putNextEntry(entry)
                        origin.copyTo(out, 1024)
                    }
                }
            }
        }
    }

    fun unzipFile(project: Project, zipFilePath: String, outputDirPath: String) {
        project.copySpec {
            it.from(project.zipTree(zipFilePath))
            it.into(outputDirPath)
        }
    }

    fun copyFiles(targetPath: String, vararg files: File) {
        val target = File(targetPath)
        if (!target.exists()) {
            val mkdirResult = target.mkdirs()
            if (!mkdirResult) {
                BroGradleLogger.e("mkdirs for $targetPath is failed!")
            }
        }
        files.forEach { file ->
            if (file.exists() && file.isFile) {
                FileUtils.copyFile(file, File(target.absolutePath + File.separator + file.name))
            }
        }
    }
}
