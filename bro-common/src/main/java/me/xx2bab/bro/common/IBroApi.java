package me.xx2bab.bro.common;

import java.util.List;

public interface IBroApi {

    void onInit();

    List<Class<? extends IBroApi>>  onEvaluate();

}