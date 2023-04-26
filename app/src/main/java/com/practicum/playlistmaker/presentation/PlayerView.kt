package com.practicum.playlistmaker.presentation

interface PlayerView {
    fun setImage(image: Int)
    fun setCurrentTime(time: String)
    fun goBack()
}