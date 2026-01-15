package com.example.poketgc_api

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.poketgc_api.Data.PokemonCard
import com.example.poketgc_api.Data.TcgDexApi
import com.example.poketgc_api.ui.theme.Pantalla.PokemonPantalla
import com.example.poketgc_api.ui.theme.PokeTGC_APITheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeTGC_APITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PokemonApp()
                }
            }
        }
    }
}

@Composable
fun PokemonApp() {
    var cards by remember { mutableStateOf<List<PokemonCard>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://api.tcgdex.net/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api = remember { retrofit.create(TcgDexApi::class.java) }

    LaunchedEffect(Unit) {
        try {
            cards = api.getAllCard().take(20) // Tomamos las primeras 20 para probar
            isLoading = false
        } catch (e: Exception) {
            errorMessage = e.message
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (errorMessage != null) {
            Text(
                text = "Error: $errorMessage",
                modifier = Modifier.align(Alignment.Center).padding(16.dp),
                color = MaterialTheme.colorScheme.error
            )
        } else {
            LazyColumn {
                items(cards) { card ->
                    PokemonPantalla(pokemonCard = card)
                }
            }
        }
    }
}
