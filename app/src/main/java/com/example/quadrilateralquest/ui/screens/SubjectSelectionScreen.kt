package com.example.quadrilateralquest.ui.screens

import androidx.compose.foundation.clickable
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
fun SubjectSelectionScreen(navController: NavController, repository: ContentRepository) {
    // 1. Fetch Data
    val tree = remember { repository.getCurriculumTree() }
    
    // 2. Get Realms (Math, Science, Humanities)
    // For prototype simplicity, we just grab Realms from Class 9 (or the first available class)
    val realms = remember(tree) {
        tree?.boards?.firstOrNull()?.classes?.find { it.id == "class_9" }?.realms ?: emptyList()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose Realm", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(realms) { realm ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        // Navigate to Chapter Selection
                        navController.navigate("chapter_selection")
                    },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(realm.name, style = MaterialTheme.typography.headlineSmall)
                        Text("${realm.subRealms.size} Sub-Realms", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
