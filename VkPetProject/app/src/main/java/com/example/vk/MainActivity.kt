package com.example.vk

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateToFragment()
        hideProgressBar()
    }

    private fun hideProgressBar() {
        runOnUiThread { binding.mainProgressBar.visibility = View.GONE }
    }

    private fun navigateToFragment() {
        (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController.setGraph(
                R.navigation.nav_graph
        )
        val navController = findNavController(R.id.navHostFragment)
        navController.navigate(R.id.listFragment)
    }

    override fun onBackPressed() {
        if (findNavController(R.id.navHostFragment).currentDestination?.id != R.id.listFragment) {
            super.onBackPressed()
        }
    }

}