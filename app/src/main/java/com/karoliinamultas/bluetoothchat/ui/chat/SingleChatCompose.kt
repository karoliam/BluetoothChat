package com.karoliinamultas.bluetoothchat.ui.chat


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.karoliinamultas.bluetoothchat.CameraButton
import com.karoliinamultas.bluetoothchat.Screen
import com.karoliinamultas.bluetoothchat.ui.StartScreen


private const val TAG = "ChatCompose"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatWindow(navController: NavController){
    //Statusbar
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(MaterialTheme.colorScheme.background)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = {
                    Text(
                        "Restroom Chat",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                Chats()
                ChatsList()
            }
        }
    )
}


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowChat(modifier: Modifier = Modifier) {

        val tekstit = listOf(
            Color(0xFF00FDDC),
            Color(0xFFFF729F),
            Color(0xFF04E762),
            Color(0xFFFDE74C),
            Color(0xFFFF4365))
        val randomTeksti = tekstit.random()

        val backgroundit = listOf(
            Color(0xFF111D4A),
            Color(0xFF43AA8B),
            Color(0xFF8B635C),
            Color(0xFF60594D),
            Color(0xFF93A29B))
        val randomBack = backgroundit.random()

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
                Text(text = "Chat box", color = randomTeksti, modifier = Modifier.padding(10.dp))
            }
        }
    }

    @Composable
    fun Chats(/*deviceName: String?*/ modifier: Modifier = Modifier) {

        val inputvalue = remember { mutableStateOf(TextFieldValue()) }


            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(fraction = 0.89f)
                ) {
                    Text(text = "No Chat History")
                }

                InputField()
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InputField(/*inputvalue: MutableState<TextFieldValue>*/ modifier: Modifier = Modifier) {
        val focusManager = LocalFocusManager.current
        val context = LocalContext.current
        Box(
            Modifier
                .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 1f))) {
            Row(
                Modifier
                    .padding(5.dp)
            ) {
                TextField(
                    value = "inputvalue.value",
                    onValueChange = {
                        "inputvalue.value = it"
                    },
                    Modifier
                        .width(265.dp)
                        .padding(5.dp),
                    shape = RoundedCornerShape(5.dp),
                    placeholder = { Text(text = "Enter your message") },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                    ),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = TextUnit.Unspecified,
                        fontFamily = FontFamily.SansSerif
                    ),
                    maxLines = 1,
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.background)
                )

                IconButton(
                    onClick = { /*Sending message comes here*/ },
                    modifier = Modifier
                        .height(60.dp)
                        .width(60.dp)
                        .padding(0.dp, 6.dp, 0.dp, 0.dp),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.background),
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Localized description"
                        )
                    }
                )

                // camera button here

                CameraButton(context)

            }
        }
    }

    @Composable
    fun ChatsList(/*messagesList: List<Message>*/ modifier: Modifier = Modifier) {
        LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            items(/*count = messagesList.size*/1) { index ->
                    ShowChat()
            }
        }
    }
