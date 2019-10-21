package com.yq.live

import android.app.Application
import com.yq.live.player.LivePlayer
import com.yq.player.log.PLog

/**
 * Created by Taurus on 2018/4/15.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        PLog.LOG_OPEN = true
        LivePlayer.init(this, 8989)
    }


    companion object {

        val PLAN_ID_IJK = 1
        val PLAN_ID_EXO = 2

        var ignoreMobile: Boolean = false

    }


}
