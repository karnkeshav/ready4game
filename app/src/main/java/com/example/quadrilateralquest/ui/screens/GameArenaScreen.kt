package com.example.quadrilateralquest.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quadrilateralquest.data.ContentRepository
import com.example.quadrilateralquest.logic.ArenaEngine
import com.example.quadrilateralquest.logic.BossState
import com.example.quadrilateralquest.logic.GameZone
import com.example.quadrilateralquest.model.ArcadeLevel
import com.example.quadrilateralquest.model.BossStage
import com.example.quadrilateralquest.model.ChapterGameScroll
import com.example.quadrilateralquest.model.SanctumQuest

@Composable
fun GameArenaScreen(
    fileRef: String, // Passed from Navigation
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
                    quests = gameScroll!!.sanctumZone.quests,
                    engine = engine
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
                    modifier = Modifier.fillMaxWidth().clickable { onLevelComplete() },
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
                    
                    // Simulated Attack Options
                    currentStage.options?.forEach { option ->
                        Button(
                            onClick = { 
                                if (option == currentStage.correctAnswer) {
                                    val state = engine.attackBoss(currentStage.damage)
                                    if (state == BossState.DEFEATED) {
                                        onBossDefeated()
                                    } else {
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
fun SanctumView(
    quests: List<SanctumQuest>,
    engine: ArenaEngine
) {
    Column {
        Text("The Scribe's Sanctum", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif)
        Text("Rigor & Precision required.", fontStyle = FontStyle.Italic)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            items(quests) { quest ->
                SanctumQuestCard(quest, engine)
            }
        }
    }
}

@Composable
fun SanctumQuestCard(
    quest: SanctumQuest,
    engine: ArenaEngine
) {
    // Local state for this specific card
    var input by remember { mutableStateOf("") }
    var selectedOption by remember { mutableIntStateOf(-1) }
    var statusMessage by remember { mutableStateOf("") }
    var isSolved by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(if (quest.type == "cryptex_input") "CRYPTEX" else "LOGIC", 
                     style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text("+${quest.xpReward} XP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Question Content
            if (quest.type == "assertion_reason") {
                Text("Assertion (A):", fontWeight = FontWeight.Bold)
                Text(quest.assertion ?: "")
                Spacer(modifier = Modifier.height(4.dp))
                Text("Reason (R):", fontWeight = FontWeight.Bold)
                Text(quest.reason ?: "")
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Options List
                quest.options?.forEachIndexed { index, optionText ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (selectedOption == index),
                                onClick = { if (!isSolved) selectedOption = index },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = (selectedOption == index), onClick = null)
                        Text(text = optionText, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
                    }
                }

            } else {
                // Cryptex / Text Question
                Text(quest.questionText ?: "", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = { if (!isSolved) input = it },
                    label = { Text("Enter Value") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSolved
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button & Feedback
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        val answer = if (quest.type == "assertion_reason") selectedOption.toString() else input
                        
                        // Use Engine Logic
                        val xpEarned = engine.submitSanctumAnswer(quest, answer)

                        if (xpEarned > 0) {
                            isSolved = true
                            statusMessage = "CORRECT! " + (quest.explanation ?: "")
                        } else {
                            statusMessage = "Incorrect. Try again."
                        }
                    },
                    enabled = !isSolved
                ) {
                    Text(if (isSolved) "Solved" else "Inscribe")
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                if (statusMessage.isNotEmpty()) {
                    Text(
                        text = statusMessage, 
                        color = if (isSolved) Color(0xFF006400) else Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
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
                Text("Back")
            }
        }
    )
}
