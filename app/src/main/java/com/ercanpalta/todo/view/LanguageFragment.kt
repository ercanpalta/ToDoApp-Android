package com.ercanpalta.todo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ercanpalta.todo.MainActivity
import com.ercanpalta.todo.R
import com.ercanpalta.todo.databinding.FragmentLanguageBinding


class LanguageFragment : Fragment() {

    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("com.ercanpalta.todo",
            AppCompatActivity.MODE_PRIVATE
        )

        when(sharedPreferences.getString("locale","gb")){
            "gb" -> binding.checkIconEnglish.setImageResource(R.drawable.ic_checked_24)
            "de" -> binding.checkIconDeutsch.setImageResource(R.drawable.ic_checked_24)
        }

        binding.englishCard.setOnClickListener {
            sharedPreferences.edit().putString("locale","gb").apply()
            (activity as MainActivity).setLocale("gb")
            (activity as MainActivity).refresh()
        }

        binding.deutschCard.setOnClickListener {
            sharedPreferences.edit().putString("locale","de").apply()
            (activity as MainActivity).setLocale("de")
            (activity as MainActivity).refresh()
        }
    }
}