import {
    Platform,
    NativeModules,
} from 'react-native';
import RNFetchBlob from 'rn-fetch-blob';

const RNUpgrade = NativeModules.upgrade;
const ANDROID_PLATFORM = Platform.OS === 'android';

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

//把字节转换成正常文件大小
function getFilesize(size) {
    if (!size)  return "";
    var num = 1024.00; //byte
    if (size < num)
        return size + "B";
    if (size < (num ** 2))
        return (size / num).toFixed(1) + "KB"; //kb
    if (size < Math.pow(num, 3))
        return (size / (num ** 2)).toFixed(1) + "MB"; //M
    if (size < (num ** 4))
        return (size / (num ** 3)).toFixed(1) + "G"; //G
    return (size / (num ** 4)).toFixed(1) + "T"; //T
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
    interval = 250,
    downloadInstall = true
}) => {
	const apkFilePath = RNUpgrade.downloadApkFilePath;
	// const apkHasDownload = await checkApkFileExist(apkFilePath);
    // if (apkHasDownload) {
    //     RNUpgrade.installApk(downloadApkFilePath);
    //     return;
    // }
    const downloadTask = await RNFetchBlob
        .config({ path: apkFilePath })
        .fetch('GET', apkUrl)
        .progress({ interval }, (received, total) => {
            callback?.onProgress(getFilesize(received), getFilesize(total), parseInt((received / total * 100)));
        })
        .catch((errorMessage, statusCode) => {
          callback?.onFailure(errorMessage, statusCode);
        });
    callback?.onComplete();
    if (downloadInstall) {
        const apkFileExist = await checkApkFileExist(apkFilePath);
        apkFileExist && RNUpgrade.installApk(apkFilePath);
    }
}

/**
 * 检查本地是否有apk文件
 * @param apkFilePath 下载的apk文件路径
 */
const checkApkFileExist = async (apkFilePath) => {
    return await RNFetchBlob.fs.exists(apkFilePath);
}

