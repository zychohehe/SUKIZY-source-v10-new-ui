/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.shared.streak

import com.metrolist.shared.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/** Satu hari pada tampilan mingguan streak. */
data class StreakDay(
    val epochDay: Long,
    /** 0 = Senin ... 6 = Minggu */
    val dayOfWeekIndex: Int,
    val active: Boolean,
    val isToday: Boolean,
)

data class StreakState(
    val count: Int = 0,
    val best: Int = 0,
    val checkedInToday: Boolean = false,
    val week: List<StreakDay> = emptyList(),
    val todayEpochDay: Long = 0L,
)

/**
 * Streak login harian: setiap hari kalender (zona waktu perangkat) di mana aplikasi dibuka
 * menambah streak. Lewat satu hari penuh tanpa membuka aplikasi -> streak kembali ke 1.
 */
object StreakManager {
    private const val MAX_RECENT_DAYS = 21

    private val _state = MutableStateFlow(StreakState())
    val state: StateFlow<StreakState> = _state.asStateFlow()

    fun currentEpochDay(): Long = epochDayOf(Clock.System.now().toEpochMilliseconds())

    private fun epochDayOf(epochMillis: Long): Long {
        val date =
            Instant
                .fromEpochMilliseconds(epochMillis)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        return date.toEpochDays().toLong()
    }

    /** Detik tersisa sampai pergantian hari berikutnya (tengah malam lokal). */
    fun secondsUntilNextDay(): Long {
        val tz = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val today = now.toLocalDateTime(tz).date
        val nextMidnight = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz)
        return (nextMidnight - now).inWholeSeconds.coerceAtLeast(0L)
    }

    /** Panggil saat aplikasi dibuka / kembali aktif / tengah malam terlewati. Idempotent per hari. */
    fun checkIn(today: Long = currentEpochDay()): StreakState {
        val settings = Settings.shared
        val last = settings.streakLastDay
        var count = settings.streakCount
        var recent = parseRecent(settings.streakRecentDays)

        if (last != today) {
            count =
                when {
                    last == today - 1 && count > 0 -> count + 1
                    else -> 1
                }
            recent = (recent + today).distinct().sorted().takeLast(MAX_RECENT_DAYS)
            settings.streakLastDay = today
            settings.streakCount = count
            settings.streakRecentDays = recent.joinToString(",")
            if (count > settings.streakBest) settings.streakBest = count
        }
        return publish(today, count, settings.streakBest, recent)
    }

    /** Muat ulang state dari penyimpanan tanpa menambah streak (mis. setelah restore backup). */
    fun refresh(today: Long = currentEpochDay()): StreakState {
        val settings = Settings.shared
        val last = settings.streakLastDay
        // Streak putus kalau kemarin terlewat; tampilkan 0 sampai check-in berikutnya.
        val effective = if (last >= today - 1) settings.streakCount else 0
        return publish(today, effective, settings.streakBest, parseRecent(settings.streakRecentDays))
    }

    private fun publish(
        today: Long,
        count: Int,
        best: Int,
        recent: List<Long>,
    ): StreakState {
        val set = recent.toSet()
        // Minggu berjalan Senin..Minggu. 1970-01-01 (epochDay 0) adalah hari Kamis.
        val todayDow = ((today + 3) % 7).toInt().let { if (it < 0) it + 7 else it } // 0 = Senin
        val monday = today - todayDow
        val week =
            (0..6).map { i ->
                val d = monday + i
                StreakDay(epochDay = d, dayOfWeekIndex = i, active = d in set, isToday = d == today)
            }
        val next =
            StreakState(
                count = count,
                best = best,
                checkedInToday = today in set,
                week = week,
                todayEpochDay = today,
            )
        _state.value = next
        return next
    }

    private fun parseRecent(raw: String): List<Long> = raw.split(',').mapNotNull { it.trim().toLongOrNull() }
}
