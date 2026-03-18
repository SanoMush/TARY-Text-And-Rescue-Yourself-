package com.sanomush.tari.helper

import android.content.Context
import android.hardware.camera2.CameraManager

object HardwareUtils {
    private var isFlashlightOn = false

    fun toggleFlashlight(context: Context): Boolean {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0]
            isFlashlightOn = !isFlashlightOn
            cameraManager.setTorchMode(cameraId, isFlashlightOn)
            isFlashlightOn
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}