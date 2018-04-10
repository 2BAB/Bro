package me.xx2bab.bro.api.remote;

import android.os.IBinder;
import android.os.RemoteException;

import java.util.HashMap;
import java.util.Map;

import me.xx2bab.bro.Bro;
import me.xx2bab.bro.common.BroProperties;


public class BinderPool extends IBinderPool.Stub {

    @Override
    public IBinder queryBinder(String binderToken) throws RemoteException {
        HashMap<String, BroProperties> map = Bro.getBroMap().getBroApiMap();
        for (Map.Entry<String, BroProperties> entry : map.entrySet()) {
            if (entry.getKey().equals(binderToken)) {
                try {
                    return (IBinder) Class.forName(entry.getValue().clazz).newInstance();
                } catch (Exception e) {
                    // ignored
                }
            }
        }
        return null;
    }


}
