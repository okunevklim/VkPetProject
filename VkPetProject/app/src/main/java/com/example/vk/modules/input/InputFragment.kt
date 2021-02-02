package com.example.vk.modules.input

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vk.R
import com.example.vk.databinding.FragmentInputBinding
import com.example.vk.fragments.AlertDialogFragment
import com.example.vk.utils.Constants
import com.example.vk.utils.Helpers.hideKeyboard
import com.example.vk.utils.Helpers.showKeyboard
import com.example.vk.viewmodels.PostsViewModel


class InputFragment : Fragment(), IInputView {
    private lateinit var binding: FragmentInputBinding
    private lateinit var presenter: IInputPresenter
    private lateinit var postsViewModel: PostsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = InputPresenter()
        postsViewModel = ViewModelProvider(requireActivity())[PostsViewModel::class.java]
        binding.editText.requestFocus()
        showKeyboard(requireContext(), binding.editText)
        binding.closeInputText.setOnClickListener {
            closeInputFragment()
        }
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.isNotBlank()) {
                    setSendButton(R.drawable.ic_up_arrow_active_svg, true)
                } else {
                    setSendButton(R.drawable.ic_up_arrow_inactive_svg, false)
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
        binding.sendInputText.setOnClickListener {
            if (binding.editText.length() != 0) {
                val text = binding.editText.text.toString()
                presenter.sendPost(text)
            }
        }
    }

    override fun setSendButton(image: Int, isEnabled: Boolean) {
        binding.sendInputText.setImageResource(image)
        binding.sendInputText.isEnabled = isEnabled
    }

    override fun closeInputFragment() {
        hideKeyboard(requireContext(), binding.editText)
        requireActivity().onBackPressed()
    }

    override fun showSuccessfulToast() {
        Toast.makeText(
                requireContext(), getString(R.string.successful_post_toast),
                Toast.LENGTH_SHORT
        ).show()
    }

    override fun showInputProgressBar() {
        binding.inputProgressBar.visibility = View.VISIBLE
    }

    override fun hideInputProgressBar() {
        binding.inputProgressBar.visibility = View.GONE
    }


    override fun showPostAlertDialog(messageID: Int) {
        val alertDialog = AlertDialogFragment()
        val bundle = Bundle()
        bundle.putInt("alert_msg_res_id", messageID)
        alertDialog.arguments = bundle
        alertDialog.setTargetFragment(this, Constants.REQUEST_CODE)
        alertDialog.show(parentFragmentManager, "custom_dialog")
    }

    override fun handleIsRefreshing(value: Int) {
        postsViewModel.handleIsRefreshing(value)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }
}