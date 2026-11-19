@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.metrolist.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.metrolist.shared.generated.resources.Res
import com.metrolist.shared.generated.resources.see_all
import com.metrolist.shared.ui.components.images.AsyncImage
import com.metrolist.shared.ui.icons.MetrolistIcons
import com.metrolist.shared.ui.utils.artistArtworkShape
import org.jetbrains.compose.resources.stringResource

private val GenreGradients =
    listOf(
        Color(0xFF7F5AF0) to Color(0xFF2CB1BC),
        Color(0xFFFF6B6B) to Color(0xFFFFA45B),
        Color(0xFF11998E) to Color(0xFF38EF7D),
        Color(0xFF396AFC) to Color(0xFF2948FF),
        Color(0xFFEE0979) to Color(0xFFFF6A00),
        Color(0xFF8E2DE2) to Color(0xFF4A00E0),
        Color(0xFF00B4DB) to Color(0xFF0083B0),
        Color(0xFFF953C6) to Color(0xFFB91D73),
        Color(0xFFF7971E) to Color(0xFFFFD200),
        Color(0xFF134E5E) to Color(0xFF71B280),
    )

@Composable
fun GenreCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
) {
    // A stable color per genre so the same genre looks the same on every screen.
    val (start, end) = GenreGradients[(title.hashCode() and Int.MAX_VALUE) % GenreGradients.size]
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = shape,
        color = Color.Transparent,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(start, end)))
                    .drawBehind {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.14f),
                            radius = size.height * 0.9f,
                            center = Offset(size.width * 0.95f, size.height * 0.1f),
                        )
                    },
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 14.dp),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    thumbnailUrl: String? = null,
    isArtistEndpoint: Boolean = false,
    artworkSeed: String = thumbnailUrl.orEmpty(),
    seeAllLabel: String? = null,
    showSeeAllLabel: Boolean = true,
    shuffleLabel: String? = null,
    playAllLabel: String? = null,
    onClick: (() -> Unit)? = null,
    onPlayAllClick: (() -> Unit)? = null,
    onShuffleClick: (() -> Unit)? = null,
    isWide: Boolean = false,
) {
    val titleStyle =
        (if (isWide) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge)
            .copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
    val rowHeight = if (isWide || label != null) 56.dp else 44.dp

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = rowHeight)
                .then(
                    if (onClick != null) {
                        Modifier.clip(RoundedCornerShape(14.dp)).clickable(
                            role = Role.Button,
                            onClickLabel = seeAllLabel ?: stringResource(Res.string.see_all),
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (thumbnailUrl != null) {
            AsyncImage(
                url = thumbnailUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(
                            if (isArtistEndpoint) artistArtworkShape(artworkSeed) else RoundedCornerShape(4.dp),
                        ),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = title,
                style = titleStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        if (onClick != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showSeeAllLabel && seeAllLabel != null) {
                    Text(
                        text = seeAllLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                SectionActionButton(
                    icon = MetrolistIcons.Outlined.ChevronRight,
                    contentDescription = null,
                    isWide = isWide,
                )
            }
        } else if (onShuffleClick != null) {
            SectionActionButton(
                icon = MetrolistIcons.Rounded.Shuffle,
                contentDescription = shuffleLabel,
                onClick = onShuffleClick,
                isWide = isWide,
            )
        } else if (onPlayAllClick != null) {
            SectionActionButton(
                icon = MetrolistIcons.Default.PlayArrow,
                contentDescription = playAllLabel,
                onClick = onPlayAllClick,
                isWide = isWide,
            )
        }
    }
}

@Composable
private fun SectionActionButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: (() -> Unit)? = null,
    isWide: Boolean = false,
) {
    val boxSize = if (isWide) 48.dp else 44.dp
    val surfaceSize = Modifier.size(36.dp, 28.dp)
    val iconSize = 18.dp

    Box(
        modifier = Modifier.size(boxSize),
        contentAlignment = Alignment.Center,
    ) {
        val content: @Composable () -> Unit = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(iconSize),
                )
            }
        }
        if (onClick != null) {
            Surface(
                modifier = surfaceSize,
                shape = RoundedCornerShape(100.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                onClick = onClick,
                content = content,
            )
        } else {
            Surface(
                modifier = surfaceSize,
                shape = RoundedCornerShape(100.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                content = content,
            )
        }
    }
}
