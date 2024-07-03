package com.example.shiplyresdemo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tencent.rdelivery.reshub.api.IBatchCallback
import com.tencent.rdelivery.reshub.api.IRes
import com.tencent.rdelivery.reshub.api.IResCallback
import com.tencent.rdelivery.reshub.api.IResHub
import com.tencent.rdelivery.reshub.api.IResLoadError
import com.tencent.rdelivery.reshub.core.ResHubCenter


class MainActivity : AppCompatActivity() {
    private lateinit var reshub: IResHub
    private lateinit var tvResourceContent: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 获取ResHub实例
        reshub = ResHubCenter.getResHub("760f8bca6b", "82787958-a6cc-4408-9897-814a0e7afd21")

        // 绑定按钮和EditText
        val loadButton: Button = findViewById(R.id.load)
        val loadLatestButton: Button = findViewById(R.id.loadLatest)
        val getButton: Button = findViewById(R.id.get)
        val getLatestButton: Button = findViewById(R.id.getLatest)

        val etRemoteResName: EditText = findViewById(R.id.et_remote_res_name)
        val etRemoteResLatestName: EditText = findViewById(R.id.et_remote_reslatest_name)
        val etGetResName: EditText = findViewById(R.id.et_get_res_name)
        val etGetResLatestName: EditText = findViewById(R.id.et_get_resLatest_name)
        tvResourceContent = findViewById(R.id.tv_resource_content)

        // 设置按钮点击事件
        loadButton.setOnClickListener {
            val resId = etRemoteResName.text.toString()
            if (resId.contains(",")) {
                val resIds = resId.split(",").map { it.trim() }.toSet()
                batchLoadResources(resIds)
            } else {
                loadResource(resId)
            }
        }

        loadLatestButton.setOnClickListener {
            val resId = etRemoteResLatestName.text.toString()
            if (resId.contains(",")) {
                val resIds = resId.split(",").map { it.trim() }.toSet()
                batchLoadLatestResources(resIds)
            } else {
                loadLatestResource(resId)
            }
        }

        getButton.setOnClickListener {
            val resId = etGetResName.text.toString()
            getResource(resId)
        }

        getLatestButton.setOnClickListener {
            val resId = etGetResLatestName.text.toString()
            getLatestResource(resId)
        }
    }

    private fun loadResource(resId: String) {
        reshub.load(resId, object : IResCallback {
            override fun onProgress(progress: Float) {
                Log.d("ResHubLoad", "资源加载进度: $progress")
            }

            override fun onComplete(isSuccess: Boolean, result: IRes?, error: IResLoadError) {
                if (isSuccess) {
                    Log.d("ResHubLoad", "资源拉取成功")
                    showCustomToast("资源拉取成功")
                } else {
                    Log.e("ResHubLoad", "资源拉取失败")
                    showCustomToast("资源拉取失败")
                }
            }
        })
    }

    private fun loadLatestResource(resId: String) {
        reshub.loadLatest(resId, object : IResCallback {
            override fun onProgress(progress: Float) {
                Log.d("ResHubLoad", "资源加载进度: $progress")
            }

            override fun onComplete(isSuccess: Boolean, result: IRes?, error: IResLoadError) {
                if (isSuccess) {
                    Log.d("ResHubLoad", "资源拉取成功")
                    showCustomToast("资源拉取成功")
                } else {
                    Log.e("ResHubLoad", "资源拉取失败")
                    showCustomToast("资源拉取失败")
                }
            }
        })
    }

    private fun getResource(resId: String) {
        val res = reshub.get(resId)
        if (res != null) {
            Log.d("ResHubGet", "成功获取本地资源: $res")
            showCustomToast("资源拉取成功")
            updateResourceContent(res)
        } else {
            Log.e("ResHubGet", "获取本地资源失败")
            showCustomToast("资源拉取失败")
        }
    }

    private fun getLatestResource(resId: String) {
        val res = reshub.getLatest(resId)
        if (res != null) {
            Log.d("ResHubGet", "成功获取本地最新资源: $res")
            showCustomToast("资源拉取成功")
            updateResourceContent(res)
        } else {
            Log.e("ResHubGet", "获取本地最新资源失败")
            showCustomToast("资源拉取失败")
        }
    }

    private fun batchLoadResources(resIds: Set<String>) {
        reshub.batchLoad(resIds, object : IBatchCallback {
            override fun onProgress(completed: Int, total: Int, progress: Float) {
                Log.d("ResHubBatchLoad", "批量资源加载进度: $progress ($completed/$total)")
            }

            override fun onComplete(
                isAllSuccess: Boolean,
                resMap: Map<String, IRes>,
                errorMap: Map<String, IResLoadError>
            ) {
                if (isAllSuccess) {
                    Log.d("ResHubBatchLoad", "批量资源拉取成功")
                    showCustomToast("批量资源拉取成功")
                } else {
                    Log.e("ResHubBatchLoad", "批量资源拉取失败")
                    showCustomToast("批量资源拉取失败")
                    for ((resId, error) in errorMap) {
                        Log.e("ResHubBatchLoad", "资源 $resId 拉取失败: $error")
                    }
                }
            }
        })
    }

    private fun batchLoadLatestResources(resIds: Set<String>) {
        reshub.batchLoadLatest(resIds, object : IBatchCallback {
            override fun onProgress(completed: Int, total: Int, progress: Float) {
                Log.d("ResHubBatchLoad", "批量资源加载进度: $progress ($completed/$total)")
            }

            override fun onComplete(
                isAllSuccess: Boolean,
                resMap: Map<String, IRes>,
                errorMap: Map<String, IResLoadError>
            ) {
                if (isAllSuccess) {
                    Log.d("ResHubBatchLoad", "批量资源拉取成功")
                    showCustomToast("批量资源拉取成功")
                } else {
                    Log.e("ResHubBatchLoad", "批量资源拉取失败")
                    showCustomToast("批量资源拉取失败")
                    for ((resId, error) in errorMap) {
                        Log.e("ResHubBatchLoad", "资源 $resId 拉取失败: $error")
                    }
                }
            }
        })
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

        tvResourceContent.text = content
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