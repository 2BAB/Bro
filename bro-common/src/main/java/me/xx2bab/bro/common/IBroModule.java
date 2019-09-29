package me.xx2bab.bro.common;

import java.util.Set;

public interface IBroModule {

    Set<Class<? extends IBroApi>> dependencies();

    void onCreate();

}
