package me.xx2bab.bro.compiler

import com.alibaba.fastjson.JSONObject
import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.common.ModuleType
import me.xx2bab.bro.common.gen.GenOutputs
import me.xx2bab.bro.common.gen.anno.IBroAnnoProcessor
import me.xx2bab.bro.common.util.FileUtils
import me.xx2bab.bro.compiler.classloader.GradleClassLoader
import me.xx2bab.bro.compiler.collector.MultiModuleCollector
import me.xx2bab.bro.compiler.collector.SingleModuleCollector
import me.xx2bab.bro.compiler.util.AptGenLocationProvider
import me.xx2bab.bro.compiler.util.BroCompileLogger.i
import me.xx2bab.bro.compiler.util.BroCompileLogger.setMessager
import me.xx2bab.bro.compiler.util.DummyClassCreator
import java.io.File
import java.io.IOException
import java.util.*
import java.util.function.Consumer
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import kotlin.collections.HashSet


/**
 * An "abstract" annotation processor, which won't be used to generate the final routing table
 * or any specific artifacts. Instead, it collects and generates some intermediates like:
 *
 *
 * 1. A json file that listing all annotating elements for each module as exposed meta data
 * 2. A collection json file for entire app that gathers meta data from all json files above
 *
 *
 * It focuses on working with input arguments like compiler options, supported annotations.
 * For collection tasks, check these two collectors below:
 *
 * @see SingleModuleCollector
 *
 * @see MultiModuleCollector
 */
class BroAnnotationProcessor : AbstractProcessor() {

    companion object {
        // Compiler arguments for each module including Application and Library
        private val compilerArgsForModule: MutableSet<String> = HashSet()
        // Compiler arguments for the Application module only
        private var compilerArgsForApp: MutableSet<String> = HashSet()
        // Compiler arguments for the Library module only
        private var compilerArgsForLib: MutableSet<String> = HashSet()
        // Combination of Compiler Arguments
        private var compileArgs: MutableSet<String> = LinkedHashSet()

        init {
            compilerArgsForModule.add(Constants.ANNO_PROC_ARG_MODULE_NAME)
            compilerArgsForModule.add(Constants.ANNO_PROC_ARG_MODULE_BUILD_TYPE)
            compilerArgsForModule.add(Constants.ANNO_PROC_ARG_MODULE_BUILD_DIR)
            compilerArgsForModule.add(Constants.ANNO_PROC_ARG_MODULE_PROCESSOR_CLASSES)
            compilerArgsForModule.add(Constants.ANNO_PROC_ARG_MODULE_GENERATOR_CLASSPATHS)

            compilerArgsForApp.add(Constants.ANNO_PROC_ARG_APP_PACKAGE_NAME)
            compilerArgsForApp.add(Constants.ANNO_PROC_ARG_APP_META_DATA_INPUT_PATH)
            compilerArgsForApp.add(Constants.ANNO_PROC_ARG_APP_APT_PATH)

            compilerArgsForLib.add(Constants.ANNO_PROC_ARG_LIB_META_DATA_OUTPUT_PATH)

            compileArgs.addAll(compilerArgsForModule)
            compileArgs.addAll(compilerArgsForApp)
            compileArgs.addAll(compilerArgsForLib)
        }
    }


    private val supportedAnnotations = HashSet<Class<out Annotation>>()
    private var moduleName: String? = null
    private var moduleBuildType: ModuleType? = null
    private var moduleBroBuildDir: String? = null
    private var moduleProcessorClassPaths: String? = null
    private var appPackageName: String? = null
    private var appMetaDataInputPath: String? = null
    private var appAptGenPath: String? = null
    private var appGeneratorClasses: String? = null
    private var libMetaDataOutputPath: String? = null

    private lateinit var aptAptGenLocationProvider: AptGenLocationProvider
    private lateinit var genOutputs: GenOutputs
    private lateinit var singleModuleCollector: SingleModuleCollector
    private lateinit var multiModuleCollector: MultiModuleCollector

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)

        setMessager(processingEnv.messager)
        i("bro-compiler processor init")

        aptAptGenLocationProvider = AptGenLocationProvider(processingEnv)
        parseCompilerArguments()
        val processors = processors
        for (processor in processors) {
            supportedAnnotations.addAll(processor.getSupportedAnnotationTypes())
        }

        singleModuleCollector = SingleModuleCollector(
                processors,
                processingEnv,
                FileUtils.default,
                moduleName!!,
                libMetaDataOutputPath,
                moduleBroBuildDir!!)

        if (moduleBuildType == ModuleType.APPLICATION) {
            genOutputs = GenOutputs(appPackageName!!, File(appAptGenPath!!), File(moduleBroBuildDir!!))
            multiModuleCollector = MultiModuleCollector(
                    processors,
                    processingEnv,
                    FileUtils.default,
                    genOutputs)
            // If the application module doesn't have any annotations that we support
            // the annotation processor will skip process(...) method below,
            // so we hack this scenario in the gradle plugin by adding a dummy class with @BroModule.
            // Previously, we add a duplicated generating process here, but it throws file-recreating
            // error and consumes times, so we aborted.
            // multiModuleCollector.load(appMetaDataInputPath);
            // multiModuleCollector.generate();

            DummyClassCreator.create(processingEnv.filer, aptAptGenLocationProvider)
        }
    }

    override fun process(set: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        i("bro-compiler processor is processing")
        var foundSupportedAnnotations = false
        for (curAnno in supportedAnnotations) {
            for (element in roundEnv.getElementsAnnotatedWith(curAnno)) {
                singleModuleCollector.addMetaRecord(element, curAnno)
                foundSupportedAnnotations = true
            }
        }
        if (!foundSupportedAnnotations) {
            return true
        }
        if (moduleBuildType == ModuleType.APPLICATION) {
            multiModuleCollector.load(appMetaDataInputPath!!)
            multiModuleCollector.addMetaRecord(singleModuleCollector.metaData)
            multiModuleCollector.generate()
        } else {
            singleModuleCollector.generate()
        }
        return true
    }


    // TODO: refactor me
    @get:Throws(IOException::class)
    private val annotationProcessorOptionsFile: File?
        get() {
            var dummyOutputDir = aptAptGenLocationProvider.get()
            while (dummyOutputDir.parentFile != null) {
                dummyOutputDir = dummyOutputDir.parentFile
                if (dummyOutputDir.name == "build") {
                    return File(dummyOutputDir.absolutePath
                            + File.separator + "bro"
                            + File.separator + Constants.MODULE_COMPILER_OPTIONS_FILE)
                }
            }
            return null
        }

    private fun parseCompilerArguments() {
        val map: Map<String, String> = try {
            val jsonFile = annotationProcessorOptionsFile
            require(!(jsonFile == null || !jsonFile.exists())) { "Error when call parseCompilerArguments()" }
            JSONObject.parseObject<Map<*, *>>(FileUtils.default.readFile(jsonFile),
                    MutableMap::class.java) as Map<String, String>
        } catch (e: IOException) {
            throw IllegalArgumentException(e.message)
        }
        val keys = map.keys
        for (key in keys) {
            if (Constants.ANNO_PROC_ARG_MODULE_NAME == key) {
                moduleName = map[key]
            } else if (Constants.ANNO_PROC_ARG_MODULE_BUILD_TYPE == key) {
                moduleBuildType = ModuleType.valueOf(map[key]!!)
            } else if (Constants.ANNO_PROC_ARG_MODULE_BUILD_DIR == key) {
                moduleBroBuildDir = map[key]
            } else if (Constants.ANNO_PROC_ARG_APP_PACKAGE_NAME == key) {
                appPackageName = map[key]
            } else if (Constants.ANNO_PROC_ARG_APP_META_DATA_INPUT_PATH == key) {
                appMetaDataInputPath = map[key]
            } else if (Constants.ANNO_PROC_ARG_APP_APT_PATH == key) {
                appAptGenPath = map[key]
            } else if (Constants.ANNO_PROC_ARG_MODULE_PROCESSOR_CLASSES == key) {
                appGeneratorClasses = map[key]
            } else if (Constants.ANNO_PROC_ARG_MODULE_GENERATOR_CLASSPATHS == key) {
                moduleProcessorClassPaths = map[key]
            } else if (Constants.ANNO_PROC_ARG_LIB_META_DATA_OUTPUT_PATH == key) {
                libMetaDataOutputPath = map[key]
            }
            i("CompilerArguments: " + key + " = " + map[key])
        }
        // Empty check to ensure we can use them safely
        compilerArgsForModule.forEach(Consumer { s -> checkNotEmpty(map, s) })
        if (moduleBuildType == ModuleType.APPLICATION) {
            compilerArgsForApp.forEach(Consumer { s -> checkNotEmpty(map, s) })
        } else { // branch for library
            compilerArgsForLib.forEach(Consumer { s -> checkNotEmpty(map, s) })
        }
    }

    private val processors: List<IBroAnnoProcessor>
        get() {
            val res: MutableList<IBroAnnoProcessor> = ArrayList()
            val gradleClassLoader = GradleClassLoader(
                    moduleProcessorClassPaths!!.split(",").toTypedArray())
            val generatorClasses = appGeneratorClasses!!.split(",").toTypedArray()
            for (clz in generatorClasses) {
                try {
                    val processor = gradleClassLoader.load(clz).newInstance() as IBroAnnoProcessor
                    res.add(processor)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                    throw IllegalArgumentException("Bro generator " + clz + "can not be found.")
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                    throw IllegalArgumentException("Bro generator " + clz + "can not be initialized.")
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                    throw IllegalArgumentException("Bro generator " + clz + "can not be initialized.")
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                    throw IllegalArgumentException("Bro generator " + clz + "can not be casted " +
                            "to IBroAnnoProcessor")
                }
            }
            return res
        }

    private fun checkNotEmpty(map: Map<String, String>, key: String) {
        require(!(!map.containsKey(key) || map.getValue(key).isEmpty())) {
            "Compiler argument " + key + " is empty! " +
                    "Please check bro-gradle-plugin is applied correctly"
        }
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        val types: MutableSet<String> = LinkedHashSet()
        for (clazz in supportedAnnotations) {
            types.add(clazz.canonicalName)
        }
        return types
    }

    override fun getSupportedOptions(): Set<String> {
        return compileArgs
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }
}