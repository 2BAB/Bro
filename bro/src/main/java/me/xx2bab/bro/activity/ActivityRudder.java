package me.xx2bab.bro.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.Set;

import me.xx2bab.bro.Bro;
import me.xx2bab.bro.base.BroErrorType;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.util.BroRuntimeLog;
import me.xx2bab.bro.util.ConvertUtils;

public class ActivityRudder {

    private Builder builder;
    private Intent intent;
    private boolean validated;
    private boolean intercepted;

    @SuppressLint("WrongConstant")
    public ActivityRudder(Builder builder) {
        this.builder = builder;
        this.validated = true;
        this.intercepted = false;

        if (builder.context == null || builder.targetUri == null) {
            this.validated = false;
            Bro.getBroMonitor().onActivityRudderException(BroErrorType.PAGE_MISSING_ARGUMENTS, null);
            return;
        }

        String name = ConvertUtils.convertUriToStringWithoutParams(builder.targetUri);
        BroProperties properties = Bro.getBroMap().getBroActivityMap().get(name); // may be null

        intent = new Intent();
        intent.setData(builder.targetUri);

        try {
            if (Bro.getBroInterceptor().onFindActivity(builder.context, builder.targetUri.toString(), intent, properties)) {
                this.intercepted = true;
                return;
            }
        } catch (Exception e) {
            BroRuntimeLog.e(e.getMessage());
        }

        intent = findActivity(intent);

        if (intent == null) {
            this.validated = false;
            Bro.getBroMonitor().onActivityRudderException(BroErrorType.PAGE_CANT_FIND_TARGET, builder);
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
            if (Bro.getBroInterceptor().onStartActivity(builder.context, builder.targetUri.toString(), intent, properties)) {
                this.intercepted = true;
                return;
            }
        } catch (Exception e) {
            BroRuntimeLog.e(e.getMessage());
        }

        if (builder.justForCheck) {
            return;
        }

        if (builder.context instanceof Activity) {
            if (builder.requestCode > 0) {
                ((Activity) builder.context).startActivityForResult(intent, builder.requestCode);
            } else {
                ((Activity) builder.context).startActivity(intent); // cast is necessary
            }
            if (Bro.getConfig().getActivityTransition() != null) {
                int enter = Bro.getConfig().getActivityTransition()[0];
                int exit = Bro.getConfig().getActivityTransition()[1];
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
        for (IActivityFinder finder : Bro.getConfig().getActivityFinders()) {
            Intent temp = new Intent(intent);
            temp = finder.find(builder.context, temp);
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

    public static class Builder {

        private static final int INVALIDATE = -128;

        private Context context;
        private String category;
        private Bundle extras;
        private int flags = INVALIDATE;
        private int requestCode = INVALIDATE;
        private boolean justForCheck = false;
        private Uri targetUri;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder withCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder withExtras(Bundle extras) {
            this.extras = extras;
            return this;
        }

        public Builder withFlags(int flags) {
            this.flags = flags;
            return this;
        }

        public Builder forResult(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder justForCheck() {
            this.justForCheck = true;
            return this;
        }

        public ActivityRudder toUri(Uri targetUri) {
            this.targetUri = targetUri;
            return new ActivityRudder(this);
        }

        public ActivityRudder toUrl(String url) {
            try {
                Uri uri = Uri.parse(url);
                return toUri(uri);
            } catch (Exception e) {
                BroRuntimeLog.e(e.getMessage());
                Bro.getBroMonitor().onActivityRudderException(BroErrorType.PAGE_MISSING_ARGUMENTS, this);
                return new ActivityRudder(this);
            }
        }

        public Uri getUri() {
            return targetUri;
        }

        public String getCategory() {
            return category;
        }

        public Bundle getExtras() {
            return extras;
        }

        public int getFlags() {
            return flags;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public boolean isJustForCheck() {
            return justForCheck;
        }

    }


}
