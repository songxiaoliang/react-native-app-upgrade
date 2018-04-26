/*
 * A smart AMap location Library for react-native apps
 * https://github.com/react-native-component/react-native-smart-amap-location/
 * Released under the MIT license
 * Copyright (c) 2016 react-native-component <moonsunfall@aliyun.com>
 */

import {
    NativeModules,
    Platform,
    DeviceEventEmitter
} from 'react-native'

const nativeUpgrade = NativeModules.upgrade;

/**
 * 升级
 * @param msg   android传入apk地址,ios传入appid
 * @param callback 只有ios有效，回传检测更新的结果
 */
export const upgrade = (msg, callback = f => f) => {
    if (Platform.OS === 'android') {
        nativeUpgrade.upgrade(msg)
    } else if (Platform.OS === 'ios') {
        nativeUpgrade.upgrade(msg, callback)
    }
};
/**
 * 根据appid打开苹果商店
 * @param appid
 */
export const openAPPStore = (appid) => {
    if (Platform.OS === 'ios') {
        nativeUpgrade.openAPPStore(appid)
    } else {
        console.warn('仅限ios调用')
    }
};

export const addDownListener = (callBack) => {
    if (Platform.OS === 'android') {
        return DeviceEventEmitter.addListener('LOAD_PROGRESS', callBack)
    } else {
        console.warn('仅限android调用')
    }
};
