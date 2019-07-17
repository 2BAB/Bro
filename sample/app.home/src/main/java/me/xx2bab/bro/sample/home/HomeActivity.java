package me.xx2bab.bro.sample.home;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import me.xx2bab.bro.core.Bro;
import me.xx2bab.bro.annotations.BroActivity;
import me.xx2bab.bro.sample.common.api.ISettingsApi;
import me.xx2bab.bro.sample.common.mine.IMinePresenter;
import me.xx2bab.bro.sample.common.annotation.RequireLogin;

@RequireLogin(23333)
@BroActivity("broapp://home")
public class HomeActivity extends AppCompatActivity {

    private Fragment homeFragment;
    private IMinePresenter mineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.home_bottom_navigation);

        bottomNavigationView.inflateMenu(R.menu.navi_menu);
        bottomNavigationView.setItemBackgroundResource(R.color.bottom_bar_bg);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_home) {
                            getFragmentManager().beginTransaction().replace(R.id.home_fragment_container, homeFragment).commitAllowingStateLoss();
                        } else if (id == R.id.action_mine) {
                            getFragmentManager().beginTransaction().replace(R.id.home_fragment_container, (Fragment) mineFragment).commitAllowingStateLoss();
                            mineFragment.updateCount();
                        }
                        return true;
                    }
                });

        homeFragment = HomeFragment.newInstance(null);
        mineFragment = Bro.get().getApi(ISettingsApi.class).getMineFragment();

        getFragmentManager().beginTransaction().replace(R.id.home_fragment_container, homeFragment).commitAllowingStateLoss();
    }
}
