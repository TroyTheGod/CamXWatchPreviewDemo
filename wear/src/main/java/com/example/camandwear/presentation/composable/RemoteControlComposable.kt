package com.example.camandwear.presentation.composable

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.camandwear.presentation.viewmodel.MainActivityViewModel
import kotlinx.coroutines.coroutineScope

@Composable
fun CameraButtonList() {
    val mainActivityViewModel: MainActivityViewModel = viewModel()
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    if (isPressed) {
        DisposableEffect(Unit) {
            onDispose {
                mainActivityViewModel.sendDataToPhone(context, "44")
            }
        }
    }

    Box(
        Modifier.fillMaxSize()
    ) {
        ScalingLazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Button(modifier = Modifier
                    .align(Alignment.Center)
                    .height(38.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            mainActivityViewModel.sendDataToPhone(context, "00")
                            val released = try {
                                tryAwaitRelease()
                            } catch (e: Exception) {
                                false
                            }
                            mainActivityViewModel.sendDataToPhone(context, "44")
                        })
                    }, onClick = {}) {
                    Text("Up")
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .height(38.dp)
                ) {
                    Button(modifier = Modifier
                        .padding(end = 17.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = {
                                mainActivityViewModel.sendDataToPhone(context, "11")
                                val released = try {
                                    tryAwaitRelease()
                                } catch (e: Exception) {
                                    false
                                }
                                mainActivityViewModel.sendDataToPhone(context, "44")
                            })
                        }, onClick = {}) {
                        Text("left")
                    }
                    Button(modifier = Modifier
                        .padding(start = 17.dp)
                        .height(38.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = {
                                mainActivityViewModel.sendDataToPhone(context, "22")
                                val released = try {
                                    tryAwaitRelease()
                                } catch (e: Exception) {
                                    false
                                }
                                mainActivityViewModel.sendDataToPhone(context, "44")
                            })
                        }, onClick = {}) {
                        Text("right")
                    }
                }
            }


            item {
                Button(modifier = Modifier
                    .align(Alignment.Center)
                    .height(38.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            mainActivityViewModel.sendDataToPhone(context, "33")
                            val released = try {
                                tryAwaitRelease()
                            } catch (e: Exception) {
                                false
                            }
                            mainActivityViewModel.sendDataToPhone(context, "44")
                        })
                    }, onClick = {}) {
                    Text("down")
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