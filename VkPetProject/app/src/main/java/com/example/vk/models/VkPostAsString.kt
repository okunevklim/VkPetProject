package com.example.vk.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "vk_posts")
data class VkPostAsString(
    @PrimaryKey(autoGenerate = false)
    val id: Long?,
    @ColumnInfo(name = "content")
    val content: String?
) : Serializable