package com.example.dialogues

import android.content.ContentValues
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions


class TranslationScreen : AppCompatActivity(), AdapterView.OnItemSelectedListener, TextToSpeech.OnInitListener  {

    private var outputString = ""

    //TEMPORARY VARIABLES, FOR KLUDGE IMPLEMENTATION
    private var sourceLanguage = ""
    private var targetLanguage = ""
    private lateinit var inputString: String
    private lateinit var targetText :TextView
    private lateinit var sourceSpinner :Spinner
    private lateinit var targetSpinner :Spinner
    private  lateinit var tts :TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation_screen)

        tts = TextToSpeech(this, this, "com.google.android.tts")

       // val inputString = intent.extras?.getString("Text", "")
        inputString = "Hello World"
        val sourceText = findViewById<TextView>(R.id.ts_textView)
        sourceText.text = inputString

        targetText = findViewById<TextView>(R.id.ts_textView2)

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

        //HARD CODED VALUE FOR FLASH TALK PLEASE UPDATE
        setLanguages(sourceSpinner, targetSpinner)
        translateString(tts, inputString)



        val button = findViewById<Button>(R.id.ts_button)

        button.setOnClickListener{
            if (inputString != null) {
                speakString(tts, inputString)
            }
        }

        val button2 = findViewById<Button>(R.id.ts_button2)

        button2.setOnClickListener{
            if (outputString != null) {
                speakString(tts, outputString)
            }
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)

        settingsButton.setOnClickListener{
            if (inputString != null) {
            }
        }



    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val item = parent.getItemAtPosition(position).toString()
        Toast.makeText(parent.context, item, Toast.LENGTH_LONG).show()

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
            "English" -> sourceLanguage = "en"
            "Spanish" -> sourceLanguage = "es"
            "French" -> sourceLanguage = "fr"
            "German" -> sourceLanguage = "de"
            "Hindi" -> sourceLanguage = "hi"
            "Chinese" -> sourceLanguage = "zh"
            "Japanese" -> sourceLanguage = "ja"
            "Arabic" -> sourceLanguage = "ar"
        }

        when(target.selectedItem.toString()){
            "English" -> targetLanguage = "en"
            "Spanish" -> targetLanguage = "es"
            "French" -> targetLanguage = "fr"
            "German" -> targetLanguage = "de"
            "Hindi" -> targetLanguage = "hi"
            "Chinese" -> targetLanguage = "zh"
            "Japanese" -> targetLanguage = "ja"
            "Arabic" -> targetLanguage = "ar"
        }

        translateString(tts, inputString)

    }

    fun translateString(tts: TextToSpeech, input: String){

        var options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.fromLanguageTag(sourceLanguage).toString())
            .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetLanguage).toString())
            .build()

        val translator = Translation.getClient(options)

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                onModelSuccess(translator)

            }
            .addOnFailureListener { Log.d("MODEL", "Model download Failed") }


    }

    fun onModelSuccess (translator: com.google.mlkit.nl.translate.Translator){

        translator.translate(inputString)
            .addOnSuccessListener {
                    translatedText -> targetText.text  = translatedText
                outputString = translatedText

            }
            .addOnFailureListener { Log.d("TRANSLATION", "Translate Failed") }

    }




    fun speakString(tts: TextToSpeech, input: String){

        tts.speak(input, TextToSpeech.QUEUE_ADD, null)

    }
}