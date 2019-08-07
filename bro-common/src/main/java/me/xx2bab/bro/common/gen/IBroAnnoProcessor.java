package me.xx2bab.bro.common.gen;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

/**
 * It's a kind of SPI mechanism to make it easier for extending any other processors by users.
 */
public interface IBroAnnoProcessor {

    /**
     * To provide a set of annotations that this processor supports.
     *
     * @return A collection of Java Annotations.
     */
    Collection<Class<? extends Annotation>> getSupportedAnnotationTypes();

    /**
     * When annotation processor process each module with annotations, we do collection of
     * meta data from them.
     *
     * @param element Annotated element that we want parse data from.
     */
    String onCollect(Element element, ProcessingEnvironment processingEnvironment);

    /**
     * After collected all meta data from each module, we are going to generate
     * routing-table/documentation/etc.
     *
     * @param inputMetaData         The meta data (ex. Annotation/Manifest) that bro collected.
     * @param genOutputs            File or App meta-data that can be used in generating outputs.
     * @param processingEnvironment Tools that passed from Java Annotation Processor.
     */
    void onGenerate(List<String> inputMetaData,
                    GenOutputs genOutputs,
                    ProcessingEnvironment processingEnvironment);

}