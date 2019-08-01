package me.xx2bab.bro.compiler.collector;

import com.alibaba.fastjson.JSON;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.common.anno.AnnotatedElement;
import me.xx2bab.bro.common.anno.Annotation;
import me.xx2bab.bro.common.util.FileUtils;

/**
 * Created on 2019-07-16
 */
public class SingleModuleCollector implements IAnnotationMetaDataCollector<Element> {

    private FileUtils fileUtils;
    private ProcessingEnvironment processingEnvironment;

    private String moduleName;
    private String libMetaDataOutputPath;

    private List<AnnotatedElement> elements;
    private Comparator<Annotation> comparator = new Comparator<Annotation>() {
        @Override
        public int compare(Annotation a1, Annotation a2) {
            return a1.name.compareTo(a2.name);
        }
    };

    public SingleModuleCollector(FileUtils fileUtils,
                                 ProcessingEnvironment processingEnvironment,
                                 String moduleName,
                                 String libMetaDataOutputPath) {
        this.fileUtils = fileUtils;
        this.processingEnvironment = processingEnvironment;
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
        annotatedElement.annotations = new TreeSet<>(comparator);
        for (int i = 0; i < list.size(); i++) {
            Annotation annotation = new Annotation();
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

        elements.add(annotatedElement);
    }

    @Override
    public void generate() {
        String fileName = fileUtils.filterIllegalCharsForResFileName(moduleName)
                + Constants.MODULE_META_INFO_FILE_SUFFIX;
        fileUtils.writeFile(JSON.toJSONString(elements), libMetaDataOutputPath, fileName);
    }

}
