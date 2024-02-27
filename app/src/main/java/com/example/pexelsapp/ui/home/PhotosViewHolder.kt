package com.example.pexelsapp.ui.home

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.pexelsapp.databinding.RvImageItemBinding
import com.example.pexelsapp.domain.models.PhotoModel

class PhotosViewHolder(
    private val binding: RvImageItemBinding,
    private val itemClick: (Int) -> Unit,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(photo: PhotoModel) {
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .skipMemoryCache(true)
            .timeout(CACHE_TIMEOUT)

        Glide
            .with(itemView.context)
            .load(photo.url)
            .apply(requestOptions)
            .into(binding.photoItem)

        itemView.setOnClickListener {
            itemClick.invoke(photo.id)
        }
    }

    companion object{
        private const val CACHE_TIMEOUT = 3600000
    }
}