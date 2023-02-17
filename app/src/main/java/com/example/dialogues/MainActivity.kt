package com.example.dialogues

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.example.dialogues.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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
            if (isAllPermissionsGranted) {
                startCamera()
            } else {
                Snackbar.make(binding.previewView, "Camera permission not granted. \nCannot perform magic ritual.", Snackbar.LENGTH_LONG).setAction("Retry") {
                    requestPermissions()
                }.show()
                Log.e(TAG, "Camera permission not granted")
            }
        }
    }

    private val cameraAdapter = CameraAdapter {
        Log.d(TAG, "Text Found: $it")
    }

    private fun startCamera() = cameraAdapter.startCamera(this, this, binding.previewView.createSurfaceProvider())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val image: InputImage
//        pull image from resources
        val resourceId = R.drawable.ocr_test
        val uri = Uri.parse("android.resource://${packageName}/${resourceId}")
        try {
            image = InputImage.fromFilePath(this, uri)
//            call the OCR activity
            TextRecognizer(::textfound).recognizeImageText(image, 0, ::resulttext)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

//    OCR Callback functions
//    Passes detected text into this one
    private fun textfound(s: String) {
        Log.d(TAG, s)
    }

//    passes boolean indicating successful text detection
    private fun resulttext(b: Boolean) {
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraAdapter.shutdown()
    }
}