package com.example.dialogues
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class Settings : AppCompatActivity() {

    private lateinit var voiceSpinner: Spinner
    private lateinit var ilSpinner: Spinner
    private lateinit var olSpinner: Spinner
    private lateinit var dropdown1: ImageButton
    private lateinit var dropdown2: ImageButton
    private lateinit var dropdown3: ImageButton
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
        voiceSpinner = findViewById(R.id.spinner)
        ilSpinner = findViewById(R.id.ilspinner)
        olSpinner = findViewById(R.id.olspinner)
        dropdown1 = findViewById(R.id.spinoptions)
        dropdown2 = findViewById(R.id.spinoptions2)
        dropdown3 = findViewById(R.id.spinoptions3)




        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs2 = PreferenceManager.getDefaultSharedPreferences(this)

        dropdown1.setOnClickListener{
            ilSpinner.performClick()
        }

        dropdown2.setOnClickListener{
            olSpinner.performClick()
        }

        dropdown3.setOnClickListener{
            voiceSpinner.performClick()
        }
        val speedBar = findViewById<SeekBar>(R.id.speedbar)
        val pitchBar = findViewById<SeekBar>(R.id.pitchbar)

        speedBar.progress = prefs.getInt("speed", 50)
        pitchBar.progress = prefs2.getInt("pitch", 50)


        speedBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val prefeditor = prefs.edit()
                prefeditor.putInt("speed", progress)
                prefeditor.apply()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        pitchBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val pref2editor = prefs2.edit()
                pref2editor.putInt("pitch", progress)
                pref2editor.apply()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        val backButton = findViewById<ImageButton>(R.id.backbutton)
        backButton.setOnClickListener {
            onBackPressed()

        }

        val ilOptions = arrayOf("English", "Spanish", "French", "German", "Hindi", "Chinese", "Japanese", "Arabic")
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ilOptions)
        ilSpinner.adapter = adapter2
        ilPreferences = getSharedPreferences("ilPreferences", MODE_PRIVATE)

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
        olPreferences = getSharedPreferences("olPreferences", MODE_PRIVATE)

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
        sharedPreferences = getSharedPreferences("VoicePreferences", MODE_PRIVATE)

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

        val switchButton = findViewById<Switch>(R.id.switcher)
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            val pauseSpeakPreferences = getSharedPreferences("pauseSpeakPrefs", MODE_PRIVATE)
            val pauseSpeakeditor = pauseSpeakPreferences.edit()
            if (isChecked) {
                pauseSpeakeditor.putBoolean("switched", true)
            } else {
                pauseSpeakeditor.remove("switched")
            }
            pauseSpeakeditor.apply()
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
