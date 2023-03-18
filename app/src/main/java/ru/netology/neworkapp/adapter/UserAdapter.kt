package ru.netology.neworkapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.neworkapp.databinding.CardUserBinding
import ru.netology.neworkapp.dto.User
import ru.netology.neworkapp.util.loadCircleCrop

interface OnUserInteractionListener {
    fun onShowUserProfile(id: Int)
}

class UserAdapter(
    private val onUserInteractionListener: OnUserInteractionListener
) : ListAdapter<User, UserViewHolder>(UserViewHolder.UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onUserInteractionListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val onUserInteractionListener: OnUserInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: User) {
        binding.apply {
            if (!user.avatar.isNullOrBlank()) {
                avatar.loadCircleCrop(user.avatar)
            }
            avatar.setOnClickListener {
                onUserInteractionListener.onShowUserProfile(user.id)
            }
        }
    }


    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}