package com.example.notes_app_room.Model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "NotesTable")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val pk: Int,
    val noteTitle: String,
    val noteContent: String
)
