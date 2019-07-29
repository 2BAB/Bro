package me.xx2bab.bro.gradle.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import me.xx2bab.bro.annotations.BroActivity;
import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.annotations.BroModule;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.common.IBroGenerator;
import me.xx2bab.bro.common.IBroRoutingTable2;
import me.xx2bab.bro.common.anno.AnnotatedElement;

/**
 * A generator to generate the implementation of "IBroRoutingTable"
 * which will be used by other Bro components.
 *
 * @see IBroRoutingTable2
 */
public class BroRoutingTableGenerator implements IBroGenerator<List<AnnotatedElement>, Filer> {

    private HashMap<String, Class<? extends Annotation>> supportedAnnotations;
    private HashSet<String> nicks;


    private static final ClassName STRING_CLASSNAME = ClassName.get("java.lang", "String");
    private static final ClassName MAP_CLASSNAME = ClassName.get("java.util", "Map");
    private static final ClassName HASHMAP_CLASSNAME = ClassName.get("java.util", "HashMap");
    private static final TypeName WILDCARD_ANNOTATION = WildcardTypeName.subtypeOf(Annotation.class);
    private static final TypeName TYPE_GENERIC_ANNOTATION_CLASS
            = ParameterizedTypeName.get(ClassName.get(Class.class),
            WILDCARD_ANNOTATION);

    private static final TypeName TYPE_STRING_AND_BRO_PROPERTIES_MAP
            = ParameterizedTypeName.get(Map.class, String.class, BroProperties.class);

    private static final TypeName TYPE_CLASS_AND_BRO_PROPERTIES_MAP_NESTED_MAP
            = ParameterizedTypeName.get(MAP_CLASSNAME, TYPE_GENERIC_ANNOTATION_CLASS,
            TYPE_STRING_AND_BRO_PROPERTIES_MAP);

    private static final TypeName TYPE_CLASS_AND_BRO_PROPERTIES_MAP_NESTED_HASHMAP
            = ParameterizedTypeName.get(HASHMAP_CLASSNAME, TYPE_GENERIC_ANNOTATION_CLASS,
            TYPE_STRING_AND_BRO_PROPERTIES_MAP);

    private static final TypeName TYPE_STRING_AND_STRING_MAP
            = ParameterizedTypeName.get(Map.class, String.class, String.class);

    private static final TypeName TYPE_STRING_AND_STRING_STRING_HASH_MAP
            = ParameterizedTypeName.get(MAP_CLASSNAME, STRING_CLASSNAME,
            TYPE_STRING_AND_STRING_MAP);

    private static final String TEMP_FIELD_TABLE = "table";
    private static final String TEMP_FIELD_BRO_MAP = "broMap";
    private static final String TEMP_FIELD_EXTRA_ANNO = "extraAnnotations";
    private static final String TEMP_FIELD_ANNO_VALUE = "annotationValues";
    private static final String TEMP_FIELD_BRO_PROP = "broProp";

    public BroRoutingTableGenerator() {
        supportedAnnotations = new HashMap<>();
        supportedAnnotations.put(BroActivity.class.getCanonicalName(), BroActivity.class);
        supportedAnnotations.put(BroApi.class.getCanonicalName(), BroApi.class);
        supportedAnnotations.put(BroModule.class.getCanonicalName(), BroModule.class);
        nicks = new HashSet<>();
    }

    @Override
    public void onGenerate(final List<AnnotatedElement> inputMetaData,
                           final Filer filer,
                           String appPackageName,
                           File appAptGenDirectory,
                           File broBuildDirectory) {
        nicks.clear();

        Map<Class<? extends Annotation>, Map<String, BroProperties>> table
                = breakdownMetaData(inputMetaData);

        TypeSpec.Builder builder = TypeSpec.classBuilder(Constants.ROUTING_TABLE_FILE_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(IBroRoutingTable2.class);

        // A field to store routing table
        builder.addField(TYPE_CLASS_AND_BRO_PROPERTIES_MAP_NESTED_MAP, TEMP_FIELD_TABLE, Modifier.PRIVATE,
                Modifier.FINAL);
        builder.addField(TYPE_STRING_AND_BRO_PROPERTIES_MAP, TEMP_FIELD_BRO_MAP, Modifier.PRIVATE);
        builder.addField(TYPE_STRING_AND_STRING_STRING_HASH_MAP, TEMP_FIELD_EXTRA_ANNO, Modifier.PRIVATE);
        builder.addField(TYPE_STRING_AND_STRING_MAP, TEMP_FIELD_ANNO_VALUE, Modifier.PRIVATE);
        builder.addField(BroProperties.class, TEMP_FIELD_BRO_PROP, Modifier.PRIVATE);

        // The constructor method to init all
        builder.addMethod(generateConstructor(table));
        // The getter method for each Annotation
        builder.addMethod(generateMapGetterMethod());

        JavaFile file = JavaFile.builder(Constants.ROUTING_TABLE_PACKAGE_NAME, builder.build())
                .indent("    ") // with 4 spaces
                .addFileComment("Generated by BroRoutingTableGenerator.").build();

//        if (existFile == null) {
//            // standard output
//            file.writeTo(filer);
//        } else {
//            // hack file override exception
//            File folder = existFile.getParentFile();
//            int times = file.toJavaFileObject().toUri().toString().split("/").length;
//            for (int i = 1; i < times; i++) {
//                folder = folder.getParentFile();
//            }
//            file.writeTo(folder);
//        }
//

        try {
            file.writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts the AnnotatedElement list to the data structure (routing-table) that Bro needs.
     *
     * @param metaDataList Get from the annotation collectors.
     * @return The map with the key (eg. BroActivity.class) and value (class & other annotations).
     * @see BroProperties
     */
    private Map<Class<? extends Annotation>, Map<String, BroProperties>> breakdownMetaData(
            final List<AnnotatedElement> metaDataList) {
        // eg. BroActivity.class -> [{nickName, BroProperties}]
        Map<Class<? extends Annotation>, Map<String, BroProperties>> res = new HashMap<>();
        for (Class<? extends Annotation> broAnno : supportedAnnotations.values()) {
            res.put(broAnno, new HashMap<String, BroProperties>());
        }
        for (final AnnotatedElement ae : metaDataList) {
            // eg. BroActivity.class
            Class<? extends Annotation> key = getBroAnnotation(ae);
            if (key == null) {
                continue;
            }
            // [{nickName, BroProperties}]
            String nick = "";
            Map<String, Map<String, String>> extraAnnotations = new HashMap<>();
            for (me.xx2bab.bro.common.anno.Annotation anno : ae.annotations) {
                if (anno.name.equals(key.getCanonicalName())) {
                    // Bro annotation only has one field which is "value()"
                    nick = anno.values.get(anno.values.firstKey());
                } else {
                    extraAnnotations.put(anno.name, anno.values);
                }
            }
            BroProperties broProperties = new BroProperties(ae.clazz, extraAnnotations);
            res.get(key).put(nick, broProperties);
        }
        return res;
    }

    private Class<? extends Annotation> getBroAnnotation(AnnotatedElement ae) {
        for (me.xx2bab.bro.common.anno.Annotation annotation : ae.annotations) {
            if (supportedAnnotations.containsKey(annotation.name)) {
                return supportedAnnotations.get(annotation.name);
            }
        }
        return null;
    }

    /**
     * The constructor of IBroRoutingTable implementation class.
     *
     * @return constructor method
     */
    private MethodSpec generateConstructor(
            Map<Class<? extends Annotation>, Map<String, BroProperties>> table) {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode(CodeBlock.builder().addStatement("table = new $T()",
                        TYPE_CLASS_AND_BRO_PROPERTIES_MAP_NESTED_HASHMAP).build());

        for (Class<? extends Annotation> broAnno : table.keySet()) {
            builder.addCode("\n")
                    .addStatement(TEMP_FIELD_BRO_MAP + " = new HashMap<>()");

            Map<String, BroProperties> map = table.get(broAnno);
            for (String nick : map.keySet()) {
                builder.addCode(generateBroProperties(map.get(nick)))
                        .addStatement(TEMP_FIELD_BRO_MAP + ".put(\"" + nick + "\", "
                                + TEMP_FIELD_BRO_PROP + ")");
            }
            builder.addStatement(TEMP_FIELD_TABLE + ".put(" + broAnno.getCanonicalName() + ".class, "
                    + TEMP_FIELD_BRO_MAP + ")");
        }

        builder.addCode("\n")
                .addStatement(TEMP_FIELD_BRO_MAP + " = null")
                .addStatement(TEMP_FIELD_EXTRA_ANNO + " = null")
                .addStatement(TEMP_FIELD_ANNO_VALUE + " = null")
                .addStatement(TEMP_FIELD_BRO_PROP + " = null");
        return builder.build();
    }

    /**
     * Generates the function getRoutingMapByAnnotation(Class<? extends Annotation>).
     *
     * @return The methodSpec object by JavaPoet.
     */
    private MethodSpec generateMapGetterMethod() {
        return MethodSpec.methodBuilder("getRoutingMapByAnnotation")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TYPE_GENERIC_ANNOTATION_CLASS, "annotation")
                .returns(TYPE_STRING_AND_BRO_PROPERTIES_MAP)
                .addStatement("return table.get(annotation)")
                .build();
    }


    private String generateBroProperties(BroProperties broProperties) {
        String clazz = broProperties.clazz;
        StringBuilder builder = new StringBuilder();

        builder.append(TEMP_FIELD_EXTRA_ANNO + " = new HashMap<>();\n");
        for (String anno : broProperties.extraAnnotations.keySet()) {
            Map<String, String> annoValues = broProperties.extraAnnotations.get(anno);
            builder.append(TEMP_FIELD_ANNO_VALUE).append(" = new HashMap<>();\n");
            for (String field : annoValues.keySet()) {
                builder.append(TEMP_FIELD_ANNO_VALUE).append(".put(\"").append(field).append("\", \"")
                        .append(annoValues.get(field)).append("\");\n");
            }
            builder.append(TEMP_FIELD_EXTRA_ANNO).append(".put(\"").append(anno).append("\", ")
                    .append(TEMP_FIELD_ANNO_VALUE).append(");\n");
        }

        builder.append(TEMP_FIELD_BRO_PROP).append(" = new BroProperties(\"")
                .append(clazz).append("\", ").append(TEMP_FIELD_EXTRA_ANNO).append(");\n");

        return builder.toString();
    }

    /**
     * Avoid duplicated nick.
     *
     * @param nick A value in Bro Annotations that can be used to get the real reference of
     *             the specific class.
     * @throws DuplicatedNickException We don't allow duplicated nicks so we throw a custom exp.
     */
    private void checkDuplicatedNick(String nick) throws DuplicatedNickException {
        if (nicks.contains(nick)) {
            throw new DuplicatedNickException("BroRoutingTableGenerator: Nick \"" + nick + "\" is" +
                    " duplicated, please check annotation values!");
        }
        nicks.add(nick);
    }

    public File findCurrentAptGenFolder(String fileName, File node) {
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


    private class DuplicatedNickException extends Exception {
        private DuplicatedNickException(String s) {
            super(s);
        }
    }


//    private String parseNick(Element element) {
//        List<? extends AnnotationMirror> list = element.getAnnotationMirrors();
//        for (int i = 0; i < list.size(); i++) {
//            String annotationType = list.get(i).getAnnotationType().toString();
//            if (annotationType.contains("me.xx2bab.bro.annotations")) {
//                Map<? extends ExecutableElement, ? extends AnnotationValue> map = list.get(i).getElementValues();
//                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : map.entrySet()) {
//                    if (entry.getKey().toString().equals("value()")) {
//                        return entry.getValue().toString().replace("(\"|\')", "");
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    private String parseExtraParams(Element element, String nick, String type) {
//        JSONObject jsonObject = new JSONObject();
//        List<? extends AnnotationMirror> list = element.getAnnotationMirrors();
//
//        for (int i = 0; i < list.size(); i++) {
//            String annotationType = list.get(i).getAnnotationType().toString();
//            if (annotationType.contains("me.xx2bab.bro.annotations")) {
//                continue;
//            }
//
//            String value = "";
//            Map<? extends ExecutableElement, ? extends AnnotationValue> map = list.get(i).getElementValues();
//            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : map.entrySet()) {
//                if (entry.getKey().toString().equals("value()")) {
//                    value = entry.getValue().toString().replace("\"", "");
//                }
//            }
//
//            jsonObject.put(annotationType, value);
//        }
//
//        if (type.equals(BroApi.class.getSimpleName())) {
//            String ApiInterface = parseApiInterface(element);
//            if (ApiInterface == null) {
//                throw new IllegalStateException(nick + ": Bro Api Must implements the interface which extends from IBroApi!");
//            }
//            jsonObject.put("ApiInterface", ApiInterface);
//        }
//
//        return jsonObject.toString();
//    }

}
