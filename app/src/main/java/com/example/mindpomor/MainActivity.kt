package com.example.mindpomor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var timeSelected: Int = 25 * 60 * 1000
    private var timeCountDown: CountDownTimer? = null
    private var timeProgress = 0
    private var pauseOffSet: Long = 0
    private var isPlaying = false
    private lateinit var progressBar: ProgressBar
    private lateinit var timerTextView: TextView
    private lateinit var startBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        progressBar = findViewById(R.id.progressBar)
        timerTextView = findViewById(R.id.timerTextView)
        startBtn = findViewById(R.id.playButton)


        startBtn.setOnClickListener {
            if (isPlaying) {
                pauseTimer()
            } else {
                startTimerSetup()
            }
            togglePlayPauseButton()
        }

        val resetBtn: ImageButton = findViewById(R.id.restartButton)
        resetBtn.setOnClickListener {
            resetTime()
            togglePlayPauseButton()
        }


        resetTime()
    }

    private fun startTimerSetup() {
        timeCountDown?.cancel()
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
                isPlaying = false
                togglePlayPauseButton()
                showCompletionDialog()
            }
        }.start()
        isPlaying = true
    }

    private fun pauseTimer() {
        timeCountDown?.cancel()
        isPlaying = false
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

    private fun togglePlayPauseButton() {
        if (isPlaying) {
            startBtn.setImageResource(R.drawable.pause_removebg)
        } else {
            startBtn.setImageResource(R.drawable.play_removebg)
        }
    }


    private fun showCompletionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tempo Encerrado!")
        builder.setMessage("Seu tempo de estudos terminou. Deseja iniciar os 5 minutos de descanso?")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            startRestTimer()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun startRestTimer() {
        timeCountDown?.cancel()
        timeCountDown = object : CountDownTimer(5 * 60 * 1000, 1000) { // 5 minutos
            override fun onTick(millisUntilFinished: Long) {
                updateTimerTextView(millisUntilFinished)
            }

            override fun onFinish() {
            }
        }.start()
    }
}
