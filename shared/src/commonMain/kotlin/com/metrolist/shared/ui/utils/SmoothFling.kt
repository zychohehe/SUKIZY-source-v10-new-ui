package com.metrolist.shared.ui.utils

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.math.abs

private const val SmoothFlingFriction = 0.7f
private const val SmoothFlingMinVelocity = 1f
private const val SmoothFlingStopThreshold = 0.5f

/** A fling with lower friction than the default, so lists keep gliding a little longer. */
private class SmoothFlingBehavior(
    private val decay: DecayAnimationSpec<Float>,
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        if (abs(initialVelocity) <= SmoothFlingMinVelocity) return initialVelocity
        var remainingVelocity = initialVelocity
        var lastValue = 0f
        AnimationState(initialValue = 0f, initialVelocity = initialVelocity).animateDecay(decay) {
            val delta = value - lastValue
            val consumed = scrollBy(delta)
            lastValue = value
            remainingVelocity = velocity
            if (abs(delta - consumed) > SmoothFlingStopThreshold) cancelAnimation()
        }
        return remainingVelocity
    }
}

@Composable
fun rememberSmoothFlingBehavior(): FlingBehavior =
    remember { SmoothFlingBehavior(exponentialDecay(frictionMultiplier = SmoothFlingFriction)) }
