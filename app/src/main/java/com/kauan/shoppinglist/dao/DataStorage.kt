package com.kauan.shoppinglist.dao

import com.kauan.shoppinglist.model.ShoppingItem
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class DataStorage(db: AppDatabase){

    private val dao = db.getShoppingItemDao()

    fun insert(item: ShoppingItem, callback: (List<ShoppingItem>) -> Unit){
        GlobalScope.launch(Dispatchers.IO) {
            dao.insert(item)
            val list = dao.findAll() // Chama o DAO no IO Dispatcher
            // Retorne ao Main thread para atualizar a interface
            withContext(Dispatchers.Main) {
                callback(list) // Chama o callback com a lista preenchida
            }
        }
    }

    fun update(item: ShoppingItem, callback: (List<ShoppingItem>) -> Unit){
        GlobalScope.launch(Dispatchers.IO) {
            dao.update(item)
            val list = dao.findAll() // Chama o DAO no IO Dispatcher
            // Retorne ao Main thread para atualizar a interface
            withContext(Dispatchers.Main) {
                callback(list) // Chama o callback com a lista preenchida
            }
        }
    }

    fun delete(item: ShoppingItem, callback: (List<ShoppingItem>) -> Unit){
        GlobalScope.launch(Dispatchers.IO) {
            dao.delete(item)
            val list = dao.findAll() // Chama o DAO no IO Dispatcher
            // Retorne ao Main thread para atualizar a interface
            withContext(Dispatchers.Main) {
                callback(list) // Chama o callback com a lista preenchida
            }
        }
    }

    fun findAll(callback: (List<ShoppingItem>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val list = dao.findAll() // Chama o DAO no IO Dispatcher
            // Retorne ao Main thread para atualizar a interface
            withContext(Dispatchers.Main) {
                callback(list) // Chama o callback com a lista preenchida
            }
        }
    }

}