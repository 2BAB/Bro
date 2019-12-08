package me.xx2bab.bro.sample.location;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import dagger.Lazy;
import me.xx2bab.bro.annotations.BroModule;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.sample.common.api.ISettingsApi;
import me.xx2bab.bro.sample.location.di.DaggerLocationAppComponent;

@BroModule
public class LocationExportApplication implements IBroModule {

    @Inject
    Lazy<ISettingsApi> settingsApiByLazy;

    @Override
    public Set<Class<? extends IBroApi>> getLaunchDependencies() {
        Set<Class<? extends IBroApi>> set = new HashSet<>();
        set.add(ISettingsApi.class);
        return set;
    }

    @Override
    public void onCreate(Context context) {
        Log.d("ModuleCreates", "LocationExportApplication");
        DaggerLocationAppComponent.create().inject(this);
        int pi = settingsApiByLazy.get().getPi();
        Intent intent = new Intent(context, LocationService.class);
        intent.putExtra("pi", pi);
        context.startService(intent);
    }

}
