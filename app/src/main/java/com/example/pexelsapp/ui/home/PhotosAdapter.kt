package com.example.pexelsapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.pexelsapp.databinding.RvImageItemBinding
import com.example.pexelsapp.domain.models.PhotoModel
import com.example.pexelsapp.ui.base.BaseListAdapter

class PhotosAdapter : BaseListAdapter<PhotoModel, PhotosViewHolder>(
    areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id }
) {
    var itemClick: (Int) -> Unit = {}

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val binding =
            RvImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotosViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}