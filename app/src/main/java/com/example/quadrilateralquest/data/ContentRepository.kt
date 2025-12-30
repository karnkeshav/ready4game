package com.example.quadrilateralquest.data

import android.content.Context
import com.example.quadrilateralquest.model.ChapterGameScroll
import com.example.quadrilateralquest.model.CurriculumTree
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class ContentRepository(private val context: Context) {

    private val gson = Gson()

    // 1. Load the Master Map (The World)
    fun getCurriculumTree(): CurriculumTree? {
        return loadJson("data/curriculum_tree.json", CurriculumTree::class.java)
    }

    // 2. Load a Specific Chapter Scroll (The Game Level)
    // Usage: loadChapterGame("content/math/class9/quadrilaterals.json")
    fun getChapterGame(fileRef: String): ChapterGameScroll? {
        // We prepend "data/" because the file_ref in JSON is relative to the data folder
        val fullPath = "data/$fileRef"
        
        // Custom adapter might be needed for the 'data' map in ArcadeLevel, 
        // but default Gson handles Map<String, Any> reasonably well for basic types.
        val type = object : TypeToken<ChapterGameScroll>() {}.type
        return try {
            val inputStream = context.assets.open(fullPath)
            val reader = InputStreamReader(inputStream)
            val result: ChapterGameScroll = gson.fromJson(reader, type)
            reader.close()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
