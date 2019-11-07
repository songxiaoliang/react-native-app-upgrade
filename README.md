<img src='http://oleeed73x.bkt.clouddn.com/1522417405_153693.png' />

### React Native App 版本升级封装库，兼容Android 4 - 9 版本

#### Android
```xml
（1）版本检测
（2）版本下载
（3）进度提示
（4）下载完成后安装
```

#### iOS
```xml
（1）版本检测
（2）跳转App Store
```

【 iOS 平台 】
打开Xcode, 将【 ios_upgrade 】导入到项目目录。

二、使用

首先导入NativeModules模块：

```Java
import {
  NativeModules
} from 'react-native';
```

【 Android 】

```Java
// 可通过NativeModules.upgrade.versionName获取apk版本号和远程版本号进行比较
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
