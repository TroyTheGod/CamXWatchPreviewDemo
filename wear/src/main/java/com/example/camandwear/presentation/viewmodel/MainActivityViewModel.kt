package com.example.camandwear.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.camandwear.presentation.composable.WearApp
import com.example.camandwear.presentation.composable.component.JoystickDirection
import com.google.android.gms.wearable.Wearable

class MainActivityViewModel : ViewModel() {
    private val _previewBitmap = MutableLiveData<Bitmap?>(null)
    val previewBitmap: LiveData<Bitmap?> get() = _previewBitmap

    fun setPreviewBitmap(bitmap: Bitmap?) {
        _previewBitmap.postValue(bitmap)
    }

    private var lastRemoteCommand = JoystickDirection.Release


    fun addOnFrameCallback(context: Context) {
        Wearable.getMessageClient(context).addListener {
            Log.e("From Phone", "on Data")
            if (it.path == "/camera_frame") {
                val byteArray = it.data
                byteArray.let {
                    Log.e("WearOS", "Received data size: ${it.size}")
                    val bitmap = decodeJpegToBitmap(it)
                    setPreviewBitmap(bitmap)
                }
            }
        }
    }

    /***
     * command:
     *
     * one time click: 0up 1left 2right 3down
     *
     * long press: 00up 11left 22right 33down 44 longPressCancel
     *
     * other command: 4take photo 5record
     */
    fun sendDataToPhone(context: Context, command: String) {
        Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener {
            for (node in it) {
                Wearable.getMessageClient(context)
                    .sendMessage(node.id, "/remote", command.toByteArray(Charsets.UTF_8))
                    .addOnSuccessListener {
                        Log.e("sendDataToPhone", "/remote: $command + ${command.toByteArray()} ")
                    }
            }
        }
    }


    private fun decodeJpegToBitmap(jpegData: ByteArray): Bitmap? {
        return try {
            // 将 JPEG 数据解码为 Bitmap
            BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null // 如果解码失败，返回 null
        }
    }

    fun parseJoystickData(context: Context, direction: JoystickDirection) {
        if (direction == lastRemoteCommand) {
            return
        }
        when (direction) {
            JoystickDirection.Up -> {
                lastRemoteCommand = JoystickDirection.Up
                sendDataToPhone(context, "00")
            }

            JoystickDirection.Down -> {
                lastRemoteCommand = JoystickDirection.Down
                sendDataToPhone(context, "33")
            }

            JoystickDirection.Left -> {
                lastRemoteCommand = JoystickDirection.Left
                sendDataToPhone(context, "11")
            }

            JoystickDirection.Right -> {
                lastRemoteCommand = JoystickDirection.Right
                sendDataToPhone(context, "22")
            }

            JoystickDirection.Release -> {
                lastRemoteCommand = JoystickDirection.Release
                sendDataToPhone(context, "44")
            }
        }
    }
}