package com.app.flickr

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.flickr.databinding.AdapterFlickrItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class FlickrAdapter: ListAdapter<Photo, FlickrAdapter.FlickrHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrHolder {
        val binding = AdapterFlickrItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FlickrHolder(binding)
    }

    override fun onBindViewHolder(holder: FlickrHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class FlickrHolder(private val binding: AdapterFlickrItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(photo: Photo) {
            binding.apply {

                val url = String.format("https://farm%d.static.flickr.com/%s/%s_%s_m.jpg",
                    photo.farm, photo.server, photo.id, photo.secret)
                Glide.with(binding.root.context as Activity)
                    .load(url)
                    .apply(RequestOptions().apply{
                        placeholder(R.mipmap.ic_launcher)
                    })
                    .into(imageIv)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areContentsTheSame(oldItem: Photo, newItem: Photo) = oldItem == newItem
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo) = oldItem.id == newItem.id
    }
}

