package me.xx2bab.bro.sample.profile;

import android.util.Log;

import java.util.Set;

import me.xx2bab.bro.annotations.BroModule;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroModule;

@BroModule()
public class SettingsModule implements IBroModule {

    @Override
    public Set<Class<? extends IBroApi>> dependencies() {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("ModuleCreates", "SettingsModule");
    }

}
