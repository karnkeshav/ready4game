package com.example.quadrilateralquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quadrilateralquest.data.ContentRepository

@Composable
fun IndexScreen(navController: NavController, repository: ContentRepository) {
    // 1. Load the Master Index (Lightweight fetch)
    val tree = remember { repository.getCurriculumTree() }
    
    // 2. Extract Classes from the CBSE board (Default)
    val classes = remember(tree) {
        tree?.boards?.find { it.id == "cbse" }?.classes ?: emptyList()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Class", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        if (classes.isEmpty()) {
            CircularProgressIndicator()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(classes) { schoolClass ->
                    Button(
                        onClick = { 
                            // Navigate to Subject Selection
                            navController.navigate("subject_selection") 
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text(schoolClass.name, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}
