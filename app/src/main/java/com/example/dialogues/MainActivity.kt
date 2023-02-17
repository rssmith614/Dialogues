package com.example.dialogues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener{
    private lateinit var tts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tts = TextToSpeech(this, this)

    }


    override fun onInit(status:Int){
        val duration = Toast.LENGTH_SHORT
        val Toasttext = "Error has occurred!"
        val Toasttext2 = "Language current not available at this moment!"
        val Toasttext3 = "Input cannot be blank"
        var btnbind = findViewById<Button>(R.id.speakbutton)
        var editbtnbind = findViewById<EditText>(R.id.editTex)

        btnbind.setOnClickListener { //listens to when user clicks on button
            if (status == TextToSpeech.ERROR) {
                Toast.makeText(getApplicationContext(), Toasttext, duration).show()
            }
            else if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.ENGLISH) //replace english with user variable for language choice
                val text = editbtnbind.text.toString() //text corresponds to user input, but will correspond to the words from an image
                if (text == null || text == " " || text ==""){
                    Toast.makeText(getApplicationContext(), Toasttext3, duration).show() //displays error to user
                }
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) { //not tested :)
                    Toast.makeText(getApplicationContext(), Toasttext2, duration).show()
                }
                tts.speak(text, TextToSpeech.QUEUE_ADD, null) //tts speaks
            }
        }

    }
}
