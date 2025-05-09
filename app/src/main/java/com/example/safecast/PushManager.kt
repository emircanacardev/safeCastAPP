package com.example.safecast

import org.zeromq.ZContext
import org.zeromq.SocketType


class PushManager {

    private lateinit var context: ZContext
    private lateinit var pushSocket: org.zeromq.ZMQ.Socket

    fun initSocket(pushAddress: String)
    {
        context = ZContext()
        pushSocket = context.createSocket(SocketType.PUSH)
        pushSocket.connect(pushAddress)
    }

    fun sendMessage(message: Any)
    {
        pushSocket.send(message.toString())
    }

    fun close() {
        pushSocket.close()
        context.close()
    }

}