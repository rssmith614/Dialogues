package com.example.dialogues

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class TranslationTTS : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation_tts)

        val displayText = findViewById<TextView>(R.id.TranslateTTS_textView)

        translateString(displayText, "Me gusta gatos y perros")

    }

    //Currently function takes a TextView to display text, modify parameters as needed
    fun translateString(displayText: TextView, input: String){

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.SPANISH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        val englishSpanishTranslator = Translation.getClient(options)

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        englishSpanishTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener { }
            .addOnFailureListener { Log.d(ContentValues.TAG, "Model download Failed") }


        englishSpanishTranslator.translate(input)
            .addOnSuccessListener { translatedText -> displayText.text  = translatedText}
            .addOnFailureListener { Log.d(ContentValues.TAG, "Translate Failed") }


    }
}