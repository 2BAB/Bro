package me.xx2bab.bro.compiler.collector;

import javax.lang.model.element.Element;

/**
 * Created on 2019-07-16
 */
public interface ISingleModuleMetaDataCollector {

    void addMetaRecord(Element element);

    void generateMetaDataFile();

}
