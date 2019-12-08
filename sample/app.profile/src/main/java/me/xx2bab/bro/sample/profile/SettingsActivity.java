package me.xx2bab.bro.sample.profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import me.xx2bab.bro.core.Bro;
import me.xx2bab.bro.sample.common.api.ILocationApi;


// @BroActivity("broapp://settings")
// equals to manifest intent filter data
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String location = Bro.get().getApi(ILocationApi.class).getUserCurrentLocation();
        Toast.makeText(this, "Current Location: " + location, Toast.LENGTH_SHORT).show();
    }

}