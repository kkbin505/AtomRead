package com.artifex.mupdf.viewer.app

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class AtomicNote(val rawContent: String) {
    // 自动从 Markdown 内容中提取 # 后的内容作为标题
    val title: String by lazy {
        rawContent.lines()
            .firstOrNull { it.trim().startsWith("# ") }
            ?.replace("# ", "")
            ?.replace(Regex("[\\\\/:*?\"<>|]"), "") // 移除非法文件名字符
            ?.trim()
            ?: "Untitled_Atom_${System.currentTimeMillis()}"
    }
}

class ObsidianExporter(private val context: Context) {

    fun exportSession(pdfName: String, notes: List<String>): String? {
        if (notes.isEmpty()) return null
        
        try {
            val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            
            // 基础目录：Documents/ObsidianVault/Atoms
            val baseDir = File(Environment.getExternalStorageDirectory(), "Documents/ObsidianVault/Atoms")
            if (!baseDir.exists()) baseDir.mkdirs()

            val atomicNotes = notes.map { AtomicNote(it) }

            // 1. 写入每一个原子笔记
            atomicNotes.forEach { note ->
                val file = File(baseDir, "${note.title}.md")
                file.writeText(note.rawContent)
            }

            // 2. 生成索引文件 (MOC)
            val indexFileName = "${pdfName.replace(".pdf", "")}_Index_$dateStr.md"
            val indexFile = File(baseDir, indexFileName)
            
            val indexContent = StringBuilder().apply {
                append("# 📖 学习会话索引: ${pdfName.replace(".pdf", "")}\n\n")
                append("日期: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}\n")
                append("来源文件: [[$pdfName]]\n\n")
                append("## 采集到的知识原子\n\n")
                atomicNotes.forEach { note ->
                    append("- [[${note.title}]]\n")
                }
                append("\n---\n#atom #index")
            }.toString()

            indexFile.writeText(indexContent)
            
            Log.d("ObsidianExporter", "Exported ${notes.size} notes and index to ${indexFile.absolutePath}")
            return indexFile.absolutePath
        } catch (e: Exception) {
            Log.e("ObsidianExporter", "Failed to export session", e)
            return null
        }
    }

    fun exportAtomicNote(content: String, title: String? = null): Boolean {
        val note = AtomicNote(content)
        val finalTitle = title ?: note.title
        return try {
            val baseDir = File(Environment.getExternalStorageDirectory(), "Documents/ObsidianVault/Atoms")
            if (!baseDir.exists()) baseDir.mkdirs()
            val file = File(baseDir, "$finalTitle.md")
            file.writeText(content)
            true
        } catch (e: Exception) {
            false
        }
    }
}
