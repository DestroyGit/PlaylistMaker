package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.track_view,parent,false)){
    private val songNameView: TextView = itemView.findViewById(R.id.songName)
    private val artistNameView: TextView = itemView.findViewById(R.id.bandName)
    private val songDurationView: TextView = itemView.findViewById(R.id.songDuration)
    private val imageAlbumView: ImageView = itemView.findViewById(R.id.imageAlbum)

    fun bind(track: Track){
        songNameView.text = track.trackName
        artistNameView.text = track.artistName
        songDurationView.text = millisFormat(track) // добавить форматирование миллисекунд в строковое значение
        Glide.with(itemView).load(track.artworkUrl100).centerCrop().transform(RoundedCorners(10)).placeholder(R.drawable.ic_album).into(imageAlbumView)
    }

    private fun millisFormat(track: Track) : String{
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
    }

}