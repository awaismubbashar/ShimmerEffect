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
import com.example.shimmereffect.databinding.ItemLayoutBinding
import com.example.shimmereffect.model.User

class UserAdapter(
    private val context: Context,
) : ListAdapter<User, UserAdapter.ViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean =
            oldItem.name == oldItem.name

        override fun areContentsTheSame(
            oldItem: User,
            newItem: User
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
        holder.itemBinding.textViewUserEmail.text = item.email
        holder.itemBinding.textViewUserName.text = item.name
        Glide.with(context)
            .load(item.avatar)
            .into(holder.itemBinding.imageViewAvatar)
    }
}

