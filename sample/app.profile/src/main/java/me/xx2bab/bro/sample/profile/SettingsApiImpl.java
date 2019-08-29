package me.xx2bab.bro.sample.profile;

import android.util.Log;

import java.util.List;

import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.sample.common.api.ISettingsApi;
import me.xx2bab.bro.sample.common.mine.IMinePresenter;

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
