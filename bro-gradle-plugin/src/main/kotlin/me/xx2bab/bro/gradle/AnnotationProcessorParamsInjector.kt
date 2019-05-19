package me.xx2bab.bro.gradle

import com.android.build.gradle.api.BaseVariant
import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.gradle.utils.BuildUtils
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import java.io.File

object AnnotationProcessorParamsInjector {

    fun inject(isApplication: Boolean, variants: DomainObjectSet<out BaseVariant>, project: Project) {
        variants.all { variant ->
            val args = variant.javaCompileOptions.annotationProcessorOptions.arguments
            val moduleName = project.name
            val buildDir = BuildUtils.getBroBuildPath(project)

            // common args
            args[Constants.KEY_MODULE_NAME] = moduleName
            args[Constants.KEY_MODULE_BUILD_TYPE] = if (isApplication) "Application" else "Library"
            args[Constants.KEY_MODULE_BUILD_DIR] = buildDir

            // different arg(s) in each condition
            if (isApplication) {
                val applicationSuffixId = (if (variant.buildType.applicationIdSuffix == null) ""
                else variant.buildType.applicationIdSuffix)
                val applicationId = variant.mergedFlavor.applicationId + applicationSuffixId
                val allMergedAssetsPaths = getAllMergedAssets(variant.name.capitalize(), project)
                val aptPath = (project.buildDir.absolutePath + File.separator + "generated"
                        + File.separator + "source" + File.separator + "apt")
                args[Constants.KEY_HOST_PACKAGE_NAME] = applicationId
                args[Constants.KEY_HOST_ALL_ASSETS_SOURCE] = allMergedAssetsPaths
                args[Constants.KEY_HOST_APT_PATH] = aptPath
            } else {
                args[Constants.KEY_LIB_BUNDLES_ASSETS_PATH] = getBuildBundlesAssetsPath(
                        variant.name.capitalize(), project)
            }
        }

    }

    /**
     * For HOST to gather .bro files together.
     * @param variantName current build type
     * @param project gradle project object
     * @return all module's merged-assets folders
     */
    private fun getAllMergedAssets(variantName: String, project: Project): String {
        val result = StringBuilder()
        val task = project.tasks.getByPath("merge${variantName}Assets")
        task.inputs.files.forEach {
            if (!it.absolutePath.contains("shaders")) {
                result.append(it.absolutePath).append(";")
            }
        }
        return result.deleteCharAt(result.length - 1).toString()
    }

    /**
     * The bundle path that will zip to an aar.
     * @param variantName current build type
     * @param project gradle project object
     * @return the folder's absolutely path
     */
    private fun getBuildBundlesAssetsPath(variantName: String, project: Project): String {
        var result = ""

        val processResourcesTask = project.tasks.getByPath("package${variantName}Assets")
        if (processResourcesTask.outputs.files.files.size == 0) {
            throw IllegalStateException("")
        }

        processResourcesTask.outputs.files.forEach {
            val path = it.absolutePath
            if (path != null && !path.contains("incremental")) {
                result = path
            }
        }

        return result
    }

}