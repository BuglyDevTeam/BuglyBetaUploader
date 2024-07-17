package com.example.shiplyresdemo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.tencent.rdelivery.reshub.api.IRes
import com.tencent.rdelivery.reshub.api.IResCallback
import com.tencent.rdelivery.reshub.api.IResHub
import com.tencent.rdelivery.reshub.api.IResLoadError
import com.tencent.rdelivery.reshub.core.ResHubCenter

class MainActivity : Activity() {
    private lateinit var reshub: IResHub
    private lateinit var tvRemoteResult: TextView
    private lateinit var tvLocalResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 获取ResHub实例
        reshub = ResHubCenter.getResHub("appId", "appKey")
        // 绑定按钮和EditText
        val loadButton: Button = findViewById(R.id.load)
        val getButton: Button = findViewById(R.id.get)
        val loadLatestButton:Button = findViewById(R.id.loadLatest)
        val getLatestButton:Button = findViewById(R.id.getLatest)

        val etRemoteResName: EditText = findViewById(R.id.et_remote_res_name)
        val etGetResName: EditText = findViewById(R.id.et_get_res_name)
        tvRemoteResult = findViewById(R.id.tv_remote_result)
        tvLocalResult = findViewById(R.id.tv_local_result)
        // 设置按钮点击事件
        loadButton.setOnClickListener {
            val resId = etRemoteResName.text.toString()
            loadResource(resId)
        }

        getButton.setOnClickListener {
            val resId = etGetResName.text.toString()
            getResource(resId)
        }
        loadLatestButton.setOnClickListener {
            val resId = etRemoteResName.text.toString()
            loadLatestResource(resId)
        }

        getLatestButton.setOnClickListener {
            val resId = etGetResName.text.toString()
            getLatestResource(resId)
        }
    }

    private fun loadLatestResource(resId: String) {
        reshub.loadLatest(resId, object : IResCallback {
            override fun onProgress(progress: Float) {
                Log.d("ResHubLoad", "资源加载进度: $progress")
            }
            override fun onComplete(isSuccess: Boolean, result: IRes?, error: IResLoadError) {
                if (isSuccess) {
                    Log.d("ResHubLoad", "资源拉取成功")
                    showCustomToast("异步最新资源拉取成功")
                    if (result != null){
                        tvRemoteResult.text = "异步最新资源拉取成功，文件路径：${result.getLocalPath()}"
                    }
                } else {
                    Log.e("ResHubLoad", "异步最新资源拉取失败")
                    showCustomToast("异步最新资源拉取失败")
                    tvRemoteResult.text = "异步最新资源拉取失败"
                }
            }
        })
    }

    private fun loadResource(resId: String) {
        reshub.load(resId, object : IResCallback {
            override fun onProgress(progress: Float) {
                Log.d("ResHubLoad", "资源加载进度: $progress")
            }

            override fun onComplete(isSuccess: Boolean, result: IRes?, error: IResLoadError) {
                if (isSuccess) {
                    Log.d("ResHubLoad", "异步资源拉取成功")
                    showCustomToast("异步资源拉取成功")
                    if (result != null){
                        tvRemoteResult.text = "异步资源拉取成功，文件路径：${result.getLocalPath()}"
                    }
                } else {
                    Log.e("ResHubLoad", "异步资源拉取失败")
                    showCustomToast("异步资源拉取失败")
                    tvRemoteResult.text = "异步资源拉取失败"
                }
            }
        })
    }

    private fun getResource(resId: String) {
        val res = reshub.get(resId)
        if (res != null) {
            Log.d("ResHubGet", "同步资源获取成功: ${res.getLocalPath()}")
            showCustomToast("同步资源获取成功")
            updateResourceContent(res)
        } else {
            Log.e("ResHubGet", "同步资源获取失败")
            showCustomToast("同步资源获取失败")
            tvLocalResult.text = "同步资源获取失败"
        }
    }

    private fun getLatestResource(resId: String) {
        val res = reshub.getLatest(resId)
        if (res != null) {
            Log.d("ResHubGet", "成功获取同步最新资源: ${res.getLocalPath()}")
            showCustomToast("同步最新资源获取成功")
            updateResourceContent(res)
        } else {
            Log.e("ResHubGet", "获取同步最新资源失败")
            showCustomToast("同步最新资源获取失败")
            tvLocalResult.text = "同步最新资源获取失败"
        }
    }

    private fun updateResourceContent(res: IRes) {
        val content = """
        ResId: ${res.getResId()}
        LocalPath: ${res.getLocalPath()}
        Version: ${res.getVersion()}
        Size: ${res.getSize()}
        MD5: ${res.getMd5()}
        DownloadUrl: ${res.getDownloadUrl()}
        FileExtra: ${res.getFileExtra()}
        ResType: ${res.getResType()}
        Description: ${res.getDescription()}
        TaskId: ${res.getTaskId()}
    """.trimIndent()
        tvLocalResult.text = content
    }

    private fun showCustomToast(message: String) {
        val inflater: LayoutInflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container))
        val toastIcon: ImageView = layout.findViewById(R.id.toast_icon)
        val toastText: TextView = layout.findViewById(R.id.toast_text)
        toastIcon.setImageResource(R.drawable.ic_launcher)
        toastText.text = message
        with (Toast(applicationContext)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }
}