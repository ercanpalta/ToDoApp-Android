package com.ercanpalta.todo.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ercanpalta.todo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationSettings.setOnClickListener {
            val settingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            startActivity(settingsIntent)
        }

        // to set theme when app started
        val sharedPreferences = requireContext().getSharedPreferences("com.ercanpalta.todo",
            AppCompatActivity.MODE_PRIVATE
        )
        val isDarkModeOpen = sharedPreferences.getBoolean("isDarkModeOpen",false)
        binding.themeSwitch.isChecked = isDarkModeOpen

        // to change theme when switch clicked
        binding.themeSwitch.setOnClickListener {
            if (binding.themeSwitch.isChecked){
                sharedPreferences.edit().putBoolean("isDarkModeOpen",true).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                sharedPreferences.edit().putBoolean("isDarkModeOpen",false).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}