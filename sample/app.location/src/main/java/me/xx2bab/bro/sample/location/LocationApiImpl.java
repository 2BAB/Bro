package me.xx2bab.bro.sample.location;

import android.util.Log;


import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.sample.common.api.ILocationApi;

@BroApi(module = LocationExportApplication.class)
public class LocationApiImpl implements ILocationApi {

    @Override
    public void onCreate() {
        Log.e("LocationApiImpl", "onInit");
    }

    @Override
    public String getUserCurrentLocation() {
        return "Random";
    }
}
