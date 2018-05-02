package me.xx2bab.bro.livereload.core;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

public enum LiveReload {

    instance;

    private String appPackageName;
    public HashMap<String, PlugadgetInfo> plugadgets;
    public static final String PLUGADGET_STORAGE = Environment.getExternalStoragePublicDirectory("bro").getAbsolutePath();
    private boolean isPlugadgetModeOn = false;
    
    public static final String APPLICATION_INFO_FILE_NAME = "bro_application_info.properties";
    public static final String APPLICATION_PUBLIC_FILE_NAME = "bro_application_public.xml";
    public static final String APPLICATION_IDS_FILE_NAME = "bro_application_ids.xml";
    public static final String APPLICATION_RES_BUNDLE_FILE_NAME = "bro_application_res_bundle.aar";
    public static final String APP_OUTPUTS_SUFFIX = "_bro_app_outputs.zip";
    public static final String PLUGADGET_SUFFIX = ".bro";

    public void init(Context baseContext) {
        if (!isPlugadgetEnabled(baseContext)) {
            return;
        }

        Properties appInfoInPlugadgetMode = getApplicationInfoInPlugadgetMode(baseContext);
        if (appInfoInPlugadgetMode == null) {
            return;
        }

        appPackageName = appInfoInPlugadgetMode.getProperty("packageName");
        if (TextUtils.isEmpty(appPackageName)) {
            return;
        }

        Log.i("Bro", "Plugadget Mode Enabled");
        isPlugadgetModeOn = true;
        installPlugadgets(baseContext);
        DispatchClassloader.injectClassloader(baseContext);
    }

    private void installPlugadgets(Context baseContext) {
        plugadgets = new HashMap<>();
        // appPackageName = baseContext.getPackageName();
        File appPlugadgetFolder = new File(PLUGADGET_STORAGE + "/" + appPackageName);
        if (!appPlugadgetFolder.exists()) {
            boolean mkdirsResult = appPlugadgetFolder.mkdirs();
            if (!mkdirsResult) {
                Log.e("Bro", "mkdirs for " + appPlugadgetFolder.getAbsolutePath() + "is failed");
                return;
            }
        }
        File[] plugadgetFiles = appPlugadgetFolder.listFiles();
        if (plugadgetFiles == null) {
            return;
        }

        for (File plugadgetFile : plugadgetFiles) {
            if (plugadgetFile.getName().endsWith(PLUGADGET_SUFFIX)) {
                PlugadgetInfo plugadgetInfo = new PlugadgetInfo();
                plugadgetInfo.packageName = plugadgetFile.getName().replace(PLUGADGET_SUFFIX, "");
                plugadgetInfo.classloader = buildPlugadgetClassloader(plugadgetFile, baseContext);
                plugadgetInfo.resources = buildPlugadgetResource(plugadgetFile, baseContext);
                plugadgets.put(plugadgetInfo.packageName, plugadgetInfo);
            }
        }
    }

    private PlugadgetClassloader buildPlugadgetClassloader(File plugadgetFile, Context baseContext) {
        File dexOutputDir = baseContext.getDir("dex", 0);
        PlugadgetClassloader plugadgetClassloader = new PlugadgetClassloader(
                plugadgetFile.getAbsolutePath(),
                dexOutputDir.getAbsolutePath(),
                null,
                baseContext.getClassLoader());
        return plugadgetClassloader;
    }

    private Resources buildPlugadgetResource(File plugadgetFile, Context baseContext) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, plugadgetFile.getAbsolutePath());
            Resources resources = new Resources(assetManager, baseContext.getResources().getDisplayMetrics(),
                    baseContext.getResources().getConfiguration());
            return resources;
        } catch (Exception e) {
            Log.e("Bro", "Build Plugadget Resources Failed (" + plugadgetFile.getAbsolutePath() + ")");
            return null;
        }
    }

    /**
     * Invoked by attachBaseContext(Context base) of plugadget activity
     *
     * @param packageName
     * @param base
     */
    public void injectPlugadgetResources(String packageName, Context base) {
        try {
            Field field = base.getClass().getDeclaredField("mResources");
            field.setAccessible(true);
            field.set(base, plugadgets.get(packageName).resources);
        } catch (Exception e) {
            Log.e("Bro", "Replace Plugadget Resources Failed : " + e.getMessage());
        }
    }

    /**
     * Invoked by plugadget module
     *
     * @param packageName
     */
    public Resources getPlugadgetResources(String packageName) {
        return plugadgets.get(packageName).resources;
    }

    public boolean isPlugadgetModeOn() {
        return isPlugadgetModeOn;
    }


    public static boolean isPlugadgetEnabled(Context context) {
        try {
            String[] filePaths = context.getAssets().list("");
            if (filePaths.length > 0) {
                for (String filePath : filePaths) {
                    if (filePath.contains(APPLICATION_INFO_FILE_NAME)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static Properties getApplicationInfoInPlugadgetMode(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(APPLICATION_INFO_FILE_NAME);
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (Exception e) {
            Log.e("Bro", e.getMessage());
            return null;
        }
    }
}