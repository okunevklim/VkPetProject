package com.example.vk.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vk.databinding.LayoutItemCareerBinding
import com.example.vk.models.VkCareer
import com.example.vk.models.VkCityName

class CareerAdapter(
        private val career: List<VkCareer>,
        private val cities: List<VkCityName>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CareerInfoViewHolder(LayoutItemCareerBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as CareerInfoViewHolder).bind(career[position], cities)
    }

    override fun getItemCount(): Int {
        return career.size
    }

    class CareerInfoViewHolder(private val binding: LayoutItemCareerBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(vkCareer: VkCareer, cities: List<VkCityName>) {
            if (vkCareer.company.isNullOrEmpty()) {
                binding.companyName.visibility = View.GONE
            } else binding.companyName.text = vkCareer.company
            binding.countryAndYear.text =
                    if (vkCareer.cityID != null && vkCareer.yearFrom != null && vkCareer.yearUntil != null) {
                        "${cities.find { it.id == vkCareer.cityID }?.title}, ${vkCareer.yearFrom} - ${vkCareer.yearUntil}"
                    } else if (vkCareer.cityID == null && vkCareer.yearFrom != null && vkCareer.yearUntil != null) {
                        "${vkCareer.yearFrom} - ${vkCareer.yearUntil}"
                    } else if (vkCareer.cityID != null && vkCareer.yearFrom == null && vkCareer.yearUntil != null) {
                        "${cities.find { it.id == vkCareer.cityID }?.title}, до ${vkCareer.yearUntil}"
                    } else if (vkCareer.cityID != null && vkCareer.yearFrom != null && vkCareer.yearUntil == null) {
                        "${cities.find { it.id == vkCareer.cityID }?.title}, с ${vkCareer.yearFrom}"
                    } else {
                        ""
                    }
            if (vkCareer.position != null) {
                binding.positionName.visibility = View.VISIBLE
                binding.positionName.text = vkCareer.position
            } else {
                binding.positionName.visibility = View.GONE
            }
        }
    }
}