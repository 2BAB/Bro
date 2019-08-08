package me.xx2bab.bro.common.gen.anno;

import java.lang.annotation.Annotation;
import java.util.Map;

import me.xx2bab.bro.common.BroProperties;

public interface IBroAliasRoutingTable {

    Map<String, BroProperties> getRoutingMapByAnnotation(
            Class<? extends Annotation> annotation);

}
