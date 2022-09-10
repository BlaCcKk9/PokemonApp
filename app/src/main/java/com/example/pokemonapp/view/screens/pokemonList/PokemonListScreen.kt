package com.example.pokemonapp.view.screens.pokemonList

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.pokemonapp.R
import com.example.pokemonapp.data.model.PokedexListEntry
import com.example.pokemonapp.ui.theme.PokemonAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun PokemonListScreen(
    navController: NavController?
) {
    Surface(
        Modifier
            .fillMaxSize()
            .background(Color(0x33D2FFFC))
    ) {
        val systemUiController = rememberSystemUiController()
        systemUiController.isStatusBarVisible = false
        PokemonList(navController)
    }
}

@Composable
fun PokemonList(
    navController: NavController?,
    viewModel: PokemonListViewModel = hiltViewModel()
) {

    val pokemonList = remember { viewModel.pokemonList }
    val endReached = remember { viewModel.endReached }
    val loadError = remember { viewModel.loadError }
    val isLoading = remember { viewModel.isLoading }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD2FFFC))
            .padding(top = 15.dp)
    ) {
        items(pokemonList.value.size) {

            if (it >= pokemonList.value.size - 1 && !endReached.value && !isLoading.value) {
                LaunchedEffect(key1 = true) {
                    viewModel.loadPokemonPaginated()
                }
            }
            PokemonListItem(navController, pokemonList.value[it])
        }
    }

    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading.value) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        if (loadError.value.isNotEmpty()) {
            RetrySection(error = loadError.value) {
                viewModel.loadPokemonPaginated()
            }
        }
    }
}


@Composable
fun PokemonListItem(
    navController: NavController?,
    entry: PokedexListEntry
) {
    val showDialog = remember {
        mutableStateOf(false)
    }

    if (showDialog.value)
        CustomDialog(imageUrl = entry.imageUrl, setShowDialog = { showDialog.value = it })

    Box(
        Modifier
            .padding(start = 15.dp, bottom = 25.dp, end = 15.dp)
            .fillMaxWidth()
            .requiredHeight(120.dp)
            .background(Color(0xFFD2FFFC))
    ) {
        Row(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .fillMaxWidth()
                .requiredHeight(115.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color.White),
        ) {
            SubcomposeAsyncImage(
                model = entry.imageUrl,
                loading = {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.scale(0.5f)
                    )
                },
                contentDescription = entry.pokemonName,
                modifier = Modifier
                    .size(135.dp)
                    .clickable { showDialog.value = true }
            )
            Text(
                text = entry.pokemonName,
                fontFamily = FontFamily(Font(R.font.font_hubballi)),
                color = Color.Black,
                fontSize = 32.sp,
                modifier = Modifier.padding(top = 20.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(width = 111.dp, height = 43.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(topStart = 15.dp, bottomEnd = 15.dp),
                )
                .background(color = Color(0xFFD4FFC5))
                .align(BottomEnd)
                .clickable { }
        ) {
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Show Details",
                    fontFamily = FontFamily(Font(R.font.font_hubballi)),
                    fontSize = 11.sp,
                    color = Color.Black
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_vector),
                    contentDescription = "vector",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(width = 4.dp, height = 8.dp)
                )
            }
        }
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun CustomDialog(imageUrl: String, setShowDialog: (Boolean) -> Unit) {

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .requiredHeight(360.dp), shape = RoundedCornerShape(15.dp)
        ) {
            Box(modifier = Modifier.background(Color.White).fillMaxSize().clip(RoundedCornerShape(15.dp))) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PokemonAppTheme {
        PokemonListScreen(navController = null)
    }
}