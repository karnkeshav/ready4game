package com.example.quadrilateralquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quadrilateralquest.data.ContentRepository
import com.example.quadrilateralquest.logic.ArenaEngine
import com.example.quadrilateralquest.logic.BossState
import com.example.quadrilateralquest.logic.GameZone
import com.example.quadrilateralquest.model.ArcadeLevel
import com.example.quadrilateralquest.model.BossStage
import com.example.quadrilateralquest.model.ChapterGameScroll

@Composable
fun GameArenaScreen(
    fileRef: String, // Passed from Navigation (e.g., "content/math/class9/quadrilaterals.json")
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ContentRepository(context) }
    val engine = remember { ArenaEngine() }
    
    // State to hold the loaded game data
    var gameScroll by remember { mutableStateOf<ChapterGameScroll?>(null) }
    var currentZone by remember { mutableStateOf(GameZone.ARCADE) }
    
    // Load data on start
    LaunchedEffect(fileRef) {
        val scroll = repository.getChapterGame(fileRef)
        if (scroll != null) {
            gameScroll = scroll
            engine.loadChapter(scroll)
        }
    }

    if (gameScroll == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            ArenaTopBar(
                title = gameScroll!!.meta.title,
                zone = currentZone,
                onBack = onBack
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            
            // Render the specific View based on the Engine's State
            when (currentZone) {
                GameZone.ARCADE -> ArcadeView(
                    levels = gameScroll!!.arcadeZone.levels,
                    onLevelComplete = { 
                        // Simulate completing arcade levels
                        engine.sanctumKeys++ 
                    },
                    onEnterBoss = { 
                        // Switch to Boss Mode
                        currentZone = GameZone.BOSS 
                    }
                )
                
                GameZone.BOSS -> BossView(
                    bossName = gameScroll!!.bossZone.bossName,
                    stages = gameScroll!!.bossZone.stages,
                    engine = engine,
                    onBossDefeated = {
                        if (engine.unlockSanctum()) {
                            currentZone = GameZone.SANCTUM
                        }
                    }
                )
                
                GameZone.SANCTUM -> SanctumView(
                    quests = gameScroll!!.sanctumZone.quests
                )
            }
        }
    }
}

// ==========================================
// SUB-VIEW: ARCADE (The Map)
// ==========================================
@Composable
fun ArcadeView(
    levels: List<ArcadeLevel>,
    onLevelComplete: () -> Unit,
    onEnterBoss: () -> Unit
) {
    Column {
        Text("Zone 1: The Shapelands", style = MaterialTheme.typography.headlineSmall)
        Text("Complete levels to find Keys.", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(levels) { level ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onLevelComplete() }, // Simulating play
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Level ${level.levelId}: ${level.title}", fontWeight = FontWeight.Bold)
                        Text(level.type.uppercase(), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            
            item {
                Button(
                    onClick = onEnterBoss,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("CHALLENGE BOSS")
                }
            }
        }
    }
}

// ==========================================
// SUB-VIEW: BOSS (The Fight)
// ==========================================
@Composable
fun BossView(
    bossName: String,
    stages: List<BossStage>,
    engine: ArenaEngine,
    onBossDefeated: () -> Unit
) {
    var currentStageIndex by remember { mutableStateOf(0) }
    val currentStage = stages.getOrNull(currentStageIndex)
    
    // Boss Health Bar Logic
    val healthPercent = engine.getBossHealthPercentage()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("BOSS: $bossName", style = MaterialTheme.typography.headlineMedium, color = Color.Red)
        
        // Health Bar
        LinearProgressIndicator(
            progress = healthPercent,
            modifier = Modifier.fillMaxWidth().height(20.dp).padding(vertical = 8.dp),
            color = Color.Red,
            trackColor = Color.Gray
        )
        Text("${engine.currentBossHealth} / ${engine.maxBossHealth} HP")

        Spacer(modifier = Modifier.height(32.dp))

        if (currentStage != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Stage ${currentStage.stageId}", style = MaterialTheme.typography.labelLarge)
                    Text(currentStage.narrative, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // The Actual Question
                    Text(currentStage.questionText ?: "Solve the puzzle!", fontWeight = FontWeight.Bold)
                    
                    // Simulated Attack Options (In real app, these are interactive)
                    currentStage.options?.forEach { option ->
                        Button(
                            onClick = { 
                                if (option == currentStage.correctAnswer) {
                                    val state = engine.attackBoss(currentStage.damage)
                                    if (state == BossState.DEFEATED) {
                                        onBossDefeated()
                                    } else {
                                        // Next stage
                                        if (currentStageIndex < stages.size - 1) currentStageIndex++
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        ) {
                            Text(option)
                        }
                    }
                }
            }
        } else {
            Text("Boss Defeated! Keys Acquired: ${engine.sanctumKeys}")
            Button(onClick = onBossDefeated) { Text("Enter Sanctum") }
        }
    }
}

// ==========================================
// SUB-VIEW: SANCTUM (The Hard Mode)
// ==========================================
@Composable
fun SanctumView(quests: List<com.example.quadrilateralquest.model.SanctumQuest>) {
    Column {
        Text("The Scribe's Sanctum", style = MaterialTheme.typography.headlineMedium, fontFamily = androidx.compose.ui.text.font.FontFamily.Serif)
        Text("Pen and Paper required beyond this point.", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(quests) { quest ->
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Quest: ${quest.type}", style = MaterialTheme.typography.labelMedium)
                        Text(quest.questionText ?: "${quest.assertion} / ${quest.reason}", fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Enter Value / Logic") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                        Button(onClick = {}, modifier = Modifier.align(Alignment.End)) {
                            Text("Submit Rune")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaTopBar(title: String, zone: GameZone, onBack: () -> Unit) {
    TopAppBar(
        title = { 
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text("Zone: ${zone.name}", style = MaterialTheme.typography.labelSmall)
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Text("Back") // Replace with Icon in production
            }
        }
    )
}
