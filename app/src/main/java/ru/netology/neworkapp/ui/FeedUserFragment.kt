//package ru.netology.neworkapp.ui
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import com.google.android.material.snackbar.Snackbar
//import dagger.hilt.android.AndroidEntryPoint
//import ru.netology.neworkapp.R
//import ru.netology.neworkapp.adapter.OnUserInteractionListener
//import ru.netology.neworkapp.adapter.UserAdapter
//import ru.netology.neworkapp.databinding.FeedUsersBinding
//import ru.netology.neworkapp.ui.UserProfileFragment.Companion.textArg
//import ru.netology.neworkapp.viewmodel.PostViewModel
//
//
//@AndroidEntryPoint
//class FeedUserFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val binding = FeedUsersBinding.inflate(inflater, container, false)
//
//        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.users)
//
//        val viewModel: PostViewModel by activityViewModels()
//
//        val adapter = UserAdapter(object : OnUserInteractionListener {
//            override fun onShowUserProfile(id: Int) {
//                val idAuthor = id.toString()
//                findNavController().navigate(
//                    R.id.userProfileFragment,
//                    Bundle().apply { textArg = idAuthor })
//            }
//        })
//        binding.list.adapter = adapter
//
//        viewModel.dataState.observe(viewLifecycleOwner) { state ->
//            if (state.loading) {
//                Snackbar.make(binding.root, R.string.server_error_message, Snackbar.LENGTH_SHORT)
//                    .show()
//            }
//        }
//
//        viewModel.usersList.observe(viewLifecycleOwner) {
//            val newUser = adapter.itemCount < it.size
//            adapter.submitList(it) {
//                if (newUser) {
//                    binding.list.smoothScrollToPosition(0)
//                }
//            }
//        }
//
//        return binding.root
//    }
//}