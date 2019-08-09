package me.xx2bab.bro.gradle.anno

import com.android.build.gradle.api.BaseVariant
import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.common.ModuleType
import org.gradle.api.Project
import java.io.File

class AppAnnoProcessorOptionInjector : AnnoProcessorOptionInjector() {

    override fun moduleType(): ModuleType {
        return ModuleType.APPLICATION
    }

    override fun onModuleSpecificInjection(project: Project,
                          variant: BaseVariant): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val applicationSuffixId = (if (variant.buildType.applicationIdSuffix == null) ""
        else variant.buildType.applicationIdSuffix)
        val applicationId = variant.mergedFlavor.applicationId + applicationSuffixId
        val allMergedAssetsPaths = getAllMergedAssets(variant.name.capitalize(), project)
        val aptPath = (project.buildDir.absolutePath + File.separator + "generated"
                + File.separator + "source" + File.separator + "apt")
        map[Constants.ANNO_PROC_ARG_APP_PACKAGE_NAME] = applicationId
        map[Constants.ANNO_PROC_ARG_APP_META_DATA_INPUT_PATH] = allMergedAssetsPaths
        map[Constants.ANNO_PROC_ARG_APP_APT_PATH] = aptPath
        return map
    }

    /**
     * For App to gather .bro files together.
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

}