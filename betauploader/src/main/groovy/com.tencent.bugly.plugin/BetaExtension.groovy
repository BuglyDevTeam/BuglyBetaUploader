package com.tencent.bugly.plugin

/**
 * Beta extension params
 * @author wenjiewu
 */
public class BetaExtension {
    public String appId = null // AppID 【必选】
    public String appKey = null // AppKey 【必选】
    //  【option】
    // 【接口参数】
    public String title = null // 标题
    public String desc = null // 版本描述
    public int secret = 1 // 公开范围（1：所有人，2：密码，4管理员，5QQ群，6白名单）
    public String users = null // 如果公开范围是"QQ群"填QQ群号；如果公开范围是"白名单"填QQ号码，并使用;切分开，5000个以内。其他场景无需设置
    public String password = null // 密码(如果公开范围是"密码"需设置)
    public int download_limit = 1000 // 下载上限(大于0，默认1000)
    public String expId = null // 需替换安装包的版本id

    // 【插件配置】
    public String apkFile = null // 指定上传的apk文件
    public Boolean enable = true // 插件开关
    public Boolean autoUpload = false // 是否自动上传
    public Boolean debugOn = false // debug模式是否上传
}