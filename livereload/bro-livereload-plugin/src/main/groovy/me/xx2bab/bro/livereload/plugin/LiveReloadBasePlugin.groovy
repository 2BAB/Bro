package me.xx2bab.bro.livereload.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class LiveReloadBasePlugin implements Plugin<Project> {

    Project project

    @Override
    void apply(Project project) {
        super.apply(project)

        this.project = project

        Env.instance.init(project)

        project.afterEvaluate {
            onAfterEvaluate()
        }
    }

    abstract protected void onAfterEvaluate()


}