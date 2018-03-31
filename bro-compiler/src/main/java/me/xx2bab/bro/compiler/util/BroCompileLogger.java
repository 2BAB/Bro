package me.xx2bab.bro.compiler.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class BroCompileLogger {

    private static final String TAG = "== Bro == : ";
    private static Messager messager;

    public static void setMessager(Messager msg) {
        messager = msg;
    }

    public static void i(String msg) {
        String msgWithPrefix = TAG + "Info : " + msg;
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, msgWithPrefix);
        } else {
            System.out.println(msgWithPrefix);
        }
    }

    public static void e(String msg) {
        String msgWithPrefix = TAG + "Error : " + msg;
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.ERROR, msgWithPrefix);
        } else {
            System.err.println(msgWithPrefix);
        }
    }

}
