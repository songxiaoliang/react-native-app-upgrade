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

1. 将upgrade包拷贝到你的Android项目src目录下。
2. 在AndroidMainfest.xml文件下添加权限和服务配置
<uses-permission android:name="android.permission.INTERNET" />  
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>   

【 iOS 平台 】

将upgrade拷贝到项目目录即可。

二、使用

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
