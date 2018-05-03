package me.xx2bab.bro.livereload.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Project

class LiveReloadLibraryPlugin extends LiveReloadBasePlugin {

    @Override
    void apply(Project project) {
        super.apply(project)

        if (project.plugins.hasPlugin(AppPlugin)) {
            println("LiveReloadLibraryPlugin applied.")
        } else (project.plugins.hasPlugin(LibraryPlugin)) {
            throw new IllegalStateException("LiveReloadLibraryPlugin should work with 'com.android.application' plugin.")
        }
    }

    @Override
    protected void onAfterEvaluate() {
        if (Env.instance.enable) {
            BroPlugadgetInjector.doLibraryInject(project)
        }
    }


}