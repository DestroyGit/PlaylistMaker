package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val songNameView: TextView
    private val artistNameView: TextView
    private val songDurationView: TextView
    private val imageAlbumView: ImageView

    init {
        songNameView = itemView.findViewById(R.id.songName)
        artistNameView = itemView.findViewById(R.id.bandName)
        songDurationView = itemView.findViewById(R.id.songDuration)
        imageAlbumView = itemView.findViewById(R.id.imageAlbum)
    }

    fun bind(track: Track){
        songNameView.text = track.trackName
        artistNameView.text = track.artistName
        songDurationView.text = track.trackTime
        Glide.with(itemView).load(track.artworkUrl100).centerCrop().transform(RoundedCorners(10)).placeholder(R.drawable.ic_album).into(imageAlbumView)
    }

}