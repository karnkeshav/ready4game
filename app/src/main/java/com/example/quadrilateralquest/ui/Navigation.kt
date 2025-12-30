package com.example.quadrilateralquest.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quadrilateralquest.data.ContentRepository
import com.example.quadrilateralquest.logic.GameManager
import com.example.quadrilateralquest.ui.screens.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation(repository: ContentRepository, gameManager: GameManager) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "index") {
        composable("index") {
            IndexScreen(navController, repository)
        }
        composable("subject_selection") {
            SubjectSelectionScreen(navController, repository)
        }
        composable("chapter_selection") {
            ChapterSelectionScreen(navController, repository)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("dashboard") {
            DashboardScreen(navController, gameManager)
        }
        composable("theorem_mastery/{theoremId}") { backStackEntry ->
            val theoremId = backStackEntry.arguments?.getString("theoremId")
            TheoremMasteryScreen(navController, theoremId, repository, gameManager)
        }
        composable("activity") {
            InteractiveActivityScreen(navController, gameManager)
        }
        composable("quiz") {
            QuizScreen(navController, repository, gameManager)
        }
        composable("leaderboard") {
            LeaderboardScreen(navController, gameManager)
        }
        
        // Fixed: Moved inside NavHost
        composable("game_arena/{fileRef}") { backStackEntry ->
            val fileRef = backStackEntry.arguments?.getString("fileRef") ?: ""
            val decodedRef = try {
                URLDecoder.decode(fileRef, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                fileRef
            }
            
            GameArenaScreen(
                fileRef = decodedRef,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
