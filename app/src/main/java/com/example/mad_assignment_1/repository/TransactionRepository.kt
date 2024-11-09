package com.example.mad_assignment_1.repository

import com.example.mad_assignment_1.Transaction
import com.example.mad_assignment_1.data.TransactionDao

class TransactionRepository(private val transactionDao: TransactionDao) {

     fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

     fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

     fun getAllTransactions(): List<Transaction> {
        return transactionDao.getAllTransactions()
    }
}
