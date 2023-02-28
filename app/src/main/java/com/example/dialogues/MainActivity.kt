package com.example.dialogues

import android.Manifest

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.android.material.snackbar.Snackbar
import com.example.dialogues.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import java.io.File
import java.io.IOException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

//    placeholder for image taken by camera
    // private val resourceId = R.drawable.stc_sign
    
    private var imageCapture:ImageCapture?=null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var outputDirectory: File

    private var URI = "hjkhkljhl"

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
                    var intent = Intent(this@MainActivity, ImageScreen::class.java)
                    intent.putExtra("imglocation", savedUri.toString())
                    startActivity(intent)

                    transportfunc(savedUri)

                }

                override fun onError(exception: ImageCaptureException) {

                    Log.e(TAG, "on Error: ${exception.message}",exception)
                }

            }
        )
    }

    fun transportfunc(link:Uri){
        URI = link.toString();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // my button


        if (isAllPermissionsGranted){
            startCamera()
        }
        else {
            requestPermissions()
        }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        var camera_Click = findViewById<Button>(R.id.camera_capture_button)

        camera_Click.setOnClickListener{
            var intent = Intent(this, ImageScreen::class.java)

            takePhoto()

            //Log.d(TAG, " code 234: $URI")



        }



//        val image: InputImage
////        pull image from resources
//        val resourceId = R.drawable.ocr_test
//        val uri = Uri.parse("android.resource://${packageName}/${resourceId}")
//        try {
//            image = InputImage.fromFilePath(this, uri)
////            call the OCR activity
//            TextRecognizer(::textfound).recognizeImageText(image, 0, ::resulttext)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}