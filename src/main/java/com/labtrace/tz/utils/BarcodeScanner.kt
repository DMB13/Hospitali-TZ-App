package com.labtrace.tz.utils

import android.app.Activity
import android.content.Intent
import com.google.zxing.integration.android.IntentIntegrator

class BarcodeScanner(private val activity: Activity) {

    fun initiateScan(): Intent {
        val integrator = IntentIntegrator(activity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan a barcode")
        integrator.setCameraId(0)  // Use a specific camera of the device
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        return integrator.createScanIntent()
    }

    fun parseScanResult(resultCode: Int, data: Intent?): String? {
        val result = IntentIntegrator.parseActivityResult(0, resultCode, data)
        return if (result != null) {
            if (result.contents == null) {
                "Cancelled"
            } else {
                result.contents
            }
        } else {
            null
        }
    }
}
