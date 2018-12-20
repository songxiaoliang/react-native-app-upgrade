package com.d3o.android_upgrade;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by Song on 2017/7/10.
 */
public class UpgradeModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext context;
    private static final String EVENT_NAME = "LOAD_PROGRESS";
    private String versionName = "1.0.0";
    private int versionCode = 1;

    public UpgradeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        PackageInfo pInfo = null;
        this.context = reactContext;
        try {
            pInfo = reactContext.getPackageManager().getPackageInfo(reactContext.getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "upgrade";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("versionName", versionName);
        constants.put("versionCode", versionCode);
        return constants;
    }

    @ReactMethod
    public void upgrade(String apkUrl) {
        UpdateDialog.goToDownload(context, apkUrl);
    }

    public static void sendProgress(int msg) {
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(EVENT_NAME, msg);
    }

}
