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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ChapterSelectionScreen(navController: NavController, repository: ContentRepository) {
    // 1. Fetch the new Curriculum Tree (The Codex)
    val tree = remember { repository.getCurriculumTree() }

    // 2. Flatten the tree to find all chapters (For prototyping ease)
    // This grabs every chapter from every Class/Realm so you can see them all in one list.
    val chapters = remember(tree) {
        val allChapters = mutableListOf<com.example.quadrilateralquest.model.ChapterRef>()
        
        tree?.boards?.forEach { board ->
            board.classes.forEach { schoolClass ->
                schoolClass.realms.forEach { realm ->
                    realm.subRealms.forEach { subRealm ->
                        allChapters.addAll(subRealm.chapters)
                    }
                }
            }
        }
        allChapters
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Chapter", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (chapters.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No chapters found in Codex. Check curriculum_tree.json")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(chapters) { chapter ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(chapter.name, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    // 3. THE NEW NAVIGATION TRIGGER
                                    // We must encode the file path (e.g., "content/math/...") to pass it safely in the URL
                                    try {
                                        val encodedRef = URLEncoder.encode(chapter.fileRef, StandardCharsets.UTF_8.toString())
                                        navController.navigate("game_arena/$encodedRef")
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Enter Game Arena")
                            }
                        }
                    }
                }
            }
        }
    }
}
