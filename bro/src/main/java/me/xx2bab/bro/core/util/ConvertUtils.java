package me.xx2bab.bro.core.util;

import android.net.Uri;
import android.os.Bundle;

import java.util.*;

public class ConvertUtils {

    public static Bundle convertHashMapToBundle(HashMap<String, String> hashMap, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (hashMap != null) {
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
            }
        }
        return bundle;
    }

    public static Bundle convertHashMapToBundle(HashMap<String, String> hashMap) {
        Bundle bundle = new Bundle();
        return convertHashMapToBundle(hashMap, bundle);
    }

    public static String convertHashMapToUrlParams(HashMap<String, String> hashMap) {
        StringBuilder builder = new StringBuilder();
        if (hashMap != null) {
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            builder.delete(builder.length() - 1, builder.length());
        }
        return builder.toString();
    }

    public static String convertUriToStringWithoutParams(Uri uri) {
        String targetString = uri.toString();
        int questionMark = targetString.indexOf("?");
        if (questionMark > 0) {
            return targetString.substring(0, questionMark);
        } else {
            return targetString;
        }
    }

}
