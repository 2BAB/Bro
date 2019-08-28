package me.xx2bab.bro.common;

import java.util.Map;

/**
 * Extra properties description for Bro annotations.
 * <p>
 * // @NeedLogin
 * // @BroActivity("Main")
 * class MainActivity {}
 * <p>
 * 1. clazz : is the full class name of MainActivity like com.example.MainActivity;
 * 2. extraParam : NeedLogin will be an extraParam, formatted by JSON, value is set only by value(),
 * a example maybe like {"com.company.NeedLogin":"blahblah"};
 */
public class BroProperties {

    public String clazz;
    public Map<String, Map<String, String>> extraAnnotations;

    public BroProperties(String clazz, Map<String, Map<String, String>> extraAnnotations) {
        this.clazz = clazz;
        this.extraAnnotations = extraAnnotations;
    }

    @Override
    public String toString() {
        return "BroProperties{" +
                "clazz='" + clazz + '\'' +
                ", extraAnnotations=" + extraAnnotations.toString() +
                '}';
    }
}
