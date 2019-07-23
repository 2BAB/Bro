package me.xx2bab.bro.common;

import java.lang.annotation.Annotation;
import java.util.HashMap;

public interface IBroRoutingTable2 {

    HashMap<String, BroProperties> getRoutingMapByAnnotation(
            Class<? extends Annotation> annotation);

}
