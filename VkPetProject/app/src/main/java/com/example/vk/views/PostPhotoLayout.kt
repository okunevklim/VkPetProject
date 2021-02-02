package com.example.vk.views

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat.getColor
import com.example.vk.R
import com.example.vk.databinding.ViewCustomPhotoLayoutBinding
import com.example.vk.interfaces.OnLikeButtonClickListener
import com.example.vk.interfaces.OnPostClickListener
import com.example.vk.interfaces.OnShareButtonClickListener
import com.example.vk.models.VkGroup
import com.example.vk.models.VkPost
import com.example.vk.models.VkUserInfo
import com.example.vk.utils.CounterFormatter.formatCounterValue
import com.example.vk.utils.DateFormatter
import com.example.vk.utils.Helpers
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import java.util.*
import kotlin.collections.ArrayList


class PostPhotoLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {
    private var binding: ViewCustomPhotoLayoutBinding =
        ViewCustomPhotoLayoutBinding.inflate(LayoutInflater.from(context), this)
    private var screenWidth = Helpers.screenWidth()
    private val toast = Toast.makeText(
        context,
        context.getString(R.string.image_saved_to_gallery),
        LENGTH_SHORT
    )

    init {
        setWillNotDraw(true)
    }

    fun setPost(
        vkItem: VkPost,
        vkUserInfo: VkUserInfo?,
        group: VkGroup?,
        listener: OnPostClickListener,
        likeListener: OnLikeButtonClickListener,
        shareListener: OnShareButtonClickListener
    ) {
        group?.let {
            applyVkGroup(it)
            binding.root.setOnClickListener {
                listener.onPostClick(
                    vkItem,
                    group.name ?: "",
                    group.photoTwoHundred ?: "",
                    if (vkItem.comments?.canPost == 1) CAN_POST_TRUE else CAN_POST_FALSE,
                    vkItem.comments?.count ?: 0
                )
            }
        } ?: vkUserInfo?.let {
            applyUserInfo(it)
            binding.root.setOnClickListener {
                listener.onPostClick(
                    vkItem,
                    "${vkUserInfo.firstName} ${vkUserInfo.lastName}",
                    vkUserInfo.photoOneHundred ?: "",
                    if (vkItem.comments?.canPost == 1) CAN_POST_TRUE else CAN_POST_FALSE,
                    vkItem.comments?.count ?: 0
                )

            }
        }
        if (vkItem.date != null) {
            binding.postDate.text = DateFormatter.formatLastDateAdv(vkItem.date, context)
        }

        binding.postContentText.text = vkItem.text
        if (vkItem.attachments != null) {
            if (vkItem.attachments.isNotEmpty()) {
                val photoSizes = vkItem.attachments[0].photo?.sizes ?: ArrayList()
                if (photoSizes.isNotEmpty()) {
                    val photoObject = photoSizes.filter { it.width in 400..1000 }
                    if (photoObject.isNotEmpty()) {
                        val newPhotoObject = photoObject.sortedByDescending { it.width }[0]
                        val photoUrl = newPhotoObject.url

                        val target = object : Target {
                            override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                                binding.postContentPhoto.setImageBitmap(bitmap)
                                binding.shareButton.setOnClickListener {
                                    shareListener.onShareButtonClick(bitmap)
                                }
                                binding.savePhoto.setOnClickListener {
                                    MediaStore.Images.Media.insertImage(
                                        context.contentResolver,
                                        bitmap,
                                        "${Calendar.getInstance().timeInMillis}",
                                        ""
                                    )
                                    toast.show()
                                }
                            }

                            override fun onBitmapFailed(p0: Exception?, p1: Drawable?) {
                            }

                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            }
                        }
                        Picasso.get().load(photoUrl).into(target)
                        if (newPhotoObject.height != null && newPhotoObject.width != null) {
                            val photoProportion =
                                newPhotoObject.width.toFloat() / newPhotoObject.height
                            val newPhotoHeight = screenWidth / photoProportion
                            binding.postContentPhoto.layoutParams.height = newPhotoHeight.toInt()
                        }

                        binding.savePhoto.visibility = View.VISIBLE
                        binding.postContentPhoto.visibility = View.VISIBLE
                        binding.amountOfShares.text = vkItem.reposts?.count.toString()

                    }
                }
            }
        } else {
            binding.savePhoto.visibility = View.GONE
            binding.amountOfShares.visibility = View.GONE
            binding.shareButton.visibility = View.GONE
            binding.postContentPhoto.visibility = View.GONE
        }

        if (vkItem.likes?.count != null) {
            val numberOfLikes = formatCounterValue(
                vkItem.likes.count ?: 0
            )
            binding.amountOfLikes.text = numberOfLikes
            binding.amountOfLikes.setTextColor(
                if (vkItem.likes.userLikes == 1) getColor(
                    context,
                    R.color.red_filled_color
                ) else getColor(context, R.color.default_color)
            )
        }
        if (vkItem.views?.count != null) {
            val numberOfViews = formatCounterValue(vkItem.views.count)
            if (vkItem.views.count > 0) {
                binding.eye.visibility = View.VISIBLE
                binding.counterOfViews.text = numberOfViews
            } else {
                binding.counterOfViews.visibility = View.GONE
                binding.eye.visibility = View.GONE
            }
        }
        val isLikedByUser = vkItem.likes?.userLikes
        binding.likeButton.setOnClickListener {
            likeListener.onPostLikeClick(vkItem)
        }
        binding.likeButton.setImageResource(
            if (isLikedByUser == 1)
                R.drawable.ic_like_filled_red_svg else R.drawable.ic_like_svg
        )
        val numberOfComments = vkItem.comments?.count
        val canPostComment = vkItem.comments?.canPost
        if (numberOfComments != null) {
            if (canPostComment == 1 || (canPostComment == 0 && numberOfComments > 0)) {
                binding.commentButton.visibility = View.VISIBLE
                binding.amountOfComments.visibility = View.VISIBLE
                binding.amountOfComments.text = numberOfComments.toString()
            } else {
                binding.commentButton.visibility = View.GONE
                binding.amountOfComments.visibility = View.GONE
            }
        }

    }

    private fun applyUserInfo(vkUserInfo: VkUserInfo) {
        val profilePhotoUrl = vkUserInfo.photoOneHundred
        val nameAndSurname = "${vkUserInfo.firstName} ${vkUserInfo.lastName}"
        applyPhotoAndName(profilePhotoUrl, nameAndSurname)
    }

    private fun applyVkGroup(vkGroup: VkGroup) {
        applyPhotoAndName(vkGroup.photoTwoHundred, vkGroup.name ?: "")
    }

    private fun applyPhotoAndName(photo: String?, name: String) {
        val targetProfilePhoto = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                binding.postGroupPhoto.setImageBitmap(bitmap)
            }

            override fun onBitmapFailed(p0: Exception?, p1: Drawable?) {
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        }
        photo?.let { Picasso.get().load(it).into(targetProfilePhoto) }
            ?: binding.postGroupPhoto.setImageResource(R.drawable.ic_vk_logo)
        binding.postContentPhoto.setImageDrawable(null)
        binding.postContentPhoto.layoutParams.height = 0
        binding.postGroupName.text = name
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0
        val newWidthSpec = MeasureSpec.makeMeasureSpec(NEW_WIDTH_SPEC, EXACTLY)

        measureChildWithMargins(
            binding.postGroupPhoto,
            newWidthSpec,
            EXTRA_WIDTH,
            newWidthSpec,
            height
        )
        height += binding.postGroupPhoto.measuredHeight

        measureChildWithMargins(
            binding.postGroupName,
            widthMeasureSpec,
            EXTRA_WIDTH,
            heightMeasureSpec,
            height
        )
        measureChildWithMargins(
            binding.savePhoto,
            widthMeasureSpec,
            EXTRA_WIDTH,
            heightMeasureSpec,
            height
        )

        measureChildWithMargins(
            binding.postDate,
            widthMeasureSpec,
            EXTRA_WIDTH,
            heightMeasureSpec,
            height
        )
        val nameAndDateHeight =
            binding.postGroupName.measuredHeight + binding.postDate.measuredHeight
        height += maxOf(binding.postGroupPhoto.measuredHeight, nameAndDateHeight)

        measureChildWithMargins(
            binding.postContentText,
            widthMeasureSpec,
            EXTRA_WIDTH,
            heightMeasureSpec,
            height
        )
        height += binding.postContentText.measuredHeight
        measureChildWithMargins(
            binding.postContentPhoto,
            widthMeasureSpec,
            EXTRA_WIDTH,
            heightMeasureSpec,
            height
        )
        height += binding.postContentPhoto.measuredHeight

        binding.likeButton.measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(EXTRA_SPEC, EXACTLY)
        )

        height += binding.likeButton.measuredHeight

        measureChildWithMargins(
            binding.amountOfLikes,
            widthMeasureSpec,
            EXTRA_WIDTH,
            heightMeasureSpec,
            height
        )

        measureChildWithMargins(
            binding.commentButton,
            widthMeasureSpec,
            EXTRA_WIDTH,
            heightMeasureSpec,
            height
        )
        measureChildWithMargins(
            binding.amountOfComments,
            widthMeasureSpec,
            EXTRA_WIDTH,
            heightMeasureSpec,
            height
        )


        measureChildWithMargins(
            binding.shareButton,
            newWidthSpec,
            EXTRA_WIDTH,
            newWidthSpec,
            height
        )
        measureChildWithMargins(
            binding.amountOfShares,
            widthMeasureSpec,
            EXTRA_WIDTH,
            heightMeasureSpec,
            height
        )
        measureChildWithMargins(
            binding.eye,
            widthMeasureSpec,
            EXTRA_WIDTH,
            heightMeasureSpec,
            height
        )
        measureChildWithMargins(
            binding.counterOfViews,
            widthMeasureSpec,
            EXTRA_WIDTH,
            heightMeasureSpec,
            height
        )
        setMeasuredDimension(desiredWidth, resolveSize(height, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentLeft = l
        var currentTop = CURRENT_TOP
        val currentRight = r

        currentLeft += DEFAULT_MARGIN
        currentTop += DEFAULT_MARGIN
        binding.postGroupPhoto.layout(
            currentLeft,
            currentTop,
            currentLeft + binding.postGroupPhoto.measuredHeight,
            currentTop + binding.postGroupPhoto.measuredHeight
        )
        currentLeft += binding.postGroupPhoto.measuredWidth
        binding.postGroupName.layout(
            currentLeft + MARGIN_BETWEEN_LOGO,
            currentTop,
            currentRight,
            currentTop + binding.postGroupName.measuredHeight
        )
        currentLeft += binding.postGroupName.measuredWidth
        binding.savePhoto.layout(
            currentRight - BUTTON_SAVE.dpToPx(),
            currentTop,
            currentRight,
            currentTop + binding.postGroupName.measuredHeight
        )
        currentLeft -= binding.postGroupName.measuredWidth
        currentTop += binding.postGroupName.measuredHeight
        binding.postDate.layout(
            currentLeft + MARGIN_BETWEEN_LOGO,
            currentTop,
            currentRight,
            currentTop + binding.postDate.measuredHeight
        )
        val nameAndDateHeight =
            binding.postGroupName.measuredHeight + binding.postDate.measuredHeight
        currentTop = DEFAULT_MARGIN + maxOf(
            binding.postGroupPhoto.measuredHeight,
            nameAndDateHeight
        )
        currentLeft = CURRENT_LEFT
        binding.postContentText.layout(
            currentLeft + MARGIN_BETWEEN_LOGO,
            currentTop,
            currentRight,
            currentTop + binding.postContentText.measuredHeight
        )
        currentTop += binding.postContentText.measuredHeight
        binding.postContentPhoto.layout(
            currentLeft,
            currentTop,
            currentRight,
            currentTop + binding.postContentPhoto.measuredHeight
        )
        currentTop += binding.postContentPhoto.measuredHeight
        binding.likeButton.layout(
            currentLeft,
            currentTop + DEFAULT_INDENT,
            currentLeft + BUTTON_WIDTH.dpToPx(),
            currentTop + BUTTON_HEIGHT.dpToPx()
        )

        currentLeft += BUTTON_WIDTH.dpToPx() - DEFAULT_INDENT

        binding.amountOfLikes.layout(
            currentLeft,
            currentTop + DEFAULT_INDENT,
            currentLeft + BUTTON_WIDTH.dpToPx(),
            currentTop + BUTTON_HEIGHT.dpToPx()
        )

        currentLeft += BUTTON_WIDTH.dpToPx() + DEFAULT_INDENT

        binding.commentButton.layout(
            currentLeft,
            currentTop + DEFAULT_INDENT,
            currentLeft + BUTTON_WIDTH.dpToPx(),
            currentTop + BUTTON_HEIGHT.dpToPx()
        )
        currentLeft += BUTTON_WIDTH.dpToPx() - DEFAULT_INDENT
        binding.amountOfComments.layout(
            currentLeft,
            currentTop + DEFAULT_INDENT,
            currentLeft + BUTTON_WIDTH.dpToPx(),
            currentTop + BUTTON_HEIGHT.dpToPx()
        )

        currentLeft += MARGIN_BETWEEN_LOGO.dpToPx()

        binding.shareButton.layout(
            currentLeft,
            currentTop + DEFAULT_INDENT,
            currentLeft + BUTTON_WIDTH.dpToPx(),
            currentTop + BUTTON_HEIGHT.dpToPx()
        )
        currentLeft += BUTTON_WIDTH.dpToPx() - DEFAULT_INDENT
        binding.amountOfShares.layout(
            currentLeft,
            currentTop + DEFAULT_INDENT,
            currentLeft + BUTTON_WIDTH.dpToPx(),
            currentTop + BUTTON_HEIGHT.dpToPx()
        )
        currentLeft += BUTTON_WIDTH.dpToPx() - EXTRA_SPEC

        binding.counterOfViews.layout(
            currentRight - BUTTON_SAVE.dpToPx(),
            currentTop + 10,
            currentRight + MARGIN_BETWEEN_LOGO.dpToPx(),
            currentTop + BUTTON_HEIGHT.dpToPx()
        )

        currentLeft += MARGIN_BETWEEN_LOGO
        binding.eye.layout(
            currentLeft,
            currentTop + DEFAULT_INDENT,
            currentRight - BUTTON_WIDTH.dpToPx() + MARGIN_BETWEEN_LOGO,
            currentTop + BUTTON_HEIGHT.dpToPx()
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet) =
        MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)

    override fun generateDefaultLayoutParams() =
        MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)

    companion object {
        const val CAN_POST_TRUE = 1
        const val CAN_POST_FALSE = 0
        const val DEFAULT_MARGIN = 30
        const val MARGIN_BETWEEN_LOGO = 20
        const val BUTTON_WIDTH = 56
        const val BUTTON_SAVE = 40
        const val BUTTON_HEIGHT = 30
        const val EXTRA_WIDTH = 0
        const val CURRENT_LEFT = 0
        const val CURRENT_TOP = 0
        const val DEFAULT_INDENT = 10
        const val NEW_WIDTH_SPEC = 100
        const val EXTRA_SPEC = 50
        fun Int.dpToPx(): Int {
            return (this * Resources.getSystem().displayMetrics.density).toInt()
        }
    }
}