package me.xx2bab.bro.gradle

import org.gradle.api.Project

class BroExtension {

    Project project

    // Library
    String packageName

    BroExtension(Project project) {
        this.project = project
    }

}