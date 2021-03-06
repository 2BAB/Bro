package me.xx2bab.bro.gradle.anno

import com.alibaba.fastjson.JSON
import com.android.build.gradle.api.BaseVariant
import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.common.ModuleType
import me.xx2bab.bro.gradle.processor.BroApiInterfaceAndAliasMapAnnoProcessor
import me.xx2bab.bro.gradle.processor.BroRoutingTableAnnoProcessor
import me.xx2bab.bro.gradle.util.BuildUtils
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import java.io.File
import java.net.URLClassLoader

abstract class AnnoProcessorOptionInjector {

    fun inject(project: Project,
               variants: DomainObjectSet<out BaseVariant>) {
        variants.all { variant ->
            val moduleName = project.name
            val buildDir = BuildUtils.getBroBuildPath(project)
            val map = mutableMapOf<String, String>()

            // common args
            map[Constants.ANNO_PROC_ARG_MODULE_NAME] = moduleName
            map[Constants.ANNO_PROC_ARG_MODULE_BUILD_TYPE] = moduleType().name
            map[Constants.ANNO_PROC_ARG_MODULE_BUILD_DIR] = buildDir
            map[Constants.ANNO_PROC_ARG_MODULE_PROCESSOR_CLASSES] = getGeneratorClasses()
            map[Constants.ANNO_PROC_ARG_MODULE_GENERATOR_CLASSPATHS] =
                    getClassPathsForGenerator(javaClass.classLoader)

            // Specific args by different module type
            val moduleMap = onModuleSpecificInjection(project, variant)
            map.putAll(moduleMap)
            exportOptionsJsonFile(project, map, buildDir)

//            addAndroidAptOptions(variant, map)
//            addKaptAptOptions(project, map)
        }
    }

//
//    private fun addAndroidAptOptions(variant: BaseVariant, map: Map<String, String>) {
//        val args = variant.javaCompileOptions.annotationProcessorOptions.arguments
//        for ((key, value) in map) {
//            args[key] = value
//        }
//    }
//
//    private fun addKaptAptOptions(project: Project, map: Map<String, String>) {
//        val kaptExt = project.extensions.findByType(KaptExtension::class.java)
//        kaptExt?.arguments{
//            for ((key, value) in map) {
//                arg(key, value)
//            }
//        }
//    }

    private fun exportOptionsJsonFile(project: Project,
                                      map: Map<String, String>,
                                      broBuildDir: String) =
            project.tasks.getByPath("preBuild").doLast {
                File(broBuildDir, Constants.MODULE_COMPILER_OPTIONS_FILE).bufferedWriter().use {
                    it.write(JSON.toJSONString(map))
                }
            }


    abstract fun moduleType(): ModuleType

    abstract fun onModuleSpecificInjection(project: Project,
                                           variant: BaseVariant): Map<String, String>


    private fun getClassPathsForGenerator(appCl: ClassLoader?): String {
        val jarBroGradlePlugin = "bro-gradle-plugin-"
        val jarBuildSrc = "buildSrc.jar"

        var appClassLoader = appCl
        val list = mutableListOf<String>()
        while (appClassLoader != null) {
            if (appClassLoader is URLClassLoader) {
                for (url in appClassLoader.urLs) {
                    val urlString = url.toString()
                    if (urlString.contains(jarBroGradlePlugin)
                            || urlString.contains(jarBuildSrc)) {
                        list.add(urlString)
                    }
                }
            }
            appClassLoader = appClassLoader.parent
        }
        return list.joinToString(",")
    }

    private fun getGeneratorClasses(): String {
        val classes = arrayOf(BroRoutingTableAnnoProcessor::class.qualifiedName,
                BroApiInterfaceAndAliasMapAnnoProcessor::class.qualifiedName)
        return classes.joinToString(",")
    }
}