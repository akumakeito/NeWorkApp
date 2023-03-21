package ru.netology.neworkapp.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.neworkapp.R
import ru.netology.neworkapp.adapter.CardUserPreviewAdapter
import ru.netology.neworkapp.adapter.OnCardUserPreviewInteractionListener
import ru.netology.neworkapp.databinding.EditPostBinding
import ru.netology.neworkapp.databinding.FragmentEditEventBinding
import ru.netology.neworkapp.databinding.FragmentNewEventBinding
import ru.netology.neworkapp.dto.AttachmentType
import ru.netology.neworkapp.dto.EventType
import ru.netology.neworkapp.ui.EditPostFragment.Companion.edit
import ru.netology.neworkapp.ui.FeedEventFragment.Companion.intArg
import ru.netology.neworkapp.util.StringArg
import ru.netology.neworkapp.util.Utils
import ru.netology.neworkapp.viewmodel.EventViewModel
import java.io.File

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditEventFragment : Fragment() {
    private val viewModel: EventViewModel by activityViewModels()
    companion object {
        var Bundle.edit: String? by StringArg

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentEditEventBinding.inflate(inflater, container,false)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.edit_event)

        arguments?.edit?.let(binding.edit::setText)
        binding.edit.setText(arguments?.getString("editedText"))


        binding.ok.setOnClickListener {
            if (binding.edit.text.isNullOrBlank()) {
                Toast.makeText(
                    activity,
                    this.getString(R.string.field_cant_be_empty),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                viewModel.changeContent(binding.edit.text.toString())
                viewModel.saveEvent()
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