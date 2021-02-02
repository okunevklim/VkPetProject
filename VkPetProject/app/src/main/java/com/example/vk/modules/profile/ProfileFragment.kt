package com.example.vk.modules.profile

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vk.R
import com.example.vk.adapters.ProfileAdapter
import com.example.vk.databinding.FragmentProfileBinding
import com.example.vk.fragments.AlertDialogFragment
import com.example.vk.interfaces.*
import com.example.vk.models.VkCityName
import com.example.vk.models.VkPost
import com.example.vk.models.VkUserInfo
import com.example.vk.utils.Constants
import com.example.vk.utils.Constants.REQUEST_CODE
import com.example.vk.utils.Helpers
import com.example.vk.utils.InternetConnectionChecker
import com.example.vk.utils.SnackBarHelper
import com.example.vk.viewmodels.PostsViewModel

class ProfileFragment : Fragment(), OnPostClickListener,
        OnEditTextClickListener, OnLikeButtonClickListener, OnShareButtonClickListener,
        OnDeletePostListener, IProfileView, ItemSwipeActionsListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var presenter: IProfilePresenter
    private lateinit var postsViewModel: PostsViewModel
    private val posts = arrayListOf<VkPost>()
    private val adapter = ProfileAdapter(posts, this, this, this, this)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = ProfilePresenter()
        binding.profileRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.profileRecycler.adapter = adapter
        checkInternetConnection()
        postsViewModel = ViewModelProvider(requireActivity())[PostsViewModel::class.java]
        val itemTouchHelper = ItemTouchHelper(SwipeItemTouchHelper(this))
        itemTouchHelper.attachToRecyclerView(binding.profileRecycler)
        showShimmerLayout()
        postsViewModel.getIsRefreshingLiveData().observe(viewLifecycleOwner, {
            if (it == 1) {
                presenter.getUserPosts(true)
            }
        })
        binding.falseLayout.retryButton.setOnClickListener {
            checkInternetConnection()
        }
    }

    override fun handleUserInfo(userInfo: VkUserInfo) {
        adapter.setUserInfo(userInfo)
        if (userInfo.career != null) {
            InternetConnectionChecker.checkInternetConnection(
                    viewLifecycleOwner,
                    { isConnected ->
                        if (isConnected) {
                            binding.profileDomain.visibility = View.VISIBLE
                            presenter.loadCitiesByID(userInfo.career.map { work ->
                                work.cityID ?: 0
                            })
                            userInfo.career.map { work -> work.cityID ?: 0 }.let {
                                presenter.loadCitiesByID(it)
                            }
                        } else {
                            binding.profileDomain.visibility = View.GONE
                        }
                    })
        }
        if (userInfo.domain.isNullOrEmpty()) {
            binding.profileDomain.visibility = View.GONE
        } else {
            binding.profileDomain.text = userInfo.domain
        }
    }

    override fun checkInternetConnection() {
        InternetConnectionChecker.checkInternetConnection(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                binding.profileDomain.visibility = View.VISIBLE
                adapter.isConnected = true
                presenter.loadUserInfo()
                presenter.getUserPosts(false)
                hideErrorLayout()
            } else {
                showAlertDialog(R.string.alert_loading_posts)
                adapter.isConnected = false
                binding.profileDomain.visibility = View.GONE
                showErrorLayout()
            }
        })
    }

    override fun setCities(cities: List<VkCityName>) {
        adapter.setCareerCities(cities)
    }

    override fun hideShimmerLayout() {
        binding.loadingLayout.root.stopShimmer()
        binding.layoutToLoad.visibility = View.GONE
    }

    override fun showShimmerLayout() {
        binding.loadingLayout.root.startShimmer()
        binding.layoutToLoad.visibility = View.VISIBLE
    }

    override fun showErrorLayout() {
        binding.profileRecycler.visibility = View.GONE
        binding.falseLayout.root.visibility = View.VISIBLE
    }

    override fun hideErrorLayout() {
        binding.profileRecycler.visibility = View.VISIBLE
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
        navController.navigate(R.id.detailsFragment, bundle)
    }

    override fun onShareButtonClick(bitmap: Bitmap) {
        Helpers.shareImage(requireActivity(), bitmap)
    }

    override fun showAlertDialog(messageID: Int) {
        val alertDialog = AlertDialogFragment()
        val bundle = Bundle()
        bundle.putInt("alert_msg_res_id", messageID)
        alertDialog.arguments = bundle
        alertDialog.setTargetFragment(this, REQUEST_CODE)
        alertDialog.show(parentFragmentManager, "custom_dialog")
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onResume() {
        super.onResume()
        InternetConnectionChecker.checkInternetConnection(viewLifecycleOwner, { isConnected ->
            if (isConnected) {
                presenter.getUserPosts(true)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun handlePosts(updVkItems: List<VkPost>, needToScrollUp: Boolean) {
        adapter.handleUpdates(updVkItems)
        hideErrorLayout()
        hideShimmerLayout()
        adapter.registerAdapterDataObserver(
                object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        if (needToScrollUp) {
                            binding.profileRecycler.scrollToPosition(0)
                        }
                    }
                }
        )
    }

    override fun showToast() {
        Toast.makeText(
                requireContext(), context?.getString(R.string.loading_db_failed),
                Toast.LENGTH_SHORT
        ).show()
    }

    override fun handleError() {
        showAlertDialog(R.string.alert_msg)
        hideShimmerLayout()
        showErrorLayout()
    }

    override fun setUserInfo(userInfo: VkUserInfo) {
        adapter.setUserInfo(userInfo)
    }

    override fun setCareerCities(cities: List<VkCityName>) {
        adapter.setCareerCities(cities)
    }

    override fun onEditTextClick() {
        val navController = requireActivity().findNavController(R.id.navHostFragment)
        navController.navigate(R.id.action_input)
    }


    override fun showSuccessfulSnackBar() {
        requireActivity().runOnUiThread {
            SnackBarHelper.showSnackBar(
                    binding.root,
                    getString(R.string.post_deleted),
                    R.drawable.bg_rounded_successful_snackbar,
                    R.drawable.ic_exclamation
            )
        }
    }

    override fun onPostLikeClick(vkPost: VkPost) {
        val isLikedByUser = vkPost.likes?.userLikes == 1
        if (isLikedByUser) {
            presenter.deleteLike(vkPost)
        } else {
            presenter.setLike(vkPost)
        }
    }

    override fun onDeletePost(vkPost: VkPost) {
        presenter.deletePost(vkPost)
    }

    override fun onSwipeHide(position: Int) {
        onDeletePost(posts[adapter.getCorrectPosition(position)])
    }

    override fun onSwipedLike(position: Int) {
        adapter.notifyItemChanged(position)
        onPostLikeClick(posts[adapter.getCorrectPosition(position)])
    }

    override fun handleLikedPosts(posts: ArrayList<VkPost>) {
        postsViewModel.handleLikedPosts(posts)
    }

    override fun handleLikedProfilePostsUserInfo(userInfo: VkUserInfo) {
        postsViewModel.handleLikedProfilePostsUserInfo(userInfo)
    }
}