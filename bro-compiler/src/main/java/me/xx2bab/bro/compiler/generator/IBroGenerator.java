package me.xx2bab.bro.compiler.generator;

import java.io.File;
import java.util.List;

import me.xx2bab.bro.common.anno.AnnotatedElement;

/**
 * It's a kind of SPI mechanism to make it easier for extending any other generators by users.
 * Bro triggers it when we have collected all meta data during application-module's compiling.
 */
public interface IBroGenerator {

    /**
     * The main callback, to generate routing table / documentation / etc.
     *
     * @param metaDataList The Annotation meta data that bro collected.
     * @param appAptGenDirectory The directory for placing new java class generated by the generator.
     * @param broBuildDirectory The directory for placing other building artifacts like docs, etc.
     */
    void onGenerate(List<AnnotatedElement> metaDataList,
                    File appAptGenDirectory,
                    File broBuildDirectory);

}