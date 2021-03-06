package me.xx2bab.bro.sample.location;

import android.util.Log;


import java.util.Random;

import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.sample.common.api.ILocationApi;

@BroApi(module = LocationExportApplication.class)
public class LocationApiImpl implements ILocationApi {

    private int flag = -1;

    @Override
    public void onCreate() {
        Log.e("LocationApiImpl", "onInit");
        flag = new Random().nextInt(100);
    }

    @Override
    public String getUserCurrentLocation() {
        return "Random: " + flag;
    }

}