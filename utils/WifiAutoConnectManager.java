package com.upcan.n1.utils;

import android.annotation.SuppressLint;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.upcan.n1.global.GlobalConfig;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Comment: WiFi相关管理类
 *
 * @author Vangelis.Wang in UpRot
 * @date 2018/7/17
 * Email:Pei.wang@icanup.cn
 */
public class WifiAutoConnectManager {

    private static final String TAG = WifiAutoConnectManager.class
            .getSimpleName();

    public static WifiManager mWifiManager = null;
    private static WifiAutoConnectManager mWifiAutoConnectManager;


    public interface ScanResultListener {
        /**
         * 扫描结果回调
         *
         * @param results WiFi Ap
         */
        void scanResult(List<ScanResult> results);

        /**
         * 没有搜索到任何结果
         */
        void noResult();
    }

    /**
     * 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
     */
    public enum WifiCipherType {
        /**
         * WEP加密方式
         */
        WIFICIPHER_WEP,
        /**
         * WPA加密方式
         */
        WIFICIPHER_WPA,
        /**
         * 没有密码
         */
        WIFICIPHER_NOPASS,
        /**
         * 无效
         */
        WIFICIPHER_INVALID
    }

    /**
     * 加密方式wpa
     */
    private static String wpa = "wpa";

    /**
     * 加密方式wep
     */
    private static String wep = "wep";

    private WifiAutoConnectManager(WifiManager wifiManager) {
        mWifiManager = wifiManager;
    }

    public static WifiAutoConnectManager newInstance(WifiManager wifiManager) {
        if (mWifiAutoConnectManager == null) {
            mWifiAutoConnectManager = new WifiAutoConnectManager(wifiManager);
        }
        return mWifiAutoConnectManager;
    }


    public void connectWifi(WifiConfiguration config) {
        int wcgID = mWifiManager.addNetwork(config);
        mWifiManager.enableNetwork(wcgID, true);
    }

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

    /**
     * 打开wifi功能
     */
    public boolean openWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    /**
     * 关闭WIFI
     */
    private void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    private boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        return !(len != 10 && len != 26 && len != 58) && isHex(wepKey);
    }

    private boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 根据给定的ssid信号量和总级别，判断当前信号量，在什么级别
     */
    public int getSignalNumsLevel(int rssi, int numLevels) {
        if (mWifiManager == null) {
            return -1;
        }
        return WifiManager.calculateSignalLevel(rssi, numLevels);
    }

    /**
     * 获取ssid的加密方式
     */
    public WifiCipherType getCipherType(String ssid) {
        if (mWifiManager == null) {
            return null;
        }
        List<ScanResult> list = mWifiManager.getScanResults();

        for (ScanResult scResult : list) {

            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities;
                if (!TextUtils.isEmpty(capabilities)) {

                    if (capabilities.contains("WPA")
                            || capabilities.contains("wpa")) {
                        Log.e("wifidemo", "wpa");
                        return WifiCipherType.WIFICIPHER_WPA;
                    } else if (capabilities.contains("WEP")
                            || capabilities.contains("wep")) {
                        Log.e("wifidemo", "wep");
                        return WifiCipherType.WIFICIPHER_WEP;
                    } else {
                        Log.e("wifidemo", "no");
                        return WifiCipherType.WIFICIPHER_NOPASS;
                    }
                }
            }
        }
        return WifiCipherType.WIFICIPHER_INVALID;
    }

    /**
     * 获取 bssid 接入点的地址
     */
    public String getBSSID() {
        if (mWifiManager == null) {
            return null;
        }
        WifiInfo info = mWifiManager.getConnectionInfo();
        Log.e("wifidemo", "getBSSID" + info.getBSSID());
        return info.getBSSID();
    }

    /**
     * 获取网关地址
     */
    public String getGateway() {
        if (mWifiManager == null) {
            return "";
        }
        InetAddress inetAddress = NetworkUtils.intToInetAddress(mWifiManager.getDhcpInfo().gateway);
        if (inetAddress == null) {
            return "";
        }
        return inetAddress.getHostAddress();
    }

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
                LogTrack.d("removeWifiBySsid success, SSID = " + wifiConfig.SSID + " netId = " + String.valueOf(wifiConfig.networkId));
                mWifiManager.removeNetwork(wifiConfig.networkId);
                mWifiManager.saveConfiguration();
            }
        }
    }

    /**
     * 获取ip地址*
     */
    public String getIpAddress() {
        if (mWifiManager == null) {
            return "";
        }
        InetAddress inetAddress = NetworkUtils.intToInetAddress(mWifiManager.getConnectionInfo().getIpAddress());
        if (inetAddress == null) {
            return "";
        }
        return inetAddress.getHostAddress();
    }

    /**
     * 获取mac地址
     */
    @SuppressLint("HardwareIds")
    public String getMacAddress() {
        if (mWifiManager == null) {
            return "";
        }
        return mWifiManager.getConnectionInfo().getMacAddress();
    }

    /**
     * 获取wifi名称
     */
    public String getSSID() {
        if (mWifiManager == null) {
            return null;
        }
        WifiInfo info = mWifiManager.getConnectionInfo();
        String ssid = info.getSSID();
        if (ssid != null) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    /**
     * 扫描WIFI AP
     */
    public boolean startStan() {
        return mWifiManager != null && mWifiManager.startScan();
    }

    /**
     * 获取所有WIFI AP
     */
    public void getScanResults(ScanResultListener listener) {
        List<ScanResult> srList = mWifiManager.getScanResults();
        List<ScanResult> formatList = new ArrayList<>();

        for (int i = 0; i < srList.size(); i++) {
            if (srList.get(i).SSID.contains("venii_n1_")
                    || srList.get(i).SSID.contains(GlobalConfig.PREFIX_APP_TYPE)) {
                formatList.add(srList.get(i));
            }
        }
        if (formatList.size() == 0) {
            listener.noResult();
        } else {
            listener.scanResult(formatList);
        }
    }


    /**
     * 判断WiFi的加密方式
     */
    public WifiAutoConnectManager.WifiCipherType judgWifiType(ScanResult result) {
        String capabilities = result.capabilities;

        WifiAutoConnectManager.WifiCipherType type =
                WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA;
        if (!TextUtils.isEmpty(capabilities.toLowerCase())) {
            if (capabilities.contains(wpa)) {
                type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA;
            } else if (capabilities.contains(wep)) {
                type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WEP;
            } else {
                type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS;
            }
        }

        return type;
    }

    /**
     * 获取服务器的Ip地址
     */
    public String getServerIP() {
        DhcpInfo dhcpinfo = mWifiManager.getDhcpInfo();
        return intToIp(dhcpinfo.serverAddress);
    }

    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    /**
     * 判断是否有密码且进行连接
     */
    public void analysisWifiType(final String ssid) {
        WifiConfiguration config = isExsits(ssid);
        if (config == null) {
            //没有密码
            config = createWifiInfo(ssid, ""
                    , WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS);
            LogTrack.v("【没有密码】开始连接...");
            connectWifi(config);
        } else {
            LogTrack.v("【已经配置了】开始连接...");
            connectWifi(config);
        }
    }

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

}
