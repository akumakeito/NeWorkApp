package ru.netology.neworkapp.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.neworkapp.R
import ru.netology.neworkapp.adapter.JobAdapter
import ru.netology.neworkapp.adapter.OnJobInteractionListener
import ru.netology.neworkapp.databinding.FragmentUserProfileBinding
import ru.netology.neworkapp.dto.Job
import ru.netology.neworkapp.util.loadCircleCrop
import ru.netology.neworkapp.viewmodel.AuthViewModel
import ru.netology.neworkapp.viewmodel.UserProfileViewModel

@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private val userProfileViewModel: UserProfileViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var navController : NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        navController = findNavController()

        authViewModel.authState.observe(viewLifecycleOwner) {
            (activity as AppCompatActivity?)?.supportActionBar?.title = "profile"
            if (!authViewModel.authenticated || arguments != null) {
                binding.addNewJob.visibility = View.GONE

            } else if (authViewModel.authenticated && arguments == null) {
                binding.addNewJob.visibility = View.VISIBLE
                val myId = userProfileViewModel.myId
                userProfileViewModel.getUserById(myId)
                userProfileViewModel.getMyJobs()

            }
        }


        val jobAdapter = JobAdapter(object : OnJobInteractionListener {
            override fun onLinkClick(url: String) {
                CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build()
                    .launchUrl(requireContext(), Uri.parse(url))
            }

            override fun onRemoveJob(job: Job) {
                userProfileViewModel.removeJobById(job.id)
            }

            override fun onEditJob(job: Job) {
                userProfileViewModel.editJob(job)
                val id = job.id
                val bundle = Bundle()
                bundle.putInt("jobId", id)
                findNavController().navigate(R.id.action_userProfileFragment_to_editJobFragment, bundle)
            }

        })

        binding.jobList.adapter = jobAdapter

        userProfileViewModel.jobData.observe(viewLifecycleOwner) {
            if (authViewModel.authenticated && arguments == null) {
                it.forEach { job ->
                    job.ownedByMe = true
                }
            }

            if (it.isEmpty()) {
                binding.jobList.visibility = View.GONE
                binding.noJobs.visibility = View.VISIBLE
            } else {
                binding.jobList.visibility = View.VISIBLE
                binding.noJobs.visibility = View.GONE
            }
            jobAdapter.submitList(it)

        }



        userProfileViewModel.userData.observe(viewLifecycleOwner) {
            (activity as AppCompatActivity?)?.supportActionBar?.title = it.name
            binding.name.text = it.name
            if (!it.avatar.isNullOrBlank()) {
                binding.avatar.loadCircleCrop(it.avatar)
            }
        }

        binding.addNewJob.setOnClickListener {
            navController.navigate(R.id.newJobFragment)
        }

        return binding.root
    }
}