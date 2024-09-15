package com.kauan.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kauan.shoppinglist.dao.AppDatabase
import com.kauan.shoppinglist.screens.ShoppingListScreen
import com.kauan.shoppinglist.ui.theme.ShoppingListTheme
import com.kauan.shoppinglist.dao.DataStorage

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val db = AppDatabase.getDatabase(LocalContext.current)
                    val dataStorage = DataStorage(db)
                    ShoppingListScreen().ShoppingListAppData(dataStorage);
                }
            }
        }
    }
}