package com.rimir.linky.ui.qrcode

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_PROCESS_TEXT
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.CaptureActivity
import com.rimir.linky.R
import com.rimir.linky.ui.MainActivity
import com.rimir.linky.util.ACTION_CREATE_LINK
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QrCodeActivity : AppCompatActivity() {

    private val REQUEST_CAMERA_PERMISSION = 1
    var qrScan: IntentIntegrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            scanQRCode()
        }
    }

    // Handle camera permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanQRCode()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    // Start QR code scanner
    private fun scanQRCode() {
        qrScan = IntentIntegrator(this).setCaptureActivity(CaptureActivityPortrait::class.java)

        qrScan?.setOrientationLocked(false)
        qrScan?.initiateScan()
    }

    // Handle QR code scanner result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {

            } else {

                val intent = Intent(this, MainActivity::class.java)
                intent.action = ACTION_PROCESS_TEXT
                intent.putExtra("shared_link", result.contents.toString())
                startActivity(intent)

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
