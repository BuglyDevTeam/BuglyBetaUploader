package com.devilwwj.plugintest;

import android.app.Application;
import android.os.Build;
import com.tencent.upgrade.bean.UpgradeConfig;
import com.tencent.upgrade.core.DefaultUpgradeStrategyRequestCallback;
import com.tencent.upgrade.core.UpgradeManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by raymondhu on 2024/1/5
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initShiplyUpgradeSDK();
        autoDetectUpgrade();
    }

    private void initShiplyUpgradeSDK() {
        Map<String, String> map = new HashMap<>();
        map.put("UserAge", "18");
        UpgradeConfig.Builder builder = new UpgradeConfig.Builder();
        builder.appId(BuildConfig.SHIPLY_APPID) // 测试项目 appid
                .appKey(BuildConfig.SHIPLY_APPKEY) // 测试项目 appkey
                .systemVersion(String.valueOf(Build.VERSION.SDK_INT)) // 用户手机系统版本，用于匹配shiply前端创建任务时设置的系统版本下发条件
                .customParams(map) // 自定义属性键值对，用于匹配shiply前端创建任务时设置的自定义下发条件
                .cacheExpireTime(1000 * 60 * 60 * 6) // 灰度策略的缓存时长（ms），如果不设置，默认缓存时长为1天
                .internalInitMMKVForRDelivery(true) // 是否由sdk内部初始化mmkv(调用MMKV.initialize()),业务方如果已经初始化过mmkv，可以设置为false
                .userId("xxx") // 用户Id,用于匹配shiply前端创建的任务中的体验名单以及下发条件中的用户号码包
                .customLogger(new MyLogger()); // 日志实现接口，建议对接到业务方的日志接口，方便排查问题
        UpgradeManager.getInstance().init(MyApplication.this, builder.build());
    }

    private void autoDetectUpgrade() {
        UpgradeManager.getInstance().checkUpgrade(false, null, new DefaultUpgradeStrategyRequestCallback());
    }
}
