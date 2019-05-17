package me.xx2bab.bro.compiler;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import me.xx2bab.bro.annotations.BroActivity;
import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.annotations.BroModule;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.compiler.generator.CodeGenerator;
import me.xx2bab.bro.compiler.util.BroCompileLogger;

//@AutoService(Processor.class)
public class BroCompilerProcessor extends AbstractProcessor {

    private static List<Class<? extends Annotation>> supportedAnnotations;

    static {
        supportedAnnotations = new ArrayList<>();
        supportedAnnotations.add(BroActivity.class);
        supportedAnnotations.add(BroApi.class);
        supportedAnnotations.add(BroModule.class);
    }

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private String moduleName;
    private String moduleBroBuildDir;
    private String hostPackageName;
    private String hostAllAssetsSourcePaths;
    private String libBundlesAssetsPath;
    private String hostAptPath;
    private ArrayList<String> jsonFiles;
    private String moduleBuildType;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        BroCompileLogger.setMessager(processingEnv.getMessager());

        BroCompileLogger.i("bro-compiler processor init");

        Map<String, String> map = processingEnv.getOptions();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (Constants.KEY_MODULE_NAME.equals(key)) {
                this.moduleName = map.get(key);
            } else if (Constants.KEY_MODULE_BUILD_TYPE.equals(key)) {
                this.moduleBuildType = map.get(key);
            } else if (Constants.KEY_MODULE_BUILD_DIR.equals(key)) {
                this.moduleBroBuildDir = map.get(key);
            } else if (Constants.KEY_HOST_PACKAGE_NAME.equals(key)) {
                this.hostPackageName = map.get(key);
            } else if (Constants.KEY_HOST_ALL_ASSETS_SOURCE.equals(key)) {
                this.hostAllAssetsSourcePaths = map.get(key);
            } else if (Constants.KEY_HOST_APT_PATH.equals(key)) {
                this.hostAptPath = map.get(key);
            } else if (Constants.KEY_LIB_BUNDLES_ASSETS_PATH.equals(key)) {
                this.libBundlesAssetsPath = map.get(key);
            }

            BroCompileLogger.i("EnvOption - " + key + " = " + map.get(key));
        }

        if (moduleBuildType.equals("Application")) {
            if (hostAllAssetsSourcePaths == null) {
                throw new NullPointerException("Folder of assets not found, please check bro-gradle-plugin is applied correctly.");
            }

            jsonFiles = new ArrayList<>();
            CodeGenerator.findModuleJsonFiles(jsonFiles, hostAllAssetsSourcePaths);

            // If the host expose nothing, the processor will not process the file-generation,
            // so hack this situation here.
            Map<String, Map<String, BroProperties>> exposeMaps = createEmptyExposeMaps();
            CodeGenerator.collectOtherModulesMapFile(jsonFiles, exposeMaps);
            CodeGenerator.generateMergeMapFile(hostPackageName, exposeMaps, filer, null, moduleBroBuildDir);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (moduleBuildType.equals("Plugadget")) {
            return true;
        }

        BroCompileLogger.i("bro-compiler processor is processing");

        Map<String, Map<String, BroProperties>> exposeMaps = createEmptyExposeMaps();

        for (int i = 0; i < supportedAnnotations.size(); i++) {
            Map<String, BroProperties> exposeMap = new HashMap<>();
            for (Element element : roundEnvironment.getElementsAnnotatedWith(supportedAnnotations.get(i))) {
                TypeElement typeElement = (TypeElement) element;
                String packageName = typeElement.getQualifiedName().toString();
                String nick = parseNick(element);
                String extraParams = parseExtraParams(element, nick, supportedAnnotations.get(i).getSimpleName());
                exposeMap.put(nick, new BroProperties(packageName, extraParams));
            }
            exposeMaps.put(supportedAnnotations.get(i).getSimpleName(), exposeMap);
        }

        boolean sthHasBeenExposed = false;
        for (Map.Entry<String, Map<String, BroProperties>> entry : exposeMaps.entrySet()) {
            if (entry.getValue().size() > 0) {
                sthHasBeenExposed = true;
                break;
            }
        }

        if (sthHasBeenExposed) {
            if (moduleBuildType.equals("Application")) { // merge all modules map file (json)
                File existFile = CodeGenerator.findCurrentAptGenFolder(Constants.MERGED_MAP_FILE_NAME + ".java", new File(hostAptPath));
                CodeGenerator.collectOtherModulesMapFile(jsonFiles, exposeMaps);
                CodeGenerator.generateMergeMapFile(hostPackageName, exposeMaps, filer, existFile, moduleBroBuildDir);
            } else {
                CodeGenerator.generateModuleMapToJson(moduleName, libBundlesAssetsPath, exposeMaps);
            }
        }

        return true;
    }

    private String parseNick(Element element) {
        List<? extends AnnotationMirror> list = element.getAnnotationMirrors();
        for (int i = 0; i < list.size(); i++) {
            String annotationType = list.get(i).getAnnotationType().toString();
            if (annotationType.contains("me.xx2bab.bro.annotations")) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> map = list.get(i).getElementValues();
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : map.entrySet()) {
                    if (entry.getKey().toString().equals("value()")) {
                        return entry.getValue().toString().replace("\"", "");
                    }
                }
            }
        }
        return null;
    }

    private String parseExtraParams(Element element, String nick, String type) {
        JSONObject jsonObject = new JSONObject();
        List<? extends AnnotationMirror> list = element.getAnnotationMirrors();

        for (int i = 0; i < list.size(); i++) {
            String annotationType = list.get(i).getAnnotationType().toString();
            if (annotationType.contains("me.xx2bab.bro.annotations")) {
                continue;
            }

            String value = "";
            Map<? extends ExecutableElement, ? extends AnnotationValue> map = list.get(i).getElementValues();
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : map.entrySet()) {
                if (entry.getKey().toString().equals("value()")) {
                    value = entry.getValue().toString().replace("\"", "");
                }
            }

            jsonObject.put(annotationType, value);
        }

        if (type.equals(BroApi.class.getSimpleName())) {
            String ApiInterface = parseApiInterface(element);
            if (ApiInterface == null) {
                throw new IllegalStateException(nick + ": Bro Api Must implements the interface which extends from IBroApi!");
            }
            jsonObject.put("ApiInterface", ApiInterface);
        }

        return jsonObject.toString();
    }

    private String parseApiInterface(Element element) {
        try {
            TypeElement typeElement = (TypeElement) element;
            for (TypeMirror mirror : typeElement.getInterfaces()) {
                String result = parseApiInterfaceInternal(mirror.toString());
                if (result != null) {
                    return mirror.toString();
                }
            }
            return null;
        } catch (Exception e) {
            BroCompileLogger.e(e.getMessage());
            return null;
        }
    }

    private String parseApiInterfaceInternal(String interfaceCanonicalName) {
        TypeElement typeElement = elementUtils.getTypeElement(interfaceCanonicalName);
        if (typeElement != null && typeElement.getInterfaces().size() > 0) {
            List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
            for (TypeMirror childInterface : interfaces) {
                if (childInterface.toString().equals(IBroApi.class.getCanonicalName())) {
                    return typeElement.toString();
                }
                String childResult = parseApiInterfaceInternal(childInterface.toString());
                if (childResult != null) {
                    return childResult;
                }
            }
        }
        return null;
    }

    private Map<String, Map<String, BroProperties>> createEmptyExposeMaps() {
        Map<String, Map<String, BroProperties>> exposeMaps = new HashMap<>();
        for (Class<? extends Annotation> clazz : supportedAnnotations) {
            exposeMaps.put(clazz.getSimpleName(), new HashMap<String, BroProperties>());
        }
        return exposeMaps;
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
        Set<String> compileArgs = new LinkedHashSet<>();
        compileArgs.add(Constants.KEY_MODULE_NAME);
        compileArgs.add(Constants.KEY_MODULE_BUILD_TYPE);
        compileArgs.add(Constants.KEY_MODULE_BUILD_DIR);
        compileArgs.add(Constants.KEY_HOST_PACKAGE_NAME);
        compileArgs.add(Constants.KEY_HOST_ALL_ASSETS_SOURCE);
        compileArgs.add(Constants.KEY_HOST_APT_PATH);
        compileArgs.add(Constants.KEY_LIB_BUNDLES_ASSETS_PATH);
        return compileArgs;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


}
