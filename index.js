import {
    Platform,
    NativeModules,
} from 'react-native';
import RNFetchBlob from 'rn-fetch-blob';

const RNUpgrade = NativeModules.upgrade;
const ANDROID_PLATFORM = Platform.OS === 'android';
const downloadApkFilePath = RNUpgrade.downloadApkFilePath;

function handlerVersionString(version) {
    let versions = version.split('.');
    let number = 0;
    if (versions.length === 3) {
      number = parseInt(versions[0]) * 10000 + parseInt(versions[1]) * 100 + parseInt(versions[2])
    } else {
      number = parseInt(versions[0]) * 10000 + parseInt(versions[1]) * 100
    }
    return number;
}

/**
 * IOS检测更新
 * @param appId   appstore的应用id
 * @param version  本地版本
 */
export async function checkUpdate(appId, version) {
    if (!ANDROID_PLATFORM) {
        try {
            const response = await fetch(
                `https://itunes.apple.com/cn/lookup?id=${appId}&t=${Date.now()}`
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
 * 根据appid打开苹果商店
 * @param appid
 */
export const openAPPStore = (appid) => {
    if (!ANDROID_PLATFORM) {
        RNUpgrade.openAPPStore(appid);
    }
};

/**
 * Android apk下载
 * @param downloadInstall 是否下载后立刻安装
 * @param apkUrl Apk Url
 * @param callback 下载结果回调
 */
export const downloadApk = async ({
    apkUrl,
    callback,
    listenCount = 10,
    downloadInstall = true
}) => {
    // const apkHasDownload = await checkApkFileExist();
    // if (apkHasDownload) {
    //     RNUpgrade.installApk(downloadApkFilePath);
    //     return;
    // }
    const downloadTask = await RNFetchBlob
        .config({ path: downloadApkFilePath })
        .fetch('GET', apkUrl)
        .progress({ count: listenCount }, (received, total) => {
            callback?.onProgress(received, total);
        })
        .catch((errorMessage, statusCode) => {
          callback?.onFailure(errorMessage, statusCode);
        });
    if (downloadInstall) {
        const apkFileExist = await checkApkFileExist();
        apkFileExist && RNUpgrade.installApk(downloadApkFilePath);
    }
}

/**
 * 检查apk文件是否已下载
 */
const checkApkFileExist = async () => {
    return await RNFetchBlob.fs.exists(downloadApkFilePath);
}

