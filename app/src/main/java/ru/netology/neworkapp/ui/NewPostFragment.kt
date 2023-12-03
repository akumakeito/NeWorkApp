package ru.netology.neworkapp.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.FragmentNewPostBinding
import ru.netology.neworkapp.dto.AttachmentType
import ru.netology.neworkapp.ui.FeedPostFragment.Companion.intArg
import ru.netology.neworkapp.util.Utils
import ru.netology.neworkapp.viewmodel.PostViewModel
import java.io.File

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentNewPostBinding.inflate(inflater, container, false)

        if (arguments?.intArg != null) {
            val id = arguments?.intArg
            id?.let { viewModel.getPostById(it) }
        }


        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changeMedia(uri, uri?.toFile(), AttachmentType.IMAGE)
                    }
                }
            }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg"
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.removeMedia.setOnClickListener {
            viewModel.changeMedia(null, null, null)
            binding.mediaContainer.visibility = View.GONE
        }
        val pickVideoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                val resultCode = activityResult.resultCode
                val data = activityResult.data

                if (resultCode == Activity.RESULT_OK) {
                    val selectedVideoUri = requireNotNull(data?.data)
                    val selectedVideoPath =
                        Utils.getVideoPathFromUri(selectedVideoUri, requireActivity())
                    if (selectedVideoPath != null) {
                        val resultFile = File(selectedVideoPath)

                        viewModel.changeMedia(selectedVideoUri, resultFile, AttachmentType.VIDEO)
                    }
                } else {
                    Snackbar.make(binding.root, R.string.video_container, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }

        binding.pickVideo.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            )
            pickVideoLauncher.launch(intent)
        }

        val pickAudioLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                val resultCode = activityResult.resultCode
                val data = activityResult.data

                if (resultCode == Activity.RESULT_OK) {
                    val selectedAudioUri = requireNotNull(data?.data)
                    val selectedAudioPath =
                        Utils.getAudioPathFromUri(selectedAudioUri, requireActivity())
                    if (selectedAudioPath != null) {
                        val resultFile = File(selectedAudioPath)
                        val file = MultipartBody.Part.createFormData(
                            "file", resultFile.name, resultFile.asRequestBody()
                        )
                        viewModel.changeMedia(selectedAudioUri, resultFile, AttachmentType.AUDIO)
                    }
                }
            }

        binding.pickAudio.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            )
            pickAudioLauncher.launch(intent)
        }



        viewModel.media.observe(viewLifecycleOwner) {
            if (it.uri?.toString().isNullOrBlank()) {
                binding.mediaContainer.visibility = View.GONE
                return@observe
            } else {
                binding.mediaContainer.visibility = View.VISIBLE


                when (it.type) {
                    AttachmentType.IMAGE -> {
                        binding.photo.setImageURI(it.uri)
                    }
                    AttachmentType.VIDEO -> {
                        binding.photo.setImageResource(R.drawable.ic_video_24)
                    }
                    AttachmentType.AUDIO -> {
                        binding.photo.setImageResource(R.drawable.audiotrack_ic)
                    }
                    null -> return@observe
                }
            }
        }


        binding.addLinkBtn.setOnClickListener {
            val link: String = binding.link.text.toString()
            viewModel.addLink(link)
        }


        binding.save.setOnClickListener {
            val text = binding.editText.text.toString()
            if (text.isBlank()) {
                Snackbar.make(binding.root,R.string.content_can_t_be_empty, Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.changeContent(text)
                viewModel.savePost()
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT)
                    .show()
            }

            binding.progress.isVisible = state.loading


        }


        return binding.root
    }
}