package com.example.praktam_2417051037

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                BahasaApp()
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
    Language(
        "Kotlin",
        "Bahasa modern untuk Android.",
        "Mobile",
        R.drawable.gambarcoding1
    ),
    Language(
        "Java",
        "Bahasa OOP populer.",
        "Programming",
        R.drawable.gambarcoding2
    ),
    Language(
        "Python",
        "Bahasa simpel untuk AI dan data.",
        "General",
        R.drawable.gambarcoding3
    ),
    Language(
        "JavaScript",
        "Untuk web interaktif.",
        "Web",
        R.drawable.gambarcoding1
    ),
    Language(
        "C++",
        "Untuk sistem dan game.",
        "System",
        R.drawable.gambarcoding2
    )
)

@Composable
fun BahasaApp() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Rekomendasi Populer",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dummyLanguage) { language ->
                    LanguageRowItem(language)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Daftar Menu Lengkap",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(dummyLanguage) { language ->
            DetailScreen(language)
        }
    }
}

@Composable
fun LanguageRowItem(language: Language) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp)
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
                    fontWeight = FontWeight.Bold
                )
                Text(language.kategori)
            }
        }
    }
}

@Composable
fun DetailScreen(language: Language) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
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
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(language.deskripsi)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Kategori: ${language.kategori}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pelajari Sekarang")
            }
        }
    }
}