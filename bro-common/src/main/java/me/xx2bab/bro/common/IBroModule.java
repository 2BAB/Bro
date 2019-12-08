package me.xx2bab.bro.common;

import android.content.Context;

import java.util.Set;

public interface IBroModule {

    Set<Class<? extends IBroApi>> getLaunchDependencies();

    void onCreate(Context context);

}
