package me.xx2bab.bro.core.defaultor;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DefaultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String hint = ArgsParser.parseHintOfType(getIntent(), this);
        setContentView(generateDefaultView(this, hint));
    }

    private View generateDefaultView(Context context, String errorHint) {
        RelativeLayout root = new RelativeLayout(context);

        TextView notice = new TextView(context);
        if (errorHint == null) {
            notice.setText("?");
        } else {
            notice.setText(errorHint);
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        root.addView(notice, layoutParams);

        return root;
    }

}
