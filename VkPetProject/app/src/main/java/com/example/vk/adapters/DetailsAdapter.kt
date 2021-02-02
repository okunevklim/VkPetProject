package com.example.vk.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.example.vk.R
import com.example.vk.base.VkApplication.Companion.context
import com.example.vk.databinding.LayoutItemCommentBinding
import com.example.vk.databinding.LayoutPostDetailsBinding
import com.example.vk.interfaces.OnCommentLikeClickListener
import com.example.vk.interfaces.OnLikeButtonClickListener
import com.example.vk.interfaces.OnShareButtonClickListener
import com.example.vk.models.VkComment
import com.example.vk.models.VkPost
import com.example.vk.models.VkUserInfo
import com.example.vk.utils.CounterFormatter.formatCounterValue
import com.example.vk.utils.DateFormatter.formatLastDateAdv
import com.example.vk.utils.Helpers
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.util.*
import kotlin.collections.ArrayList

class DetailsAdapter(
        private val vkItem: VkPost,
        private val comments: ArrayList<VkComment>,
        private val commentListener: OnCommentLikeClickListener,
        private val shareListener: OnShareButtonClickListener,
        private val postLikeListener: OnLikeButtonClickListener,
        private val name: String,
        private val photoURL: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val commentsInfo = arrayListOf<VkUserInfo>()
    private var amountOfComments: Long = 0
    private var isShimmerLoading: Int = 0

    fun setVkComments(comments: ArrayList<VkComment>, commentsInfo: ArrayList<VkUserInfo>) {
        this.commentsInfo.clear()
        this.commentsInfo.addAll(commentsInfo)
        this.comments.clear()
        this.comments.addAll(comments)
        notifyDataSetChanged()
    }

    fun setAmountOfLikes(likes: Long, userLikes: Int) {
        vkItem.likes?.count = likes
        vkItem.likes?.userLikes = userLikes
        notifyDataSetChanged()
    }

    fun setShimmerLoading(isShimmerLoading: Int) {
        this.isShimmerLoading = isShimmerLoading
        notifyDataSetChanged()
    }

    fun setAmountOfComments(amountOfComments: Long) {
        this.amountOfComments = amountOfComments
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            COMMENT_TEXT_TYPE -> CommentViewHolder(
                    LayoutItemCommentBinding.inflate(
                            LayoutInflater.from(parent.context)
                    )
            )
            POST_WITH_PHOTO_TYPE -> PostPhotoViewHolder(
                    LayoutPostDetailsBinding.inflate(
                            LayoutInflater.from(parent.context)
                    )
            )

            else -> PostTextViewHolder(
                    LayoutPostDetailsBinding.inflate(
                            LayoutInflater.from(parent.context)
                    )
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            COMMENT_TEXT_TYPE -> (viewHolder as CommentViewHolder).bind(
                    comments[position - 1],
                    commentsInfo,
                    commentListener,
                    vkItem
            )
            POST_WITH_PHOTO_TYPE -> (viewHolder as PostPhotoViewHolder).bind(
                    vkItem,
                    shareListener,
                    postLikeListener,
                    name,
                    photoURL,
                    amountOfComments.toInt(),
                    isShimmerLoading
            )
            POST_WITH_TEXT_TYPE -> (viewHolder as PostTextViewHolder).bind(
                    vkItem,
                    postLikeListener,
                    name,
                    photoURL,
                    amountOfComments.toInt(),
                    isShimmerLoading
            )
        }
    }

    override fun getItemCount(): Int {
        return comments.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        val isPhoto = !vkItem.attachments.isNullOrEmpty() && position == 0
        val isText = vkItem.attachments.isNullOrEmpty() && position == 0
        return when {
            isText -> POST_WITH_TEXT_TYPE
            isPhoto -> POST_WITH_PHOTO_TYPE
            else -> COMMENT_TEXT_TYPE
        }
    }

    class CommentViewHolder(private val binding: LayoutItemCommentBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(
                vkComment: VkComment,
                commentsInfo: ArrayList<VkUserInfo>,
                commentListener: OnCommentLikeClickListener,
                vkPost: VkPost
        ) {
            val nameAndSurname =
                    "${commentsInfo.find { it.id == vkComment.fromID }?.firstName} ${commentsInfo.find { it.id == vkComment.fromID }?.lastName}"
            binding.personName.text = nameAndSurname
            val commentPhotoUrl = commentsInfo.find { it.id == vkComment.fromID }?.profilePhoto
            Picasso.get().load(commentPhotoUrl).into(binding.personPhoto)
            binding.dateOfComment.text =
                    formatLastDateAdv(vkComment.date ?: 0, itemView.context)
            binding.commentLikes.text = formatCounterValue(vkComment.likes?.count ?: 0)
            binding.textOfComment.text = vkComment.text
            binding.commentLikes.setTextColor(
                    if (vkComment.likes?.userLikes == 1) getColor(
                            itemView.context,
                            R.color.red_filled_color
                    ) else getColor(itemView.context, R.color.default_color)
            )
            binding.personLike.setImageResource(
                    if (vkComment.likes?.userLikes == 1) R.drawable.ic_like_filled_red_svg else R.drawable.ic_like_svg
            )
            binding.personLike.setOnClickListener {
                commentListener.onCommentLikeClick(vkComment, vkPost)
            }
        }
    }

    class PostPhotoViewHolder(private val binding: LayoutPostDetailsBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun showDownloadToast() {
            Toast.makeText(
                    itemView.context,
                    itemView.context.getString(R.string.image_saved_to_gallery),
                    Toast.LENGTH_SHORT
            ).show()
        }

        fun bind(
                vkPost: VkPost,
                shareListener: OnShareButtonClickListener,
                postLikeListener: OnLikeButtonClickListener,
                name: String,
                photoURL: String,
                amountOfComments: Int,
                isShimmerLoading: Int
        ) {
            val screenWidth = Helpers.screenWidth()
            Picasso.get().load(photoURL).into(binding.detailsPhoto)
            if (vkPost.attachments != null) {
                if (!vkPost.attachments.isNullOrEmpty()) {
                    val photoSizes = vkPost.attachments[0].photo?.sizes ?: ArrayList()
                    if (photoSizes.isNotEmpty()) {
                        val photoObject = photoSizes.filter { it.width in 400..2500 }
                                .sortedByDescending { it.width }[0]
                        val photoUrl = photoObject.url
                        val target = object : Target {
                            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                                binding.detailsPicture.setImageBitmap(bitmap)
                                binding.detailsShare.setOnClickListener {
                                    shareListener.onShareButtonClick(bitmap)
                                }
                                binding.download.setOnClickListener {
                                    MediaStore.Images.Media.insertImage(
                                            context.contentResolver,
                                            bitmap,
                                            "${Calendar.getInstance().timeInMillis}",
                                            ""
                                    )
                                    showDownloadToast()
                                }
                            }

                            override fun onBitmapFailed(p0: Exception?, p1: Drawable?) {
                            }

                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            }
                        }
                        Picasso.get().load(photoUrl).into(target)
                        if (photoObject.height != null && photoObject.width != null) {
                            val photoProportion = photoObject.width.toFloat() / photoObject.height
                            val newPhotoHeight = screenWidth / photoProportion
                            binding.detailsPicture.layoutParams.height =
                                    newPhotoHeight.toInt()
                        }
                        binding.detailsPicture.visibility = View.VISIBLE
                        binding.amountOfDetailsShares.text =
                                vkPost.reposts?.count.toString()
                    }
                }
            } else {
                binding.amountOfDetailsShares.visibility = View.GONE
                binding.detailsShare.visibility = View.GONE
                binding.detailsPicture.visibility = View.GONE
            }
            binding.detailsName.text = name
            binding.dateOfDetails.text =
                    formatLastDateAdv(vkPost.date ?: 0, itemView.context)
            binding.detailsText.text = vkPost.text
            val numberOfLikes = vkPost.likes?.count ?: 0
            binding.detailsLike.setOnClickListener {
                postLikeListener.onPostLikeClick(vkPost)
            }
            binding.amountOfDetailsLikes.text = numberOfLikes.toString()
            if (vkPost.likes?.userLikes == 1) {
                binding.amountOfDetailsLikes.setTextColor(
                        getColor(
                                itemView.context,
                                R.color.red_filled_color
                        )
                )
                binding.detailsLike.setImageResource(R.drawable.ic_like_filled_red_svg)
            } else {
                binding.amountOfDetailsLikes.setTextColor(
                        getColor(
                                itemView.context,
                                R.color.default_color
                        )
                )
                binding.detailsLike.setImageResource(R.drawable.ic_like_svg)
            }
            val numberOfReposts = vkPost.reposts?.count ?: 0
            if (numberOfReposts > 0) {
                binding.amountOfDetailsShares.visibility = View.VISIBLE
                binding.amountOfDetailsShares.text =
                        formatCounterValue(numberOfReposts)
            } else {
                binding.amountOfDetailsShares.visibility = View.GONE
            }
            val numberOfViews = vkPost.views?.count ?: 0
            if (numberOfViews > 0) {
                binding.detailsView.visibility = View.VISIBLE
                binding.amountOfDetailsViews.visibility = View.VISIBLE
                binding.amountOfDetailsViews.text =
                        formatCounterValue(numberOfViews)
            } else {
                binding.amountOfDetailsViews.visibility = View.GONE
                binding.detailsView.visibility = View.GONE
            }
            val canUserComment = vkPost.comments?.canPost
            val textNumberOfComments = itemView.context.resources.getQuantityString(
                    R.plurals.comment_plurals,
                    amountOfComments,
                    amountOfComments
            )
            checkShimmer(0)
            if (canUserComment == 1) {
                checkShimmer(isShimmerLoading)
                binding.amountOfDetailsComments.text = textNumberOfComments
            } else if (canUserComment == 0) {
                if (amountOfComments > 0) {
                    checkShimmer(isShimmerLoading)
                    binding.amountOfDetailsComments.text = textNumberOfComments
                }
                binding.download.visibility = View.VISIBLE
            }
        }

        private fun checkShimmer(isShimmerLoading: Int) {
            if (isShimmerLoading == 1) {
                binding.amountOfDetailsComments.visibility = View.GONE
                binding.commentShimmer.visibility = View.VISIBLE
                binding.commentShimmer.startShimmer()
            } else {
                binding.amountOfDetailsComments.visibility = View.VISIBLE
                binding.commentShimmer.visibility = View.GONE
                binding.commentShimmer.stopShimmer()
            }
        }
    }

    class PostTextViewHolder(private val binding: LayoutPostDetailsBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(
                vkItem: VkPost,
                postLikeListener: OnLikeButtonClickListener,
                name: String,
                photoURL: String,
                amountOfComments: Int,
                isShimmerLoading: Int
        ) {
            Picasso.get().load(photoURL).into(binding.detailsPhoto)
            binding.detailsName.text = name
            binding.dateOfDetails.text =
                    formatLastDateAdv(vkItem.date ?: 0, itemView.context)
            binding.detailsText.text = vkItem.text
            val numberOfLikes = vkItem.likes?.count ?: 0
            binding.amountOfDetailsLikes.text =
                    formatCounterValue(numberOfLikes)
            binding.detailsLike.setOnClickListener {
                postLikeListener.onPostLikeClick(vkItem)
            }
            if (vkItem.likes?.userLikes == 1) {
                binding.amountOfDetailsLikes.setTextColor(
                        getColor(
                                itemView.context,
                                R.color.red_filled_color
                        )
                )
                binding.detailsLike.setImageResource(R.drawable.ic_like_filled_red_svg)
            } else {
                binding.amountOfDetailsLikes.setTextColor(
                        getColor(
                                itemView.context,
                                R.color.default_color
                        )
                )
                binding.detailsLike.setImageResource(R.drawable.ic_like_svg)
            }
            val numberOfReposts = vkItem.reposts?.count ?: 0
            if (numberOfReposts > 0) {
                binding.detailsShare.visibility = View.VISIBLE
                binding.amountOfDetailsShares.visibility = View.VISIBLE
                binding.amountOfDetailsShares.text =
                        formatCounterValue(vkItem.reposts?.count ?: 0)
            } else {
                binding.amountOfDetailsShares.visibility = View.GONE
                binding.detailsShare.visibility = View.GONE
            }
            val numberOfViews = vkItem.views?.count ?: 0
            if (numberOfViews > 0) {
                binding.detailsView.visibility = View.VISIBLE
                binding.amountOfDetailsViews.visibility = View.VISIBLE
                binding.amountOfDetailsViews.text =
                        formatCounterValue(vkItem.views?.count ?: 0)
            } else {
                binding.amountOfDetailsViews.visibility = View.GONE
                binding.detailsView.visibility = View.GONE
            }
            val canUserComment = vkItem.comments?.canPost
            val textNumberOfComments = itemView.context.resources.getQuantityString(
                    R.plurals.comment_plurals,
                    amountOfComments,
                    amountOfComments
            )
            if (canUserComment == 1) {
                checkShimmer(isShimmerLoading)
                binding.amountOfDetailsComments.text = textNumberOfComments
            } else if (canUserComment == 0) {
                if (amountOfComments > 0) {
                    checkShimmer(isShimmerLoading)
                    binding.amountOfDetailsComments.text = textNumberOfComments
                }
                binding.download.visibility = View.VISIBLE
            }
            binding.download.visibility = View.GONE
        }

        private fun checkShimmer(isShimmerLoading: Int) {
            if (isShimmerLoading == 1) {
                binding.amountOfDetailsComments.visibility = View.GONE
                binding.commentShimmer.visibility = View.VISIBLE
                binding.commentShimmer.startShimmer()
            } else {
                binding.amountOfDetailsComments.visibility = View.VISIBLE
                binding.commentShimmer.visibility = View.GONE
                binding.commentShimmer.stopShimmer()
            }
        }
    }

    companion object {
        const val POST_WITH_PHOTO_TYPE = 13
        const val POST_WITH_TEXT_TYPE = 14
        const val COMMENT_TEXT_TYPE = 15
    }
}