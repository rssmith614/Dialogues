package com.example.dialogues

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.preference.PreferenceManager
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*


//KNOWN ISSUES:
//SETTINGS BUTTON IN HAPTIC MODE VIBRATES EVEN WHEN THERES NO VIBRATE CODE ATTACHED TO IT (DOESNT NEED TO BE NECESSARILY FIXED)
//TOGGLING LIGHT/DARK MODE VIBRATES TWO TIMES (BUG COULD BE PASSED OFF AS A FEATURE :) ) (DOESNT NEED TO BE NECESSARILY FIXED)
class TranslationTTS : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var talkspeedPrefs: SharedPreferences
    private lateinit var pitchPrefs: SharedPreferences
    private var selectedvoice: String = ""
    private var selectedil: String = ""
    private var selectedol: String = ""
    private var pitchvoice: Float = 0.0f
    private var talkspeed: Float = 0.0f
    private val barStarter = 50
    private lateinit var button: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation_tts)

        tts = TextToSpeech(this, this, "com.google.android.tts")
        talkspeedPrefs = PreferenceManager.getDefaultSharedPreferences(this) // Initialize prefs
        pitchPrefs = PreferenceManager.getDefaultSharedPreferences(this) // Initialize prefs
        talkspeed = talkspeedPrefs.getInt("speed", barStarter).toFloat()
        pitchvoice = pitchPrefs.getInt("pitch", barStarter).toFloat()/50.0f

        val ilPreferences = getSharedPreferences("ilPreferences", MODE_PRIVATE)
        selectedil = ilPreferences.getString("Selectedil","").toString()


        val hapticFeedbackPreferences = getSharedPreferences("hfPrefs", Context.MODE_PRIVATE)
        val HapticFeedbackOn = hapticFeedbackPreferences.getBoolean("HapticFeedbackEnabled", false)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        fun vibrateTime() {
            if (HapticFeedbackOn) {
                vibrator.vibrate(50)
            }
        }



        val testString = intent.extras?.getString("Text", "")
        val originalText = findViewById<TextView>(R.id.source_textView)

        originalText.text = testString

        val displayText = findViewById<TextView>(R.id.TranslateTTS_textView)

        button = findViewById<ImageButton>(R.id.clickButton)

        val tts = TextToSpeech(this, this, "com.google.android.tts")
        button.setOnClickListener{
            if (tts.isSpeaking) {
                vibrateTime()
                tts.stop()
            } else {
                if (testString != null) {
                    vibrateTime()
                    translateString(tts, displayText, testString)

                }
            }
        }
        val darkModePreferences = getSharedPreferences("darkModePrefs", MODE_PRIVATE)
        val isNightModeEnabled = darkModePreferences.getBoolean("isNightModeEnabled", false)

        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        //val box = findViewById<View>(R.id.box)

        //if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
         // box.setBackgroundColor(resources.getColor(R.color.dark_mode_color))
       //} else {
           // box.setBackgroundColor(resources.getColor(R.color.notwhite))
       //}
    }

    override fun onInit(status: Int) {
        val settingsbind = findViewById<ImageButton>(R.id.settingbutton)
        if (status == TextToSpeech.SUCCESS) {
            Log.d(ContentValues.TAG, "TTS Success")
        }



        settingsbind.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
            //textView.setText("")
        }
        val backButton = findViewById<ImageButton>(R.id.backbutton)
        backButton.setOnClickListener {
            onBackPressed()

        }
    }


    //Currently function takes a TextView to display text, modify parameters as needed
    fun translateString(tts: TextToSpeech, displayText: TextView, input: String){
        val olPreferences = getSharedPreferences("olPreferences", MODE_PRIVATE)
        selectedol = olPreferences.getString("Selectedol","").toString()


        fun translate(options : TranslatorOptions) {
            val englishSpanishTranslator = Translation.getClient(options)

            var conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            englishSpanishTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener { }
                .addOnFailureListener { Log.d(ContentValues.TAG, "Model download Failed") }


            englishSpanishTranslator.translate(input)
                .addOnSuccessListener { translatedText ->
                    displayText.text = translatedText
                    speakString(tts, translatedText)

                }
                .addOnFailureListener { Log.d(ContentValues.TAG, "Translate Failed") }

        }

        val sourceLang: String = when(selectedil) {
            "English" -> TranslateLanguage.ENGLISH
            "Spanish" -> TranslateLanguage.SPANISH
            "French" -> TranslateLanguage.FRENCH
            "German" -> TranslateLanguage.GERMAN
            "Portuguese" -> TranslateLanguage.PORTUGUESE
            "Italian" -> TranslateLanguage.ITALIAN
            "Polish" -> TranslateLanguage.POLISH
            "Romanian" -> TranslateLanguage.ROMANIAN
            else -> TranslateLanguage.ENGLISH // Default to English if no language is selected
        }


        when (selectedol){
            "English" -> {
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build()
                translate(options)
            }

            "Spanish"-> {
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.SPANISH)
                    .build()
                translate(options)
            }
            "French"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.FRENCH)
                    .build()
                translate(options)
            }
            "German"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.GERMAN)
                    .build()
                translate(options)
            }
            "Portuguese"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.PORTUGUESE)
                    .build()
                translate(options)
            }

            "Italian"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.ITALIAN)
                    .build()
                translate(options)
            }
            "Polish"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.POLISH)
                    .build()
                translate(options)
            }
            "Romanian"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.ROMANIAN)
                    .build()
                translate(options)
            }
            "Japanese"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.JAPANESE)
                    .build()
                translate(options)
            }
            "Mandarin"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.CHINESE)
                    .build()
                translate(options)
            }
            "Russian"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.RUSSIAN)
                    .build()
                translate(options)
            }
        }


    }


    fun speakString(tts: TextToSpeech, input: String){
        val talkspeed = talkspeedPrefs.getInt("speed", barStarter ).toFloat()/50.0f
        if (talkspeed == 0f){
            tts.setSpeechRate(0.10f)
        }
        val pitchvoice = pitchPrefs.getInt("pitch", barStarter ).toFloat()/50.0f
        if(pitchvoice == 0f){
            tts.setPitch(0.10f)
        }
        tts.setSpeechRate(talkspeed)
        tts.setPitch(pitchvoice)

        val sharedPreferences = getSharedPreferences("VoicePreferences", MODE_PRIVATE)
        selectedvoice = sharedPreferences.getString("SelectedVoice", "").toString()
       
        when (selectedvoice) {
            "Female 1 (UK)" -> { //BRITISH WOMEN
                val chosenVoice = Voice("en-gb-x-gba-network", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(chosenVoice)

            }
            "Female 2 (US)" -> { //American WOMEN
                val chosenVoice = Voice("en-us-x-tpf-local", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(chosenVoice)

            }
            "Female 3 (IN)" -> { //INDIAN WOMEN
                val chosenVoice = Voice("en-in-x-enc-network", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(chosenVoice)

            }
            "Female 4 (ES)" -> { //SPANISH MAN
                val locSpanish = Locale("spa","MEX")
                val chosenVoice = Voice("es-es-x-eee-local", locSpanish, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(chosenVoice)
            }

            "Male 1 (UK)" -> { //BRITISH MAN
                val chosenVoice = Voice("en-gb-x-gbb-local", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(chosenVoice)
            }
            "Male 2 (US)" -> { //AMERICAN MAN
                val chosenVoice = Voice("en-us-x-iol-local", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(chosenVoice)
            }
            "Male 3 (IN)" -> { //INDIAN MAN
                val chosenVoice = Voice("en-in-x-ene-network", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(chosenVoice)
            }
            "Male 4 (ES)" -> { //SPANISH MAN
                val locSpanish = Locale("spa","MEX")
                val chosenVoice = Voice("es-es-x-eef-local", locSpanish, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(chosenVoice)
            }
        }
        val pauseSpeakPreferences = getSharedPreferences("pauseSpeakPrefs", MODE_PRIVATE)
        val pauseSpeakPrefSwitch = pauseSpeakPreferences.getBoolean("switched", false)
            if(pauseSpeakPrefSwitch == true){
                val alttext = input.split(" ")
                var i = 0
                while (i < alttext.size) {
                    tts.speak(alttext[i], TextToSpeech.QUEUE_ADD, null) //tts speaks
                    i++
                }
            }
        else {
                tts.speak(input, TextToSpeech.QUEUE_ADD, null)
            }


    }


}
