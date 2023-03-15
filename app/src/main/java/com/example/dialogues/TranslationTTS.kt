package com.example.dialogues

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*

class TranslationTTS : AppCompatActivity(), TextToSpeech.OnInitListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation_tts)


        val testString = intent.extras?.getString("Text", "")
        val originalText = findViewById<TextView>(R.id.source_textView)

        originalText.text = testString

        val displayText = findViewById<TextView>(R.id.TranslateTTS_textView)

        val button = findViewById<Button>(R.id.clickButton)

        val tts = TextToSpeech(this, this, "com.google.android.tts")

        button.setOnClickListener{
            if (testString != null) {
                translateString(tts, displayText, testString)
            }
        }


    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d(ContentValues.TAG, "TTS Success")
        }
    }

    //Currently function takes a TextView to display text, modify parameters as needed
    fun translateString(tts: TextToSpeech, displayText: TextView, input: String){

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()

        val englishSpanishTranslator = Translation.getClient(options)

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        englishSpanishTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener { }
            .addOnFailureListener { Log.d(ContentValues.TAG, "Model download Failed") }


        englishSpanishTranslator.translate(input)
            .addOnSuccessListener {
                    translatedText -> displayText.text  = translatedText
                    speakString(tts, translatedText)

            }
            .addOnFailureListener { Log.d(ContentValues.TAG, "Translate Failed") }


    }

    fun speakString(tts: TextToSpeech, input: String){

            tts.speak(input, TextToSpeech.QUEUE_ADD, null)

    }

}