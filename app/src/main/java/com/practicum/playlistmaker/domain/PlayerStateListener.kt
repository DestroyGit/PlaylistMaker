package com.practicum.playlistmaker.domain

interface PlayerStateListener {
    fun setStatePrepared()
    fun removeHandlersCallbacks()
    fun setImagePlay()
    fun setCurrentTimeZero()
}