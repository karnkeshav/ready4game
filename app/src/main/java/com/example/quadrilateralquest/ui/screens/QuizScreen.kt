package com.example.quadrilateralquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quadrilateralquest.data.ContentRepository
import com.example.quadrilateralquest.logic.GameManager

@Composable
fun QuizScreen(navController: NavController, repository: ContentRepository, gameManager: GameManager) {
    val questions = repository.getQuestions()?.questions ?: emptyList()
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var showHint by remember { mutableStateOf(false) }
    var answerSelected by remember { mutableStateOf<Int?>(null) }

    // Timer would be here
    val startTime = remember { System.currentTimeMillis() }

    if (currentQuestionIndex >= questions.size) {
        // Quiz Finished
        LaunchedEffect(Unit) {
            gameManager.updateClarityQuotient(score * 10)
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Quiz Complete!", style = MaterialTheme.typography.headlineLarge)
            Text("Score: $score / ${questions.size}")
            Button(onClick = { navController.navigate("dashboard") }) {
                Text("Return to Dashboard")
            }
        }
        return
    }

    val question = questions[currentQuestionIndex]

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Question ${currentQuestionIndex + 1}/${questions.size}", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(question.text, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        question.options.forEachIndexed { index, option ->
            Button(
                onClick = {
                    if (answerSelected == null) {
                        answerSelected = index
                        val timeTaken = (System.currentTimeMillis() - startTime) / 1000
                        if (index == question.correctIndex) {
                            score++
                            // Check Remediation logic would go here if we were strictly following the complex flow immediately
                        } else {
                            gameManager.updateStruggleMetric(question.id, timeTaken, if (showHint) 1 else 0, 1)
                            // If needed, redirect to theorem
                            if (gameManager.needsRemediation(question.id, 0)) {
                                navController.navigate("theorem_mastery/${question.relevantTheoremId}")
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (answerSelected == index) {
                        if (index == question.correctIndex) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (answerSelected != null) {
            Button(onClick = {
                currentQuestionIndex++
                answerSelected = null
                showHint = false
            }) {
                Text("Next Question")
            }
        } else {
            OutlinedButton(onClick = { showHint = true }) {
                Text("Need a Hint?")
            }
            if (showHint) {
                Text("Hint: ${question.hint}", style = MaterialTheme.typography.bodySmall, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        }
    }
}
