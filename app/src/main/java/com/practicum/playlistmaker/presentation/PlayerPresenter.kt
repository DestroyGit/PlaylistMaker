package com.practicum.playlistmaker.presentation

import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.Interactor
import com.practicum.playlistmaker.domain.PlayerStateListener
import java.text.SimpleDateFormat
import java.util.*

private const val STATE_DEFAULT = 0
private const val STATE_PREPARED = 1
private const val STATE_PLAYING = 2
private const val STATE_PAUSED = 3
private const val RELOAD_PROGRESS = 300L
private const val CURRENT_TIME_ZERO = "00:00"

private const val play = R.drawable.ic_play_button
private const val pause = R.drawable.ic_pause_button

class PlayerPresenter(private val view: PlayerView) : PlayerStateListener {

    private val interactor = Interactor(this)

    val mainHandler: Handler = Handler(Looper.getMainLooper())
    private var playerState = STATE_DEFAULT

    private val runThread = object : Runnable {
        override fun run() {
            val currentTime =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(interactor.getCurrentTime())
            view.setCurrentTime(currentTime)

            mainHandler.postDelayed(
                this,
                RELOAD_PROGRESS
            )
        }
    }

    fun onClickPlayAndPause() {
        playbackControl()
        currentTimeControl()
    }

    fun preparePlayer(track: Track) {
        interactor.preparePlayer(track)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    fun startPlayer() {
        interactor.start()
        view.setImage(pause)
        playerState = STATE_PLAYING
    }

    fun pausePlayer() {
        interactor.pause()
        view.setImage(play)
        playerState = STATE_PAUSED
    }

    fun onCompletionListener() {
        interactor.onCompletionListener()
    }

    private fun currentTimeControl() {
        when (playerState) {
            STATE_PLAYING -> {
                mainHandler.postDelayed(
                    runThread,
                    RELOAD_PROGRESS
                )
            }
            STATE_PAUSED -> {
                mainHandler.removeCallbacks(runThread)
            }
        }
    }

    fun backButtonClicked() {
        view.goBack()
    }

    fun clearPlayer() {
        interactor.releasePlayer()
        mainHandler.removeCallbacks(runThread)
    }

    fun fixReleaseDate(string: String): String = string.removeRange(4 until string.length)

    fun millisFormat(track: Track): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)

    override fun setStatePrepared() {
        view.setImage(play)
        playerState = STATE_PREPARED
    }

    override fun removeHandlersCallbacks() {
        mainHandler.removeCallbacks(runThread)
    }

    override fun setImagePlay() {
        view.setImage(play)
    }

    override fun setCurrentTimeZero() {
        view.setCurrentTime(CURRENT_TIME_ZERO)
    }
}