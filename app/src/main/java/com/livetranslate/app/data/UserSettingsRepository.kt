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
        val textColorHex = stringPreferencesKey("text_color_hex")
        val backgroundColorHex = stringPreferencesKey("background_color_hex")
        val backgroundAlpha = floatPreferencesKey("background_alpha")
        val isBoldFont = booleanPreferencesKey("is_bold_font")
        val outlineEnabled = booleanPreferencesKey("outline_enabled")
        val outlineColorHex = stringPreferencesKey("outline_color_hex")
        val translatedAudioDelayMs = intPreferencesKey("translated_audio_delay_ms")
        val translatedAudioBufferMs = intPreferencesKey("translated_audio_buffer_ms")
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
            prefs[Keys.textColorHex] = next.textColorHex
            prefs[Keys.backgroundColorHex] = next.backgroundColorHex
            prefs[Keys.backgroundAlpha] = next.backgroundAlpha
            prefs[Keys.isBoldFont] = next.isBoldFont
            prefs[Keys.outlineEnabled] = next.outlineEnabled
            prefs[Keys.outlineColorHex] = next.outlineColorHex
            prefs[Keys.translatedAudioDelayMs] = next.translatedAudioDelayMs
            prefs[Keys.translatedAudioBufferMs] = next.translatedAudioBufferMs
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
                textColorHex = UserSettings.Defaults.TEXT_COLOR_HEX,
                backgroundColorHex = UserSettings.Defaults.BACKGROUND_COLOR_HEX,
                backgroundAlpha = UserSettings.Defaults.BACKGROUND_ALPHA,
                isBoldFont = UserSettings.Defaults.IS_BOLD_FONT,
                outlineEnabled = UserSettings.Defaults.OUTLINE_ENABLED,
                outlineColorHex = UserSettings.Defaults.OUTLINE_COLOR_HEX,
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
        textColorHex = this[Keys.textColorHex] ?: UserSettings.Defaults.TEXT_COLOR_HEX,
        backgroundColorHex = this[Keys.backgroundColorHex] ?: UserSettings.Defaults.BACKGROUND_COLOR_HEX,
        backgroundAlpha = this[Keys.backgroundAlpha] ?: UserSettings.Defaults.BACKGROUND_ALPHA,
        isBoldFont = this[Keys.isBoldFont] ?: UserSettings.Defaults.IS_BOLD_FONT,
        outlineEnabled = this[Keys.outlineEnabled] ?: UserSettings.Defaults.OUTLINE_ENABLED,
        outlineColorHex = this[Keys.outlineColorHex] ?: UserSettings.Defaults.OUTLINE_COLOR_HEX,
        translatedAudioDelayMs = (this[Keys.translatedAudioDelayMs] ?: UserSettings.Defaults.TRANSLATED_AUDIO_DELAY_MS).let { if (it == 2000) 0 else it },
        translatedAudioBufferMs = this[Keys.translatedAudioBufferMs] ?: UserSettings.Defaults.TRANSLATED_AUDIO_BUFFER_MS,
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
