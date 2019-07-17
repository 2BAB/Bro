package me.xx2bab.bro.compiler.collector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.compiler.util.BroCompileLogger;

/**
 * Created on 2019-07-16
 * TODO: move some original logic from MetaDataCollector to here
 */
public class DefaultMultiModuleMetaDataCollector implements IMultiModuleMetaDataCollector {

    private String appPackageName;
    private String appAssetsSourcePaths;
    private String appAptGenPath;

    private List<String> jsonFiles;

    public DefaultMultiModuleMetaDataCollector(String appPackageName,
                                               String appAssetsSourcePaths,
                                               String appAptGenPath) {
        this.appPackageName = appPackageName;
        this.appAssetsSourcePaths = appAssetsSourcePaths;
        this.appAptGenPath = appAptGenPath;
    }

    @Override
    public void addModuleMetaDataFile() {

    }

    @Override
    public void generateEntireMetaDataTable() {
        jsonFiles = new ArrayList<>();
        findModuleJsonFiles(jsonFiles, appAssetsSourcePaths);
        BroCompileLogger.i(jsonFiles.size() + "");
    }

    private void findModuleJsonFiles(List<String> jsonFiles, String assetsPaths) {
        String[] splitPaths = assetsPaths.split(";");
        for (String path : splitPaths) {
            File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                File[] childFiles = file.listFiles();

                if (childFiles == null || childFiles.length == 0) {
                    continue;
                }

                for (File child : childFiles) {
                    BroCompileLogger.i("Gathering meta data file: " + child.getName());
                    if (child.getName().endsWith(Constants.MODULE_META_INFO_FILE_SUFFIX)) {
                        jsonFiles.add(child.getAbsolutePath());
                    }
                }
            }
        }
    }

}
