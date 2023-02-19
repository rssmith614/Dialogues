package com.example.dialogues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import java.util.*

/*TO-DO LIST (Will probably not implement all of these!)
1. Add text highlighting while the tts is speaking (pain)
2. Fix up UI
3. Add more voices (agony)
4. Pause functionality
*/
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
        val Toasttext3 = "Input cannot be blank"
        var btnbind = findViewById<Button>(R.id.speakbutton)
        var editbtnbind = findViewById<EditText>(R.id.editTex)
        var stopbind = findViewById<Button>(R.id.stopbutton)

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

                val talkspeed = BarSpeed()
                val pitchvoice = BarPitch()
                tts.setSpeechRate(talkspeed)
                tts.setPitch(pitchvoice)
                tts.speak(text, TextToSpeech.QUEUE_ADD, null) //tts speaks

            }
        }

        stopbind.setOnClickListener {
            tts.stop()
        }
    }
    fun BarSpeed(): Float {
        var speedbarz = findViewById<SeekBar>(R.id.speedbar)
        var talkspeed = speedbarz.progress.toFloat()
        talkspeed /= 50

        if (talkspeed <0.25){
            talkspeed = 0.25f
        }
        return talkspeed
    }

    fun BarPitch() :Float {
        var pitchbarz = findViewById<SeekBar>(R.id.pitchbar)
        var pitchvoice = pitchbarz.progress.toFloat()
        pitchvoice /= 50

        if(pitchvoice < 0.1){
            pitchvoice = 0.1f
        }
        return pitchvoice
    }
}
