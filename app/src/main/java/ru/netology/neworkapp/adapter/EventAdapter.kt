package ru.netology.neworkapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.content.res.AppCompatResources
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.CardEventBinding
import ru.netology.neworkapp.dto.AttachmentType
import ru.netology.neworkapp.dto.Event
import ru.netology.neworkapp.dto.EventType
import ru.netology.neworkapp.util.Utils
import ru.netology.neworkapp.util.loadCircleCrop
import ru.netology.neworkapp.util.loadImage


interface OnEventInteractionListener {
    fun onLike(event: Event) {}
//    fun onParticipateInEvent(event: Event) {}
    fun onEdit(event: Event) {}
    fun onRemove(event: Event) {}

    fun onLinkClick(url: String) {}
//    fun loadEventUsersList(event: Event) {}
}

class EventAdapter(
    private val onInteractionListener: OnEventInteractionListener,
) : PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position) ?: return
        holder.bind(event)
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val listener: OnEventInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    private val parentView = binding.root
    val videoThumbnail = binding.videoThumbnail
    val videoContainer = binding.videoContainer
    val videoProgressBar = binding.videoProgressBar
    var videoPreview: MediaItem? = null
    val videoPlayIcon: ImageView = binding.videoButton

    fun bind(event: Event) {
        parentView.tag = this
        binding.apply {
            when (event.type) {
                EventType.OFFLINE -> type.setImageResource(R.drawable.offline_ic)
                EventType.ONLINE -> type.setImageResource(R.drawable.online_ic)
            }
            if (event.authorAvatar != null) {
                avatar.loadCircleCrop(event.authorAvatar)
                }


            if (event.attachment?.url != "") {
                when (event.attachment?.type) {
                    AttachmentType.IMAGE -> {
                        videoPreview = null
                        image.visibility = View.VISIBLE
                        videoContainer.visibility = View.GONE
                        image.loadImage(event.attachment.url)
                    }
                    AttachmentType.VIDEO -> {
                        image.visibility = View.GONE
                        videoContainer.visibility = View.VISIBLE
                        videoPreview = MediaItem.fromUri(event.attachment.url)
                        videoThumbnail.loadImage(event.attachment.url)
                    }
                    AttachmentType.AUDIO -> {
                        image.visibility = View.GONE
                        videoContainer.visibility = View.VISIBLE
                        videoPreview = MediaItem.fromUri(event.attachment.url)
                        videoThumbnail.setImageDrawable(
                            AppCompatResources.getDrawable(
                                itemView.context,
                                R.drawable.audiotrack_ic
                            )
                        )
                    }
                    null -> {
                        videoContainer.visibility = View.GONE
                        image.visibility = View.GONE
                        videoPreview = null
                    }
                }
            }

            author.text = event.author
            published.text = Utils.formatMillisToDateTimeString(event.published.toEpochMilli())
            dateTime.text = Utils.convertDateAndTime(event.datetime)
            content.text = event.content
            like.isChecked = event.likedByMe
            like.text = "${event.likeOwnerIds.size}"
            link.text = event.link

            link.setOnClickListener {
                listener.onLinkClick(link.text.toString())
            }

            menu.visibility = if (event.ownedByMe) View.VISIBLE else View.GONE


            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.list_item_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(event)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(event)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                listener.onLike(event)
            }
        }
    }
}

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }

}
