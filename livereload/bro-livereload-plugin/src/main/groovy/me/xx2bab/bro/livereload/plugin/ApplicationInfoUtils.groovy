package me.xx2bab.bro.livereload.plugin

import org.gradle.api.Project

class ApplicationInfoUtils {

    static generateApplicationInfo(Project project, def variant) {
        File appInfoFile = BuildUtils.getApplicationInfoFile(project, variant.name.capitalize())
        File variantFolder = appInfoFile.getParentFile()
        if (!variantFolder.exists()) {
            variantFolder.mkdirs()
        }
        if (!appInfoFile.exists()) {
            appInfoFile.createNewFile()
        }
        StringBuilder builder = new StringBuilder()
        def applicationId = [variant.mergedFlavor.applicationId, variant.buildType.applicationIdSuffix].findAll().join()
        builder.append("packageName").append("=").append(applicationId).append("\n")
        appInfoFile.text = builder.toString()
    }


}
