package me.xx2bab.bro.gradle.utils

import org.gradle.api.Project

class BroGradleLogger {

    static final String TAG = "== Bro == : "
    static logger

    static setLogger(Project project) {
        logger = project.logger
    }

    static l(String msg) {
        String msgWithPrefix = TAG + "Info : " + msg
        if (logger != null) {
            logger.lifecycle(msgWithPrefix)
        } else {
            System.out.println(msgWithPrefix);
        }
    }

    static e(String msg) {
        String msgWithPrefix = TAG + "Error : " + msg
        if (logger != null) {
            logger.error(msgWithPrefix)
        } else {
            System.err.println(msgWithPrefix)
        }
    }

}