package me.xx2bab.bro.core.base;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroModule;

public interface IBroInterceptor {

    boolean beforeFindActivity(@Nullable Context context,
                               String target,
                               Intent intent,
                               BroProperties properties);

    boolean beforeStartActivity(@Nullable Context context,
                                String target,
                                Intent intent,
                                BroProperties properties);

    boolean beforeGetApi(@Nullable Context context,
                         String target,
                         IBroApi api,
                         BroProperties properties);

    boolean beforeGetModule(@Nullable Context context,
                            String target,
                            IBroModule module,
                            BroProperties properties);

}
