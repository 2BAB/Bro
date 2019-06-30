package me.xx2bab.bro.common;

import java.util.HashMap;

public interface IBroRoutingTable {

    HashMap<String, BroProperties> getBroActivityMap();

    HashMap<String, BroProperties> getBroApiMap();

    HashMap<String, BroProperties> getBroModuleMap();
}
