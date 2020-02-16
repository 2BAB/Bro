package me.xx2bab.bro.compiler.collector

import com.alibaba.fastjson.JSON
import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.common.gen.anno.IBroAnnoProcessor
import me.xx2bab.bro.common.util.FileUtils
import me.xx2bab.bro.compiler.util.DummyClassCreator
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

/**
 * To collect meta data of every module with registered processor, and generate intermediates.
 *
 * @see IBroAnnoProcessor
 */
class SingleModuleCollector(private val processors: List<IBroAnnoProcessor>,
                            private val processingEnvironment: ProcessingEnvironment,
                            private val fileUtils: FileUtils,
                            private val moduleName: String,
                            private val libMetaDataOutputPath: String?, // Only valid on lib module
                            private val moduleBroBuildDir: String) {
    /**
     * A map that provides <ProcessorName></ProcessorName>, FormattedMetaDataString>
     */
    private val elements: MutableMap<String, MutableList<String>>

    val metaData: Map<String, MutableList<String>>
        get() = elements

    init {
        elements = HashMap()
        for (processor in processors) {
            elements[processor.javaClass.canonicalName] = ArrayList()
        }
    }

    fun addMetaRecord(element: Element, annoType: Class<out Annotation?>?) {
        val annotatedElementName = element.asType().toString()
        if (annotatedElementName == DummyClassCreator.canonicalName) {
            return
        }
        for (processor in processors) {
            if (!processor.getSupportedAnnotationTypes().contains(annoType)) {
                continue
            }
            val collectedRes = processor.onCollect(element, processingEnvironment)
            if (collectedRes == null || collectedRes.isEmpty()) {
                continue
            }
            elements[processor.javaClass.canonicalName]?.add(collectedRes)
        }
    }

    fun generate() {
        val fileName = (fileUtils.filterIllegalCharsForResFileName(moduleName)
                + Constants.MODULE_META_INFO_FILE_SUFFIX)
        val result = JSON.toJSONString(elements)
        // Write to libMetaDataOutputPath (/{modulePath}/build/intermediates/library_assets/{variantName}/out),
        // however because "javac" task is prior than "package assets" task,
        // and package task will override the output directory.
        // So this writing action is a temporary output, and only for the module that isn't compiled
        // by aar file (it means the lib is existed in the project with app module,
        // or you can call it monorepo).
        // Please check BroPlugin class in bro-gradle-plugin,
        // to see the hack for copying .bro file into libMetaDataOutputPath again.
        fileUtils.writeFile(result, libMetaDataOutputPath!!, fileName)
        // Write to moduleBroBuildDir (/{modulePath}/build/bro),
        // this is a backup, and we will copy from here to libMetaDataOutputPath later
        // Please check BroPlugin class in bro-gradle-plugin,
        // to see the hack for copying .bro file into libMetaDataOutputPath again.
        fileUtils.writeFile(result, moduleBroBuildDir, fileName)
    }

}