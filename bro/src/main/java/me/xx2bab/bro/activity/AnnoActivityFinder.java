package me.xx2bab.bro.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import me.xx2bab.bro.Bro;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.util.ConvertUtils;


public class AnnoActivityFinder implements IActivityFinder {

    public AnnoActivityFinder() {
    }

    @Override
    public Intent find(Context context, Intent intent) {
        String name = ConvertUtils.convertUriToStringWithoutParams(intent.getData());
        BroProperties properties = Bro.getBroMap().getBroActivityMap().get(name);

        if (properties == null) {
            return null;
        }
        try {
            String activityName = properties.clazz;
            if (TextUtils.isEmpty(activityName)) {
                return null;
            }

            Class destActivityCls = Class.forName(activityName);
            intent.setClass(context, destActivityCls);
            return intent;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


}
