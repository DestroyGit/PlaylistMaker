package com.practicum.playlistmaker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SearchActivity
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.presentation.PlayerPresenter
import com.practicum.playlistmaker.presentation.PlayerView

class PlayerActivity : AppCompatActivity(), PlayerView {

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

    private lateinit var track: Track
    private lateinit var presenter: PlayerPresenter
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initValues()
        initViews()
        fillViews(track)
        presenter.preparePlayer(track)
        presenter.onCompletionListener()

        btnPlay.setOnClickListener {
            presenter.onClickPlayAndPause()
        }

        btnBack.setOnClickListener {
            presenter.backButtonClicked()
        }
    }

    private fun initValues() {
        presenter = PlayerPresenter(this)
        track = gson.fromJson(
            intent.getStringExtra(SearchActivity.TRACKS_FOR_PLAYER),
            Track::class.java
        )
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
        timeAttributes.text = presenter.millisFormat(track)
        albumNameAttributes.text = track.collectionName
        yearAttributes.text = presenter.fixReleaseDate(track.releaseDate)
        genreAttributes.text = track.primaryGenreName
        countryAttributes.text = track.country
        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.player_round_corner)))
            .placeholder(R.drawable.ic_album)
            .into(imageSong)
    }

    override fun onPause() {
        super.onPause()
        presenter.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.clearPlayer()
    }

    override fun setImage(image: Int) {
        btnPlay.setImageResource(image)
    }

    override fun setCurrentTime(time: String) {
        currentTime.text = time
    }

    override fun goBack() {
        finish()
    }
}