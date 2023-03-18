package ru.netology.neworkapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.neworkapp.R
import ru.netology.neworkapp.adapter.EventAdapter
import ru.netology.neworkapp.adapter.EventRecyclerView
import ru.netology.neworkapp.adapter.OnEventInteractionListener
import ru.netology.neworkapp.adapter.PagingLoadStateAdapter
import ru.netology.neworkapp.databinding.FragmentFeedEventsBinding
import ru.netology.neworkapp.dto.AttachmentType
import ru.netology.neworkapp.dto.Event
import ru.netology.neworkapp.ui.FeedPostFragment.Companion.intArg
import ru.netology.neworkapp.ui.UserProfileFragment.Companion.textArg
import ru.netology.neworkapp.util.IntArg
import ru.netology.neworkapp.viewmodel.AuthViewModel
import ru.netology.neworkapp.viewmodel.EventViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedEventFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModels()
    private val viewModel: EventViewModel by activityViewModels()
    lateinit var mediaRecyclerView: EventRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFeedEventsBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.events)


        mediaRecyclerView = binding.list

        val adapter = EventAdapter(object : OnEventInteractionListener {
            override fun onLike(event: Event) {
                if (authViewModel.authenticated) {
                    if (!event.likedByMe) viewModel.likeEventById(event.id) else viewModel.dislikeEventById(
                        event.id
                    )
                } else {
                    Snackbar.make(binding.root, R.string.login_to_continue, Snackbar.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.loginFragment)
                }
            }

            override fun onEdit(event: Event) {
                findNavController().navigate(
                    R.id.newEventFragment,
                    Bundle().apply { intArg = event.id })
            }

            override fun onRemove(event: Event) {
                viewModel.removeEventById(event.id)
            }

            override fun loadEventUsersList(event: Event) {
                if (authViewModel.authenticated) {
                    if (event.speakerIds.isEmpty()) {
                        return
                    } else {
                        viewModel.getEventUsersList(event)
                        findNavController().navigate(R.id.choosePostUsersFragment)
                    }
                } else {
                    Snackbar.make(binding.root, R.string.login_to_continue, Snackbar.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.loginFragment)
                }
            }

            override fun onParticipateInEvent(event: Event) {
                if (authViewModel.authenticated) {
                    if (!event.participatedByMe) viewModel.participateInEvent(event.id) else viewModel.quitParticipateInEvent(
                        event.id
                    )
                } else {
                    Snackbar.make(binding.root, R.string.login_to_continue, Snackbar.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.loginFragment)
                }
            }

        })

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(object :
                PagingLoadStateAdapter.OnPagingInteractionListener {
                override fun onRetry() {
                    adapter.retry()
                }
            }),
            footer = PagingLoadStateAdapter(object :
                PagingLoadStateAdapter.OnPagingInteractionListener {
                override fun onRetry() {
                    adapter.retry()
                }
            }),
        )

        binding.list.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                    .show()
            }
            if (state.loading) {
                Snackbar.make(binding.root, R.string.server_error_message, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (positionStart == 0) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        })

        adapter.loadStateFlow
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing = it.refresh is LoadState.Loading
            }
        }

        return binding.root
    }

    companion object {
        var Bundle.intArg: Int by IntArg
    }

    override fun onResume() {
        if (::mediaRecyclerView.isInitialized) mediaRecyclerView.createPlayer()
        super.onResume()
    }

    override fun onPause() {
        if (::mediaRecyclerView.isInitialized) mediaRecyclerView.releasePlayer()
        super.onPause()
    }

    override fun onStop() {
        if (::mediaRecyclerView.isInitialized) mediaRecyclerView.releasePlayer()
        super.onStop()
    }
}
