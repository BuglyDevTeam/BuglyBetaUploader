package com.example.shiplyconfigdemo;


import android.app.Application;
import android.os.Build;
import com.tencent.rdelivery.DependencyInjector;
import com.tencent.rdelivery.RDelivery;
import com.tencent.rdelivery.RDeliverySetting;
import com.tencent.rdelivery.dependencyimpl.HandlerTask;
import com.tencent.rdelivery.dependencyimpl.HttpsURLConnectionNetwork;
import com.tencent.rdelivery.dependencyimpl.MmkvStorage.MmkvStorageFactory;
import com.tencent.rdelivery.dependencyimpl.MmkvStorageKt;
import com.tencent.rdelivery.dependencyimpl.SystemLog;
import com.tencent.rdelivery.listener.LocalDataInitListener;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initShiplyConfigSDK();
    }

    private void initShiplyConfigSDK() {
        //本地配置开关更新策略
        String hostAppId = "testAppId"; //在 RDelivery 前端页面申请的项目的 appid
        String hostAppKey = "testAppKey"; //在 RDelivery 前端页面申请的项目的 appkey
        String hostPacketName = com.example.shiplyconfigdemo.BuildConfig.APPLICATION_ID; //宿主的 app 包名
        String hostAppVersion = com.example.shiplyconfigdemo.BuildConfig.VERSION_NAME; //宿主的 app 版本
        String userId = "123321"; //用户id
        //当前手机是否是64位cpu
        boolean is64BitCpu = true;
        //宿主是否是debug包
        boolean isDebugPackage = com.example.shiplyconfigdemo.BuildConfig.DEBUG;

        /**
         * 固定间隔拉取策略会计算APP在后台的时长: APP从后台切换到前台时，如果APP在后台的时长超过拉取时间间隔，直接触发拉取；如果还没到时间间隔，按照剩余的时间间隔发起定时；
         * 为了节省请求量，建议业务方只设置 START_UP 和 PERIODIC 两种策略，更新时间间隔建议大于1小时；
         */
        int strategy =
                RDeliverySetting.UpdateStrategy.START_UP.getValue() //sdk 启动时拉取全量配置
                        | RDeliverySetting.UpdateStrategy.PERIODIC.getValue(); //按照固定时间间隔，周期拉取全量配置

        int updateInterval = 60 * 60 * 3; //更新时间间隔，单位为秒，更新策略包括 RDeliverySetting.UpdateStrategy.PERIODIC 才有效
        RDeliverySetting setting = new RDeliverySetting.Builder()
                .setAppId(hostAppId)
                .setAppKey(hostAppKey)
                .setUserId(userId)
                .setIsDebugPackage(isDebugPackage)
                .setUpdateStrategy(strategy)
                .setUpdateInterval(updateInterval)
                .setBundleId(hostPacketName)
                .setHostAppVersion(hostAppVersion)
                .setDevModel(Build.MODEL)
                .setDevManufacturer(Build.MANUFACTURER)
                .setAndroidSystemVersion(String.valueOf(Build.VERSION.SDK_INT))
                .setIs64BitCpu(is64BitCpu)
                .setEnableDetailLog(true) // 是否要打印详细日志，为了方便排查问题，建议开启，当对配置保密性要求高时可以关闭
                .build();
        MmkvStorageKt.initMMKV(getFilesDir().getAbsolutePath() + "/mmkv"); //初始化 mmkv
        //外部依赖
        DependencyInjector injector = new DependencyInjector(
                new HttpsURLConnectionNetwork(this), //网络接口的默认实现
                new MmkvStorageFactory(), //存储接口的默认实现
                new HandlerTask(), //任务调度接口的默认实现
                new SystemLog() //日志接口的默认实现
        );
        LocalDataInitListener listener = new LocalDataInitListener() { //本地缓存初始化结束回调接口
            @Override
            public void onInitFinish() {

            }
        };
        RDelivery rDelivery = RDelivery.create(this, setting, injector, listener);
        RdeliveryHolder.getInstance().setRdeliveryInstance(rDelivery);
    }
}
