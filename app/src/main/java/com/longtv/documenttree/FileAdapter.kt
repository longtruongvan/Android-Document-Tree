package com.longtv.documenttree

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileAdapter(private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<FileAdapter.ViewHolder>() {
    private val files = mutableListOf<DocumentFile>()

    fun setData(files: Array<DocumentFile>) {
        this.files.clear()
        this.files.addAll(files)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fileName = itemView.findViewById<TextView>(R.id.file_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_file_item_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fileName.text = files[position].name
        holder.itemView.setOnClickListener {
            var uri = "${files[position].uri.toString()}"
            itemClickListener.itemClick(files[position])
        }
    }

    interface ItemClickListener {
        fun itemClick(documentFile: DocumentFile)
    }
}