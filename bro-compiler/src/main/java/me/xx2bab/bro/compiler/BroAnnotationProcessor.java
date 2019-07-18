package me.xx2bab.bro.compiler;

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
import me.xx2bab.bro.compiler.collector.DefaultMultiModuleMetaDataCollector;
import me.xx2bab.bro.compiler.collector.DefaultSingleModuleMetaDataCollector;
import me.xx2bab.bro.compiler.collector.IMultiModuleMetaDataCollector;
import me.xx2bab.bro.compiler.collector.ISingleModuleMetaDataCollector;
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
 * @see DefaultSingleModuleMetaDataCollector
 * @see DefaultMultiModuleMetaDataCollector
 */
public class BroAnnotationProcessor extends AbstractProcessor {

    private final static List<Class<? extends Annotation>> supportedAnnotations;

    // Meta info of each module including app and libs
    private static final List<String> compilerArgumentForModule;
    // Meta info of the app module
    private static final List<String> compilerArgumentForApp;
    // Meta info of lib modules
    private static final List<String> compilerArgumentForLib;

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

        compilerArgumentForApp = new ArrayList<>();
        compilerArgumentForApp.add(Constants.ANNO_PROC_ARG_APP_PACKAGE_NAME);
        compilerArgumentForApp.add(Constants.ANNO_PROC_ARG_APP_META_DATA_FILES);
        compilerArgumentForApp.add(Constants.ANNO_PROC_ARG_APP_APT_PATH);

        compilerArgumentForLib = new ArrayList<>();
        compilerArgumentForLib.add(Constants.ANNO_PROC_ARG_LIB_BUNDLES_ASSETS_PATH);

        compileArgs = new LinkedHashSet<>();
        compileArgs.addAll(compilerArgumentForModule);
        compileArgs.addAll(compilerArgumentForApp);
        compileArgs.addAll(compilerArgumentForLib);
    }

    private String moduleName;
    private ModuleType moduleBuildType;
    private String moduleBroBuildDir;
    private String appPackageName;
    private String appAssetsSourcePaths;
    private String appAptGenPath;
    private String libBundlesAssetsPath;

    private ISingleModuleMetaDataCollector singleModuleMetaDataCollector;
    private IMultiModuleMetaDataCollector multiModuleMetaDataCollector;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        BroCompileLogger.setMessager(processingEnv.getMessager());
        BroCompileLogger.i("bro-compiler processor init");

        parseCompilerArguments();

        singleModuleMetaDataCollector = new DefaultSingleModuleMetaDataCollector(
                processingEnv.getTypeUtils(),
                processingEnv.getElementUtils(),
                processingEnv.getFiler(),
                moduleName,
                libBundlesAssetsPath);
        multiModuleMetaDataCollector = new DefaultMultiModuleMetaDataCollector(
                appPackageName,
                appAssetsSourcePaths,
                appAptGenPath
        );

        // If the application module doesn't expose anything,
        // the annotation processor will skip process(...) method below,
        // so we hack this situation here.
        if (moduleBuildType == ModuleType.APPLICATION) {
//            jsonFiles = new ArrayList<>();
//            MetaDataCollector.findModuleJsonFiles(jsonFiles, appAssetsSourcePaths);
//            Map<String, Map<String, BroProperties>> exposeMaps = createEmptyExposeMaps();
//            MetaDataCollector.collectOtherModulesMapFile(jsonFiles, exposeMaps);
//            MetaDataCollector.generateMergeMapFile(appPackageName, exposeMaps, filer, null, moduleBroBuildDir);
            multiModuleMetaDataCollector.generateEntireMetaDataTable();
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        BroCompileLogger.i("bro-compiler processor is processing");

        for (int i = 0; i < supportedAnnotations.size(); i++) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(supportedAnnotations.get(i))) {
                singleModuleMetaDataCollector.addMetaRecord(element);
            }
        }

        if (moduleBuildType == ModuleType.APPLICATION) {
//                File existFile = MetaDataCollector.findCurrentAptGenFolder(Constants.ROUTING_TABLE_FILE_NAME + ".java", new File(appAptGenPath));
//                MetaDataCollector.collectOtherModulesMapFile(jsonFiles, exposeMaps);
//                MetaDataCollector.generateMergeMapFile(appPackageName, exposeMaps, filer, existFile, moduleBroBuildDir);
            multiModuleMetaDataCollector.generateEntireMetaDataTable();
        } else {
            singleModuleMetaDataCollector.generateMetaDataFile();
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
            } else if (Constants.ANNO_PROC_ARG_APP_META_DATA_FILES.equals(key)) {
                this.appAssetsSourcePaths = map.get(key);
            } else if (Constants.ANNO_PROC_ARG_APP_APT_PATH.equals(key)) {
                this.appAptGenPath = map.get(key);
            } else if (Constants.ANNO_PROC_ARG_LIB_BUNDLES_ASSETS_PATH.equals(key)) {
                this.libBundlesAssetsPath = map.get(key);
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

    private void checkNotEmpty(Map<String, String> map, String key) {
        if (!map.containsKey(key) || processingEnv.getOptions().get(key).isEmpty()) {
            throw new IllegalArgumentException("Compiler argument " + key + "is empty! " +
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
