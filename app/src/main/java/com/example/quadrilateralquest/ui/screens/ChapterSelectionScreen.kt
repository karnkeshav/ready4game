package com.example.quadrilateralquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quadrilateralquest.data.ContentRepository

@Composable
fun ChapterSelectionScreen(navController: NavController, repository: ContentRepository) {
    val chapters = repository.getChapterConfig()?.classes?.firstOrNull()?.subjects?.firstOrNull()?.chapters ?: emptyList()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Select City (Chapter)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        chapters.forEach { chapter ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(chapter.name, style = MaterialTheme.typography.titleLarge)
                    Text(chapter.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("login") },
                        enabled = !chapter.isLocked
                    ) {
                        Text("Start the Quest")
                    }
                }
            }
        }
    }
}
