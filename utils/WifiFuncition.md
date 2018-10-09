# 常用的一些WiFi相关的方法

## 连接WiFi
```java
/**
 * 连接wifi
 * <p>
 * 备注：Android 6.0 关于WiFi的改变（https://blog.csdn.net/xx326664162/article/details/51483915）
 * 解决：最近发现在安卓6.0的机子上，无法切换到指定WiFi
 * 原因：原因就是第一条，在扫描WiFi列表时，系统会自动创建曾经连接成功过的WiFi的WifiConfiguration
 * ，使用addNetwork()添加这个WiFi时，就是改变了系统创建的WifiConfiguration对象，所以addNetwork()会返回-1。
 *
 * @param ssid     名字
 * @param password 密码
 * @param type     类型
 */
public void connectPasswordWifi(String ssid, String password, WifiCipherType type) {
    // 连接到外网
    WifiConfiguration mWifiConfiguration;
    //检测指定SSID的WifiConfiguration 是否存在
    WifiConfiguration tempConfig = isExsits(ssid);
    if (tempConfig == null) {
        //创建一个新的WifiConfiguration ，CreateWifiInfo()需要自己实现
        mWifiConfiguration = createWifiInfo(ssid, password, type);
        int wcgID = mWifiManager.addNetwork(mWifiConfiguration);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
    } else {
        //发现指定WiFi，并且这个WiFi以前连接成功过
        mWifiConfiguration = tempConfig;
        boolean b = mWifiManager.enableNetwork(mWifiConfiguration.networkId, true);
    }
}
```
## 遗忘WiFi配置项(删除已有的连接过的WiFi)
```java
/**
 * 移除一个wifi的信息
 *
 * @param targetSsid Wifi SSID
 */
public void removeWifiBySsid(String targetSsid) {
    LogTrack.d("try to removeWifiBySsid, targetSsid=" + targetSsid);
    List<WifiConfiguration> wifiConfigs = mWifiManager.getConfiguredNetworks();

    for (WifiConfiguration wifiConfig : wifiConfigs) {
        String ssid = wifiConfig.SSID;
        if (ssid.equals(targetSsid)) {
            LogTrack.d("removeWifiBySsid success, SSID = " + wifiConfig.SSID + " netId = " +    String.valueOf(wifiConfig.networkId));
            mWifiManager.removeNetwork(wifiConfig.networkId);
            mWifiManager.saveConfiguration();
        }
    }
}
```
注意：调用方法需要为SSID加引号：
```java
//删除原有的设备WiFi配置
mAutoManager?.removeWifiBySsid("\""+wifiConfig.deviceSsid+"\"")
```
## 创建wifi配置文件
```java
/**
 * 创建wifi配置文件
 *
 * @return WifiConfiguration
 */
public WifiConfiguration createWifiInfo(String ssid, String password, WifiCipherType type) {
    WifiConfiguration config = new WifiConfiguration();
    config.allowedAuthAlgorithms.clear();
    config.allowedGroupCiphers.clear();
    config.allowedKeyManagement.clear();
    config.allowedPairwiseCiphers.clear();
    config.allowedProtocols.clear();
    config.SSID = "\"" + ssid + "\"";
    // config.SSID = SSID;
    // nopass
    if (type == WifiCipherType.WIFICIPHER_NOPASS) {
        // config.wepKeys[0] = "";
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        // config.wepTxKeyIndex = 0;

        // wep
    } else if (type == WifiCipherType.WIFICIPHER_WEP) {
        if (!TextUtils.isEmpty(password)) {
            if (isHexWepKey(password)) {
                config.wepKeys[0] = password;
            } else {
                config.wepKeys[0] = "\"" + password + "\"";
            }
        }
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        config.wepTxKeyIndex = 0;

        // wpa
    } else if (type == WifiCipherType.WIFICIPHER_WPA) {
        config.preSharedKey = "\"" + password + "\"";
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.status = WifiConfiguration.Status.ENABLED;
    }
    return config;
}
```
## 查看以前是否也配置过这个网络
```java
/**
 * 查看以前是否也配置过这个网络
 */
public WifiConfiguration isExsits(String ssid) {
    List<WifiConfiguration> existingConfigs = mWifiManager
            .getConfiguredNetworks();
    for (WifiConfiguration existingConfig : existingConfigs) {
        if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
            return existingConfig;
        }
    }
    return null;
}
```

## 附：完成的工具类
