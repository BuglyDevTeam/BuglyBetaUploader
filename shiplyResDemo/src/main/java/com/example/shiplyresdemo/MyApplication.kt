package com.example.shiplyresdemo

import android.app.Application
import android.util.Log
import com.tencent.mmkv.MMKV
import com.tencent.rdelivery.dependencyimpl.SystemLog
import com.tencent.rdelivery.reshub.api.ResHubParams
import com.tencent.rdelivery.reshub.core.ResHubCenter
import com.tencent.rdelivery.reshub.net.ResHubDefaultDownloadImpl
import com.tencent.rdelivery.reshub.processor.TryPatchProcessor
import com.tencent.rdelivery.reshub.report.ResHubDefaultReportImpl

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        resHubSetUpInit()
    }

    private fun resHubSetUpInit() {
        try {
            ResHubCenter.init(
                context = this,                                     // 注入context
                params = getParamss(),                              // 注入配置字段
                downloadDelegate = ResHubDefaultDownloadImpl(),     // 注入下载能力，建议由宿主实现接口
                reportDelegate = ResHubDefaultReportImpl()          // 注入上报能力，建议由宿主实现接口
            )
            ResHubCenter.logDelegate = SystemLog()
            setPatchProcessor()                                     // 可选项，是否需要差量包更新能力
            Log.d("ResHubInit", "ResHubCenter 初始化成功")
        } catch (e: Exception) {
            Log.e("ResHubInit", "ResHubCenter 初始化失败", e)
        }
    }

    private fun getParamss(): ResHubParams {
        return ResHubParams(
            deviceId = getYourDeviceId(),   // 设备ID，
            appVersion = getVersionName(),  // APP版本号
            isRdmTest = isDebugMode(),      // 是否是debug包
            multiProcessMode = true         // 是否是多进程模式
        )
    }

    private fun setPatchProcessor() {
        ResHubCenter.injectProcessor(listOf(TryPatchProcessor()))
    }

    private fun getYourDeviceId(): String {
        // 实现获取设备ID的方法
        return "设备ID"
    }

    private fun getVersionName(): String {
        // 实现获取版本号的方法
        return "APP版本号"
    }

    private fun isDebugMode(): Boolean {
        // 实现判断是否是debug模式的方法
        return false
    }
}
