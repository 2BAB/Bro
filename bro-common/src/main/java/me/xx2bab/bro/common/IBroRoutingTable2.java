package me.xx2bab.bro.common;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface IBroRoutingTable2 {

    Map<String, BroProperties> getRoutingMapByAnnotation(
            Class<? extends Annotation> annotation);

}
