package ru.netology.neworkapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.neworkapp.databinding.LoadStateBinding

class PagingLoadStateAdapter(
    private val onPagingInteractionListener: OnPagingInteractionListener
) : LoadStateAdapter<PagingLoadStateAdapter.LoadStateViewHolder>() {
    interface OnPagingInteractionListener {
        fun onRetry()
    }


    class LoadStateViewHolder(
        private val binding : LoadStateBinding,
        private val onPagingInteractionListener: OnPagingInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState : LoadState) {
            binding.apply {
                progress.isVisible = loadState is LoadState.Loading
                retry.isVisible = loadState is LoadState.Error

                retry.setOnClickListener {
                    onPagingInteractionListener.onRetry()
                }

            }
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
       holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LoadStateViewHolder(
            LoadStateBinding.inflate(layoutInflater, parent, false),
            onPagingInteractionListener
        )
    }

}