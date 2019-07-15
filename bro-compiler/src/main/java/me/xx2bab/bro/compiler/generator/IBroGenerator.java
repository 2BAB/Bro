package me.xx2bab.bro.compiler.generator;

import java.util.Map;

import me.xx2bab.bro.common.BroProperties;

public interface IBroGenerator {

    void onGenerate(String broBuildDirectory, Map<String, Map<String, BroProperties>> exposeMaps);

}