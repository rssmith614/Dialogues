package com.example.dialogues

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class TranslationScreen : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sourceText = findViewById<TextView>(R.id.ts_textView)
        val targetText = findViewById<TextView>(R.id.ts_textView2)

        val sourceSpinner = findViewById<Spinner>(R.id.ts_spinner)
        val targetSpinner = findViewById<Spinner>(R.id.ts_spinner2)

        val sourceOptions = arrayOf("English", "Spanish", "French", "German", "Hindi", "Chinese", "Japanese", "Arabic")
        val sourceSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sourceOptions)

        sourceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sourceSpinner.adapter = sourceSpinnerAdapter
        sourceSpinner.onItemSelectedListener = this


    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //Spinner stuff
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {}
}