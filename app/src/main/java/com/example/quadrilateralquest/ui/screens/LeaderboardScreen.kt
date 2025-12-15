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
fun LeaderboardScreen(navController: NavController, gameManager: GameManager) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Leaderboard", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Your Clarity Quotient: ${gameManager.userProgress.clarityQuotient}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Badges Earned:")
                gameManager.userProgress.badges.forEach { badge ->
                    Text("- $badge")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Mock global leaderboard
        Text("Top Learners", style = MaterialTheme.typography.titleMedium)
        Text("1. Alice - 1200 CQ")
        Text("2. Bob - 1150 CQ")
        Text("3. You - ${gameManager.userProgress.clarityQuotient} CQ")
    }
}
