package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.domain.Track

class TrackAdapter(private val clickListener: TrackClickListener): RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener{
            clickListener.onTrackClick(tracks[position])
        }
    }

    override fun getItemCount() = tracks.size

    fun interface TrackClickListener{
        fun onTrackClick(track: Track)
    }
}