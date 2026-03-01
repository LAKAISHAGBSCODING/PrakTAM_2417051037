package com.example.praktam_2417051037

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.praktam_2417051037.model.LanguageSource
import com.example.praktam_2417051037.ui.theme.PrakTAM_2417051037Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2417051037Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HalamanTopBahasaBerwarna(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HalamanTopBahasaBerwarna(modifier: Modifier = Modifier) {
    val data = LanguageSource.dummyLanguage

    // Background gradient lembut
    val bg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFEEF6FF),
            Color(0xFFFFF3E6),
            Color(0xFFF2FFF0)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ===== HEADER =====
            item {
                HeaderTopBahasa()
            }

            // ===== LIST =====
            itemsIndexed(data) { index, item ->
                val accent = when (index) {
                    0 -> Color(0xFF3B82F6) // Biru
                    1 -> Color(0xFFF59E0B) // Oranye
                    else -> Color(0xFF22C55E) // Hijau
                }

                LanguageCardBerwarna(
                    nomor = index + 1,
                    nama = item.nama,
                    deskripsi = item.deskripsi,
                    imageRes = item.imageRes,
                    accentColor = accent
                )
            }
        }
    }
}

@Composable
fun HeaderTopBahasa() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Top 3 Bahasa yang Sering Dipakai",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Belajar coding dari yang paling populer 🚀",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Badge kecil
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFF111827))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Beginner Friendly ✨",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun LanguageCardBerwarna(
    nomor: Int,
    nama: String,
    deskripsi: String,
    imageRes: Int,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Gambar
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = nama,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                contentScale = ContentScale.Crop
            )

            // Isi card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Baris judul + badge nomor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "#$nomor",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = nama,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF374151)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTopBahasaBerwarna() {
    PrakTAM_2417051037Theme {
        HalamanTopBahasaBerwarna()
    }
}