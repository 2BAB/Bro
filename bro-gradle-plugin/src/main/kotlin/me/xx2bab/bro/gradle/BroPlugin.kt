package me.xx2bab.bro.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.gradle.anno.AnnoProcessorOptionInjector
import me.xx2bab.bro.gradle.anno.AppAnnoProcessorOptionInjector
import me.xx2bab.bro.gradle.anno.LibAnnoProcessorOptionInjector
import me.xx2bab.bro.gradle.util.BroGradleLogger
import me.xx2bab.bro.gradle.util.BuildUtils
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import java.io.File

class BroPlugin : Plugin<Project> {

    private val extensionName = "bro"

    override fun apply(project: Project) {
        // Tools initialization
        BroGradleLogger.setProject(project)
        BroGradleLogger.l("bro-gradle-plugin begin to process")

        // Create extension
        val broExtension = project.extensions.create(extensionName, BroExtension::class.java)

        // Register Tasks
        project.afterEvaluate {
            BuildUtils.mkdirBroBuildDir(project)
            onAfterEvaluate(project)
        }
    }

    private fun onAfterEvaluate(project: Project) {
        val variants = getVariants(project)
        val injector = if (isApplication(project)) {
            AppAnnoProcessorOptionInjector()
        } else {
            LibAnnoProcessorOptionInjector()
        }
        injector.inject(project, variants)

        variants.all { variant ->
            val variantName = variant.name.capitalize()
            // A hack to make sure we run annotation processors every time
            val task = project.tasks.getByPath("generate${variantName}Sources")
            task.outputs.upToDateWhen { false }

            if (!isApplication(project)) {
                // A hack to make sure .bro file will be packaged into the aar,
                // by copying each from /module-path/build/bro/xxx.bro
                // to /{modulePath}/build/intermediates/library_assets/{variantName}/out
                val packageAssetsTask = project.tasks.getByPath("package${variantName}Assets")
                packageAssetsTask.doLast {
                    val libAssetsMergedPath = (injector as LibAnnoProcessorOptionInjector)
                            .getBuildBundlesAssetsPath(variantName, project)
                    val broBuildDir = BuildUtils.getBroBuildDir(project).listFiles()
                    var broFile: File? = null
                    broBuildDir?.forEach {
                        if (".${it.extension}" == Constants.MODULE_META_INFO_FILE_SUFFIX) {
                            broFile = it
                        }
                    }
                    broFile?.let { BuildUtils.copyFiles(libAssetsMergedPath, it) }
                }
            }
        }
    }

    private fun isApplication(project: Project): Boolean {
        return project.plugins.hasPlugin(AppPlugin::class.java)
    }

    private fun getVariants(project: Project): DomainObjectSet<out BaseVariant> {
        return if (isApplication(project)) {
            project.extensions.findByType(AppExtension::class.java)!!.applicationVariants
        } else {
            project.extensions.findByType(LibraryExtension::class.java)!!.libraryVariants
        }
    }

}