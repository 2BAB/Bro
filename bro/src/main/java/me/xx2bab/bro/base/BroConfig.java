package me.xx2bab.bro.base;

import java.util.ArrayList;
import java.util.List;

import me.xx2bab.bro.api.IApiFinder;
import me.xx2bab.bro.api.local.AnnoApiFinder;
import me.xx2bab.bro.api.remote.BinderApiFinder;
import me.xx2bab.bro.defaultor.DefaultActivity;
import me.xx2bab.bro.activity.AnnoActivityFinder;
import me.xx2bab.bro.activity.IActivityFinder;
import me.xx2bab.bro.activity.PackageManagerActivityFinder;

public class BroConfig {

    private boolean logEnabled = true;
    private boolean moduleAutoInitEnabled = true;
    private boolean apiAutoInitEnabled = true;
    private Class activityCls;
    private List<IActivityFinder> activityFinders;
    private List<IApiFinder> apiFinders;
    private int[] activityTransition;

    private BroConfig(Builder builder) {
        logEnabled = builder.logEnabled;
        moduleAutoInitEnabled = builder.moduleAutoInitEnabled;
        apiAutoInitEnabled = builder.apiAutoInitEnabled;
        activityCls = builder.activityCls;
        activityFinders = builder.activityFinders;
        apiFinders = builder.apiFinders;
        activityTransition = builder.activityTransition;

        if (activityFinders == null) {
            activityFinders = new ArrayList<>();
        }
        if (activityFinders.size() == 0) {
            activityFinders.add(new AnnoActivityFinder());
            activityFinders.add(new PackageManagerActivityFinder());
        }

        if (apiFinders == null) {
            apiFinders = new ArrayList<>();
        }
        if (apiFinders.size() == 0) {
            apiFinders.add(new AnnoApiFinder());
            apiFinders.add(new BinderApiFinder());
        }
    }

    public List<IApiFinder> getApiFinders() {
        return apiFinders;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public boolean isModuleAutoInitEnabled() {
        return moduleAutoInitEnabled;
    }

    public boolean isApiAutoInitEnabled() {
        return apiAutoInitEnabled;
    }

    public Class getDefaultActivity() {
        return activityCls;
    }

    public List<IActivityFinder> getActivityFinders() {
        return activityFinders;
    }

    public int[] getActivityTransition() {
        return activityTransition;
    }

    public static class Builder {

        private boolean logEnabled = true;
        private boolean moduleAutoInitEnabled = true;
        private boolean apiAutoInitEnabled = true;
        private Class activityCls = DefaultActivity.class;
        private List<IActivityFinder> activityFinders = new ArrayList<>();
        private List<IApiFinder> apiFinders = new ArrayList<>();
        private int[] activityTransition = new int[]{-1, -1};

        public Builder() {
        }

        public Builder setModuleAutoInitEnabled(boolean moduleAutoInitEnabled) {
            this.moduleAutoInitEnabled = moduleAutoInitEnabled;
            return this;
        }

        public Builder setApiClassesAutoInitEnabled(boolean apiAutoInitEnabled) {
            this.apiAutoInitEnabled = apiAutoInitEnabled;
            return this;
        }

        public Builder setLogEnable(boolean logEnabled) {
            this.logEnabled = logEnabled;
            return this;
        }

        public Builder setDefaultActivity(Class activityCls) {
            this.activityCls = activityCls;
            return this;
        }

        public Builder setActivityFinders(List<IActivityFinder> pageFinders) {
            this.activityFinders = pageFinders;
            return this;
        }

        public void setApiFinders(List<IApiFinder> apiFinders) {
            this.apiFinders = apiFinders;
        }

        public Builder setActivityTransition(int enterAnim, int exitAnim) {
            if (enterAnim > 0 && exitAnim > 0) {
                activityTransition[0] = enterAnim;
                activityTransition[1] = exitAnim;
            } else {
                throw new IllegalArgumentException("Bro ActivityRudder Transition Arguments is Illegal");
            }
            return this;
        }

        public BroConfig build() {
            return new BroConfig(this);
        }
    }
}
