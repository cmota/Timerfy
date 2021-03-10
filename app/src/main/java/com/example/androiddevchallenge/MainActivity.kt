/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.backgroundColor
import com.example.androiddevchallenge.ui.theme.backgroundFillerColor
import com.example.androiddevchallenge.ui.theme.backgroundFinishColor
import com.example.androiddevchallenge.ui.theme.backgroundSelectedColor
import com.example.androiddevchallenge.ui.theme.borderColor
import com.example.androiddevchallenge.ui.theme.primaryColor
import com.example.androiddevchallenge.ui.theme.textColor
import com.example.androiddevchallenge.ui.theme.typography
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"
private const val THRESHOLD = 5000L

private lateinit var statusBarColor: MutableState<Int>

private lateinit var elapsedTime: MutableState<Long>
private lateinit var selectedTime: MutableState<Long>
private lateinit var started: MutableState<Boolean>

private lateinit var countDownTimer: CountDownTimer

private lateinit var customTypeface: Typeface

class MainActivity : AppCompatActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            statusBarColor = remember { mutableStateOf(backgroundColor.toArgb()) }

            MyTheme(window = window, statusBarColor = statusBarColor.value) {

                val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                    bottomSheetState = rememberBottomSheetState(
                        initialValue = BottomSheetValue.Collapsed
                    )
                )

                val times = integerArrayResource(id = R.array.timer)
                selectedTime = remember { mutableStateOf(times[0].toLong()) }

                started = remember { mutableStateOf(false) }

                BottomSheetScaffold(
                    sheetContent = {
                        AddTimer(selectTimer = bottomSheetScaffoldState)
                    },

                    scaffoldState = bottomSheetScaffoldState, sheetPeekHeight = 0.dp
                ) {

                    Scaffold(
                        floatingActionButton = {
                            AddFloatingActionButton()
                        },
                        floatingActionButtonPosition = FabPosition.Center,
                        content = {
                            MyApp()

                            AddTopBar(selectTimer = bottomSheetScaffoldState)
                        },
                    )
                }
            }
        }
    }
}

// Start building your app here!
@SuppressLint("NewApi")
@Composable
fun MyApp() {
    customTypeface = LocalContext.current.resources.getFont(R.font.roboto_black)

    Surface(modifier = Modifier.background(Color.Transparent)) {

        elapsedTime = remember { mutableStateOf(selectedTime.value) }

        statusBarColor.value = if (elapsedTime.value == 0L) {
            backgroundFinishColor.toArgb()
        } else {
            backgroundColor.toArgb()
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            val paint = Paint().apply {
                isAntiAlias = true
                textSize = 525f
                typeface = customTypeface
                letterSpacing = -0.075f
                style = Paint.Style.FILL
                textAlign = Paint.Align.CENTER
                color = primaryColor.toArgb()
            }
            drawIntoCanvas { canvas ->
                val text = String.format("%02d", 0L)
                val bounds = Rect()

                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
                paint.getTextBounds(text, 0, text.length, bounds)
                canvas.nativeCanvas.drawText(text, size.width / 2, size.height / 2 - size.width / 8, paint)

                val topOffset = (size.height * elapsedTime.value) / selectedTime.value
                Log.d(TAG, "size.height=${size.height} | progress=${elapsedTime.value} | offset=$topOffset")

                val background = if (elapsedTime.value < THRESHOLD) {
                    backgroundFinishColor
                } else {
                    backgroundFillerColor
                }

                paint.color = background.toArgb()
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
                canvas.nativeCanvas.drawRect(0f, topOffset, size.width, size.height, paint)

                paint.color = Color.White.toArgb()
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas.nativeCanvas.drawText(text, size.width / 2, size.height / 2 - size.width / 8, paint)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(color = Color.Transparent) {

                Text(
                    text = ":",
                    style = typography.caption,
                    modifier = Modifier.padding(start = 60.dp, top = 105.dp),
                )

                Text(
                    text = String.format("%02d", elapsedTime.value / 1000),
                    style = typography.h2,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AddTopBar(selectTimer: BottomSheetScaffoldState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Card(
            modifier = Modifier
                .height(35.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .clickable {
                    GlobalScope.launch {
                        selectTimer.bottomSheetState.expand()
                    }
                },
            backgroundColor = backgroundColor,

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = getFormattedTime(selectedTime.value),
                    style = typography.body1,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp)
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AddTimer(selectTimer: BottomSheetScaffoldState) {
    val times = integerArrayResource(id = R.array.timer)

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {

        itemsIndexed(times.toList()) { _, item ->
            AddSection(value = item.toLong(), selectTimer = selectTimer)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AddSection(value: Long, selectTimer: BottomSheetScaffoldState) {

    val backgroundColor = if (value == selectedTime.value) {
        backgroundSelectedColor
    } else {
        backgroundColor
    }

    val textColor = if (value == selectedTime.value) {
        backgroundFillerColor
    } else {
        textColor
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(backgroundColor)
            .clickable {
                GlobalScope.launch {
                    elapsedTime.value = value
                    selectedTime.value = value

                    selectTimer.bottomSheetState.collapse()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = getFormattedTime(value),
            style = typography.body2,
            color = textColor
        )
    }
}

@Composable
fun AddFloatingActionButton() {
    FloatingActionButton(
        onClick = {
            if (started.value) {
                cancelTimer()
            } else {
                startTimer()
            }

            started.value = !started.value
        }
    ) {
        val icon = if (started.value) {
            painterResource(R.drawable.ic_pause)
        } else {
            painterResource(R.drawable.ic_play)
        }
        val description = stringResource(id = R.string.description_play_pause)

        Icon(
            painter = icon,
            contentDescription = description
        )
    }
}

private fun getMinutes(time: Long) = (time / 1000) / 60

private fun getSeconds(time: Long) = ((time / 1000) % 60)

private fun getFormattedTime(time: Long): String {
    val minutes = getMinutes(time)
    val seconds = getSeconds(time)

    return if (seconds == 0L) {
        "$minutes min"
    } else {
        "$minutes:$seconds min"
    }
}

private fun startTimer() {
    countDownTimer = object : CountDownTimer(elapsedTime.value, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            Log.d(TAG, "onTick | millisUntilFinished=$millisUntilFinished")
            elapsedTime.value = millisUntilFinished
        }

        override fun onFinish() {
            Log.d(TAG, "onTick | Finished!")
            elapsedTime.value = 0
        }
    }.start()
}

private fun cancelTimer() {
    if (!started.value) {
        return
    }

    countDownTimer.cancel()
}
