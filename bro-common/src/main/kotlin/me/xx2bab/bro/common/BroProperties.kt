package me.xx2bab.bro.common

/**
 * Extra properties description for Bro annotations.
 *
 *
 * // @requireLoginSession
 * // @BroActivity("Main")
 * class MainActivity {}
 *
 *
 * 1. clazz: the full class name of MainActivity like com.example.MainActivity;
 * 2. module: the
 * 3. extraParam: requireLoginSession will be an extraParam, formatted by JSON, value is set only by value(),
 * a example maybe like {"com.company.NeedLogin":"blahblah"};
 */
data class BroProperties(var clazz: String,
                    var module: String,
                    var extraAnnotations: Map<String, Map<String, String>>) {
    override fun toString(): String {
        return "BroProperties{" +
                "clazz='" + clazz + '\'' +
                ", module=" + module +
                ", extraAnnotations=" + extraAnnotations +
                '}'
    }

}