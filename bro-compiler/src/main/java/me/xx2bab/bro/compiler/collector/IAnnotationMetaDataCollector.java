package me.xx2bab.bro.compiler.collector;

import me.xx2bab.bro.common.gen.IBroAnnoProcessor;

/**
 * A collector for collecting meta data of modules/app via annotated elements.
 */
public interface IAnnotationMetaDataCollector<T> {

    /**
     * To add a new meta data record.
     *
     * @param element
     */
    void addMetaRecord(T element);

    /**
     * To generate some intermediates of meta data or be a delegate for generators.
     *
     * @see IBroAnnoProcessor
     */
    void generate();

}
