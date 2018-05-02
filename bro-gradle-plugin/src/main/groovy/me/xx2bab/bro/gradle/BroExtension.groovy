package me.xx2bab.bro.gradle

import org.gradle.api.Project

class BroExtension {

    Project project

    BroExtension(Project project) {
        this.project = project
    }

}