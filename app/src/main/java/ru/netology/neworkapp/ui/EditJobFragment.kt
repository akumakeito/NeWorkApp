package ru.netology.neworkapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.FragmentEditJobBinding
import ru.netology.neworkapp.util.Utils
import ru.netology.neworkapp.viewmodel.UserProfileViewModel

@AndroidEntryPoint
class EditJobFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: UserProfileViewModel by activityViewModels()

        val binding = FragmentEditJobBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.edit_job)

        val job = viewModel.editedJob

        binding.company.setText(job?.value?.name)
        binding.position.setText(job?.value?.position)
        binding.addStartDate.setText(job?.value?.start)
        binding.addEndDate.setText(job?.value?.finish)
        binding.link.setText(job?.value?.link)




        binding.addStartDate.setOnClickListener {
            Utils.selectDateDialog(binding.addStartDate, requireContext())
            val startDate = binding.addStartDate.text.toString()
            viewModel.updateStartDate(startDate)
        }

        binding.addEndDate.setOnClickListener {
            Utils.selectDateDialog(binding.addEndDate, requireContext())
            val endDate = binding.addEndDate.text.toString()
            viewModel.updateEndDate(endDate)
        }


        binding.ok.setOnClickListener {
            if (binding.company.text.isNullOrBlank() || binding.position.text.isNullOrBlank() || binding.addStartDate.text.isNullOrBlank()) {
                Toast.makeText(
                    activity,
                    this.getString(R.string.field_cant_be_empty),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                viewModel.changeJobCompany(binding.company.text.toString())
                viewModel.changeJobPosition(binding.position.text.toString())
                viewModel.changeJobLink(binding.link.text.toString())
                viewModel.updateStartDate(binding.addStartDate.text.toString())
                viewModel.updateEndDate(binding.addEndDate.text.toString())
                viewModel.saveJob()
                Utils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }

        }

        binding.cancelButton.setOnClickListener {
            Utils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        return binding.root
    }
}