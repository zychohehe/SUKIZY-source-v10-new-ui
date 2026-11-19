/**
 * SUKIZY liquid glass layer for Metrolist (GPL-3.0). Original app (C) MetrolistGroup.
 * Blur and refraction come from Haze (dev.chrisbanes.haze); this file only wires it to the app theme.
 */

package com.metrolist.shared.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlass
import dev.chrisbanes.haze.hazeSource

/**
 * Pauses live blur while a list scrolls. The glass keeps its tint, so the look stays the same but frames stay cheap.
 * Lists set [scrolling] from their scroll state.
 */
object GlassBlurPolicy {
    private var scrollingState by mutableStateOf(false)

    /** Read by glass surfaces; true while a list is scrolling. */
    val scrolling: Boolean get() = scrollingState

    // Named differently from the property setter so the JVM signatures do not clash.
    fun updateScrolling(value: Boolean) {
        if (scrollingState != value) scrollingState = value
    }
}

/** Shared blur source. Null means no live blur is available and glass falls back to a denser tint. */
val LocalGlassHazeState = staticCompositionLocalOf<HazeState?> { null }

/** True when the platform can render the live blur (Android 12+). Provided by the host app. */
val LocalGlassBlurSupported = staticCompositionLocalOf { false }

/** Marks the content that glass surfaces blur, and paints a soft backdrop so the glass has color to pick up. */
@Composable
fun Modifier.glassSource(baseColor: Color): Modifier {
    val state = LocalGlassHazeState.current.takeUnless { GlassBlurPolicy.scrolling }
    // No live blur: a flat backdrop is all the glass needs, so skip the gradients drawn behind the whole screen.
    if (state == null) return this.drawBehind { drawRect(baseColor) }
    val scheme = MaterialTheme.colorScheme
    val primary = scheme.primary
    val tertiary = scheme.tertiary
    val dark = baseColor.luminance() < 0.5f
    // Gradients are built once per size instead of on every draw frame behind the whole screen.
    val backdrop =
        Modifier.drawWithCache {
            val strength = if (dark) 0.20f else 0.14f
            val primaryGlow =
                Brush.radialGradient(
                    listOf(primary.copy(alpha = strength), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.12f),
                    radius = size.maxDimension * 0.7f,
                )
            val tertiaryGlow =
                Brush.radialGradient(
                    listOf(tertiary.copy(alpha = strength * 0.8f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.85f),
                    radius = size.maxDimension * 0.65f,
                )
            onDrawBehind {
                drawRect(baseColor)
                if (baseColor.alpha > 0f) {
                    drawRect(primaryGlow)
                    drawRect(tertiaryGlow)
                }
            }
        }
    // The backdrop sits inside the source so the blur samples it too.
    return if (state != null) this.hazeSource(state).then(backdrop) else this.then(backdrop)
}

/**
 * Liquid glass look: live blur (when supported), tint, soft top-light gradient, rim highlight and shadow.
 *
 * @param accent optional color (for example from album art) mixed into the tint.
 * @param liveBlur set false for surfaces in their own window (dialogs, sheets) that cannot sample the app content.
 */
@Composable
fun Modifier.glassSurface(
    shape: Shape,
    accent: Color = Color.Unspecified,
    shadow: Dp = 12.dp,
    liveBlur: Boolean = true,
): Modifier {
    val scheme = MaterialTheme.colorScheme
    val dark = scheme.surface.luminance() < 0.5f
    val hazeState = LocalGlassHazeState.current
    val blurred = liveBlur && hazeState != null && LocalGlassBlurSupported.current && !GlassBlurPolicy.scrolling
    val base = scheme.surfaceContainerHigh
    val mixed = if (accent.isSpecified) lerp(base, accent, 0.30f) else base
    // Without live blur the tint must be dense enough to keep text readable on any content.
    val tintAlpha =
        when {
            blurred -> if (dark) 0.34f else 0.42f
            liveBlur -> if (dark) 0.90f else 0.93f
            else -> if (dark) 0.80f else 0.86f
        }
    if (!blurred) {
        // Flat tint, hairline border and a small shadow: far cheaper than gradients and a large blur shadow.
        val hairline = scheme.onSurface.copy(alpha = 0.08f)
        val flatShadow = if (shadow < 4.dp) shadow else 4.dp
        return this
            .shadow(flatShadow, shape, clip = false, ambientColor = Color.Black.copy(alpha = 0.25f), spotColor = Color.Black.copy(alpha = 0.25f))
            .background(mixed.copy(alpha = tintAlpha), shape)
            .border(1.dp, hairline, shape)
    }
    val topLight =
        Brush.verticalGradient(
            listOf(Color.White.copy(alpha = if (dark) 0.16f else 0.50f), Color.White.copy(alpha = 0.02f)),
        )
    val rimStart = Color.White.copy(alpha = if (dark) 0.50f else 0.90f)
    val rimEnd = (if (accent.isSpecified) accent else scheme.primary).copy(alpha = if (dark) 0.30f else 0.35f)
    val rim = Brush.linearGradient(listOf(rimStart, Color.White.copy(alpha = 0.06f), rimEnd))
    val shadowColor = Color.Black.copy(alpha = if (dark) 0.45f else 0.22f)

    val glassShape = shape
    val withBlur =
        if (blurred && hazeState != null) {
            // Haze's glass style only accepts RoundedCornerShape, so other shapes (for example CircleShape)
            // are applied by clipping the glass instead.
            val roundedShape = glassShape as? RoundedCornerShape
            Modifier
                .hazeGlass(
                    input = HazeInput.Sources(hazeState),
                    style =
                        GlassStyle.regular.then {
                            tint(mixed.copy(alpha = tintAlpha))
                            if (roundedShape != null) shape(roundedShape)
                        },
                ).clip(glassShape)
        } else {
            Modifier.background(mixed.copy(alpha = tintAlpha), shape)
        }
    return this
        .shadow(shadow, shape, clip = false, ambientColor = shadowColor, spotColor = shadowColor)
        .then(withBlur)
        .background(topLight, shape)
        .border(1.dp, rim, shape)
}

/** Reusable glass container. */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    accent: Color = Color.Unspecified,
    shadow: Dp = 12.dp,
    liveBlur: Boolean = true,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.glassSurface(shape, accent, shadow, liveBlur)) {
        CompositionLocalProvider(LocalContentColor provides contentColor) { content() }
    }
}

/** Translucent container color for sheets that live in their own window (no live blur possible there). */
@Composable
fun glassSheetContainerColor(): Color {
    val scheme = MaterialTheme.colorScheme
    val dark = scheme.surface.luminance() < 0.5f
    return scheme.surfaceContainerLow.copy(alpha = if (dark) 0.84f else 0.88f)
}
