package com.yq.player.base.record;


import com.yq.player.base.entity.DataSource;

/**
 * Created by Taurus on 2018/12/12.
 */
public interface RecordKeyProvider {

    String generatorKey(DataSource dataSource);

}
