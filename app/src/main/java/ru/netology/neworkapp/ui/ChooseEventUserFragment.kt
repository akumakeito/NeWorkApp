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
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import ru.netology.neworkapp.R
//import ru.netology.neworkapp.adapter.ChooseUsersAdapter
//import ru.netology.neworkapp.adapter.ChooseUsersInteractionListener
//import ru.netology.neworkapp.databinding.FragmentChooseUserBinding
//import ru.netology.neworkapp.viewmodel.EventViewModel
//
//@ExperimentalCoroutinesApi
//@AndroidEntryPoint
//class ChooseEventUsersFragment : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        val binding = FragmentChooseUserBinding.inflate(inflater, container, false)
//
//        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.choose_users)
//
//        val viewModel: EventViewModel by activityViewModels()
//
//        viewModel.getUsers()
//
//        val adapter = ChooseUsersAdapter(object : ChooseUsersInteractionListener {
//            override fun check(id: Int) {
//                viewModel.check(id)
//            }
//
//            override fun unCheck(id: Int) {
//                viewModel.unCheck(id)
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
//        binding.add.setOnClickListener {
//            viewModel.addSpeakerIds()
//            findNavController().navigateUp()
//        }
//        return binding.root
//    }
//}