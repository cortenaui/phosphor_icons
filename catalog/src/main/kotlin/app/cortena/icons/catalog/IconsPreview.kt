/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 * Copyright (C) 2026-present The CortenaOS Project
 */
package app.cortena.icons.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import framework.cortena.icons.PhosphorGlyph
import framework.cortena.icons.PhosphorIcon
import framework.cortena.icons.PhosphorIconWeight
import framework.cortena.icons.PhosphorIcons
import framework.cortena.ui.components.Button
import framework.cortena.ui.components.ButtonStyle
import framework.cortena.ui.components.ButtonVariant
import framework.cortena.ui.components.Separator
import framework.cortena.ui.components.Slider
import framework.cortena.ui.components.Text
import framework.cortena.ui.components.TextRole
import framework.cortena.ui.geometry.Orientation
import framework.cortena.ui.layout.GridColumns
import framework.cortena.ui.layout.LazyGridView
import framework.cortena.ui.layout.SafeArea
import framework.cortena.ui.layout.ScrollView
import framework.cortena.ui.shape.RoundedShape
import framework.cortena.ui.size.SizeToken
import framework.cortena.ui.theme.LocalColors
import framework.cortena.ui.typography.TextWeight

/**
 * Main catalog screen that displays all Phosphor icons with search, weight filtering, and size
 * adjustment � all built with CortenaUI components.
 */
@Composable
fun IconCatalogScreen() {
    // Selected weight filter. null means show all weights.
    var selectedWeight by rememberSaveable {
        mutableStateOf<PhosphorIconWeight?>(PhosphorIconWeight.Regular)
    }

    // Icon display size controlled by the slider.
    var iconSize by rememberSaveable { mutableFloatStateOf(42f) }

    // Derive the filtered icon list from the catalog.
    val filteredIcons by
        remember(selectedWeight) {
            derivedStateOf {
                PhosphorIconCatalog.filter { entry ->
                    val matchesWeight = selectedWeight == null || entry.weight == selectedWeight
                    matchesWeight
                }
            }
        }

    Column(modifier = Modifier.fillMaxSize()) {
        SafeArea {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                WeightFilterRow(
                    selectedWeight = selectedWeight,
                    onWeightSelected = { selectedWeight = it },
                )
                Separator()
                // Size slider
                ControlsRow(
                    size = iconSize,
                    iconSize = { iconSize },
                    onIconSizeChange = { iconSize = it },
                )
                Separator()
            }
        }
        // Icon grid
        IconGrid(entries = filteredIcons, iconSize = iconSize.dp)
    }
}

private val WeightOptions: List<Pair<String, PhosphorIconWeight?>> =
    listOf(
        "Regular" to PhosphorIconWeight.Regular,
        "Bold" to PhosphorIconWeight.Bold,
        "Light" to PhosphorIconWeight.Light,
        "Fill" to PhosphorIconWeight.Fill,
        "Thin" to PhosphorIconWeight.Thin,
    )

@Composable
private fun WeightFilterRow(
    selectedWeight: PhosphorIconWeight?,
    onWeightSelected: (PhosphorIconWeight?) -> Unit,
) {
    ScrollView(orientation = Orientation.Horizontal, showScrollIndicator = false) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeightOptions.forEach { (label, weight) ->
                val isSelected = selectedWeight == weight
                Button(
                    onClick = { onWeightSelected(weight) },
                    size = SizeToken.Small,
                    style = if (isSelected) ButtonStyle.Primary else ButtonStyle.Ghost,
                    variant = if (isSelected) ButtonVariant.Default else ButtonVariant.Soft,
                ) {
                    Text(label)
                }
            }
        }
    }
}

@Composable
private fun ControlsRow(size: Float, iconSize: () -> Float, onIconSizeChange: (Float) -> Unit) {
    val colors = LocalColors.current
    SafeArea {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Size slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                val textAaIcon = PhosphorIcon(glyph = PhosphorIcons.Regular.TextAa)
                textAaIcon(Modifier, Color(colors.onSurfaceVariant), 16.dp, true, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Size",
                    role = TextRole.BodySmall,
                    weight = TextWeight.Medium,
                    color = Color(colors.onSurfaceVariant),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    Slider(
                        value = iconSize,
                        onValueChange = onIconSizeChange,
                        valueRange = 24f..64f,
                        size = SizeToken.Small,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${size.toInt()}dp",
                    role = TextRole.BodySmall,
                    color = Color(colors.onSurfaceVariant).copy(alpha = 0.6f),
                )
            }
        }
    }
}

@Composable
private fun IconGrid(entries: List<PhosphorIconEntry>, iconSize: Dp) {
    LazyGridView(
        columns = GridColumns.Adaptive(minSize = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(entries.size) { index ->
            val entry = entries[index]
            IconCell(name = entry.name, glyph = entry.glyph, iconSize = iconSize)
        }
    }
}

@Composable
private fun IconCell(name: String, glyph: PhosphorGlyph, iconSize: Dp) {
    val colors = LocalColors.current
    val render = PhosphorIcon(glyph = glyph)
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedShape(24.dp))
                .background(Color(colors.surfaceVariant))
                .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        render(Modifier, Color(colors.onSurfaceVariant), iconSize, true, name)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            color = Color(colors.onSurfaceVariant).copy(alpha = 0.7f),
            role = TextRole.BodySmall,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
    }
}
