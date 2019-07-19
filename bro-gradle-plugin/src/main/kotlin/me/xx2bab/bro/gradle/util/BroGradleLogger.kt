package me.xx2bab.bro.gradle.util

import org.gradle.api.Project
import org.gradle.api.logging.Logger

object BroGradleLogger {

    private val TAG = "[Bro]: [bro-gradle-plugin] "
    private lateinit var logger: Logger

    fun setProject(project: Project) {
        logger = project.logger
    }

    fun d(message: String) {
        logger.debug(TAG + message)
    }

    fun l(message: String) {
        logger.lifecycle(TAG + message)
    }

    fun e(message: String) {
        logger.error(TAG + message)
    }


}

