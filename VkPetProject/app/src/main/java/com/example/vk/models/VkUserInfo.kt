package com.example.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VkUserInfo(
    val id: Long?,
    val domain: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("photo_max_orig")
    val profilePhoto: String?,
    @SerializedName("bdate")
    val birthdayDate: String?,
    val city: VkLocation?,
    val country: VkLocation?,
    val career: List<VkCareer>?,
    val universities: ArrayList<VkUniversity>?,
    val schools: ArrayList<VkSchool>?,
    @SerializedName("followers_count")
    val followersCount: Long?,
    @SerializedName("last_seen")
    val lastSeen: VkLastSeen?,
    val about: String?,
    val sex: Int?,
    @SerializedName("photo_100")
    val photoOneHundred: String?
) : Serializable

data class VkLastSeen(val time: Long?) : Serializable
data class VkLocation(val title: String?) : Serializable

data class VkCareer(
    @SerializedName("from")
    val yearFrom: Int?,
    @SerializedName("until")
    val yearUntil: Int?,
    val position: String?,
    val company: String?,
    @SerializedName("city_id")
    val cityID: Int?
) : Serializable

data class VkUniversity(
    val name: String?,
    @SerializedName("faculty_name")
    val facultyName: String?,
    @SerializedName("chair_name")
    val chairName: String?,
    @SerializedName("education_status")
    val educationStatus: String?,
    val graduation: Int?
) : Serializable

data class VkSchool(
    val name: String?,
    @SerializedName("year_from")
    val yearFrom: Int?,
    @SerializedName("year_to")
    val yearTo: Int?
) : Serializable

data class VkEducation(
    val title: String?,
    val yearFrom: Int?,
    val yearTo: Int?,
    val viewType: Int?,
    @SerializedName("faculty_name")
    val facultyName: String?,
    @SerializedName("chair_name")
    val chairName: String?,
) : Serializable
