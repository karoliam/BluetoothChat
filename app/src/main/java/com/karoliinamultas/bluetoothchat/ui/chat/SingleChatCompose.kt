package com.karoliinamultas.bluetoothchat.ui.chat


import android.bluetooth.BluetoothAdapter
import android.widget.Toast
import androidx.compose.material3.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.karoliinamultas.bluetoothchat.*
import com.karoliinamultas.bluetoothchat.R
import kotlinx.coroutines.launch


private const val TAG = "ChatCompose"


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ChatWindow(navController: NavController, mBluetoothAdapter: BluetoothAdapter, model:MyViewModel){
    //Statusbar
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(MaterialTheme.colorScheme.surface)
    // Create a boolean variable
    // to store the display menu state
    var mDisplayMenu by remember { mutableStateOf(false) }

    // fetching local context
    val mContext = LocalContext.current
    //Topbar
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                elevation = 5.dp,
                backgroundColor = MaterialTheme.colorScheme.surface,
                title = {
                    Text(
                        "Restroom Chat",
                        Modifier.padding(40.dp, 0.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.ShowChats.route) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back button"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu button"
                        )
                    }
                },
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {
                Chats(Modifier, navController,mBluetoothAdapter, model)
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowChat(message:String, modifier: Modifier = Modifier) {

    val textColors_random = listOf(
        Color(0xFF00FDDC),
        Color(0xFFFF729F),
        Color(0xFF04E762),
        Color(0xFFFDE74C),
        Color(0xFFFF4365))
    val randomTexts = textColors_random.random()

    val backgroundColors_random = listOf(
        Color(0xFF111D4A),
        Color(0xFF43AA8B),
        Color(0xFF8B635C),
        Color(0xFF60594D),
        Color(0xFF93A29B))
    val randomBack = backgroundColors_random.random()

    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .width(150.dp)
                .padding(5.dp),

            colors = CardDefaults.cardColors(containerColor = randomBack),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Text(text = message, color = randomTexts, modifier = Modifier.padding(10.dp))
        }
    }
}


@Composable
fun Chats( modifier: Modifier = Modifier, navController: NavController,mBluetoothAdapter: BluetoothAdapter, model: MyViewModel) {


    val inputvalue = remember { mutableStateOf(TextFieldValue()) }


    Column(modifier = Modifier.fillMaxSize()) {

        Surface(modifier = Modifier
            .padding(all = Dp(0f))
            .fillMaxHeight(fraction = 0.89f)) {
            ChatsList(model)
        }
        InputField( modifier, navController, mBluetoothAdapter, model)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun InputField( modifier: Modifier = Modifier, navController: NavController, mBluetoothAdapter: BluetoothAdapter, model: MyViewModel) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var text by rememberSaveable { mutableStateOf("") }
    //BotMenu
    // Declaring a Boolean value to
    // store bottom sheet collapsed state
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState =
    BottomSheetState(BottomSheetValue.Collapsed))

    // Declaring Coroutine scope
    val coroutineScope = rememberCoroutineScope()

        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent =  {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))) {
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.End) {

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                                            bottomSheetScaffoldState.bottomSheetState.collapse()
                                        } else {
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(60.dp)
                                    .padding(0.dp, 6.dp, 0.dp, 0.dp),
                                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.background),
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "Localized description"
                                    )
                                }
                            )}
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row() {
                            IconButton(
                                onClick = { navController.navigate(Screen.DrawingPad.route) },
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(60.dp)
                                    .padding(0.dp, 6.dp, 0.dp, 0.dp),
                                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.background),
                                content = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.draw),
                                        contentDescription = "Localized description"
                                    )
                                }
                            )
                            CameraButton(context)
                            GalleryButton(context)
                        }
                    }
                }
            },
            sheetPeekHeight = 0.dp
        ){
                Box(
                    Modifier
                        .drawWithCache {
                            val offsetY = (-5).dp.toPx()
                            val shadowColor = Color.Black
                            val shadowAlpha = 0.3f
                            val shadowBlur = 6.dp.toPx()
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    shadowColor.copy(alpha = shadowAlpha),
                                    Offset(0f, offsetY),
                                    size = Size(size.width, shadowBlur),
                                    alpha = shadowAlpha,
                                    style = Fill
                                )
                            }
                        }
                        .background(color = MaterialTheme.colorScheme.surface)
                        .fillMaxWidth()) {
                    Row(
                        Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                    ) {
                        TextField(
                            value = text,
                            onValueChange = {
                                text = it
                            },
                            Modifier
                                .weight(9f)
                                .padding(5.dp),
                            shape = RoundedCornerShape(5.dp),
                            placeholder = { Text(text = "Enter your message", color = Color(0xFF242124).copy(0.5f)) },
                            trailingIcon = {
                                IconButton(
                                    onClick = { coroutineScope.launch {
                                        if (bottomSheetScaffoldState.bottomSheetState.isCollapsed){
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }else{
                                            bottomSheetScaffoldState.bottomSheetState.collapse()
                                        }
                                    } },
                                    modifier = Modifier
                                        .height(60.dp)
                                        .width(60.dp)
                                        .padding(0.dp, 6.dp, 0.dp, 0.dp),
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color(0xFF242124)),
                                    content = {
                                        Icon(
                                            imageVector = Icons.Filled.KeyboardArrowUp,
                                            contentDescription = "Localized description"
                                        )
                                    }
                                )
                            },
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                            ),
                            textStyle = TextStyle(
                                color = Color(0xFF242124),
                                fontSize = TextUnit.Unspecified,
                                fontFamily = FontFamily.SansSerif
                            ),
                            maxLines = 1,
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFF5FEFD),
                                textColor = Color(0xFF242124),
                                disabledTextColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent)
                        )

                IconButton(
                    onClick = {
                        if(!model.mSending.value!!){
                        model.sendMessage(mBluetoothAdapter, mBluetoothAdapter.bluetoothLeScanner, text, "")
                        text = ""
                        } else {
                            Toast.makeText(context, "sending message", Toast.LENGTH_SHORT).show()
                        }
                              },
                    modifier = Modifier
                        .height(60.dp)
                        .width(60.dp)
                        .padding(0.dp, 6.dp, 0.dp, 0.dp),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Localized description"
                        )
                    }
                )





            }}
    }
}

@Composable
fun ChatsList(model: MyViewModel/*messagesList: List<Message>*/, modifier: Modifier = Modifier) {
    val valueList: List<String>? by model.messages.observeAsState()

    LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        items(valueList?.size ?: 0) { index ->
            ShowChat(valueList?.get(index).toString() ?: "viesti tuli perille ilman dataa")
        }
    }
}