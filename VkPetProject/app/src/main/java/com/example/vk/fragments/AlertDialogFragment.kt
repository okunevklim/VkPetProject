package com.example.vk.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.vk.databinding.LayoutAlertDialogBinding


class AlertDialogFragment : DialogFragment() {
    private lateinit var binding: LayoutAlertDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = LayoutAlertDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
        isCancelable = false
        binding.alertPositiveButton.setOnClickListener {
            dialog?.cancel()
        }
        val dialog = builder.create()
        val alertMessageID = requireArguments().getInt("alert_msg_res_id")
        binding.alertMessage.setText(alertMessageID)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}