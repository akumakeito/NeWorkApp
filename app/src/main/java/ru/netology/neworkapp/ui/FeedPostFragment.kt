package ru.netology.neworkapp.ui

import ru.netology.neworkapp.util.IntArg
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import ru.netology.neworkapp.adapter.MediaRecyclerView
import ru.netology.neworkapp.adapter.OnPostInteractionListener
import ru.netology.neworkapp.adapter.PagingLoadStateAdapter
import ru.netology.neworkapp.adapter.PostAdapter
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.viewmodel.AuthViewModel
import ru.netology.neworkapp.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.paging.LoadState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.FeedPostsBinding


@AndroidEntryPoint
class FeedPostFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var navController: NavController

    companion object {
        var Bundle.intArg: Int by IntArg
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FeedPostsBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.posts)

        Log.d(
            "auth",
            "id ${authViewModel.authState.value?.id} token  ${authViewModel.authState.value?.id}  authenticated ${authViewModel.authenticated}"
        )

        viewLifecycleOwner.lifecycle.addObserver(
            object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> binding.postsList.createPlayer()
                        Lifecycle.Event.ON_PAUSE,
                        Lifecycle.Event.ON_STOP -> binding.postsList.releasePlayer()
                        Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
                        else -> Unit
                    }
                }

            }
        )


        navController = findNavController()

        val currentBackStackEntry = navController.currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
            .observe(currentBackStackEntry) { success ->
                if (!success) {
                    val startDestination = navController.graph.startDestinationId
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build()
                    navController.navigate(startDestination, null, navOptions)
                }
            }


        val adapter = PostAdapter(object : OnPostInteractionListener {
            override fun onAvatarClick(post: Post) {
                navController.navigate(R.id.action_postFragment_to_userProfileFragment)
            }

            override fun onLinkClick(url: String) {
                CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build()
                    .launchUrl(requireContext(), Uri.parse(url))
            }

            override fun onLike(post: Post) {
                if (authViewModel.authenticated) {
                    if (!post.likedByMe) viewModel.likePostById(post.id)
                    else viewModel.unlikePostById(post.id)
                } else {
                    Snackbar.make(
                        binding.root, R.string
                            .login_to_continue, Snackbar.LENGTH_SHORT
                    )
                        .setAction(
                            R.string.login,
                            { navController.navigate(R.id.action_postFragment_to_loginFragment) })
                        .show()

                }
            }

            override fun onEdit(post: Post) {
                viewModel.editPost(post)
                val text = post.content
                val bundle = Bundle()
                bundle.putString("editedText", text)
                findNavController().navigate(R.id.action_postFragment_to_editPostFragment, bundle)
            }

            override fun onRemove(post: Post) {
                viewModel.removePostById(post.id)
            }
        })

        binding.postsList.adapter = adapter.withLoadStateHeaderAndFooter(
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

            })
        )

        binding.postsList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            if (state.error) {
                Snackbar.make(binding.root, R.string.loading_error, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.retry, { adapter.refresh() })
                    .show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest { adapter.submitData(it) }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }

        return binding.root
    }
}
