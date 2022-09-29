package com.ercanpalta.todo.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ercanpalta.todo.R
import com.ercanpalta.todo.databinding.FragmentAddBinding
import com.ercanpalta.todo.databinding.FragmentReminderBinding


class ReminderFragment : Fragment() {
    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var title = "title"
        var content = "content"

        arguments?.let {
            title = it.getString("title", "title")
            content = it.getString("content", "content")
        }

        binding.taskNameTv.text = title
        binding.detailsText.text = content

    }



}