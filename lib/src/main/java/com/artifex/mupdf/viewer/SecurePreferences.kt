package com.artifex.mupdf.viewer

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurePreferences {
    private const val PREFS_NAME = "secure_prefs"
    private const val KEY_OPENAI_API_KEY = "openai_api_key"
    private const val KEY_AI_MODEL = "ai_model"
    private const val KEY_AI_BASE_URL = "ai_base_url"

    private const val DEFAULT_MODEL = "gpt-4.1-mini"
    private const val DEFAULT_BASE_URL = "https://api.openai.com/v1"

    private fun getEncryptedPrefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveApiKey(context: Context, apiKey: String) {
        getEncryptedPrefs(context).edit().putString(KEY_OPENAI_API_KEY, apiKey).apply()
    }

    fun getApiKey(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_OPENAI_API_KEY, null)
    }

    fun saveModel(context: Context, model: String) {
        getEncryptedPrefs(context).edit().putString(KEY_AI_MODEL, model).apply()
    }

    fun getModel(context: Context): String {
        return getEncryptedPrefs(context).getString(KEY_AI_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL
    }

    fun saveBaseUrl(context: Context, baseUrl: String) {
        getEncryptedPrefs(context).edit().putString(KEY_AI_BASE_URL, baseUrl).apply()
    }

    fun getBaseUrl(context: Context): String {
        return getEncryptedPrefs(context).getString(KEY_AI_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }

    fun hasApiKey(context: Context): Boolean {
        return !getApiKey(context).isNullOrEmpty()
    }
}
