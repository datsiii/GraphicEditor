package com.example.drawappcompose.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ContentAlpha
import androidx.wear.compose.material.LocalContentAlpha
import com.example.drawappcompose.R
import com.example.drawappcompose.models.Draws
import com.example.drawappcompose.models.HomeUiState
import com.example.drawappcompose.repository.Resources
import com.example.drawappcompose.ui.theme.DrawAppComposeTheme
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.drawappcompose.Utils.base64ToImageBitmap
import com.example.drawappcompose.ui.theme.LightPurple
import com.example.drawappcompose.ui.theme.MGray
import java.io.ByteArrayOutputStream


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    homeViewModel: HomeViewModel?,
    onDrawClick: (id: String) -> Unit,
    navToDetailScreen: () -> Unit,
    navToLoginScreen: () -> Unit
) {
    val homeUiState = homeViewModel?.homeUiState ?: HomeUiState()

    var openDialog by remember {
        mutableStateOf(false)
    }
    var selectedDraw: Draws? by remember {
        mutableStateOf(null)
    }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    LaunchedEffect(key1 = Unit) {
        homeViewModel?.loadDraws()
    }
    val fontFamily = FontFamily(
        Font(R.font.museomodernoblack, FontWeight.ExtraBold)
    )

    Scaffold(
        //scaffoldsState
        floatingActionButton = {
            FloatingActionButton(onClick = { navToDetailScreen.invoke() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        topBar = {
            TopAppBar(
                navigationIcon = {},
                actions = {
                    IconButton(onClick = {
                        homeViewModel?.signOut()
                        navToLoginScreen.invoke()
                    }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)

                    }
                },
                title = {
                    Text(
                        text = "Your Pics",
                        fontFamily = fontFamily,
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) { padding ->
        Image(painterResource(id = R.drawable.screen), contentDescription = "", modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .padding(padding)
                .background(Color.White)
        ) {
            when (homeUiState.drawList) {
                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }

                is Resources.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(
                            homeUiState.drawList.data ?: emptyList()
                        ) { draw ->
                            DrawItem(draws = draw,
                                onLongClick = {
                                    openDialog = true
                                    selectedDraw = draw
                                }
                            ) {
                                onDrawClick.invoke(draw.documentId)

                            }

                        }

                    }
                    AnimatedVisibility(visible = openDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                openDialog = false
                            },
                            title = { Text(text = "Delete Note?",
                                fontFamily = fontFamily) },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        selectedDraw?.documentId?.let {
                                            homeViewModel?.deleteDraw(it)
                                        }
                                        openDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        //contentColor = Color.Red
                                    ),
                                ) {
                                    Text(text = "Delete",
                                        fontFamily = fontFamily)
                                }
                            },
                            dismissButton = {
                                Button(onClick = { openDialog = false }) {
                                    Text(text = "Cancel",
                                        fontFamily = fontFamily)
                                }
                            }
                        )


                    }
                }

                else -> {
                    Text(
                        text = homeUiState
                            .drawList.throwable?.localizedMessage ?: "Unknown Error",
                        color = Color.Red
                    )
                }
            }
            LaunchedEffect(key1 = homeViewModel?.hasUser) {
                if(homeViewModel?.hasUser == false){
                    navToLoginScreen.invoke()
                }
            }

        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawItem(
    draws: Draws,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val imageBitmap = draws.drawImage?.let { base64ToImageBitmap(it) }
    val fontFamily = FontFamily(
        Font(R.font.museomodernoblack, FontWeight.ExtraBold)
    )
    Card(
        modifier = Modifier
            .combinedClickable(
                onLongClick = { onLongClick.invoke() },
                onClick = { onClick.invoke() }
            )
            .padding(8.dp)
            .fillMaxWidth(),
        //background?
    ) {
        Column {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                if (imageBitmap != null) {
                    Spacer(modifier = Modifier.size(10.dp))
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "draw image",
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.empty_image),
                        contentDescription = "draw image",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                Text(
                    text = formatDate(draws.timestamp),
                    //style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = fontFamily,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.End)
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            /*Text(
                text = draws.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(4.dp)
            )*/

        }

    }

}

private fun formatDate(timestamp: com.google.firebase.Timestamp): String {
    val sdf = SimpleDateFormat("dd-MM-yy hh:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}

@Preview
@Composable
fun PrevHomeScreen() {
    DrawAppComposeTheme {
        Home(homeViewModel = null, onDrawClick = {}, navToDetailScreen = { /*TODO*/ }) {

        }
    }
}
