package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.data.MediaPlayerListener
import com.practicum.playlistmaker.data.PlayerRepository

class Interactor(private val playerStateListener: PlayerStateListener) : MediaPlayerListener {

    private val playerRepository = PlayerRepository(this)

    fun start() {
        playerRepository.start()
    }

    fun pause() {
        playerRepository.pause()
    }

    fun getCurrentTime(): Int {
        return playerRepository.getCurrentTime()
    }

    fun preparePlayer(track: Track) {
        playerRepository.preparePlayer(track)
    }

    fun onCompletionListener() {
        playerRepository.onCompletionListener()
    }

    fun releasePlayer() {
        playerRepository.releasePlayer()
    }

    override fun setStatePrepared() {
        playerStateListener.setStatePrepared()
    }

    override fun removeHandlersCallbacks() {
        playerStateListener.removeHandlersCallbacks()
    }

    override fun setImagePlay() {
        playerStateListener.setImagePlay()
    }

    override fun setCurrentTimeZero() {
        playerStateListener.setCurrentTimeZero()
    }

}