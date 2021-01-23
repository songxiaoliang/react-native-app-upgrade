package com.songlcy.rnupgrade;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.songlcy.rnupgrade.Constants.Constants;
import com.songlcy.rnupgrade.utils.ApkInstallUtils;
import com.songlcy.rnupgrade.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Song on 2017/7/10.
 * Update by Song on 2021/01/21.19:58
 */
public class UpgradeModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext context;
    private String versionName = "1.0.0";
    private int versionCode = 1;

    public UpgradeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
        try {
            PackageInfo pInfo = reactContext.getPackageManager().getPackageInfo(reactContext.getPackageName(), 0);
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
        String apkFilePath = FileUtils.getCacheDirectory(this.context).getAbsolutePath() + Constants.APK_FILE_DIR_NAME;
        constants.put("versionName", versionName);
        constants.put("versionCode", versionCode);
        constants.put("downloadApkFilePath", apkFilePath);
        return constants;
    }

    /**
     * RN调用安装apk
     * @param filePath
     */
    @ReactMethod
    public void installApk(String filePath) {
        ApkInstallUtils.install(context, filePath);
    }
}
