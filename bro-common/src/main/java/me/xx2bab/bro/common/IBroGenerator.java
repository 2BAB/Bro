package me.xx2bab.bro.common;

import java.io.File;

/**
 * It's a kind of SPI mechanism to make it easier for extending any other generators by users.
 * Bro triggers it when we have collected all meta data during application-module's compiling.
 */
public interface IBroGenerator<T> {

    /**
     * The main callback, to generate routing table/documentation/etc.
     *
     * @param metaDataList       The meta data (ex. Annotation/Manifest) that bro collected.
     * @param appPackageName     The package name of the app.
     * @param appAptGenDirectory The directory for placing new java class generated by the generator.
     * @param broBuildDirectory  The directory for placing other building artifacts like docs, etc.
     */
    void onGenerate(T metaDataList,
                    String appPackageName,
                    File appAptGenDirectory,
                    File broBuildDirectory);

}