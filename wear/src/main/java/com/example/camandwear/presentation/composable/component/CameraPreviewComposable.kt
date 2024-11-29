package com.example.camandwear.presentation.composable.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Text
import com.example.camandwear.presentation.viewmodel.MainActivityViewModel

@Composable
fun CameraPreview() {
    val mainActivityViewModel: MainActivityViewModel = viewModel()
    val context = LocalContext.current
    val previewBitmap by mainActivityViewModel.previewBitmap.observeAsState()

    LaunchedEffect(Unit) {
        mainActivityViewModel.addOnFrameCallback(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(16 / 9f)
    ) {
        previewBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: run {
            Text(
                text = "No frame available",
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color(255, 255, 255, 1))
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun Preview(){
    CameraPreview()
}