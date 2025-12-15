package com.example.quadrilateralquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quadrilateralquest.data.ContentRepository
import com.example.quadrilateralquest.logic.GameManager

@Composable
fun TheoremMasteryScreen(
    navController: NavController,
    theoremId: String?,
    repository: ContentRepository,
    gameManager: GameManager
) {
    val theorems = repository.getTheorems()?.theorems
    val theorem = theorems?.find { it.id == theoremId }

    if (theorem == null) {
        Text("Theorem not found")
        return
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val currentStep = theorem.steps.getOrNull(currentStepIndex)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(theorem.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        // Step Indicator
        Row {
            theorem.steps.forEachIndexed { index, _ ->
                val color = if (index == currentStepIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                Box(modifier = Modifier.padding(4.dp)) {
                   Text((index + 1).toString(), color = color)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentStep != null) {
            Card(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(currentStep.stepType.uppercase(), style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(currentStep.content)
                    // Placeholder for Media (Lottie/Image)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Media Placeholder: ${currentStep.mediaUrl}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { if (currentStepIndex > 0) currentStepIndex-- },
                enabled = currentStepIndex > 0
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    if (currentStepIndex < theorem.steps.size - 1) {
                        currentStepIndex++
                    } else {
                        // Completed
                        gameManager.userProgress.completedTheorems.add(theorem.id)
                        gameManager.checkAndAwardBadges()
                        navController.popBackStack()
                    }
                }
            ) {
                Text(if (currentStepIndex < theorem.steps.size - 1) "Next" else "Finish")
            }
        }
    }
}
