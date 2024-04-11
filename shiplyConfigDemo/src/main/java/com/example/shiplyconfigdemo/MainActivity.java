package com.example.shiplyconfigdemo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.tencent.rdelivery.data.RDeliveryData;
import com.tencent.rdelivery.listener.FullReqResultListener;
import com.tencent.rdelivery.listener.MultiKeysReqResultListener;
import com.tencent.rdelivery.listener.SingleReqResultListener;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "RDelivery_MainActivity";
    private TextView remoteResult;
    private TextView localResult;
    private EditText etRemoteConfigName;
    private EditText etRemoteConfigList;
    private EditText etLocalConfigName;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == null) return;
            switch (v.getId()) {
                case R.id.req_single:
                    requestSingleConfig();
                    break;
                case R.id.req_multi:
                    requestMultiConfig();
                    break;
                case R.id.req_full:
                    requestFullConfig();
                    break;
                case R.id.get_local_switch:
                    getLocalSwitch();
                    break;
                case R.id.get_local_config:
                    getLocalConfig();
                    break;
                case R.id.get_local_data:
                    getLocalConfigSwitchData();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        remoteResult = findViewById(R.id.remote_result);
        localResult = findViewById(R.id.local_result);
        etRemoteConfigName = findViewById(R.id.et_remote_config_name);
        etRemoteConfigList = findViewById(R.id.et_remote_config_list);
        etLocalConfigName = findViewById(R.id.et_local_config_name);
        findViewById(R.id.req_single).setOnClickListener(listener);
        findViewById(R.id.req_multi).setOnClickListener(listener);
        findViewById(R.id.req_full).setOnClickListener(listener);
        findViewById(R.id.get_local_switch).setOnClickListener(listener);
        findViewById(R.id.get_local_config).setOnClickListener(listener);
        findViewById(R.id.get_local_data).setOnClickListener(listener);
    }

    private void requestSingleConfig() {
        String key = etRemoteConfigName.getText().toString();
        Log.d(TAG, "requestSingleConfig key = " + key);
        if (TextUtils.isEmpty(key)) {
            return;
        }
        RdeliveryHolder.getInstance().getRdelivery().requestSingleRemoteDataByKey(key, new SingleReqResultListener() {
            @Override
            public void onSuccess(RDeliveryData data) {
                remoteResult.post(new Runnable() {
                    @Override
                    public void run() {
                        remoteResult.setText("req single success");
                    }
                });
                Log.d(TAG, "requestSingleRemoteDataByKey onSuccess data = " + data);
            }

            @Override
            public void onFail(String reason) {
                remoteResult.post(new Runnable() {
                    @Override
                    public void run() {
                        remoteResult.setText("req single fail: " + reason);
                    }
                });
            }
        });
    }

    private void requestMultiConfig() {
        String key = etRemoteConfigList.getText().toString();
        Log.d(TAG, "requestMultiConfig key = " + key);
        if (TextUtils.isEmpty(key)) {
            return;
        }
        List<String> keyList = Arrays.asList(key.split(","));
        RdeliveryHolder.getInstance().getRdelivery().requestMultiRemoteData(keyList, new MultiKeysReqResultListener() {
            @Override
            public void onSuccess(List<RDeliveryData> datas) {
                remoteResult.post(new Runnable() {
                    @Override
                    public void run() {
                        remoteResult.setText("requestMultiConfig success datas = " + datas);
                    }
                });
            }

            @Override
            public void onFail(String reason) {
                remoteResult.post(new Runnable() {
                    @Override
                    public void run() {
                        remoteResult.setText("requestMultiConfig fail: " + reason);
                    }
                });
            }
        });
    }

    private void requestFullConfig() {
        RdeliveryHolder.getInstance().getRdelivery().requestFullRemoteData(new FullReqResultListener() {
            @Override
            public void onSuccess() {
                remoteResult.post(new Runnable() {
                    @Override
                    public void run() {
                        remoteResult.setText("req full success");
                    }
                });
                Log.d(TAG, "manual full req result, onSuccess without arg ");
            }

            @Override
            public void onSuccess(List<RDeliveryData> remainedDatas, List<RDeliveryData> updatedDatas, List<RDeliveryData> deletedDatas) {
                onSuccess();
                Log.d(TAG, "manual full req result, onSuccess with arg, updatedDatas = " + updatedDatas + " remainedDatas = " + remainedDatas);
            }

            @Override
            public void onFail(String reason) {
                remoteResult.post(new Runnable() {
                    @Override
                    public void run() {
                        remoteResult.setText("req full fail: " + reason);
                    }
                });
            }
        });
    }

    private String getLocalConfigKey() {
        String content = etLocalConfigName.getText().toString();
        Log.d(TAG, "getLocalConfigKey content = " + content);
        return content;
    }

    private void getLocalConfig() {
        String key = getLocalConfigKey();
        if (TextUtils.isEmpty(key)) {
            return;
        }
        String value = RdeliveryHolder.getInstance().getRdelivery().getStringByKey(key, null, true);
        localResult.setText("getLocalConfig key=" + key + " value=" + value);
    }

    private void getLocalSwitch() {
        String key = getLocalConfigKey();
        if (TextUtils.isEmpty(key)) {
            return;
        }
        boolean value = RdeliveryHolder.getInstance().getRdelivery().isOnByKey(key, false, true);
        localResult.setText("getLocalSwitch key=" + key + " value=" + value);
    }

    private void getLocalConfigSwitchData() {
        String key = getLocalConfigKey();
        if (TextUtils.isEmpty(key)) {
            return;
        }
        RDeliveryData value = RdeliveryHolder.getInstance().getRdelivery().getRDeliveryDataByKey(key, null, true);
        localResult.setText("getLocalConfigSwitchData key=" + key + " value=" + value);
    }

}