package com.example.quadrilateralquest.model

import com.google.gson.annotations.SerializedName

// ==========================================
// ROOT: The Master Scroll (Chapter)
// ==========================================
data class ChapterScroll(
    @SerializedName("chapter_id") val chapterId: String,
    val meta: ChapterMeta,
    @SerializedName("stage_1_shaper_lab") val shaperLab: GameZone,
    @SerializedName("stage_2_fractal_forge") val fractalForge: GameZone,
    @SerializedName("stage_3_proof_arena") val proofArena: GameZone,
    @SerializedName("stage_4_hyper_loop") val hyperLoop: RunnerZone,
    @SerializedName("stage_5_sanctum") val sanctum: SanctumZone
)

data class ChapterMeta(
    val title: String,
    val description: String,
    @SerializedName("learning_outcomes") val outcomes: List<String>
)

// ==========================================
// GENERIC ZONES (Lab, Forge, Arena)
// ==========================================
data class GameZone(
    val title: String,
    val description: String,
    val levels: List<GeometryLevel>
)

data class GeometryLevel(
    @SerializedName("level_id") val levelId: String,
    val title: String,
    @SerializedName("mechanic_type") val mechanicType: MechanicType, // ELASTIC_DRAG, CONSTRUCT, DEFENSE
    val instruction: String,
    val data: LevelData // Polymorphic container for specific level details
)

enum class MechanicType {
    @SerializedName("elastic_drag") ELASTIC_DRAG,       // Stage 1
    @SerializedName("midpoint_build") MIDPOINT_BUILD,   // Stage 2
    @SerializedName("proof_defense") PROOF_DEFENSE      // Stage 3
}

// ==========================================
// LEVEL DATA STRUCTURES
// ==========================================
data class LevelData(
    // For Elastic Drag (Shaper's Lab)
    @SerializedName("initial_shape") val initialShape: List<Vertex>?,
    @SerializedName("constraints") val constraints: List<Constraint>?,
    @SerializedName("win_condition") val winCondition: String?, // e.g., "diagonals_equal"

    // For Construction (Fractal Forge)
    @SerializedName("base_structure") val baseStructure: List<Edge>?,
    @SerializedName("target_points") val targetPoints: List<String>?, // e.g., ["midpoint_AB", "midpoint_AC"]

    // For Proof Arena
    @SerializedName("narrative_intro") val narrative: String?,
    @SerializedName("enemy_weakness") val weakness: String? // Logic rule to defeat boss
)

data class Vertex(
    val id: String,
    var x: Float,
    var y: Float,
    @SerializedName("is_locked") val isLocked: Boolean
)

data class Edge(
    val from: String,
    val to: String
)

data class Constraint(
    val type: String, // "parallel", "equal_length", "90_degrees"
    val targets: List<String> // IDs of edges or vertices involved
)

// ==========================================
// SPECIAL ZONES (Runner & Sanctum)
// ==========================================
data class RunnerZone(
    val title: String,
    @SerializedName("duration_seconds") val duration: Int,
    val obstacles: List<RunnerObstacle>
)

data class RunnerObstacle(
    val prompt: String,
    @SerializedName("correct_gate") val correctGate: String, // "Rhombus"
    @SerializedName("wrong_gates") val wrongGates: List<String>
)

data class SanctumZone(
    val title: String,
    val exercises: List<SanctumExercise>
)

data class SanctumExercise(
    @SerializedName("exercise_ref") val exerciseRef: String, // "Ex 8.1 Q7"
    val task: String,
    @SerializedName("tool_required") val toolRequired: String // "parallel_line_tool", "bisector_tool"
)
