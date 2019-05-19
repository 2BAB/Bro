package me.xx2bab.bro.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import me.xx2bab.bro.gradle.utils.BroGradleLogger
import me.xx2bab.bro.gradle.utils.BuildUtils
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project

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
        AnnotationProcessorParamsInjector.inject(isApplication(project), variants, project)
        variants.all { variant ->
            val task = project.tasks.getByPath("generate${variant.name.capitalize()}Sources")
            task.outputs.upToDateWhen { false }
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