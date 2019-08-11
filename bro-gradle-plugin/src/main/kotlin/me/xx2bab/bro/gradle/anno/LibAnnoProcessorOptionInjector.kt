package me.xx2bab.bro.gradle.anno

import com.android.build.gradle.api.BaseVariant
import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.common.ModuleType
import org.gradle.api.Project

class LibAnnoProcessorOptionInjector : AnnoProcessorOptionInjector() {

    override fun moduleType(): ModuleType {
        return ModuleType.LIBRARY
    }

    override fun onModuleSpecificInjection(project: Project,
                                           variant: BaseVariant): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map[Constants.ANNO_PROC_ARG_LIB_META_DATA_OUTPUT_PATH] = getBuildBundlesAssetsPath(
                variant.name.capitalize(), project)
        return map
    }


    /**
     * The bundle path that will zip to an aar.
     * @param variantName current build type
     * @param project gradle project object
     * @return the folder's absolutely path
     */
    fun getBuildBundlesAssetsPath(variantName: String, project: Project): String {
        var result = ""

        val packageAssetsTask = project.tasks.getByPath("package${variantName}Assets")
        if (packageAssetsTask.outputs.files.files.size == 0) {
            throw IllegalStateException("")
        }

        packageAssetsTask.outputs.files.forEach {
            val path = it.absolutePath
            if (!path.contains("incremental") && !path.contains("shader")) {
                result = path
            }
        }

        return result
    }

}