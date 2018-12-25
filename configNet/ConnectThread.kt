package cn.venii.n1.thread

import android.os.Bundle
import android.os.Message
import cn.venii.n1.app.global.DeviceConfig
import cn.venii.n1.app.global.GlobalConfig
import cn.venii.n1.app.global.GlobalConfig.GET_MSG
import cn.venii.n1.app.global.GlobalConfig.SEND_MSG_ERROR
import cn.venii.n1.app.global.GlobalConfig.SEND_MSG_SUCCSEE
import cn.venii.n1.app.utils.DeviceConfigUtil
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.concurrent.Executors

/**
 * Comment: 连接线程
 *
 * @author Vangelis.Wang in UpCan
 * @date 2018/9/13
 * Email:Pei.wang@icanup.cn
 */
class ConnectThread(var socket: Socket, var handler: DeviceConfigUtil.SocketMessageHandlder) : Thread() {

    lateinit var inputStream: InputStream
    var outputStream: OutputStream? = null
    var bytes: Int = 0

    override fun run() {
        handler.sendEmptyMessage(GlobalConfig.DEVICE_CONNECTED)
        try {
            inputStream = socket.getInputStream()
            outputStream = socket.getOutputStream()

            val buffer = ByteArray(1024)
            while (true) {
                bytes = inputStream.read(buffer)
                if (bytes > 0) {
                    val data = ByteArray(bytes)
                    System.arraycopy(buffer, 0, data, 0, bytes)

                    val message = Message.obtain()
                    message.what = GET_MSG
                    val bundle = Bundle()
                    bundle.putString(DeviceConfig.SOCKET_DATA_MSG, String(data))
                    message.data = bundle
                    handler.sendMessage(message)
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 发送数据
     */
    fun sendData(msg: String) {
        val singleThreadExecutor = Executors.newSingleThreadExecutor()
        singleThreadExecutor.execute {
            try {
                outputStream?.write(msg.toByteArray())
                val message = Message.obtain()
                message.what = SEND_MSG_SUCCSEE
                val bundle = Bundle()
                bundle.putString(DeviceConfig.SOCKET_DATA_MSG, msg)
                message.data = bundle
                handler.sendMessage(message)
            } catch (e: IOException) {
                e.printStackTrace()
                val message = Message.obtain()
                message.what = SEND_MSG_ERROR
                val bundle = Bundle()
                bundle.putString(DeviceConfig.SOCKET_DATA_MSG, msg)
                message.data = bundle
                handler.sendMessage(message)
            }
        }
    }
}