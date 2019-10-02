package me.xx2bab.bro.sample.profile;

import android.util.Log;

import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.core.Bro;
import me.xx2bab.bro.sample.common.api.ISettingsApi;
import me.xx2bab.bro.sample.common.mine.IMinePresenter;

@BroApi(module = SettingsModule.class)
public class SettingsApiImpl implements ISettingsApi {

    @Override
    public double getPi() {
        return Bro.get()
                .getModule(SettingsModule.class)
                .getDependencyBySingleton(PiCalculator.class)
                .calculate();
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
    public void onCreate() {
        Log.e("SettingsApiImpl", "onInit");
    }

}
