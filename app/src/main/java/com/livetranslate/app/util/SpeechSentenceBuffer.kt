package com.livetranslate.app.util

/** Collects translated transcript updates and releases complete phrases for speech synthesis. */
class SpeechSentenceBuffer {
    private val transcript = StringBuilder()
    private var spokenPrefixLength = 0

    fun reset() {
        transcript.clear()
        spokenPrefixLength = 0
    }

    fun append(chunk: String): List<String> {
        if (chunk.isBlank()) return emptyList()
        TranscriptBuffer.append(transcript, chunk, maxChars = 0)
        if (spokenPrefixLength > transcript.length) return emptyList()
        val result = mutableListOf<String>()
        while (spokenPrefixLength < transcript.length) {
            val end = findSentenceEnd(spokenPrefixLength) ?: break
            val sentence = transcript.substring(spokenPrefixLength, end).trim()
            spokenPrefixLength = end
            if (sentence.isNotBlank()) result += sentence
        }
        return result
    }

    fun flushPending(): String? {
        if (spokenPrefixLength >= transcript.length) return null
        val pending = transcript.substring(spokenPrefixLength).trim()
        spokenPrefixLength = transcript.length
        return pending.takeIf { it.isNotBlank() }
    }

    private fun findSentenceEnd(start: Int): Int? {
        for (index in start until transcript.length) {
            if (transcript[index] !in SENTENCE_ENDS) continue
            var end = index + 1
            while (end < transcript.length && transcript[end] in CLOSERS) end++
            if (end < transcript.length && transcript[end] == ' ') end++
            return end
        }
        return null
    }

    private companion object {
        val SENTENCE_ENDS = setOf('。', '．', '！', '？', '.', '!', '?', '…', '\n')
        val CLOSERS = setOf('”', '’', '"', '\'', '」', '』', '）', ')', '】', '》', '>')
    }
}
