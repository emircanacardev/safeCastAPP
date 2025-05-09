package com.example.safecast

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.zeromq.SocketType
import org.zeromq.ZContext

class SubManager {
    private lateinit var context: ZContext
    private lateinit var subSocket: org.zeromq.ZMQ.Socket

    fun initSocket(subAddress: String, topics: List<String> )
    {
        context = ZContext()
        subSocket = context.createSocket(SocketType.SUB)
        subSocket.connect(subAddress)

        for (topic in topics) {
            subSocket.subscribe(topic.toByteArray())
        }
    }

    fun startReceivingMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    val message = subSocket.recvStr()
                    Log.d("ZMQ", "Received: $message")
                } catch (e: Exception) {
                    Log.e("ZMQ", "Error receiving message: ${e.message}")
                    delay(1000)
                }
            }
        }
    }

    fun close() {
        subSocket.close()
        context.close()
    }
}