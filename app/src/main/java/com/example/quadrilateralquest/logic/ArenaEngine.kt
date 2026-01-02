package com.example.quadrilateralquest.logic

import com.example.quadrilateralquest.model.*

// The 5 Stages of the Universal Blueprint
enum class GameStage { SHAPER_LAB, FRACTAL_FORGE, PROOF_ARENA, HYPER_LOOP, SANCTUM }

class ArenaEngine {

    var currentStage: GameStage = GameStage.SHAPER_LAB
    var activeScroll: ChapterScroll? = null
    
    // Track progress within the current stage
    var currentLevelIndex: Int = 0
    var isStageComplete: Boolean = false

    // Session Stats
    var score: Int = 0
    var keystonesEarned: Int = 0

    // Initialize the Engine with the new Chapter Scroll
    fun loadChapter(scroll: ChapterScroll) {
        activeScroll = scroll
        currentStage = GameStage.SHAPER_LAB
        currentLevelIndex = 0
        isStageComplete = false
        score = 0
    }

    // Returns the current level data based on the active stage
    fun getCurrentLevel(): GeometryLevel? {
        val scroll = activeScroll ?: return null
        
        val levels = when (currentStage) {
            GameStage.SHAPER_LAB -> scroll.shaperLab.levels
            GameStage.FRACTAL_FORGE -> scroll.fractalForge.levels
            GameStage.PROOF_ARENA -> scroll.proofArena.levels
            else -> emptyList()
        }
        
        return levels.getOrNull(currentLevelIndex)
    }

    // Call this when a player solves a puzzle (Drag, Construct, or Prove)
    fun completeLevel() {
        val scroll = activeScroll ?: return
        
        // Determine total levels in current stage
        val levelsInStage = when (currentStage) {
            GameStage.SHAPER_LAB -> scroll.shaperLab.levels.size
            GameStage.FRACTAL_FORGE -> scroll.fractalForge.levels.size
            GameStage.PROOF_ARENA -> scroll.proofArena.levels.size
            else -> 0
        }

        // Advance Logic
        if (currentLevelIndex < levelsInStage - 1) {
            currentLevelIndex++
        } else {
            completeStage()
        }
    }

    private fun completeStage() {
        isStageComplete = true
        // Auto-advance to next stage (Simplified for Phase 1 flow)
        currentStage = when (currentStage) {
            GameStage.SHAPER_LAB -> GameStage.FRACTAL_FORGE
            GameStage.FRACTAL_FORGE -> GameStage.PROOF_ARENA
            GameStage.PROOF_ARENA -> GameStage.HYPER_LOOP
            GameStage.HYPER_LOOP -> GameStage.SANCTUM
            GameStage.SANCTUM -> GameStage.SANCTUM // End of Line for now
        }
        currentLevelIndex = 0 // Reset for new stage
        isStageComplete = false
    }

    // Specific Logic for Hyper-Loop (Runner)
    fun processRunnerInput(obstacle: RunnerObstacle, chosenGate: String): Boolean {
        return if (chosenGate == obstacle.correctGate) {
            score += 100
            true
        } else {
            score -= 50
            false
        }
    }
}
