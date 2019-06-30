package me.xx2bab.bro.compiler.generator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.CommonUtils;
import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.common.IBroRoutingTable;
import me.xx2bab.bro.compiler.util.BroCompileLogger;
import me.xx2bab.bro.compiler.util.FileUtil;

public class CodeGenerator {

    /**
     * Step 1 :
     * <p>
     * Generate map to json (temp) in each module's raw folder
     *
     * @param moduleName        module name for generated-file name
     * @param moduleBroBuildDir build path for generate file
     * @param exposeMaps        exposeMaps
     */
    public static void generateModuleMapToJson(String moduleName, String moduleBroBuildDir,
                                               Map<String, Map<String, BroProperties>> exposeMaps) {
        String fileName = CommonUtils.filterIllegalCharsForRawFileName(moduleName) + Constants.MODULE_MAP_JSON_SUFFIX;

        JSONObject mapObj = new JSONObject();

        for (Map.Entry<String, Map<String, BroProperties>> entry : exposeMaps.entrySet()) {
            JSONObject childJsonObj = new JSONObject();
            for (Map.Entry<String, BroProperties> childEntry : entry.getValue().entrySet()) {
                childJsonObj.put(childEntry.getKey(), childEntry.getValue().toJsonString());
            }
            mapObj.put(entry.getKey(), childJsonObj);
        }

        //CommonUtils.clean(new File(moduleRawPath));
        FileUtil.writeFile(mapObj.toJSONString(), moduleBroBuildDir, fileName);
    }

    /**
     * Step 2 :
     * <p>
     * Gather all modules' map file
     *
     * @param jsonFileList map files of all modules expect host
     * @param exposeMaps   maintain all map relation
     */
    public static void collectOtherModulesMapFile(ArrayList<String> jsonFileList,
                                                  Map<String, Map<String, BroProperties>> exposeMaps) {
        try {
            for (String filePath : jsonFileList) {
                File file = new File(filePath);
                if (!file.exists()) {
                    continue;
                }
                String jsonString = FileUtil.readFile(file);
                JSONObject moduleJson = JSON.parseObject(jsonString);
                if (moduleJson == null) {
                    continue;
                }
                for (Map.Entry<String, Object> entry : moduleJson.entrySet()) {
                    JSONObject childObj = (JSONObject) entry.getValue();
                    Map<String, BroProperties> exposeMap = exposeMaps.get(entry.getKey());
                    if (exposeMap != null) {
                        for (Map.Entry<String, Object> childEntry : childObj.entrySet()) {
                            preCheckBeforeCollect(exposeMap, childEntry.getKey(), entry.getKey());
                            BroProperties broProperties = new BroProperties();
                            broProperties.fromJsonString(childEntry.getValue().toString());
                            exposeMap.put(childEntry.getKey(), broProperties);
                        }
                    }
                }
            }
        } catch (DuplicatedNickException e) {
            BroCompileLogger.e(e.getMessage());
        }
    }


    /**
     * Step 3 :
     * <p>
     * Generate all map file with temp-json by other module in app-module's build path
     *
     * @param packageName package name for generating host file
     * @param filer       filer tools
     * @param existFile   exist file generated by processor.init()
     */
    public static void generateMergeMapFile(String packageName,
                                            Map<String, Map<String, BroProperties>> exposeMaps,
                                            Filer filer, File existFile,
                                            String rootProjectPath) {
        try {
            TypeSpec.Builder builder = TypeSpec.classBuilder(Constants.MERGED_MAP_FILE_NAME)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(IBroRoutingTable.class);

            for (Map.Entry<String, Map<String, BroProperties>> entry : exposeMaps.entrySet()) {
                builder.addMethod(generateMapMethod("get" + entry.getKey() + "Map", entry.getValue()));
            }

            JavaFile file = JavaFile.builder(Constants.MERGED_MAP_PACKAGE_NAME, builder.build())
                    .addFileComment("Generated by Bro.").build();

            if (existFile == null) {
                file.writeTo(filer); // standard output
            } else { // hack file override exception
                File folder = existFile.getParentFile();
                int times = file.toJavaFileObject().toUri().toString().split("/").length;
                for (int i = 1; i < times; i++) {
                    folder = folder.getParentFile();
                }

                file.writeTo(folder);
            }

            DocGenerator.generateDoc(rootProjectPath, exposeMaps);
        } catch (Exception e) {
            BroCompileLogger.e(e.getMessage());
        }
    }

    private static MethodSpec generateMapMethod(String methodName, Map<String, BroProperties> map) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(HashMap.class, String.class, BroProperties.class))
                .addStatement("HashMap<String, BroProperties> map = new HashMap<String, BroProperties>()");

        // keep the order
        Map<String, BroProperties> orderMap = new TreeMap<>(map);
        Set<Map.Entry<String, BroProperties>> set = orderMap.entrySet();
        for (Map.Entry<String, BroProperties> entry : set) {
            builder.addStatement("map.put(\"" + entry.getKey() + "\", new BroProperties(\""
                    + entry.getValue().clazz + "\", $S" + "))", entry.getValue().extraParams);
        }
        builder.addStatement("return map");
        return builder.build();
    }

    /**
     * Avoid duplicate nick.
     *
     * @param map
     * @param key
     * @param errorType
     * @throws DuplicatedNickException
     */
    private static void preCheckBeforeCollect(Map<String, BroProperties> map, String key, String errorType) throws DuplicatedNickException {
        if (map.containsKey(key)) {
            throw new DuplicatedNickException(errorType + ": " + " Nick " + key + " is duplicated, please check!");
        }
    }

    public static File findCurrentAptGenFolder(String fileName, File node) {
        if (node.getName().equals("androidTest")) { // filter some folders
            return null;
        }
        if (node.isDirectory()) {
            File[] subFiles = node.listFiles();
            List<String> subFileNames = Arrays.asList(node.list());

            if (subFiles == null) {
                return null;
            }
            if (subFileNames.contains(fileName)) {
                File target = new File(node.getAbsolutePath() + File.separator + fileName);
                if (!target.isDirectory()) {
                    return target;
                }
            }

            for (File f : subFiles) {
                File file = findCurrentAptGenFolder(fileName, f);
                if (file != null) {
                    return file;
                }
            }

            return null;
        }
        return null;
    }

    public static void findModuleJsonFiles(List<String> jsonFiles, String assetsPaths) {
        String[] splitPaths = assetsPaths.split(";");

        for (String path : splitPaths) {
            File file = new File(path);

            if (file.exists() && file.isDirectory()) {

                File[] childFiles = file.listFiles();

                if (childFiles == null || childFiles.length == 0) {
                    continue;
                }

                for (File child : childFiles) {
                    BroCompileLogger.i(child.getName());
                    if (child.getName().endsWith(Constants.MODULE_MAP_JSON_SUFFIX)) {
                        jsonFiles.add(child.getAbsolutePath());
                    }
                }
            }
        }
    }

    private static class DuplicatedNickException extends Exception {
        private DuplicatedNickException(String s) {
            super(s);
        }
    }
}
