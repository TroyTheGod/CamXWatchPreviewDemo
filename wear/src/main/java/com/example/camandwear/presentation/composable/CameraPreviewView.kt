package com.example.camandwear.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.camandwear.presentation.composable.component.CameraPreview
import com.example.camandwear.presentation.theme.CamAndWearTheme

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


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}