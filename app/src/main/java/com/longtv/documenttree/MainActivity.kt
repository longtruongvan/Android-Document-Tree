package com.longtv.documenttree

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.documentfile.provider.DocumentFile
//https://stackoverflow.com/questions/67552027/android-11-action-open-document-tree-set-initial-uri-to-the-documents-folderff

class MainActivity : AppCompatActivity() {
    companion object {
        private const val CREATE_FILE = 1
        private const val PICK_PDF_FILE = 2
        private const val REQUEST_ACTION_OPEN_DOCUMENT_TREE = 9999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        accessFile()
    }

    private fun accessFile(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val storageManager = application.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val intent =  storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()

            //String startDir = "Android";
            //String startDir = "Download"; // Not choosable on an Android 11 device
            //String startDir = "DCIM";
            //String startDir = "DCIM/Camera";  // replace "/", "%2F"
            //String startDir = "DCIM%2FCamera";
            val startDir = "Documents"

            var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")

            var scheme = uri.toString()


            scheme = scheme.replace("/root/", "/document/")

            scheme += "%3A$startDir"

            uri = Uri.parse(scheme)

            intent.putExtra("android.provider.extra.INITIAL_URI", uri)


            startActivityForResult(intent, REQUEST_ACTION_OPEN_DOCUMENT_TREE)

            return
        }

    }


    private fun createFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "invoice.pdf")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, PICK_PDF_FILE)
    }

    private var treeUri = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE) {
            if (data != null) {
                data.data?.let { treeUri ->

                    // treeUri is the Uri of the file

                    // if life long access is required the takePersistableUriPermission() is used

                    contentResolver.takePersistableUriPermission(
                        treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    this.treeUri = treeUri.toString()

                    val docFile = DocumentFile.fromTreeUri(this, treeUri)
                    val resultFile = docFile!!.createFile("text/plain", "IMG_20200327_144048.txt")
                    val x = resultFile;
                }
            }
        }
    }
}