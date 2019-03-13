package com.ndomx.dicom

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpensesDao
{
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expenses WHERE contactId = :phone ORDER BY date DESC")
    fun getAllExpenses(phone: String): List<Expense>

    @Query("SELECT * FROM expenses WHERE amount > 0")
    fun getPositiveExpenses(): List<Expense>

    @Query("SELECT * FROM expenses WHERE contactId = :phone AND amount > 0 ORDER BY date DESC")
    fun getPositiveExpenses(phone: String): List<Expense>

    @Query("SELECT * FROM expenses WHERE amount < 0 ORDER BY date DESC")
    fun getNegativeExpenses(): List<Expense>

    @Query("SELECT * FROM expenses WHERE contactId = :phone AND amount < 0 ORDER BY date DESC")
    fun getNegativeExpenses(phone: String): List<Expense>

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalAmount(): LiveData<Int>

    @Query("SELECT SUM(amount) FROM expenses WHERE contactId = :phone")
    fun getAmount(phone: String): Int

    @Query("SELECT COUNT() FROM expenses")
    fun numberOfExpenses(): Int

    @Query("SELECT COUNT() FROM expenses WHERE contactId = :phone")
    fun numberOfExpenses(phone: String): Int

    @Insert
    fun addExpenses(vararg expenses: Expense): List<Long>

    @Delete
    fun deleteExpenses(vararg expenses: Expense)
}