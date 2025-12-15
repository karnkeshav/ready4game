package com.example.quadrilateralquest.model

import com.google.gson.annotations.SerializedName

data class ChapterConfig(
    val classes: List<ClassConfig>
)

data class ClassConfig(
    val id: String,
    val name: String,
    val subjects: List<SubjectConfig>
)

data class SubjectConfig(
    val id: String,
    val name: String,
    val chapters: List<Chapter>
)

data class Chapter(
    val id: String,
    val name: String,
    val description: String,
    @SerializedName("is_locked") val isLocked: Boolean
)

data class TheoremData(
    @SerializedName("chapter_id") val chapterId: String,
    val theorems: List<Theorem>
)

data class Theorem(
    val id: String,
    val title: String,
    val steps: List<TheoremStep>
)

data class TheoremStep(
    @SerializedName("step_type") val stepType: String,
    val content: String,
    @SerializedName("media_url") val mediaUrl: String
)

data class QuestionData(
    @SerializedName("chapter_id") val chapterId: String,
    val questions: List<Question>
)

data class Question(
    val id: String,
    val type: String, // basic, application, theorem_based
    val text: String,
    val options: List<String>,
    @SerializedName("correct_index") val correctIndex: Int,
    @SerializedName("relevant_theorem_id") val relevantTheoremId: String,
    val hint: String
)
