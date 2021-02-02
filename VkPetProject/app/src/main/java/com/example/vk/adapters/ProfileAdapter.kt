package com.example.vk.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vk.adapters.EducationAdapter.Companion.SCHOOL_VIEW_TYPE
import com.example.vk.adapters.EducationAdapter.Companion.UNIVERSITY_VIEW_TYPE
import com.example.vk.databinding.LayoutProfileBinding
import com.example.vk.interfaces.OnEditTextClickListener
import com.example.vk.interfaces.OnLikeButtonClickListener
import com.example.vk.interfaces.OnPostClickListener
import com.example.vk.interfaces.OnShareButtonClickListener
import com.example.vk.models.VkCityName
import com.example.vk.models.VkEducation
import com.example.vk.models.VkPost
import com.example.vk.models.VkUserInfo
import com.example.vk.utils.DateFormatter
import com.example.vk.utils.DateFormatter.formatBirthdayDate
import com.example.vk.utils.PostDiffUtilCallback
import com.example.vk.views.PostPhotoLayout
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


class ProfileAdapter(
        private val posts: ArrayList<VkPost>,
        private val listener: OnPostClickListener,
        private val editListener: OnEditTextClickListener,
        private val likeListener: OnLikeButtonClickListener,
        private val shareListener: OnShareButtonClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var vkUserInfo: VkUserInfo
    private val cities = arrayListOf<VkCityName>()
    var isConnected = true

    private fun getInternetStatus(): Boolean {
        return isConnected
    }

    fun setUserInfo(vkUserInfo: VkUserInfo) {
        this.vkUserInfo = vkUserInfo
        notifyDataSetChanged()
    }

    fun setCareerCities(cities: List<VkCityName>) {
        this.cities.clear()
        this.cities.addAll(cities)
        notifyDataSetChanged()
    }


    fun handleUpdates(newList: List<VkPost>) {
        newList.let {
            Single.fromCallable {
                DiffUtil.calculateDiff(PostDiffUtilCallback(newList, posts))
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { diffResult ->
                        posts.clear()
                        posts.addAll(newList)
                        diffResult.dispatchUpdatesTo(this)
                    }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PROFILE_INFO_TYPE -> ProfileInfoViewHolder(
                    LayoutProfileBinding.inflate(
                            LayoutInflater.from(parent.context)
                    )
            )
            POST_WITH_PHOTO_TYPE -> PostPhotoViewHolder(
                    PostPhotoLayout(
                            parent.context
                    )
            )

            else -> PostTextViewHolder(
                    PostPhotoLayout(
                            parent.context
                    )
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            PROFILE_INFO_TYPE -> (viewHolder as ProfileInfoViewHolder).bind(
                    vkUserInfo, cities, editListener, getInternetStatus()
            )
            POST_WITH_PHOTO_TYPE -> (viewHolder as PostPhotoViewHolder).bind(
                    posts[getCorrectPosition(position)],
                    checkUserInfo(),
                    listener, likeListener, shareListener
            )
            POST_WITH_TEXT_TYPE -> (viewHolder as PostTextViewHolder).bind(
                    posts[getCorrectPosition(position)],
                    checkUserInfo(),
                    listener, likeListener, shareListener
            )
        }
    }

    override fun getItemCount(): Int {
        return posts.size + if (this::vkUserInfo.isInitialized) 1 else 0
    }

    fun getCorrectPosition(position: Int): Int {
        return if (this::vkUserInfo.isInitialized) position - 1 else position
    }

    private fun checkUserInfo(): VkUserInfo? {
        return if (this::vkUserInfo.isInitialized) vkUserInfo else null
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            this::vkUserInfo.isInitialized && position == 0 -> PROFILE_INFO_TYPE
            posts.isNotEmpty() && posts[getCorrectPosition(position)].attachments.isNullOrEmpty() -> POST_WITH_TEXT_TYPE
            else -> POST_WITH_PHOTO_TYPE
        }
    }

    class ProfileInfoViewHolder(
            private val binding: LayoutProfileBinding
    ) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(
                vkUserInfo: VkUserInfo,
                cities: List<VkCityName>,
                editListener: OnEditTextClickListener,
                isConnected: Boolean
        ) {
            if (isConnected) {
                binding.inputText.setOnClickListener {
                    editListener.onEditTextClick()
                }
            } else {
                binding.inputText.isClickable = false
                binding.inputText.isFocusable = false
            }

            val profilePhotoUrl = vkUserInfo.profilePhoto
            val targetProfilePhoto = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    binding.profilePhoto.setImageBitmap(bitmap)
                }

                override fun onBitmapFailed(p0: Exception?, p1: Drawable?) {
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }
            }
            Picasso.get().load(profilePhotoUrl).into(targetProfilePhoto)
            val postPhotoUrl = vkUserInfo.profilePhoto
            val targetPostPhoto = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    binding.photoToSendPost.setImageBitmap(bitmap)
                }

                override fun onBitmapFailed(p0: Exception?, p1: Drawable?) {
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }
            }
            Picasso.get().load(postPhotoUrl).into(targetPostPhoto)
            if (vkUserInfo.firstName != null && vkUserInfo.lastName != null) {
                val profileNameAndSurname = "${vkUserInfo.firstName} ${vkUserInfo.lastName}"
                binding.profileName.text = profileNameAndSurname
            } else binding.profileName.text = "Пользователь не найден"
            if (vkUserInfo.lastSeen != null) {
                if (vkUserInfo.sex != null || vkUserInfo.sex == 1) {
                    val dateSeen = "была в сети ${
                        DateFormatter.formatLastDateAdv(
                                vkUserInfo.lastSeen.time ?: 0,
                                itemView.context
                        )
                    }"
                    binding.profileLastSeen.text = dateSeen
                } else {
                    val dateSeen = "был в сети ${
                        DateFormatter.formatLastDateAdv(
                                vkUserInfo.lastSeen.time ?: 0,
                                itemView.context
                        )
                    }"
                    binding.profileLastSeen.text = dateSeen
                }
            }
            val birthdayDate = vkUserInfo.birthdayDate
            if (birthdayDate != null && birthdayDate.isNotEmpty()) {
                binding.profileBDayNumbers.text = formatBirthdayDate(birthdayDate)
            } else {
                binding.profileBDayText.visibility = View.GONE
                binding.profileBDayNumbers.visibility = View.GONE
            }
            if (vkUserInfo.followersCount != null) {
                binding.profileFollowersNumbers.text =
                        vkUserInfo.followersCount.toString()
            } else {
                binding.profileFollowersText.visibility = View.GONE
                binding.profileFollowersNumbers.visibility = View.GONE
            }
            if (vkUserInfo.about.isNullOrEmpty()) {
                binding.profileAboutText.visibility = View.GONE
                binding.profileAbout.visibility = View.GONE
            } else {
                binding.profileAboutText.text = vkUserInfo.about
            }
            if (vkUserInfo.city?.title != null) {
                binding.profileHomeText.text = vkUserInfo.city.title
            } else {
                binding.profileHome.visibility = View.GONE
                binding.profileHomeText.visibility = View.GONE
            }
            if (vkUserInfo.country?.title != null) {
                binding.profileCountryText.text = vkUserInfo.country.title
            } else {
                binding.profileCountryPicture.visibility = View.GONE
                binding.profileCountryText.visibility = View.GONE
            }
            if (vkUserInfo.career.isNullOrEmpty()) {
                binding.profileCareerRecycler.visibility = View.GONE
                binding.profileCareer.visibility = View.GONE
            } else {
                binding.profileCareerRecycler.visibility = View.VISIBLE
                binding.profileCareer.visibility = View.VISIBLE
                binding.profileCareerRecycler.layoutManager =
                        LinearLayoutManager(itemView.context)
                binding.profileCareerRecycler.adapter =
                        CareerAdapter(vkUserInfo.career, cities)
            }
            val education = arrayListOf<VkEducation>()
            education.addAll(vkUserInfo.universities?.map { it ->
                VkEducation(
                        it.name ?: "",
                        0,
                        it.graduation ?: 0,
                        UNIVERSITY_VIEW_TYPE,
                        it.facultyName,
                        it.chairName
                )
            } ?: listOf())
            education.addAll(vkUserInfo.schools?.map {
                VkEducation(
                        it.name ?: "",
                        it.yearFrom ?: 0,
                        it.yearTo ?: 0,
                        SCHOOL_VIEW_TYPE,
                        "",
                        ""
                )
            } ?: listOf())
            if (education.isNotEmpty()) {
                binding.profileEducationRecycler.visibility = View.VISIBLE
                binding.profileEducation.visibility = View.VISIBLE
                binding.profileEducationRecycler.layoutManager =
                        LinearLayoutManager(itemView.context)
                binding.profileEducationRecycler.adapter = EducationAdapter(education)
            } else {
                binding.profileEducationRecycler.visibility = View.GONE
                binding.profileEducation.visibility = View.GONE
            }
        }
    }

    class PostPhotoViewHolder(private val photoLayout: PostPhotoLayout) :
            RecyclerView.ViewHolder(photoLayout) {
        fun bind(
                vkItem: VkPost,
                vkUserInfo: VkUserInfo?,
                listener: OnPostClickListener,
                likeListener: OnLikeButtonClickListener,
                shareListener: OnShareButtonClickListener
        ) {
            photoLayout.setPost(vkItem, vkUserInfo, null, listener, likeListener, shareListener)
        }
    }

    class PostTextViewHolder(private val textLayout: PostPhotoLayout) :
            RecyclerView.ViewHolder(textLayout) {
        fun bind(
                vkItem: VkPost,
                vkUserInfo: VkUserInfo?,
                listener: OnPostClickListener,
                likeListener: OnLikeButtonClickListener,
                shareListener: OnShareButtonClickListener
        ) {
            textLayout.setPost(vkItem, vkUserInfo, null, listener, likeListener, shareListener)
        }
    }

    companion object {
        const val PROFILE_INFO_TYPE = 12
        const val POST_WITH_PHOTO_TYPE = 13
        const val POST_WITH_TEXT_TYPE = 14
    }
}