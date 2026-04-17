package com.example.praktam_2417051037

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.praktam_2417051037.ui.theme.PraktiktamTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PraktiktamTheme {
                val navController = rememberNavController()

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .windowInsetsPadding(WindowInsets.statusBars)
                    )

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation(navController)
                    }
                }
            }
        }
    }
}

data class Language(
    val nama: String,
    val deskripsi: String,
    val kategori: String,
    val imageRes: Int
)

val dummyLanguage = listOf(
    Language("Kotlin", "Bikin aplikasi HP Android pertamamu yang super keren!", "Aplikasi HP", R.drawable.gambarcoding1),
    Language("Java", "Belajar bikin game dan program komputer seru!", "Program & Game", R.drawable.gambarcoding2),
    Language("Python", "Coding gampang banget kaya main puzzle susun balok!", "Robot Pintar", R.drawable.gambarcoding3),
    Language("JavaScript", "Bikin website kamu jadi hidup, warna-warni, dan bisa gerak!", "Website Seru", R.drawable.gambarcoding1),
    Language("C++", "Bahasa kilat untuk bikin game 3D yang jagoan!", "Game 3D", R.drawable.gambarcoding2)
)

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            BahasaApp(navController)
        }

        composable("detail/{nama}") { backStackEntry ->
            val nama = backStackEntry.arguments?.getString("nama")
            val language = dummyLanguage.find { it.nama == nama }

            if (language != null) {
                DetailScreen(language = language, navController = navController)
            }
        }
    }
}

@Composable
fun BahasaApp(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Pilihan Petualangan Favorit!",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dummyLanguage) { language ->
                    LanguageRowItem(language = language, navController = navController)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Semua Kelas Coding",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(dummyLanguage) { language ->
            LanguageListItem(language = language, navController = navController)
        }
    }
}

@Composable
fun LanguageRowItem(language: Language, navController: NavController) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable {
                navController.navigate("detail/${language.nama}")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Image(
                painter = painterResource(language.imageRes),
                contentDescription = language.nama,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(8.dp)) {
                Text(
                    language.nama,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    language.kategori,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun LanguageListItem(language: Language, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("detail/${language.nama}")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(language.imageRes),
                contentDescription = language.nama,
                modifier = Modifier
                    .size(64.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    language.nama,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    language.kategori,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun DetailScreen(language: Language, navController: NavController) {
    var isFavorite by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(Modifier.padding(16.dp)) {

                Box {
                    Image(
                        painter = painterResource(language.imageRes),
                        contentDescription = language.nama,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite) Color.Red else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    language.nama,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    language.deskripsi,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Kategori Misi: ${language.kategori}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            delay(2000)
                            snackbarHostState.showSnackbar(
                                "Hore! Misi ${language.nama} sudah siap dimainkan!"
                            )
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Siap-siap...")
                    } else {
                        Text("Mulai Belajar & Main!")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kembali ke Pilihan")
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}