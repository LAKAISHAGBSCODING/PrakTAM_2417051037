package com.example.praktam_2417051037

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import com.example.praktam_2417051037.ui.theme.PraktiktamTheme
import com.example.praktam_2417051037.data.model.Language
import com.example.praktam_2417051037.data.model.QuizData
import com.example.praktam_2417051037.data.repository.LanguageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PraktiktamTheme {
                val navController = rememberNavController()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color(0xFFEAF4FC), Color(0xFFF9F9FF))))
                ) {
                    AppNavigation(navController)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    var languageList by remember { mutableStateOf<List<Language>>(emptyList()) }
    var isLoadingData by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }

    val favoriteSet = remember { mutableStateListOf<String>() }
    val repository = remember { LanguageRepository() }

    LaunchedEffect(refreshTrigger) {
        isLoadingData = true
        isError = false
        try {
            languageList = repository.getLanguages()
            if (languageList.isEmpty()) {
                isError = true
            }
        } catch (e: Exception) {
            isError = true
        } finally {
            isLoadingData = false
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            if (isLoadingData) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6C63FF), strokeWidth = 6.dp)
                }
            } else if (isError) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Error",
                            tint = Color(0xFFFF6584),
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Oops! Koneksi Terputus \uD83D\uDD0C",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2D3748)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Gagal memuat data kelas. Pastikan internetmu menyala atau tarik layar ke bawah untuk mencoba lagi!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF718096),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Button(
                            onClick = { refreshTrigger++ },
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = Color.White)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("COBA LAGI", fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            } else {
                BahasaApp(
                    navController = navController,
                    languageList = languageList,
                    favoriteSet = favoriteSet,
                    onRefresh = { refreshTrigger++ }
                )
            }
        }

        composable("detail/{nama}") { backStackEntry ->
            val decodedNama = backStackEntry.arguments?.getString("nama") ?: ""
            val language = languageList.find { it.nama == decodedNama }

            if (language != null) {
                DetailScreen(language = language, navController = navController, favoriteSet = favoriteSet)
            }
        }

        composable("quiz/{nama}") { backStackEntry ->
            val decodedNama = backStackEntry.arguments?.getString("nama") ?: ""
            val language = languageList.find { it.nama == decodedNama }
            QuizScreen(nama = decodedNama, imageUrl = language?.imageUrl ?: "", navController = navController)
        }
    }
}

@Composable
fun BahasaApp(
    navController: NavController,
    languageList: List<Language>,
    favoriteSet: List<String>,
    onRefresh: () -> Unit
) {
    val listState = rememberLazyListState()
    var isPulling by remember { mutableStateOf(false) }
    var pullOffset by remember { mutableFloatStateOf(0f) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = languageList.filter {
        it.nama.contains(searchQuery, ignoreCase = true) ||
                it.kategori.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { isPulling = true },
                    onDragEnd = {
                        isPulling = false
                        if (pullOffset > 150f) {
                            onRefresh()
                        }
                        pullOffset = 0f
                    }
                ) { change, dragAmount ->
                    if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {
                        if (dragAmount > 0 || pullOffset > 0) {
                            pullOffset = (pullOffset + dragAmount).coerceIn(0f, 300f)
                        }
                    }
                }
            }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    placeholder = { Text("Mau belajar apa hari ini?", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Cari", tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Hapus", tint = Color.Gray)
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedTextColor = Color(0xFF2D3748),
                        unfocusedTextColor = Color(0xFF2D3748)
                    ),
                    singleLine = true
                )
            }

            if (searchQuery.isEmpty()) {
                item {
                    Text(
                        "Kelas Favorit Kamu! ⭐",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        items(languageList) { language ->
                            LanguageRowItem(
                                language = language,
                                navController = navController,
                                isFavorite = language.nama in favoriteSet
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        "Semua Kelas Coding \uD83D\uDE80",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                items(languageList) { language ->
                    LanguageListItem(
                        language = language,
                        navController = navController,
                        isFavorite = language.nama in favoriteSet
                    )
                }
            } else {
                item {
                    Text(
                        "Hasil Pencarian \uD83D\uDD0E",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                if (filteredList.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Tidak ditemukan",
                                tint = Color.LightGray,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Kelas tidak ditemukan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    items(filteredList) { language ->
                        LanguageListItem(
                            language = language,
                            navController = navController,
                            isFavorite = language.nama in favoriteSet
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = pullOffset > 0f,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape)
                    .padding(8.dp)
                    .offset(y = (pullOffset / 4).dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowDownward,
                    contentDescription = "Tarik untuk Refresh",
                    tint = Color(0xFF6C63FF),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun LanguageRowItem(language: Language, navController: NavController, isFavorite: Boolean) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable {
                val safeRoute = Uri.encode(language.nama)
                navController.navigate("detail/$safeRoute")
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp, pressedElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFF1F5F9))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                SmartImage(
                    imageUrl = language.imageUrl,
                    contentDescription = language.nama,
                    modifier = Modifier.fillMaxSize()
                )

                if (isFavorite) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Favorit",
                        tint = Color(0xFFFF6584),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-8).dp)
                            .background(Color.White, CircleShape)
                            .padding(4.dp)
                            .size(16.dp)
                    )
                }
            }

            Column(Modifier.padding(20.dp)) {
                Text(
                    language.nama,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2D3748)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    language.kategori,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF6C63FF),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LanguageListItem(language: Language, navController: NavController, isFavorite: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable {
                val safeRoute = Uri.encode(language.nama)
                navController.navigate("detail/$safeRoute")
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFF1F5F9), CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                SmartImage(
                    imageUrl = language.imageUrl,
                    contentDescription = language.nama,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    language.nama,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2D3748)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE0E7FF)
                ) {
                    Text(
                        text = language.kategori,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF4338CA),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            if (isFavorite) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorit",
                    tint = Color(0xFFFF6584),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun DetailScreen(language: Language, navController: NavController, favoriteSet: MutableList<String>) {
    val isFavorite = language.nama in favoriteSet

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFFDBEAFE), Color(0xFFEFF6FF))))
        ) {
            SmartImage(
                imageUrl = language.imageUrl,
                contentDescription = language.nama,
                modifier = Modifier.fillMaxSize().padding(60.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.background(Color.White, CircleShape).size(48.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF2D3748))
                }

                IconButton(
                    onClick = {
                        if (isFavorite) favoriteSet.remove(language.nama)
                        else favoriteSet.add(language.nama)
                    },
                    modifier = Modifier.background(Color.White, CircleShape).size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFF6584) else Color.LightGray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 280.dp),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp)
            ) {
                Text(
                    text = language.nama,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2D3748)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE0E7FF)
                ) {
                    Text(
                        text = "Kategori : ${language.kategori}",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4338CA)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = language.deskripsi,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 19.sp,
                        lineHeight = 28.sp,
                        color = Color(0xFF4A5568),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val safeRoute = Uri.encode(language.nama)
                        navController.navigate("quiz/$safeRoute")
                    },
                    modifier = Modifier.fillMaxWidth().height(72.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 2.dp)
                ) {
                    Text("MASUK KELAS SEKARANG!", fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play", modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}

@Composable
fun QuizScreen(nama: String, imageUrl: String, navController: NavController) {
    var quizList by remember { mutableStateOf<List<QuizData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val repository = remember { LanguageRepository() }

    LaunchedEffect(nama) {
        try {
            quizList = repository.getQuizzes()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF6C63FF), strokeWidth = 6.dp)
        }
        return
    }

    val currentQuiz = quizList.find { it.bahasa == nama }

    if (currentQuiz == null || currentQuiz.kuis.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                Icon(Icons.Filled.Warning, contentDescription = "Kosong", modifier = Modifier.size(80.dp), tint = Color(0xFFFFB020))
                Spacer(modifier = Modifier.height(24.dp))
                Text("Yah, Kuis untuk $nama belum tersedia \uD83E\uDD7A", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3748))
                ) {
                    Text("Kembali", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
        return
    }

    var screenState by remember { mutableStateOf("MATERI") }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        AnimatedContent(
            targetState = screenState,
            transitionSpec = {
                scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn() togetherWith scaleOut(animationSpec = tween(300)) + fadeOut()
            },
            label = "ScreenState"
        ) { state ->
            when (state) {
                "MATERI" -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp).padding(top = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.background(Color.White, CircleShape).size(48.dp)
                            ) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF2D3748))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Materi $nama",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2D3748)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 20.dp, vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(Color(0xFFF1F5F9), CircleShape)
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SmartImage(
                                        imageUrl = imageUrl,
                                        contentDescription = nama,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Pengenalan Dasar \uD83D\uDCD6",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF6C63FF)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = currentQuiz.materi,
                                    fontSize = 17.sp,
                                    lineHeight = 28.sp,
                                    color = Color(0xFF4A5568),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { screenState = "QUIZ" },
                            modifier = Modifier.fillMaxWidth().height(72.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Text("SAYA SIAP UJIAN! \uD83C\uDFAE", fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
                "QUIZ" -> {
                    val progress by animateFloatAsState(
                        targetValue = (currentQuestionIndex + 1).toFloat() / currentQuiz.kuis.size,
                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                    )

                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp).padding(top = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.background(Color.White, CircleShape).size(48.dp)
                            ) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF2D3748))
                            }
                            Spacer(modifier = Modifier.width(20.dp))

                            SmartImage(
                                imageUrl = imageUrl,
                                contentDescription = nama,
                                modifier = Modifier.size(48.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Kuis $nama",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2D3748)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(16.dp).clip(CircleShape),
                            color = Color(0xFF38B2AC),
                            trackColor = Color(0xFFE2E8F0),
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        AnimatedContent(
                            targetState = currentQuestionIndex,
                            transitionSpec = {
                                slideInHorizontally(animationSpec = tween(400)) { width -> width } + fadeIn() togetherWith slideOutHorizontally(animationSpec = tween(400)) { width -> -width } + fadeOut()
                            },
                            label = "quiz_animation"
                        ) { targetIndex ->
                            val question = currentQuiz.kuis[targetIndex]

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(bottom = 32.dp)
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(32.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    Text(
                                        text = question.soal,
                                        modifier = Modifier.padding(32.dp),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        textAlign = TextAlign.Center,
                                        color = Color(0xFF2D3748),
                                        lineHeight = 36.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(40.dp))

                                question.pilihan.forEachIndexed { index, pilihanText ->
                                    Button(
                                        onClick = {
                                            if (index == question.jawabanBenar) {
                                                score++
                                            }
                                            if (currentQuestionIndex < currentQuiz.kuis.size - 1) {
                                                currentQuestionIndex++
                                            } else {
                                                screenState = "RESULT"
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).heightIn(min = 72.dp),
                                        shape = RoundedCornerShape(32.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = Color(0xFF4338CA)
                                        ),
                                        border = BorderStroke(3.dp, Color(0xFFE0E7FF)),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 0.dp),
                                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                                    ) {
                                        Text(pilihanText, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                }
                "RESULT" -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .background(Color.White, CircleShape)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            SmartImage(
                                imageUrl = imageUrl,
                                contentDescription = nama,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Text("MISI SELESAI! \uD83C\uDF89", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color(0xFF2D3748))
                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            shape = RoundedCornerShape(40.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(40.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Skor Akhir Kamu \uD83C\uDFC6", fontSize = 20.sp, color = Color(0xFF6C63FF), fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${score * 20}", fontSize = 96.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFB020))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Benar $score dari ${currentQuiz.kuis.size} Soal", fontSize = 18.sp, color = Color(0xFF718096), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(56.dp))

                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.fillMaxWidth().height(72.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38B2AC)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Text("KEMBALI KE MENU", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartImage(imageUrl: String?, contentDescription: String?, modifier: Modifier = Modifier) {
    val safeUrl = imageUrl ?: ""
    val context = LocalContext.current

    if (safeUrl.startsWith("data:image")) {
        val bitmap = remember(safeUrl) {
            try {
                val rawBase64 = safeUrl.substringAfter(",")
                val cleanBase64 = rawBase64.replace("\\s+".toRegex(), "")
                val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = modifier.background(Color.LightGray))
        }
    } else {
        val imageLoader = remember {
            ImageLoader.Builder(context)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()
        }

        AsyncImage(
            model = safeUrl,
            contentDescription = contentDescription,
            imageLoader = imageLoader,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}