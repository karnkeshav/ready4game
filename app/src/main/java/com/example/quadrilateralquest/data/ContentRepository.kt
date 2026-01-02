package com.example.quadrilateralquest.data

import android.content.Context
import com.example.quadrilateralquest.model.ChapterScroll
import com.example.quadrilateralquest.model.CurriculumTree
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class ContentRepository(private val context: Context) {

    private val gson = Gson()

    // 1. Load the Master Map (The World)
    // This fetches the high-level curriculum tree (Class -> Realm -> Chapter List)
    fun getCurriculumTree(): CurriculumTree? {
        return loadJson("data/curriculum_tree.json", CurriculumTree::class.java)
    }

    // 2. Load the New Universal Chapter Scroll
    // This fetches the specific gameplay data for a chapter (e.g., "content/math/class9/quadrilaterals.json")
    fun getChapterScroll(fileRef: String): ChapterScroll? {
        // The fileRef usually comes from the curriculum tree, e.g., "content/math/class9/quadrilaterals.json"
        // We prepend "data/" because the actual asset path is "app/src/main/assets/data/..."
        val fullPath = "data/$fileRef"
        
        val type = object : TypeToken<ChapterScroll>() {}.type
        return try {
            val inputStream = context.assets.open(fullPath)
            val reader = InputStreamReader(inputStream)
            val result: ChapterScroll = gson.fromJson(reader, type)
            reader.close()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Helper to generic JSON loading
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
