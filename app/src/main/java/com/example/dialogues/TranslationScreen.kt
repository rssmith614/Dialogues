package com.example.dialogues

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Bundle
import android.os.Vibrator
import android.preference.PreferenceManager
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*


class TranslationScreen : AppCompatActivity(), AdapterView.OnItemSelectedListener, TextToSpeech.OnInitListener  {

    private var outputString = ""

    //TEMPORARY VARIABLES, FOR KLUDGE IMPLEMENTATION
    private var sourceLanguage = ""
    private var targetLanguage = ""
    private lateinit var inputString: String
    private lateinit var sourceText :TextView
    private lateinit var targetText :TextView
    private lateinit var sourceSpinner :Spinner
    private lateinit var targetSpinner :Spinner
    private  lateinit var ttsSource :TextToSpeech
    private  lateinit var ttsTarget :TextToSpeech
    private lateinit var talkspeedPrefs: SharedPreferences
    private lateinit var pitchPrefs: SharedPreferences
    private var pitchvoice: Float = 0.0f
    private var talkspeed: Float = 0.0f
    private val barStarter = 50
    private var isSpeaking = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation_screen)

        ttsSource = TextToSpeech(this, this, "com.google.android.tts")
        ttsTarget = TextToSpeech(this, this, "com.google.android.tts")

        inputString = intent.extras?.getString("Text", "")!!
        //inputString = "Hello World"
        sourceText = findViewById<TextView>(R.id.ts_textView)
        sourceText.text = inputString
        sourceText.movementMethod = ScrollingMovementMethod()

        targetText = findViewById<TextView>(R.id.ts_textView2)
        targetText.movementMethod = ScrollingMovementMethod()

        sourceSpinner = findViewById<Spinner>(R.id.ts_spinner)
        sourceSpinner.onItemSelectedListener = this

        targetSpinner = findViewById<Spinner>(R.id.ts_spinner2)
        targetSpinner.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            this,
            R.array.LanguageOptions,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            sourceSpinner.adapter = adapter
            targetSpinner.adapter = adapter
        }


        val spinnerArray = resources.getStringArray(R.array.LanguageOptions)

        var sharedPreferences = getSharedPreferences("ilPreferences", MODE_PRIVATE)
        var selectedPreference = sharedPreferences.getString("Selectedil", "").toString()
        sourceSpinner.setSelection(spinnerArray.indexOf(selectedPreference))


        sharedPreferences = getSharedPreferences("olPreferences", MODE_PRIVATE)
        selectedPreference = sharedPreferences.getString("Selectedol", "").toString()
        targetSpinner.setSelection(spinnerArray.indexOf(selectedPreference))


        setLanguages(sourceSpinner, targetSpinner)
        translateString(inputString)

        val button = findViewById<Button>(R.id.ts_button)

        button.setOnClickListener {
            vibrateTime()
            if (isSpeaking) { //STOP BUTTON FUNCTIONALITY
                ttsSource.stop()
            } else {
                if (inputString != null) {
                    setLanguages(sourceSpinner, targetSpinner)
                    speakString(ttsSource, inputString)
                }
            }
            isSpeaking = !isSpeaking
        }

        val button2 = findViewById<Button>(R.id.ts_button2)

        button2.setOnClickListener { //STOP BUTTON FUNCTIONALITY
            vibrateTime()
            if (isSpeaking) {
                ttsTarget.stop()
            } else {
                if (outputString != null) {
                    setLanguages(sourceSpinner, targetSpinner)
                    speakString(ttsTarget, outputString)
                }
            }
            isSpeaking = !isSpeaking
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)

        settingsButton.setOnClickListener{
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
        
    }

    private fun vibrateTime() { //HAPTIC FEEDBACK
        val hapticFeedbackPreferences = getSharedPreferences("hfPrefs", Context.MODE_PRIVATE)
        val HapticFeedbackOn = hapticFeedbackPreferences.getBoolean("HapticFeedbackEnabled", false)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (HapticFeedbackOn) {
            vibrator.vibrate(50)
        }
    }

    override fun onResume() {
        super.onResume()

        val spinnerArray = resources.getStringArray(R.array.LanguageOptions)

        var sharedPreferences = getSharedPreferences("ilPreferences", MODE_PRIVATE)
        var selectedPreference = sharedPreferences.getString("Selectedil", "").toString()
        sourceSpinner.setSelection(spinnerArray.indexOf(selectedPreference))


        sharedPreferences = getSharedPreferences("olPreferences", MODE_PRIVATE)
        selectedPreference = sharedPreferences.getString("Selectedol", "").toString()
        targetSpinner.setSelection(spinnerArray.indexOf(selectedPreference))


        setLanguages(sourceSpinner, targetSpinner)
        translateString(inputString)

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val item = parent.getItemAtPosition(position).toString()


        val sourceChoice = sourceSpinner.selectedItemPosition
        var sharedPreferences = getSharedPreferences("ilPreferences", MODE_PRIVATE)
        var preferenceEditor = sharedPreferences.edit()
        preferenceEditor.putString("Selectedil", sourceSpinner.selectedItem.toString())
        preferenceEditor.apply()
        vibrateTime()

        val targetChoice = targetSpinner.selectedItemPosition
        sharedPreferences = getSharedPreferences("olPreferences", MODE_PRIVATE)
        preferenceEditor = sharedPreferences.edit()
        preferenceEditor.putString("Selectedol", targetSpinner.selectedItem.toString())
        vibrateTime()
        preferenceEditor.apply()



        setLanguages(sourceSpinner, targetSpinner)
    }


    override fun onNothingSelected(arg0: AdapterView<*>?) {}

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d(ContentValues.TAG, "TTS Success")
        }
    }

    fun setLanguages(source: Spinner, target: Spinner){
        when(source.selectedItem.toString()){
            "English" -> {
                sourceLanguage = "en"
                val voice = Voice("en-us-x-tpf-local", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                ttsSource.setVoice(voice)
            }
            "Spanish" -> {
                sourceLanguage = "es"
                val locSpanish = Locale("spa","MEX")
                val voice = Voice("es-es-x-eee-local", locSpanish, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                ttsSource.setVoice(voice)
            }
            "French" -> {
                targetLanguage = "fr"
                val voice = Voice("fr-fr-x-vlf-local", Locale.FRANCE, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                ttsSource.setVoice(voice)
            }
            "German" -> {
                targetLanguage = "de"
                val voice = Voice("de-de-x-nfh-local", Locale.GERMANY, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                ttsSource.setVoice(voice)
            }
        }

        when(target.selectedItem.toString()){
            "English" -> {
                targetLanguage = "en"
                val voice = Voice("en-us-x-tpf-local", Locale.US, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                ttsTarget.setVoice(voice)
            }
            "Spanish" -> {
                targetLanguage = "es"
                val locSpanish = Locale("spa","MEX")
                val voice = Voice("es-es-x-eee-local", locSpanish, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                ttsTarget.setVoice(voice)
            }

            "French" -> {
                targetLanguage = "fr"
                val voice = Voice("fr-fr-x-vlf-local", Locale.FRANCE, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                ttsTarget.setVoice(voice)
            }
            "German" ->{
                targetLanguage = "de"
                val voice = Voice("de-de-x-nfh-local", Locale.GERMANY, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null)
                ttsTarget.setVoice(voice)
            }
        }

        translateString(inputString)

    }

    fun translateString(input: String){

        var options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.fromLanguageTag(sourceLanguage).toString())
            .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetLanguage).toString())
            .build()

        val translator = Translation.getClient(options)

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        findViewById<ConstraintLayout>(R.id.loadingPanel).visibility = View.VISIBLE
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                onModelSuccess(translator)

            }
            .addOnFailureListener {
                Log.d("MODEL", "Model download Failed")
                findViewById<ConstraintLayout>(R.id.loadingPanel).visibility = View.GONE
            }




    }
    private fun getTalkSpeed(): Float { //Talk speed
        talkspeedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        talkspeed = talkspeedPrefs.getInt("speed", barStarter).toFloat() / 50.0f
        if (talkspeed == 0f) {
            return 0.10f
        }
        return talkspeed
    }

    private fun getPitchSpeed(): Float { //Pitch Speed
        pitchPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        pitchvoice = pitchPrefs.getInt("pitch", barStarter).toFloat() / 50.0f
        if (pitchvoice == 0f) {
            return 0.10f
        }
        return pitchvoice
    }

    fun onModelSuccess (translator: com.google.mlkit.nl.translate.Translator){

        translator.translate(inputString)
            .addOnSuccessListener {
                    translatedText -> targetText.text  = translatedText
                outputString = translatedText
                findViewById<ConstraintLayout>(R.id.loadingPanel).visibility = View.GONE

            }
            .addOnFailureListener {
                findViewById<ConstraintLayout>(R.id.loadingPanel).visibility = View.GONE
                Log.d("TRANSLATION", "Translate Failed")
            }

    }




    fun speakString(tts: TextToSpeech, input: String){
        val pauseSpeakPreferences = getSharedPreferences("pauseSpeakPrefs", MODE_PRIVATE)
        val pauseSpeakPrefSwitch = pauseSpeakPreferences.getBoolean("switched", false)
        val talkspeed = getTalkSpeed()
        val pitchvoice = getPitchSpeed()
        tts.setSpeechRate(talkspeed)
        tts.setPitch(pitchvoice)
        if(pauseSpeakPrefSwitch == true){ //PAUSE AFTER EACH WORD FEATURE
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
    override fun onPause() { //STOPS TTS FROM SPEAKING OUTSIDE THE APP
        super.onPause()
        if (ttsSource.isSpeaking) {
            ttsSource.stop()
        }
        if (ttsTarget.isSpeaking) {
            ttsTarget.stop()
        }
    }


}
