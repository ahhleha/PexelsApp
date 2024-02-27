package com.example.pexelsapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pexelsapp.databinding.RvRequestItemBinding
import com.example.pexelsapp.domain.models.RequestModel
import com.example.pexelsapp.ui.base.BaseListAdapter

class RequestsAdapter : BaseListAdapter<RequestModel, RequestsViewHolder>(
    areItemsTheSame = { oldItem, newItem -> oldItem == newItem }
) {
    var itemClick: (String, Int) -> Unit = { _, _ -> }
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsViewHolder {
        val binding =
            RvRequestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestsViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: RequestsViewHolder, position: Int) {
        holder.onBind(getItem(position), position == selectedPosition, position)
    }

    fun setSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(position)
    }
}