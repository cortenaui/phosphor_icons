/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 * Copyright (C) 2026-present The CortenaOS Project
 */
package framework.cortena.icons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import framework.cortena.icons.resources.Res
import framework.cortena.icons.resources.phosphor_regular
import org.jetbrains.compose.resources.Font

/**
 * Creates a renderer that draws a single Phosphor glyph using the bundled Phosphor font.
 *
 * The returned composable lambda is designed to plug directly into CortenaUI's
 * `framework.cortena.ui.components.Icon` overload that accepts a renderer:
 *
 * `Icon(PhosphorIcon(codeId = PhosphorIcons.Alarm), contentDescription = null)`
 *
 * This keeps the icon pack lightweight while still allowing CortenaUI to own tint, size,
 * accessibility, and enabled-state behavior.
 */
fun PhosphorIcon(
    codeId: String,
): @Composable
(modifier: Modifier, tint: Color, size: Dp, enabled: Boolean, contentDescription: String?) -> Unit =
    { modifier, tint, size, enabled, _ ->
        val fontFamily = FontFamily(Font(Res.font.phosphor_regular))
        Box(
            modifier = modifier.size(size).alpha(if (enabled) 1f else 0.38f),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = codeId,
                style =
                    TextStyle(
                        color = tint,
                        fontFamily = fontFamily,
                        fontSize = size.value.sp,
                        lineHeight = size.value.sp,
                        textAlign = TextAlign.Center,
                    ),
            )
        }
    }
