package com.metrolist.shared.ui.glass

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/** iOS-like motion: critically damped springs (no bounce) and short fades, cheap enough for long lists. */
object IosMotion {
    fun fade(): FiniteAnimationSpec<Float> = tween(durationMillis = 180)

    fun fadeFast(): FiniteAnimationSpec<Float> = tween(durationMillis = 90)

    fun placement(): FiniteAnimationSpec<IntOffset> =
        spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)

    fun sizeSpec(): FiniteAnimationSpec<IntSize> =
        spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow, visibilityThreshold = IntSize(1, 1))

    fun <T> press(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium)
}
