package com.example.safecast

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.zeromq.SocketType
import org.zeromq.ZContext

class SubManager (private val onMessageReceived: (String) -> Unit) {

    private lateinit var context: ZContext
    private lateinit var subSocket: org.zeromq.ZMQ.Socket

    fun initSocket(relativePhoneNumbers: List<String>)
    {
        context = ZContext()
        subSocket = context.createSocket(SocketType.SUB)
        subSocket.connect("tcp://10.0.2.2:5556")


        for (topic in relativePhoneNumbers) {
            subSocket.subscribe(topic.toString().toByteArray())
        }
    }

    fun startReceivingMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    val message = subSocket.recvStr()
                    Log.d("ZMQ", "Received: $message")
                    onMessageReceived(message) // Mesaj alındığında callback'i çağırıyoruz

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