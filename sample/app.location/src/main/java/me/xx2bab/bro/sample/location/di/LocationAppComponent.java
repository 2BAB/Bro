package me.xx2bab.bro.sample.location.di;

import dagger.Component;
import me.xx2bab.bro.sample.location.LocationExportApplication;

@Component(modules = {LocationModule.class})
public interface LocationAppComponent {

    void inject(LocationExportApplication app);

}
