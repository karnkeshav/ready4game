package com.example.quadrilateralquest.logic

data class UserProgress(
    var struggles: MutableMap<String, StruggleMetric> = mutableMapOf(), // key: questionId
    var clarityQuotient: Int = 0,
    var badges: MutableList<String> = mutableListOf(),
    var completedTheorems: MutableSet<String> = mutableSetOf()
)

data class StruggleMetric(
    var timeTakenSeconds: Long = 0,
    var hintsRequested: Int = 0,
    var unsuccessfulInteractions: Int = 0
)

class GameManager {
    val userProgress = UserProgress()

    fun calculateStruggleScore(questionId: String): Double {
        val metric = userProgress.struggles[questionId] ?: return 0.0
        // Formula: Weighted sum (simplified for prototype)
        // Time > 60s adds weight, Hints add weight, Errors add weight
        val timeScore = (metric.timeTakenSeconds / 60.0).coerceAtMost(1.0) * 0.3
        val hintScore = (metric.hintsRequested * 0.2).coerceAtMost(1.0) * 0.3
        val errorScore = (metric.unsuccessfulInteractions * 0.2).coerceAtMost(1.0) * 0.4

        return (timeScore + hintScore + errorScore) * 100
    }

    fun updateStruggleMetric(questionId: String, time: Long, hints: Int, errors: Int) {
        val metric = userProgress.struggles.getOrPut(questionId) { StruggleMetric() }
        metric.timeTakenSeconds += time
        metric.hintsRequested += hints
        metric.unsuccessfulInteractions += errors
    }

    fun needsRemediation(questionId: String, scorePercentage: Int): Boolean {
        if (scorePercentage < 70) return true
        val struggleScore = calculateStruggleScore(questionId)
        return struggleScore > 60.0 // Arbitrary threshold for "high struggle"
    }

    fun updateClarityQuotient(score: Int) {
        userProgress.clarityQuotient += score
    }

    fun checkAndAwardBadges() {
        if (userProgress.completedTheorems.size >= 2 && !userProgress.badges.contains("The Proof Seeker")) {
            userProgress.badges.add("The Proof Seeker")
        }
    }
}
