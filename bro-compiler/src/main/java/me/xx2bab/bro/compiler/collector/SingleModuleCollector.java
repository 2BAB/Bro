package me.xx2bab.bro.compiler.collector;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.common.gen.anno.IBroAnnoProcessor;
import me.xx2bab.bro.common.util.FileUtils;

/**
 * To collect meta data of every module with registered processor, and generate intermediates.
 *
 * @see IBroAnnoProcessor
 */
public class SingleModuleCollector implements IAnnotationMetaDataCollector<Element> {

    private List<IBroAnnoProcessor> processors;
    private FileUtils fileUtils;
    private ProcessingEnvironment processingEnvironment;
    private String moduleName;
    private String libMetaDataOutputPath;

    /**
     * A map that provides <ProcessorName, FormattedMetaDataString>
     */
    private Map<String, List<String>> elements;

    public SingleModuleCollector(List<IBroAnnoProcessor> processors,
                                 ProcessingEnvironment processingEnvironment,
                                 FileUtils fileUtils,
                                 String moduleName,
                                 String libMetaDataOutputPath) {
        this.processors = processors;
        this.fileUtils = fileUtils;
        this.processingEnvironment = processingEnvironment;
        this.moduleName = moduleName;
        this.libMetaDataOutputPath = libMetaDataOutputPath;

        elements = new HashMap<>();
        for (IBroAnnoProcessor processor : processors) {
            elements.put(processor.getClass().getCanonicalName(), new ArrayList<String>());
        }
    }

    @Override
    public void addMetaRecord(Element element) {
        for (IBroAnnoProcessor processor : processors) {
            String collectedRes = processor.onCollect(element, processingEnvironment);
            if (collectedRes == null || collectedRes.isEmpty()) {
                continue;
            }
            elements.get(processor.getClass().getCanonicalName())
                    .add(collectedRes);
        }
    }

    @Override
    public void generate() {
        String fileName = fileUtils.filterIllegalCharsForResFileName(moduleName)
                + Constants.MODULE_META_INFO_FILE_SUFFIX;
        fileUtils.writeFile(JSON.toJSONString(elements), libMetaDataOutputPath, fileName);
    }

    public Map<String, List<String>> getMetaData() {
        return elements;
    }


}
