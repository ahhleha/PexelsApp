package com.example.pexelsapp.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.pexelsapp.databinding.RvImageWithAuthorItemBinding
import com.example.pexelsapp.domain.models.PhotoModel
import com.example.pexelsapp.ui.base.BaseListAdapter

class SavedPhotosAdapter : BaseListAdapter<PhotoModel, SavedPhotosViewHolder>(
    areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id }
) {
    var itemClick: (Int) -> Unit = {}

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedPhotosViewHolder {
        val binding =
            RvImageWithAuthorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedPhotosViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: SavedPhotosViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}