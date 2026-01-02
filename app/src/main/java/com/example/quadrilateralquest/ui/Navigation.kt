package com.example.quadrilateralquest.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quadrilateralquest.ui.screens.*

@Composable
fun QuadrilateralQuestApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "index") {
        
        // 1. The Landing Page (Class Selection)
        composable("index") {
            IndexScreen(
                onClassSelected = { classId -> 
                    navController.navigate("subject_selection/$classId") 
                }
            )
        }

        // 2. Subject Selection (Math, Science, etc.)
        composable(
            route = "subject_selection/{classId}",
            arguments = listOf(navArgument("classId") { type = NavType.StringType })
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId") ?: "class_9"
            SubjectSelectionScreen(
                classId = classId,
                onSubjectSelected = { subjectId ->
                    navController.navigate("chapter_selection/$classId/$subjectId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 3. Chapter Selection (The Menu)
        composable(
            route = "chapter_selection/{classId}/{subjectId}",
            arguments = listOf(
                navArgument("classId") { type = NavType.StringType },
                navArgument("subjectId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId") ?: ""
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            
            ChapterSelectionScreen(
                classId = classId,
                subjectId = subjectId,
                onChapterSelected = { fileRef ->
                    // CRITICAL: We pass the file path (e.g., "content/math/...") to the game
                    // We must encode the "/" to ensure navigation doesn't break
                    val encodedPath = java.net.URLEncoder.encode(fileRef, "UTF-8")
                    navController.navigate("game_arena/$encodedPath")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 4. THE GAME ARENA (Universal Engine)
        composable(
            route = "game_arena/{fileRef}",
            arguments = listOf(navArgument("fileRef") { type = NavType.StringType })
        ) { backStackEntry ->
            // Decode the path back to normal
            val encodedRef = backStackEntry.arguments?.getString("fileRef") ?: ""
            val fileRef = java.net.URLDecoder.decode(encodedRef, "UTF-8")
            
            GameArenaScreen(
                fileRef = fileRef,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
