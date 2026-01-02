package com.example.quadrilateralquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.quadrilateralquest.ui.QuadrilateralQuestApp // Import the new Navigation
import com.example.quadrilateralquest.ui.theme.QuadrilateralQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Note: Repository and GameManager are now initialized inside QuadrilateralQuestApp 
        // or the specific screens, so we don't need to pass them here anymore.

        setContent {
            QuadrilateralQuestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    // CALL THE NEW NAVIGATION HOST
                    QuadrilateralQuestApp()
                }
            }
        }
    }
}
