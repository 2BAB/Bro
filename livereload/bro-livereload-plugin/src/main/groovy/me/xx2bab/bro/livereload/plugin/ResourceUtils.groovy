package me.xx2bab.bro.livereload.plugin

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.tasks.ProcessAndroidResources
import groovy.io.FileType
import groovy.xml.MarkupBuilder
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.Task

class ResourceUtils {

    static final String AAPT_XMLNS = "http://schemas.android.com/aapt"

    def static addPublicDefinitionsFlag(Project project) {
        String publicFilePath = BuildUtils.getApplicationPublicFilePath(project)
        project.tasks['preBuild'].doLast {
            File file = new File(publicFilePath)
            if (!file.exists()) {
                file.createNewFile()
            }
        }
        project.android.aaptOptions.additionalParameters("-P", publicFilePath)
    }

    def static clearAdditionalFlags(Project project) {
        project.android.aaptOptions.setAdditionalParameters(null)
    }

    def static generateIdsUsingPublicFile(Project project, File publicFile, File mergedValuesFile) {
        Node appPublicRoot = new XmlParser().parse(publicFile)
        File idsFile = BuildUtils.getApplicationIdsFile(project)
        def xml = new MarkupBuilder(new FileWriter(idsFile))
        xml.resources {
            appPublicRoot.each {
                if (it.attribute('type') == 'id') {
                    // append idsFile
                    item(type: it.attribute('type'), name: it.attribute('name'))
                }
            }
        }

        filterDuplicateIds(idsFile, mergedValuesFile)

        return idsFile
    }

    def static generateApplicationResourcesAar(Project project, ApplicationVariant variant) {
        String aarPath = BuildUtils.getApplicationResBundleFilePath(project)
        File applicationAarBundleDir = new File(aarPath.replace('.aar', ''))
        if (!applicationAarBundleDir.exists()) {
            applicationAarBundleDir.mkdirs()
        }
        File originRDotTxt = new File(getRDotTxtFilePath(project, variant))
        File originAppMergedDir = new File(getMergedResourcesDir(project, variant))
        File rDotTxt = new File(applicationAarBundleDir.absolutePath + File.separator + 'R.txt')
        File appMergedDir = new File(applicationAarBundleDir.absolutePath + File.separator + 'res')

        FileUtils.copyFile(originRDotTxt, rDotTxt)
        FileUtils.copyDirectory(originAppMergedDir, appMergedDir)
        File dummyManifest = createDummyManifest(applicationAarBundleDir.absolutePath + File.separator + 'AndroidManifest.xml')

        filterFormatAttribute(appMergedDir)
        filterCompiled9Patch(project, originAppMergedDir, appMergedDir, variant)

        BuildUtils.zipFiles(aarPath, appMergedDir.getParentFile())

        return new File(aarPath)
    }

    def static filterFormatAttribute(File appMergedDir) {
        File values = new File(appMergedDir.absolutePath + BroPlugadgetInjector.VALUES_DIR + "values.xml")
        Node xml = new XmlParser().parse(values)
        HashSet<String> attrOfDepth2 = new HashSet<>()
        HashSet<String> attrOfDepth3 = new HashSet<>()
        xml.attr.each {
            if (it.attributes().containsKey('format') && !attrOfDepth2.add(it.@name)) {
                println('values.xml : remove duplicated format attribute (depth2-2) of : ' + it.toString())
                it.attributes.remove('format')
            }
        }
        xml.'declare-styleable'.each { ds ->
            ds.attr.each {
                if (it.attributes().containsKey('format') && attrOfDepth2.contains(it.@name)) {
                    println('values.xml : remove duplicated format attribute (depth2-3) of : ' + ds.@name + " " + it.toString())
                    it.attributes().remove('format')
                } else if (it.attributes().containsKey('format') && !attrOfDepth3.add(it.@name)) {
                    println('values.xml : remove duplicated format attribute (depth3-3) of : ' + ds.@name + " " + it.toString())
                    it.attributes().remove('format')
                }
            }
        }

        new XmlNodePrinter(new PrintWriter(new FileWriter(values))).print(xml)
    }

    def
    static filterCompiled9Patch(Project project, File originAppMergedDir, File appMergedDir, ApplicationVariant variant) {
        String mapPath = (project.buildDir.absolutePath + File.separator + 'intermediates' + File.separator + 'incremental'
                + File.separator + "merge${variant.name.capitalize()}Resources" + File.separator + 'compile-file-map.properties')
        File originMap = new File(mapPath)
        File afterChangeMap = new File(originMap.getParent() + File.separator + 'after-change-map.properties')
        FileUtils.copyFile(originMap, afterChangeMap)
        afterChangeMap.text = afterChangeMap.text.replaceAll(originAppMergedDir.absolutePath, appMergedDir.absolutePath)
        Properties resCompileFileMap = new Properties()
        resCompileFileMap.load(afterChangeMap.newInputStream())

        appMergedDir.traverse(type: FileType.FILES) { resFile ->
            if (resFile.name.endsWith('.9.png')) {
                resCompileFileMap.each { property ->
                    if (property.value == resFile.absolutePath) {
                        FileUtils.copyFile(new File(property.key.toString()),
                                new File(property.value.toString()))
                        println('.9.png : will replace by origin one: ' + property.key.toString())
                    }
                }
            }
        }
    }

    def static filterInnerResByAaptXmlns(File publicFIle, File mergedResDir) {
        List<String> innerResList = []
        mergedResDir.eachDirRecurse { dir ->
            if (dir.name.startsWith('anim') || dir.name.startsWith('animator') || dir.name.startsWith('drawable')) {
                dir.eachFileRecurse { resFile ->
                    if (resFile.name.endsWith('.xml') && resFile.text.contains(AAPT_XMLNS)) {
                        innerResList += resFile.name.replace('.xml', '')
                    }
                }
            }
        }
        if (innerResList.size() == 0) {
            return
        }

        Node publicXmlRoot = new XmlParser().parse(publicFIle)
        List<Node> nodeWillRemove = []
        publicXmlRoot.each { Node node ->
            innerResList.each { String ir ->
                if (node.attribute('name').toString().matches("${ir}_\\d")) {
                    println('public.xml : delete the node: ' + node.toString())
                    nodeWillRemove += node
                }
            }
        }
        nodeWillRemove.each {
            publicXmlRoot.remove(it)
        }
        new XmlNodePrinter(new PrintWriter(new FileWriter(publicFIle))).print(publicXmlRoot)
    }

    def static filterDuplicateIds(File idsFile, File mergedValuesFile) {
        Node idsRoot = new XmlParser().parse(idsFile)
        Node mvRoot = new XmlParser().parse(mergedValuesFile)
        List<Node> idsWillFilter = []
        idsRoot.each { idNode ->
            def idName = idNode.attribute('name')
            mvRoot.each { mvIdNode ->
                if (mvIdNode.attribute('type') == 'id') {
                    if (idName == mvIdNode.attribute('name')) {
                        idsWillFilter += idNode
                    }
                }
            }
        }

        idsWillFilter.each {
            idsRoot.remove(it)
        }

        new XmlNodePrinter(new PrintWriter(new FileWriter(idsFile))).print(idsRoot)
    }

    static File createDummyManifest(String path) {
        File manifest = new File(path)
        if (!manifest.exists()) {
            manifest.createNewFile()
        }
        manifest.text = '<?xml version="1.0" encoding="utf-8"?><manifest xmlns:android="http://schemas.android.com/apk/res/android" package="me.xx2bab.bro.appres" android:versionCode="1" android:versionName="1.0.0" ><application></application></manifest>'
        return manifest
    }

    static String getMergedResourcesDir(Project project, def variant) {
        def scope = variant.getVariantData().getScope()
        String mergeTaskName = scope.getMergeResourcesTask().name
        Task mergeTask = project.tasks[mergeTaskName]
        return mergeTask.outputDir.absolutePath
    }

    static String getRDotTxtFilePath(Project project, ApplicationVariant variant) {
        final ProcessAndroidResources aaptTask = project.tasks["process${variant.name.capitalize()}Resources"]
        return aaptTask.textSymbolOutputDir.absolutePath + File.separator + 'R.txt'
    }

}
