package me.xx2bab.bro.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Extra properties description for Bro annotations.
 * <p>
 * @NeedLogin
 * @BroActivty("Main") 
 * class MainActivity {}
 * <p>
 * 1. clazz : is the full class name of MainActivity like com.example.MainActivity;
 * 2. extraParam : NeedLogin will be an extraParam,formatted by JSON, value is set only by value(),
 * a example maybe like {"com.company.NeedLogin":"blahblah"};
 */
public class BroProperties {

    public String clazz;
    public String extraParams;

    public BroProperties() {
    }

    public BroProperties(String clazz, String extraParams) {
        this.clazz = clazz;
        this.extraParams = extraParams;
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    public void fromJsonString(String jsonString) {
        JSONObject obj = (JSONObject) JSON.parse(jsonString);
        clazz = obj.getString("clazz");
        extraParams = obj.getString("extraParams");
        obj.clear();
    }

}
