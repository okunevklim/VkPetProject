package com.example.vk.adapters

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.vk.interfaces.OnLikeButtonClickListener
import com.example.vk.interfaces.OnPostClickListener
import com.example.vk.interfaces.OnShareButtonClickListener
import com.example.vk.models.VkGroup
import com.example.vk.models.VkPost
import com.example.vk.models.VkUserInfo
import com.example.vk.utils.PostDiffUtilCallback
import com.example.vk.views.PostPhotoLayout
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PostAdapter(
        private val posts: ArrayList<VkPost>,
        private val listener: OnPostClickListener,
        private val likeListener: OnLikeButtonClickListener,
        private val shareListener: OnShareButtonClickListener
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var users = arrayListOf<VkUserInfo>()
    private var groups = arrayListOf<VkGroup>()

    fun setNewsInfo(users: ArrayList<VkUserInfo>?, groups: ArrayList<VkGroup>?) {
        if (users != null) {
            this.users.clear()
            this.users.addAll(users)
        }
        if (groups != null) {
            this.groups.clear()
            this.groups.addAll(groups)
        }
        notifyDataSetChanged()
    }

    fun handleUpdates(newList: List<VkPost>) {
        newList.let {
            Single.fromCallable {
                DiffUtil.calculateDiff(PostDiffUtilCallback(newList, posts))
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError { Log.e("PostAdapter", "Error in the DIFF UTIL!") }
                    .subscribe { diffResult ->
                        posts.clear()
                        posts.addAll(newList)
                        diffResult.dispatchUpdatesTo(this)
                    }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            POST_WITH_PHOTO_TYPE -> PostPhotoViewHolder(PostPhotoLayout(parent.context))
            else -> PostTextViewHolder(PostPhotoLayout(parent.context))
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (posts[position].attachments.isNullOrEmpty()) POST_WITH_TEXT_TYPE else POST_WITH_PHOTO_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val vkPost = posts[position]
        when (getItemViewType(position)) {
            POST_WITH_PHOTO_TYPE -> (viewHolder as PostPhotoViewHolder).bind(
                    vkPost,
                    users.find { it.id == vkPost.ownerId },
                    groups.find { it.id == kotlin.math.abs(vkPost.ownerId ?: 0) },
                    listener, likeListener, shareListener
            )
            POST_WITH_TEXT_TYPE -> (viewHolder as PostTextViewHolder).bind(
                    vkPost,
                    users.find { it.id == vkPost.ownerId },
                    groups.find { it.id == kotlin.math.abs(vkPost.ownerId ?: 0) },
                    listener, likeListener, shareListener
            )
        }
    }

    class PostPhotoViewHolder(private val photoLayout: PostPhotoLayout) :
            RecyclerView.ViewHolder(photoLayout) {
        fun bind(
                vkPost: VkPost,
                vkUserInfo: VkUserInfo?,
                group: VkGroup?,
                listener: OnPostClickListener,
                likeListener: OnLikeButtonClickListener,
                shareListener: OnShareButtonClickListener
        ) {
            photoLayout.setPost(vkPost, vkUserInfo, group, listener, likeListener, shareListener)
        }
    }

    class PostTextViewHolder(private val textLayout: PostPhotoLayout) :
            RecyclerView.ViewHolder(textLayout) {
        fun bind(
                vkPost: VkPost,
                vkUserInfo: VkUserInfo?,
                group: VkGroup?,
                listener: OnPostClickListener,
                likeListener: OnLikeButtonClickListener,
                shareListener: OnShareButtonClickListener
        ) {
            textLayout.setPost(vkPost, vkUserInfo, group, listener, likeListener, shareListener)
        }
    }

    companion object {
        const val POST_WITH_PHOTO_TYPE = 0
        const val POST_WITH_TEXT_TYPE = 1
    }
}