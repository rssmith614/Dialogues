package com.example.dialogues

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dialogues.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val PreviewView.surfaceProvider: Preview.SurfaceProvider?
    get() {
        TODO("Not yet implemented")
    }

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    private var imageCapture:ImageCapture?=null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var outputDirectory: File


//    private var URI = ""

    private val isAllPermissionsGranted get() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val TAG = MainActivity::class.java.name
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private fun requestPermissions() = ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (isAllPermissionsGranted) {
//                startCamera()
//            } else {
//                Snackbar.make(binding.previewView, "Camera permission not granted. \nCannot perform magic ritual.", Snackbar.LENGTH_LONG).setAction("Retry") {
//                    requestPermissions()
//                }.show()
//                Log.e(TAG, "Camera permission not granted")
//            }
        }
    }

    private val cameraAdapter = CameraAdapter {
        Log.d(TAG, "Text Found: $it")
    }

    //private fun startCamera() = cameraAdapter.startCamera(this, this, binding.previewView.createSurfaceProvider())


    private fun getOutputDirectory(): File{
        val mediaDir = externalMediaDirs.first()?.let{ mFile ->
            File(mFile, resources.getString(R.string.app_name)).apply{
                mkdirs()
            }
        }
        return if(mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


    private fun startCamera() {
        findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        val photoFile = File(outputDirectory, SimpleDateFormat("yy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
            .format(System.currentTimeMillis()) + ".jpg")

        val outputOptions = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: OutputFileResults) {

                    val savedUri = Uri.fromFile(photoFile)

                    val msg = "Photo saved"
                    Log.d(TAG, "On success code23: " + savedUri.toString())
                    Toast.makeText(this@MainActivity, "$msg $savedUri", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this@MainActivity, OCRConfirmation::class.java)
                    intent.putExtra("imglocation", savedUri.toString())
                    intent.putExtra("Confirm", true)
                    startActivity(intent)
                    findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE

                    transportfunc(savedUri)

                }

                override fun onError(exception: ImageCaptureException) {

                    Log.e(TAG, "on Error: ${exception.message}",exception)
                }

            }
        )
    }

    fun transportfunc(link:Uri){
//        URI = link.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // my button


        if (isAllPermissionsGranted) {
            startCamera()
        } else {
            requestPermissions()
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

//        var switch1 = findViewById<Switch>(R.id.switch1)
//        var switch2 = findViewById<Switch>(R.id.switch2)
        //var switch3 = findViewById<Switch>(R.id.switch3)

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        fun vibrateTime() {
            val hapticFeedbackPreferences = getSharedPreferences("hfPrefs", Context.MODE_PRIVATE)
            val HapticFeedbackOn = hapticFeedbackPreferences.getBoolean("HapticFeedbackEnabled", false)
            if (HapticFeedbackOn) {
                vibrator.vibrate(50)
            }
        }
        var camera_Click = findViewById<Button>(R.id.camera_capture_button)
//        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
//            camera_Click.setBackgroundColor(resources.getColor(R.color.dark_mode_color))
//        } else {
//            camera_Click.setBackgroundColor(resources.getColor(R.color.notwhite))
//        }
        camera_Click.setOnClickListener {
//            var intent = Intent(this, ImageScreen::class.java)
            findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.VISIBLE
            takePhoto()
            vibrateTime()
            //Log.d(TAG, " code 234: $URI")

        }
        val ilPreferences = getSharedPreferences("ilPreferences", MODE_PRIVATE)
        val selectedil = ilPreferences.getString("Selectedil","").toString()
        val olPreferences = getSharedPreferences("olPreferences", MODE_PRIVATE)
        val selectedol = olPreferences.getString("Selectedol","").toString()


        val sharedPreferences = getSharedPreferences("VoicePreferences", MODE_PRIVATE)
        val selectedvoice = sharedPreferences.getString("SelectedVoice", "").toString()


        val pauseSpeakPreferences = getSharedPreferences("pauseSpeakPrefs", MODE_PRIVATE)
        val pauseSpeakPrefSwitch = pauseSpeakPreferences.getBoolean("switched", false)

        val darkModePreferences = getSharedPreferences("darkModePrefs", MODE_PRIVATE)
        val isNightModeEnabled = darkModePreferences.getBoolean("isNightModeEnabled", false)

        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val settingsbind = findViewById<Button>(R.id.settings_button)

        settingsbind.setOnClickListener {
            val intent2 = Intent(this, Settings::class.java)
            startActivity(intent2)

            //textView.setText("")
        }
    }
}
