package com.example.shimmereffect.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shimmereffect.R
import com.example.shimmereffect.database.QuoteEntity
import com.example.shimmereffect.databinding.ItemLayoutBinding
import com.example.shimmereffect.model.Quotes
import com.example.shimmereffect.model.User

class QuotesAdapter(
    private val context: Context,
) : ListAdapter<QuoteEntity, QuotesAdapter.ViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<QuoteEntity>() {
        override fun areItemsTheSame(
            oldItem: QuoteEntity,
            newItem: QuoteEntity
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: QuoteEntity,
            newItem: QuoteEntity
        ): Boolean =
            oldItem == newItem
    }

    inner class ViewHolder(val itemBinding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(position)

        holder.itemBinding.quote = item
    }
}

