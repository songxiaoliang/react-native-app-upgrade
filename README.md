
React Native App 版本升级封装库，兼容Android 4 - 10 版本

### 注意
```xml
由于 Android Q(10) 版本中，系统对于后台拉起Activity做了非常严格的限制，在下载过程中， 如果回到Home桌面， 当下载完成时可能无法拉起安装Apk的页面，为了适配此场景，需要当前App优先申请打开【悬浮窗权限】, 即 SYSTEM_ALERT_WINDOW
```
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

  // 低于0.6+版本
  react-native link rn-app-upgrade
```

iOS
打开Xcode, 将 ios_upgrade 导入到项目目录。


```javascript

  import { 
    upgrade,
    versionName,
    versionCode,
    openAPPStore,
    checkIOSUpdate,
    addDownLoadListener,
  } from 'rn-app-upgrade';
  
  //可通过RN.versionName获取apk版本号和远程版本号进行比较
  if(Android) {
    if(res.versionCode > versionCode) {
      upgrade(res.apkUrl);
    }
  } else {
    const IOSUpdateInfo = await checkUpdate(appid, 当前版本号);
    IOSUpdateInfo.code // -1: 未查询到该App 或 网络错误 1: 有最新版本 0: 没有新版本
    IOSUpdateInfo.msg
    IOSUpdateInfo.version
  }
```

如果需要接收下载进度，可通过如下方式：
```javascript
   addDownLoadListener((progress) => {});
```
