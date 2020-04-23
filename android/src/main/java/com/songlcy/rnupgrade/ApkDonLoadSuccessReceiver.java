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
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancel(intent.getIntExtra("notificationId", -1));
        moveAppToFront(context);
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

}
