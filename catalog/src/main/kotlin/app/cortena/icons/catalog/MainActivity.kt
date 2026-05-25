/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 * Copyright (C) 2026-present The CortenaOS Project
 */
package app.cortena.icons.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import framework.cortena.ui.layout.Body
import framework.cortena.ui.layout.ContentView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContentView { Body { IconsPreview() } }
    }
}
