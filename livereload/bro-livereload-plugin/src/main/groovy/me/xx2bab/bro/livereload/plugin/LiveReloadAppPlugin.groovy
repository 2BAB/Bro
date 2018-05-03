package me.xx2bab.bro.livereload.plugin

class LiveReloadAppPlugin extends LiveReloadBasePlugin {

    protected void onAfterEvaluate() {
        if (Env.instance.enable) {
            BroPlugadgetInjector.collectAppInfo(project)
        }
    }


}