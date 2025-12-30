package com.example.quadrilateralquest.model

import com.google.gson.annotations.SerializedName

// ==========================================
// PART 1: The Master Index (Curriculum Tree)
// ==========================================

data class CurriculumTree(
    @SerializedName("curriculum_version") val version: String,
    val boards: List<Board>
)

data class Board(
    val id: String,
    val name: String,
    val classes: List<SchoolClass>
)

data class SchoolClass(
    val id: String,
    val name: String,
    val realms: List<Realm>
)

data class Realm(
    val id: String,
    val name: String,
    @SerializedName("sub_realms") val subRealms: List<SubRealm>
)

data class SubRealm(
    val name: String,
    val chapters: List<ChapterRef>
)

data class ChapterRef(
    val id: String,
    val name: String,
    @SerializedName("file_ref") val fileRef: String
)

// ==========================================
// PART 2: The Game Scroll (Chapter Content)
// ==========================================

data class ChapterGameScroll(
    @SerializedName("chapter_id") val chapterId: String,
    val meta: ChapterMeta,
    @SerializedName("prodigy_link") val prodigyLink: ProdigyLink?,
    @SerializedName("zone_1_arcade") val arcadeZone: ArcadeZone,
    @SerializedName("zone_2_miniboss") val bossZone: BossZone,
    @SerializedName("zone_3_sanctum") val sanctumZone: SanctumZone
)

data class ChapterMeta(
    val title: String,
    val description: String,
    @SerializedName("learning_outcomes") val outcomes: List<String>
)

data class ProdigyLink(
    @SerializedName("prerequisite_chapter_id") val prerequisiteId: String,
    @SerializedName("bridge_concept") val bridgeConcept: String
)

// --- ZONE 1: ARCADE ---
data class ArcadeZone(
    val description: String,
    val levels: List<ArcadeLevel>
)

data class ArcadeLevel(
    @SerializedName("level_id") val levelId: Int,
    val title: String,
    val type: String, // "match_3", "hidden_object", "runner"
    val instruction: String,
    val data: Map<String, Any> // Flexible container for level-specific data
)

// --- ZONE 2: BOSS ---
data class BossZone(
    @SerializedName("boss_name") val bossName: String,
    val description: String,
    val health: Int,
    val stages: List<BossStage>
)

data class BossStage(
    @SerializedName("stage_id") val stageId: Int,
    val mechanic: String, // "shield_break", "weak_point"
    val narrative: String,
    @SerializedName("question") val questionText: String?,
    @SerializedName("correct_answer") val correctAnswer: String?,
    @SerializedName("damage_deal") val damage: Int,
    val options: List<String>? = null
)

// --- ZONE 3: SANCTUM ---
data class SanctumZone(
    val title: String,
    @SerializedName("access_key_cost") val keyCost: String,
    val quests: List<SanctumQuest>
)

data class SanctumQuest(
    @SerializedName("quest_id") val questId: String,
    val type: String, // "cryptex_input", "assertion_reason"
    @SerializedName("question_text") val questionText: String?,
    @SerializedName("correct_value") val correctValue: String?,

    // Updated fields for Assertion-Reasoning & Multiple Choice
    @SerializedName("assertion") val assertion: String?,
    @SerializedName("reason") val reason: String?,
    val options: List<String>?,
    @SerializedName("correct_option_index") val correctOptionIndex: Int?,
    val explanation: String?,

    @SerializedName("xp_reward") val xpReward: Int
)
