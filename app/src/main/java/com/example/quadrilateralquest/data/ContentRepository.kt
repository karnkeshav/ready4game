package com.example.quadrilateralquest.data

import android.content.Context
import com.example.quadrilateralquest.model.ChapterConfig
import com.example.quadrilateralquest.model.QuestionData
import com.example.quadrilateralquest.model.TheoremData
import com.google.gson.Gson
import java.io.InputStreamReader

class ContentRepository(private val context: Context) {

    private val gson = Gson()

    fun getChapterConfig(): ChapterConfig? {
        return loadJson("data/chapter_config.json", ChapterConfig::class.java)
    }

    fun getTheorems(): TheoremData? {
        return loadJson("data/theorems.json", TheoremData::class.java)
    }

    fun getQuestions(): QuestionData? {
        return loadJson("data/questions.json", QuestionData::class.java)
    }

    private fun <T> loadJson(assetPath: String, classOfT: Class<T>): T? {
        return try {
            val inputStream = context.assets.open(assetPath)
            val reader = InputStreamReader(inputStream)
            val result = gson.fromJson(reader, classOfT)
            reader.close()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
