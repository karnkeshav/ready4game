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
