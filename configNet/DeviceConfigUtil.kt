package cn.venii.n1.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Message
import cn.venii.n1.app.global.DeviceConfig
import cn.venii.n1.app.global.GlobalConfig
import cn.venii.n1.mqtt.IMqttConnectStatusListener
import cn.venii.n1.mqtt.MqttTriadData
import cn.venii.n1.mqtt.NativeMqttManager
import cn.venii.n1.mvp.contract.DeviceConfigContract
import cn.venii.n1.mvp.model.entity.DeviceReturnInfo
import cn.venii.n1.mvp.model.entity.WifiConfigEntity
import cn.venii.n1.thread.ConnectThread
import cn.venii.n1.thread.ListenerThread
import com.alibaba.fastjson.JSON
import com.kongqw.wifilibrary.WiFiManager
import com.orhanobut.hawk.Hawk
import timber.log.Timber
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.Socket
import java.util.concurrent.Executors

/**
 * Comment: WiFi配网的工具类
 *
 * @author Vangelis.Wang in UpCan
 * @date 2018/11/28
 * Email:Pei.wang@icanup.cn
 */
class DeviceConfigUtil(activity: Activity, private var mView: DeviceConfigContract.IDeviceConifgView) :
    DeviceConfigContract.IDeviceConifgPresenter {

    private var activityWeakReference: WeakReference<Activity> = WeakReference(activity)

    /**
     * 获取当前连接信息和公网的工作
     */
    private var mAutoManager: WifiAutoConnectManager? = null

    /**
     * 连接Ap热点
     */
    private lateinit var mWifiAnotherManager: WiFiManager

    /**
     * 消息处理
     */
    private lateinit var mSocketMessageHandlder: SocketMessageHandlder

    /**
     * 连接线程
     */
    private var mConntectThread: ConnectThread? = null

    /**
     * 监听线程
     */
    private var mListenerThread: ListenerThread? = null

    /**
     * 监听连接设备AP侧的状态
     */
    private lateinit var mConnectDeviceIntentFilter: IntentFilter

    /**
     * 检测连接公网的状态
     */
    private lateinit var mConenctNetIntentFilter: IntentFilter

    /**
     * 存储的WiFi配置信息
     */
    private var mConfigInfo: WifiConfigEntity? = null

    /**
     * 是否正在连接
     */
    private var isConnect: Boolean = false

    /**
     * 切换mode --> 让ap切换掉
     */
    private val changeModeCommand = "{\"wifimode\":\"client\"}"

    /**
     * 每个步长的间隔时间
     */
    private val stepTime = 3000

    /**
     * 是否已经注册了连接Ap的监听
     */
    private var isRegisterApReceiver = false

    /**
     * 记录重新连接次数
     */
    private var mReconnectTimes: Int = 1

    /**
     * 是否正在处于重试中
     */
    private var mIsReconnect = false

    /**
     * 是否正在绑定设备中
     */
    private var mIsBindingDevice = false

    /**
     * 初始化
     */
    override fun initWifiConfig() {
        mWifiAnotherManager = WiFiManager.getInstance(activityWeakReference.get())

        mSocketMessageHandlder = SocketMessageHandlder()

        mAutoManager = WifiAutoConnectManager
            .newInstance(
                activityWeakReference.get()?.applicationContext
                    ?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            )

        initListenerThread()

        mConnectDeviceIntentFilter = IntentFilter()
        mConenctNetIntentFilter = IntentFilter()
        mConenctNetIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        mConenctNetIntentFilter.priority = 1000
        mConnectDeviceIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)

        //获取存入的WiFi配置信息
        mConfigInfo = Hawk.get(DeviceConfig.CONFIG_WIFI_DATA, WifiConfigEntity())

        mView.updateConfigProgress(10, "初始化中...")

        //这里注册监听公网网络的广播
        activityWeakReference.get()?.registerReceiver(connectApReceiver, mConenctNetIntentFilter)
        isRegisterApReceiver = true
    }

    /**
     * 释放资源和反注册
     */
    override fun releaseConfigResource() {
        if (mListenerThread != null) {
            mListenerThread?.setClose(false)
        }

        if (isRegisterApReceiver) {
            isRegisterApReceiver = false
            activityWeakReference.get()?.unregisterReceiver(connectApReceiver)
        }
    }

    /**
     * 关闭线程
     */
    private fun closeThread() {
        mListenerThread?.interrupt()
        mListenerThread = null
        mConntectThread?.interrupt()
        mConntectThread = null
    }

    /**
     * 初始化监听线程
     */
    private fun initListenerThread() {
        mListenerThread = ListenerThread(DeviceConfig.LISTENER_PORT, mSocketMessageHandlder)
        mListenerThread?.setClose(true)
        mListenerThread?.start()
    }

    /**
     * Handler 和连接和接收线程的通信
     */
    @SuppressLint("HandlerLeak")
    inner class SocketMessageHandlder internal constructor() : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                GlobalConfig.DEVICE_CONNECTING -> {
                    mListenerThread?.socket?.let {
                        mConntectThread = ConnectThread(it, mSocketMessageHandlder)
                        mConntectThread?.start()
                    }
                }
                GlobalConfig.DEVICE_CONNECTED -> {
                    mView.updateConfigProgress(40, "设备已经连接上")
                    //重试连接次数归零
                    mReconnectTimes = 0
                    Handler().postDelayed({
                        if (mConntectThread != null) {
                            val json = JSON.toJSONString(
                                WifiConfigEntity(
                                    mConfigInfo?.ssid
                                    , mConfigInfo?.password
                                )
                            )
                            mConntectThread?.sendData(json)
                            mView.updateConfigProgress(50, "正在发送数据到设备...")
                        }
                    }, stepTime.toLong())
                }
                GlobalConfig.SEND_MSG_SUCCSEE -> if (changeModeCommand == msg.data.getString(DeviceConfig.SOCKET_DATA_MSG)) {
                    mView.updateConfigProgress(80, "监听公网状态中...")
                    Timber.tag("Bug").d("监听公网状态中")
                    //连接公网
                    mIsBindingDevice = true
                    val wifiConfig = Hawk.get(DeviceConfig.CONFIG_WIFI_DATA, WifiConfigEntity())

                    mAutoManager?.connectPasswordWifi(
                        wifiConfig.ssid, wifiConfig.password
                        , mAutoManager?.getCipherType(wifiConfig.ssid)
                    )

                    //删除原有的设备WiFi配置
                    mAutoManager?.removeWifiBySsid("\"" + wifiConfig.deviceSsid + "\"")

                } else {
                    mView.updateConfigProgress(60, "发送信息到设备成功")
                }
                GlobalConfig.SEND_MSG_ERROR -> {
                }
                GlobalConfig.GET_MSG -> {
                    val result = msg.data.getString(DeviceConfig.SOCKET_DATA_MSG)

                    //将收到的机器发回的数据进行存储
                    val mReturnInfo = JSON.parseObject(result, DeviceReturnInfo::class.java)
                    Hawk.put(DeviceConfig.DEVICE_RETURN_INFO, mReturnInfo)
                    Handler().postDelayed({
                        //收到消息后发送关闭wifi的指令给下位机
                        if (mConntectThread != null) {
                            mConntectThread?.sendData(changeModeCommand)
                            mListenerThread?.setClose(true)
                        }
                        mView.updateConfigProgress(70, "开始进行设备模式切换...")
                    }, stepTime.toLong())
                }
            }
        }
    }

    /**
     * 启动开始连接线程
     */
    private fun startConnectThread() {
        val singleThreadExecutor = Executors.newSingleThreadExecutor()
        singleThreadExecutor.execute {
            try {
                //todo !!!!下面这一句可能会报错!!!! 现在增加重连逻辑
                //下面这一句可能会报错
                val socket = Socket(DeviceConfig.CONNECT_IP, DeviceConfig.LISTENER_PORT)
                mConntectThread = ConnectThread(socket, mSocketMessageHandlder)
                mConntectThread?.start()
            } catch (e: IOException) {
                mIsReconnect = true
                //是否在连接中
                isConnect = false
                activityWeakReference.get()?.runOnUiThread { }
                //开始重连
                startConnectDeviceAP()
                e.printStackTrace()
            }
        }
    }

    /**
     * 开始连接设备的ap热点
     */
    override fun startConnectDeviceAP() {
        activityWeakReference.get()?.runOnUiThread {
            if (!mIsReconnect) {
                mView.updateConfigProgress(20, "开始连接设备...")
            } else {
                mView.updateConfigProgress(20, "连接异常，正在重新尝试连接设备....第$mReconnectTimes" + "次")
                mReconnectTimes++
            }
        }

        val singleThreadExecutor = Executors.newSingleThreadExecutor()
        singleThreadExecutor.execute {
            mConfigInfo?.let {
                mWifiAnotherManager.connectOpenNetwork(it.deviceSsid)
            }
        }
    }

    /**
     * 检测当前WiFi是否连接上了Ap热点
     * 连接上后就开始连接socket
     */
    private val connectApReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            if (ConnectivityManager.CONNECTIVITY_ACTION == action) {
                val connectivityManager = activityWeakReference.get()
                    ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                if (networkInfo != null) {
                    if (networkInfo.extraInfo.isNotEmpty()) {
                        if (networkInfo.extraInfo.contains(GlobalConfig.PREFIX_APP_TYPE)) {
                            Handler().postDelayed({
                                if (!isConnect) {
                                    isConnect = true
                                    startConnectThread()
                                }
                            }, stepTime.toLong())
                        } else if (networkInfo.isAvailable && networkInfo.isConnected) {
                            if (mIsBindingDevice) {

                                Handler().postDelayed({ loginMqttServer() }, 1000)
                            }
                        }
                    }
                }
            }
        }
    }


    private fun loginMqttServer() {
        //有网络后就再次登录
        val mqttTriadData = Hawk.get<MqttTriadData>(GlobalConfig.MQTT_TRIAD_DATA)
        mView.updateConfigProgress(95, "开始登入Mqtt服务器")

        NativeMqttManager.with()
            .init(mqttTriadData)
            .setConnectListener(object : IMqttConnectStatusListener {
                override fun connectMqttServerSuccess() {

                    activityWeakReference.get()?.runOnUiThread {
                        mView.updateConfigProgress(100, "设备配网完成")
                    }

                    if (mIsBindingDevice) {
                        closeThread()
                        mView.loginMqttServerSuccess()
                        mIsBindingDevice = false
                    }
                }

                override fun connectMqttServerFaild() {}
            })
            .connectMqtt()
    }
}