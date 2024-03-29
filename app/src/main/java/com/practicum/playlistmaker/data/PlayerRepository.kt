package com.practicum.playlistmaker.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.Track

class PlayerRepository(private val mediaPlayerListener: MediaPlayerListener) {

    private val mediaPlayer = MediaPlayer()

    fun start() {
        mediaPlayer.start()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun getCurrentTime(): Int {
        return mediaPlayer.currentPosition
    }

    fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener() {
            mediaPlayerListener.setStatePrepared()
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayerListener.setStatePrepared()
        }
    }

    fun onCompletionListener() {
        mediaPlayer.setOnCompletionListener {
            mediaPlayerListener.setStatePrepared()
            mediaPlayerListener.removeHandlersCallbacks()
            mediaPlayerListener.setImagePlay()
            mediaPlayerListener.setCurrentTimeZero()
        }
    }

    fun releasePlayer() {
        mediaPlayer.release()
    }

}