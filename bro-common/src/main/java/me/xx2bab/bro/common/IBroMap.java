package me.xx2bab.bro.common;

import java.util.HashMap;

public interface IBroMap {

    HashMap<String, BroProperties> getBroActivityMap();

    HashMap<String, BroProperties> getBroApiMap();

    HashMap<String, BroProperties> getBroModuleMap();
}
