package com.example.quadrilateralquest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quadrilateralquest.data.ContentRepository

@Composable
fun IndexScreen(navController: NavController, repository: ContentRepository) {
    val classes = repository.getChapterConfig()?.classes ?: emptyList()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Universe of Knowledge", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Text("Select Your World (Class)", style = MaterialTheme.typography.titleMedium)

        classes.forEach { classConfig ->
            Button(
                onClick = { navController.navigate("subject_selection") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text(classConfig.name)
            }
        }
    }
}
