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
fun SubjectSelectionScreen(navController: NavController, repository: ContentRepository) {
    // Assuming we selected the first class for now or passed it as arg.
    // For prototype, we just get the subjects of the first class.
    val subjects = repository.getChapterConfig()?.classes?.firstOrNull()?.subjects ?: emptyList()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Select Continent (Subject)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        subjects.forEach { subject ->
            Button(
                onClick = { navController.navigate("chapter_selection") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text(subject.name)
            }
        }
    }
}
