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


//KNOWN ERRORS/ISSUES:
//BACK BUTTON FOR SETTINGS WORKS BUT DOESNT REMEMBER THE TEXT ITS SUPPOSE TO SPEAK (STATE ISSUE)
//PAUSE AFTER EACH WORD FEATURE IS MISSING AND STILL NEEDS TO BE INTEGRATED.
//IF USER PUTS PROGRESS BAR TO ZERO, IT WILL TALK NORMAL. ADD A RESTRICTION THAT GIVES THE LOWEST PITCH OR SPEED INSTEAD. 
class TranslationTTS : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var prefs: SharedPreferences
    private lateinit var prefs2: SharedPreferences
    private var selectedvoice: String = ""
    private var pitchvoice: Float = 0.0f
    private var talkspeed: Float = 0.0f
    private val barStarter = 50
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation_tts)

        tts = TextToSpeech(this, this, "com.google.android.tts")
        prefs = PreferenceManager.getDefaultSharedPreferences(this) // Initialize prefs
        prefs2 = PreferenceManager.getDefaultSharedPreferences(this) // Initialize prefs
        talkspeed = prefs.getInt("speed", barStarter).toFloat()/50.0f
        pitchvoice = prefs2.getInt("pitch", barStarter).toFloat()/50.0f


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
        val settingsbind = findViewById<ImageButton>(R.id.settingbutton)
        if (status == TextToSpeech.SUCCESS) {
            Log.d(ContentValues.TAG, "TTS Success")
        }



        settingsbind.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
            //textView.setText("")
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
        val talkspeed = prefs.getInt("speed", barStarter ).toFloat()/50.0f
        val pitchvoice = prefs2.getInt("pitch", barStarter ).toFloat()/50.0f
        tts.setSpeechRate(talkspeed)
        tts.setPitch(pitchvoice)
        val sharedPreferences = getSharedPreferences("VoicePreferences", Context.MODE_PRIVATE)
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

        tts.speak(input, TextToSpeech.QUEUE_ADD, null)

    }

}
