package me.xx2bab.bro.compiler.collector;

import com.alibaba.fastjson.JSON;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.common.anno.AnnotatedElement;
import me.xx2bab.bro.common.anno.Annotation;
import me.xx2bab.bro.common.util.CommonUtils;

/**
 * Created on 2019-07-16
 */
public class SingleModuleCollector implements IAnnotationMetaDataCollector<Element> {

    private CommonUtils commonUtils;

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private String moduleName;
    private String libMetaDataOutputPath;

    private List<AnnotatedElement> elements;

    public SingleModuleCollector(CommonUtils commonUtils,
                                 Types typeUtils,
                                 Elements elementUtils,
                                 Filer filer,
                                 String moduleName,
                                 String libMetaDataOutputPath) {
        this.commonUtils = commonUtils;
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.filer = filer;
        this.moduleName = moduleName;
        this.libMetaDataOutputPath = libMetaDataOutputPath;
        elements = new ArrayList<>();
    }

    @Override
    public List<AnnotatedElement> getMetaData() {
        return elements;
    }

    @Override
    public void addMetaRecord(Element element) {
        List<? extends AnnotationMirror> list = element.getAnnotationMirrors();
        AnnotatedElement annotatedElement = new AnnotatedElement();
        annotatedElement.name = element.asType().toString();
        annotatedElement.annotations = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Annotation annotation = new Annotation();
            annotation.name = list.get(i).getAnnotationType().toString();
            annotation.values = new HashMap<>();
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

        elements.add(annotatedElement);
    }

    @Override
    public void generate() {
        String fileName = commonUtils.filterIllegalCharsForResFileName(moduleName)
                + Constants.MODULE_META_INFO_FILE_SUFFIX;
        commonUtils.writeFile(JSON.toJSONString(elements), libMetaDataOutputPath, fileName);
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
