package com.example.pokemonapp.view.screens.pokemonDetail

import android.provider.Settings.Global.getString
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.pokemonapp.R
import com.example.pokemonapp.data.remote.responses.Pokemon
import com.example.pokemonapp.util.Resource
import com.example.pokemonapp.view.screens.pokemonDetail.helper.getHeightInM
import com.example.pokemonapp.view.screens.pokemonDetail.helper.getWeightInKg
import kotlin.math.round

@Composable
fun PokemonDetailScreen(
    pokemonName: String,
    navController: NavController,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {

    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonInfo(pokemonName)
    }.value

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFD2FFFC))
    ) {
        when(pokemonInfo){
            is Resource.Success -> {
                TopSection(
                    pokemonInfo.data!!,
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 15.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.TopCenter), navController = navController
                )

                BottomSection(
                    pokemonInfo.data,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .align(Alignment.BottomCenter)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 55.dp, topEnd = 55.dp)
                        )
                )
            }
            is Resource.Error -> {
                Text(
                    text = pokemonInfo.message!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is Resource.Loading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

}

@Composable
fun TopSection(pokemon:  Pokemon, modifier: Modifier, navController: NavController?) {
    Column(modifier = modifier) {
        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    navController?.popBackStack()
                })

        SubcomposeAsyncImage(
            model = pokemon.sprites.front_default,
            contentDescription = pokemon.name,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
        )
    }
}

@Composable
fun BottomSection(pokemon: Pokemon, modifier: Modifier) {
    Column(modifier = modifier) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_rectangle),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        )

        Text(
            text = pokemon.name,
            fontFamily = FontFamily(Font(R.font.font_hubballi)),
            color = Color(0xFF535353),
            fontSize = 48.sp,
            fontWeight = FontWeight(400),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 11.dp)
                .fillMaxWidth()
        )

        PokemonParameterRow(pokemon)
        GenderRow()
        Divider(
            modifier = Modifier.padding(start = 21.dp, end = 21.dp, top = 20.dp),
            color = Color(0x1A000000)
        )
        TypeSection(pokemon)
        WeaknessesSection()
    }
}

@Composable
fun PokemonParameterRow(pokemon: Pokemon){
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(top = 45.dp, start = 20.dp, end = 20.dp)
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (item in 0 until 4) {
            when(item){
                0 -> {ParameterItem(context.getString(R.string.height), getHeightInM(pokemon))}
                1 -> {ParameterItem(context.getString(R.string.weight), getWeightInKg(pokemon))}
                2 -> {ParameterItem(context.getString(R.string.xp), pokemon.base_experience.toString())}
                3 -> {ParameterItem(context.getString(R.string.abilities), pokemon.abilities[0].ability.name)}
            }
            if (item != 3) {
                Image(
                    painter = painterResource(id = R.drawable.ic_divider_line),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .requiredHeight(40.dp)
                        .background(Color(0x1A000000))
                )
            }
        }
    }
}

@Composable
fun ParameterItem(parameterName: String, parameterValue: String){
    Column() {
        Text(
            text = parameterName,
            fontFamily = FontFamily(Font(R.font.font_hubballi)),
            color = Color(0xFF64A4AD),
            fontSize = 20.sp
        )
        Text(
            text = parameterValue,
            fontFamily = FontFamily(Font(R.font.font_hubballi)),
            color = Color(0xFF535353),
            fontSize = 20.sp
        )
    }
}

@Composable
fun GenderRow(){
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(top = 13.dp, start = 20.dp)
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = context.getString(R.string.gender),
            fontFamily = FontFamily(Font(R.font.font_hubballi)),
            color = Color(0xFF64A4AD),
            fontSize = 20.sp,
        )
        Image(
            painter = painterResource(id = R.drawable.ic_gender_male),
            contentDescription = null,
            Modifier
                .padding(start = 6.dp, bottom = 2.dp)
                .size(14.dp)
        )
    }
}

@Composable
fun TypeSection(pokemon: Pokemon){
    val context = LocalContext.current
    Text(
        text = context.getString(R.string.type),
        fontFamily = FontFamily(Font(R.font.font_hubballi)),
        color = Color(0xFFACAD64),
        fontSize = 30.sp,
        modifier = Modifier.padding(start = 20.dp, top = 13.dp)
    )

    Row(
        modifier = Modifier
            .padding(top = 10.dp, start = 20.dp)
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (type in pokemon.types){
            Text(
                text = type.type.name,
                fontFamily = FontFamily(Font(R.font.font_hubballi)),
                color = Color(0xFF535353),
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 20.dp)
            )
        }
    }
}


// ესეთი ინფო არ მოიძებნა აპი-ში მაგრამ დიზაინი, რომ ლამაზად გამჩენილიყო ჰარდად დავტოვებ. იმედია არ მიწყენთ ;დ (სქესზეც ანალოგიურად)
@Composable
fun WeaknessesSection(){
    val context = LocalContext.current
    Text(
        text = context.getString(R.string.weaknesses),
        fontFamily = FontFamily(Font(R.font.font_hubballi)),
        color = Color(0xFFAD6471),
        fontSize = 30.sp,
        modifier = Modifier.padding(start = 20.dp, top = 13.dp)
    )

    Row(
        modifier = Modifier
            .padding(top = 10.dp, start = 21.dp)
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Fire",
            fontFamily = FontFamily(Font(R.font.font_hubballi)),
            color = Color(0xFF535353),
            fontSize = 20.sp,
        )

        Text(
            text = "Psychic",
            fontFamily = FontFamily(Font(R.font.font_hubballi)),
            color = Color(0xFF535353),
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 20.dp)
        )

        Text(
            text = "Flying",
            fontFamily = FontFamily(Font(R.font.font_hubballi)),
            color = Color(0xFF535353),
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}



