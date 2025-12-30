package com.example.quadrilateralquest.logic

import com.example.quadrilateralquest.model.ChapterGameScroll

enum class GameZone { ARCADE, BOSS, SANCTUM }

class ArenaEngine {

    var currentZone: GameZone = GameZone.ARCADE
    var currentBossHealth: Int = 0
    var maxBossHealth: Int = 0
    var sanctumKeys: Int = 0
    
    // Track player stats for this specific session
    var sessionScore: Int = 0
    var sessionErrors: Int = 0

    // Initialize the Arena with data from the Scroll
    fun loadChapter(scroll: ChapterGameScroll) {
        // Reset State
        currentZone = GameZone.ARCADE
        sessionScore = 0
        sessionErrors = 0
        
        // Setup Boss Stats
        maxBossHealth = scroll.bossZone.health
        currentBossHealth = maxBossHealth
        
        // Setup Keys (Simulated logic: completing Arcade gives 1 key)
        sanctumKeys = 0 
    }

    fun attackBoss(damageAmount: Int): BossState {
        currentBossHealth -= damageAmount
        if (currentBossHealth < 0) currentBossHealth = 0
        
        return if (currentBossHealth == 0) {
            sanctumKeys++ // Drop a key on boss defeat
            BossState.DEFEATED
        } else {
            BossState.ALIVE
        }
    }

    fun unlockSanctum(): Boolean {
        if (currentZone == GameZone.BOSS && currentBossHealth == 0 && sanctumKeys > 0) {
            currentZone = GameZone.SANCTUM
            return true
        }
        return false
    }

    // Helper for UI to display health bar
    fun getBossHealthPercentage(): Float {
        if (maxBossHealth == 0) return 0f
        return currentBossHealth.toFloat() / maxBossHealth.toFloat()
    }

    // Returns the XP earned if correct, or 0 if wrong
    fun submitSanctumAnswer(quest: com.example.quadrilateralquest.model.SanctumQuest, userAnswer: String): Int {
        var isCorrect = false

        if (quest.type == "cryptex_input") {
            // Normalize string (trim spaces, ignore case)
            if (userAnswer.trim().equals(quest.correctValue?.trim(), ignoreCase = true)) {
                isCorrect = true
            }
        } else if (quest.type == "assertion_reason") {
            // User answer is expected to be the index "0", "1", etc.
            val index = userAnswer.toIntOrNull()
            if (index != null && index == quest.correctOptionIndex) {
                isCorrect = true
            }
        }

        return if (isCorrect) {
            sessionScore += quest.xpReward
            quest.xpReward
        } else {
            sessionErrors++
            0
        }
    }
}

enum class BossState { ALIVE, DEFEATED }
