package me.xx2bab.bro.gradle.utils

object ManifestUtils {

//    private val manifest
//
//    private const val ANDROID_MANIFEST_NAME = "AndroidManifest.xml"
//
//    fun registerManifestPathFinder(project: Project) {
//        project.android.applicationVariants.all { variant ->
//            String variantName = variant.name.capitalize()
//            Task processManifestTask = project.tasks["process${variantName}Manifest"]
//            processManifestTask.doLast {
//                processManifestTask.outputs.getFiles().each {
//                    File manifestFile = new File(it.absolutePath)
//                    if (!manifestFile.exists() || manifestFile.name != ANDROID_MANIFEST_NAME) {
//                        return
//                    }
//                    // BroGradleLogger.l("find AndroidManifest.xml in path of $it.absolutePath")
//                    instance.init(it.absolutePath)
//                }
//            }
//        }
//    }
//
//    fun init( manifestPath:String) {
//        manifest = new XmlSlurper().parse(manifestPath)
//    }
//
//    fun  getActivities():List<String> {
//        if (manifest == null) {
//            throw IllegalStateException("ManifestUtils should init before used.")
//        }
//
//        val activities = []
//        val pkg = manifest.@package
//
//        manifest.application.activity.each {
//            String name = it.'@android:name'
//            if (name.substring(0, 1) == '.') {
//                name = pkg + name
//            }
//            activities += name
//        }
//
//        activities
//    }
}