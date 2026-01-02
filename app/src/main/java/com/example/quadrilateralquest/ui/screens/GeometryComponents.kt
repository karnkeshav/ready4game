package com.example.quadrilateralquest.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.quadrilateralquest.model.GeometryLevel
import com.example.quadrilateralquest.model.Vertex
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun ShaperLabView(
    level: GeometryLevel,
    onSuccess: () -> Unit
) {
    // 1. Extract Initial State
    // In a real app, this state logic would be more robust to handle resets.
    val initialPoints = level.data.initialShape ?: emptyList()
    
    // Convert generic Vertex model to MutableState for Compose
    // We use a map to track positions by ID ("A", "B", "C")
    val pointStates = remember(level) {
        mutableStateMapOf<String, Offset>().apply {
            initialPoints.forEach { v -> 
                // Scaling for screen (multiplying by 3 for visibility prototype)
                put(v.id, Offset(v.x * 3f + 100f, v.y * 3f + 300f)) 
            }
        }
    }

    var feedbackMessage by remember { mutableStateOf(level.instruction) }
    var isSolved by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // HUD
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = level.title, style = MaterialTheme.typography.titleLarge)
                Text(text = feedbackMessage)
            }
        }

        // CANVAS ARENA
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // Find closest point to drag
                        val touchPoint = change.position
                        val closestId = pointStates.entries.minByOrNull { 
                            (it.value - touchPoint).getDistance() 
                        }?.key

                        // Update position if found and unlocked (mocking lock check)
                        if (closestId != null) {
                            val currentPos = pointStates[closestId]!!
                            pointStates[closestId] = currentPos + dragAmount
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path()
                
                // Draw Edges (Simple Loop for Quadrilateral)
                val sortedPoints = pointStates.entries.sortedBy { it.key } // A, B, C...
                if (sortedPoints.isNotEmpty()) {
                    val first = sortedPoints.first().value
                    path.moveTo(first.x, first.y)
                    for (i in 1 until sortedPoints.size) {
                        path.lineTo(sortedPoints[i].value.x, sortedPoints[i].value.y)
                    }
                    path.close()
                }

                drawPath(
                    path = path,
                    color = if (isSolved) Color.Green else Color.Blue,
                    style = Stroke(width = 8f)
                )

                // Draw Vertices
                pointStates.forEach { (id, offset) ->
                    drawCircle(Color.Red, radius = 20f, center = offset)
                }
            }
        }

        // CONTROL PANEL
        Button(
            onClick = {
                // MOCK VALIDATION LOGIC
                // In production, this would call engine.validate(points)
                if (level.data.winCondition == "congruence_overlap") {
                    // Simulate Success
                    isSolved = true
                    feedbackMessage = "Congruence Established! Structure Stabilized."
                }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = !isSolved
        ) {
            Text("Stabilize")
        }
        
        if (isSolved) {
            Button(
                onClick = onSuccess,
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
            ) {
                Text("Next Level")
            }
        }
    }
}

// Helper Extension
fun Offset.getDistance(): Float = sqrt(x.pow(2) + y.pow(2))
// ... (Keep ShaperLabView and imports) ...

@Composable
fun FractalForgeView(
    level: GeometryLevel,
    onSuccess: () -> Unit
) {
    // 1. Setup Base Structure (Triangle/Quad)
    // For prototype, if no shape provided, default to a "Mountain" Triangle
    val vertices = remember(level) {
        level.data.initialShape ?: listOf(
            Vertex("A", 50f, 100f, true),
            Vertex("B", 0f, 300f, true),
            Vertex("C", 100f, 300f, true)
        )
    }

    // Scale for screen
    val points = remember(vertices) {
        vertices.associate { v -> 
            v.id to Offset(v.x * 3f + 100f, v.y * 3f + 100f) 
        }
    }

    // 2. State for Construction
    // "Found" midpoints (e.g., "mid_AB" -> Offset)
    val revealedMidpoints = remember { mutableStateMapOf<String, Offset>() }
    // "Built" beams (List of pairs of point IDs)
    val builtBeams = remember { mutableStateListOf<Pair<String, String>>() }
    
    var selectedPointId by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("Select edges to find midpoints.") }
    var isSolved by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // HUD
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("FRACTAL FORGE", style = MaterialTheme.typography.labelSmall)
                Text(level.title, style = MaterialTheme.typography.titleLarge)
                Text(feedback)
            }
        }

        // CONSTRUCTION CANVAS
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        // LOGIC A: Tap Edge to Spawn Midpoint
                        // Check distance to imaginary lines AB, BC, CA...
                        val sortedIds = points.keys.sorted()
                        for (i in sortedIds.indices) {
                            val startId = sortedIds[i]
                            val endId = sortedIds[(i + 1) % sortedIds.size]
                            val p1 = points[startId]!!
                            val p2 = points[endId]!!
                            
                            if (distanceToSegment(p1, p2, tapOffset) < 30f) {
                                // Found edge! Spawn midpoint
                                val midId = "mid_${startId}${endId}"
                                if (!revealedMidpoints.containsKey(midId)) {
                                    revealedMidpoints[midId] = (p1 + p2) / 2f
                                    feedback = "Midpoint Identified. Tap it to connect."
                                }
                                return@detectTapGestures
                            }
                        }

                        // LOGIC B: Tap Midpoint to Build Beam
                        val clickedMid = revealedMidpoints.entries.find { 
                            (it.value - tapOffset).getDistance() < 40f 
                        }
                        
                        if (clickedMid != null) {
                            if (selectedPointId == null) {
                                selectedPointId = clickedMid.key
                                feedback = "Constructing... Select target."
                            } else {
                                // Connect!
                                builtBeams.add(selectedPointId!! to clickedMid.key)
                                selectedPointId = null
                                feedback = "Beam Deployed."
                                
                                // Check Win Condition (Simple Check)
                                // If we have at least 1 beam, we assume success for prototype
                                if (builtBeams.isNotEmpty()) {
                                    isSolved = true
                                    feedback = "Structure Stable! Theorem 8.8 Verified."
                                }
                            }
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 1. Draw Base Structure (Phantom Lines)
                val sortedIds = points.keys.sorted()
                val pPath = Path()
                val p1 = points[sortedIds[0]]!!
                pPath.moveTo(p1.x, p1.y)
                points.values.forEach { pPath.lineTo(it.x, it.y) }
                pPath.close()
                drawPath(pPath, Color.Gray, style = Stroke(width = 5f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f))))

                // 2. Draw Constructed Beams
                builtBeams.forEach { (id1, id2) ->
                    val start = revealedMidpoints[id1]!!
                    val end = revealedMidpoints[id2]!!
                    drawLine(Color.Cyan, start, end, strokeWidth = 10f)
                }

                // 3. Draw Vertices (Base)
                points.forEach { (_, pos) -> drawCircle(Color.Gray, 15f, pos) }

                // 4. Draw Midpoints (Interactive)
                revealedMidpoints.forEach { (id, pos) ->
                    val isSelected = (id == selectedPointId)
                    drawCircle(if (isSelected) Color.Yellow else Color.Cyan, 20f, pos)
                }
            }
        }

        if (isSolved) {
            Button(
                onClick = onSuccess,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
            ) {
                Text("Forge Complete")
            }
        }
    }
}

// Math Helper: Distance from point P to segment AB
fun distanceToSegment(p1: Offset, p2: Offset, p: Offset): Float {
    val l2 = (p1 - p2).getDistance().pow(2)
    if (l2 == 0f) return (p - p1).getDistance()
    var t = ((p.x - p1.x) * (p2.x - p1.x) + (p.y - p1.y) * (p2.y - p1.y)) / l2
    t = t.coerceIn(0f, 1f)
    val proj = p1 + (p2 - p1) * t
    return (p - proj).getDistance()
}
@Composable
fun ProofArenaView(
    level: GeometryLevel,
    onSuccess: () -> Unit
) {
    // 1. Boss State
    var bossHealth by remember { mutableFloatStateOf(1f) } // 100%
    var dialogue by remember { mutableStateOf(level.data.narrative ?: "I am the Logic Construct.") }
    var combatLog by remember { mutableStateOf("") }
    
    // 2. Available Tactics (Theorems)
    // In a real app, these would come from the Player's inventory (unlocked theorems)
    val tactics = listOf(
        "co_interior_angles" to "Co-Interior Angles Sum 180°",
        "alternate_angles" to "Alternate Interior Angles Equal",
        "diagonal_bisect" to "Diagonals Bisect Each Other",
        "midpoint_theorem" to "Mid-Point Theorem"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // BOSS HUD
        Text("THE PRISM CONSTRUCT", color = Color.Red, style = MaterialTheme.typography.headlineMedium)
        LinearProgressIndicator(
            progress = bossHealth, 
            modifier = Modifier.fillMaxWidth().height(20.dp).padding(vertical = 8.dp),
            color = Color.Red
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // VISUAL (The Boss Avatar)
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(Color.Red.copy(alpha = 0.2f))
                drawRect(Color.Black, style = Stroke(width = 5f))
            }
            Text(" [ ? ] ", style = MaterialTheme.typography.headlineLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // DIALOGUE BOX
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(dialogue, style = MaterialTheme.typography.bodyLarge)
                if (combatLog.isNotEmpty()) {
                    Text(combatLog, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // TACTICAL OPTIONS
        Text("SELECT COUNTER-ARGUMENT:", style = MaterialTheme.typography.labelLarge)
        tactics.forEach { (id, label) ->
            Button(
                onClick = {
                    if (id == level.data.enemy_weakness) {
                        // CRITICAL HIT
                        bossHealth = 0f
                        dialogue = "Logic Error... System Critical... You are correct."
                        combatLog = "Used $label. It's Super Effective!"
                    } else {
                        // MISS
                        combatLog = "Construct: Irrelevant theorem. Try again."
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                enabled = bossHealth > 0
            ) {
                Text(label)
            }
        }

        if (bossHealth <= 0f) {
            Button(
                onClick = onSuccess,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400)),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("CLAIM VICTORY")
            }
        }
    }
}
// ... (Keep existing views) ...

@Composable
fun RunnerView(
    zoneData: com.example.quadrilateralquest.model.RunnerZone,
    onObstaclePass: (Boolean) -> Unit,
    onComplete: () -> Unit
) {
    // 1. Runner State
    // Lanes: 0 (Left), 1 (Center), 2 (Right)
    var playerLane by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var gameTime by remember { mutableIntStateOf(zoneData.duration) }
    
    // 2. Obstacle State
    // We only track one active obstacle for this prototype to keep it smooth
    var currentObstacleIndex by remember { mutableIntStateOf(0) }
    var obstacleY by remember { mutableFloatStateOf(0f) } // 0f to 1f (screen progress)
    var isCrashed by remember { mutableStateOf(false) }

    val currentObstacle = zoneData.obstacles.getOrNull(currentObstacleIndex)

    // 3. Game Loop (The Heartbeat)
    LaunchedEffect(currentObstacleIndex, isCrashed) {
        if (currentObstacle == null) {
            onComplete()
            return@LaunchedEffect
        }
        if (isCrashed) return@LaunchedEffect

        // Animation Loop
        val startTime = System.nanoTime()
        while (obstacleY < 1.2f) { // Run until off-screen
            val now = System.nanoTime()
            val dt = (now - startTime) / 1_000_000_000f // Delta time in seconds
            
            // Speed increases slightly with every obstacle
            val speed = 0.3f + (currentObstacleIndex * 0.05f) 
            obstacleY += speed * 0.016f // Assume 60fps roughly
            
            // Collision Detection (at y=0.8)
            if (obstacleY > 0.8f && obstacleY < 0.85f) {
                // Determine which lane is correct
                // Logic: We randomize the correct lane for the "Gate" visualization
                // For this prototype, let's say:
                // Lane 0: Wrong, Lane 1: Correct, Lane 2: Wrong (Simplified mapping)
                // In a real app, we'd map options to lanes dynamically.
                
                // Let's simluate: Correct Gate is always CENTER (Lane 1) for this specific JSON
                // because mapping "Rhombus" string to a lane requires UI layout state.
                // We'll enforce: Player must be in Lane 1 to hit "Rhombus".
                
                if (playerLane == 1) {
                     // Hit Correct!
                } else {
                    isCrashed = true // Hit Wrong!
                }
            }

            kotlinx.coroutines.delay(16) // ~60 FPS cap
        }
        
        // Reset for next
        if (!isCrashed) {
            onObstaclePass(true)
            score += 100
            obstacleY = 0f
            currentObstacleIndex++
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // HUD
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SCORE: $score", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text("TIME: ${gameTime}s", style = MaterialTheme.typography.bodyMedium)
        }

        // RUNNER TRACK
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Simple Tap controls: Left side -> Lane 0, Center -> 1, Right -> 2
                        val width = size.width
                        if (offset.x < width / 3) playerLane = 0
                        else if (offset.x > width * 2 / 3) playerLane = 2
                        else playerLane = 1
                    }
                }
        ) {
            // Draw Track
            Canvas(modifier = Modifier.fillMaxSize()) {
                val laneWidth = size.width / 3
                
                // Lane Dividers
                drawLine(Color.Gray, Offset(laneWidth, 0f), Offset(laneWidth, size.height), 5f)
                drawLine(Color.Gray, Offset(laneWidth * 2, 0f), Offset(laneWidth * 2, size.height), 5f)

                // Draw Player (Triangle)
                val playerX = (playerLane * laneWidth) + (laneWidth / 2)
                val playerY = size.height * 0.85f
                drawCircle(Color.Cyan, radius = 40f, center = Offset(playerX, playerY))

                // Draw Obstacle (Gates)
                if (currentObstacle != null) {
                    val obsYPos = obstacleY * size.height
                    
                    // Gate 0 (Left)
                    drawRect(
                        color = Color.Red.copy(alpha = 0.8f),
                        topLeft = Offset(10f, obsYPos),
                        size = androidx.compose.ui.geometry.Size(laneWidth - 20f, 100f)
                    )
                    // Gate 1 (Center - Correct for prototype)
                    drawRect(
                        color = Color.Green.copy(alpha = 0.8f),
                        topLeft = Offset(laneWidth + 10f, obsYPos),
                        size = androidx.compose.ui.geometry.Size(laneWidth - 20f, 100f)
                    )
                    // Gate 2 (Right)
                    drawRect(
                        color = Color.Red.copy(alpha = 0.8f),
                        topLeft = Offset(laneWidth * 2 + 10f, obsYPos),
                        size = androidx.compose.ui.geometry.Size(laneWidth - 20f, 100f)
                    )
                }
            }
            
            // Text Overlays for Gates (using standard Composables for text rendering ease)
            if (currentObstacle != null) {
                // We use a Box with offset to simulate moving text
                // Note: Direct text in Canvas is harder in Compose, so we overlay
                Box(Modifier.fillMaxSize()) {
                     // Prompt
                     Text(
                         currentObstacle.prompt, 
                         modifier = Modifier.align(Alignment.TopCenter).padding(top = 100.dp),
                         style = MaterialTheme.typography.headlineSmall,
                         color = Color.Black
                     )
                     
                     // Gate Labels (Moving down)
                     // Since obstacleY is state, this recomposes. 
                     // Optimization: In real game, use Layout modifiers.
                }
            }
        }
        
        if (isCrashed) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("CRASHED!") },
                text = { Text("You hit the wrong property gate.\nFinal Score: $score") },
                confirmButton = {
                    Button(onClick = { 
                        // Reset simple
                        isCrashed = false 
                        obstacleY = 0f
                        // Penalty or Retry logic
                    }) { Text("Revive (-50 Pts)") }
                },
                dismissButton = {
                     Button(onClick = onComplete) { Text("Exit Run") }
                }
            )
        }
    }
}
