package com.tencent.bugly.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import groovy.json.JsonSlurper

/**
 * {@code BetaPlugin} is a gradle plugin for uploading apk file to Bugly platform
 *
 * <p> The plugin will create a task "upload${variantName}BetaApkFile.
 * ({@code "variantname"} means the name of variant. e.g.,"Release")  for doing following things:
 * <p> 1.upload apk file to bugly platform.
 *
 * if you want to upload release apk file ,you must set the signingConfigs on build.gradle, like this:
 *  signingConfigs {*    release {*          storeFile file("your keystore file")
 *          storePassword "your keystore password"
 *          keyAlias "your key alias"
 *          keyPassword "your key password"
 *}*}*
 * <p>The plugin should be configured through the following required properties:
 * <p>{@code appId}: app ID of Bugly platform.
 * <p>{@code appKey}: app Key of Bugly platform.
 * <p>Other optional properties:
 * <p>{@code title}: title of the version
 * <p>{@code desc}: description of the version
 * <p>{@code enable}: switch for controlling execution of "upload${variantName}BetaApkFile".
 * <p>{@code apkFile}: the file path you want to set
 * <p>{@code secret}: the open range 1: all 2:password 4:administrator 5: QQ group 6: white list
 * <p>{@code users}: if the open range was QQ group, you should set users like this : users = "group num1group num2, ..."
 * if the open range was white list, you should set users like this: users = "qq num1;qq num2;...", other scene no need to set
 * <p>{@code password}: if you set secret to 2, you should set the password
 * <p>{@code download_limit}: download limit, default 1000
 * <p>{@code expId}: the apk id you has been upload
 * @author wenjiewu
 */
public class BetaPlugin implements Plugin<Project> {
    private Project project = null;

    // URL for uploading apk file
    private static final String APK_UPLOAD_URL = "https://api.bugly.qq.com/beta/apiv1/exp?app_key=";
    private static final String APK_UPLOAD_URL2 = "https://api.bugly.qq.com/beta2/apiv1/exp?app_key=";


    @Override
    void apply(Project project) {
        this.project = project
        // 接收外部参数
        project.extensions.create("beta", BetaExtension)

        // 取得外部参数
        if (project.android.hasProperty("applicationVariants")) { // For android application.
            project.android.applicationVariants.all { variant ->
                String variantName = variant.name.capitalize()

                // Check for execution
                if (false == project.beta.enable) {
                    project.logger.error("Bugly: beta gradle enable is false, if you want to auto upload apk file, you should set the execute = true")
                    return
                }

                // Create task.
                Task betaTask = createUploadTask(variant)

                // Check autoUpload
                if (!project.beta.autoUpload) {
                    // dependsOn task
                    betaTask.dependsOn project.tasks["assemble${variantName}"]
                } else {
                    // autoUpload after assemble
                    project.tasks["assemble${variantName}"].doLast {
                        // if debug model and debugOn = false no execute upload
                        if (variantName.contains("Debug") && !project.beta.debugOn) {
                            println("Bugly: the option debugOn is closed, if you want to upload apk file on debug model, you can set debugOn = true to open it")
                            return
                        }

                        if (variantName.contains("Release")) {
                            println("Bugly: the option autoUpload is opened, it will auto upload the release to the bugly platform")
                        }
                        uploadApk(generateUploadInfo(variant))

                    }
                }
            }
        }
    }

    /**
     * generate upload info
     * @param variant
     * @return
     */
    public UploadInfo generateUploadInfo(Object variant) {
//        def manifestFile = variant.outputs.processManifest.manifestOutputFile[0]
//        println("-> Manifest: " + manifestFile)
//        println("VersionCode: " + variant.getVersionCode() + " VersionName: " + variant.getVersionName())

        UploadInfo uploadInfo = new UploadInfo()
        uploadInfo.appId = project.beta.appId
        uploadInfo.appKey = project.beta.appKey
        if (project.beta.title == null) {
            uploadInfo.title = project.getName() + "-" + variant.getVersionName() + variant.getVersionCode()
        } else {
            uploadInfo.title = project.beta.title
        }

        if (project.beta.desc == null) {
            uploadInfo.description = ""
        } else {
            uploadInfo.description = project.beta.desc
        }

        uploadInfo.secret = project.beta.secret
        uploadInfo.users = project.beta.users
        uploadInfo.password = project.beta.password
        uploadInfo.download_limit = project.beta.download_limit
        // if you not set apkFile, default get the assemble output file
        if (project.beta.apkFile != null) {
            uploadInfo.sourceFile = project.beta.apkFile
            println("Bugly: you has set the custom apkFile")
            println("Bugly: your apk absolutepath :" + project.beta.apkFile)
        } else {
            File apkFile = variant.outputs[0].outputFile
            uploadInfo.sourceFile = apkFile.getAbsolutePath()
            println("Bugly: the apkFile is default set to build file")
            println("Bugly: your apk absolutepath :" + apkFile.getAbsolutePath())
        }

        if (project.beta.expId != null) {
            uploadInfo.expId = project.beta.expId
        }

        return uploadInfo
    }

    /**
     * 创建上传任务
     *
     * @param variant 编译参数
     * @return
     */
    private Task createUploadTask(Object variant) {
        String variantName = variant.name.capitalize()
        Task uploadTask = project.tasks.create("upload${variantName}BetaApkFile") << {
            // if debug model and debugOn = false no execute upload
            if (variantName.contains("Debug") && !project.beta.debugOn) {
                println("Bugly: the option debugOn is closed, if you want to upload apk file on debug model, you can set debugOn = true to open it")
                return
            }
            uploadApk(generateUploadInfo(variant))
        }
        println("Bugly:create upload${variantName}BetaApkFile task")
        return uploadTask
    }

    /**
     *  上传apk
     * @param uploadInfo
     * @return
     */
    public boolean uploadApk(UploadInfo uploadInfo) {
        // 拼接url如：https://api.bugly.qq.com/beta/apiv1/exp?app_key=bQvYLRrBNiqUctfi
        String url = APK_UPLOAD_URL + uploadInfo.appKey
        if (project.beta.expId != null) {
             url = APK_UPLOAD_URL2 + uploadInfo.appKey;
        }
        println("Bugly: Apk start uploading....")
        //
        if (uploadInfo.appId == null) {
            project.logger.error("Please set the app id, eg: appId = \"900037672\"")
            return false
        }

        if (uploadInfo.appKey == null) {
            project.logger.error("Please set app key, eg: appKey = \"bQvYLRrBNiqUctfi\"")
            return false
        }

        if (uploadInfo.secret == Constants.OPEN_FOR_PASSWORD) {
            if (uploadInfo.password == null) {
                project.logger.error("your apk download open for password, you must set the password")
                return false
            }
        } else if (uploadInfo.secret == Constants.OPEN_FOR_QQGROUP) {
            if (uploadInfo.users == null) {
                project.logger.error("your apk download open for qq group, you must set the users")
                return false
            }
        } else if (uploadInfo.secret == Constants.OPEN_FOR_WHITELIST) {
            if (uploadInfo.users == null) {
                project.logger.error("your apk download open for white list, you must set the users")
                return false
            }
        }
        println("Bugly:" + uploadInfo.toString())

        if (!post(url, uploadInfo.sourceFile, uploadInfo)) {
            project.logger.error("Bugly: Failed to upload!")
            return false
        } else {
            println("Bugly: upload apk success !!!")
            return true
        }
    }

    /**
     * 上传apk
     * @param url 地址
     * @param filePath 文件路径
     * @param uploadInfo 更新信息
     * @return
     */
    public boolean post(String url, String filePath, UploadInfo uploadInfo) {
        HttpURLConnectionUtil connectionUtil = new HttpURLConnectionUtil(url, Constants.HTTPMETHOD_POST);
        if (uploadInfo.expId != null) {
            connectionUtil.addTextParameter(Constants.EXP_ID, uploadInfo.expId);
            connectionUtil.setHttpMethod(Constants.HTTPMETHOD_PUT)
        } else {
            connectionUtil.addTextParameter(Constants.APP_ID, uploadInfo.appId);
        }
        connectionUtil.addTextParameter(Constants.PLATFORM_ID, uploadInfo.pid);
        connectionUtil.addTextParameter(Constants.TITLE, uploadInfo.title);
        connectionUtil.addTextParameter(Constants.DESCRIPTION, uploadInfo.description);
        connectionUtil.addTextParameter(Constants.SECRET, String.valueOf(uploadInfo.secret));
        connectionUtil.addTextParameter(Constants.USERS, uploadInfo.users);
        connectionUtil.addTextParameter(Constants.PASSWORD, uploadInfo.password);
        connectionUtil.addTextParameter(Constants.DOWNLOAD_LIMIT, String.valueOf(uploadInfo.download_limit));

        connectionUtil.addFileParameter(Constants.FILE, new File(filePath));

        String result = new String(connectionUtil.post(), "UTF-8");
        def data = new JsonSlurper().parseText(result)
        if (data.rtcode == 0) {
            println("Bugly --->share url: " + data.data.url)
            return true
        }
        return false;
    }


    private static class UploadInfo {
        // App ID of Bugly platform.
        public String appId = null
        // App Key of Bugly platform.
        public String appKey = null
        // platform id
        public String pid = "1"
        // Name of apk file to upload.
        public String sourceFile = null
        // app version title
        public String title = null
        // app version description [option]
        public String description = null
        // app secret level
        public int secret = 0
        // if open range was qq group set users to qq group num separate by ';' eg: 13244;23456;43843
        // if open range was qq num set users to qq num separate by ';' eg: 1000136; 10000148;1888432
        public String users = null
        // if open range was password you must set password
        public String password = null
        // download limit [option] default 10000
        public int download_limit = 10000
        // exp id
        public String expId = null


        @Override
        public String toString() {
            return "UploadInfo{" +
                    "appId='" + appId + '\'' +
                    ", appKey='" + appKey + '\'' +
                    ", pid='" + pid + '\'' +
                    ", apkFile='" + sourceFile + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", secret=" + secret +
                    ", users='" + users + '\'' +
                    ", password='" + password + '\'' +
                    ", download_limit=" + download_limit +
                    ", expId='" + expId + '\'' +
                    '}';
        }
    }


}
