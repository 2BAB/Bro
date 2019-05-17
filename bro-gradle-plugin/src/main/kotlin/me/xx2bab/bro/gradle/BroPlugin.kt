package me.xx2bab.bro.gradle

import me.xx2bab.bro.gradle.utils.BroGradleLogger
import me.xx2bab.bro.gradle.utils.BuildUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppPlugin

class BroPlugin implements Plugin<Project> {

    protected Project project
    protected BroExtension broExtension

    @Override
    void apply(Project project) {
        this.project = project

        BroGradleLogger.setLogger(project)
        BroGradleLogger.l("bro-gradle-plugin begin to process")

        broExtension = project.extensions.create("bro", BroExtension, project)

        project.afterEvaluate {
            BuildUtils.mkdirBroBuildDir(project)
            onAfterEvaluate()
        }
    }

    protected void onAfterEvaluate() {
        BroAnnotationArgumentsInjector.inject(isApplication(), project)
        getVariants().all { variant ->
            def task = project.tasks["generate${variant.name.capitalize()}Sources"]
            if (task != null) {
                task.outputs.upToDateWhen { false }
            }
        }
    }

    private boolean isApplication() {
        return project.plugins.hasPlugin(AppPlugin)
    }

    def getVariants() {
        return isApplication() ? project.android.applicationVariants : project.android.libraryVariants
    }

}