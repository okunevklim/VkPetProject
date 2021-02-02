package com.example.vk.modules.news

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vk.R
import com.example.vk.adapters.PostAdapter
import com.example.vk.databinding.FragmentRecyclerBinding
import com.example.vk.fragments.AlertDialogFragment
import com.example.vk.interfaces.*
import com.example.vk.models.VkGroup
import com.example.vk.models.VkPost
import com.example.vk.models.VkUserInfo
import com.example.vk.utils.Constants
import com.example.vk.utils.Constants.REQUEST_CODE
import com.example.vk.utils.Helpers.shareImage
import com.example.vk.utils.InternetConnectionChecker.checkInternetConnection
import com.example.vk.utils.SnackBarHelper
import com.example.vk.viewmodels.PostsViewModel

class NewsFragment : Fragment(), OnPostClickListener,
        OnLikeButtonClickListener, OnShareButtonClickListener, INewsView, ItemSwipeActionsListener,
        OnHidePostListener {
    private lateinit var binding: FragmentRecyclerBinding
    private lateinit var presenter: INewsPresenter
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
        presenter = NewsPresenter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(SwipeItemTouchHelper(this))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        showShimmerLayout()
        postsViewModel = ViewModelProvider(requireActivity())[PostsViewModel::class.java]
        postsViewModel.getIsRefreshingLiveData().observe(viewLifecycleOwner, {
            if (it == 1) presenter.getUserNews(true)
        })
        checkInternetConnection(viewLifecycleOwner, { isConnected ->
            hideShimmerLayout()
            if (isConnected) {
                binding.swipeRefreshLayout.isEnabled = true
                hideErrorLayout()
                presenter.getUserNews(true)
            } else {
                binding.swipeRefreshLayout.isEnabled = false
                showErrorLayout()
                showSnackBar(
                        R.string.no_internet_title,
                        R.drawable.bg_rounded_error_snackbar,
                        R.drawable.ic_exclamation
                )
            }
        })
        binding.falseLayout.retryButton.setOnClickListener {
            checkInternetConnection(viewLifecycleOwner, { isConnected ->
                if (isConnected) {
                    binding.swipeRefreshLayout.isEnabled = true
                    hideErrorLayout()
                    hideShimmerLayout()
                    presenter.getUserNews(false)
                } else {
                    binding.swipeRefreshLayout.isEnabled = false
                    showErrorLayout()
                    showAlertDialog(R.string.alert_loading_posts)
                }
            })
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            presenter.getUserNews(true)
        }
    }

    override fun stopRefreshing() {
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun hideShimmerLayout() {
        binding.loadingLayout.root.stopShimmer()
        binding.layoutLoading.visibility = View.GONE
    }

    override fun showShimmerLayout() {
        binding.loadingLayout.root.startShimmer()
        binding.layoutLoading.visibility = View.VISIBLE
    }

    override fun showErrorLayout() {
        binding.recyclerView.visibility = View.GONE
        binding.falseLayout.root.visibility = View.VISIBLE
    }

    override fun hideErrorLayout() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.falseLayout.root.visibility = View.GONE
    }

    override fun onPostClick(
            vkPost: VkPost,
            name: String,
            photoURL: String,
            canPost: Int,
            numberOfComments: Long
    ) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.KEY_POST, vkPost)
        bundle.putString(Constants.KEY_POST_NAME, name)
        bundle.putString(Constants.KEY_POST_PHOTO, photoURL)
        bundle.putInt(Constants.KEY_CAN_POST_COMMENT, canPost)
        bundle.putLong(Constants.KEY_NUMBER_OF_COMMENTS, numberOfComments)
        val navController = requireActivity().findNavController(R.id.navHostFragment)
        navController.navigate(R.id.action_see_details, bundle)
    }

    override fun onShareButtonClick(bitmap: Bitmap) {
        shareImage(requireActivity(), bitmap)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        checkInternetConnection(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                presenter.getUserNews(true)
            }
        })
    }

    override fun handlePosts(updVkItems: List<VkPost>) {
        adapter.handleUpdates(updVkItems)
        hideErrorLayout()
        hideShimmerLayout()
    }

    override fun showAlertDialog(messageID: Int) {
        val alertDialog = AlertDialogFragment()
        val bundle = Bundle()
        bundle.putInt("alert_msg_res_id", messageID)
        alertDialog.arguments = bundle
        alertDialog.setTargetFragment(this, REQUEST_CODE)
        alertDialog.show(parentFragmentManager, "custom_dialog")
    }

    override fun handleError() {
        hideShimmerLayout()
        showErrorLayout()
    }

    override fun setNewsInfo(
            vkPosts: List<VkPost>,
            users: ArrayList<VkUserInfo>,
            groups: ArrayList<VkGroup>
    ) {
        adapter.setNewsInfo(users, groups)
        adapter.handleUpdates(vkPosts)
    }

    override fun onPostLikeClick(vkPost: VkPost) {
        val isLikedByUser = vkPost.likes?.userLikes == 1
        if (isLikedByUser) {
            presenter.deleteLike(vkPost)
        } else {
            presenter.setLike(vkPost)
        }
    }

    override fun onSwipeHide(position: Int) {
        onHidePost(posts[position])
    }

    override fun onSwipedLike(position: Int) {
        adapter.notifyItemChanged(position)
        onPostLikeClick(posts[position])
    }

    override fun showSnackBar(title: Int, backgroundColor: Int, image: Int) {
        requireActivity().runOnUiThread {
            SnackBarHelper.showSnackBar(
                    binding.swipeRefreshLayout,
                    getString(title),
                    backgroundColor,
                    image
            )
        }
    }

    override fun onHidePost(vkPost: VkPost) {
        presenter.hidePost(vkPost)
    }

    override fun handleGroups(groups: ArrayList<VkGroup>) {
        postsViewModel.handleGroups(groups)
    }

    override fun handleProfiles(profiles: ArrayList<VkUserInfo>) {
        postsViewModel.handleProfiles(profiles)
    }

    override fun handleLikedPosts(likedPosts: ArrayList<VkPost>) {
        postsViewModel.handleLikedPosts(likedPosts)
    }
}