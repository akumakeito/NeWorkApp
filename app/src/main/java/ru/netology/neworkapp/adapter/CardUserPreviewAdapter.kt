package ru.netology.neworkapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.neworkapp.databinding.CardUserPreviewBinding
import ru.netology.neworkapp.dto.User
import ru.netology.neworkapp.util.loadCircleCrop

interface OnCardUserPreviewInteractionListener {
    fun openUserProfile(id: Int)
    fun deleteFromList(id: Int)
}

class CardUserPreviewAdapter(
    private val onCardUserPreviewInteractionListener: OnCardUserPreviewInteractionListener
) : ListAdapter<User, CardUserPreviewViewHolder>(UserViewHolder.UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardUserPreviewViewHolder {
        val binding =
            CardUserPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardUserPreviewViewHolder(binding, onCardUserPreviewInteractionListener)
    }

    override fun onBindViewHolder(holder: CardUserPreviewViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class CardUserPreviewViewHolder(
    private val binding: CardUserPreviewBinding,
    private val onCardUserPreviewInteractionListener: OnCardUserPreviewInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: User) {
        binding.apply {
            if (!user.avatar.isNullOrBlank()) {
                avatar.loadCircleCrop(user.avatar)
            }
            authorName.text = user.name

            removeMention.setOnClickListener {
                onCardUserPreviewInteractionListener.deleteFromList(user.id)
            }

            card.setOnClickListener {
                onCardUserPreviewInteractionListener.openUserProfile(user.id)
            }
        }
    }
}