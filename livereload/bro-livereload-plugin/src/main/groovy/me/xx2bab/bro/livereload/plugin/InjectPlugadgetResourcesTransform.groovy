package me.xx2bab.bro.livereload.plugin

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.NotFoundException
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class InjectPlugadgetResourcesTransform extends Transform {

    private Project project
    public static final String RAW_DIR = File.separator + "raw" + File.separator

    InjectPlugadgetResourcesTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return 'injectPlugadgetResources'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(
            final TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {

        if (!isIncremental()) {
            transformInvocation.outputProvider.deleteAll()
        }

        ClassPool classPool = new ClassPool(true)
        classPool.insertClassPath(getAndroidClasspath())

        transformInvocation.inputs.each {
            it.directoryInputs.each { DirectoryInput directoryInput ->
                classPool.insertClassPath(directoryInput.file.absolutePath)
            }

            it.jarInputs.each { jarInput ->
                def status = jarInput.getStatus()
                if (status != Status.REMOVED) {
                    classPool.insertClassPath(jarInput.file.absolutePath)
                }
            }
        }


        transformInvocation.inputs.each {
            it.directoryInputs.each { DirectoryInput directoryInput ->
                modifyActivityAttachBaseContext(classPool, directoryInput.file.absolutePath)
                modifyBroModuleGetResource(classPool, directoryInput.file.absolutePath)

                def dest = transformInvocation.outputProvider.getContentLocation(
                        directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            it.jarInputs.each { jarInput ->
                // we don't need libs currently
            }
        }

    }

    String getAndroidClasspath() {
        return "${project.android.getSdkDirectory().getAbsolutePath()}/platforms/" +
                "${project.android.getCompileSdkVersion()}/android.jar"
    }

    void modifyBroModuleGetResource(ClassPool classPool, String inputClasspath) {
        HashSet<String> moduleClasses = []

        project.android.applicationVariants.all { variant ->
            def rawDirPath = ResourceUtils.getMergedResourcesDir(project, variant) + RAW_DIR
            def rawDir = new File(rawDirPath)

            List<File> moduleInfoFile = rawDir.listFiles()
            if (moduleInfoFile != null && moduleInfoFile.size() > 0) {
                moduleInfoFile.each {
                    // find .moduleInfo file
                    if (it.name.endsWith(Constants.MODULE_MAP_JSON_SUFFIX)) {
                        // parse BroModule class
                        JSONObject broModules = JSON.parseObject(it.text).getJSONObject("BroModule")
                        Set<Map.Entry<String, Object>> set = broModules.entrySet()
                        if (set != null && set.size() > 0) {
                            for (Map.Entry<String, Object> entry : set) {
                                JSONObject module = JSON.parseObject(entry.value.toString())
                                moduleClasses.add(module.getString("clazz"))
                            }
                        }
                    }
                }
            }
        }

        moduleClasses.each {
            CtClass broModuleClass
            try {
                broModuleClass = classPool.getCtClass(it)
                if (broModuleClass.isFrozen()) {
                    broModuleClass.defrost()
                }
                try {
                    broModuleClass.getDeclaredMethod("getResources")
                    throw new IllegalStateException('DO NOT OVERRIDE BroModule\'s getResources() method of ' + it)
                } catch (NotFoundException e) {
                    // attachBaseContext is not declared, inject one with plugin-resource replacement
                    println("Inject getResources method to ${it}")
                    String getResourcesEntity = "public static android.content.res.Resources getResources() {" +
                            "return me.xx2bab.bro.livereload.core.LiveReload.instance.getPlugadgetResources(\"${extension.packageName}\");" +
                            "}"
                    CtMethod overridedGetResourcesMethod = CtMethod.make(getResourcesEntity, broModuleClass)
                    broModuleClass.addMethod(overridedGetResourcesMethod)
                    broModuleClass.writeFile(inputClasspath)
                }

            } catch (NotFoundException e) {
                println(e.getMessage())
            } finally {
                if (broModuleClass != null) {
                    broModuleClass.detach()
                }
            }
        }
    }

    void modifyActivityAttachBaseContext(ClassPool classPool, String inputClasspath) {
        def activities = ManifestUtil.instance.activities

        activities.each {
            CtClass activityClass
            try {
                activityClass = classPool.getCtClass(it)
                if (activityClass.isFrozen()) {
                    activityClass.defrost()
                }
                try {
                    CtMethod abcMethod = activityClass.getDeclaredMethod("attachBaseContext")

                    // attachBaseContext is declared, just replace plugin-resource
                    println("Inject plugadget Activity resources in attachBaseContext of ${it}")
                    abcMethod.insertBefore("me.xx2bab.bro.livereload.core.LiveReload.instance.injectPlugadgetResources(\"${extension.packageName}\", base);")
                    activityClass.writeFile(inputClasspath)

                } catch (NotFoundException e) {

                    // attachBaseContext is not declared, inject one with plugin-resource replacement
                    println("Inject attachBaseContext method to ${it}")
                    String abcEntity = "protected void attachBaseContext(android.content.Context base) { " +
                            "me.xx2bab.bro.livereload.core.LiveReload.instance.injectPlugadgetResources(\"${extension.packageName}\", base);" +
                            "super.attachBaseContext(base); " +
                            "}"
                    CtMethod newAbcMethod = CtMethod.make(abcEntity, activityClass)
                    activityClass.addMethod(newAbcMethod)
                    activityClass.writeFile(inputClasspath)
                }

            } catch (NotFoundException e) {
                println(e.getMessage())
            } finally {
                if (activityClass != null) {
                    activityClass.detach()
                }
            }
        }

    }
}
