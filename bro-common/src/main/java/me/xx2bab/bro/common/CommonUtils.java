package me.xx2bab.bro.common;

public class CommonUtils {

    public static String filterIllegalCharsForRawFileName(String origin) {
        return origin.replace(":", "_")
                .replace(".", "_")
                .replace("-", "_");
    }

}