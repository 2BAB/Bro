package me.xx2bab.bro.sample.profile;

import android.util.Log;

import java.util.List;

import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.sample.common.annotation.RequireLogin;
import me.xx2bab.bro.sample.common.annotation.RequireMultiValues;
import me.xx2bab.bro.sample.common.api.ISettingsApi;
import me.xx2bab.bro.sample.common.mine.IMinePresenter;

@RequireLogin(123)
@RequireMultiValues(value = 1, value1 = "AString", value2 = 12345L, value3 = 'a', value4 = true)
@BroApi("SettingsApi")
public class SettingsApiImpl implements ISettingsApi {


    @Override
    public int getPi() {
        return 314159;
    }

    @Override
    public IMinePresenter getMineFragment() {
        return MinePresenterFragment.newInstance(null);
    }

    @Override
    public int getBaseValue() {
        return -65535;
    }

    @Override
    public void onInit() {
        Log.e("SettingsApiImpl", "onInit");
    }

    @Override
    public List<Class<? extends IBroApi>> onEvaluate() {
        return null;
    }
}
