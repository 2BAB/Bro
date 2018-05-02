package me.xx2bab.bro.livereload.plugin

import org.gradle.api.Project

@Singleton
class Env {

    static final String BRO_LIVE_RELOAD_PREFIX = 'bro.livereload.'
    String packageName = ""
    String moduleName = ""
    boolean enable = false

    def init(Project project) {
        enable = getProperties().get(BRO_LIVE_RELOAD_PREFIX + 'enable')
        if (enable) {
            packageName = getProperties().get(BRO_LIVE_RELOAD_PREFIX + 'packageName')
            moduleName = getProperties().get(BRO_LIVE_RELOAD_PREFIX + 'moduleName')
        }
    }
}
