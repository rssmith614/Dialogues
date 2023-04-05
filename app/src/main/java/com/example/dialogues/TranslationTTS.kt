package com.example.dialogues

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*


//KNOWN ISSUES:
//BACK BUTTON FOR SETTINGS WORKS BUT DOESNT REMEMBER THE TEXT ITS SUPPOSE TO SPEAK (STATE ISSUE)


class TranslationTTS : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var prefs: SharedPreferences
    private lateinit var prefs2: SharedPreferences
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
        prefs = PreferenceManager.getDefaultSharedPreferences(this) // Initialize prefs
        prefs2 = PreferenceManager.getDefaultSharedPreferences(this) // Initialize prefs
        talkspeed = prefs.getInt("speed", barStarter).toFloat()
        pitchvoice = prefs2.getInt("pitch", barStarter).toFloat()/50.0f


        val testString = intent.extras?.getString("Text", "")
        val originalText = findViewById<TextView>(R.id.source_textView)

        originalText.text = testString

        val displayText = findViewById<TextView>(R.id.TranslateTTS_textView)

        button = findViewById<ImageButton>(R.id.clickButton)

        val tts = TextToSpeech(this, this, "com.google.android.tts")
        var isSpeaking = false
        button.setOnClickListener{
            if (isSpeaking) {
                tts.stop()
                isSpeaking = false
            } else {
                if (testString != null) {
                    translateString(tts, displayText, testString) // Call your translateString() method
                    isSpeaking = true

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
         //   box.setBackgroundColor(resources.getColor(R.color.dark_mode_color))
       // } else {
        //    box.setBackgroundColor(resources.getColor(R.color.notwhite))
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
        val ilPreferences = getSharedPreferences("ilPreferences", MODE_PRIVATE)
        selectedil = ilPreferences.getString("Selectedil","").toString()

        val sourceLang: String = when(selectedil) {
            "English" -> TranslateLanguage.ENGLISH
            "Spanish" -> TranslateLanguage.SPANISH
            "French" -> TranslateLanguage.FRENCH
            "German" -> TranslateLanguage.GERMAN
            "Hindi" -> TranslateLanguage.HINDI
            "Chinese" -> TranslateLanguage.CHINESE
            "Japanese" -> TranslateLanguage.JAPANESE
            "Arabic" -> TranslateLanguage.ARABIC
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
            "Hindi"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.HINDI)
                    .build()
                translate(options)
            }

            "Chinese"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.CHINESE)
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
            "Arabic"->{
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(TranslateLanguage.ARABIC)
                    .build()
                translate(options)
            }
        }


    }


    fun speakString(tts: TextToSpeech, input: String){
        val talkspeed = prefs.getInt("speed", barStarter ).toFloat()/50.0f
        if (talkspeed == 0f){
            tts.setSpeechRate(0.10f)
        }
        val pitchvoice = prefs2.getInt("pitch", barStarter ).toFloat()/50.0f
        if(pitchvoice == 0f){
            tts.setPitch(0.10f)
        }
        tts.setSpeechRate(talkspeed)
        tts.setPitch(pitchvoice)

        val sharedPreferences = getSharedPreferences("VoicePreferences", MODE_PRIVATE)
        selectedvoice = sharedPreferences.getString("SelectedVoice", "").toString()
       
        when (selectedvoice) {
            "Female 1 (UK)" -> { //BRITISH WOMEN
                val voice1 = Voice("en-gb-x-gba-network", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(voice1)

            }
            "Female 2 (US)" -> { //American WOMEN
                val voice1 = Voice("en-us-x-tpf-local", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(voice1)

            }
            "Female 3 (IN)" -> { //INDIAN WOMEN
                val voice1 = Voice("en-in-x-enc-network", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(voice1)

            }
            "Female 4 (ES)" -> { //SPANISH MAN
                val locSpanish = Locale("spa","MEX")
                val voice1 = Voice("es-es-x-eee-local", locSpanish, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(voice1)
            }

            "Male 1 (UK)" -> { //BRITISH MAN
                val voice1 = Voice("en-gb-x-gbb-local", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(voice1)
            }
            "Male 2 (US)" -> { //AMERICAN MAN
                val voice1 = Voice("en-us-x-iol-local", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(voice1)
            }
            "Male 3 (IN)" -> { //INDIAN MAN
                val voice1 = Voice("en-in-x-ene-network", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(voice1)
            }
            "Male 4 (ES)" -> { //SPANISH MAN
                val locSpanish = Locale("spa","MEX")
                val voice1 = Voice("es-es-x-eef-local", locSpanish, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                tts.setVoice(voice1)
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
