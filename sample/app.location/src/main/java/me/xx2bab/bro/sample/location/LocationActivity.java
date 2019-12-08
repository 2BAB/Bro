package me.xx2bab.bro.sample.location;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;



// @BroActivity("broapp://settings")
// equals to manifest intent filter data
public class LocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getApplicationContext().getResources or get{$Anything} from Resources will be unstable in plugadget mode
        // Toast.makeText(this, getApplicationContext().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
        // suggest replacing getApplicationContext().getResources with XXXXXModule.getResources()
        Toast.makeText(this, getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
    }

}