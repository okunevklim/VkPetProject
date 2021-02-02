package com.example.vk.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vk.R
import com.example.vk.databinding.LayoutItemSchoolBinding
import com.example.vk.databinding.LayoutItemUniversityBinding
import com.example.vk.models.VkEducation
import java.util.*

class EducationAdapter(
        private val education: ArrayList<VkEducation>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            UNIVERSITY_VIEW_TYPE -> UniversityViewHolder(
                    LayoutItemUniversityBinding.inflate(
                            LayoutInflater.from(parent.context)
                    )
            )
            SCHOOL_VIEW_TYPE -> SchoolViewHolder(
                    LayoutItemSchoolBinding.inflate(
                            LayoutInflater.from(
                                    parent.context
                            )
                    )
            )
            else -> throw IllegalStateException("Illegal view type")
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            UNIVERSITY_VIEW_TYPE -> (viewHolder as UniversityViewHolder).bind(education[position])
            SCHOOL_VIEW_TYPE -> (viewHolder as SchoolViewHolder).bind(education[position])
        }
    }

    override fun getItemCount(): Int {
        return education.size
    }

    override fun getItemViewType(position: Int): Int {
        return education[position].viewType ?: 0
    }

    class UniversityViewHolder(private val binding: LayoutItemUniversityBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(vkEducation: VkEducation) {
            binding.universityType.text = itemView.context.getString(R.string.university)
            if (vkEducation.yearTo != 0) {
                val educationName = "${vkEducation.title}' ${vkEducation.yearTo}"
                binding.educationName.text = educationName
            } else {
                val educationName = vkEducation.title
                binding.educationName.text = educationName
            }

            if (vkEducation.facultyName != null) {
                binding.facultyName.text = vkEducation.facultyName
                binding.facultyName.visibility = View.VISIBLE
            } else {
                binding.facultyName.visibility = View.GONE
            }

            if (vkEducation.chairName != null) {
                binding.specialtyName.text = vkEducation.chairName
                binding.specialtyName.visibility = View.VISIBLE
            } else {
                binding.specialtyName.visibility = View.GONE
            }
        }
    }

    class SchoolViewHolder(private val binding: LayoutItemSchoolBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(vkEducation: VkEducation) {
            binding.schoolType.text = itemView.context.getString(R.string.school)
            val schoolNameAndYear = "${vkEducation.title}' ${vkEducation.yearTo}"
            binding.schoolName.text = schoolNameAndYear

            binding.townAndYear.text = if (vkEducation.yearFrom != 0) {
                when {
                    vkEducation.yearTo != 0 -> {
                        "${vkEducation.yearFrom} - ${vkEducation.yearTo}"
                    }
                    vkEducation.yearTo == 0 -> {
                        "с ${vkEducation.yearFrom}"
                    }
                    else -> ""
                }
            } else if (vkEducation.yearFrom == 0 && vkEducation.yearTo != 0) {
                "до ${vkEducation.yearTo}"
            } else ""
        }
    }

    companion object {
        const val UNIVERSITY_VIEW_TYPE = 0
        const val SCHOOL_VIEW_TYPE = 1
    }
}