package me.xx2bab.bro.livereload.plugin

import com.android.build.gradle.api.ApplicationVariant
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.Task


class BroPlugadgetInjector {

    static final String VALUES_DIR = File.separator + "values" + File.separator

    static collectAppInfo(Project project) {
        ResourceUtils.addPublicDefinitionsFlag(project) // file 1: make aapt generate public.xml

        project.android.applicationVariants.all { ApplicationVariant variant ->
            String variantName = variant.name.capitalize()

            project.tasks["pre${variantName}Build"].doLast {
                ApplicationInfoUtils.generateApplicationInfo(project, variant) // file 2: generate application info properties
            }

            Task mergeAssetsTask = project.tasks["merge${variantName}Assets"]
            mergeAssetsTask.doLast {
                String mergeAssetsOutput
                mergeAssetsTask.outputs.getFiles().each {
                    if (it.absolutePath.contains(File.separator + "assets" + File.separator)) {
                        mergeAssetsOutput = it.absolutePath
                    }
                }
                String mergedResPath = ResourceUtils.getMergedResourcesDir(project, variant)
                File mergedResDir = new File(mergedResPath)

                // collect all files
                AppInfoOutputs outputs = new AppInfoOutputs()
                outputs.applicationPublicXml = BuildUtils.getApplicationPublicFile(project)
                ResourceUtils.filterInnerResByAaptXmlns(outputs.applicationPublicXml, mergedResDir)
                outputs.applicationIdsXml = ResourceUtils.generateIdsUsingPublicFile(project,
                        outputs.applicationPublicXml, new File(mergedResPath + VALUES_DIR + "values.xml")) // file 3: generate ids.xml
                outputs.applicationInfoProperties = BuildUtils.getApplicationInfoFile(project, variant.name)
                outputs.applicationResAar = ResourceUtils.generateApplicationResourcesAar(project, variant)

                // copy files to assets, so application can read it at runtime
                BuildUtils.copyFiles(mergeAssetsOutput,
                        // BuildUtils.getApplicationPublicFile(project),
                        outputs.applicationInfoProperties
                )

                // zip files, the archives will be used for plugadget packaging
                String archivePath = (BuildUtils.getBroBuildPath(project) + File.separator
                        + variant.name.toString() + Constants.APP_OUTPUTS_SUFFIX)
                BuildUtils.zipFiles(archivePath,
                        outputs.applicationPublicXml,
                        outputs.applicationIdsXml,
                        outputs.applicationInfoProperties,
                        outputs.applicationResAar
                )
            }
        }
    }

    static doLibraryInject(Project project, File appOutputsZipFile) {
        // arguments check
        AppInfoOutputs appInfoOutputs = parseApplicationOutputs(project, appOutputsZipFile)
        if (Env.instance.packageName == null || Env.instance.trim() == "") {
            throw new IllegalArgumentException("Plugadget-Package with Bro need packageName argument! Please read THE MANUAL!")
        }

        // utils init
        ManifestUtil.registerManifestPathFinder(project)

        // class transform
        project.android.registerTransform(new InjectPlugadgetResourcesTransform(project))
        project.android.registerTransform(new StripPlugadgetClassTransform(project))
        project.android.registerTransform(new StripPlugadgetNativeLibsTransform(project))

        // apply bro_app_outputs.zip
        applyApplicationOutputs(project, appInfoOutputs)

        installPlugadgetToDevice(project, appInfoOutputs)
    }

    static applyApplicationOutputs(Project project, AppInfoOutputs appInfoOutputs) {
        project.android.applicationVariants.all { variant ->

            def processResourcesTask = project.tasks["process${variant.name.capitalize()}Resources"]
            processResourcesTask.outputs.upToDateWhen { false }
            processResourcesTask.doFirst {
                // prepare params
                String mergedResPath = ResourceUtils.getMergedResourcesDir(project, variant)

                // generate public.xml & ids.xml
                /*File appPublic = appInfoOutputs.applicationPublicXml
                File intersectionPublic, idsFile
                (intersectionPublic, idsFile) = ResourceUtils.computeIntersectionBetweenTwoPublicFile(project, appPublic)
                ResourceUtils.filterInnerResByAaptXmlns(intersectionPublic, new File(mergedResPath))
                ResourceUtils.filterDuplicateIds(idsFile, new File(mergedResPath + VALUES_DIR + "values.xml"))*/

                // copy them into merged/res/variant/values/, so aapt will handle it
                FileUtils.copyFile(appInfoOutputs.applicationPublicXml, new File(mergedResPath + VALUES_DIR + "public.xml"))
                FileUtils.copyFile(appInfoOutputs.applicationIdsXml, new File(mergedResPath + VALUES_DIR + "ids.xml"))

                println('keep resources id by intersection public file successfully')

            }
        }
    }

    static AppInfoOutputs parseApplicationOutputs(Project project, File appOutputsZipFile) {
        project.tasks['preBuild'].doLast {
            BuildUtils.unzipFile(project, appOutputsZipFile.absolutePath, BuildUtils.getBroBuildPath(project))
        }
        AppInfoOutputs appInfoOutputs = new AppInfoOutputs()
        appInfoOutputs.applicationPublicXml = BuildUtils.getApplicationPublicFile(project)
        appInfoOutputs.applicationIdsXml = BuildUtils.getApplicationIdsFile(project)
        appInfoOutputs.applicationInfoProperties = new File(BuildUtils.getBroBuildPath(project) + File.separator + Constants.APPLICATION_INFO_FILE_NAME)
        return appInfoOutputs
    }

    static installPlugadgetToDevice(Project project, AppInfoOutputs appInfoOutputs) {
        project.android.applicationVariants.all { variant ->
            def plugadgetPath = BuildUtils.getBroBuildPath(project) + File.separator + Env.instance.packageName + Constants.PLUGADGET_SUFFIX

            variant.outputs.each { output ->
                if (output.outputFile != null && output.outputFile.name.endsWith('.apk')) {
                    output.outputFile = project.file(plugadgetPath)
                }
            }

            // install plugadget to sdcard/bro/packageName and stop the target app
            project.tasks["assemble${variant.name.capitalize()}"].doLast {

                Properties properties = new Properties()
                properties.load(new FileInputStream(appInfoOutputs.applicationInfoProperties))

                String sdcardLocation = CommandUtil.runCommandWithResultBack('adb shell echo \$EXTERNAL_STORAGE')
                if (sdcardLocation == null) {
                    sdcardLocation = '/sdcard'
                } else {
                    sdcardLocation = sdcardLocation.replaceAll("\n", "")
                }

                String targetPath = "${sdcardLocation}/bro/${properties.getProperty("packageName")}/"

                String mkdirTargetCommand = "adb shell mkdir -p " + targetPath
                println(mkdirTargetCommand)
                CommandUtil.runCommand(mkdirTargetCommand)

                String installCommand = "adb push ${plugadgetPath} " + targetPath
                println(installCommand)
                CommandUtil.runCommand(installCommand)

                String forceStopCommand = "adb shell am force-stop ${properties.getProperty("packageName")}"
                println(forceStopCommand)
                CommandUtil.runCommand(forceStopCommand)
            }
        }
    }

}
