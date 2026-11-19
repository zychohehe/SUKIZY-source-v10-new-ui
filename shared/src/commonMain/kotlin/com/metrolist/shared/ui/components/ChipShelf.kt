package com.metrolist.shared.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.metrolist.shared.generated.resources.Res
import com.metrolist.shared.generated.resources.chip_content_description
import com.metrolist.shared.ui.tv.tvFocusHighlight
import com.metrolist.shared.ui.utils.carouselScroll
import org.jetbrains.compose.resources.stringResource

private val ChipShelfHeight = 56.dp
private val ChipHeight = 38.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> ChipShelf(
    options: List<T>,
    selectedOption: T?,
    onSelectionChange: (T?) -> Unit,
    label: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    allLabel: String? = null,
    pinnedLabel: String? = null,
    onPinnedClick: () -> Unit = {},
    selectionRequired: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp),
    leadingContent: (@Composable () -> Unit)? = null,
    scrollState: ScrollState = rememberScrollState(),
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(ChipShelfHeight)
                .clipToBounds()
                .focusGroup()
                .selectableGroup()
                .carouselScroll(scrollState)
                .horizontalScroll(scrollState)
                .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingContent?.invoke()
        pinnedLabel?.let { text ->
            ShelfChip(
                text = text,
                selected = true,
                onClick = onPinnedClick,
            )
        }
        allLabel?.let { text ->
            ShelfChip(
                text = text,
                selected = selectedOption == null,
                onClick = { onSelectionChange(null) },
            )
        }
        options.forEach { option ->
            val selected = selectedOption == option
            ShelfChip(
                text = label(option),
                selected = selected,
                onClick = {
                    onSelectionChange(
                        if (selected && !selectionRequired) null else option,
                    )
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ShelfChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val chipDescription = stringResource(Res.string.chip_content_description, text)
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(50)
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "chipScale",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) scheme.onPrimary else scheme.onSurface,
        animationSpec = tween(200),
        label = "chipContent",
    )
    val unselectedColor by animateColorAsState(
        targetValue = if (selected) Color.Transparent else scheme.surfaceContainerHigh,
        animationSpec = tween(200),
        label = "chipContainer",
    )
    val selectedBrush = Brush.horizontalGradient(listOf(scheme.primary, scheme.tertiary))
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .height(ChipHeight)
                .scale(scale)
                .clip(shape)
                .then(if (selected) Modifier.background(selectedBrush) else Modifier.background(unselectedColor))
                .then(
                    if (selected) {
                        Modifier
                    } else {
                        Modifier.border(1.dp, scheme.outlineVariant.copy(alpha = 0.6f), shape)
                    },
                ).tvFocusHighlight(shape)
                .clearAndSetSemantics {
                    contentDescription = chipDescription
                    role = Role.Button
                    this.selected = selected
                    onClick {
                        onClick()
                        true
                    }
                }.clickable(onClick = onClick)
                .padding(horizontal = 18.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
            maxLines = 1,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    ToggleButton(
        checked = selected,
        onCheckedChange = { onClick() },
        enabled = enabled,
        shapes = ToggleButtonDefaults.shapesFor(ButtonDefaults.ExtraSmallContainerHeight),
        colors =
            ToggleButtonDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        contentPadding = ButtonDefaults.ExtraSmallContentPadding,
        modifier =
            modifier
                .height(ButtonDefaults.ExtraSmallContainerHeight)
                .tvFocusHighlight(ToggleButtonDefaults.shape)
                .semantics { this.selected = selected },
    ) {
        label()
    }
}
