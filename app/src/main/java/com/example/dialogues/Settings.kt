package com.example.dialogues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.os.Vibrator
import androidx.core.content.ContextCompat

class Settings : AppCompatActivity() {

    private lateinit var voiceSpinner: Spinner
    private lateinit var ilSpinner: Spinner
    private lateinit var olSpinner: Spinner
    private lateinit var dropdown1: ImageButton
    private lateinit var dropdown2: ImageButton
    private lateinit var dropdown3: ImageButton
    //private  var selectedVoice: String = ""
    private var selectedil: String = ""
    private var selectedol: String = ""
    private var speedValueText: String = ""
    //private lateinit var sharedPreferences: SharedPreferences
    private lateinit var talkspeedPrefs: SharedPreferences
    private lateinit var pitchPrefs: SharedPreferences
    private lateinit var ilPreferences: SharedPreferences
    private lateinit var olPreferences: SharedPreferences
    private var HapticFeedbackOn = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_tts)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        //voiceSpinner = findViewById(R.id.spinner)
        ilSpinner = findViewById(R.id.ilspinner)
        olSpinner = findViewById(R.id.olspinner)
        dropdown1 = findViewById(R.id.spinoptions)
        dropdown2 = findViewById(R.id.spinoptions2)
        //dropdown3 = findViewById(R.id.spinoptions3)
        fun vibrateDevice() {
            if (HapticFeedbackOn == true) {
                vibrator.vibrate(50)
            }
        }

        var haptic_feedback = findViewById<Switch>(R.id.hapticswitch)

        val hapticFeedbackPreferences = getSharedPreferences("hfPrefs", Context.MODE_PRIVATE)
        HapticFeedbackOn = hapticFeedbackPreferences.getBoolean("HapticFeedbackEnabled", false)
        haptic_feedback.isChecked = HapticFeedbackOn
        haptic_feedback.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                HapticFeedbackOn = true
                Toast.makeText(this, "Haptic Feedback Enabled", Toast.LENGTH_SHORT).show()
                vibrateDevice()
                }
            else {
                HapticFeedbackOn = false
                Toast.makeText(this, "Haptic Feedback Disabled", Toast.LENGTH_SHORT).show()
                }
            hapticFeedbackPreferences.edit().putBoolean("HapticFeedbackEnabled", HapticFeedbackOn).apply()
            }


        talkspeedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        pitchPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        dropdown1.setOnClickListener{
            ilSpinner.performClick()
            //vibrateDevice()

        }

        dropdown2.setOnClickListener{
            olSpinner.performClick()
            //vibrateDevice()

        }

        //dropdown3.setOnClickListener{
           // voiceSpinner.performClick()
            //vibrateDevice()

        //}
        val speedBar = findViewById<SeekBar>(R.id.speedbar)
        val pitchBar = findViewById<SeekBar>(R.id.pitchbar)
        val speedText =findViewById<TextView>(R.id.speedtext)
        val pitchText =findViewById<TextView>(R.id.pitchtext)


        speedBar.progress = talkspeedPrefs.getInt("speed", 50)
        pitchBar.progress = pitchPrefs.getInt("pitch", 50)

        speedText.text = talkspeedPrefs.getInt("speed", 50).toString()
        pitchText.text = pitchPrefs.getInt("pitch", 50).toString()

        speedBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val prefeditor = talkspeedPrefs.edit()
                prefeditor.putInt("speed", progress)
                prefeditor.apply()
                speedText.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        pitchBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val pref2editor = pitchPrefs.edit()
                pref2editor.putInt("pitch", progress)
                pref2editor.apply()
                pitchText.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        val backButton = findViewById<ImageButton>(R.id.backbutton)
        backButton.setOnClickListener {
                vibrateDevice()
                onBackPressed()

        }
        val ilOptions = resources.getStringArray(R.array.LanguageOptions)
        //val ilOptions = arrayOf("English", "Spanish", "French", "German", "Portuguese", "Italian", "Polish", "Romanian")

        val inputAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ilOptions)
        ilSpinner.adapter = inputAdapter
        ilPreferences = getSharedPreferences("ilPreferences", MODE_PRIVATE)

        selectedil = ilPreferences.getString("Selectedil", "").toString()
        ilSpinner.setSelection(ilOptions.indexOf(selectedil))

        ilSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedil = parent.getItemAtPosition(position).toString()
                val ileditor = ilPreferences.edit()
                ileditor.putString("Selectedil", selectedil)
                ileditor.apply()
                vibrateDevice()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        val olOptions = resources.getStringArray(R.array.LanguageOptions2)
        //val olOptions =  arrayOf("English", "Spanish", "French", "German", "Portuguese", "Italian", "Polish", "Romanian", "Japanese", "Mandarin", "Russian" )
        val outputAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, olOptions)
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        olSpinner.adapter = outputAdapter
        olPreferences = getSharedPreferences("olPreferences", MODE_PRIVATE)

        selectedol = olPreferences.getString("Selectedol", "").toString()
        olSpinner.setSelection(olOptions.indexOf(selectedol))


        olSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedol = parent.getItemAtPosition(position).toString()
                val oleditor = olPreferences.edit()
                oleditor.putString("Selectedol", selectedol)
                oleditor.apply()
                vibrateDevice()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }




        val switchButton = findViewById<Switch>(R.id.switcher)
        val pauseSpeakPreferences = getSharedPreferences("pauseSpeakPrefs", MODE_PRIVATE)
        val switchChecker = pauseSpeakPreferences.getBoolean("switched", false)
        switchButton.isChecked = switchChecker

        switchButton.setOnCheckedChangeListener { _, isChecked ->
            val pauseSpeakeditor = pauseSpeakPreferences.edit()
            if (isChecked) {
                pauseSpeakeditor.putBoolean("switched", true)
                Toast.makeText(this, "Pausing Feature Enabled", Toast.LENGTH_SHORT).show()
                vibrateDevice()
            } else {
                pauseSpeakeditor.remove("switched")
                Toast.makeText(this, "Pausing Feature Disabled", Toast.LENGTH_SHORT).show()
            }
            pauseSpeakeditor.apply()
        }

        //Dark/light mode code
        val lightdarkbutton = findViewById<Switch>(R.id.switch_dark_mode)
        val nightModeRetriever = AppCompatDelegate.getDefaultNightMode()
        val nightModeChecker = when(nightModeRetriever){
            AppCompatDelegate.MODE_NIGHT_YES -> true
            else -> false
        }
        lightdarkbutton.isChecked = nightModeChecker

        lightdarkbutton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                Toast.makeText(this, "Light Mode Enabled", Toast.LENGTH_SHORT).show()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            }

            val darkModePreferences = getSharedPreferences("darkModePrefs", MODE_PRIVATE)
            val darkModeEditor = darkModePreferences.edit()
            darkModeEditor.putBoolean("isNightModeEnabled", isChecked)
            darkModeEditor.apply()
        }


    }

}
