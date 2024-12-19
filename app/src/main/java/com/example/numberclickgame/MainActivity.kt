package com.example.numberclickgame

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var startButton: Button
    private lateinit var scoreTextView: TextView

    private lateinit var soundPool: SoundPool
    private val soundMap = mutableMapOf<Int, Int>() // 数字とサウンドIDを対応付ける

    private var startTime: Long = 0
    private var nextNumber = 1
    private val numbers = (1..8).shuffled()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)
        startButton = findViewById(R.id.startButton)
        scoreTextView = findViewById(R.id.scoreTextView)

        // SoundPoolの初期化
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8) // 同時再生できる音の数
            .setAudioAttributes(audioAttributes)
            .build()

        // 音声を読み込む
        soundMap[1] = soundPool.load(this, R.raw.note_do, 1) // ド
        soundMap[2] = soundPool.load(this, R.raw.note_re, 1) // レ
        soundMap[3] = soundPool.load(this, R.raw.note_mi, 1) // ミ
        soundMap[4] = soundPool.load(this, R.raw.note_fa, 1) // ファ
        soundMap[5] = soundPool.load(this, R.raw.note_so, 1) // ソ
        soundMap[6] = soundPool.load(this, R.raw.note_la, 1) // ラ
        soundMap[7] = soundPool.load(this, R.raw.note_si, 1) // シ
        soundMap[8] = soundPool.load(this, R.raw.note_high_do, 1) // 高いド

        // スタートボタンの処理
        startButton.setOnClickListener {
            startGame()
        }
    }

    private fun startGame() {
        // タイマーリセット
        startTime = SystemClock.elapsedRealtime()
        nextNumber = 1

        // 数字を配置
        gridLayout.removeAllViews()
        for (number in numbers) {
            val button = Button(this).apply {
                text = number.toString()
                textSize = 100f // テキストサイズを大きく
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 220 // ボタンの幅
                    height = 160 // ボタンの高さ
                }
                setOnClickListener { onNumberClick(this, number) }
            }
            gridLayout.addView(button)
        }

        // タイマーをリセット
        scoreTextView.text = "タイム : --"
        startButton.text = "１から順番に押して"
    }

    private fun onNumberClick(button: Button, number: Int) {
        if (number == nextNumber) {
            // ボタンを非表示にする
            button.visibility = View.INVISIBLE

            // サウンドを再生
            soundMap[number]?.let { soundId ->
                soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f) // 左右の音量1.0、再生速度1.0
            }

            nextNumber++

            if (nextNumber > 8) {
                // ゲーム完了
                val elapsed = (SystemClock.elapsedRealtime() - startTime) / 1000.0
                scoreTextView.text = "タイム : %.2f 秒".format(elapsed)
                startButton.text = "ボタンを押してゲーム開始"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // SoundPoolのリソースを解放
        soundPool.release()
    }
}
