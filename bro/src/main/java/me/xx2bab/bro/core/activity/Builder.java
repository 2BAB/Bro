package me.xx2bab.bro.core.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import me.xx2bab.bro.core.BroContext;
import me.xx2bab.bro.core.base.BroErrorType;
import me.xx2bab.bro.core.util.BroRuntimeLog;

public class Builder {

    static final int INVALIDATE = -128;

    Context context;
    String category;
    Bundle extras;
    int flags = INVALIDATE;
    int requestCode = INVALIDATE;
    boolean dryRun = false;
    Uri targetUri;
    BroContext broContext;

    public Builder(Context context, BroContext broContext) {
        this.context = context;
        this.broContext = broContext;
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

    public Builder dryRun() {
        this.dryRun = true;
        return this;
    }

    public ActivityNaviProcessor toUri(Uri targetUri) {
        this.targetUri = targetUri;
        return new ActivityNaviProcessor(this);
    }

    public ActivityNaviProcessor toUrl(String url) {
        try {
            Uri uri = Uri.parse(url);
            return toUri(uri);
        } catch (Exception e) {
            BroRuntimeLog.e(e.getMessage());
            broContext.monitor.onActivityRudderException(
                    BroErrorType.PAGE_MISSING_ARGUMENTS, this);
            return new ActivityNaviProcessor(this);
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

    public boolean isDryRun() {
        return dryRun;
    }

}
