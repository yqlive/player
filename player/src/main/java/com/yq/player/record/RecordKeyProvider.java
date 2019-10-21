package com.yq.player.record;

import com.yq.player.entity.DataSource;

/**
 * Created by Taurus on 2018/12/12.
 */
public interface RecordKeyProvider {

    String generatorKey(DataSource dataSource);

}
