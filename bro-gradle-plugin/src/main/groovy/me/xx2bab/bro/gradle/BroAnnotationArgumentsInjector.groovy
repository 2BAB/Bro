package me.xx2bab.bro.gradle

import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.gradle.utils.BuildUtils
import org.gradle.api.Project
import org.gradle.api.Task

class BroAnnotationArgumentsInjector {

    static inject(boolean isApplication, Project project) {
        def variants = isApplication ? project.android.applicationVariants : project.android.libraryVariants
        variants.all { variant ->
            def args = variant.javaCompile.options.compilerArgs
            def moduleName = variant.androidBuilder.mProjectId.toString().substring(1)
            def buildDir = BuildUtils.getBroBuildPath(project)

            // common
            args << genAnnotationProcessorArgs(Constants.KEY_MODULE_NAME, moduleName)
            args << genAnnotationProcessorArgs(Constants.KEY_MODULE_BUILD_TYPE, isApplication ? "Application" : "Library")
            args << genAnnotationProcessorArgs(Constants.KEY_MODULE_BUILD_DIR, buildDir)

            // condition
            if (isApplication) {
                def applicationId = [variant.mergedFlavor.applicationId, variant.buildType.applicationIdSuffix].findAll().join()
                def allMergedAssetsPaths = getAllMergedAssets(variant, project)
                def AptPath = project.buildDir.absolutePath + File.separator + "generated" + File.separator + "source" + File.separator + "apt"
                args << genAnnotationProcessorArgs(Constants.KEY_HOST_PACKAGE_NAME, applicationId)
                args << genAnnotationProcessorArgs(Constants.KEY_HOST_ALL_ASSETS_SOURCE, allMergedAssetsPaths)
                args << genAnnotationProcessorArgs(Constants.KEY_HOST_APT_PATH, AptPath)
            } else {
                args << genAnnotationProcessorArgs(Constants.KEY_LIB_BUNDLES_ASSETS_PATH, getBuildBundlesAssetsPath(variant, project))
            }
        }

    }

    /**
     * For HOST to gather .bro files together.
     * @param variant current build type
     * @param project gradle project object
     * @return all module's merged-assets folders
     */
    static String getAllMergedAssets(def variant, Project project) {
        StringBuilder result = new StringBuilder()
        String variantName = variant.name.capitalize()
        Task task = project.tasks["merge${variantName}Assets"]
        task.inputs.files.each {
            if (!it.absolutePath.contains("shaders")) {
                result.append(it.absolutePath).append(";")
            }
        }
//        task.outputs.files.each {
//            if (!it.absolutePath.contains("shaders")) {
//                result.append(it.absolutePath).append(";")
//            }
//        }
        return result.deleteCharAt(result.length() - 1).toString()
    }

    /**
     * The bundle path that will zip to an aar.
     * @param variant current build type
     * @param project gradle project object
     * @return the folder's absolutely path
     */
    static String getBuildBundlesAssetsPath(def variant, Project project) {
        String result = ""
        String variantName = variant.name.capitalize()
        Task packageAssetsTask = project.tasks["package${variantName}Assets"]
        if (packageAssetsTask == null || packageAssetsTask.outputs.files.size() == 0) {
            throw new IllegalStateException("")
        }

        packageAssetsTask.outputs.files.each {
            String path = it.absolutePath
            if (path != null && path.contains(File.separator + 'intermediates' + File.separator + 'packagedAssets' + File.separator)) {
                result = path
            }
        }

        return result
    }

    /**
     * Generate java compile arguments.
     * @param key
     * @param value
     * @return
     */
    static String genAnnotationProcessorArgs(String key, String value) {
        return "-A" + key + "=" + value
    }

}