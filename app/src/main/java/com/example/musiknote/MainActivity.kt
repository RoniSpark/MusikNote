package com.example.musiknote

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm

class MainActivity : AppCompatActivity() {
    private lateinit var startRecordingButton: Button
    private lateinit var stopRecordingButton: Button
    private lateinit var result_tv: TextView
    private lateinit var mediaRecorder: MediaRecorder
    private var isRecording = false

    fun clickStartRecording(){
        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)

        val pdh = PitchDetectionHandler { result, e ->
            val pitchInHz = result.pitch
            runOnUiThread {
                result_tv.text = "" + pitchInHz
            }
        }
        val p: AudioProcessor = PitchProcessor(PitchEstimationAlgorithm.FFT_YIN, 22050f, 1024, pdh)
        dispatcher.addAudioProcessor(p)
        Thread(dispatcher, "Audio Dispatcher").start()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startRecordingButton = findViewById(R.id.button_start_recording)
        stopRecordingButton = findViewById(R.id.button_stop_recording)
        result_tv = findViewById(R.id.result_tv)


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }

        startRecordingButton.setOnClickListener {
            clickStartRecording()

        }
    }
}
