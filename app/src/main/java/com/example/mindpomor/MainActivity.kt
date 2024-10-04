package com.example.mindpomor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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
    private var isPlaying = false // Alterado para representar o estado do timer
    private lateinit var progressBar: ProgressBar
    private lateinit var timerTextView: TextView
    private lateinit var startBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Criação do canal de notificação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "TIMER_CHANNEL",
                "Timer Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificações do temporizador"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialize os componentes
        progressBar = findViewById(R.id.progressBar)
        timerTextView = findViewById(R.id.timerTextView)
        startBtn = findViewById(R.id.playButton) // Mova a inicialização do startBtn aqui

        // Configura o clique para iniciar ou pausar o timer
        startBtn.setOnClickListener {
            if (isPlaying) {
                pauseTimer() // Se estiver tocando, pausa
            } else {
                startTimerSetup() // Se não estiver tocando, inicia
            }
            togglePlayPauseButton() // Alterna entre Play e Pause
        }

        // Botão para reiniciar o timer
        val resetBtn: ImageButton = findViewById(R.id.restartButton)
        resetBtn.setOnClickListener {
            resetTime()
            togglePlayPauseButton() // Retorna para Play ao reiniciar
        }

        // Defina o valor inicial do timer
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
                showNotification() // Exibe a notificação
                startRestTimer() // Inicia o temporizador de descanso
            }
        }.start()
        isPlaying = true
    }

    private fun pauseTimer() {
        timeCountDown?.cancel() // Cancela o timer
        isPlaying = false // Atualiza o estado para não estar mais tocando
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
            startBtn.setImageResource(R.drawable.pause_removebg) // Muda para o ícone de Pause
        } else {
            startBtn.setImageResource(R.drawable.play_removebg) // Muda para o ícone de Play
        }
    }

    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(this, "TIMER_CHANNEL")
            .setSmallIcon(R.drawable.notificacao) // Use um ícone de notificação apropriado
            .setContentTitle("Tempo Encerrado!")
            .setContentText("Seu tempo de trabalho acabou. Hora de descansar por 5 minutos!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun startRestTimer() {
        timeCountDown?.cancel() // Cancela qualquer timer existente
        timeCountDown = object : CountDownTimer(5 * 60 * 1000, 1000) { // 5 minutos
            override fun onTick(millisUntilFinished: Long) {
                updateTimerTextView(millisUntilFinished) // Atualiza o TextView
            }

            override fun onFinish() {
                timerTextView.text = "Descanso Terminado!" // Atualiza o TextView quando acabar
            }
        }.start()
    }
}
