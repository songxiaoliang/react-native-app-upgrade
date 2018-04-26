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

const upgrade = NativeModules.upgrade;

/**
 * android升级
 * @param url
 */
export const androidUpgrade = (url) => {
    if (Platform.OS === 'android') {
        upgrade.upgrade(url)
    } else {
        console.warn('仅限android调用')
    }
};
/**
 * 根据appid打开苹果商店
 * @param appid
 */
export const openAPPStore = (appid) => {
    if (Platform.OS === 'ios') {
        upgrade.openAPPStore(appid)
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

