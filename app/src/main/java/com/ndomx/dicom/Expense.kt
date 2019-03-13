package com.ndomx.dicom

import androidx.room.*
import java.util.*

@Entity(tableName = "expenses")
data class Expense(
    var title: String,
    var description: String,
    var contactId: String,
    var amount: Int,
    var date: Date,

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)