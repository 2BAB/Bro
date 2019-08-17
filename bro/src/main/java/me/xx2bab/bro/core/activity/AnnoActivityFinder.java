package me.xx2bab.bro.core.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.util.Map;

import me.xx2bab.bro.annotations.BroActivity;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.gen.anno.IBroAliasRoutingTable;
import me.xx2bab.bro.core.BroContext;
import me.xx2bab.bro.core.util.ConvertUtils;


public class AnnoActivityFinder implements IBroActivityFinder {

    public AnnoActivityFinder() {
    }

    @Override
    public Intent find(Context context, Intent intent, BroContext broContext) {
        String name = ConvertUtils.convertUriToStringWithoutParams(intent.getData());
        Map<String, BroProperties> map = broContext.broRudder
                .getImplementationByInterface(IBroAliasRoutingTable.class)
                .getRoutingMapByAnnotation(BroActivity.class);
        BroProperties properties = map.get(name);

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
