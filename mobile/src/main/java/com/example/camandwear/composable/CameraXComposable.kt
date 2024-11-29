package com.example.camandwear.composable

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.wearable.Wearable
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraX() {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()

        // 添加 ImageAnalysis 用于帧捕获
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        var frameCounter = 0L
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            frameCounter++
            if (frameCounter % 3 == 0L) {
                val frameData = compressFrame(imageProxy, 177, 100)
                sendFrameToWear(context, frameData)
            }
            imageProxy.close()
        }

        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageAnalysis)
        preview.surfaceProvider = previewView.surfaceProvider

        Wearable.getMessageClient(context).addListener {
            Log.e("From Watch", "on Data")
            if (it.path == "/remote") {
                val remoteCommand = it.data.toString(Charsets.UTF_8)
                Log.e("remote", "command: $remoteCommand")
            }
        }
    }
    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
}

fun sendFrameToWear(context: Context, frameData: ByteArray) {
    Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener {
        it.forEach {
            Wearable.getMessageClient(context)
                .sendMessage(it.id, "/camera_frame", frameData)
                .addOnSuccessListener {
                    Log.e("toWear", "data sent ${frameData.size}")

                }
                .addOnFailureListener {
                    Log.e("toWear", "data fail ${frameData.size}")
                }
        }
    }
}

private fun compressFrame(imageProxy: ImageProxy, targetWidth: Int, targetHeight: Int): ByteArray {
    val outputStream = ByteArrayOutputStream()

    try {
        val format = imageProxy.format
        if (format != ImageFormat.YUV_420_888) {
            Log.e("CompressFrameError", "Unsupported image format: $format")
            return ByteArray(0)
        }

        // 获取 YUV_420_888 数据缓冲区
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        // 将 YUV_420_888 转换为 NV21 格式
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        // 创建 YuvImage 并设置压缩目标区域
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)

        // 调整到目标分辨率
        val rect = Rect(0, 0, imageProxy.width, imageProxy.height)
        yuvImage.compressToJpeg(rect, 30, outputStream)

        // 解码为 Bitmap 进行缩放
        val jpegData = outputStream.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

        // 再次编码为 JPEG
        outputStream.reset()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return outputStream.toByteArray()
    } catch (e: Exception) {
        Log.e("CompressFrameError", "Failed to compress frame: ${e.message}")
        return ByteArray(0)
    } finally {
        outputStream.close()
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun Preview() {
    CameraX()
}