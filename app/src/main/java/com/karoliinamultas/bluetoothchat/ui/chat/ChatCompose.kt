package com.karoliinamultas.bluetoothchat.ui.chat


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.karoliinamultas.bluetoothchat.CameraButton
import com.karoliinamultas.bluetoothchat.bluetooth.ChatServer
import com.karoliinamultas.bluetoothchat.models.Message


private const val TAG = "ChatCompose"

object ChatCompose {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowChat(message: Message) {

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
            horizontalArrangement = if (message is Message.RemoteMessage) Arrangement.Start else Arrangement.End
        ) {
            Card(
                modifier = Modifier
                    .width(150.dp)
                    .padding(5.dp),

                colors = CardDefaults.cardColors(containerColor = randomBack),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Text(text = message.text, color = randomTeksti, modifier = Modifier.padding(10.dp))
            }
        }
    }

    @Composable
    fun Chats(deviceName: String?) {
        val message by ChatServer.messages.observeAsState()

        val inputvalue = remember { mutableStateOf(TextFieldValue()) }

        val messageList = remember {
            mutableStateListOf<Message>()
        }

        if (message != null && !messageList.contains(message)) {
            messageList.add(message!!)
        }



        if (messageList.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(modifier = Modifier
                    .fillMaxHeight(fraction = 0.89f)) {
                    ChatsList(messageList)
                }


                InputField(inputvalue)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(fraction = 0.89f)
                ) {
                    Text(text = "No Chat History")
                }

                InputField(inputvalue = inputvalue)
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InputField(inputvalue: MutableState<TextFieldValue>) {
        val focusManager = LocalFocusManager.current
        val context = LocalContext.current
        Box(
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))) {
            Row(
                Modifier
                    .padding(5.dp)
            ) {
                TextField(
                    value = inputvalue.value,
                    onValueChange = {
                        inputvalue.value = it
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
                        color = Color.White,
                        fontSize = TextUnit.Unspecified,
                        fontFamily = FontFamily.SansSerif
                    ),
                    maxLines = 1,
                    singleLine = true,
                )

                IconButton(
                    onClick = {
                        if (inputvalue.value.text.isNotEmpty()) {
                            ChatServer.sendMessage(inputvalue.value.text)
                            inputvalue.value = TextFieldValue()
                        }
                    },
                    modifier = Modifier
                        .height(60.dp)
                        .width(80.dp)
                        .padding(0.dp, 6.dp, 0.dp, 0.dp),
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
    fun ChatsList(messagesList: List<Message>) {
        LazyColumn(modifier = Modifier.background(Color.White)) {
            items(count = messagesList.size) { index ->
                if (messagesList.isNotEmpty())
                    ShowChat(message = messagesList[index])
            }
        }
    }

}