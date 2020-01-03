package com.yq.player.ipfs

import android.content.Context
import java.io.File

interface IpfsWriter{

    fun write(context: Context,path:File)

}