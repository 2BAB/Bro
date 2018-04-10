package me.xx2bab.bro.api.remote;

import android.os.IBinder;

interface IBinderPool {

    IBinder queryBinder(String binderToken);

}
