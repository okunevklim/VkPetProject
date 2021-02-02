package com.example.vk.modules.liked

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vk.R
import com.example.vk.adapters.PostAdapter
import com.example.vk.databinding.FragmentRecyclerBinding
import com.example.vk.fragments.AlertDialogFragment
import com.example.vk.interfaces.OnHidePostListener
import com.example.vk.interfaces.OnLikeButtonClickListener
import com.example.vk.interfaces.OnPostClickListener
import com.example.vk.interfaces.OnShareButtonClickListener
import com.example.vk.models.VkPost
import com.example.vk.utils.Constants
import com.example.vk.utils.Constants.KEY_CAN_POST_COMMENT
import com.example.vk.utils.Constants.KEY_POST
import com.example.vk.utils.Constants.KEY_POST_NAME
import com.example.vk.utils.Constants.KEY_POST_PHOTO
import com.example.vk.utils.Constants.REQUEST_CODE
import com.example.vk.utils.Helpers
import com.example.vk.viewmodels.PostsViewModel

class LikedFragment : Fragment(), OnPostClickListener,
        OnLikeButtonClickListener, OnShareButtonClickListener, ILikedView, OnHidePostListener {

    private lateinit var binding: FragmentRecyclerBinding
    private lateinit var presenter: ILikedPresenter
    private lateinit var postsViewModel: PostsViewModel
    private val posts = arrayListOf<VkPost>()
    private val adapter = PostAdapter(posts, this, this, this)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecyclerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefreshLayout.isEnabled = false
        presenter = LikedPresenter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.falseLayout.retryButton.visibility = View.GONE
        postsViewModel = ViewModelProvider(requireActivity())[PostsViewModel::class.java]
        postsViewModel.getLikedPostsLiveData().observe(viewLifecycleOwner, {
            adapter.handleUpdates(it)
        })
        postsViewModel.getLikedPostsProfileInfoLiveData().observe(viewLifecycleOwner, {
            adapter.setNewsInfo(it, null)
        })
        postsViewModel.getLikedPostsGroupInfoLiveData().observe(viewLifecycleOwner, {
            adapter.setNewsInfo(null, it)
        })
    }

    override fun showShimmerLayout() {
        binding.loadingLayout.root.startShimmer()
        binding.layoutLoading.visibility = View.VISIBLE
    }

    override fun hideShimmerLayout() {
        binding.loadingLayout.root.stopShimmer()
        binding.layoutLoading.visibility = View.GONE
    }

    override fun showErrorLayout() {
        binding.recyclerView.visibility = View.GONE
        binding.falseLayout.root.visibility = View.VISIBLE
    }

    override fun hideErrorLayout() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.falseLayout.root.visibility = View.GONE
    }

    override fun showAlertDialog(messageID: Int) {
        val alertDialog = AlertDialogFragment()
        val bundle = Bundle()
        bundle.putInt("alert_msg_res_id", messageID)
        alertDialog.arguments = bundle
        alertDialog.setTargetFragment(this, REQUEST_CODE)
        alertDialog.show(parentFragmentManager, "custom_dialog")
    }

    override fun onPostClick(
            vkPost: VkPost,
            name: String,
            photoURL: String,
            canPost: Int,
            numberOfComments: Long
    ) {
        val bundle = Bundle()
        bundle.putSerializable(KEY_POST, vkPost)
        bundle.putString(KEY_POST_NAME, name)
        bundle.putString(KEY_POST_PHOTO, photoURL)
        bundle.putInt(KEY_CAN_POST_COMMENT, canPost)
        bundle.putLong(Constants.KEY_NUMBER_OF_COMMENTS, numberOfComments)
        val navController = requireActivity().findNavController(R.id.navHostFragment)
        navController.navigate(R.id.detailsFragment, bundle)
    }

    override fun onShareButtonClick(bitmap: Bitmap) {
        Helpers.shareImage(requireActivity(), bitmap)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onPostLikeClick(vkPost: VkPost) {
        val isLikedByUser = vkPost.likes?.userLikes == 1
        if (isLikedByUser) {
            presenter.deleteLike(vkPost)
        } else {
            presenter.setLike(vkPost)
        }
    }

    override fun onHidePost(vkPost: VkPost) {
        postsViewModel.handleLike(vkPost)
    }

    override fun handleLike(vkPost: VkPost) {
        postsViewModel.handleLike(vkPost)
    }
}