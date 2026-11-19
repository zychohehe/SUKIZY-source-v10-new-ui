@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.metrolist.shared.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShortNavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.metrolist.shared.ui.components.focusOutline
import com.metrolist.shared.ui.glass.IosMotion

val LocalFloatingNavBar = compositionLocalOf { false }

private val FloatingBarHeight = 64.dp
private val FloatingItemHeight = 48.dp

@Composable
internal fun <T> FloatingNavigation(
    items: List<AdaptiveNavigationItem<T>>,
    selectedKey: T?,
    onSelect: (T) -> Unit,
    navigationVisible: Boolean,
    separateKey: T?,
    itemModifier: @Composable (T) -> Modifier,
    onBarHeightChanged: (Dp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = navigationVisible,
        modifier = modifier,
        // Slide and fade run on the graphics layer, so the bar never re-measures (and never resizes the
        // screen content) frame by frame the way expand/shrink does.
        enter = slideInVertically(animationSpec = IosMotion.placement(), initialOffsetY = { it }) + fadeIn(IosMotion.fade()),
        exit = slideOutVertically(animationSpec = IosMotion.placement(), targetOffsetY = { it }) + fadeOut(IosMotion.fadeFast()),
    ) {
        FloatingNavigationBar(
            items = items,
            selectedKey = selectedKey,
            onSelect = onSelect,
            separateKey = separateKey,
            itemModifier = itemModifier,
            modifier =
                Modifier.onSizeChanged { size ->
                    onBarHeightChanged(with(density) { size.height.toDp() })
                },
        )
    }
}

@Composable
private fun <T> FloatingNavigationBar(
    items: List<AdaptiveNavigationItem<T>>,
    selectedKey: T?,
    onSelect: (T) -> Unit,
    separateKey: T?,
    itemModifier: @Composable (T) -> Modifier,
    modifier: Modifier = Modifier,
) {
    val pillItems = items.filter { it.key != separateKey }
    val separateItem = items.firstOrNull { it.key == separateKey }

    Row(
        modifier =
            modifier
                .wrapContentWidth()
                .windowInsetsPadding(ShortNavigationBarDefaults.windowInsets)
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.wrapContentWidth().height(FloatingBarHeight).navPill(),
            shape = CircleShape,
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Row(
                modifier = Modifier.wrapContentWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                pillItems.forEach { item ->
                    FloatingNavigationItem(
                        item = item,
                        selected = item.key == selectedKey,
                        onClick = { onSelect(item.key) },
                        modifier = itemModifier(item.key),
                    )
                }
            }
        }

        if (separateItem != null) {
            val selected = separateItem.key == selectedKey
            val content by animateColorAsState(
                if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                label = "floatingSearchContent",
            )
            Surface(
                onClick = { onSelect(separateItem.key) },
                modifier =
                    Modifier
                        .size(FloatingBarHeight)
                        .navPill()
                        .navSelectionIndicator(selected)
                        .then(itemModifier(separateItem.key)),
                shape = CircleShape,
                color = Color.Transparent,
                contentColor = content,
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = if (selected) separateItem.selectedIcon else separateItem.icon,
                        contentDescription = separateItem.label,
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> FloatingNavigationItem(
    item: AdaptiveNavigationItem<T>,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "floatingItemContent",
    )
    Row(
        modifier =
            modifier
                .height(FloatingItemHeight)
                .clip(CircleShape)
                .navSelectionIndicator(selected)
                .focusOutline(CircleShape)
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    role = Role.Tab,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                ).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(IosMotion.fade()) + expandHorizontally(IosMotion.sizeSpec()),
            exit = fadeOut(IosMotion.fadeFast()) + shrinkHorizontally(IosMotion.sizeSpec()),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = item.selectedIcon, contentDescription = null, tint = contentColor)
                Spacer(Modifier.width(8.dp))
            }
        }
        Text(
            text = item.label,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Plain navigation pill: flat surface color with a hairline shadow. No blur, gradients or borders. */
@Composable
private fun Modifier.navPill(): Modifier {
    val color = MaterialTheme.colorScheme.surfaceContainer
    return this.shadow(2.dp, CircleShape, clip = false).background(color, CircleShape)
}

/** Selected tab marker. The animated value is read only while drawing, so tab changes never recompose the bar. */
@Composable
private fun Modifier.navSelectionIndicator(selected: Boolean): Modifier {
    val fraction =
        animateFloatAsState(
            targetValue = if (selected) 1f else 0f,
            animationSpec = tween(durationMillis = 180),
            label = "navSelection",
        )
    val color = MaterialTheme.colorScheme.secondaryContainer
    return this.drawBehind {
        val value = fraction.value
        if (value > 0.001f) drawRoundRect(color, cornerRadius = CornerRadius(size.minDimension / 2f), alpha = value)
    }
}
