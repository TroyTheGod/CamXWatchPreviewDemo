package com.example.camandwear.presentation.composable

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.camandwear.presentation.theme.CamAndWearTheme
import com.example.camandwear.presentation.viewmodel.MainActivityViewModel

@Composable
fun WearApp() {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val pageIndicatorState = remember {
        object : PageIndicatorState {
            override val pageCount: Int
                get() = pagerState.pageCount
            override val pageOffset: Float
                get() = pagerState.currentPageOffsetFraction
            override val selectedPage: Int
                get() = pagerState.currentPage

        }
    }

    CamAndWearTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
        ) {
            TimeText()
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                HorizontalPager(state = pagerState) {
                    when (it) {
                        0 -> {
                            CameraPreview()
                        }

                        1 -> {
                            CameraButtonList()
                        }
                    }
                }
            }
            HorizontalPageIndicator(
                pageIndicatorState = pageIndicatorState
            )

        }
    }
}

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
                    .fillMaxWidth()
                    .align(Alignment.CenterEnd)
                    .background(Color(255, 255, 255, 1))
            )
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}