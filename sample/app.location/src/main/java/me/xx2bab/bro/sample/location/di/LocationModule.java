package me.xx2bab.bro.sample.location.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import me.xx2bab.bro.core.Bro;
import me.xx2bab.bro.sample.common.api.ISettingsApi;

@Module
public class LocationModule {

    @Provides
    public ISettingsApi provideSettingsApi() {
        return Bro.get().getApi(ISettingsApi.class);
    }

}
