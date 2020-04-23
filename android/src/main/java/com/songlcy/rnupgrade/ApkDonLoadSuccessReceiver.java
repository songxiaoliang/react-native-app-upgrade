package com.songlcy.rnupgrade;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Apk 下载成功 Notification 点击监听
 * Created by Song on 2020/04/23.
 */

public class ApkDonLoadSuccessReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        File apkFile = (File)intent.getSerializableExtra("apkFile");
        moveAppToFront(context);
        installAPk(apkFile, context);
    }

    private void moveAppToFront(Context context) {
        //获取ActivityManager
        ActivityManager mAm = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (rti.topActivity.getPackageName().equals(context.getPackageName())) {
                mAm.moveTaskToFront(rti.id, 0);
                return;
            }
        }
    }

    private void installAPk(File apkFile, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = context.getPackageName() + ".updateFileProvider";
            Uri apkUri = FileProvider.getUriForFile(context, authority, apkFile);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            //如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
            try {
                String[] command = {"chmod", "777", apkFile.toString()};
                ProcessBuilder builder = new ProcessBuilder(command);
                builder.start();
            } catch (IOException ignored) {
            }
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
