package me.xx2bab.bro.common.gen.anno

import me.xx2bab.bro.common.gen.GenOutputs
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import kotlin.Annotation

/**
 * It's a kind of SPI mechanism to make it easier for extending any other processors by users.
 */
interface IBroAnnoProcessor {
    /**
     * To provide a set of annotations that this processor supports.
     *
     * @return A collection of Java Annotations.
     */
    fun getSupportedAnnotationTypes(): Collection<Class<out Annotation>>

    /**
     * When annotation processor process each module with annotations, we do collection of
     * meta data from them.
     *
     * @param element Annotated element that we want parse data from.
     */
    fun onCollect(element: Element, processingEnvironment: ProcessingEnvironment): String?

    /**
     * After collected all meta data from each module, we are going to generate
     * routing-table/documentation/etc.
     *
     * @param inputMetaData         The meta data (ex. Annotation/Manifest) that bro collected.
     * @param genOutputs            File or App meta-data that can be used in generating outputs.
     * @param processingEnvironment Tools that passed from Java Annotation Processor.
     */
    fun onGenerate(inputMetaData: List<String>,
                   genOutputs: GenOutputs,
                   processingEnvironment: ProcessingEnvironment)
}