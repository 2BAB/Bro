package me.xx2bab.bro.core.activity;

import android.content.Context;
import android.content.Intent;

import me.xx2bab.bro.core.BroContext;

public interface IActivityFinder {

    Intent find(Context context, Intent intent, BroContext broContext);

}
