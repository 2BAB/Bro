package me.xx2bab.bro.common.gen;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;

import me.xx2bab.bro.common.anno.AnnotatedElement;

/**
 * It's a kind of SPI mechanism to make it easier for extending any other generators by users.
 * Bro triggers it when we have collected all meta data during application-module's compiling.
 */
public interface IBroAnnoGenerator {

    /**
     * The main callback, to generate routing-table/documentation/etc.
     *
     * @param inputMetaData The meta data (ex. Annotation/Manifest) that bro collected.
     */
    void onGenerate(final List<AnnotatedElement> inputMetaData,
                    final GenOutputs genOutputs,
                    final ProcessingEnvironment processingEnvironment);

}