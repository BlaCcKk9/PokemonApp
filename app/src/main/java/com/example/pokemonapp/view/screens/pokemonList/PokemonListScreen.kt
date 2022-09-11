package com.example.pokemonapp.view.screens.pokemonList

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.pokemonapp.R
import com.example.pokemonapp.data.model.PokedexListEntry
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun PokemonListScreen(
    navController: NavController
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
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {

    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val isLoading by remember { viewModel.isLoading }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD2FFFC))
            .padding(top = 15.dp)
    ) {
        items(pokemonList.size) {

            if (it >= pokemonList.size - 1 && !endReached && !isLoading) {
                LaunchedEffect(key1 = true) {
                    viewModel.loadPokemonPaginated()
                }
            }
            PokemonListItem(navController, pokemonList[it])
        }
    }
    PokemonListLoadingProgressBar(viewModel)
}
@Composable
fun PokemonListItem(
    navController: NavController,
    entry: PokedexListEntry
) {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) ZoomPokemonImage(
        imageUrl = entry.imageUrl,
        setShowDialog = { showDialog.value = it })

    Box(
        Modifier
            .padding(start = 15.dp, bottom = 25.dp, end = 15.dp)
            .fillMaxWidth()
            .requiredHeight(120.dp)
            .background(Color(0xFFD2FFFC))
    ) {
        PokemonListItemContent(entry) { showDialog.value = true }
        ShowDetailButton(
            Modifier
                .size(width = 111.dp, height = 43.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(topStart = 15.dp, bottomEnd = 15.dp),
                )
                .background(color = Color(0xFFD4FFC5))
                .align(BottomEnd)
        ) { navController.navigate("pokemon_detail_screen/${entry.pokemonName}") }
    }
}

@Composable
fun PokemonListItemContent(entry: PokedexListEntry, onImageClicked: () -> Unit) {
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
                .clickable { onImageClicked.invoke() }
        )
        Text(
            text = entry.pokemonName,
            fontFamily = FontFamily(Font(R.font.font_hubballi)),
            color = Color.Black,
            fontSize = 32.sp,
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}

@Composable
fun ShowDetailButton(modifier: Modifier, onClick: () -> Unit) {
    val context = LocalContext.current
    Box(modifier = modifier.clickable { onClick.invoke() }) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .align(Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = context.getString(R.string.show_details),
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

@Composable
fun PokemonListLoadingProgressBar(
    viewModel: PokemonListViewModel,
) {
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }

    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        if (loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.loadPokemonPaginated()
            }
        }
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    val context = LocalContext.current
    Column {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = context.getString(R.string.retry))
        }
    }
}

@Composable
fun ZoomPokemonImage(imageUrl: String, setShowDialog: (Boolean) -> Unit) {
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            modifier = Modifier.requiredHeight(360.dp), color = Color.White, shape = RoundedCornerShape(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(30.dp)
                    .background(Color.White)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp))
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}