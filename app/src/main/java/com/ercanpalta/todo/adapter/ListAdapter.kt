package com.ercanpalta.todo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ercanpalta.todo.databinding.ListItemBinding
import com.ercanpalta.todo.enums.FilterType
import com.ercanpalta.todo.model.TaskList
import com.ercanpalta.todo.view.HomeFragment
import java.util.*
import kotlin.collections.ArrayList

class ListAdapter(private val dataSet: ArrayList<TaskList>, val fragment: HomeFragment): RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(private var binding:ListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(list:TaskList, fragment: HomeFragment, context: Context){
            binding.listText.text = list.name
            binding.colorCard.setCardBackgroundColor(ContextCompat.getColor(context, list.color))
            binding.root.setOnClickListener {
                if(binding.listText.text != "New List"){
                    fragment.changeCurrentListName(binding.listText.text.toString())
                    fragment.filterList(binding.listText.text.toString(), FilterType.List)
                }else if(binding.listText.text == "New List"){
                    fragment.navigateToAddList()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = dataSet[position]
        holder.bind(list, fragment, context)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}