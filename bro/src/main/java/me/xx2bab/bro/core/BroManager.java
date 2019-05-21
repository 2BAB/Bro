package me.xx2bab.bro.core;

import android.content.Context;

import java.util.List;

import me.xx2bab.bro.core.activity.ActivityRudder;
import me.xx2bab.bro.core.activity.IActivityFinder;
import me.xx2bab.bro.core.api.ApiRudder;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroMap;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.core.module.ModuleRudder;

class BroManager {

    private List<IActivityFinder> activityFinders;
    private ApiRudder apiRudder;
    private ModuleRudder moduleRudder;

    BroManager(IBroMap broMap) {
        activityFinders = Bro.getConfig().getActivityFinders();
        apiRudder = new ApiRudder(broMap.getBroApiMap());
        moduleRudder = new ModuleRudder(broMap.getBroModuleMap());

        // todo: plugin matters
    }

    <T extends IBroApi> T getApi(Class<T> apiInterface) {
        return apiRudder.getApi(apiInterface);
    }

    IBroApi getApi(String nick) {
        return apiRudder.getApi(nick);
    }

    IBroModule getModule(String moduleNick) {
        return moduleRudder.getModule(moduleNick);
    }


    ActivityRudder.Builder startPageFrom(Context context) {
        return new ActivityRudder.Builder(context, activityFinders);
    }

}