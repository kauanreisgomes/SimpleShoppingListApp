package com.kauan.shoppinglist.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.kauan.shoppinglist.model.ShoppingItem
import com.kauan.shoppinglist.model.ShoppingItemMutable
import com.kauan.shoppinglist.ui.theme.ShoppingListTheme
import com.kauan.shoppinglist.dao.DataStorage

class ShoppingListScreen{

    private lateinit var dataStorage: DataStorage;
    private var sItems = mutableStateListOf<ShoppingItem>()
    private val callbackDb: (List<ShoppingItem>) -> Unit = {
        sItems.clear()
        sItems.addAll(it)
    }

    @Composable
    fun ShoppingListAppData(dataStorage: DataStorage){

        this.dataStorage = dataStorage

        sItems = remember {
            mutableStateListOf()
        }

        dataStorage.findAll(callbackDb)

        val showDialog = remember {
            mutableStateOf(false)
        }
        val showRemoveDialog = remember {
            mutableStateOf(false)
        }
        val shoppingItem by remember { mutableStateOf(ShoppingItemMutable(
            name = mutableStateOf(""),
            quantity = mutableStateOf(""),
            shoppingItem = mutableStateOf(ShoppingItem(0, "", 0))
        ))}

        ShoppingListApp(
            showDialog = showDialog,
            removeDialog = showRemoveDialog,
            shoppingItem = shoppingItem
        )
    }

    @Composable
    fun ShoppingListApp(
        showDialog: MutableState<Boolean> = mutableStateOf(false),
        removeDialog: MutableState<Boolean> = mutableStateOf(false),
        shoppingItem: ShoppingItemMutable
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { showDialog.value = true  }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Icon")
                Text("Add Item")
            }
            ShoppingTable(
                onEditClick = {
                    shoppingItem.fillByShoppingItem(it)
                    showDialog.value = true
                },
                onDeleteClick = {
                    shoppingItem.fillByShoppingItem(it)
                    removeDialog.value = true
                }
            )
        }

        if(showDialog.value){
            AlertDialogAddItem(showDialog, shoppingItem)
        }

        if(removeDialog.value){
            AlertDialogRemoveItem(shoppingItem, removeDialog) {
                removeItem(shoppingItem.shoppingItem.value)
            }
        }
    }

    @Composable
    fun AlertDialogAddItem(
        showDialog: MutableState<Boolean>,
        shoppingItem: ShoppingItemMutable
    ){
        val isEdition = shoppingItem.shoppingItem.value.id != 0L
        var label = "Add"
        if(isEdition) label = "Edit"

        val context = LocalContext.current;

        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Button(onClick = {
                        resetItem(shoppingItem)
                        showDialog.value = false
                    }) {
                        Text("Cancel")
                    }

                    Button(onClick = {
                        addItem(shoppingItem)
                        showDialog.value = false
                    }) {
                        Text(label)
                    }
                }
            },
            title = {
                Text("$label Shopping Item")
            },
            text = {
                Column {

                    if(isEdition) {
                        Text("ID: ${shoppingItem.shoppingItem.value.id}")
                    }

                    OutlinedTextField(
                        value = shoppingItem.name.value,
                        onValueChange = {
                            if(it.length > 255){
                                Toast
                                    .makeText(context, "Maximum characters is 255", Toast.LENGTH_SHORT)
                                    .show()
                                return@OutlinedTextField;
                            }
                            shoppingItem.name.value = it
                        },
                        singleLine = true,
                        label = {Text("Name (Máx: 255)")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dp(8f))
                    )

                    OutlinedTextField(
                        value = shoppingItem.quantity.value,
                        onValueChange = {
                            if(it.isDigitsOnly()){
                                shoppingItem.quantity.value = it
                            }else{
                                Toast
                                    .makeText(context, "Only numbers for Quantity", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        label = {Text("Quantity")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dp(8f))
                    )

                }
            }
        )
    }

    @Composable
    fun AlertDialogRemoveItem(
        shoppingItem: ShoppingItemMutable,
        removeDialog: MutableState<Boolean>, onConfirm: () -> Unit){
        AlertDialog(
            onDismissRequest = { removeDialog.value = false },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Button(onClick = {
                        onConfirm()
                        resetItem(shoppingItem)
                        removeDialog.value = false
                    }) {
                        Text("Confirm")
                    }
                    Button(onClick = {
                        resetItem(shoppingItem)
                        removeDialog.value = false
                    }) {
                        Text("Cancel")
                    }
                }

            },
            title = {
                Text("Removing a Shopping Item")
            },
            text = {
                Column {
                    Text("You're shure want remove the Item '${shoppingItem.name.value}' ?")
                }
            }
        )
    }


    @Composable
    fun ShoppingTable(onEditClick: (item: ShoppingItem) -> Unit, onDeleteClick: (item: ShoppingItem) -> Unit){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dp(16f))
        ){
            items(sItems.toList()){
                ShoppingListItem(
                    it,
                    onEditClick = {onEditClick(it)},
                    onDeleteClick = {onDeleteClick(it)}
                )
            }
        }
    }

    @Composable
    fun ShoppingListItem(
        shoppingItem: ShoppingItem,
        onEditClick: () -> Unit,
        onDeleteClick: () -> Unit,
    ){
        val headerList = TextStyle(
            color= Color(102, 102, 204),
            fontSize = 12f.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )

        Column {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(Dp(3F), Color(153, 102, 204)), RoundedCornerShape(Dp(20F)))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column{
                    Text("Name", style = headerList)
                    Text(shoppingItem.name, modifier = Modifier
                        .wrapContentHeight()
                        .width(150.dp))
                }
                Column {
                    Text("Quantity", style = headerList)
                    Text(shoppingItem.quantity.toString())
                }

                Row(
                    modifier = Modifier.padding(8.dp)
                ) {
                    IconButton(onClick = onEditClick){
                        Icon(imageVector = Icons.Default.Edit, "Edit Button")
                    }

                    IconButton(onClick = {onDeleteClick()}){
                        Icon(imageVector = Icons.Default.Delete, "Exclusion Button")
                    }
                }

            }

            Spacer(modifier = Modifier.height(6.dp))

        }
    }

    private fun addItem(mutableItem: ShoppingItemMutable){

        if(mutableItem.name.value.isBlank()) return;

        if(mutableItem.shoppingItem.value.id == 0L) {
            dataStorage.insert(ShoppingItem(
                name = mutableItem.name.value,
                quantity = mutableItem.quantity.value.toIntOrNull() ?: 1
            ), callbackDb)
        }else{
            mutableItem.shoppingItem.value.fillByMutable(mutableItem)
            dataStorage.update(mutableItem.shoppingItem.value, callbackDb)
        }

        resetItem(mutableItem)
    }

    private fun removeItem(item: ShoppingItem){
        dataStorage.delete(item, callbackDb)
    }

    private fun resetItem(shoppingItem: ShoppingItemMutable){
        shoppingItem.name.value = ""
        shoppingItem.quantity.value = ""
        shoppingItem.shoppingItem.value = ShoppingItem(0, "", 0)
    }

    @Preview(showBackground = true)
    @Composable
    fun ShoppingListPreview(){
        ShoppingListTheme{
            sItems = remember {
                mutableStateListOf()
            }
            val showDialog = remember {
                mutableStateOf(false)
            }
            val showRemoveDialog = remember {
                mutableStateOf(false)
            }
            val shoppingItem by remember { mutableStateOf(ShoppingItemMutable(
                name = mutableStateOf(""),
                quantity = mutableStateOf(""),
                shoppingItem = mutableStateOf(ShoppingItem(0, "", 0))
            ))}

            sItems.add(ShoppingItem(1, "Maça", 2))
            sItems.add(ShoppingItem(2, "Banana", 1))
            sItems.add(ShoppingItem(3, "Orange", 4))
            sItems.add(ShoppingItem(3, "Fruits green like a Lemon or Mango", 4))

            ShoppingListApp(
                showDialog = showDialog,
                removeDialog = showRemoveDialog,
                shoppingItem = shoppingItem
            )
        }
    }
}

