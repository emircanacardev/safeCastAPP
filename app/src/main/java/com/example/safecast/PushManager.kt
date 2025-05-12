package com.example.safecast

import com.google.firebase.auth.FirebaseAuth
import org.zeromq.ZContext
import org.zeromq.SocketType


class PushManager {

    private lateinit var context: ZContext
    private lateinit var pushSocket: org.zeromq.ZMQ.Socket
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun initSocket()
    {
        context = ZContext()
        pushSocket = context.createSocket(SocketType.PUSH)
        pushSocket.connect("tcp://10.0.2.2:4712")
    }

    fun sendMessage(message: Any)
    {
        val currentUserPhoneNumber = auth.currentUser?.phoneNumber
        pushSocket.send( currentUserPhoneNumber.toString() + message.toString())
    }

    fun close() {
        pushSocket.close()
        context.close()
    }

}