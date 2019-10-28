# YQLivePlayer 引用说明


# 使用

需要的权限，如果targetSDK版本在Android M以上的，请注意运行时权限的处理。<br>

```xml
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

    <uses-permission android:name="android.permission.RECORD_AUDIO"/>
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>

    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
```

添加如下依赖<br>

```gradle
dependencies {
  implementation 'com.github.yqlive:player:0.0.10'
}
```

由于播放内核使用到了exoplayer的库，需要在gradle中增加如下配置。
```gradle
buildTypes {

    //...
    
    compileOptions{
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

**初始化**
***本SDK支持kotlin语法，以下说明以java示例，使用中可自行切换到自己项目的适用语法
```java
public class App extends Application {

    @Override
    public void onCreate() {
        //...
        
        //初始化库
        //端口配置为可选项，建议做修改
        LivePlayer.init(this,8080,5001,4001);
      
    }
    
}
```

使用LivePlayerView对象。

```xml
    <!--传统android的xml的布局方式 -->
    <android.support.constraint.ConstraintLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity">
        
        <com.yq.player.player.LivePlayerView
                android:id="@+id/player"
                app:layout_constraintTop_toTopOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                android:background="@drawable/blur_bg"
                android:layout_width="match_parent"
                android:layout_height="200dp">
        </com.yq.player.player.LivePlayerView>
    
    </android.support.constraint.ConstraintLayout>
```

```java
    //java语法下动态创建LivePlayerView
    LivePlayerView player= new LivePlayerView(context)
    player.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT))
    addView(player)
```

```kotlin
     //如果项目kotlin语法下则支持anko布局方式创建
     //方式1：kotilin语法下使用anko创建LivePlayerView
      customView<LivePlayerView> {
            backgroundColor = 0xFF000000.toInt()
        }.lparams(matchConstraint, matchConstraint) {
            topOf = parentId
            dimensionRatio = "16:9"
        }
     //方式2：kotlin语法下代码动态创建LivePlayerView
     val player = LivePlayerView(context)
        player.layoutParams=ViewGroup.LayoutParams(matchParent,dip(200))
        addView(player)
```


**获取比赛数据并播放**

```java
  player = findViewById(R.id.player)
  //使用中注意非空校验以及子线程和主线程间切换及通讯的问题
  Tribute<Live<Live>> tribute= apiService.lives().body
  if(tribute.isSucess){
    List<Live> lives = tribute.data.value
    Live live = lives.get(0)
    player.live = live
    player.resolution = live.resolutions[0].value
    player.start()
  }
```
