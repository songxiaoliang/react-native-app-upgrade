# react-native-app-upgrade
React Native App 版本升级封装库

### 功能模块
#### Android：
##### （1）版本检测
##### （2）版本下载
##### （3）进度提示
##### （4）下载完成后安装

#### iOS
##### （1）版本检测
##### （2）跳转App Store


#### 
一、配置

【 Android 平台 】

1. 将【 android_upgrade 】包拷贝到你的Android项目src目录下。
2. 在AndroidMainfest.xml文件下添加权限和服务配置
```Java
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>   
```
```Java
    <application
      android:name=".MainApplication"
      android:allowBackup="true"
      android:label="@string/app_name"
      android:icon="@mipmap/ic_launcher"
      android:theme="@style/AppTheme">
      
        // 省略其他代码...
        
        <service
            android:name=".upgrade.DownloadService"
            android:exported="true" />
            
    </application>
```
3.在你的工程目录中，打开android/app/src/main/res/values/strings.xml文件，添加如下代码:
```Java
    <string name="android_auto_update_download_progress">正在下载:%1$d%%</string>
```
4.在MainApplication.java文件下的getPackages方法中添加如下代码：
```Java
    new UpgradePackage()
```
【 iOS 平台 】

将【 ios_upgrade 】拷贝到项目目录即可。

二、使用

首先导入NativeModules模块：

```Java
import {
  NativeModules
} from 'react-native';
```

【 Android 】

```Java
Http.get(Api.api_checkupdate, null, false, (result)=>{  
    if(result.ok) {  
        // 下载最新Apk  
        NativeModules.upgrade.upgrade(this.state.apkUrl);  
    }  
});  
```

【 iOS 】

```Java
NativeModules.upgrade.upgrade('Apple ID',(msg) =>{  
    if('YES' == msg) {  
       //跳转到APP Stroe  
       NativeModules.upgrade.openAPPStore('Apple ID');  
    } else {  
        Toast.show('当前为最新版本');  
    }  
})  
```
