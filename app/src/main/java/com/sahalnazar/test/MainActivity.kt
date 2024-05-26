package com.sahalnazar.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.sahalnazar.test.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var setupNavController: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUi()
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        setupNavController(navController)
        setupClickHandlers(navController)
    }

    private fun setupClickHandlers(navController: NavController) {
        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun setupNavController(navController: NavController) {
        setupNavController = AppBarConfiguration(navController.graph)
    }

    private fun initUi() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}