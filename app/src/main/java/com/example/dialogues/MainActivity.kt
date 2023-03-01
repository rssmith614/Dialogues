package com.example.dialogues

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : AppCompatActivity() {

    /**
       Setting to English to Spanish for now since other languages
       requires a machine learning implementation for later

       Source: https://developers.google.com/ml-kit/language/translation/android
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)

        val testString = "Me gusta gatos"

        translateString(textView, testString)



    }

    fun translateString(textView: TextView, input: String){


        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.SPANISH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        val englishSpanishTranslator = Translation.getClient(options)

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        englishSpanishTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener { textView.text = "Model downloaded" }
            .addOnFailureListener { Log.d(TAG, "Model download Failed") }


        englishSpanishTranslator.translate(input)
            .addOnSuccessListener { translatedText -> textView.text = translatedText }
            .addOnFailureListener { Log.d(TAG, "Translate Failed") }


    }

}