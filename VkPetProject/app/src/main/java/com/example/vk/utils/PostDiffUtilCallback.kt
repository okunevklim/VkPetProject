package com.example.vk.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.vk.models.VkPost

class PostDiffUtilCallback(
        private val newList: List<VkPost>,
        private val oldList: ArrayList<VkPost>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].equals(newList[newItemPosition].id)
    }
}