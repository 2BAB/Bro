package me.xx2bab.bro.core.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.Set;

import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.core.base.BroErrorType;
import me.xx2bab.bro.core.base.IBroInterceptor;
import me.xx2bab.bro.core.base.IBroMonitor;
import me.xx2bab.bro.core.util.BroRuntimeLog;
import me.xx2bab.bro.core.util.ConvertUtils;

public class ActivityNaviProcessor {

    private Builder builder;
    private Intent intent;
    private boolean validated;
    private boolean intercepted;

    @SuppressLint("WrongConstant")
    public ActivityNaviProcessor(Builder builder) {
        IBroMonitor monitor = builder.broContext.monitor;
        IBroInterceptor interceptor = builder.broContext.interceptor;
        this.builder = builder;
        this.validated = true;
        this.intercepted = false;

        if (builder.context == null || builder.targetUri == null
                || builder.broContext.activityFinders == null) {
            this.validated = false;
            monitor.onActivityRudderException(
                    BroErrorType.PAGE_MISSING_ARGUMENTS, null);
            return;
        }

        String name = ConvertUtils.convertUriToStringWithoutParams(builder.targetUri);

        // may be null
        BroProperties properties = builder.broContext.routingTable.getBroActivityMap().get(name);

        intent = new Intent();
        intent.setData(builder.targetUri);

        try {
            if (interceptor.beforeFindActivity(builder.context,
                    builder.targetUri.toString(), intent, properties)) {
                this.intercepted = true;
                return;
            }
        } catch (Exception e) {
            BroRuntimeLog.e(e.getMessage());
        }

        intent = findActivity(intent);

        if (intent == null) {
            this.validated = false;
            monitor.onActivityRudderException(
                    BroErrorType.PAGE_CANT_FIND_TARGET, builder);
            return;
        }

        if (!TextUtils.isEmpty(builder.category)) {
            intent.addCategory(builder.category);
        }

        if (builder.extras != null) {
            intent.putExtras(builder.extras);
        }

        if (builder.flags != Builder.INVALIDATE) {
            intent.addFlags(builder.flags);
        }

        injectParamsFromUri(intent, builder.targetUri);

        try {
            if (interceptor.beforeStartActivity(builder.context,
                    builder.targetUri.toString(), intent, properties)) {
                this.intercepted = true;
                return;
            }
        } catch (Exception e) {
            BroRuntimeLog.e(e.getMessage());
        }

        if (builder.dryRun) {
            return;
        }

        if (builder.context instanceof Activity) {
            if (builder.requestCode > 0) {
                ((Activity) builder.context).startActivityForResult(intent, builder.requestCode);
            } else {
                ((Activity) builder.context).startActivity(intent); // cast is necessary
            }
            int[] transition = builder.broContext.activityTransition;
            if (transition != null) {
                int enter = transition[0];
                int exit = transition[1];
                if (enter > 0 && exit > 0) {
                    ((Activity) builder.context).overridePendingTransition(enter, exit);
                }
            }
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            builder.context.startActivity(intent);
        }
    }

    public boolean isIntentValidate() {
        return validated;
    }

    public boolean isIntercepted() {
        return intercepted;
    }

    public Builder getBuilder() {
        return builder;
    }

    public Intent getIntent() {
        return intent;
    }

    private Intent findActivity(Intent intent) {
        for (IActivityFinder finder : builder.broContext.activityFinders) {
            Intent temp = new Intent(intent);
            temp = finder.find(builder.context, temp, builder.broContext);
            if (temp != null) {
                return temp;
            }
        }
        return null;
    }


    private void injectParamsFromUri(Intent intent, Uri uri) {
        if (intent == null || uri == null) {
            return;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }

        try {
            Set<String> keySet = uri.getQueryParameterNames();
            for (String key : keySet) {
                String originValue = bundle.getString(key);
                if (TextUtils.isEmpty(originValue)) {
                    bundle.putString(key, uri.getQueryParameter(key));
                }
            }
        } catch (Exception e) {
            BroRuntimeLog.e(e.getMessage());
        }

        intent.putExtras(bundle);
    }
}
