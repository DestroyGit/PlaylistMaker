package com.practicum.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val RELOAD_PROGRESS = 300L
        private const val CURRENT_TIME_ZERO = "00:00"
    }

    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var timeAttributes: TextView
    private lateinit var albumNameAttributes: TextView
    private lateinit var yearAttributes: TextView
    private lateinit var genreAttributes: TextView
    private lateinit var countryAttributes: TextView
    private lateinit var currentTime: TextView
    private lateinit var imageSong: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var btnPlay: FloatingActionButton
    private val gson = Gson()
    private val play = R.drawable.ic_play_button
    private val pause = R.drawable.ic_pause_button
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var mainHandler: Handler? = null

    private val runThread = object : Runnable {
        override fun run() {
            currentTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)

            mainHandler?.postDelayed(
                this,
                RELOAD_PROGRESS
            )
        }
    }

    private fun currentTimeControl() {
        when (playerState) {
            STATE_PLAYING -> {
                mainHandler?.postDelayed(
                    runThread,
                    RELOAD_PROGRESS
                )
            }
            STATE_PAUSED -> {
                mainHandler?.removeCallbacks(runThread)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainHandler?.removeCallbacks(runThread)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = gson.fromJson(
            intent.getStringExtra(SearchActivity.TRACKS_FOR_PLAYER),
            Track::class.java
        )
        mainHandler = Handler(Looper.getMainLooper())

        initViews()
        fillViews(track)
        preparePlayer(track)

        btnPlay.setOnClickListener {
            playbackControl()
            currentTimeControl()
        }

        btnBack.setOnClickListener {
            finish()
        }

        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            mainHandler?.removeCallbacks(runThread)
            btnPlay.setImageResource(play)
            currentTime.text = CURRENT_TIME_ZERO
        }

    }

    private fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener() {
            btnPlay.setImageResource(play)
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            btnPlay.setImageResource(play)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        btnPlay.setImageResource(pause)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        btnPlay.setImageResource(play)
        playerState = STATE_PAUSED
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

    private fun initViews() {
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        currentTime = findViewById(R.id.currentTime)
        timeAttributes = findViewById(R.id.time)
        albumNameAttributes = findViewById(R.id.albumName)
        yearAttributes = findViewById(R.id.year)
        genreAttributes = findViewById(R.id.genre)
        countryAttributes = findViewById(R.id.country)
        imageSong = findViewById(R.id.imageSong)
        btnBack = findViewById(R.id.btnBack)
        btnPlay = findViewById(R.id.btnPlay)
    }

    private fun fillViews(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        timeAttributes.text = millisFormat(track)
        albumNameAttributes.text = track.collectionName
        yearAttributes.text = fixReleaseDate(track.releaseDate)
        genreAttributes.text = track.primaryGenreName
        countryAttributes.text = track.country
        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.player_round_corner)))
            .placeholder(R.drawable.ic_album)
            .into(imageSong)
    }

    private fun fixReleaseDate(string: String): String = string.removeRange(4 until string.length)

    private fun millisFormat(track: Track): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
}