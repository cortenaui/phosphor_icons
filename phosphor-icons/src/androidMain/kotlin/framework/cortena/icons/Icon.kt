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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Suppress("ModifierParameter")
@Composable
fun PhosphorIcon(
    iconId: String,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color.Unspecified,
) {
    val semanticsModifier =
        if (contentDescription != null) {
            Modifier.semantics { this.contentDescription = contentDescription }
        } else {
            Modifier
        }

    Box(
        modifier = modifier.then(semanticsModifier).size(size),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = iconId,
            style =
                TextStyle(
                    color = if (tint.isSpecified) tint else Color.Black,
                    fontFamily = PhosphorIcons.DefaultFontFamily,
                    fontSize = size.value.sp,
                    lineHeight = size.value.sp,
                    textAlign = TextAlign.Center,
                ),
        )
    }
}
