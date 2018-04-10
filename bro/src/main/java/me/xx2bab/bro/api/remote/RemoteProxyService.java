package me.xx2bab.bro.api.remote;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class RemoteProxyService extends Service {

    private Binder binderPool = new BinderPool();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binderPool;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
