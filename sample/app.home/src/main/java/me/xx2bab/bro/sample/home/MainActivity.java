package me.xx2bab.bro.sample.home;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import me.xx2bab.bro.core.Bro;
import me.xx2bab.bro.sample.common.api.ISettingsApi;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startActivityUsingAnnotation(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("bundleparam", "123");
        Bro.get().startActivityFrom(this)
                .withExtras(bundle)
                .toUri(Uri.parse("broapp://home?urlparam=233"));
    }

    public void startActivityUsingIntentFilter(View view) {
        Bro.get().startActivityFrom(this).toUri(Uri.parse("broapp://settings"));
    }

    public void getApi(View view) {
        double pi = Bro.get().getApi(ISettingsApi.class).getPi();
        Toast.makeText(this, pi + "", Toast.LENGTH_SHORT).show();
    }

}
