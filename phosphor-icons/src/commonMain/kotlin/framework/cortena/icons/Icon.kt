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
import framework.cortena.icons.resources.phosphor_bold
import framework.cortena.icons.resources.phosphor_fill
import framework.cortena.icons.resources.phosphor_light
import framework.cortena.icons.resources.phosphor_regular
import framework.cortena.icons.resources.phosphor_thin
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource

/**
 * Creates a renderer that draws a single Phosphor glyph using the bundled Phosphor font.
 *
 * The returned composable lambda is designed to plug directly into CortenaUI's
 * `framework.cortena.ui.components.Icon` overload that accepts a renderer:
 *
 * `Icon(PhosphorIcon(PhosphorIcons.Bold.Alarm), contentDescription = null)`
 *
 * The glyph carries its own font weight, so callers never pass a [FontFamily]. This keeps the icon
 * pack lightweight while still allowing CortenaUI to own tint, size, accessibility, and
 * enabled-state behavior.
 */
fun PhosphorIcon(
    glyph: PhosphorGlyph
): @Composable
(modifier: Modifier, tint: Color, size: Dp, enabled: Boolean, contentDescription: String?) -> Unit =
    { modifier, tint, size, enabled, _ ->
        val fontFamily = FontFamily(Font(fontResourceFor(glyph.weight)))
        Box(
            modifier = modifier.size(size).alpha(if (enabled) 1f else 0.38f),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = glyph.code,
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

/** Maps a [PhosphorIconWeight] to its bundled font resource. */
private fun fontResourceFor(weight: PhosphorIconWeight): FontResource =
    when (weight) {
        PhosphorIconWeight.Regular -> Res.font.phosphor_regular
        PhosphorIconWeight.Bold -> Res.font.phosphor_bold
        PhosphorIconWeight.Light -> Res.font.phosphor_light
        PhosphorIconWeight.Fill -> Res.font.phosphor_fill
        PhosphorIconWeight.Thin -> Res.font.phosphor_thin
    }
