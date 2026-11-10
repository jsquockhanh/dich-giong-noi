package com.livetranslate.app.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.livetranslate.app.R
import com.livetranslate.app.BuildConfig
import com.livetranslate.app.data.ApiKeyStore
import com.livetranslate.app.ui.components.PageTitle
import com.livetranslate.app.ui.components.SectionCard
import com.livetranslate.app.ui.components.SettingSwitchRow
import com.livetranslate.app.ui.theme.Booth
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    onOpenOverlayPermission: () -> Unit,
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val apiKeyFields by viewModel.apiKeyFields.collectAsStateWithLifecycle()
    val testResult by viewModel.testResult.collectAsStateWithLifecycle()
    val testing by viewModel.testing.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val appLanguage = AppCompatDelegate.getApplicationLocales().toLanguageTags()

    var endpointField by remember {
        mutableStateOf(TextFieldValue(settings.endpoint, TextRange(settings.endpoint.length)))
    }
    var modelField by remember {
        mutableStateOf(TextFieldValue(settings.modelId, TextRange(settings.modelId.length)))
    }
    var revealKey by remember { mutableStateOf(false) }

    var endpointHydrated by remember { mutableStateOf(false) }
    var modelHydrated by remember { mutableStateOf(false) }
    LaunchedEffect(settings.endpoint) {
        if (!endpointHydrated && settings.endpoint.isNotBlank()) {
            endpointField = TextFieldValue(settings.endpoint, TextRange(settings.endpoint.length))
            endpointHydrated = true
        }
    }
    LaunchedEffect(settings.modelId) {
        if (!modelHydrated && settings.modelId.isNotBlank()) {
            modelField = TextFieldValue(settings.modelId, TextRange(settings.modelId.length))
            modelHydrated = true
        }
    }

    // Dark-mode safe field colors (explicit text / cursor colors)
    val scheme = MiuixTheme.colorScheme
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = scheme.onSurface,
        unfocusedTextColor = scheme.onSurface,
        disabledTextColor = scheme.disabledOnSurface,
        focusedBorderColor = Booth.Accent,
        unfocusedBorderColor = scheme.outline.copy(alpha = 0.45f),
        focusedLabelColor = Booth.Accent,
        unfocusedLabelColor = scheme.onSurfaceVariantSummary,
        cursorColor = Booth.Accent,
        focusedContainerColor = scheme.surface,
        unfocusedContainerColor = scheme.surface,
        focusedPlaceholderColor = scheme.onSurfaceVariantSummary,
        unfocusedPlaceholderColor = scheme.onSurfaceVariantSummary,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PageTitle(
            title = stringResource(R.string.settings_title),
            subtitle = stringResource(R.string.settings_subtitle),
        )

        SmallTitle(
            text = stringResource(R.string.settings_interface_language),
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        SectionCard {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_interface_language_hint),
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                listOf(
                    Triple("system", R.string.settings_language_system, ""),
                    Triple("vi", R.string.settings_language_vietnamese, "vi"),
                    Triple("zh", R.string.settings_language_chinese, "zh"),
                ).forEach { (key, label, tag) ->
                    VoiceChoiceRow(
                        label = stringResource(label),
                        selected = if (key == "system") appLanguage.isBlank() else appLanguage.startsWith(tag, ignoreCase = true),
                        onClick = {
                            val locales = if (key == "system") LocaleListCompat.getEmptyLocaleList()
                            else LocaleListCompat.forLanguageTags(tag)
                            AppCompatDelegate.setApplicationLocales(locales)
                        },
                    )
                }
            }
        }

        SmallTitle(
            text = stringResource(R.string.settings_api),
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        SectionCard {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_api_desc),
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                OutlinedTextField(
                    value = endpointField,
                    onValueChange = { v ->
                        endpointField = v
                        viewModel.update { it.copy(endpoint = v.text.trim()) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { androidx.compose.material3.Text(stringResource(R.string.settings_endpoint)) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                )

                Text(
                    text = stringResource(R.string.settings_api_keys_hint),
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )

                apiKeyFields.forEachIndexed { index, field ->
                    val isLast = index == apiKeyFields.lastIndex
                    val canAdd = isLast && apiKeyFields.size < ApiKeyStore.MAX_KEYS
                    val canRemove = index > 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (canAdd) {
                            IconButton(
                                onClick = viewModel::addApiKeyField,
                                modifier = Modifier.size(40.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Add,
                                    contentDescription = stringResource(R.string.settings_add_key),
                                    tint = Booth.Accent,
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(40.dp))
                        }
                        OutlinedTextField(
                            value = field,
                            onValueChange = { v -> viewModel.updateApiKeyField(index, v) },
                            modifier = Modifier.weight(1f),
                            label = {
                                androidx.compose.material3.Text(
                                    stringResource(R.string.settings_api_key_n, index + 1),
                                )
                            },
                            singleLine = true,
                            visualTransformation = if (revealKey) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                            shape = RoundedCornerShape(16.dp),
                            colors = fieldColors,
                        )
                        if (canRemove) {
                            IconButton(
                                onClick = { viewModel.removeApiKeyField(index) },
                                modifier = Modifier.size(40.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Remove,
                                    contentDescription = stringResource(R.string.settings_remove_key),
                                    tint = Booth.Danger,
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(40.dp))
                        }
                    }
                }

                TextButton(
                    text = stringResource(
                        if (revealKey) R.string.settings_hide else R.string.settings_show,
                    ),
                    onClick = { revealKey = !revealKey },
                )

                OutlinedTextField(
                    value = modelField,
                    onValueChange = { v ->
                        modelField = v
                        viewModel.update { it.copy(modelId = v.text.trim()) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { androidx.compose.material3.Text(stringResource(R.string.settings_model_id)) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                )
                Button(
                    onClick = {
                        viewModel.saveApiKeys()
                        viewModel.testConnection()
                    },
                    enabled = !testing,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColorsPrimary(),
                ) {
                    Text(
                        text = stringResource(
                            if (testing) R.string.settings_testing else R.string.settings_save_and_test,
                        ),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                TextButton(
                    text = stringResource(R.string.settings_save_key_only),
                    onClick = viewModel::saveApiKeys,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (!testResult.isNullOrBlank()) {
                    val resultText = testResult.orEmpty()
                    val hasOk = resultText.contains("✅")
                    val hasFail = resultText.contains("❌")
                    Text(
                        text = resultText,
                        fontSize = 13.sp,
                        color = when {
                            hasOk && !hasFail -> Booth.Success
                            hasFail && !hasOk -> Booth.Danger
                            else -> MiuixTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        },
                    )
                }
            }
        }

        SmallTitle(
            text = stringResource(R.string.settings_appearance),
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        SectionCard {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_font_size, settings.fontSizeSp.toInt()),
                    fontSize = 13.sp,
                )
                Slider(
                    value = settings.fontSizeSp,
                    onValueChange = { size -> viewModel.update { it.copy(fontSizeSp = size) } },
                    valueRange = 12f..32f,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(
                        R.string.settings_bg_alpha,
                        (settings.backgroundAlpha * 100).toInt(),
                    ),
                    fontSize = 13.sp,
                )
                Slider(
                    value = settings.backgroundAlpha,
                    onValueChange = { a -> viewModel.update { it.copy(backgroundAlpha = a) } },
                    valueRange = 0f..0.95f,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "Màu chữ phụ đề",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                ColorPalette(
                    colors = listOf(
                        "#FFFFFF", "#000000", "#F44336", "#FF9800", "#FFC107",
                        "#4CAF50", "#2196F3", "#9C27B0", "#E91E63", "#9E9E9E",
                    ),
                    selected = settings.textColorHex,
                    onSelect = { color -> viewModel.update { it.copy(textColorHex = color) } },
                )
                Text(
                    text = "Màu nền phụ đề",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                ColorPalette(
                    colors = listOf(
                        "#000000", "#FFFFFF", "#263238", "#37474F", "#174EA6",
                        "#4A148C", "#B71C1C", "#1B5E20", "#E65100", "#607D8B",
                    ),
                    selected = settings.backgroundColorHex,
                    onSelect = { color -> viewModel.update { it.copy(backgroundColorHex = color) } },
                )
                Text(
                    text = "Xem trước phụ đề",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            runCatching { Color(android.graphics.Color.parseColor(settings.backgroundColorHex)) }
                                .getOrDefault(Color.Black)
                                .copy(alpha = settings.backgroundAlpha.coerceIn(0f, 0.95f)),
                            RoundedCornerShape(14.dp),
                        )
                        .padding(horizontal = 14.dp, vertical = 18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "你好！\\nXin chào! Đây là phụ đề xem trước.",
                        fontSize = settings.fontSizeSp.sp,
                        fontWeight = if (settings.isBoldFont) FontWeight.Bold else FontWeight.Normal,
                        color = runCatching { Color(android.graphics.Color.parseColor(settings.textColorHex)) }
                            .getOrDefault(Color.White),
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = if (settings.outlineEnabled) {
                                androidx.compose.ui.graphics.Shadow(
                                    color = runCatching { Color(android.graphics.Color.parseColor(settings.outlineColorHex)) }
                                        .getOrDefault(Color.Black),
                                    offset = Offset(0f, 0f),
                                    blurRadius = 3f,
                                )
                            } else null,
                        ),
                    )
                }
                Text(
                    text = "Bản xem trước thay đổi ngay theo màu sắc, cỡ chữ và độ mờ nền.",
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                SettingSwitchRow(
                    title = "Chữ phụ đề in đậm",
                    summary = "Bật hoặc tắt kiểu chữ đậm trên phụ đề nổi",
                    checked = settings.isBoldFont,
                    onCheckedChange = { enabled -> viewModel.update { it.copy(isBoldFont = enabled) } },
                )
                SettingSwitchRow(
                    title = "Viền chữ phụ đề",
                    summary = "Làm nổi chữ bằng viền tương phản",
                    checked = settings.outlineEnabled,
                    onCheckedChange = { enabled -> viewModel.update { it.copy(outlineEnabled = enabled) } },
                )
                if (settings.outlineEnabled) {
                    Text("Màu viền chữ", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    ColorPalette(
                        colors = listOf("#000000", "#FFFFFF"),
                        selected = settings.outlineColorHex,
                        onSelect = { color -> viewModel.update { it.copy(outlineColorHex = color) } },
                    )
                }
            }
            SettingSwitchRow(
                title = stringResource(R.string.settings_bilingual),
                summary = stringResource(
                    if (settings.bilingual) {
                        R.string.settings_bilingual_on
                    } else {
                        R.string.settings_bilingual_off
                    },
                ),
                checked = settings.bilingual,
                onCheckedChange = { c -> viewModel.update { it.copy(bilingual = c) } },
            )
            SettingSwitchRow(
                title = stringResource(R.string.settings_overlay_lock),
                summary = stringResource(R.string.settings_overlay_lock_hint),
                checked = settings.overlayPositionLocked,
                onCheckedChange = { locked -> viewModel.update { it.copy(overlayPositionLocked = locked) } },
            )
            TextButton(
                text = stringResource(R.string.settings_reset_appearance),
                onClick = viewModel::resetSubtitleAppearance,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        SmallTitle(
            text = stringResource(R.string.settings_voice),
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        SectionCard {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_voice_region_hint),
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
            SettingSwitchRow(
                title = stringResource(R.string.settings_play_voice),
                summary = stringResource(R.string.settings_play_voice_summary),
                checked = settings.playTranslatedAudio,
                onCheckedChange = { c -> viewModel.update { it.copy(playTranslatedAudio = c) } },
            )
            Text(
                text = stringResource(R.string.settings_voice_stream_summary),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                fontSize = 12.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Độ trễ âm thanh dịch: ${settings.translatedAudioDelayMs / 1000f} giây",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "Chỉ trì hoãn giọng dịch để có thêm thời gian nhận dạng và dịch; âm thanh gốc không bị trì hoãn.",
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    listOf(0, 1, 2, 3, 5, 10).forEach { seconds ->
                        val selected = settings.translatedAudioDelayMs == seconds * 1000
                        Text(
                            text = "${seconds}s",
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (selected) Booth.Accent.copy(alpha = 0.16f)
                                    else MiuixTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(10.dp),
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (selected) Booth.Accent
                                    else MiuixTheme.colorScheme.outline.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(10.dp),
                                )
                                .clickable {
                                    viewModel.update { it.copy(translatedAudioDelayMs = seconds * 1000) }
                                }
                                .padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Booth.Accent else MiuixTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
            SettingSwitchRow(
                title = stringResource(R.string.settings_duck_original),
                summary = stringResource(R.string.settings_duck_original_summary),
                checked = settings.duckOriginalAudio,
                onCheckedChange = { enabled -> viewModel.update { it.copy(duckOriginalAudio = enabled) } },
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = buildString {
                        append(
                            stringResource(
                                R.string.settings_voice_volume,
                                (settings.translatedVolume * 100).toInt(),
                            ),
                        )
                        if (settings.translatedVolume > 1f) {
                            append(stringResource(R.string.settings_voice_volume_boost))
                        }
                    },
                    fontSize = 13.sp,
                )
                Slider(
                    value = settings.translatedVolume.coerceIn(0f, 2f),
                    onValueChange = { v ->
                        viewModel.update { it.copy(translatedVolume = v.coerceIn(0f, 2f)) }
                    },
                    valueRange = 0f..2f,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.settings_voice_volume_hint),
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
        }

        SmallTitle(
            text = stringResource(R.string.settings_permissions),
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        SectionCard {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                TextButton(
                    text = stringResource(R.string.settings_open_overlay),
                    onClick = onOpenOverlayPermission,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.settings_permissions_hint),
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
        }

        SmallTitle(
            text = stringResource(R.string.settings_about),
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        SectionCard {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                Text(
                    text = "quocthanhjs",
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(R.string.settings_modified_from, "luoxiaoxin123"),
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                Text(
                    text = stringResource(R.string.settings_github) + ": https://github.com/luoxiaoxin123/live-translate",
                    color = Booth.Accent,
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/luoxiaoxin123/live-translate")))
                    },
                )
                TextButton(
                    text = stringResource(R.string.settings_github_open),
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/luoxiaoxin123/live-translate")))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun VoiceChoiceRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (selected) "◉" else "○",
            modifier = Modifier.padding(end = 12.dp),
            color = if (selected) Booth.Accent else MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Text(
            text = label,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) Booth.Accent else MiuixTheme.colorScheme.onSurface,
        )
    }
}



@Composable
private fun ColorPalette(
    colors: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        colors.chunked(5).forEach { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                rowColors.forEach { hex ->
                    val swatchColor = runCatching {
                        Color(android.graphics.Color.parseColor(hex))
                    }.getOrDefault(Color.Gray)
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .weight(1f)
                            .size(42.dp)
                            .background(swatchColor, CircleShape)
                            .border(
                                width = if (hex.equals(selected, ignoreCase = true)) 3.dp else 1.dp,
                                color = if (hex.equals(selected, ignoreCase = true)) Booth.Accent
                                    else MiuixTheme.colorScheme.outline.copy(alpha = 0.55f),
                                shape = CircleShape,
                            )
                            .clickable { onSelect(hex) },
                    )
                }
                repeat(5 - rowColors.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
