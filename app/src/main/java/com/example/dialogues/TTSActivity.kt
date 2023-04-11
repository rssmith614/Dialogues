package com.example.dialogues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.*
import java.util.*

/*
******************* TTS WITH ACCESSIBILITY FEATURES ***************
* Features:
* Different TTS voices (English and Spanish)!
* Adjustable pitch
* Adjustable voice
* Clickable buttons! (so cool)
* Stop Button
* TTS Pause between words
*
* Features that need to be implemented:
* Color-blind friendly interface
* Settings Menu with all the features
*
* Optional Features:
* Text Highlighting while TTS is reading (pain)
* Pause Button
* Mute with proximity sensor
* Have user adjust font size
* Volume Control
*
 */
class TTSActivity : AppCompatActivity(), TextToSpeech.OnInitListener{
    private lateinit var tts: TextToSpeech
    private var selectedvoice: String = ""
    private var pitchvoice: Float = 0.0f
    private var talkspeed: Float = 0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tts)
        tts = TextToSpeech(this, this, "com.google.android.tts")

    }


    override fun onInit(status:Int){
        val duration = Toast.LENGTH_SHORT
        val toasttext = "Error has occurred!"
        val toasttext3 = "Input cannot be blank"
        val btnbind = findViewById<Button>(R.id.speakbutton)
        val editbtnbind = findViewById<EditText>(R.id.editTex)
        val stopbind = findViewById<Button>(R.id.stopbutton)
        val switchbind = findViewById<Switch>(R.id.switcher)
        val options = arrayOf("Male 1 (UK)","Male 2 (US)", "Male 3 (IN)", "Male 4 (ES)","Female 1 (UK)", "Female 2 (US)", "Female 3 (IN)", "Female 4 (ES)" )
        val spinner = findViewById<Spinner>(R.id.spinner)
        val listadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        spinner.adapter = listadapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedvoice = spinner.selectedItem.toString()
                Log.d("Voicez:", selectedvoice)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        btnbind.setOnClickListener { //listens to when user clicks on button
            if (status == TextToSpeech.ERROR) {
                Toast.makeText(getApplicationContext(), toasttext, duration).show()
            }
            else if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.ENGLISH) //replace english with user variable for language choice
                val text = editbtnbind.text.toString() //text corresponds to user input, but will correspond to the words from an image
                if (text == " " || text ==""){
                    Toast.makeText(getApplicationContext(), toasttext3, duration).show() //displays error to user
                }
                val talkspeed = barSpeed()
                val pitchvoice = barPitch()
                tts.setSpeechRate(talkspeed)
                tts.setPitch(pitchvoice)


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
                    "Female 4 (ES)" -> { //SPANISH MANtext
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
                if (switchbind.isChecked) {
                    val alttext = text.split(" ")
                    var i = 0
                    while (i < alttext.size) { //Text highlighting
                        tts.speak(alttext[i], TextToSpeech.QUEUE_ADD, null) //tts speaks
                        i++
                    }
                }
                else {
                        tts.speak(text, TextToSpeech.QUEUE_ADD, null)
                    }
            }
        }

        stopbind.setOnClickListener {
            tts.stop()
            //textView.setText("")
        }
    }
    private fun barSpeed(): Float {
        val speedbarz = findViewById<SeekBar>(R.id.speedbar)
        talkspeed = speedbarz.progress.toFloat()
        talkspeed /= 50

        if (talkspeed <0.25){
            talkspeed = 0.25f
        }
        return talkspeed
    }

    private fun barPitch() :Float {
        val pitchbarz = findViewById<SeekBar>(R.id.pitchbar)
        pitchvoice = pitchbarz.progress.toFloat()
        pitchvoice /= 50

        if(pitchvoice < 0.1){
            pitchvoice = 0.1f
        }
        return pitchvoice
    }
}

/* val voice2 = tts.getVoices()
               for (voice in voice2){
                   Log.d("TTS", "Voice Name: ${voice.name}")
               }

                */

/* while(i < text2.size) { //Text highlighting
               val text2 = text.split(" ")
               var i =0
                   tts.speak(text2[i], TextToSpeech.QUEUE_ADD, null) //tts speaks
                   i++

               }

               */
