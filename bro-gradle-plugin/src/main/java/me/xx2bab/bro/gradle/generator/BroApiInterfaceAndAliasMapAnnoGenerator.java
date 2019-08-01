package me.xx2bab.bro.gradle.generator;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.anno.AnnotatedElement;
import me.xx2bab.bro.common.anno.Annotation;
import me.xx2bab.bro.common.gen.GenOutputs;
import me.xx2bab.bro.common.gen.IBroAnnoGenerator;
import me.xx2bab.bro.common.gen.IBroApiInterfaceAndAliasMap;

/**
 * To
 */
public class BroApiInterfaceAndAliasMapAnnoGenerator implements IBroAnnoGenerator {

    private static final String BRO_API_CLASS = BroApi.class.getCanonicalName();

    private static final TypeName TYPE_STRING_AND_STRING_MAP
            = ParameterizedTypeName.get(Map.class, String.class, String.class);
    private static final TypeName TYPE_STRING_AND_STRING_HASHMAP
            = ParameterizedTypeName.get(HashMap.class, String.class, String.class);


    private static final String FIELD_MAP = "interfaceAliasMap";

    @Override
    public void onGenerate(List<AnnotatedElement> inputMetaData,
                           GenOutputs genOutputs,
                           ProcessingEnvironment processingEnvironment) {
        // Extract ClassAlias<->Interface map
        Map<String, String> interfaceAliasMap = new HashMap<>();
        for (AnnotatedElement ae : inputMetaData) {
            if (containBroApi(ae.annotations)) {
                TypeElement element = processingEnvironment.getElementUtils().getTypeElement(ae.clazz);
                String interfaze = parseApiInterface(element, processingEnvironment);
                if (interfaze == null || interfaze.isEmpty()) {
                    continue;
                }
                String classAlias = parseClassAlias(ae.annotations);
                interfaceAliasMap.put(interfaze, classAlias);
            }
        }

        // Write it down to a java file
        String className = IBroApiInterfaceAndAliasMap.class.getSimpleName()
                + Constants.GEN_CLASS_SUFFIX;
        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(IBroApiInterfaceAndAliasMap.class);

        builder.addField(TYPE_STRING_AND_STRING_MAP, FIELD_MAP,
                Modifier.PRIVATE, Modifier.FINAL);

        builder.addMethod(generateConstructor(interfaceAliasMap));
        builder.addMethod(generateGetAliasByInterfaceMethod());

        JavaFile file = JavaFile.builder(Constants.GEN_PACKAGE_NAME, builder.build())
                .indent("    ") // with 4 spaces
                .addFileComment("Generated by BroRoutingTableAnnoGenerator.").build();
        try {
            file.writeTo(processingEnvironment.getFiler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean containBroApi(Set<Annotation> set) {
        for (Annotation anno : set) {
            if (anno.name.equals(BRO_API_CLASS)) {
                return true;
            }
        }
        return false;
    }

    private String parseClassAlias(Set<Annotation> set) {
        for (Annotation anno : set) {
            if (anno.name.equals(BRO_API_CLASS)) {
                // Bro annotation only has one field which is "value()"
                return anno.values.get(anno.values.firstKey());
            }
        }
        return null;
    }

    private String parseApiInterface(TypeElement element, ProcessingEnvironment pe) {
        for (TypeMirror mirror : element.getInterfaces()) {
            String result = parseApiInterfaceInternal(mirror.toString(), pe);
            if (result != null) {
                return mirror.toString();
            }
        }
        return null;
    }

    private String parseApiInterfaceInternal(String  interfaceCanonicalName,
                                             ProcessingEnvironment pe) {
        TypeElement typeElement = pe.getElementUtils().getTypeElement(interfaceCanonicalName);
        if (typeElement != null && typeElement.getInterfaces().size() > 0) {
            List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
            for (TypeMirror childInterface : interfaces) {
                if (childInterface.toString().equals(IBroApi.class.getCanonicalName())) {
                    return typeElement.toString();
                }
                String childResult = parseApiInterfaceInternal(childInterface.toString(), pe);
                if (childResult != null) {
                    return childResult;
                }
            }
        }
        return null;
    }

    private MethodSpec generateConstructor(Map<String, String> interfaceAliasMap) {
        final MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode(CodeBlock.builder().addStatement(FIELD_MAP + " = new $T()",
                        TYPE_STRING_AND_STRING_HASHMAP).build());

        for (String key : interfaceAliasMap.keySet()) {
            builder.addStatement(FIELD_MAP + ".put(\"" + key + "\", \""
                    + interfaceAliasMap.get(key) + "\")");
        }

        return builder.build();
    }

    /**
     * Implements the #getAliasByInterface(String interfaze) method
     * from IBroApiInterfaceAndAliasMap.class.
     *
     * @return The MethodSpec object used by JavaPoet.
     */
    private MethodSpec generateGetAliasByInterfaceMethod() {
        String param = "interfaze";
        return MethodSpec.methodBuilder("getAliasByInterface")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(String.class, param)
                .returns(String.class)
                .addStatement("return" + FIELD_MAP + ".get(" + param + ")")
                .build();
    }
}
