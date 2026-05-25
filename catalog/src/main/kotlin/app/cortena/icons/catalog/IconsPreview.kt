/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 * Copyright (C) 2026-present The CortenaOS Project
 */
package app.cortena.icons.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import framework.cortena.icons.PhosphorIcon
import framework.cortena.ui.components.Text
import framework.cortena.ui.components.TextRole
import framework.cortena.ui.layout.SafeArea
import framework.cortena.ui.layout.ScrollView
import framework.cortena.ui.shape.RoundedShape
import framework.cortena.ui.theme.LocalColors

/**
 * Catalog screen that lists every Phosphor icon shipped with the library in a fixed 3-column grid.
 */
@Composable
fun IconsPreview() {
    val colors = LocalColors.current
    ScrollView {
        SafeArea(horizontal = 12.dp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Phosphor Icons",
                    color = Color(colors.primary),
                    role = TextRole.TitleLarge,
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    PhosphorIconCatalog.chunked(3).forEach { rowEntries ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            rowEntries.forEach { entry ->
                                Column(modifier = Modifier.weight(1f)) {
                                    IconItem(name = entry.name, code = entry.code)
                                }
                            }
                            // Pad the last row so the trailing cells keep equal width.
                            repeat(3 - rowEntries.size) { Spacer(modifier = Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IconItem(name: String, code: String) {
    val colors = LocalColors.current
    val render = PhosphorIcon(codeId = code)
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .height(120.dp)
                .background(Color(colors.surfaceVariant), RoundedShape(24.dp))
                .padding(horizontal = 8.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        render(Modifier, Color(colors.onSurfaceVariant), 48.dp, true, name)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = name, color = Color(colors.onSurfaceVariant), role = TextRole.BodySmall)
    }
}
