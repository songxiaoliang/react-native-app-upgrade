package com.songlcy.rnupgrade.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.os.BuildCompat;

import com.songlcy.rnupgrade.Constants.Constants;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.os.Environment.MEDIA_MOUNTED;


public final class FileUtils {

    /**
     * 只读模式
     */
    public static final String MODE_READ_ONLY = "r";
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    private FileUtils() {

    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted and app has appropriate permission. Else -
     * Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}
     */
    public static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            Log.e(Constants.TAG, "Can't define system cache directory! The app should be re-installed.");
        }
        return appCacheDir;
    }


    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                Log.e(Constants.TAG, "Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                Log.e(Constants.TAG, "Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(Context context, final File file) {
      if (file == null) {
        return false;
      }
      if (file.exists()) {
        return true;
      }
      return isFileExists(context, file.getAbsolutePath());
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(Context context, final String filePath) {
      File file = getFileByPath(filePath);
      if (file == null) {
        return false;
      }
      if (file.exists()) {
        return true;
      }
      return isFileExistsApi29(context, filePath);
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    @Nullable
    public static File getFileByPath(final String filePath) {
      return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
      if (s == null) {
        return true;
      }
      for (int i = 0, len = s.length(); i < len; ++i) {
        if (!Character.isWhitespace(s.charAt(i))) {
          return false;
        }
      }
      return true;
    }

    /**
     * Android 10判断文件是否存在的方法
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    private static boolean isFileExistsApi29(Context context, String filePath) {
      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P || BuildCompat.isAtLeastQ()) {
        AssetFileDescriptor afd = null;
        try {
          Uri uri = Uri.parse(filePath);
          afd = openAssetFileDescriptor(context, uri);
          if (afd == null) {
            return false;
          } else {
            closeIOQuietly(afd);
          }
        } catch (FileNotFoundException e) {
          return false;
        } finally {
          closeIOQuietly(afd);
        }
        return true;
      }
      return false;
    }

    /**
     * 安静关闭 IO
     *
     * @param closeables closeables
     */
    public static void closeIOQuietly(final Closeable... closeables) {
      if (closeables == null) {
        return;
      }
      for (Closeable closeable : closeables) {
        if (closeable != null) {
          try {
            closeable.close();
          } catch (IOException ignored) {
          }
        }
      }
    }

    /**
     * 从uri资源符中读取文件描述
     *
     * @param uri 文本资源符
     * @return AssetFileDescriptor
     */
    public static AssetFileDescriptor openAssetFileDescriptor(Context context, Uri uri) throws FileNotFoundException {
      return context.getContentResolver().openAssetFileDescriptor(uri, MODE_READ_ONLY);
    }

    /**
     * File Uri
     *
     * @param file The file.
     * @return a content URI for a given file
     */
    @Nullable
    public static Uri getUriForFile(Context context, final File file) {
      if (file == null) {
        return null;
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        String authority = context.getPackageName() + ".updateFileProvider";
        return FileProvider.getUriForFile(context, authority, file);
      } else {
        return Uri.fromFile(file);
      }
    }
}
