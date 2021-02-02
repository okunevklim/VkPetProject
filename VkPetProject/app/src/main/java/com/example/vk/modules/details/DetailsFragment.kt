package com.example.vk.modules.details

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vk.R
import com.example.vk.adapters.DetailsAdapter
import com.example.vk.databinding.FragmentDetailsBinding
import com.example.vk.fragments.AlertDialogFragment
import com.example.vk.interfaces.OnCommentLikeClickListener
import com.example.vk.interfaces.OnLikeButtonClickListener
import com.example.vk.interfaces.OnShareButtonClickListener
import com.example.vk.models.VkComment
import com.example.vk.models.VkPost
import com.example.vk.models.VkUserInfo
import com.example.vk.utils.Constants.KEY_CAN_POST_COMMENT
import com.example.vk.utils.Constants.KEY_NUMBER_OF_COMMENTS
import com.example.vk.utils.Constants.KEY_POST
import com.example.vk.utils.Constants.KEY_POST_NAME
import com.example.vk.utils.Constants.KEY_POST_PHOTO
import com.example.vk.utils.Constants.REQUEST_CODE
import com.example.vk.utils.Helpers
import com.example.vk.utils.Helpers.hideKeyboard
import com.example.vk.utils.InternetConnectionChecker
import com.example.vk.utils.SnackBarHelper
import com.example.vk.viewmodels.PostsViewModel

class DetailsFragment : Fragment(), OnCommentLikeClickListener,
    OnShareButtonClickListener, OnLikeButtonClickListener, IDetailsView {
    private val comments = arrayListOf<VkComment>()
    private lateinit var adapter: DetailsAdapter
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var presenter: IDetailsPresenter
    private lateinit var postsViewModel: PostsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = DetailsPresenter()
        postsViewModel = ViewModelProvider(requireActivity())[PostsViewModel::class.java]
        val vkPost = requireArguments().getSerializable(KEY_POST) as VkPost
        val name = requireArguments().getString(KEY_POST_NAME) as String
        val photoURL = requireArguments().getString(KEY_POST_PHOTO) as String
        val canPost = requireArguments().getInt(KEY_CAN_POST_COMMENT)
        val numberOfComments = requireArguments().getLong(KEY_NUMBER_OF_COMMENTS)
        adapter = DetailsAdapter(vkPost, comments, this, this, this, name, photoURL)
        setupRecycler()
        binding.inputComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.isNotBlank()) {
                    activateSendButton()
                } else {
                    deactivateSendButton()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        binding.sendComment.setOnClickListener {
            if (binding.inputComment.length() != 0) {
                presenter.postComment(vkPost)
                binding.inputComment.clearFocus()
                hideKeyboard(requireContext(), binding.inputComment)
            }
        }

        InternetConnectionChecker.checkInternetConnection(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                if (canPost == 1 || (canPost == 0 && numberOfComments > 0)) {
                    presenter.getVkComments(vkPost)
                }
            } else {
                showSnackBar()
            }
        })
        if (canPost == 1) {
            showInputField()
        } else {
            hideInputField()
        }
    }

    override fun isShimmerLoading(isLoading: Int) {
        adapter.setShimmerLoading(isLoading)
    }

    override fun setNumberOfComments(numberOfComments: Long) {
        adapter.setAmountOfComments(numberOfComments)
    }

    override fun setupRecycler() {
        binding.commentRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.commentRecycler.adapter = adapter
    }

    override fun hideInputField() {
        binding.inputComment.visibility = View.GONE
        binding.sendComment.visibility = View.GONE
    }

    override fun updatePosition(position: Int) {
        adapter.notifyItemChanged(position)
    }

    override fun showInputField() {
        binding.inputComment.visibility = View.VISIBLE
        binding.sendComment.visibility = View.VISIBLE
    }

    override fun activateSendButton() {
        binding.sendComment.setImageResource(R.drawable.ic_fill_send)
        binding.sendComment.isEnabled = true
    }

    override fun deactivateSendButton() {
        binding.sendComment.setImageResource(R.drawable.ic_send)
        binding.sendComment.isEnabled = false
    }

    override fun showSnackBar() {
        requireActivity().runOnUiThread {
            SnackBarHelper.showSnackBar(
                binding.detailsLayout,
                getString(R.string.no_internet_title),
                R.drawable.bg_rounded_error_snackbar,
                R.drawable.ic_exclamation
            )
        }
    }

    override fun handleIsRefreshing(value: Int) {
        postsViewModel.handleIsRefreshing(value)
    }

    override fun handleLike(vkPost: VkPost) {
        postsViewModel.handleLike(vkPost)
    }

    override fun getInputTextToString(): String? {
        return binding.inputComment.text.toString()
    }

    override fun clearInputText() {
        binding.inputComment.text.clear()
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun setVkComments(
        vkComments: ArrayList<VkComment>,
        commentInfo: ArrayList<VkUserInfo>
    ) {
        adapter.setVkComments(vkComments, commentInfo)
    }

    override fun showSuccessfulCommentToast() {
        Toast.makeText(
            requireContext(), context?.getString(R.string.successful_comment_toast),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showAlertDialog(message: Int) {
        val alertDialog = AlertDialogFragment()
        val bundle = Bundle()
        bundle.putInt("alert_msg_res_id", message)
        alertDialog.arguments = bundle
        alertDialog.setTargetFragment(this, REQUEST_CODE)
        alertDialog.show(parentFragmentManager, "custom_dialog")
    }

    override fun showDetailsProgressBar() {
        binding.detailsProgressBar.visibility = View.VISIBLE
    }

    override fun hideDetailsProgressBar() {
        binding.detailsProgressBar.visibility = View.GONE
    }

    override fun onShareButtonClick(bitmap: Bitmap) {
        Helpers.shareImage(requireActivity(), bitmap)
    }

    override fun updateAmountOfLikes(likes: Long, userLikes: Int) {
        adapter.setAmountOfLikes(likes, userLikes)
    }

    override fun onCommentLikeClick(vkComment: VkComment, vkPost: VkPost) {
        if (vkComment.likes?.userLikes == 1) {
            presenter.deleteLike("comment", vkPost, vkComment.id ?: 0)
        } else {
            presenter.setLike("comment", vkPost, vkComment.id ?: 0)
        }
    }

    override fun onPostLikeClick(vkPost: VkPost) {
        val isLikedByUser = vkPost.likes?.userLikes == 1

        if (isLikedByUser) {
            presenter.deleteLike("post", vkPost, vkPost.id ?: 0)
        } else {
            presenter.setLike("post", vkPost, vkPost.id ?: 0)
        }
    }
}