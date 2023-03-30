package ru.netology.neworkapp.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.neworkapp.R
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.databinding.FragmentRegistrationBinding
import ru.netology.neworkapp.dto.MediaUpload
import ru.netology.neworkapp.repository.AuthRepository
import ru.netology.neworkapp.util.Utils
import ru.netology.neworkapp.viewmodel.RegistrationLoginViewModel
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    @Inject
    lateinit var auth: AppAuth

    @Inject
    lateinit var repository: AuthRepository

    private val viewModel: RegistrationLoginViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        navController = findNavController()

        viewModel.isSignedIn.observe(viewLifecycleOwner) { isSignedId ->
            if (isSignedId) {
                binding.progress.visibility = View.GONE
                Utils.hideKeyboard(requireView())
                findNavController().popBackStack()
                viewModel.invalidateSignedInState()
            }

        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading

            if (state.error) {
                val msg = getString(R.string.error_loading)
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.ok), {})
                    .show()
                viewModel.invalidateDataState()
            }
        }


        binding.createAccountBtn.setOnClickListener {
            val login = binding.login.text.toString()
            val pass = binding.password.text.toString()
            val name = binding.username.text.toString()

            when {
                binding.login.text.isNullOrBlank() || binding.password.text.isNullOrBlank() -> {
                    Toast.makeText(
                        activity,
                        getString(R.string.field_cant_be_empty),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                binding.confirmPassword.text.toString() != pass -> {
                    Toast.makeText(
                        activity,
                        getString(R.string.password_doesnt_match),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                viewModel.photo.value?.file == null -> {
                    viewModel.register(login, pass, name)
                    Utils.hideKeyboard(requireView())
                }
                else -> {
                    val file = viewModel.photo.value?.file?.let { MediaUpload(it) }
                    file?.let { viewModel.registerWithPhoto(login, pass, name, it) }
                    Utils.hideKeyboard(requireView())
                }
            }


        }

        val handlePhotoResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                val resultCode = activityResult.resultCode
                val data = activityResult.data

                if (resultCode == Activity.RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    viewModel.changePhoto(fileUri, fileUri.toFile())
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Snackbar.make(
                        binding.root,
                        ImagePicker.getError(activityResult.data),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        binding.avatarContainer.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .galleryOnly()
                .galleryMimeTypes(arrayOf("image/png", "image/jpeg"))
                .createIntent { intent ->
                    handlePhotoResult.launch(intent)
                }
        }

        viewModel.photo.observe(viewLifecycleOwner) { photoModel ->
            if (photoModel.uri == null) {
                binding.setAvatar.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.avatar_placeholder,
                        null
                    )
                )
                return@observe
            }

            binding.setAvatar.setImageURI(photoModel.uri)

        }


        return binding.root

    }

    override fun onDestroyView() {
        viewModel.changePhoto(null, null)
        super.onDestroyView()
    }
}