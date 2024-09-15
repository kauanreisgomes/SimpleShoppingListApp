package com.kauan.shoppinglist.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kauan.shoppinglist.model.ShoppingItem


@Database(entities = [ShoppingItem::class], version = 1)
abstract class AppDatabase() : RoomDatabase(){
    abstract fun getShoppingItemDao(): ShoppingItemDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shopping_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
