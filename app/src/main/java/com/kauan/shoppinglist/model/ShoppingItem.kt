package com.kauan.shoppinglist.model

import androidx.compose.runtime.MutableState
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0L,
    var name: String,
    var quantity: Int
){
    fun fillByMutable(mutable: ShoppingItemMutable){
        name = mutable.name.value.trim()
        quantity = mutable.quantity.value.filter { ti2 -> ti2.isDigit() }.toInt()
    }

}

data class ShoppingItemMutable(
    var name: MutableState<String>,
    var quantity: MutableState<String>,
    var shoppingItem: MutableState<ShoppingItem>
){
    fun fillByShoppingItem(shoppingItem: ShoppingItem){
        name.value = shoppingItem.name.trim()
        quantity.value = shoppingItem.quantity.toString()
        this.shoppingItem.value = shoppingItem
    }
}