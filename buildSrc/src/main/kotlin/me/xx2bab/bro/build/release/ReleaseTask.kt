package me.xx2bab.bro.build.release

import me.xx2bab.bro.build.release.utils.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class ReleaseTask : DefaultTask() {

    @TaskAction
    fun release() {
        Logger.i("Deploy Completed.");
    }

}