package me.xx2bab.bro.core.activity;

import android.content.Context;
import android.content.Intent;

public interface IActivityFinder {

    Intent find(Context context, Intent intent);

}
