package com.songlcy.rnupgrade;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.core.content.FileProvider;

//import android.support.v4.app.NotificationCompat.Builder;
//import android.support.v4.content.FileProvider;

public class DownloadService extends IntentService {

    private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
    private static final String TAG = "DownloadService";

    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private Notification.Builder mBuilder;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new Notification.Builder(this, "downLoadChannelId");
            NotificationChannel channel = new NotificationChannel("downLoadChannelId", "downLoadChannel", NotificationManager.IMPORTANCE_LOW);
            mNotifyManager.createNotificationChannel(channel);
        } else {
            mBuilder = new Notification.Builder(this);
        }

        String appName = getString(getApplicationInfo().labelRes);
        int icon = getApplicationInfo().icon;

        mBuilder.setContentTitle(appName).setSmallIcon(icon);
        String urlStr = intent.getStringExtra(Constants.APK_DOWNLOAD_URL);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();
            long bytetotal = urlConnection.getContentLength();
            long bytesum = 0;
            int byteread = 0;
            in = urlConnection.getInputStream();
            File dir = StorageUtils.getCacheDirectory(this);
            String apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
            File apkFile = new File(dir, apkName);
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;

            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                int progress = (int) (bytesum * 100L / bytetotal);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress != oldProgress) {
                    updateProgress(progress);
                }
                oldProgress = progress;
            }
            // 下载完成
            installAPk(apkFile);
            mNotifyManager.cancel(NOTIFICATION_ID);

        } catch (Exception e) {
            Log.e(TAG, "download apk file error");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    private void updateProgress(int progress) {
        // 将进度回传给RN
        UpgradeModule.sendProgress(progress);
        //"正在下载:" + progress + "%"
        mBuilder.setContentText(this.getString(R.string.android_auto_update_download_progress, progress)).setProgress(100, progress, false);
        //setContentInent如果不设置在4.0+上没有问题，在4.0以下会报异常
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingintent);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void installAPk(File apkFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = getPackageName() + ".updateFileProvider";
            Uri apkUri = FileProvider.getUriForFile(this, authority, apkFile);
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
        startActivity(intent);
    }
}
