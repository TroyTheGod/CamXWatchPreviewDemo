package com.example.camandwear.presentation.composable.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class JoystickDirection {
    Up, Down, Left, Right, Release
}

@Composable
fun JoystickView(outerRadius: Float, onDirectionChange: (JoystickDirection) -> Unit) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        Modifier
            .width(outerRadius.dp)
            .height(outerRadius.dp)
    ) {
        Canvas(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume() // 消耗手势事件
                            val newOffset = dragOffset + dragAmount

                            if (newOffset.getDistance() <= size.width / 2 - size.width / 6) {
                                dragOffset = newOffset

                                // 判断滑动方向
                                val xAbs = kotlin.math.abs(dragOffset.x)
                                val yAbs = kotlin.math.abs(dragOffset.y)

                                val direction = when {
                                    xAbs > yAbs && dragOffset.x > 0 -> JoystickDirection.Right
                                    xAbs > yAbs && dragOffset.x < 0 -> JoystickDirection.Left
                                    yAbs > xAbs && dragOffset.y > 0 -> JoystickDirection.Down
                                    yAbs > xAbs && dragOffset.y < 0 -> JoystickDirection.Up
                                    else -> null
                                }

                                direction?.let { onDirectionChange(it) }
                            }
                        },
                        onDragEnd = {
                            // 手势结束时将圆点恢复到中心
                            dragOffset = Offset.Zero
                            onDirectionChange(JoystickDirection.Release)
                        },
                        onDragCancel = {
                            // 手势结束时将圆点恢复到中心
                            dragOffset = Offset.Zero
                            onDirectionChange(JoystickDirection.Release)
                        }
                    )
                }
        ) {
            drawJoystick(dragOffset)
        }
    }
}

private fun DrawScope.drawJoystick(
    dragOffset: Offset,
) {
    drawCircle(
        color = Color.Gray,
        radius = size.width / 2,
        center = Offset(size.width / 2, size.height / 2)
    )

    drawCircle(
        color = Color.LightGray,
        radius = size.width / 6,
        center = Offset(size.width / 2, size.height / 2) + dragOffset
    )
}

@Preview
@Composable
private fun preview() {
    JoystickView(160f, {})
}