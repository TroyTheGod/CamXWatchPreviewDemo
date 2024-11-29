package com.example.camandwear.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.camandwear.presentation.composable.component.JoystickView
import com.example.camandwear.presentation.viewmodel.MainActivityViewModel

@Composable
fun CameraButtonList() {
    val mainActivityViewModel: MainActivityViewModel = viewModel()
    val context = LocalContext.current
    val listState = rememberScalingLazyListState()

    LaunchedEffect(Unit) {
        listState.scrollToItem(0)
    }

    Box(
        Modifier.fillMaxSize()
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState // 绑定滚动状态
        ) {
            item {
                Box(
                    Modifier
                        .padding(start = 30.dp, end = 30.dp, top = 25.dp)
                ) {
                    JoystickView(160f, onDirectionChange = {
                        mainActivityViewModel.parseJoystickData(context, it)
                    })
                }
            }

            item {
                Row {
                    Button(modifier = Modifier.weight(weight = 1f), onClick = {
                        mainActivityViewModel.sendDataToPhone(context, "4")
                    }) {
                        Text("拍照")
                    }
                    Button(modifier = Modifier.weight(weight = 1f), onClick = {
                        mainActivityViewModel.sendDataToPhone(context, "5")
                    }) {
                        Text("开始录制")
                    }
                }
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun Preview() {
    CameraButtonList()
}