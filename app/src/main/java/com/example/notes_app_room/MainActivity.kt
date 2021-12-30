package com.example.notes_app_room

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes_app_room.Model.Note
import com.example.notes_app_room.Model.NoteDatabase
import com.example.notes_app_room.Model.NoteRepository
import com.example.notes_app_room.Resource.RVAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var llMain: LinearLayout
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btSave: FloatingActionButton

    private lateinit var rvMain: RecyclerView

    private val noteDao by lazy { NoteDatabase.getDatabase(this).noteDao() }
    private val repository by lazy { NoteRepository(noteDao) }
    private lateinit var notes: ArrayList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectView()


        btSave.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etContent.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                addNote(title, content)
                //Toast.makeText(this, "Save Successes", Toast.LENGTH_LONG).show()
                Snackbar.make(llMain, "Save Successes", Snackbar.LENGTH_LONG).show()
            } else {
                //Toast.makeText(this, "Save Successes", Toast.LENGTH_LONG).show()
                Snackbar.make(llMain, "Please Enter something!", Snackbar.LENGTH_LONG).show()
            }

            clearText()
            updateRV()
        }

        getItemsList()
        updateRV()

    }


    private fun addNote(noteTitle: String, noteContent: String) {
        CoroutineScope(IO).launch {
            repository.addNote(Note(0, noteTitle, noteContent))
        }
    }

    private fun editNote(noteID: Int, noteTitle: String, noteContent: String) {
        CoroutineScope(IO).launch {
            repository.updateNote(Note(noteID, noteTitle, noteContent))
        }
    }

    fun deleteNote(noteID: Int) {
        CoroutineScope(IO).launch {
            repository.deleteNote(Note(noteID, "", ""))
        }
    }


    private fun getItemsList() {
        CoroutineScope(IO).launch {
            val data = async {
                repository.getNotes
            }.await()
            if (data.isNotEmpty()) {
                notes = data as ArrayList<Note>
                updateRV()
            } else {
                Log.e("MainActivity", "Unable to get data")
            }
        }
    }

    private fun connectView() {
        llMain = findViewById(R.id.llMain)
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btSave = findViewById(R.id.btSave)
        rvMain = findViewById(R.id.rvMain)
        notes = arrayListOf()
    }


    private fun clearText() {
        etTitle.text.clear()
        etContent.text.clear()
        etTitle.clearFocus()
        etContent.clearFocus()
    }


    private fun updateRV() {
        rvMain.adapter = RVAdapter(notes, this)
        rvMain.layoutManager = LinearLayoutManager(this)
    }


    fun raiseDialog(id: Int) {
        var bodyOfDialog = LinearLayout(this)

        val dialogBuilder = AlertDialog.Builder(this)

        val updatedTitle = EditText(this)
        val updatedContent = EditText(this)

        updatedTitle.hint = "Enter new Title..."
        updatedContent.hint = "Enter new Text..."

        dialogBuilder
            .setCancelable(false)
            .setPositiveButton("Save", DialogInterface.OnClickListener { _, _ ->
                if (updatedTitle.text.isNotEmpty() && updatedContent.text.isNotEmpty()) {
                    editNote(
                        id,
                        updatedTitle.text.toString(),
                        updatedContent.text.toString()
                    )
                } else {
                    Toast.makeText(this, "Please Enter Something!", Toast.LENGTH_LONG).show()
                }
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("Update Note")

        bodyOfDialog.orientation
        bodyOfDialog.addView(updatedTitle)
        bodyOfDialog.addView(updatedContent)

        alert.setView(bodyOfDialog)
        alert.show()
    }


}