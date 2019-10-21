package com.yq.player.player;

import android.os.Bundle;
import com.yq.player.entity.DataSource;

/**
 * Created by Taurus on 2018/12/12.
 */
public interface IPlayerProxy {

    void onDataSourceReady(DataSource dataSource);

    void onIntentStop();

    void onIntentReset();

    void onIntentDestroy();

    void onPlayerEvent(int eventCode, Bundle bundle);

    void onErrorEvent(int eventCode, Bundle bundle);

    int getRecord(DataSource dataSource);

}
