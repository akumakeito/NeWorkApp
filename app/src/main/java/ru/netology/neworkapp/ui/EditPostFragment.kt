package ru.netology.neworkapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.EditPostBinding
import ru.netology.neworkapp.util.StringArg
import ru.netology.neworkapp.util.Utils
import ru.netology.neworkapp.viewmodel.PostViewModel

@AndroidEntryPoint
class EditPostFragment : Fragment() {
    companion object {
        var Bundle.edit: String? by StringArg

    }

    private val viewModel: PostViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = EditPostBinding.inflate(inflater, container, false)

        arguments?.edit?.let(binding.editText::setText)
        binding.editText.setText(arguments?.getString("editedText"))


        binding.ok.setOnClickListener {
            if (binding.editText.text.isNullOrBlank()) {
                Toast.makeText(
                    activity,
                    this.getString(R.string.field_cant_be_empty),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                viewModel.changeContent(binding.editText.text.toString())
                viewModel.savePost()
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
