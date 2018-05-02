package me.xx2bab.bro.livereload.plugin

import org.gradle.api.Project
import org.gradle.api.Task
/**
 * Created by 2bab on 2017/11/3.
 */
enum ManifestUtil {

    instance

    def manifest

    static final String ANDROID_MANIFEST_NAME = "AndroidManifest.xml"

    def static registerManifestPathFinder(Project project) {
        project.android.applicationVariants.all { variant ->
            String variantName = variant.name.capitalize()
            Task processManifestTask = project.tasks["process${variantName}Manifest"]
            processManifestTask.doLast {
                processManifestTask.outputs.getFiles().each {
                    File manifestFile = new File(it.absolutePath)
                    if (!manifestFile.exists() || manifestFile.name != ANDROID_MANIFEST_NAME) {
                        return
                    }
                    println("find AndroidManifest.xml in path of $it.absolutePath")
                    instance.init(it.absolutePath)
                }
            }
        }
    }

    void init(String manifestPath) {
        manifest = new XmlSlurper().parse(manifestPath)
    }

    List<String> getActivities() {
        if (manifest == null) {
            throw new IllegalStateException("ManifestUtil should init before used.")
        }

        def activities = []
        String pkg = manifest.@package

        manifest.application.activity.each {
            String name = it.'@android:name'
            if (name.substring(0, 1) == '.') {
                name = pkg + name
            }
            activities += name
        }

        activities
    }
}