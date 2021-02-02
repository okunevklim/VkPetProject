package com.example.vk.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "vk_cities")
data class VkCityName(
    @PrimaryKey(autoGenerate = false)
    val id: Int?,
    @ColumnInfo(name = "title")
    val title: String?
) : Serializable