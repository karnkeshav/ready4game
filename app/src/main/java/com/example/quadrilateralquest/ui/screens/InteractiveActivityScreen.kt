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
import androidx.navigation.NavController
import com.example.quadrilateralquest.logic.GameManager

@Composable
fun InteractiveActivityScreen(navController: NavController, gameManager: GameManager) {
    // A simplified parallelogram activity.
    // To allow the user to "break" the parallel property, we'll allow independent dragging of the bottom-right vertex (p3).
    // The challenge is to move p3 such that the segment p2-p3 is parallel to p1-p4 (and equal length).

    var startX by remember { mutableFloatStateOf(100f) }
    var startY by remember { mutableFloatStateOf(100f) }
    var fixedWidth by remember { mutableFloatStateOf(300f) }
    var height by remember { mutableFloatStateOf(200f) }

    // p1 (Top-Left) and p2 (Top-Right) are fixed relative to each other.
    // p4 (Bottom-Left) is fixed for this exercise.
    // p3 (Bottom-Right) is draggable.

    var p4Skew by remember { mutableFloatStateOf(100f) }

    // Initial correct position for p3
    var p3X by remember { mutableFloatStateOf(startX + fixedWidth + p4Skew) }
    var p3Y by remember { mutableFloatStateOf(startY + height) }

    var isParallel by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Interactive Lab", style = MaterialTheme.typography.headlineMedium)
        Text("Drag the bottom-right vertex. Ensure opposite sides remain parallel!", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        p3X += dragAmount.x
                        p3Y += dragAmount.y

                        // Check logic:
                        // For a parallelogram, vector p2->p3 should equal vector p1->p4.
                        // p1=(startX, startY), p4=(startX+p4Skew, startY+height) -> vector = (p4Skew, height)
                        // p2=(startX+fixedWidth, startY), p3=(p3X, p3Y) -> vector = (p3X - (startX+fixedWidth), p3Y - startY)

                        val targetVectorX = p4Skew
                        val targetVectorY = height
                        val currentVectorX = p3X - (startX + fixedWidth)
                        val currentVectorY = p3Y - startY

                        // Allow some tolerance
                        val tolerance = 20f
                        isParallel = Math.abs(currentVectorX - targetVectorX) < tolerance &&
                                     Math.abs(currentVectorY - targetVectorY) < tolerance
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path()

                // Points
                val p1 = Offset(startX, startY)
                val p2 = Offset(startX + fixedWidth, startY)
                val p4 = Offset(startX + p4Skew, startY + height)
                val p3 = Offset(p3X, p3Y) // Draggable

                path.moveTo(p1.x, p1.y)
                path.lineTo(p2.x, p2.y)
                path.lineTo(p3.x, p3.y)
                path.lineTo(p4.x, p4.y)
                path.close()

                drawPath(
                    path = path,
                    color = if (isParallel) Color.Green else Color.Red,
                    style = Stroke(width = 5f)
                )
            }
        }

        if (!isParallel) {
            Text("WARNING: Opposite sides are not parallel!", color = Color.Red)
        }
    }
}
