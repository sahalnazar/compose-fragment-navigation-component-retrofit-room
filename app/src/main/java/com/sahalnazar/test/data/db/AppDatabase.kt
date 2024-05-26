package com.sahalnazar.test.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sahalnazar.test.data.model.ProductListResponseItem

@Database(entities = [ProductListResponseItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}