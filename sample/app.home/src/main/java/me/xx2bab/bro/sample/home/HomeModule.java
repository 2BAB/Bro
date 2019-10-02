package me.xx2bab.bro.sample.home;

import android.content.Context;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import me.xx2bab.bro.annotations.BroModule;
import me.xx2bab.bro.common.AbstractBroModule;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.core.Bro;
import me.xx2bab.bro.sample.common.api.ISettingsApi;

@BroModule()
public class HomeModule extends AbstractBroModule {

    @Override
    public Set<Class<? extends IBroApi>> dependencies() {
        Set<Class<? extends IBroApi>> set = new HashSet<>();
        set.add(ISettingsApi.class);
        return set;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        Log.d("ModuleCreates", "HomeModule, getPi() from SettingsApi: "
                + Bro.get().getApi(ISettingsApi.class).getPi());
    }

}
