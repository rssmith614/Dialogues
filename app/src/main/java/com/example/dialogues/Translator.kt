package com.example.dialogues

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class Translator : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translator)

    }

    fun translateString(input: String){


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
            .addOnSuccessListener { translatedText -> }
            .addOnFailureListener { Log.d(ContentValues.TAG, "Translate Failed") }


    }
}