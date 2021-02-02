package com.example.vk

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.vk.databinding.ActivityLoginBinding
import com.example.vk.network.NetworkConnection
import com.example.vk.utils.PreferencesHelper
import com.example.vk.utils.SnackBarHelper.showSnackBar
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkValidToken()
    }

    private fun checkInternetConnection() {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, { isConnected ->
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)
            if (isConnected) {
                showLoginButton()
                binding.loginToVK.setOnClickListener {
                    loginToVk()
                }
            } else {
                runOnUiThread {
                    showSnackBar(
                            binding.loginLayout,
                            getString(R.string.disconnected),
                            R.drawable.bg_rounded_error_snackbar,
                            R.drawable.ic_exclamation
                    )
                }
                hideLoginButton()
            }
        }
        )
    }

    private fun hideLoginButton() {
        binding.loginToVK.visibility = View.GONE
        binding.disconnectedImage.visibility = View.VISIBLE
        binding.disconnectedText.visibility = View.VISIBLE
    }

    private fun showLoginButton() {
        binding.loginToVK.visibility = View.VISIBLE
        binding.disconnectedImage.visibility = View.GONE
        binding.disconnectedText.visibility = View.GONE
    }

    private fun checkValidToken() {
        if (PreferencesHelper.getRefreshToken().isNotBlank()) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        } else {
            checkInternetConnection()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {

            override fun onLogin(token: VKAccessToken) {
                PreferencesHelper.putRefreshToken(token.accessToken)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            override fun onLoginFailed(errorCode: Int) {
                AlertDialog.Builder(this@LoginActivity)
                        .setMessage(getString(R.string.auth_cancelled))
                        .show()
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun loginToVk() {
        VK.login(
                this,
                arrayListOf(
                        VKScope.WALL,
                        VKScope.PHOTOS,
                        VKScope.OFFLINE,
                        VKScope.FRIENDS,
                        VKScope.GROUPS
                )
        )
    }

}