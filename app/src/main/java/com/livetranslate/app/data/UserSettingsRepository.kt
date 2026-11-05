package com.livetranslate.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserSettingsRepository(private val context: Context) {
    private object Keys {
        val endpoint = stringPreferencesKey("endpoint")
        val modelId = stringPreferencesKey("model_id")
        val targetLanguage = stringPreferencesKey("target_language")
        val sourceLanguage = stringPreferencesKey("source_language")
        val fontSizeSp = floatPreferencesKey("font_size_sp")
        val backgroundAlpha = floatPreferencesKey("background_alpha")
        val bilingual = booleanPreferencesKey("bilingual")
        val captionsEnabled = booleanPreferencesKey("captions_enabled")
        val playTranslatedAudio = booleanPreferencesKey("play_translated_audio")
        val translatedVolume = floatPreferencesKey("translated_volume")
        val translatedVoiceName = stringPreferencesKey("translated_voice_name")
        val duckOriginalAudio = booleanPreferencesKey("duck_original_audio")
        val overlayX = intPreferencesKey("overlay_x")
        val overlayY = intPreferencesKey("overlay_y")
        val overlayWidthDp = intPreferencesKey("overlay_width_dp")
        val overlayHeightDp = intPreferencesKey("overlay_height_dp")
        val overlayPositionLocked = booleanPreferencesKey("overlay_position_locked")
        val audioSourceMode = stringPreferencesKey("audio_source_mode")
    }

    val settings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        prefs.toSettings()
    }

    suspend fun update(transform: (UserSettings) -> UserSettings) {
        context.dataStore.edit { prefs ->
            val next = transform(prefs.toSettings())
            prefs[Keys.endpoint] = next.endpoint
            prefs[Keys.modelId] = next.modelId
            prefs[Keys.targetLanguage] = next.targetLanguageCode
            prefs[Keys.sourceLanguage] = next.sourceLanguageCode
            prefs[Keys.fontSizeSp] = next.fontSizeSp
            prefs[Keys.backgroundAlpha] = next.backgroundAlpha
            prefs[Keys.bilingual] = next.bilingual
            prefs[Keys.captionsEnabled] = next.captionsEnabled
            prefs[Keys.playTranslatedAudio] = next.playTranslatedAudio
            prefs[Keys.translatedVolume] = next.translatedVolume
            prefs[Keys.translatedVoiceName] = next.translatedVoiceName
            prefs[Keys.duckOriginalAudio] = next.duckOriginalAudio
            prefs[Keys.overlayX] = next.overlayX
            prefs[Keys.overlayY] = next.overlayY
            prefs[Keys.overlayWidthDp] = next.overlayWidthDp
            prefs[Keys.overlayHeightDp] = next.overlayHeightDp
            prefs[Keys.overlayPositionLocked] = next.overlayPositionLocked
            prefs[Keys.audioSourceMode] = next.audioSourceMode.name
        }
    }

    suspend fun resetSubtitleAppearance() {
        update {
            it.copy(
                fontSizeSp = UserSettings.Defaults.FONT_SIZE_SP,
                backgroundAlpha = UserSettings.Defaults.BACKGROUND_ALPHA,
                bilingual = UserSettings.Defaults.BILINGUAL,
            )
        }
    }

    private fun Preferences.toSettings(): UserSettings = UserSettings(
        endpoint = this[Keys.endpoint] ?: UserSettings.Defaults.ENDPOINT,
        modelId = this[Keys.modelId] ?: UserSettings.Defaults.MODEL_ID,
        targetLanguageCode = SupportedLanguages.canonicalOrDefault(
            this[Keys.targetLanguage] ?: UserSettings.Defaults.TARGET_LANGUAGE,
        ),
        sourceLanguageCode = SupportedLanguages.canonicalSourceOrAuto(
            this[Keys.sourceLanguage] ?: UserSettings.Defaults.SOURCE_LANGUAGE,
        ),
        fontSizeSp = this[Keys.fontSizeSp] ?: UserSettings.Defaults.FONT_SIZE_SP,
        backgroundAlpha = this[Keys.backgroundAlpha] ?: UserSettings.Defaults.BACKGROUND_ALPHA,
        bilingual = this[Keys.bilingual] ?: UserSettings.Defaults.BILINGUAL,
        captionsEnabled = this[Keys.captionsEnabled] ?: UserSettings.Defaults.CAPTIONS_ENABLED,
        playTranslatedAudio = this[Keys.playTranslatedAudio]
            ?: UserSettings.Defaults.PLAY_TRANSLATED_AUDIO,
        translatedVolume = this[Keys.translatedVolume] ?: UserSettings.Defaults.TRANSLATED_VOLUME,
        translatedVoiceName = this[Keys.translatedVoiceName] ?: UserSettings.Defaults.TRANSLATED_VOICE_NAME,
        duckOriginalAudio = this[Keys.duckOriginalAudio] ?: UserSettings.Defaults.DUCK_ORIGINAL_AUDIO,
        overlayX = this[Keys.overlayX] ?: UserSettings.Defaults.OVERLAY_X,
        overlayY = this[Keys.overlayY] ?: UserSettings.Defaults.OVERLAY_Y,
        overlayWidthDp = this[Keys.overlayWidthDp] ?: UserSettings.Defaults.OVERLAY_WIDTH_DP,
        overlayHeightDp = this[Keys.overlayHeightDp] ?: UserSettings.Defaults.OVERLAY_HEIGHT_DP,
        overlayPositionLocked = this[Keys.overlayPositionLocked] ?: UserSettings.Defaults.OVERLAY_POSITION_LOCKED,
        audioSourceMode = AudioSourceMode.fromStorage(this[Keys.audioSourceMode]),
    )
}
