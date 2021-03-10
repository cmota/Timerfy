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
package com.example.androiddevchallenge.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.R

val RobotoFontFamily = FontFamily(
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_black, FontWeight.Black),
    Font(R.font.roboto_regular)
)

// Set of Material typography styles to start with
val typography = Typography(
    h1 = TextStyle(
        color = primaryColor,
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 200.sp,
        letterSpacing = (-14).sp,
    ),

    h2 = TextStyle(
        color = secondaryColor,
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 200.sp,
        letterSpacing = (-14).sp
    ),

    h3 = TextStyle(
        color = primaryColorVariant,
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 200.sp,
        letterSpacing = (-14).sp
    ),

    body1 = TextStyle(
        color = textColor,
        fontFamily = RobotoFontFamily,
        fontSize = 14.sp,
    ),

    body2 = TextStyle(
        color = textColor,
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp,
    ),

    caption = TextStyle(
        color = secondaryColor,
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 100.sp,
        letterSpacing = (-7).sp
    )
)
