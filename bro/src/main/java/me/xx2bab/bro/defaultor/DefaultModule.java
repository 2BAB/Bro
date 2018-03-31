package me.xx2bab.bro.defaultor;

import android.content.res.Resources;

import me.xx2bab.bro.Bro;
import me.xx2bab.bro.common.IBroModule;

public abstract class DefaultModule implements IBroModule {

    public static Resources getResources() {
        return Bro.appContext.getResources();
    }

}