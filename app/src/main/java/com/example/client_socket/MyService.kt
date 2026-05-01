package com.example.client_socket

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.Socket
import java.nio.charset.Charset
import java.util.Timer
import java.util.TimerTask

class MyService : Service() {

    private lateinit var inputStream: BufferedReader
    private lateinit var outputStream: BufferedWriter
    private lateinit var clientSocket: Socket
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val timer = Timer()
        timer.schedule(MinhaTimerTask("hora"), 0, 1000)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    suspend fun conexaoTask(protocol: String) = withContext(Dispatchers.IO) {

        var result = ""

        try {

            if (!::clientSocket.isInitialized) {

                val ip = BuildConfig.SERVER_IP
                val port = BuildConfig.SERVER_PORT

                clientSocket = Socket(ip, port) //linha é bloqueante ou dará exceção
                //Conectado com o server

                outputStream =
                    clientSocket.getOutputStream().bufferedWriter(Charset.forName("UTF-8"))
                inputStream = clientSocket.getInputStream().bufferedReader(Charset.forName("UTF-8"))
                //Fluxo de IO criado
            }



            outputStream.write(protocol + "\n")


            outputStream.flush()
            //Mensagem enviado ao servidor sem bloqueios

            result = inputStream.readLine() //linha
            //Mensagem recebida do servidor


        } catch (e: Exception) {
            result = e.message.toString()
        }

    }

    inner class MinhaTimerTask( msg: String ) : TimerTask() {

        override fun run() {
            serviceScope.launch {
                conexaoTask("hora")
            }
        }
    }
}