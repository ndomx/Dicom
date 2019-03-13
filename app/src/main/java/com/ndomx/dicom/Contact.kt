package com.ndomx.dicom

import androidx.room.*

@Entity(tableName = "contacts")
data class Contact(
    var name: String,
    @PrimaryKey
    var phone: String
)