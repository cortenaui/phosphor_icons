/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 * Copyright (C) 2026-present The CortenaOS Project
 */
package app.cortena.icons.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import framework.cortena.icons.PhosphorIcon
import framework.cortena.icons.PhosphorIcons

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun App() {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F1EA))) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .background(Color(0xFF171717))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            BasicText(
                text = "Phosphor Icons Catalog",
                style =
                    TextStyle(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            IconItem(name = "Acorn", iconCode = PhosphorIcons.Acorn)
            IconItem(name = "Airplane", iconCode = PhosphorIcons.Airplane)
            IconItem(name = "Alarm", iconCode = PhosphorIcons.Alarm)
            IconItem(name = "Alien", iconCode = PhosphorIcons.Alien)
            IconItem(name = "Anchor", iconCode = PhosphorIcons.Anchor)
            IconItem(name = "AndroidLogo", iconCode = PhosphorIcons.AndroidLogo)
        }
    }
}

@Composable
fun IconItem(name: String, iconCode: String) {
    Column(
        modifier =
            Modifier.size(width = 132.dp, height = 128.dp)
                .background(Color.White, RoundedCornerShape(18.dp))
                .border(1.dp, Color(0xFFE2DED2), RoundedCornerShape(18.dp))
                .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        PhosphorIcon(
            iconCode = iconCode,
            contentDescription = name,
            size = 34.dp,
            tint = Color(0xFF171717),
        )
        Spacer(modifier = Modifier.weight(1f))
        BasicText(
            text = name,
            style = TextStyle(color = Color(0xFF3A3A3A), fontSize = 12.sp, lineHeight = 16.sp),
        )
    }
}
