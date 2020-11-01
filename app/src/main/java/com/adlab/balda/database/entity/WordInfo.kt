package com.adlab.balda.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WordInfo(
        @PrimaryKey val id: Long,
        @ColumnInfo(name = "word") val word: String,
        @ColumnInfo(name = "code") val code: Long?,
        @ColumnInfo(name = "code_parent") val codeParent: Long?,
        @ColumnInfo(name = "gender") val gender: String?,
        @ColumnInfo(name = "wcase") val wcase: String?,
        @ColumnInfo(name = "soul") val soul: Int?
)