package ru.netology.neworkapp.ui

import android.os.Bundle


import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.netology.neworkapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfig: AppBarConfiguration

    fun setActionBarTitle(title: String) {
        binding.toolbar.title = title
    }

    fun onClickFabAddPost(view: View) {}
    fun onClickFabAddEvent(view: View) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment

        navController = navHostFragment.navController

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val bottomNavView = binding.bottomNavView.apply {
            background = null
            menu.findItem(R.id.fab_in_menu).isEnabled = false
        }

        val topLevelDestinations = setOf(
            R.id.navigation_home,
            R.id.navigation_events,
            R.id.navigation_my_profile,
        )

        appBarConfig = AppBarConfiguration.Builder(topLevelDestinations).build()
        NavigationUI.setupActionBarWithNavController(
            this,
            navController,
            appBarConfig
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> {
                    toolbar.visibility = View.GONE
                    bottomNavView.visibility = View.GONE
                    binding.bottomAppBar.visibility = View.GONE
                    binding.fabLayout.visibility = View.GONE
                }

                R.id.registrationFragment -> {
                    toolbar.visibility = View.GONE
                    bottomNavView.visibility = View.GONE
                    binding.bottomAppBar.visibility = View.GONE
                    binding.fab.visibility = View.GONE
                }
                R.id.newPostFragment -> {
                    bottomNavView.visibility = View.GONE
                    binding.bottomAppBar.visibility = View.GONE
                    binding.fabLayout.visibility = View.GONE
                }

                R.id.newEventFragment -> {
                    bottomNavView.visibility = View.GONE
                    binding.bottomAppBar.visibility = View.GONE
                    binding.fabLayout.visibility = View.GONE
                }
                else -> {
                    toolbar.visibility = View.VISIBLE
                    bottomNavView.visibility = View.VISIBLE
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.fabLayout.visibility = View.VISIBLE
                }
            }
        }

        bottomNavView.setupWithNavController(navController)

        binding.fabAddPost.setOnClickListener {
            navController.navigate(R.id.newPostFragment)
        }

        binding.fabAddEvent.setOnClickListener {
            navController.navigate(R.id.newEventFragment)
        }



        authViewModel.authState.observe(this) {user ->
            if (!authViewModel.authenticated) {
                binding.fabLayout.visibility = View.GONE
                binding.loginButton.visibility = View.VISIBLE
                binding.signUpButton.visibility = View.VISIBLE
                binding.bottomNavView.menu.findItem(R.id.navigation_my_profile).isEnabled = false
                binding.bottomNavView.menu.findItem(R.id.navigation_my_profile).isVisible = false
            } else {
                binding.fab.visibility = View.VISIBLE
                binding.loginButton.visibility = View.GONE
                binding.signUpButton.visibility = View.GONE
                binding.bottomNavView.menu.findItem(R.id.navigation_my_profile).isEnabled = true
                binding.bottomNavView.menu.findItem(R.id.navigation_my_profile).isVisible = true
            }
        }

        binding.loginButton.setOnClickListener {
            navController.navigate(R.id.loginFragment)
        }

        binding.signUpButton.setOnClickListener {
            navController.navigate(R.id.registrationFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}