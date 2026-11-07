package com.livetranslate.app.data

/**
 * All user-facing settings that are safe to put in DataStore (not the API key).
 *
 * [sourceLanguageCode] is sent as an input-transcription language hint when selected.
 */
data class UserSettings(
    val endpoint: String = Defaults.ENDPOINT,
    val modelId: String = Defaults.MODEL_ID,
    val targetLanguageCode: String = Defaults.TARGET_LANGUAGE,
    val sourceLanguageCode: String = Defaults.SOURCE_LANGUAGE,
    val fontSizeSp: Float = Defaults.FONT_SIZE_SP,
    val textColorHex: String = Defaults.TEXT_COLOR_HEX,
    val backgroundColorHex: String = Defaults.BACKGROUND_COLOR_HEX,
    val isBoldFont: Boolean = Defaults.IS_BOLD_FONT,
    val translatedAudioBufferMs: Int = Defaults.TRANSLATED_AUDIO_BUFFER_MS,
    val translatedAudioDelayMs: Int = Defaults.TRANSLATED_AUDIO_DELAY_MS,
    val videoDelayMs: Int = Defaults.VIDEO_DELAY_MS,
    val originalAudioDelayMs: Int = Defaults.ORIGINAL_AUDIO_DELAY_MS,
    val showFloatingLogo: Boolean = Defaults.SHOW_FLOATING_LOGO,
    val backgroundAlpha: Float = Defaults.BACKGROUND_ALPHA,
    val bilingual: Boolean = Defaults.BILINGUAL,
    val captionsEnabled: Boolean = Defaults.CAPTIONS_ENABLED,
    val playTranslatedAudio: Boolean = Defaults.PLAY_TRANSLATED_AUDIO,
    val translatedVolume: Float = Defaults.TRANSLATED_VOLUME,
    val translatedVoiceName: String = Defaults.TRANSLATED_VOICE_NAME,
    val duckOriginalAudio: Boolean = Defaults.DUCK_ORIGINAL_AUDIO,
    val overlayX: Int = Defaults.OVERLAY_X,
    val overlayY: Int = Defaults.OVERLAY_Y,
    val overlayWidthDp: Int = Defaults.OVERLAY_WIDTH_DP,
    val overlayHeightDp: Int = Defaults.OVERLAY_HEIGHT_DP,
    val overlayPositionLocked: Boolean = Defaults.OVERLAY_POSITION_LOCKED,
    val audioSourceMode: AudioSourceMode = Defaults.AUDIO_SOURCE,
) {
    object Defaults {
        const val ENDPOINT =
            "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1beta.GenerativeService.BidiGenerateContent"
        const val MODEL_ID = "gemini-3.5-live-translate-preview"
        const val TARGET_LANGUAGE = "zh-Hans"
        const val SOURCE_LANGUAGE = "auto"
        const val FONT_SIZE_SP = 18f
        const val TEXT_COLOR_HEX = "#FFFFFF"
        const val BACKGROUND_COLOR_HEX = "#000000"
        const val IS_BOLD_FONT = true
        const val TRANSLATED_AUDIO_BUFFER_MS = 200
        const val TRANSLATED_AUDIO_DELAY_MS = 0
        const val VIDEO_DELAY_MS = 0
        const val ORIGINAL_AUDIO_DELAY_MS = 0
        const val SHOW_FLOATING_LOGO = true
        const val BACKGROUND_ALPHA = 0.65f
        const val BILINGUAL = false
        const val CAPTIONS_ENABLED = true
        const val PLAY_TRANSLATED_AUDIO = false
        const val TRANSLATED_VOLUME = 0.8f
        const val TRANSLATED_VOICE_NAME = "Kore"
        const val DUCK_ORIGINAL_AUDIO = true
        const val OVERLAY_X = 24
        const val OVERLAY_Y = -1
        const val OVERLAY_WIDTH_DP = 360
        const val OVERLAY_HEIGHT_DP = 120
        const val OVERLAY_POSITION_LOCKED = false
        val AUDIO_SOURCE: AudioSourceMode = AudioSourceMode.MEDIA
    }
}
