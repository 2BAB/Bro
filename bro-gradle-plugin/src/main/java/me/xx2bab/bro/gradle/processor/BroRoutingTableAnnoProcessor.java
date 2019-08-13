package me.xx2bab.bro.gradle.processor;

import com.alibaba.fastjson.JSON;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import me.xx2bab.bro.annotations.BroActivity;
import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.annotations.BroModule;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.common.gen.GenOutputs;
import me.xx2bab.bro.common.gen.anno.AnnotatedElement;
import me.xx2bab.bro.common.gen.anno.IBroAliasRoutingTable;
import me.xx2bab.bro.common.gen.anno.IBroAnnoProcessor;
import me.xx2bab.bro.common.util.FileUtils;

/**
 * A generator to generate the implementation of "IBroAliasRoutingTable"
 * which will be used by other Bro components.
 *
 * @see IBroAliasRoutingTable
 */
public class BroRoutingTableAnnoProcessor implements IBroAnnoProcessor {

    private HashMap<String, Class<? extends Annotation>> supportedAnnotations;
    private HashSet<String> aliases;

    private Comparator<me.xx2bab.bro.common.gen.anno.Annotation> comparator
            = new Comparator<me.xx2bab.bro.common.gen.anno.Annotation>() {
        @Override
        public int compare(me.xx2bab.bro.common.gen.anno.Annotation a1, me.xx2bab.bro.common.gen.anno.Annotation a2) {
            return a1.name.compareTo(a2.name);
        }
    };

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

    public BroRoutingTableAnnoProcessor() {
        supportedAnnotations = new HashMap<>();
        supportedAnnotations.put(BroActivity.class.getCanonicalName(), BroActivity.class);
        supportedAnnotations.put(BroApi.class.getCanonicalName(), BroApi.class);
        supportedAnnotations.put(BroModule.class.getCanonicalName(), BroModule.class);
        aliases = new HashSet<>();
    }

    @Override
    public Collection<Class<? extends Annotation>> getSupportedAnnotationTypes() {
        return supportedAnnotations.values();
    }

    @Override
    public String onCollect(Element element, ProcessingEnvironment processingEnvironment) {
        List<? extends AnnotationMirror> list = element.getAnnotationMirrors();
        AnnotatedElement annotatedElement = new AnnotatedElement();
        annotatedElement.name = element.asType().toString();
        annotatedElement.annotations = new TreeSet<>(comparator);
        for (int i = 0; i < list.size(); i++) {
            me.xx2bab.bro.common.gen.anno.Annotation annotation = new me.xx2bab.bro.common.gen.anno.Annotation();
            annotation.name = list.get(i).getAnnotationType().toString();
            annotation.values = new TreeMap<>();
            Map<? extends ExecutableElement, ? extends AnnotationValue> map
                    = list.get(i).getElementValues();
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                    : map.entrySet()) {
                annotation.values.put(entry.getKey().toString().replaceAll("\\(\\)", ""),
                        entry.getValue().toString().replaceAll("([\"'])", ""));
            }
            annotatedElement.annotations.add(annotation);
        }

        // TODO: support more element type
        // Parse interface info for class
        if (element instanceof TypeElement) {
            annotatedElement.type = ElementType.TYPE;
            annotatedElement.clazz = annotatedElement.name;
//            TypeElement typeElement = (TypeElement) element;
//            String packageName = typeElement.getQualifiedName().toString();
        }

        return JSON.toJSONString(annotatedElement);
    }

    @Override
    public void onGenerate(final List<String> inputMetaData,
                           final GenOutputs genOutputs,
                           final ProcessingEnvironment processingEnvironment) {
        // Prepare params
        List<AnnotatedElement> elements = new ArrayList<>();
        for (String json : inputMetaData) {
            elements.add(JSON.parseObject(json, AnnotatedElement.class));
        }
        aliases.clear();

        // Convert the data
        Map<Class<? extends Annotation>, Map<String, BroProperties>> table
                = breakdownMetaData(elements);

        // Generate the Java class
        String className = genOutputs.generateClassNameForImplementation(
                IBroAliasRoutingTable.class);
        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(IBroAliasRoutingTable.class);

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

        JavaFile file = JavaFile.builder(Constants.GEN_PACKAGE_NAME, builder.build())
                .indent("    ") // with 4 spaces
                .addFileComment("Generated by BroRoutingTableAnnoProcessor.").build();
        try {
            file.writeTo(processingEnvironment.getFiler());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        // Generate the doc
        generateDoc(table, genOutputs);
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
        // eg. BroActivity.class -> [{Alias, BroProperties}]
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
            // [{Alias, BroProperties}]
            String alias = "";
            Map<String, Map<String, String>> extraAnnotations = new HashMap<>();
            for (me.xx2bab.bro.common.gen.anno.Annotation anno : ae.annotations) {
                if (anno.name.equals(key.getCanonicalName())) {
                    // Bro annotation only has one field which is "value()"
                    alias = anno.values.get(anno.values.firstKey());
                } else {
                    extraAnnotations.put(anno.name, anno.values);
                }
            }
            checkDuplicatedAlias(alias);
            BroProperties broProperties = new BroProperties(ae.clazz, extraAnnotations);
            res.get(key).put(alias, broProperties);
        }
        return res;
    }

    private Class<? extends Annotation> getBroAnnotation(AnnotatedElement ae) {
        for (me.xx2bab.bro.common.gen.anno.Annotation annotation : ae.annotations) {
            if (supportedAnnotations.containsKey(annotation.name)) {
                return supportedAnnotations.get(annotation.name);
            }
        }
        return null;
    }

    /**
     * The constructor of IBroAliasRoutingTable implementation class.
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
            for (String alias : map.keySet()) {
                builder.addCode(generateBroProperties(map.get(alias)))
                        .addStatement(TEMP_FIELD_BRO_MAP + ".put(\"" + alias + "\", "
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
     * @return The MethodSpec object used by JavaPoet.
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
     * Avoid duplicated alias.
     *
     * @param alias A value in Bro Annotations that can be used to get the real reference of
     *              the specific class.
     */
    private void checkDuplicatedAlias(String alias) {
        if (aliases.contains(alias)) {
            throw new DuplicatedAliasException("BroRoutingTableAnnoProcessor: Alias \"" + alias + "\" is" +
                    " duplicated, please check annotation values!");
        }
        aliases.add(alias);
    }

    private class DuplicatedAliasException extends IllegalArgumentException {
        private DuplicatedAliasException(String s) {
            super(s);
        }
    }


    private void generateDoc(
            Map<Class<? extends Annotation>, Map<String, BroProperties>> annoInfoMap,
            GenOutputs genOutputs) {
        StringBuilder builder = new StringBuilder();
        builder.append(new Heading("RoutingTable by [Alias, BroProperties]", 1))
                .append("\n\n\n");
        for (Class<? extends Annotation> annoType : annoInfoMap.keySet()) {
            builder.append(new Heading(annoType.getSimpleName(), 2)).append("\n\n");
            Map<String, BroProperties> aliasPropMap = annoInfoMap.get(annoType);
            Table.Builder aliasPropTable = new Table.Builder()
                    .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                    .addRow("Alias", "BroProperties");
            for (String alias : aliasPropMap.keySet()) {
                String broPropJson = JSON.toJSONString(aliasPropMap.get(alias));
                aliasPropTable.addRow(alias, broPropJson);
            }
            builder.append(aliasPropTable.build()).append("\n\n");
        }
        FileUtils.getDefault().writeFile(builder.toString(),
                genOutputs.broBuildDirectory.getAbsolutePath(), "bro-alias-prop-map.md");
    }

//    public File findCurrentAptGenFolder(String fileName, File node) {
//        if (node.getName().equals("androidTest")) { // filter some folders
//            return null;
//        }
//        if (node.isDirectory()) {
//            File[] subFiles = node.listFiles();
//            List<String> subFileNames = Arrays.asList(node.list());
//
//            if (subFiles == null) {
//                return null;
//            }
//            if (subFileNames.contains(fileName)) {
//                File target = new File(node.getAbsolutePath() + File.separator + fileName);
//                if (!target.isDirectory()) {
//                    return target;
//                }
//            }
//
//            for (File f : subFiles) {
//                File file = findCurrentAptGenFolder(fileName, f);
//                if (file != null) {
//                    return file;
//                }
//            }
//
//            return null;
//        }
//        return null;
//    }

}
