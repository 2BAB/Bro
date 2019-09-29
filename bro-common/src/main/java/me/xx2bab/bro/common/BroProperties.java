package me.xx2bab.bro.common;

import java.util.Map;

/**
 * Extra properties description for Bro annotations.
 * <p>
 * // @requireLoginSession
 * // @BroActivity("Main")
 * class MainActivity {}
 * <p>
 * 1. clazz: the full class name of MainActivity like com.example.MainActivity;
 * 2. module: the
 * 3. extraParam: requireLoginSession will be an extraParam, formatted by JSON, value is set only by value(),
 * a example maybe like {"com.company.NeedLogin":"blahblah"};
 */
public class BroProperties {

    public String clazz;
    public String module;
    public Map<String, Map<String, String>> extraAnnotations;

    public BroProperties(String clazz,
                         String module,
                         Map<String, Map<String, String>> extraAnnotations) {
        this.clazz = clazz;
        this.module = module;
        this.extraAnnotations = extraAnnotations;
    }

    @Override
    public String toString() {
        return "BroProperties{" +
                "clazz='" + clazz + '\'' +
                ", module=" + module +
                ", extraAnnotations=" + extraAnnotations +
                '}';
    }
}
