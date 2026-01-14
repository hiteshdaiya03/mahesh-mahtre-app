package com.ward10.checker.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ward_people", indices = [Index(value = ["name","mobile"], unique = true)])
data class WardPerson(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val mobile: String,
    val isChecked: Boolean = false
)
