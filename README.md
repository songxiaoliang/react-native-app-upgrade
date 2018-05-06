<img src='http://oleeed73x.bkt.clouddn.com/1522417405_153693.png' />

### React Native App 版本升级封装库，兼容Android 4.4+、5.+、6.+、7.+版本

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

1. 将【 android_upgrade 】包拷贝到你的Android项目包名目录下（app/src/main/com.xxx）
2. 在 app / src / main / res /目录下创建xml资源文件夹，并创建名称为【 update_file_provider 】名称的xml文件，在文件中添加如下代码：
```java
    <paths>
        <!--升级-->
        <external-cache-path
            name="update_external_cache"
            path="." />

        <cache-path
            name="update_cache"
            path="." />
    </paths>
```
3. 在AndroidMainfest.xml文件下添加权限和服务配置
```Java
    <uses-permission android:name="android.permission.INTERNET" />
    <!--8.0安装需要的权限-->
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>   
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```
```Java
    <application
      android:name=".MainApplication"
      android:allowBackup="true"
      android:label="@string/app_name"
      android:icon="@mipmap/ic_launcher"
      android:theme="@style/AppTheme">
      
        // 添加fileProvider配置代码
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.updateFileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/update_file_provider" />
        </provider>
     
        // 添加Service代码
        <service
            android:name=".android_upgrade.DownloadService"
            android:exported="true" />
            
            // 省略其他代码... 
    </application>
```
4.打开android/app/build.gradle文件，修改 compileSdkVersion 26 buildToolsVersion "26.0.3"，版本指定为26以上即可。如下：
```Java
android {

    compileSdkVersion 26
    buildToolsVersion "26.0.3"

    defaultConfig {
      ...
        
    }
    splits {
        ...
    }
    buildTypes {
       ...
    }
    
}
```
5.在你的工程目录中，打开android/app/src/main/res/values/strings.xml文件，添加如下代码:
```Java
    <string name="android_auto_update_download_progress">正在下载:%1$d%%</string>
```
6.在MainApplication.java文件下的getPackages方法中添加如下代码：[首先要将UpgradePackage导入]
```Java
    import 项目工程包名.UpgradePackage;
    new UpgradePackage()
```
7.如果项目中使用到了react-native-image-crop-picker，则需要共用同一个FileProvider,修改如下：
找到/node_modules/react-native-image-crop-picker/android/src/main/java/com.reactnative/ivpusic/imagepicker/PickerModule.java
```java

（1）添加如下代码：
    private String fileProviderAuthorities = "provider";

（2）添加如下方法：
    @ReactMethod
    public void setFileProviderAuthorities(String fileProviderAuthorities) {
      this.fileProviderAuthorities = fileProviderAuthorities;
    }

(3) 修改303行代码，替换成如下方法：
    mCameraCaptureURI = FileProvider.getUriForFile(activity,
      activity.getApplicationContext().getPackageName() + "." + this.fileProviderAuthorities,
      imageFile);
  
(4)在使用前，先调用setFileProviderAuthorities方法即可。
    if(Device.Android) {
      ImagePicker.setFileProviderAuthorities('updateFileProvider');
    }
    ImagePicker.openPicker({...})

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

如果需要接收下载进度，可通过如下方式：

```Java
    componentWillMount(){
        DeviceEventEmitter.addListener('LOAD_PROGRESS',(msg)=>{
            let title = "当前下载进度：" + msg 
            ToastAndroid.show(title, ToastAndroid.SHORT);  
        }); 
    } 
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
