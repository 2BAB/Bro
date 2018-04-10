package me.xx2bab.bro.api.remote;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import me.xx2bab.bro.Bro;

import static android.content.Context.BIND_AUTO_CREATE;

public class BinderManager implements ServiceConnection, IBinder.DeathRecipient {

    private volatile static BinderManager instance;
    private IBinderPool binderPool;

    private BinderManager() {
    }

    public static BinderManager getInstance() {
        if (instance == null) {
            synchronized (BinderManager.class) {
                if (instance == null) {
                    instance = new BinderManager();
                    instance.bindBinderService();
                }
            }
        }
        return instance;
    }

    private void bindBinderService() {
        Intent intent = new Intent(Bro.appContext.getApplicationContext(), RemoteProxyService.class);
        Bro.appContext.getApplicationContext().bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binderPool = IBinderPool.Stub.asInterface(service);
        try {
            service.linkToDeath(this, 0);
        } catch (RemoteException e) {
            e.printStackTrace(); // todo
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // nothing we ought to do
    }


    @Override
    public void binderDied() {
        binderPool.asBinder().unlinkToDeath(this, 0);
        binderPool = null;
        bindBinderService();
    }

    public IBinderPool getBinderPool() {
        return binderPool;
    }
}
