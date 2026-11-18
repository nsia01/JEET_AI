package com.jeet.ai

import android.app.Activity
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.SpeechRecognizer
import android.speech.RecognizerIntent
import android.speech.RecognitionListener
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var status: TextView
    private lateinit var recognizer: SpeechRecognizer
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // VOICE ENGINE
        tts = TextToSpeech(this) { result ->
            ttsReady = result == TextToSpeech.SUCCESS
            if (ttsReady) {
                tts?.language = Locale("hi", "IN")
            }
        }

        // APP DESIGN
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(30, 30, 30, 30)
        layout.setBackgroundColor(Color.rgb(15, 20, 35))

        val title = TextView(this)
        title.text = "JEET AI"
        title.textSize = 32f
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        status = TextView(this)
        status.text = "Your Personal AI Assistant"
        status.textSize = 18f
        status.setTextColor(Color.LTGRAY)
        status.gravity = Gravity.CENTER

        val start = Button(this)
        start.text = "START LISTENING"

        val stop = Button(this)
        stop.text = "STOP"

        layout.addView(title)
        layout.addView(status)
        layout.addView(start)
        layout.addView(stop)

        setContentView(layout)

        // SPEECH RECOGNITION
        if (SpeechRecognizer.isRecognitionAvailable(this)) {

            recognizer = SpeechRecognizer.createSpeechRecognizer(this)

            recognizer.setRecognitionListener(object : RecognitionListener {

                override fun onReadyForSpeech(params: Bundle?) {
                    status.text = "Listening..."
                }

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    status.text = "Processing..."
                }

                override fun onError(error: Int) {
                    status.text = "Error: $error"
                }

                override fun onResults(results: Bundle?) {

                    val data = results?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )

                    val spokenText = data?.firstOrNull() ?: "Nothing heard"

                    status.text = spokenText

                    speak(spokenText)
                }

                override fun onPartialResults(partialResults: Bundle?) {}

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

        } else {
            status.text = "Speech recognition unavailable"
        }

        // START BUTTON
        start.setOnClickListener {

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                    arrayOf(Manifest.permission.RECORD_AUDIO), 101
                )

            } else {
                startListening()
            }
        }

        // STOP BUTTON
        stop.setOnClickListener {

            if (::recognizer.isInitialized) {
                recognizer.cancel()
            }

            tts?.stop()
            status.text = "Stopped"
        }
    }

    // START LISTENING
    private fun startListening() {

        if (!::recognizer.isInitialized) return

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        // Hindi + English recognition
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "hi-IN"
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
            "hi-IN"
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_PARTIAL_RESULTS,
            true
        )

        recognizer.startListening(intent)
    }

    // VOICE REPLY
    private fun speak(text: String) {

        if (!ttsReady) return

        val language = if (text.any {
                it.code in 0x0900..0x097F
            }) {
            Locale("hi", "IN")
        } else {
            Locale.US
        }

        val result = tts?.setLanguage(language)

        if (result != TextToSpeech.LANG_MISSING_DATA &&
            result != TextToSpeech.LANG_NOT_SUPPORTED) {

            tts?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "JEET_AI"
            )
        }
    }

    // MICROPHONE PERMISSION
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )

        if (requestCode == 101 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListening()

        } else {
            status.text = "Microphone permission denied"
        }
    }

    // CLOSE VOICE ENGINE
    override fun onDestroy() {

        if (::recognizer.isInitialized) {
            recognizer.destroy()
        }

        tts?.shutdown()

        super.onDestroy()
    }
}
