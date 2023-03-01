package com.karoliinamultas.bluetoothchat.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.karoliinamultas.bluetoothchat.R
import com.karoliinamultas.bluetoothchat.Screen
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import java.io.ByteArrayOutputStream


data class PathState(
    val path: Path,
    val color: Color,
    val stroke: Float
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingPad(context: Context, navController: NavController) {
    val drawColor = remember { mutableStateOf(Color.Black) }
    val drawBrush = remember { mutableStateOf(5f) }
    val path = remember { mutableStateOf(mutableListOf<PathState>()) }
    val showTools = remember {mutableStateOf(true)}
    val density = LocalDensity.current

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxSize(),
             verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End) {

                Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = { navController.navigate(Screen.ShowChats.route) }, modifier = Modifier.padding(8.dp)) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back button",
                        )
                    }
                    TextButton(onClick = {
                        showTools.value = !showTools.value
                    }, modifier = Modifier.padding(end = 8.dp)) {
                        if(showTools.value)
                            Text(text = "Hide tools")
                        else {
                            Text(text = "Show tools")
                        }
                    }
                }
                        AnimatedVisibility(visible = showTools.value,
                        enter = slideInHorizontally {
                            with(density) {-40.dp.roundToPx()}
                        } + expandHorizontally (
                            expandFrom = Alignment.Start
                                )
                        + fadeIn(
                            initialAlpha = 0.6f
                        ),
                            exit = slideOutHorizontally() + shrinkHorizontally() + fadeOut()
                        ) {
                            DrawingTools(drawColor, drawBrush, path, navController)
                        }
                    }
            }
    ) {
        Column{
            PaintBody(path, context, drawColor, drawBrush)
        }
    }
}


@Composable
fun DrawingTools(drawColor: MutableState<Color>, drawBrush: MutableState<Float>, path: MutableState<MutableList<PathState>>, navController: NavController) {
    val blackColor = Color.Black
    val blueColor = Color.Blue
    val greenColor = Color.Green
    val pinkColor = Color(255, 163, 165)
    val yellowColor = Color.Yellow
    val redColor = Color.Red
    val magentaColor = Color.Magenta
    val purpleColor = Color(103, 58, 183, 255)
    val brownColor = Color(100, 68, 51, 255)
    val eraser = Color.White

    val colorList = listOf(
        blackColor,
        blueColor,
        redColor,
        magentaColor,
        greenColor,
        pinkColor,
        yellowColor,
        purpleColor,
        brownColor
    )
    val strokes = remember { (1..30 step 5).toList() }
    var isClicked = remember { mutableStateOf(false)}
    val strokeMap: MutableMap<Int, Boolean> = remember {mutableStateMapOf()}

    fun addStrokesToMap() {
        strokes.forEach {
            strokeMap[it] = false
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row() {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(colorList) { color ->
                    IconButton(
                        onClick = {
                            drawColor.value = color
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .border(
                                border = BorderStroke(
                                    width = 100.dp,
                                    color = color
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .size(42.dp)
                    ) {}
                }
            }
        }

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(strokes) { stroke ->
                IconButton(
                    onClick = {
                        addStrokesToMap()
                        drawBrush.value = stroke.toFloat()
                        strokeMap[stroke] = true
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .border(
                            border = BorderStroke(
                                width = with(LocalDensity.current) { stroke.toDp() },
                                color = drawColor.value
                            ),
                            shape = if (strokeMap[stroke] == true) {
                                CircleShape
                            } else {
                                RoundedCornerShape(16.dp)
                            }
                        )
                        .size(42.dp)
                ) {}

            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = {
                drawColor.value = eraser
                Log.d("DBG", "$path")
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.highlighter_size_4_40px),
                    contentDescription = "Eraser",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(start = 8.dp)
                )
            }
            IconButton(onClick = {
                path.value = mutableListOf()
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}




@Composable
// Uses path of type Path state to listen to all location on the screen drawn
fun PaintBody(path: MutableState<MutableList<PathState>>, context: Context, drawColor: MutableState<Color>, drawBrush: MutableState<Float>) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        path.value.add(PathState(Path(), drawColor.value, drawBrush.value))
        DrawingCanvas(
            drawColor,
            drawBrush,
            path.value,
            context
        )
    }
}



fun bitmapToByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(format, quality, outputStream)
    return outputStream.toByteArray()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawingCanvas(
    drawColor: MutableState<Color>,
    drawBrush: MutableState<Float>,
    path: MutableList<PathState>,
    context: Context
) {
    val currentPath = path.last().path
    val movePath = remember { mutableStateOf<Offset?>(null) }
    val captureController = rememberCaptureController()
    var canvasBitmap: ImageBitmap? by remember { mutableStateOf(null) }

    Capturable(controller = captureController, onCaptured = { bitmap, error -> canvasBitmap = bitmap }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            currentPath.moveTo(it.x, it.y)
                        }
                        MotionEvent.ACTION_MOVE -> {
                            movePath.value = Offset(it.x, it.y)
                        }
                        else -> {
                            movePath.value = null
                        }
                    }
                    true
                }
        ) {
            movePath.value?.let {
                currentPath.lineTo(it.x, it.y)
                drawPath(
                    path = currentPath,
                    color = drawColor.value,
                    style = Stroke(drawBrush.value)
                )
            }
            path.forEach {
                drawPath(
                    path = it.path,
                    color = it.color,
                    style = Stroke(it.stroke)
                )

            }
        }
    }
Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.End){
    FloatingActionButton(onClick = { captureController.capture() }, modifier = Modifier.padding(24.dp)) {
        Text(text = "Save")
    }
}
    // When Ticket's Bitmap image is captured, show preview in dialog
    canvasBitmap?.let { bitmap ->
        Dialog(onDismissRequest = { }) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Preview of Canvas image \uD83D\uDC47")
                Spacer(Modifier.size(16.dp))
                Image(
                    bitmap = bitmap,
                    contentDescription = "Preview of canvas",
                )
                Spacer(Modifier.size(4.dp))
                Button(onClick = {
                    canvasBitmap = null
                    val byteArray = bitmapToByteArray(bitmap.asAndroidBitmap())
                    Log.d("DBG", "bytearray ${byteArray}")
                }
                ) {
                    Text("Close Preview")
                }
            }
        }
    }
}