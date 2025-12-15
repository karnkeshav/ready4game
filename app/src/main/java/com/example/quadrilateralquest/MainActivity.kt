package com.example.quadrilateralquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.quadrilateralquest.data.ContentRepository
import com.example.quadrilateralquest.logic.GameManager
import com.example.quadrilateralquest.ui.AppNavigation
import com.example.quadrilateralquest.ui.theme.QuadrilateralQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ContentRepository(this)
        val gameManager = GameManager() // In real app, this should be scoped/ViewModel

        setContent {
            QuadrilateralQuestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(repository, gameManager)
                }
            }
        }
    }
}
