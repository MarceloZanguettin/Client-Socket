package com.example.client_socket

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Reader
import java.net.Socket
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var rbData: RadioButton
    private lateinit var rbHora: RadioButton
    private lateinit var btEnviar: Button
    private lateinit var tvResultado: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var clientSocket: Socket
    private lateinit var inputStream: BufferedReader
    private lateinit var outputStream: BufferedWriter



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
        progressBar = findViewById(R.id.progressBar)
    }

    fun btEnviarOnClick(view: View) {

        val protocol = if (rbData.isChecked) "data" else "hora"

        ConexaoTask().execute(protocol)
    }

    override fun onStop() {
        super.onStop()
        clientSocket.close()
    }

    inner class ConexaoTask : AsyncTask<String, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
            btEnviar.isEnabled = false
        }

        override fun doInBackground(vararg protocol: String?): String? {

            try {

                Thread.sleep(2000)

                if ( ! ::clientSocket.isInitialized) {

                    val ip = BuildConfig.SERVER_IP
                    val port = BuildConfig.SERVER_PORT

                    clientSocket = Socket(ip, port) //linha é bloqueante ou dará exceção
                    //Conectado com o server

                    outputStream =
                        clientSocket.getOutputStream().bufferedWriter(Charset.forName("UTF-8"))
                    inputStream =
                        clientSocket.getInputStream().bufferedReader(Charset.forName("UTF-8"))
                    //Fluxo de IO criado
                }



                outputStream.write(protocol[0] + "\n")
                outputStream.flush()
                //Mensagem enviado ao servidor sem bloqueios

                val result = inputStream.readLine() //linha
                //Mensagem recebida do servidor

                return result


            }  catch (e: Exception) {
                return e.message
            }
        }

        override fun onPostExecute(result: String?) {
            tvResultado.text = result
            progressBar.visibility = View.GONE
            btEnviar.isEnabled = true
        }
    }



}//Fim da MainActivity