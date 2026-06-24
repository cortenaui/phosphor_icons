# Usage

The library provides the `PhosphorIcons` object, containing all icons categorized by weight (Regular, Bold, Light, Fill, Thin).

## Basic Usage

The standard way to use these icons is to wrap them in the `PhosphorIcon` function, which creates an icon renderer compatible with CortenaUI's `Icon` component.

```kotlin
import framework.cortena.ui.components.Icon
import framework.cortena.icons.PhosphorIcon
import framework.cortena.icons.PhosphorIcons

// Inside your Composable:
Icon(
    renderer = PhosphorIcon(PhosphorIcons.Regular.House),
    contentDescription = "Home"
)
```

## Icon Weights

Every icon is available in 6 weights. You can access them through the respective object under `PhosphorIcons`:

```kotlin
// Regular
PhosphorIcon(PhosphorIcons.Regular.Heart)

// Bold
PhosphorIcon(PhosphorIcons.Bold.Heart)

// Light
PhosphorIcon(PhosphorIcons.Light.Heart)

// Fill
PhosphorIcon(PhosphorIcons.Fill.Heart)

// Thin
PhosphorIcon(PhosphorIcons.Thin.Heart)

// Duotone
PhosphorIcon(PhosphorIcons.Duotone.Heart)
```

## How it Works

Instead of shipping thousands of individual vector files, this library bundles Phosphor's TrueType fonts. The `PhosphorIcon` function creates a text-based layout that renders the exact Unicode glyph associated with the requested icon.

This ensures:

1. Fast rendering performance.
2. Perfect scaling.
3. Minimal binary size impact compared to traditional XML/SVG vectors.
