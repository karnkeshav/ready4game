package com.example.quadrilateralquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.quadrilateralquest.data.ContentRepository
import com.example.quadrilateralquest.logic.ArenaEngine
import com.example.quadrilateralquest.logic.GameStage
import com.example.quadrilateralquest.model.GeometryLevel
import com.example.quadrilateralquest.model.MechanicType

@Composable
fun GameArenaScreen(
    fileRef: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ContentRepository(context) }
    // In a real app, engine should be a ViewModel. Here we remember it across recompositions.
    val engine = remember { ArenaEngine() }
    
    // State to trigger UI updates when engine state changes
    var refreshTrigger by remember { mutableStateOf(0) }

    // Load Data
    LaunchedEffect(fileRef) {
        val scroll = repository.getChapterScroll(fileRef)
        if (scroll != null) {
            engine.loadChapter(scroll)
            refreshTrigger++ // Force update
        }
    }

    if (engine.activeScroll == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
            Text("Loading Scroll...", modifier = Modifier.padding(top = 48.dp))
        }
        return
    }

    Scaffold(
        topBar = {
            // Simple Header showing Stage Title
            SmallTopAppBar(
                title = { 
                    Column {
                        Text(text = engine.activeScroll?.meta?.title ?: "Unknown")
                        Text(
                            text = "Stage: ${engine.currentStage.name.replace("_", " ")}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            // THE STAGE SWITCHER
            when (engine.currentStage) {
                GameStage.SHAPER_LAB -> {
                    val level = engine.getCurrentLevel()
                    if (level != null) {
                        ShaperLabView(level) { 
                            engine.completeLevel()
                            refreshTrigger++
                        }
                    } else {
                        StageCompleteView("Lab Complete! Proceeding...", engine) { refreshTrigger++ }
                    }
                }
                
                GameStage.FRACTAL_FORGE -> {
                    // Reuse Lab View for now or generic Placeholder
                    Text("Fractal Forge Under Construction", modifier = Modifier.align(Alignment.Center))
                    Button(
                        onClick = { engine.completeLevel(); refreshTrigger++ },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                    ) { Text("Auto-Build (Debug)") }
                }

                GameStage.PROOF_ARENA -> {
                    Text("Proof Arena Boss Fight", modifier = Modifier.align(Alignment.Center))
                    Button(
                        onClick = { engine.completeLevel(); refreshTrigger++ },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                    ) { Text("Defeat Boss (Debug)") }
                }

                else -> {
                    Text("Stage Content Loading...", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun StageCompleteView(message: String, engine: ArenaEngine, onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { 
            // Engine logic already handled stage transition in completeStage(), 
            // strictly calling update here to refresh UI
            onNext() 
        }) {
            Text("Enter Next Stage")
        }
    }
}
