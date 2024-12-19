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
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var startButton: Button
    private lateinit var scoreTextView: TextView

    private lateinit var soundPool: SoundPool
    private val soundMap = mutableMapOf<Int, Int>() // 数字とサウンドIDを対応付ける
    private var ganbatteneSoundId: Int = 0 // 頑張ってね音声のID

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
        ganbatteneSoundId = soundPool.load(this, R.raw.ganbattene, 1) // 頑張ってね音声

        // スタートボタンの処理
        startButton.setOnClickListener {
            startGame()
        }
    }

    private fun startGame() {
        // タイマーリセット
        startTime = SystemClock.elapsedRealtime()
        nextNumber = 1

        // 「頑張ってね」音声を再生
        soundPool.play(ganbatteneSoundId, 1.0f, 1.0f, 0, 0, 1.0f)

        // 数字を配置
        gridLayout.removeAllViews()

        // デバイスの画面サイズを取得
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels // 画面幅
        val screenHeight = displayMetrics.heightPixels // 画面高さ

        // ボタンサイズを計算（例: 画面幅の1/5,縦1/4に設定）
        val buttonSizeWidth = (screenWidth / 5) // 画面幅の1/5
        val buttonSizeHeight = (screenHeight / 4) // 画面縦の1/4

        for (number in numbers) {
            val button = Button(this).apply {
                text = getString(R.string.number_placeholder, number)
                textSize = 100f // テキストサイズ
                layoutParams = GridLayout.LayoutParams().apply {
                    width = buttonSizeWidth
                    height = buttonSizeHeight
                    setMargins(1, 1, 1, 1) // マージン
                }
                setOnClickListener { onNumberClick(this, number) }
            }
            gridLayout.addView(button)
        }

        // タイマーをリセット
        scoreTextView.text = getString(R.string.time_placeholder)
        startButton.text = getString(R.string.prompt_start)
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
                // String.format を使用してロケールを指定
                scoreTextView.text = String.format(Locale.getDefault(), "タイム : %.2f 秒", elapsed)
                startButton.text = getString(R.string.prompt_restart)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // SoundPoolのリソースを解放
        soundPool.release()
    }
}
