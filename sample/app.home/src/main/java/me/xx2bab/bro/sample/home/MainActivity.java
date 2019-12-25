package me.xx2bab.bro.sample.home;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import me.xx2bab.bro.core.Bro;
import me.xx2bab.bro.core.activity.ActivityNaviProcessor;
import me.xx2bab.bro.sample.common.api.ILocationApi;
import me.xx2bab.bro.sample.common.api.ISettingsApi;
import me.xx2bab.bro.sample.common.api.ISingletonLocationApi;

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
//        ActivityNaviProcessor processor = Bro.get()
//                .startActivityFrom(this)
//                .withExtras(bundle)
//                .withFlags(flags)
//                .withCategory(category)
//                .forResult()
//                .dryRun()
//                .toUri(Uri.parse("broapp://settings"));
    }

    public void getApi(View view) {
        String location = Bro.get().getApi(ILocationApi.class).getUserCurrentLocation();
        Toast.makeText(this, location, Toast.LENGTH_SHORT).show();
    }

    public void getSingletonApi(View view) {
        String location = Bro.get().getApi(ISingletonLocationApi.class).getUserCurrentLocation();
        Toast.makeText(this, location, Toast.LENGTH_SHORT).show();
    }
}
