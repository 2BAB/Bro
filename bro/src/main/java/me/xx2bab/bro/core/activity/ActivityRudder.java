package me.xx2bab.bro.core.activity;

import android.content.Context;

import me.xx2bab.bro.core.BroContext;

public class ActivityRudder {

    private BroContext broContext;

    public ActivityRudder(BroContext broContext) {
        this.broContext = broContext;
    }

    public Builder startActivity(Context context) {
        return new Builder(context, broContext);
    }

}
