package com.example.dialogues

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dialogues.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

//    placeholder for image taken by camera
    private val resourceId = R.drawable.stc_sign

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

//    private fun startCamera() = cameraAdapter.startCamera(this, this, binding.previewView.createSurfaceProvider())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val inputUri = "android.resource://${packageName}/${resourceId}"
//        Assign button to start OCR activity, passing the "image" to it
        findViewById<Button>(R.id.button).setOnClickListener {
            val intent = Intent(this, OCRConfirmation::class.java).putExtra("Image", inputUri)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraAdapter.shutdown()
    }
}