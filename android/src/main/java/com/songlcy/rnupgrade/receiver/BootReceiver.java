package com.songlcy.rnupgrade.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      // 安装广播
      if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
        String packageName = intent.getDataString();
        // 安装了: packageName 包名的程序
      }
      // 重新安装广播
      if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
        String packageName = intent.getDataString();
        if(packageName == "com.laso.lasogene") {
          // TODO 覆盖安装成功后，删除本地 genebox.apk 文件
        }
      }
      // 卸载广播
      if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
        String packageName = intent.getDataString();
        // 卸载了: packageName 包名的程序
      }
    }
}
