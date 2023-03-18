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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
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
import ru.netology.neworkapp.adapter.CardUserPreviewAdapter
import ru.netology.neworkapp.adapter.OnCardUserPreviewInteractionListener
import ru.netology.neworkapp.databinding.FragmentNewPostBinding
import ru.netology.neworkapp.dto.AttachmentType
import ru.netology.neworkapp.ui.FeedPostFragment.Companion.intArg
import ru.netology.neworkapp.util.Utils
import ru.netology.neworkapp.viewmodel.PostViewModel
import java.io.File

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private val viewModel : PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.create_post)

        val binding = FragmentNewPostBinding.inflate(inflater, container,false)

        if (arguments?.intArg != null) {
            val id = arguments?.intArg
            id?.let { viewModel.getPostById(it) }
        }

        val adapter = CardUserPreviewAdapter(object : OnCardUserPreviewInteractionListener{
            override fun openUserProfile(id: Int) {
                findNavController().navigate(
                    R.id.userProfileFragment,
                    Bundle().apply { intArg = id }
                )
            }

            override fun deleteFromList(id: Int) {
                viewModel.uncheckUser(id)
                viewModel.updateMentionsIds()
            }

        })


        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when(it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        val uri : Uri? = it.data?.data
                        val resultFile = uri?.toFile()
                        val file = MultipartBody.Part.createFormData(
                            "file", resultFile?.name, resultFile!!.asRequestBody()
                        )
                        viewModel.changeMedia(uri, resultFile, AttachmentType.IMAGE)
                        viewModel.addMediaToPost(AttachmentType.IMAGE, file)
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
                        "image/jpeg",
                        "image/jpg"
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

        val pickVideoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                val resultCode = activityResult.resultCode
                val data = activityResult.data

                if (resultCode == Activity.RESULT_OK) {
                    val selectedVideoUri = data?.data!!
                    val selectedVideoPath =
                        Utils.getVideoPathFromUri(selectedVideoUri, requireActivity())
                    if (selectedVideoPath != null) {
                        val resultFile = File(selectedVideoPath)
                        val file = MultipartBody.Part.createFormData(
                            "file", resultFile.name, resultFile.asRequestBody()
                        )
                        viewModel.changeMedia(selectedVideoUri, resultFile, AttachmentType.VIDEO)
                        viewModel.addMediaToPost(AttachmentType.VIDEO, file)
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
                    val selectedAudioUri = data?.data!!
                    val selectedAudioPath =
                        Utils.getAudioPathFromUri(selectedAudioUri, requireActivity())
                    if (selectedAudioPath != null) {
                        val resultFile = File(selectedAudioPath)
                        val file = MultipartBody.Part.createFormData(
                            "file", resultFile.name, resultFile.asRequestBody()
                        )
                        viewModel.changeMedia(selectedAudioUri, resultFile, AttachmentType.AUDIO)
                        viewModel.addMediaToPost(AttachmentType.AUDIO, file)
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

        viewModel.media.observe(viewLifecycleOwner)
        { mediaModel ->
            if (mediaModel.uri == null) {
                binding.mediaContainer.visibility = View.GONE
                return@observe
            }
            when (mediaModel.type) {
                AttachmentType.IMAGE -> {
                    binding.mediaContainer.visibility = View.VISIBLE
                    binding.photo.setImageURI(mediaModel.uri)
                }
                AttachmentType.VIDEO -> {
                    binding.mediaContainer.visibility = View.VISIBLE
                    binding.photo.setImageResource(R.drawable.pick_video_ic)
                }
                AttachmentType.AUDIO -> {
                    binding.mediaContainer.visibility = View.VISIBLE
                    binding.photo.setImageResource(R.drawable.audiotrack_ic)
                }
                null -> return@observe
            }
        }

        binding.addMentionBtn.setOnClickListener {
            findNavController().navigate(R.id.choosePostUsersFragment)
        }
        binding.link.setOnClickListener {
            val link: String = binding.link.text.toString()
            viewModel.addLink(link)
        }

        binding.mentionedUsers.adapter = adapter
        viewModel.mentionsData.observe(viewLifecycleOwner)
        {
            if (it.isEmpty()) {
                binding.mentionedUsersScroll.visibility = View.GONE
            } else {
                adapter.submitList(it)
                binding.mentionedUsersScroll.visibility = View.VISIBLE
            }
        }

        viewModel.editedPost.observe(viewLifecycleOwner)
        {
            it?.content.let(binding.editText::setText)
            it?.link.let(binding.link::setText)
            if (it?.attachment != null) {
                binding.mediaContainer.visibility = View.VISIBLE
            } else {
                binding.mediaContainer.visibility = View.GONE
            }
        }

        binding.removeMedia.setOnClickListener {
            viewModel.changeMedia(null, null, null)
            viewModel.removeMedia()
            binding.mediaContainer.visibility = View.GONE
        }

        binding.save.setOnClickListener {
            val content = binding.editText.text.toString()
            if (content == "") {
                Snackbar.make(binding.root, R.string.content_cant_be_empty, Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.savePost(content)
            }

            viewModel.postCreated.observe(viewLifecycleOwner) {
                findNavController().navigate(R.id.postFragment)
            }

            viewModel.dataState.observe(viewLifecycleOwner) { state ->
                if (state.error) {
                    Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }

        return binding.root
    }
}