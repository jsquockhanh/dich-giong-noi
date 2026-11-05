package com.livetranslate.app.audio

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException

/** Requests a selectable Gemini TTS voice for translated text, using the existing Gemini key. */
class GeminiSpeechSynthesizer {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun synthesize(
        apiKey: String,
        targetLanguageCode: String,
        voiceName: String,
        text: String,
    ): SpeechAudio? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || text.isBlank()) return@withContext null

        val target = targetLanguageCode.ifBlank { "vi" }
        val prompt = "Read the following translated text in language code $target exactly as written. " +
            "Do not translate it, explain it, or add any words. Text:\n$text"
        val body = JSONObject()
            .put("model", MODEL_ID)
            .put("input", prompt)
            .put("response_format", JSONObject().put("type", "audio"))
            .put(
                "generation_config",
                JSONObject().put(
                    "speech_config",
                    JSONArray().put(JSONObject().put("voice", if (voiceName == VOICE_MALE) VOICE_MALE else VOICE_FEMALE)),
                ),
            )
            .toString()

        val encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8.name())
        val request = Request.Builder()
            .url("$ENDPOINT?key=$encodedKey")
            .header("Content-Type", "application/json")
            .post(body.toRequestBody(JSON_MEDIA_TYPE))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val payload = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    Log.w(TAG, "Gemini TTS failed: HTTP ${response.code} ${payload.take(300)}")
                    return@withContext null
                }
                val parsed = JSONObject(payload)
                val audio = findAudio(parsed)
                if (audio == null) {
                    Log.w(TAG, "Gemini TTS response did not contain audio")
                    null
                } else {
                    SpeechAudio(
                        pcm = Base64.decode(audio.first, Base64.DEFAULT),
                        mimeType = audio.second ?: DEFAULT_MIME_TYPE,
                    )
                }
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Log.w(TAG, "Gemini TTS request failed", error)
            null
        }
    }

    private fun findAudio(root: JSONObject): Pair<String, String?>? {
        val outputAudio = root.optJSONObject("output_audio") ?: root.optJSONObject("outputAudio")
        outputAudio?.optString("data")?.takeIf { it.isNotBlank() }?.let { data ->
            return data to (outputAudio.optString("mime_type").ifBlank {
                outputAudio.optString("mimeType").ifBlank { null }
            })
        }

        val steps = root.optJSONArray("steps")
        if (steps != null) {
            for (i in 0 until steps.length()) {
                val step = steps.optJSONObject(i) ?: continue
                val content = step.optJSONArray("content") ?: continue
                for (j in 0 until content.length()) {
                    val item = content.optJSONObject(j) ?: continue
                    val data = item.optString("data").takeIf { it.isNotBlank() }
                        ?: item.optJSONObject("audio")?.optString("data")?.takeIf { it.isNotBlank() }
                    if (data != null) {
                        return data to (item.optString("mime_type").ifBlank {
                            item.optString("mimeType").ifBlank { null }
                        })
                    }
                }
            }
        }

        // Accept the previous Interactions response shape and GenerateContent-style audio parts.
        val outputs = root.optJSONArray("outputs")
        if (outputs != null) {
            for (i in 0 until outputs.length()) {
                val output = outputs.optJSONObject(i) ?: continue
                output.optString("data").takeIf { it.isNotBlank() }?.let { data ->
                    return data to output.optString("mime_type").ifBlank { null }
                }
                val content = output.optJSONArray("content") ?: continue
                for (j in 0 until content.length()) {
                    val item = content.optJSONObject(j) ?: continue
                    val data = item.optString("data").takeIf { it.isNotBlank() }
                        ?: item.optJSONObject("inline_data")?.optString("data")?.takeIf { it.isNotBlank() }
                    if (data != null) {
                        val inline = item.optJSONObject("inline_data")
                        return data to (item.optString("mime_type").ifBlank {
                            inline?.optString("mime_type")?.ifBlank { null }
                        })
                    }
                }
            }
        }
        val candidates = root.optJSONArray("candidates") ?: return null
        for (i in 0 until candidates.length()) {
            val parts = candidates.optJSONObject(i)?.optJSONObject("content")?.optJSONArray("parts") ?: continue
            for (j in 0 until parts.length()) {
                val part = parts.optJSONObject(j) ?: continue
                val inline = part.optJSONObject("inlineData") ?: part.optJSONObject("inline_data") ?: continue
                val data = inline.optString("data").takeIf { it.isNotBlank() } ?: continue
                return data to (inline.optString("mimeType").ifBlank {
                    inline.optString("mime_type").ifBlank { null }
                })
            }
        }
        return null
    }

    data class SpeechAudio(val pcm: ByteArray, val mimeType: String)

    companion object {
        const val VOICE_FEMALE = "Kore"
        const val VOICE_MALE = "Puck"
        private const val MODEL_ID = "gemini-3.1-flash-tts-preview"
        private const val ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/interactions"
        private const val DEFAULT_MIME_TYPE = "audio/pcm;rate=24000"
        private const val TAG = "GeminiSpeechTTS"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
