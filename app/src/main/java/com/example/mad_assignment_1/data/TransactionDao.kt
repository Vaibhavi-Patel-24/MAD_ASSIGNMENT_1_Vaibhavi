package com.example.mad_assignment_1.data

import androidx.room.*
import com.example.mad_assignment_1.Transaction

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTransaction(transaction: Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transaction_table WHERE id = :transactionId")
    fun deleteTransactionById(transactionId: Int)

    @Query("SELECT * FROM transaction_table ORDER BY date DESC")
    fun getAllTransactions(): List<Transaction>
}
