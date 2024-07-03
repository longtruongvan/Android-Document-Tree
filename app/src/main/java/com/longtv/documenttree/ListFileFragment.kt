package com.longtv.documenttree

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListFileFragment(private val documentFile: DocumentFile, private val listFileListener: ListFileListener) : Fragment(), FileAdapter.ItemClickListener {
    private var recyclerViewFiles: RecyclerView? = null
    private var fileAdapter: FileAdapter? = null
    private var icBack: ImageView? = null

    interface ListFileListener {
        fun itemClick(documentFile: DocumentFile)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_list_file, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initListeners() {
        icBack?.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
    }

    private fun initViews() {
        recyclerViewFiles = view?.findViewById(R.id.recyclerViewFiles)
        fileAdapter = FileAdapter(this)
        recyclerViewFiles?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewFiles?.adapter = fileAdapter

        icBack = view?.findViewById(R.id.ic_back)
        showListFile()

    }

    private fun showListFile() {
        val documentFiles = documentFile?.listFiles()
        val files = documentFiles?.filter { it.isFile }
        fileAdapter?.setData(documentFiles ?: arrayOf())
    }

    override fun itemClick(documentFile: DocumentFile) {
        if(documentFile.isFile){
            context?.let { showFileInfo(it) }
            return
        }
        listFileListener.itemClick(documentFile)
    }

    private fun showFileInfo(context: Context) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("File Info")
        val message = "Name: ${documentFile.name}\n Size: ${documentFile.length()} bytes\n Date: ${documentFile.lastModified()}"
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.create().show()
    }
}