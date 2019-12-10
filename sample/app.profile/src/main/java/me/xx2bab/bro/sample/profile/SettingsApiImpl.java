package me.xx2bab.bro.sample.profile;

import android.util.Log;

import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.annotations.BroSingleton;
import me.xx2bab.bro.sample.common.api.ISettingsApi;
import me.xx2bab.bro.sample.common.mine.IMinePresenter;

@BroSingleton
@BroApi(module = SettingsModule.class)
public class SettingsApiImpl implements ISettingsApi {

    @Override
    public int getPi() {
        return 314159;
    }

    @Override
    public IMinePresenter getProfileFragment() {
        return ProfilePresenterFragment.newInstance(null);
    }

    @Override
    public int getBaseValue() {
        return -65535;
    }

    @Override
    public void onCreate() {
        Log.e("SettingsApiImpl", "onInit");
    }

}
