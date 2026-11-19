/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.android.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.metrolist.shared.streak.StreakDay
import com.metrolist.shared.streak.StreakManager
import com.metrolist.shared.streak.StreakState
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

private val FlameOuterTop = Color(0xFFFF3D00)
private val FlameOuterBottom = Color(0xFFFFB300)
private val FlameInnerTop = Color(0xFFFFD54F)
private val FlameInnerBottom = Color(0xFFFFF8E1)
private val FlameGlow = Color(0xFFFF6D00)

/**
 * Mengaktifkan streak login: check-in saat dibuka, saat kembali aktif, dan otomatis tepat
 * ketika hari berganti (real-time) walau aplikasi tetap terbuka.
 */
@Composable
fun rememberStreakState(): StreakState {
    LaunchedEffect(Unit) {
        StreakManager.checkIn()
        while (true) {
            delay(StreakManager.secondsUntilNextDay() * 1000L + 500L)
            StreakManager.checkIn()
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { StreakManager.checkIn() }
    val state by StreakManager.state.collectAsStateWithLifecycle()
    return state
}

/** Api animasi: bergoyang, berkedip, bercahaya, dan memercikkan bara. */
@Composable
fun AnimatedStreakFlame(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    lit: Boolean = true,
) {
    val transition = rememberInfiniteTransition(label = "flame")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "flameT",
    )
    val fast by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(620, easing = LinearEasing), RepeatMode.Restart),
        label = "flameFast",
    )
    val glow by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "flameGlow",
    )

    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val two = (2 * PI).toFloat()
        val sway = sin(t * two) * w * 0.06f
        val flickerY = 1f + sin(fast * two) * 0.045f
        val flickerX = 1f + sin(fast * two + 1.2f) * 0.03f

        if (lit) {
            drawCircle(
                brush =
                    Brush.radialGradient(
                        listOf(FlameGlow.copy(alpha = glow * 0.55f), Color.Transparent),
                        center = Offset(w / 2f, h * 0.62f),
                        radius = w * 0.85f,
                    ),
                radius = w * 0.85f,
                center = Offset(w / 2f, h * 0.62f),
            )
        }

        val flameW = w * 0.64f * flickerX
        val flameH = h * 0.8f * flickerY
        translate(left = (w - flameW) / 2f, top = h * 0.94f - flameH) {
            drawFlamePath(
                width = flameW,
                height = flameH,
                tipShift = sway,
                brush =
                    if (lit) {
                        Brush.verticalGradient(listOf(FlameOuterTop, FlameOuterBottom), 0f, flameH)
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFF6B6F7A), Color(0xFF454851)), 0f, flameH)
                    },
            )
            val innerW = flameW * 0.52f
            val innerH = flameH * 0.58f
            translate(left = (flameW - innerW) / 2f, top = flameH - innerH - flameH * 0.04f) {
                drawFlamePath(
                    width = innerW,
                    height = innerH,
                    tipShift = -sway * 0.7f,
                    brush =
                        if (lit) {
                            Brush.verticalGradient(listOf(FlameInnerTop, FlameInnerBottom), 0f, innerH)
                        } else {
                            Brush.verticalGradient(listOf(Color(0xFF8A8E99), Color(0xFF5E616B)), 0f, innerH)
                        },
                )
            }
        }

        if (lit) {
            // Percikan bara naik dan memudar.
            val sparks = 4
            for (i in 0 until sparks) {
                val p = (t + i / sparks.toFloat()) % 1f
                val x = w * (0.30f + 0.4f * ((i * 37 % 10) / 10f)) + sin((p + i) * two) * w * 0.05f
                val y = h * (0.45f - p * 0.42f)
                drawCircle(
                    color = FlameInnerTop.copy(alpha = (1f - p) * 0.9f),
                    radius = w * 0.028f * (1f - p * 0.5f) + 0.5f,
                    center = Offset(x, y),
                )
            }
        }
    }
}

private fun DrawScope.drawFlamePath(
    width: Float,
    height: Float,
    tipShift: Float,
    brush: Brush,
) {
    val tipX = width / 2f + tipShift
    val path =
        Path().apply {
            moveTo(tipX, 0f)
            cubicTo(width * 0.98f, height * 0.34f, width * 1.04f, height * 0.74f, width * 0.5f, height)
            cubicTo(-width * 0.04f, height * 0.74f, width * 0.02f, height * 0.34f, tipX, 0f)
            close()
        }
    drawPath(path, brush)
}

private val Ember = Color(0xFFFF8A00)
private val Milestones = listOf(3, 7, 14, 30, 60, 100, 200, 365)
private val DayLabels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

/** Warna angka streak: terang di tema gelap, lebih pekat di tema terang agar tetap terbaca. */
@Composable
private fun emberTextColor(lit: Boolean): Color {
    if (!lit) return MaterialTheme.colorScheme.onSurfaceVariant
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return if (dark) Color(0xFFFFB74D) else Color(0xFFC43E00)
}

/** Chip ringkas di top bar: api bergerak + jumlah hari. Ketuk untuk membuka detail streak. */
@Composable
fun StreakChip(
    state: StreakState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val lit = state.count > 0
    val shape = RoundedCornerShape(50)
    val label = "Streak ${state.count} hari"
    Row(
        modifier =
            modifier
                .semantics { contentDescription = label }
                .clip(shape)
                .background(
                    if (lit) {
                        Brush.horizontalGradient(listOf(Ember.copy(alpha = 0.26f), Ember.copy(alpha = 0.10f)))
                    } else {
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                            ),
                        )
                    },
                ).border(
                    1.dp,
                    if (lit) Ember.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outlineVariant,
                    shape,
                ).clickable(onClick = onClick)
                .padding(start = 4.dp, end = 12.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedStreakFlame(size = 30.dp, lit = lit)
        AnimatedContent(
            targetState = state.count,
            transitionSpec = {
                (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
            },
            label = "streakCount",
        ) { count ->
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleSmall.copy(fontFeatureSettings = "tnum"),
                fontWeight = FontWeight.ExtraBold,
                color = emberTextColor(lit),
            )
        }
    }
}

/** Lembar detail streak: hero api, rantai mingguan, progres target, rekor, dan hitung mundur real-time. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakSheet(
    state: StreakState,
    onDismiss: () -> Unit,
) {
    var secondsLeft by remember { mutableLongStateOf(StreakManager.secondsUntilNextDay()) }
    LaunchedEffect(state.todayEpochDay) {
        while (true) {
            secondsLeft = StreakManager.secondsUntilNextDay()
            delay(1000L)
        }
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StreakHero(state)
            WeekCard(state)
            MilestoneCard(state.count)
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                StatCell("Rekor terbaik", "${state.best} hari", Modifier.weight(1f))
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .padding(vertical = 14.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                )
                StatCell("Hari baru dalam", formatCountdown(secondsLeft), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StreakHero(state: StreakState) {
    val lit = state.count > 0
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .drawBehind {
                    if (lit) {
                        drawRect(
                            Brush.radialGradient(
                                colors = listOf(Ember.copy(alpha = 0.30f), Color.Transparent),
                                center = Offset(size.width / 2f, size.height * 0.26f),
                                radius = size.width * 0.8f,
                            ),
                        )
                    }
                }.padding(horizontal = 20.dp, vertical = 22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedStreakFlame(size = 112.dp, lit = lit)
            Text(
                text = state.count.toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontFeatureSettings = "tnum"),
                fontWeight = FontWeight.ExtraBold,
                color = emberTextColor(lit),
            )
            Text(
                text = if (lit) "hari beruntun" else "Belum ada streak",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(16.dp))
            StatusPill(state.checkedInToday)
        }
    }
}

@Composable
private fun StatusPill(checkedIn: Boolean) {
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (checkedIn) {
            Box(
                Modifier.size(18.dp).clip(CircleShape).background(Ember),
                contentAlignment = Alignment.Center,
            ) {
                CheckMark(Modifier.size(11.dp), Color.White)
            }
        } else {
            Box(Modifier.size(8.dp).clip(CircleShape).background(Ember))
        }
        Text(
            text =
                if (checkedIn) {
                    "Hari ini sudah tercatat. Buka lagi besok."
                } else {
                    "Buka aplikasi hari ini untuk menjaga api."
                },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun WeekCard(state: StreakState) {
    val activeCount = state.week.count { it.active }
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Minggu ini",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "$activeCount dari 7 hari",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(Modifier.fillMaxWidth()) {
            state.week.forEachIndexed { index, day ->
                DayCell(
                    day = day,
                    prevActive = state.week.getOrNull(index - 1)?.active == true,
                    nextActive = state.week.getOrNull(index + 1)?.active == true,
                    future = day.epochDay > state.todayEpochDay,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** Satu hari: label + lingkaran, dengan pita penghubung ke hari aktif di sebelahnya (efek rantai). */
@Composable
private fun DayCell(
    day: StreakDay,
    prevActive: Boolean,
    nextActive: Boolean,
    future: Boolean,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outlineVariant
    val filled = MaterialTheme.colorScheme.surfaceContainerHighest
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            DayLabels[day.dayOfWeekIndex],
            style = MaterialTheme.typography.labelSmall,
            color = if (day.isToday) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .drawBehind {
                        if (day.active) {
                            val bar = 6.dp.toPx()
                            val top = (size.height - bar) / 2f
                            if (prevActive) drawRect(Ember, Offset(0f, top), Size(size.width / 2f, bar))
                            if (nextActive) drawRect(Ember, Offset(size.width / 2f, top), Size(size.width / 2f, bar))
                        }
                    },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (day.active) {
                                Brush.verticalGradient(listOf(FlameOuterTop, FlameOuterBottom))
                            } else {
                                Brush.verticalGradient(
                                    listOf(if (future) Color.Transparent else filled, if (future) Color.Transparent else filled),
                                )
                            },
                        ).then(
                            when {
                                day.isToday -> Modifier.border(2.dp, primary, CircleShape)
                                future -> Modifier.border(1.5.dp, outline.copy(alpha = 0.7f), CircleShape)
                                else -> Modifier
                            },
                        ),
                contentAlignment = Alignment.Center,
            ) {
                if (day.active) CheckMark(Modifier.size(16.dp), Color.White)
            }
        }
    }
}

@Composable
private fun CheckMark(
    modifier: Modifier,
    color: Color,
) {
    Canvas(modifier) {
        val path =
            Path().apply {
                moveTo(size.width * 0.18f, size.height * 0.54f)
                lineTo(size.width * 0.42f, size.height * 0.76f)
                lineTo(size.width * 0.84f, size.height * 0.28f)
            }
        drawPath(
            path,
            color,
            style = Stroke(width = size.width * 0.16f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
private fun MilestoneCard(count: Int) {
    val previous = Milestones.lastOrNull { it <= count } ?: 0
    val next = Milestones.firstOrNull { it > count } ?: ((count / 100) + 1) * 100
    val target = ((count - previous).toFloat() / (next - previous).toFloat()).coerceIn(0f, 1f)
    val progress by animateFloatAsState(target, tween(700), label = "milestoneProgress")
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Target berikutnya",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "$next hari",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = emberTextColor(true),
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(FlameOuterTop, FlameOuterBottom))),
            )
        }
        Text(
            text = "Tinggal ${next - count} hari lagi",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun formatCountdown(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}
