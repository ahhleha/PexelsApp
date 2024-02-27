package com.example.pexelsapp.ui.home

import androidx.recyclerview.widget.RecyclerView
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.RvRequestItemBinding
import com.example.pexelsapp.domain.models.RequestModel

class RequestsViewHolder(
    private val binding: RvRequestItemBinding,
    private val itemClick: (String, Int) -> Unit,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(request: RequestModel, isSelected: Boolean, position: Int) {
        with(binding) {
            requestItem.text = request.title

            if (isSelected) {
                requestItem.setBackgroundResource(R.drawable.active_request_background)
                requestItem.setTextColor(itemView.context.getColor(R.color.white))
            } else {
                requestItem.setBackgroundResource(R.drawable.inactive_request_background)
            }

            itemView.setOnClickListener {
                itemClick.invoke(request.title, position)
            }
        }
    }
}