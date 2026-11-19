package com.metrolist.android.spotifyimport

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * One-shot request from the Library tab to open the Spotify import dialog as soon as the
 * playlist import screen is shown.
 */
object SpotifyImportLaunchRequest {
    private val pending = MutableStateFlow(false)
    val isPending: StateFlow<Boolean> = pending.asStateFlow()

    fun request() {
        pending.value = true
    }

    fun consume() {
        pending.value = false
    }
}
