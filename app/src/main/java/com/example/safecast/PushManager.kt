package com.example.safecast

import android.util.Log
import org.zeromq.ZContext
import org.zeromq.SocketType
import org.json.JSONObject

class PushManager {

    private lateinit var context: ZContext
    private lateinit var pushSocket: org.zeromq.ZMQ.Socket

    fun initSocket()
    {
        context = ZContext()
        pushSocket = context.createSocket(SocketType.PUSH)
        pushSocket.connect("tcp://10.0.2.2:4712")
    }

    fun sendMessage(currentUserName: String, currentUserPhoneNumber: String, message: String, location: String) {
        val json = JSONObject().apply {
            put("name", currentUserName)
            put("phone", currentUserPhoneNumber)
            put("message", message)
            put("location", location)
            put("timestamp", System.currentTimeMillis())
        }
        Log.e("ZMQ", "Sending JSON: $json")
        pushSocket.send(json.toString())
    }

    fun close() {
        pushSocket.close()
        context.close()
    }

}