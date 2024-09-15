package com.kauan.shoppinglist.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kauan.shoppinglist.model.ShoppingItem

@Dao
interface ShoppingItemDao {

    @Insert
    suspend fun insert(shoppingItem: ShoppingItem)

    @Update
    suspend fun update(shoppingItem: ShoppingItem)

    @Delete
    suspend fun delete(shoppingItem: ShoppingItem)

    @Query("SELECT * FROM shopping_items")
    suspend fun findAll(): List<ShoppingItem>
}