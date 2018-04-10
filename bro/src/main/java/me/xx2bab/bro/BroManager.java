package me.xx2bab.bro;

import android.content.Context;

import me.xx2bab.bro.activity.ActivityRudder;
import me.xx2bab.bro.api.ApiRudder;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroMap;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.module.ModuleRudder;

class BroManager {

    private ApiRudder apiRudder;
    private ModuleRudder moduleRudder;

    BroManager(IBroMap broMap) {
        apiRudder = new ApiRudder();
        moduleRudder = new ModuleRudder();

        // todo: plugin matters
    }

    <T extends IBroApi> T getApi(Class<T> apiInterface) {
        return apiRudder.getApi(apiInterface);
    }

    <T extends IBroModule> T getModule(Class<T> moduleClass) {
        return moduleRudder.getModule(moduleClass);
    }

    ActivityRudder.Builder startPageFrom(Context context) {
        return new ActivityRudder.Builder(context);
    }

}