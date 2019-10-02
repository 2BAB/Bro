package me.xx2bab.bro.sample.profile;

import android.content.Context;
import android.util.Log;

import java.util.Set;

import me.xx2bab.bro.annotations.BroModule;
import me.xx2bab.bro.common.AbstractBroModule;
import me.xx2bab.bro.common.IBroApi;

@BroModule()
public class SettingsModule extends AbstractBroModule {

    @Override
    public Set<Class<? extends IBroApi>> dependencies() {
        return null;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        Log.d("ModuleCreates", "SettingsModule");
    }

}
