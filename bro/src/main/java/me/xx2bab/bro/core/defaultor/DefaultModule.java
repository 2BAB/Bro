package me.xx2bab.bro.core.defaultor;

import android.content.res.Resources;

import me.xx2bab.bro.common.IBroModule;

public abstract class DefaultModule implements IBroModule {

    public static Resources getResources() {
        return null; // TODO: If user really need it
    }

}