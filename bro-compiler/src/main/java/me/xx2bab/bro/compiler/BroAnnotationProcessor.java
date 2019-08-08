package me.xx2bab.bro.compiler;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import me.xx2bab.bro.annotations.BroActivity;
import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.annotations.BroModule;
import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.common.ModuleType;
import me.xx2bab.bro.common.gen.GenOutputs;
import me.xx2bab.bro.common.gen.anno.IBroAnnoProcessor;
import me.xx2bab.bro.common.util.FileUtils;
import me.xx2bab.bro.compiler.classloader.GradleClassLoader;
import me.xx2bab.bro.compiler.collector.MultiModuleCollector;
import me.xx2bab.bro.compiler.collector.SingleModuleCollector;
import me.xx2bab.bro.compiler.util.BroCompileLogger;

/**
 * An "abstract" annotation processor, which won't be used to generate the final routing table
 * or any specific artifacts. Instead, it collects and generates some intermediates like:
 * <p>
 * 1. A json file that listing all annotating elements for each module as exposed meta data
 * 2. A collection json file for entire app that gathers meta data from all json files above
 * <p>
 * It focuses on working with input arguments like compiler options, supported annotations.
 * For collection tasks, check these two collectors below:
 *
 * @see SingleModuleCollector
 * @see MultiModuleCollector
 */
public class BroAnnotationProcessor extends AbstractProcessor {

    private FileUtils fileUtils;
    private final static List<Class<? extends Annotation>> supportedAnnotations;

    // Compiler arguments for each module including Application and Library
    private static final List<String> compilerArgumentForModule;
    // Compiler arguments for the Application module only
    private static final List<String> compilerArgumentForApp;
    // Compiler arguments for the Library module only
    private static final List<String> compilerArgumentForLib;
    // Combination of Compiler Arguments
    private static final Set<String> compileArgs;

    static {
        supportedAnnotations = new ArrayList<>();
        supportedAnnotations.add(BroActivity.class);
        supportedAnnotations.add(BroApi.class);
        supportedAnnotations.add(BroModule.class);

        compilerArgumentForModule = new ArrayList<>();
        compilerArgumentForModule.add(Constants.ANNO_PROC_ARG_MODULE_NAME);
        compilerArgumentForModule.add(Constants.ANNO_PROC_ARG_MODULE_BUILD_TYPE);
        compilerArgumentForModule.add(Constants.ANNO_PROC_ARG_MODULE_BUILD_DIR);
        compilerArgumentForModule.add(Constants.ANNO_PROC_ARG_MODULE_PROCESSOR_CLASSES);
        compilerArgumentForModule.add(Constants.ANNO_PROC_ARG_MODULE_GENERATOR_CLASSLOADERS);

        compilerArgumentForApp = new ArrayList<>();
        compilerArgumentForApp.add(Constants.ANNO_PROC_ARG_APP_PACKAGE_NAME);
        compilerArgumentForApp.add(Constants.ANNO_PROC_ARG_APP_META_DATA_INPUT_PATH);
        compilerArgumentForApp.add(Constants.ANNO_PROC_ARG_APP_APT_PATH);

        compilerArgumentForLib = new ArrayList<>();
        compilerArgumentForLib.add(Constants.ANNO_PROC_ARG_LIB_META_DATA_OUTPUT_PATH);

        compileArgs = new LinkedHashSet<>();
        compileArgs.addAll(compilerArgumentForModule);
        compileArgs.addAll(compilerArgumentForApp);
        compileArgs.addAll(compilerArgumentForLib);
    }

    private String moduleName;
    private ModuleType moduleBuildType;
    private String moduleBroBuildDir;
    private String moduleProcessorClassLoaders;
    private String appPackageName;
    private String appMetaDataInputPath;
    private String appAptGenPath;
    private String appGeneratorClasses;
    private String libMetaDataOutputPath;

    private GenOutputs genOutputs;

    private SingleModuleCollector singleModuleCollector;
    private MultiModuleCollector multiModuleCollector;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        BroCompileLogger.setMessager(processingEnv.getMessager());
        BroCompileLogger.i("bro-compiler processor init");
        fileUtils = FileUtils.getDefault();

        parseCompilerArguments();
        List<IBroAnnoProcessor> processors = getProcessors();

        singleModuleCollector = new SingleModuleCollector(
                processors,
                processingEnv,
                fileUtils,
                moduleName,
                libMetaDataOutputPath);

        // If the application module doesn't have any annotations that we support
        // the annotation processor will skip process(...) method below,
        // so we hack this kind of case here.
        if (moduleBuildType == ModuleType.APPLICATION) {
            genOutputs = new GenOutputs();
            genOutputs.appPackageName = appPackageName;
            genOutputs.appAptGenDirectory = new File(appAptGenPath);
            genOutputs.broBuildDirectory = new File(moduleBroBuildDir);
            multiModuleCollector = new MultiModuleCollector(
                    processors,
                    processingEnv,
                    fileUtils,
                    genOutputs);
            multiModuleCollector.load(appMetaDataInputPath);
            multiModuleCollector.generate();
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        BroCompileLogger.i("bro-compiler processor is processing");

        for (int i = 0; i < supportedAnnotations.size(); i++) {
            for (Element element : roundEnv.getElementsAnnotatedWith(supportedAnnotations.get(i))) {
                singleModuleCollector.addMetaRecord(element);
            }
        }

        if (moduleBuildType == ModuleType.APPLICATION) {
            multiModuleCollector.load(appMetaDataInputPath);
            multiModuleCollector.addMetaRecord(singleModuleCollector.getMetaData());
            multiModuleCollector.generate();
        } else {
            singleModuleCollector.generate();
        }

        return true;
    }


    private void parseCompilerArguments() {
        final Map<String, String> map = processingEnv.getOptions();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (Constants.ANNO_PROC_ARG_MODULE_NAME.equals(key)) {
                this.moduleName = map.get(key);
            } else if (Constants.ANNO_PROC_ARG_MODULE_BUILD_TYPE.equals(key)) {
                this.moduleBuildType = ModuleType.valueOf(map.get(key));
            } else if (Constants.ANNO_PROC_ARG_MODULE_BUILD_DIR.equals(key)) {
                this.moduleBroBuildDir = map.get(key);
            } else if (Constants.ANNO_PROC_ARG_APP_PACKAGE_NAME.equals(key)) {
                this.appPackageName = map.get(key);
            } else if (Constants.ANNO_PROC_ARG_APP_META_DATA_INPUT_PATH.equals(key)) {
                this.appMetaDataInputPath = map.get(key);
            } else if (Constants.ANNO_PROC_ARG_APP_APT_PATH.equals(key)) {
                this.appAptGenPath = map.get(key);
            } else if (Constants.ANNO_PROC_ARG_MODULE_PROCESSOR_CLASSES.equals(key)) {
                this.appGeneratorClasses = map.get(key);
            } else if (Constants.ANNO_PROC_ARG_MODULE_GENERATOR_CLASSLOADERS.equals(key)) {
                this.moduleProcessorClassLoaders = map.get(key);
            } else if (Constants.ANNO_PROC_ARG_LIB_META_DATA_OUTPUT_PATH.equals(key)) {
                this.libMetaDataOutputPath = map.get(key);
            }
            BroCompileLogger.i("CompilerArguments: " + key + " = " + map.get(key));
        }

        // Empty check to ensure we can use them safely
        compilerArgumentForModule.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                checkNotEmpty(map, s);
            }
        });
        if (moduleBuildType == ModuleType.APPLICATION) {
            compilerArgumentForApp.forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    checkNotEmpty(map, s);
                }
            });
        } else { // branch for library
            compilerArgumentForLib.forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    checkNotEmpty(map, s);
                }
            });
        }
    }


    @SuppressWarnings("unchecked")
    private List<IBroAnnoProcessor> getProcessors() {
        List<IBroAnnoProcessor> res = new ArrayList<>();
        GradleClassLoader gradleClassLoader = new GradleClassLoader(
                moduleProcessorClassLoaders.split(","));
        String[] generatorClasses = appGeneratorClasses.split(",");
        for (String clz : generatorClasses) {
            try {
                IBroAnnoProcessor processor =
                        (IBroAnnoProcessor) gradleClassLoader.load(clz).newInstance();
                res.add(processor);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Bro generator " + clz + "can not be found.");
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Bro generator " + clz + "can not be initialized.");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Bro generator " + clz + "can not be initialized.");
            } catch (ClassCastException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Bro generator " + clz + "can not be casted " +
                        "to IBroAnnoProcessor");
            }
        }
        return res;
    }

    private void checkNotEmpty(Map<String, String> map, String key) {
        if (!map.containsKey(key) || processingEnv.getOptions().get(key).isEmpty()) {
            throw new IllegalArgumentException("Compiler argument " + key + " is empty! " +
                    "Please check bro-gradle-plugin is applied correctly");
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> clazz : supportedAnnotations) {
            types.add(clazz.getCanonicalName());
        }
        return types;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return compileArgs;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


}
