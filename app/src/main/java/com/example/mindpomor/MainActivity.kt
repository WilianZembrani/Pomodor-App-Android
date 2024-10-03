package com.example.mindpomor

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var timeSelected: Int = 25 * 60 * 1000 // Exemplo de 25 minutos, ajustável
    private var timeCountDown: CountDownTimer? = null
    private var timeProgress = 0
    private var pauseOffSet: Long = 0
    private var isStart = true
    private lateinit var progressBar: ProgressBar
    private lateinit var timerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialize os componentes
        progressBar = findViewById(R.id.progressBar)
        timerTextView = findViewById(R.id.timerTextView)

        // Botões para iniciar e resetar o timer
        val startBtn: ImageButton = findViewById(R.id.playButton)
        startBtn.setOnClickListener {
            startTimerSetup()
        }

        val resetBtn: ImageButton = findViewById(R.id.restartButton)
        resetBtn.setOnClickListener {
            resetTime()
        }

        // Defina o valor inicial do timer
        resetTime()
    }

    private fun startTimerSetup() {
        timeCountDown?.cancel() // Cancela qualquer timer existente
        timeCountDown = object : CountDownTimer(timeSelected.toLong() - pauseOffSet, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                pauseOffSet = timeSelected.toLong() - millisUntilFinished
                timeProgress = ((pauseOffSet.toDouble() / timeSelected.toDouble()) * 100).toInt()
                progressBar.progress = timeProgress
                updateTimerTextView(millisUntilFinished)
            }

            override fun onFinish() {
                pauseOffSet = 0
                timeProgress = 100
                progressBar.progress = timeProgress
                timerTextView.text = "Tempo Encerrado!"
            }
        }.start()
    }

    private fun resetTime() {
        timeCountDown?.cancel()
        pauseOffSet = 0
        timeProgress = 0
        progressBar.progress = 0
        timerTextView.text = formatTime(timeSelected.toLong())
    }

    private fun updateTimerTextView(millisUntilFinished: Long) {
        timerTextView.text = formatTime(millisUntilFinished)
    }

    private fun formatTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

