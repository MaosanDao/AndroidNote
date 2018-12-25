package cn.venii.n1.thread

import android.os.Message
import cn.venii.n1.app.global.GlobalConfig.DEVICE_CONNECTING
import cn.venii.n1.app.utils.DeviceConfigUtil
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

/**
 * Comment: 监听线程
 *
 * @author Vangelis.Wang in UpRot
 * @date 2018/7/17
 * Email:Pei.wang@icanup.cn
 */

class ListenerThread(
    port: Int, private val handler
    : DeviceConfigUtil.SocketMessageHandlder
) : Thread() {

    private lateinit var serverSocket: ServerSocket
    var socket: Socket? = null
        private set

    /**
     * 设置中断
     */
    @Volatile
    var mRun = true

    init {
        name = "ListenerThread"
        try {
            serverSocket = ServerSocket()
            serverSocket.reuseAddress = true
            serverSocket.bind(InetSocketAddress(port))
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun setClose(run: Boolean) {
        mRun = run
    }

    override fun run() {
        while (!mRun) {
            try {
                //阻塞，等待设备连接
                socket = serverSocket.accept()

                val message = Message.obtain()
                message.what = DEVICE_CONNECTING
                handler.sendMessage(message)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}
