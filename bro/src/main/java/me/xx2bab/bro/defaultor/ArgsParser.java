package me.xx2bab.bro.defaultor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import me.xx2bab.bro.R;
import me.xx2bab.bro.util.Constants;

class ArgsParser {

    static String parseHintOfType(Intent intent, Context context) {
        int type = intent.getIntExtra(Constants.KEY_DEFAULT_PAGE_TYPE, Constants.DEFAULT_PAGE_NOT_FOUND);
        return parseHintOfType(type, context);
    }

    static String parseHintOfType(Bundle bundle, Context context) {
        int type = bundle.getInt(Constants.KEY_DEFAULT_PAGE_TYPE);
        return parseHintOfType(type, context);
    }

    private static String parseHintOfType(int type, Context context) {
        String hint;
        if (type == Constants.DEFAULT_PAGE_NOT_FOUND) {
            hint = context.getResources().getString(R.string.default_page_not_found);
        } else if (type == Constants.DEFAULT_PAGE_PERMISSION_DENIED) {
            hint = context.getResources().getString(R.string.default_page_permission_denied);
        } else if (type == Constants.DEFAULT_PAGE_NOT_NEW_INSTANCE_METHOD) {
            hint = context.getResources().getString(R.string.default_page_not_new_instance_method);
        } else if (type == Constants.DEFAULT_PAGE_UNKNOWN_ERROR) {
            hint = context.getResources().getString(R.string.default_page_unknown_error);
        } else {
            hint = context.getResources().getString(R.string.default_page_not_found);
        }
        return hint;
    }


}
