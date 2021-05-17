
React Native App 版本升级封装库，兼容Android4以上所有版本

### 一、功能
#### Android
```xml
（1）版本检测
（2）下载更新
（3）进度提示
（4）自动安装
```

#### iOS
```xml
（1）版本检测
（2）自动跳转App Store
```

### 二、使用

```xml
  yarn add rn-app-upgrade

  // or 
  npm install rn-app-upgrade
 
  // less than 0.6
  react-native link rn-app-upgrade
```

iOS
打开Xcode, 将 ios_upgrade 导入到项目目录。


```javascript

  import { 
    downloadApk,
    versionName,
    versionCode,
    openAPPStore,
    checkIOSUpdate,
    addDownLoadListener,
  } from 'rn-app-upgrade';
  
  //可通过RN.versionName获取apk版本号和远程版本号进行比较
  if(Android) {
    if(res.versionCode > versionCode) {
        downloadApk({
            interval: 666, // listen to upload progress event, emit every 666ms
            apkUrl: "https://xxxx.apk",
            downloadInstall: true,
            callback: {
                onProgress: (received, total， percent) => {},
                onFailure: (errorMessage, statusCode) => {},
                onComplete: () => {},
            },
        });
    }
  } else {
    const IOSUpdateInfo = await checkIOSUpdate(appid, 当前版本号);
    IOSUpdateInfo.code // -1: 未查询到该App 或 网络错误 1: 有最新版本 0: 没有新版本
    IOSUpdateInfo.msg
    IOSUpdateInfo.version
  }
```
