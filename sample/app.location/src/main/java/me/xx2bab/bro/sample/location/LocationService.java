package me.xx2bab.bro.sample.location;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service {

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int pi = intent.getIntExtra("pi", -1);
        if (pi > 0) {
            Log.i("LocationService", "We really finished the flow of " +
                    "calling exported api with the help of DI, the Pi value is " + pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }
}
