package me.xx2bab.bro.compiler.collector;

import java.util.List;

import me.xx2bab.bro.common.IBroGenerator;
import me.xx2bab.bro.common.anno.AnnotatedElement;

/**
 * A collector for collecting meta data of modules/app via annotated elements.
 */
public interface IAnnotationMetaDataCollector<T> {

    /**
     * To get current meta data collection.
     *
     * @return Current meta data list.
     */
    List<AnnotatedElement> getMetaData();

    /**
     * To add a new meta data record.
     *
     * @param element
     */
    void addMetaRecord(T element);

    /**
     * To generate some intermediates of meta data or be a delegate for generators.
     *
     * @see IBroGenerator
     */
    void generate();

}
