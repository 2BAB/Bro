package me.xx2bab.bro.sample.location;

import android.util.Log;

import java.util.Random;

import javax.inject.Singleton;

import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.annotations.BroSingleton;
import me.xx2bab.bro.sample.common.api.ILocationApi;
import me.xx2bab.bro.sample.common.api.ISingletonLocationApi;

@BroSingleton
@BroApi(module = LocationExportApplication.class)
public class LocationApiSingletonImpl implements ISingletonLocationApi {

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