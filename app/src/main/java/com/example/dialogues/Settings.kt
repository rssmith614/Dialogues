package com.example.dialogues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*

class Settings : AppCompatActivity() {

    private lateinit var voiceSpinner: Spinner
    private lateinit var ilSpinner: Spinner
    private lateinit var olSpinner: Spinner
    private  var selectedVoice: String = ""
    private var selectedil: String = ""
    private var selectedol: String = ""
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefs: SharedPreferences
    private lateinit var prefs2: SharedPreferences
    private lateinit var ilPreferences: SharedPreferences
    private lateinit var olPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_tts)
        val actionBar = supportActionBar
        actionBar!!.title = "Settings Activity"
        voiceSpinner = findViewById<Spinner>(R.id.spinner)
        ilSpinner = findViewById<Spinner>(R.id.ilspinner)
        olSpinner = findViewById<Spinner>(R.id.olspinner)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs2 = PreferenceManager.getDefaultSharedPreferences(this)
        val speedBar = findViewById<SeekBar>(R.id.speedbar)
        val pitchBar = findViewById<SeekBar>(R.id.pitchbar)
        speedBar.progress = prefs.getInt("speed", 50)
        pitchBar.progress = prefs2.getInt("pitch", 50)


        speedBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val prefeditor = prefs.edit()
                prefeditor.putInt("speed", progress)
                prefeditor.apply()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        pitchBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val pref2editor = prefs2.edit()
                pref2editor.putInt("pitch", progress)
                pref2editor.apply()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        val ilOptions = arrayOf("English", "Spanish", "French", "German", "Hindi", "Chinese", "Japanese", "Arabic")
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ilOptions)
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ilSpinner.adapter = adapter2
        ilPreferences = getSharedPreferences("ilPreferences", Context.MODE_PRIVATE)

        selectedil = ilPreferences.getString("Selectedil", "").toString()
        ilSpinner.setSelection(ilOptions.indexOf(selectedil))

        ilSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedil = parent.getItemAtPosition(position).toString()
                val ileditor = ilPreferences.edit()
                ileditor.putString("Selectedil", selectedil)
                ileditor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val olOptions =  arrayOf("English", "Spanish", "French", "German", "Hindi", "Chinese", "Japanese", "Arabic")
        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ilOptions)
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        olSpinner.adapter = adapter3
        olPreferences = getSharedPreferences("olPreferences", Context.MODE_PRIVATE)

        selectedol = olPreferences.getString("Selectedol", "").toString()
        olSpinner.setSelection(olOptions.indexOf(selectedol))

        olSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedol = parent.getItemAtPosition(position).toString()
                val oleditor = olPreferences.edit()
                oleditor.putString("Selectedol", selectedol)
                oleditor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        //Voice Part of Code
        val voiceOptions = arrayOf("Female 1 (UK)", "Female 2 (US)", "Female 3 (IN)", "Female 4 (ES)", "Male 1 (UK)", "Male 2 (US)", "Male 3 (IN)", "Male 4 (ES)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, voiceOptions)
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        voiceSpinner.adapter = adapter
        sharedPreferences = getSharedPreferences("VoicePreferences", Context.MODE_PRIVATE)

        selectedVoice = sharedPreferences.getString("SelectedVoice", "").toString()
        voiceSpinner.setSelection(voiceOptions.indexOf(selectedVoice))

        voiceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedVoice = parent.getItemAtPosition(position).toString()
                val voiceeditor = sharedPreferences.edit()
                voiceeditor.putString("SelectedVoice", selectedVoice)
                voiceeditor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        //Dark/light mode code
        val switchbtn = findViewById<Switch>(R.id.switch_dark_mode)
        switchbtn.setOnCheckedChangeListener{_, isChecked ->


            if (switchbtn.isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchbtn.text = "Disable dark mode"
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchbtn.text ="Enable dark mode"
            }
        }

    }

}








// class Settings: AppCompatActivity() {
// private lateinit var tts: TextToSpeech
// private var selectedvoice: String = ""
// private var pitchvoice: Float = 0.0f
// private var talkspeed: Float = 0.0f
// val speedbarz = findViewById<SeekBar>(R.id.speedbar)
//
// override fun onCreate(savedInstanceState: Bundle?) {
// super.onCreate(savedInstanceState)
// setContentView(R.layout.settings_tts)
// val actionBar = supportActionBar
// actionBar!!.title = "Settings Activity"
// actionBar.setDisplayHomeAsUpEnabled(true)
//
// }
// fun ttsfeatures(){
// val ttsActivity = TTSActivity()
// val options = arrayOf("Male 1 (UK)","Male 2 (US)", "Male 3 (IN)", "Male 4 (ES)","Female 1 (UK)", "Female 2 (US)", "Female 3 (IN)", "Female 4 (ES)" )
// val spinner = findViewById<Spinner>(R.id.spinner)
// val listadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
// val switchbind = findViewById<Switch>(R.id.switcher)
//
// spinner.adapter = listadapter
//
// spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
// override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
// selectedvoice = spinner.selectedItem.toString()
// Log.d("Voicez:", selectedvoice)
// }
//
// override fun onNothingSelected(p0: AdapterView<*>?) {
// TODO("Not yet implemented")
// }
//
// }
// val talkspeed = barSpeed()
// val pitchvoice = barPitch()
//
// }
//
// fun changeVoice(){
//
// when (selectedvoice) {
// "Female 1 (UK)" -> { //BRITISH WOMEN
// val voice1 = Voice(
// "en-gb-x-gba-network",
// Locale.US,
// Voice.QUALITY_NORMAL,
// Voice.LATENCY_NORMAL,
// false,
// null
// )
// tts.setVoice(voice1)
//
// }
// "Female 2 (US)" -> { //American WOMEN
// val voice1 = Voice(
// "en-us-x-tpf-local",
// Locale.US,
// Voice.QUALITY_NORMAL,
// Voice.LATENCY_NORMAL,
// false,
// null
// )
// tts.setVoice(voice1)
//
// }
// "Female 3 (IN)" -> { //INDIAN WOMEN
// val voice1 = Voice(
// "en-in-x-enc-network",
// Locale.US,
// Voice.QUALITY_NORMAL,
// Voice.LATENCY_NORMAL,
// false,
// null
// )
// tts.setVoice(voice1)
//
// }
// "Female 4 (ES)" -> { //SPANISH MAN
// val locSpanish = Locale("spa", "MEX")
// val voice1 = Voice(
// "es-es-x-eee-local",
// locSpanish,
// Voice.QUALITY_NORMAL,
// Voice.LATENCY_NORMAL,
// false,
// null
// )
// tts.setVoice(voice1)
// }
//
// "Male 1 (UK)" -> { //BRITISH MAN
// val voice1 = Voice(
// "en-gb-x-gbb-local",
// Locale.US,
// Voice.QUALITY_NORMAL,
// Voice.LATENCY_NORMAL,
// false,
// null
// )
// tts.setVoice(voice1)
// }
// "Male 2 (US)" -> { //AMERICAN MAN
// val voice1 = Voice(
// "en-us-x-iol-local",
// Locale.US,
// Voice.QUALITY_NORMAL,
// Voice.LATENCY_NORMAL,
// false,
// null
// )
// tts.setVoice(voice1)
// }
// "Male 3 (IN)" -> { //INDIAN MAN
// val voice1 = Voice(
// "en-in-x-ene-network",
// Locale.US,
// Voice.QUALITY_NORMAL,
// Voice.LATENCY_NORMAL,
// false,
// null
// )
// tts.setVoice(voice1)
// }
// "Male 4 (ES)" -> { //SPANISH MAN
// val locSpanish = Locale("spa", "MEX")
// val voice1 = Voice(
// "es-es-x-eef-local",
// locSpanish,
// Voice.QUALITY_NORMAL,
// Voice.LATENCY_NORMAL,
// false,
// null
// )
// tts.setVoice(voice1)
// }
// }
// }
// fun barSpeed(): Float {
// val speedbarz = findViewById<SeekBar>(R.id.speedbar)
// talkspeed = speedbarz.progress.toFloat()
// talkspeed /= 50
//
// if (talkspeed <0.25){
// talkspeed = 0.25f
// }
// return talkspeed
// }
//
// fun barPitch() :Float {
// val pitchbarz = findViewById<SeekBar>(R.id.pitchbar)
// pitchvoice = pitchbarz.progress.toFloat()
// pitchvoice /= 50
//
// if(pitchvoice < 0.1){
// pitchvoice = 0.1f
// }
// return pitchvoice
// }
//
// }
