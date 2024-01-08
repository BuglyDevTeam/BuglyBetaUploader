package com.devilwwj.plugintest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import com.tencent.upgrade.core.UpgradeManager;
import com.tencent.upgrade.core.UpgradeReqCallbackForUserManualCheck;

public class MainActivity extends Activity {

    private OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.btn_detect_upgrade:
                manualDetectUpgrade();
                break;
            default:
                break;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_detect_upgrade).setOnClickListener(onClickListener);
    }

    private void manualDetectUpgrade() {
        UpgradeManager.getInstance().checkUpgrade(true, null, new UpgradeReqCallbackForUserManualCheck());
    }
}
