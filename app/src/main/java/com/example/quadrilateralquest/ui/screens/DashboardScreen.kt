package com.example.quadrilateralquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quadrilateralquest.logic.GameManager

@Composable
fun DashboardScreen(navController: NavController, gameManager: GameManager) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mission Control", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Progress Summary
        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Clarity Quotient: ${gameManager.userProgress.clarityQuotient}")
                Text("Badges: ${gameManager.userProgress.badges.joinToString()}")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("theorem_mastery/theorem_1") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Theorem 1: Parallelograms")
        }

        Button(
            onClick = { navController.navigate("theorem_mastery/theorem_2") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Theorem 2: Rhombus")
        }

        Button(
            onClick = { navController.navigate("activity") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Interactive Lab")
        }

        Button(
            onClick = { navController.navigate("quiz") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Mastery Quiz")
        }

        Button(
            onClick = { navController.navigate("leaderboard") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Leaderboard")
        }
    }
}
