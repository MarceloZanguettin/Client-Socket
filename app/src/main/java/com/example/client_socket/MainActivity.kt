package com.example.client_socket

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.net.Socket
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var rbData: RadioButton
    private lateinit var rbHora: RadioButton
    private lateinit var btEnviar: Button
    private lateinit var tvResultado: TextView
    private lateinit var clientSocket: Socket


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        rbData = findViewById(R.id.rbData)
        rbHora = findViewById(R.id.rbHora)
        btEnviar = findViewById(R.id.btEnviar)
        tvResultado = findViewById(R.id.tvResultado)
    }

    fun btEnviarOnClick(view: View) {

        val ip = "10.0.2.2"
        val port = 12345

        try {
            clientSocket = Socket(ip, port) //linha é bloqueante ou dará exceção
            //Conectado com o server

            val outputStream = clientSocket.getOutputStream().bufferedWriter(Charset.forName("UTF-8"))
            val inputStream = clientSocket.getInputStream().bufferedReader(Charset.forName("UTF-8"))
            //Fluxo de IO criado

            var protocol = ""

            when ( rbHora.isChecked ) {
                true -> protocol = "hora"
                false -> protocol = "data"
            }

            outputStream.write(protocol + "\n")
            outputStream.flush()
            //Mensagem enviado ao servidor sem bloqueios

            val result = inputStream.readLine() //linha
            //Mensagem recebida do servidor

            tvResultado.text = result

        } catch (e: Exception) {
            tvResultado.text = "Erro: " + e.message
        } finally {
            clientSocket.close()
        }

        var protocol = ""

        when ( rbHora.isChecked ) {
            true -> protocol = "hora"
            false -> protocol = "data"
        }

        Toast.makeText(
            this,
            protocol,
            Toast.LENGTH_SHORT
        ).show()
    }
}