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
    private String moduleBroBuildDir;

    /**
     * A map that provides <ProcessorName, FormattedMetaDataString>
     */
    private Map<String, List<String>> elements;

    public SingleModuleCollector(List<IBroAnnoProcessor> processors,
                                 ProcessingEnvironment processingEnvironment,
                                 FileUtils fileUtils,
                                 String moduleName,
                                 String libMetaDataOutputPath,
                                 String moduleBroBuildDir) {
        this.processors = processors;
        this.fileUtils = fileUtils;
        this.processingEnvironment = processingEnvironment;
        this.moduleName = moduleName;
        this.libMetaDataOutputPath = libMetaDataOutputPath;
        this.moduleBroBuildDir = moduleBroBuildDir;

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
        String result = JSON.toJSONString(elements);

        // Write to libMetaDataOutputPath (/{modulePath}/build/intermediates/library_assets/{variantName}/out),
        // however because "javac" task is prior than "package assets" task,
        // and package task will override the output directory.
        // So this writing action is a temporary output, and only for the module that isn't compiled
        // by aar file (it means the lib is existed in the project with app module,
        // or you can call it monorepo).
        // Please check BroPlugin class in bro-gradle-plugin,
        // to see the hack for copying .bro file into libMetaDataOutputPath again.
        fileUtils.writeFile(result, libMetaDataOutputPath, fileName);

        // Write to moduleBroBuildDir (/{modulePath}/build/bro),
        // this is a backup, and we will copy from here to libMetaDataOutputPath later
        // Please check BroPlugin class in bro-gradle-plugin,
        // to see the hack for copying .bro file into libMetaDataOutputPath again.
        fileUtils.writeFile(result, moduleBroBuildDir, fileName);
    }

    public Map<String, List<String>> getMetaData() {
        return elements;
    }


}
