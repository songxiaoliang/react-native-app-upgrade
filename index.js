import {
    Linking,
    Platform,
    NativeModules,
    DeviceEventEmitter,
} from 'react-native';

const RNUpgrade = NativeModules.upgrade;
const ANDROID_PLATFORM = Platform.OS === 'android';

/**
 * IOS检测更新
 * @param appId   appstore的应用id
 * @param version  本地版本
 */
export async function checkUpdate(appId, version) {
    if (ANDROID_PLATFORM) {
        try {
            const response = await fetch(
                `https://itunes.apple.com/cn/lookup?id=${appId}`
            );
            const res = await response.json();
            if (res.results.length < 1) {
                return {
                    code: -1,
                    msg: '此APPID为未上架的APP或者查询不到'
                };
            }
            const msg = res.results[0];
            if (handlerVersionString(version) < handlerVersionString(msg.version)) {
                return {
                    code: 1,
                    msg: msg.releaseNotes,
                    version: msg.version
                };
            } else {
                return {
                    code: 0,
                    msg: '没有新版'
                };
            }
        } catch (e) {
            return {
                code: -1,
                msg: '你可能没有连接网络哦'
            };
        }
    }
}

/**
 * 升级 android平台
 * @param apkUrl   android传入apk地址
 */
export const upgrade = (apkUrl) => {
    if (ANDROID_PLATFORM) {
        RNUpgrade.upgrade(apkUrl)
    }
};
/**
 * 根据appid打开苹果商店
 * @param appid
 */
export const openAPPStore = (appid) => {
    if (!ANDROID_PLATFORM) {
        Linking.openURL(`https://itunes.apple.com/us/app/id${appid}?ls=1&mt=8`)
    }
};

/**
 * android apk下载回调
 * @param callBack
 */
export const addDownListener = (callBack) => {
    if (ANDROID_PLATFORM) {
        return DeviceEventEmitter.addListener('LOAD_PROGRESS', callBack)
    }
};