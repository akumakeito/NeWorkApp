package ru.netology.neworkapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.CardJobBinding
import ru.netology.neworkapp.dto.Job
import ru.netology.neworkapp.util.Utils

interface OnJobInteractionListener {
    fun onLinkClick(url: String) {}
    fun onRemoveJob(job: Job) {}

    fun onEditJob(job: Job) {}
}

class JobAdapter (private val onInteractionListener: OnJobInteractionListener) : ListAdapter<Job,
        JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job)
    }
}

class JobViewHolder(
    private val binding: CardJobBinding,
    private val listener: OnJobInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        binding.apply {
            companyName.text = job.name
            position.text = job.position
            startDate.text = Utils.convertDate(job.start)
            if (job.finish.isNullOrBlank()) endDate.setText(R.string.till_now) else { endDate.text = Utils.convertDate(job.finish) }
            link.text = job.link
            link.setOnClickListener {
                listener.onLinkClick(link.text.toString())
            }
            delete.visibility = if (job.ownedByMe) View.VISIBLE else View.GONE
            delete.setOnClickListener {
                listener.onRemoveJob(job)
            }

            editJob.setOnClickListener {
                listener.onEditJob(job)
            }

        }
    }
}

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}