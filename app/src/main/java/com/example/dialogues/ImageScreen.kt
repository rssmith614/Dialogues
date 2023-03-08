package com.example.dialogues

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView

class ImageScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_screen)

        var intent = getIntent();
        var imagePath = intent.getStringExtra("imglocation")
        var FilePath = Uri.parse(imagePath)

        Log.d(TAG,"here is the image path uri code24" + imagePath )

        var IV = findViewById<ImageView>(R.id.Pic)

        IV.setImageURI(FilePath)

    }
}