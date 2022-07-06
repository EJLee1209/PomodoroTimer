package com.dldmswo1209.pomodorotimer

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import com.dldmswo1209.pomodorotimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var mBinding : ActivityMainBinding? = null
    val binding get() = mBinding!!
    private var currentCountDownTimer: CountDownTimer? = null
    private val soundPool = SoundPool.Builder().build()
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()

        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()

        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        soundPool.release() // soundPool에 로드 되었던 사운드 파일의 메모리 해제
    }

    private fun bindViews() {
        binding.seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(fromUser)
                        updateRemainTime(progress * 60 * 1000L)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return
                    if(seekBar.progress == 0) {
                        stopCountDown()
                    }else {
                        startCountDown()
                    }
                }
            }
        )
    }

    private fun initSounds() {
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object: CountDownTimer(initialMillis, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekbar(millisUntilFinished)
            }
            override fun onFinish() {
               completeCountDown()
            }
        }

    private fun startCountDown(){
        currentCountDownTimer = createCountDownTimer(binding.seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()


        tickingSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun stopCountDown(){
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }
    private fun completeCountDown(){
        updateRemainTime(0)
        updateSeekbar(0)

        soundPool.autoPause()
        bellSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun updateRemainTime(remainMillis: Long){
        val remainSeconds = remainMillis / 1000

        binding.remainMinuteTextView.text = "%02d'".format(remainSeconds / 60)
        binding.remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }
    private fun updateSeekbar(remainMillis: Long){
        binding.seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }

}