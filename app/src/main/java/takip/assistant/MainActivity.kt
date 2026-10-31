/*
 * Takip
 * Copyright (C) 2026 oustxr
 *
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package takip.assistant

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.security.SecureRandom
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.roundToInt

val LightPrimary = Color(0xFF2860E1)
val LightBackground = Color(0xFFF7F9FC)
val LightSurface = Color(0xFFFFFFFF)
val LightBorder = Color(0xFFE8EDF3)
val LightTextMain = Color(0xFF1C2024)
val LightTextSub = Color(0xFF64748B)

val DarkPrimary = Color(0xFF4B83FB)
val DarkBackground = Color(0xFF121214)
val DarkSurface = Color(0xFF1E1E24)
val DarkBorder = Color(0xFF2D2D38)
val DarkTextMain = Color(0xFFF1F5F9)
val DarkTextSub = Color(0xFF94A3B8)

val WaterColor = Color(0xFF0288D1)
val CompletedColor = Color(0xFF1C7F4A)
val SavingsColor = Color(0xFFD97706)
val DiaryColor = Color(0xFF7C3AED)
val CounterColor = Color(0xFF0D9488)

data class LanguagePack(
    val code: String,
    val name: String,
    val defaultCurrency: String,
    val strings: Map<String, String>
)

object LocalizationManager {
    val defaultFallbackStrings = mapOf(
        "title_routines" to "Yapılacaklar",
        "title_notes" to "Notlar",
        "title_diary" to "Günlük",
        "title_water" to "Su Takibi",
        "title_calendar" to "Takvim",
        "title_savings" to "Birikim",
        "title_reminders" to "Bildirimler",
        "delete_confirm_title" to "Silmek istediğine emin misin?",
        "delete_confirm_body" to "Bu işlem geri alınamaz.",
        "common_cancel" to "Vazgeç",
        "reminder_title" to "Hatırlatıcı",
        "reminder_message" to "Bildirim metni",
        "reminder_once" to "Tek tarih",
        "reminder_daily" to "Her gün",
        "reminder_start" to "Başlangıç tarihi",
        "reminder_end" to "Bitiş tarihi",
        "reminder_time" to "Saat",
        "reminder_add" to "Bildirim oluştur",
        "reminder_empty" to "Henüz hatırlatıcı yok.",
        "reminder_select_date" to "Tarih seç",
        "common_empty" to "Kayıt bulunmuyor.",
        "common_save" to "Kaydet",
        "common_delete" to "Sil",
        "common_add" to "+ Ekle",
        "common_today" to "Bugün",
        "common_search" to "Ara...",
        "common_title" to "Başlık",
        "common_reset" to "Sıfırla",
        "common_custom" to "Özel",
        "common_apply" to "Seçimi Uygula",
        "routine_new" to "Yeni Görev",
        "routine_timing" to "Zamanlama",
        "routine_timing_none" to "Süresiz",
        "routine_timing_single" to "Tek Gün",
        "routine_timing_range" to "Aralık",
        "routine_items" to "Görevler",
        "routine_add_item" to "+ Görev Ekle",
        "routine_completed" to "Tamamlandı",
        "routine_finish" to "Bitir",
        "routine_item_placeholder" to "Görev",
        "routine_days_left" to "gün kaldı",
        "routine_last_day" to "Bugün Son",
        "routine_expired" to "Süresi Doldu",
        "routine_ending_today" to "Bugün Bitiyor",
        "note_new" to "Yeni Not",
        "note_edit" to "Not",
        "note_content_placeholder" to "Metin...",
        "diary_records" to "Kayıtlar",
        "diary_lock" to "Kilitle",
        "diary_change_pin" to "PIN Değiştir",
        "diary_placeholder" to "Yeni kayıt yaz...",
        "diary_pin_title" to "PIN Kodunu Girin",
        "diary_pin_new" to "Yeni PIN Belirleyin (4-12 Hane)",
        "diary_pin_confirm" to "Yeni PIN'i Doğrulayın",
        "diary_pin_new_label" to "Yeni PIN (4-12 Hane)",
        "diary_pin_confirm_label" to "Yeni PIN Tekrar",
        "diary_pin_mismatch" to "PIN kodları eşleşmiyor veya 4-12 hane aralığında değil.",
        "diary_pin_update" to "Güncelle",
        "water_goal" to "Hedef",
        "water_set_goal" to "Hedef Belirle",
        "water_add_amount" to "Miktar Ekle",
        "water_amount_label" to "Miktar (ml)",
        "water_goal_label" to "Hedef (ml)",
        "cal_day_pt" to "Pt",
        "cal_day_sa" to "Sa",
        "cal_day_ca" to "Ça",
        "cal_day_pe" to "Pe",
        "cal_day_cu" to "Cu",
        "cal_day_ct" to "Ct",
        "cal_day_pz" to "Pz",
        "inspector_water" to "Su",
        "inspector_savings" to "Birikim",
        "inspector_routine" to "Yapılacaklar",
        "inspector_diary" to "Günlük",
        "inspector_diary_exists" to "Kayıt mevcut",
        "inspector_no_record" to "Kayıt yok",
        "savings_total" to "Toplam",
        "savings_goal_label" to "Hedef",
        "savings_settings" to "Ayarlar",
        "savings_currency_label" to "Sembol / Para Birimi",
        "savings_amount_label" to "Miktar",
        "savings_note_label" to "Açıklama",
        "savings_add_title" to "Kayıt Ekle",
        "lang_select_title" to "Dil Seçimi / Select Language",
        "lang_select_button" to "Seç / Confirm"
    )

    fun loadAvailableLanguages(context: Context): List<LanguagePack> {
        val list = mutableListOf<LanguagePack>()
        val files = context.assets.list("languages") ?: emptyArray()
        for (file in files.sorted()) {
            if (!file.endsWith(".json")) continue
            try {
                val jsonStr = context.assets.open("languages/$file").bufferedReader().use { it.readText() }
                val json = JSONObject(jsonStr)
                val code = json.getString("language_code")
                val name = json.getString("language_name")
                val currency = json.optString("default_currency", "₺")
                val stringsObj = json.getJSONObject("strings")
                val map = mutableMapOf<String, String>()
                val keys = stringsObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    map[key] = stringsObj.getString(key)
                }
                list.add(LanguagePack(code, name, currency, map))
            } catch (_: Exception) {
                // Ignore only the malformed language file; keep the remaining languages available.
            }
        }

        if (list.isEmpty()) {
            list.add(LanguagePack("tr", "Türkçe", "₺", defaultFallbackStrings))
        }
        return list
    }
}

data class RoutineItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isChecked: Boolean = false,
    val checkedDate: LocalDate? = null
)

enum class RoutineDateType { NONE, SINGLE, RANGE }

data class Routine(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dateType: RoutineDateType,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val isCompleted: Boolean = false,
    val completedDate: LocalDate? = null,
    val items: List<RoutineItem> = emptyList()
)

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val date: LocalDate = LocalDate.now()
)

data class DiaryEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate = LocalDate.now(),
    val cipherText: String,
    val iv: String
)

data class WaterLog(
    val id: String = UUID.randomUUID().toString(),
    val time: String,
    val amount: Int,
    val date: LocalDate = LocalDate.now()
)

data class SavingsEntry(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val date: LocalDate = LocalDate.now(),
    val note: String = ""
)

data class CounterEntry(
    val id: String = UUID.randomUUID().toString(),
    val amount: Long,
    val date: LocalDate = LocalDate.now(),
    val time: String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
)

data class CounterItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val entries: List<CounterEntry> = emptyList()
) {
    val total: Long get() = entries.sumOf { it.amount }
}

data class CounterGroup(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val items: List<CounterItem> = emptyList()
)


data class AppReminder(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val hour: Int,
    val minute: Int,
    val daily: Boolean,
    val enabled: Boolean = true
)

object StorageManager {
    private const val PREFS_NAME = "takip_assistant_storage"

    fun saveRoutines(context: Context, routines: List<Routine>) {
        val array = JSONArray()
        routines.forEach { r ->
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("title", r.title)
            obj.put("dateType", r.dateType.name)
            obj.put("startDate", r.startDate?.toString() ?: "")
            obj.put("endDate", r.endDate?.toString() ?: "")
            obj.put("isCompleted", r.isCompleted)
            obj.put("completedDate", r.completedDate?.toString() ?: "")
            val itemsArr = JSONArray()
            r.items.forEach { item ->
                val itemObj = JSONObject()
                itemObj.put("id", item.id)
                itemObj.put("text", item.text)
                itemObj.put("isChecked", item.isChecked)
                itemObj.put("checkedDate", item.checkedDate?.toString() ?: "")
                itemsArr.put(itemObj)
            }
            obj.put("items", itemsArr)
            array.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("routines_data", array.toString())
            .apply()
    }

    fun loadRoutines(context: Context): List<Routine> {
        val jsonStr = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("routines_data", null) ?: return emptyList()
        val list = mutableListOf<Routine>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val itemsArr = obj.optJSONArray("items") ?: JSONArray()
                val items = mutableListOf<RoutineItem>()
                for (j in 0 until itemsArr.length()) {
                    val itemObj = itemsArr.getJSONObject(j)
                    val cdStr = itemObj.optString("checkedDate", "")
                    items.add(
                        RoutineItem(
                            id = itemObj.getString("id"),
                            text = itemObj.getString("text"),
                            isChecked = itemObj.getBoolean("isChecked"),
                            checkedDate = if (cdStr.isNotEmpty()) LocalDate.parse(cdStr) else null
                        )
                    )
                }
                val sdStr = obj.optString("startDate", "")
                val edStr = obj.optString("endDate", "")
                val compStr = obj.optString("completedDate", "")
                list.add(
                    Routine(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        dateType = RoutineDateType.valueOf(obj.getString("dateType")),
                        startDate = if (sdStr.isNotEmpty()) LocalDate.parse(sdStr) else null,
                        endDate = if (edStr.isNotEmpty()) LocalDate.parse(edStr) else null,
                        isCompleted = obj.getBoolean("isCompleted"),
                        completedDate = if (compStr.isNotEmpty()) LocalDate.parse(compStr) else null,
                        items = items
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveNotes(context: Context, notes: List<Note>) {
        val array = JSONArray()
        notes.forEach { n ->
            val obj = JSONObject()
            obj.put("id", n.id)
            obj.put("title", n.title)
            obj.put("content", n.content)
            obj.put("date", n.date.toString())
            array.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("notes_data", array.toString())
            .apply()
    }

    fun loadNotes(context: Context): List<Note> {
        val jsonStr = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("notes_data", null) ?: return emptyList()
        val list = mutableListOf<Note>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Note(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        content = obj.getString("content"),
                        date = LocalDate.parse(obj.getString("date"))
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveDiaryEntries(context: Context, entries: List<DiaryEntry>) {
        val array = JSONArray()
        entries.forEach { e ->
            val obj = JSONObject()
            obj.put("id", e.id)
            obj.put("date", e.date.toString())
            obj.put("cipherText", e.cipherText)
            obj.put("iv", e.iv)
            array.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("diary_data", array.toString())
            .apply()
    }

    fun loadDiaryEntries(context: Context): List<DiaryEntry> {
        val jsonStr = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("diary_data", null) ?: return emptyList()
        val list = mutableListOf<DiaryEntry>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    DiaryEntry(
                        id = obj.getString("id"),
                        date = LocalDate.parse(obj.getString("date")),
                        cipherText = obj.getString("cipherText"),
                        iv = obj.getString("iv")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveWaterLogs(context: Context, logs: List<WaterLog>) {
        val array = JSONArray()
        logs.forEach { l ->
            val obj = JSONObject()
            obj.put("id", l.id)
            obj.put("time", l.time)
            obj.put("amount", l.amount)
            obj.put("date", l.date.toString())
            array.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("water_data", array.toString())
            .apply()
    }

    fun loadWaterLogs(context: Context): List<WaterLog> {
        val jsonStr = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("water_data", null) ?: return emptyList()
        val list = mutableListOf<WaterLog>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    WaterLog(
                        id = obj.getString("id"),
                        time = obj.getString("time"),
                        amount = obj.getInt("amount"),
                        date = LocalDate.parse(obj.getString("date"))
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveSavingsEntries(context: Context, entries: List<SavingsEntry>) {
        val array = JSONArray()
        entries.forEach { s ->
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("amount", s.amount)
            obj.put("date", s.date.toString())
            obj.put("note", s.note)
            array.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("savings_data", array.toString())
            .apply()
    }

    fun loadSavingsEntries(context: Context): List<SavingsEntry> {
        val jsonStr = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("savings_data", null) ?: return emptyList()
        val list = mutableListOf<SavingsEntry>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    SavingsEntry(
                        id = obj.getString("id"),
                        amount = obj.getDouble("amount"),
                        date = LocalDate.parse(obj.getString("date")),
                        note = obj.optString("note", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveCounterGroups(context: Context, groups: List<CounterGroup>) {
        val array = JSONArray()
        groups.forEach { group ->
            val groupJson = JSONObject().put("id", group.id).put("title", group.title)
            val items = JSONArray()
            group.items.forEach { item ->
                val itemJson = JSONObject().put("id", item.id).put("title", item.title)
                val entries = JSONArray()
                item.entries.forEach { entry ->
                    entries.put(JSONObject().put("id", entry.id).put("amount", entry.amount)
                        .put("date", entry.date.toString()).put("time", entry.time))
                }
                itemJson.put("entries", entries)
                items.put(itemJson)
            }
            groupJson.put("items", items)
            array.put(groupJson)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString("counter_groups_data", array.toString()).apply()
    }

    fun loadCounterGroups(context: Context): List<CounterGroup> {
        val raw = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("counter_groups_data", null) ?: return emptyList()
        return try {
            val result = mutableListOf<CounterGroup>()
            val groups = JSONArray(raw)
            for (i in 0 until groups.length()) {
                val g = groups.getJSONObject(i)
                val items = mutableListOf<CounterItem>()
                val itemArray = g.optJSONArray("items") ?: JSONArray()
                for (j in 0 until itemArray.length()) {
                    val item = itemArray.getJSONObject(j)
                    val entries = mutableListOf<CounterEntry>()
                    val entryArray = item.optJSONArray("entries") ?: JSONArray()
                    for (k in 0 until entryArray.length()) {
                        val entry = entryArray.getJSONObject(k)
                        entries.add(CounterEntry(
                            id = entry.optString("id", UUID.randomUUID().toString()),
                            amount = entry.optLong("amount", 0L),
                            date = LocalDate.parse(entry.optString("date", LocalDate.now().toString())),
                            time = entry.optString("time", "")
                        ))
                    }
                    items.add(CounterItem(id = item.optString("id", UUID.randomUUID().toString()),
                        title = item.optString("title", ""), entries = entries))
                }
                result.add(CounterGroup(id = g.optString("id", UUID.randomUUID().toString()),
                    title = g.optString("title", ""), items = items))
            }
            result
        } catch (_: Exception) { emptyList() }
    }
}
object BackupManager {
    private const val FORMAT = "takip_assistant_backup"
    private const val PREFS = "takip_assistant_storage"

    fun export(context: Context): String {
        val allPrefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).all
        val values = JSONObject()
        allPrefs.forEach { (key, value) ->
            val encoded = JSONObject()
            when (value) {
                is String -> encoded.put("type", "string").put("value", value)
                is Boolean -> encoded.put("type", "boolean").put("value", value)
                is Int -> encoded.put("type", "int").put("value", value)
                is Long -> encoded.put("type", "long").put("value", value)
                is Float -> encoded.put("type", "float").put("value", value.toDouble())
                is Set<*> -> encoded.put("type", "string_set").put("value", JSONArray(value.map { it.toString() }))
            }
            if (encoded.has("type")) values.put(key, encoded)
        }
        return JSONObject().put("format", FORMAT).put("version", 1)
            .put("exported_at", java.time.Instant.now().toString())
            .put("preferences", values).toString(2)
    }

    fun import(context: Context, raw: String) {
        val root = JSONObject(raw)
        require(root.optString("format") == FORMAT) { "invalid backup format" }
        require(root.optInt("version", 0) == 1) { "unsupported backup version" }
        val values = root.getJSONObject("preferences")
        val editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear()
        val keys = values.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val item = values.getJSONObject(key)
            when (item.getString("type")) {
                "string" -> editor.putString(key, item.optString("value"))
                "boolean" -> editor.putBoolean(key, item.getBoolean("value"))
                "int" -> editor.putInt(key, item.getInt("value"))
                "long" -> editor.putLong(key, item.getLong("value"))
                "float" -> editor.putFloat(key, item.getDouble("value").toFloat())
                "string_set" -> {
                    val array = item.getJSONArray("value")
                    val set = (0 until array.length()).map { array.getString(it) }.toSet()
                    editor.putStringSet(key, set)
                }
            }
        }
        check(editor.commit()) { "could not restore backup" }
    }
}



object CryptoHelper {
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val GCM_TAG_LENGTH = 128

    fun deriveKey(pin: String, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(pin.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    fun encrypt(plainText: String, secretKey: SecretKeySpec): Pair<String, String> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Pair(
            Base64.encodeToString(cipherBytes, Base64.NO_WRAP),
            Base64.encodeToString(iv, Base64.NO_WRAP)
        )
    }

    fun decrypt(cipherText: String, iv: String, secretKey: SecretKeySpec): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val cipherBytes = Base64.decode(cipherText, Base64.NO_WRAP)
        val ivBytes = Base64.decode(iv, Base64.NO_WRAP)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, ivBytes))
        return String(cipher.doFinal(cipherBytes), Charsets.UTF_8)
    }
}

class MainActivity : ComponentActivity() {
    override fun onResume() {
        super.onResume()
        ReminderScheduler.rescheduleAll(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPref = remember { getSharedPreferences("takip_assistant_storage", Context.MODE_PRIVATE) }
            var isDarkTheme by remember {
                mutableStateOf(sharedPref.getBoolean("is_dark_theme", false))
            }

            val animBg by animateColorAsState(
                targetValue = if (isDarkTheme) DarkBackground else LightBackground,
                animationSpec = tween(350),
                label = ""
            )
            val animSurface by animateColorAsState(
                targetValue = if (isDarkTheme) DarkSurface else LightSurface,
                animationSpec = tween(350),
                label = ""
            )
            val animOutline by animateColorAsState(
                targetValue = if (isDarkTheme) DarkBorder else LightBorder,
                animationSpec = tween(350),
                label = ""
            )
            val animTextMain by animateColorAsState(
                targetValue = if (isDarkTheme) DarkTextMain else LightTextMain,
                animationSpec = tween(350),
                label = ""
            )
            val animPrimary by animateColorAsState(
                targetValue = if (isDarkTheme) DarkPrimary else LightPrimary,
                animationSpec = tween(350),
                label = ""
            )

            val colorScheme = lightColorScheme(
                primary = animPrimary,
                background = animBg,
                surface = animSurface,
                onPrimary = Color.White,
                onBackground = animTextMain,
                onSurface = animTextMain,
                outline = animOutline
            )

            MaterialTheme(colorScheme = colorScheme) {
                MainAppScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = {
                        isDarkTheme = !isDarkTheme
                        sharedPref.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("takip_assistant_storage", Context.MODE_PRIVATE) }

    val availableLanguages = remember { LocalizationManager.loadAvailableLanguages(context) }
    var selectedLanguageCode by remember { mutableStateOf(sharedPrefs.getString("selected_language_code", null)) }
    var isLanguageWheelOpen by remember { mutableStateOf(false) }

    val activeLanguagePack = remember(selectedLanguageCode, availableLanguages) {
        availableLanguages.find { it.code == selectedLanguageCode }
            ?: availableLanguages.find { it.code == "en" }
            ?: availableLanguages.firstOrNull()
            ?: LanguagePack("en", "English", "$", LocalizationManager.defaultFallbackStrings)
    }

    fun tr(key: String): String {
        return activeLanguagePack.strings[key] ?: LocalizationManager.defaultFallbackStrings[key] ?: key
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var pendingDeleteAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val reminders = remember { mutableStateListOf<AppReminder>().apply { addAll(ReminderStore.load(context)) } }
    val counterGroups = remember { mutableStateListOf<CounterGroup>().apply { addAll(StorageManager.loadCounterGroups(context)) } }
    var isNewCounterGroupSheetOpen by remember { mutableStateOf(false) }
    var itemGroupForNewItem by remember { mutableStateOf<String?>(null) }
    var activeCounterTarget by remember { mutableStateOf<Pair<String, String>?>(null) }
    var isBackupDialogOpen by remember { mutableStateOf(false) }
    var pendingBackupJson by remember { mutableStateOf("") }
    val exportBackupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) try { context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(pendingBackupJson) } ?: error("destination") ; Toast.makeText(context, tr("backup_exported"), Toast.LENGTH_LONG).show() } catch (_: Exception) { Toast.makeText(context, tr("backup_error"), Toast.LENGTH_LONG).show() }
    }
    val importBackupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) try { val raw = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: error("empty backup"); BackupManager.import(context, raw); Toast.makeText(context, tr("backup_imported"), Toast.LENGTH_LONG).show(); (context as? Activity)?.recreate() } catch (_: Exception) { Toast.makeText(context, tr("backup_error"), Toast.LENGTH_LONG).show() }
    }


    val routines = remember { mutableStateListOf<Routine>().apply { addAll(StorageManager.loadRoutines(context)) } }
    val notes = remember { mutableStateListOf<Note>().apply { addAll(StorageManager.loadNotes(context)) } }
    val diaryEntries = remember { mutableStateListOf<DiaryEntry>().apply { addAll(StorageManager.loadDiaryEntries(context)) } }
    var diaryKey by remember { mutableStateOf<SecretKeySpec?>(null) }
    var diarySaltBase64 by remember { mutableStateOf(sharedPrefs.getString("diary_salt", null)) }
    var diaryPinHash by remember { mutableStateOf(sharedPrefs.getString("diary_pinhash", null)) }

    val waterLogs = remember { mutableStateListOf<WaterLog>().apply { addAll(StorageManager.loadWaterLogs(context)) } }
    var waterGoal by remember { mutableIntStateOf(sharedPrefs.getInt("water_goal", 2500)) }

    val savingsEntries = remember { mutableStateListOf<SavingsEntry>().apply { addAll(StorageManager.loadSavingsEntries(context)) } }
    var savingsGoal by remember { mutableDoubleStateOf(sharedPrefs.getString("savings_goal", "0.0")?.toDoubleOrNull() ?: 0.0) }
    var savingsCurrency by remember { mutableStateOf(sharedPrefs.getString("savings_currency", activeLanguagePack.defaultCurrency) ?: activeLanguagePack.defaultCurrency) }

    var activeRoutineDetail by remember { mutableStateOf<Routine?>(null) }
    var isNewRoutineSheetOpen by remember { mutableStateOf(false) }
    var activeNoteEditor by remember { mutableStateOf<Note?>(null) }
    var isNewNoteSheetOpen by remember { mutableStateOf(false) }
    var isCustomWaterSheetOpen by remember { mutableStateOf(false) }
    var isWaterGoalSheetOpen by remember { mutableStateOf(false) }
    var isSavingsSettingsSheetOpen by remember { mutableStateOf(false) }
    var isAddSavingsSheetOpen by remember { mutableStateOf(false) }
    var isChangePinSheetOpen by remember { mutableStateOf(false) }
    var selectedCalendarDay by remember { mutableStateOf<LocalDate?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            HeaderBar(
                selectedTab = selectedTab,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onLanguageClick = { isLanguageWheelOpen = true },
                onBackupClick = { isBackupDialogOpen = true },
                tr = ::tr
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> RoutinesScreen(
                        routines = routines,
                        onRoutineClick = { activeRoutineDetail = it },
                        tr = ::tr
                    )
                    1 -> NotesScreen(
                        notes = notes,
                        onNoteClick = { activeNoteEditor = it },
                        tr = ::tr
                    )
                    2 -> DiaryScreen(
                        entries = diaryEntries,
                        diaryKey = diaryKey,
                        hasPinConfigured = diaryPinHash != null,
                        onConfigurePin = { pin ->
                            val salt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
                            val saltStr = Base64.encodeToString(salt, Base64.NO_WRAP)
                            val key = CryptoHelper.deriveKey(pin, salt)
                            val pinHash = Base64.encodeToString(key.encoded, Base64.NO_WRAP)
                            sharedPrefs.edit()
                                .putString("diary_salt", saltStr)
                                .putString("diary_pinhash", pinHash)
                                .apply()
                            diarySaltBase64 = saltStr
                            diaryPinHash = pinHash
                            diaryKey = key
                        },
                        onUnlock = { pin ->
                            val salt = Base64.decode(diarySaltBase64, Base64.NO_WRAP)
                            val key = CryptoHelper.deriveKey(pin, salt)
                            val computedHash = Base64.encodeToString(key.encoded, Base64.NO_WRAP)
                            if (computedHash == diaryPinHash) {
                                diaryKey = key
                                true
                            } else {
                                false
                            }
                        },
                        onLock = { diaryKey = null },
                        onOpenChangePin = { isChangePinSheetOpen = true },
                        onAddEntry = { text ->
                            diaryKey?.let { key ->
                                val (ct, iv) = CryptoHelper.encrypt(text, key)
                                diaryEntries.add(0, DiaryEntry(cipherText = ct, iv = iv))
                                StorageManager.saveDiaryEntries(context, diaryEntries)
                            }
                        },
                        onDeleteEntry = { entry ->
                            pendingDeleteAction = {
                                diaryEntries.remove(entry)
                                StorageManager.saveDiaryEntries(context, diaryEntries)
                            }
                        },
                        tr = ::tr
                    )
                    3 -> WaterScreen(
                        waterLogs = waterLogs,
                        goal = waterGoal,
                        onAddWater = { amount ->
                            val now = LocalTime.now()
                            val timeStr = String.format(Locale.getDefault(), "%02d:%02d", now.hour, now.minute)
                            waterLogs.add(0, WaterLog(time = timeStr, amount = amount, date = LocalDate.now()))
                            StorageManager.saveWaterLogs(context, waterLogs)
                        },
                        onOpenCustomWater = { isCustomWaterSheetOpen = true },
                        onOpenGoalSetting = { isWaterGoalSheetOpen = true },
                        onDeleteLog = { log ->
                            pendingDeleteAction = {
                                waterLogs.remove(log)
                                StorageManager.saveWaterLogs(context, waterLogs)
                            }
                        },
                        onResetToday = {
                            pendingDeleteAction = {
                                val today = LocalDate.now()
                                waterLogs.removeAll { it.date == today }
                                StorageManager.saveWaterLogs(context, waterLogs)
                            }
                        },
                        tr = ::tr
                    )
                    4 -> CalendarScreen(
                        routines = routines,
                        waterLogs = waterLogs,
                        savingsEntries = savingsEntries,
                        diaryEntries = diaryEntries,
                        onDayClick = { selectedCalendarDay = it },
                        tr = ::tr
                    )
                    5 -> CounterScreen(
                        groups = counterGroups,
                        onAddGroup = { isNewCounterGroupSheetOpen = true },
                        onAddItem = { itemGroupForNewItem = it },
                        onItemClick = { groupId, itemId -> activeCounterTarget = groupId to itemId },
                        onDeleteItem = { groupId, itemId ->
                            pendingDeleteAction = {
                                val index = counterGroups.indexOfFirst { it.id == groupId }
                                if (index >= 0) { counterGroups[index] = counterGroups[index].copy(items = counterGroups[index].items.filterNot { it.id == itemId }); StorageManager.saveCounterGroups(context, counterGroups) }
                            }
                        },
                        onDeleteGroup = { groupId -> pendingDeleteAction = { counterGroups.removeAll { it.id == groupId }; StorageManager.saveCounterGroups(context, counterGroups) } },
                        tr = ::tr
                    )
                    6 -> SavingsScreen(
                        entries = savingsEntries,
                        goal = savingsGoal,
                        currency = savingsCurrency,
                        onAddClick = { isAddSavingsSheetOpen = true },
                        onSettingsClick = { isSavingsSettingsSheetOpen = true },
                        onDeleteEntry = { entry ->
                            pendingDeleteAction = {
                                savingsEntries.remove(entry)
                                StorageManager.saveSavingsEntries(context, savingsEntries)
                            }
                        },
                        tr = ::tr
                    )
                    7 -> ReminderScreen(
                        reminders = reminders,
                        onAddReminder = { reminder ->
                            if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                (context as? Activity)?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 4102) }
                            }
                            reminders.add(0, reminder)
                            ReminderStore.save(context, reminders)
                            ReminderScheduler.schedule(context, reminder)
                            ReminderScheduler.requestExactAlarmAccess(context)
                        },
                        onDeleteReminder = { reminder ->
                            pendingDeleteAction = {
                                reminders.removeAll { it.id == reminder.id }
                                ReminderStore.save(context, reminders)
                                ReminderScheduler.cancel(context, reminder.id)
                            }
                        },
                        tr = ::tr
                    )
                }

                if (selectedTab == 0 || selectedTab == 1) {
                    FloatingActionButton(
                        onClick = {
                            if (selectedTab == 0) isNewRoutineSheetOpen = true
                            else isNewNoteSheetOpen = true
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(20.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }

            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                modifier = Modifier.navigationBarsPadding()
            )
        }

        if (isNewCounterGroupSheetOpen) CounterNameBottomSheet(title = tr("counter_new_group"), label = tr("counter_group_name"), onDismiss = { isNewCounterGroupSheetOpen = false }, onSave = { name -> counterGroups.add(0, CounterGroup(title = name)); StorageManager.saveCounterGroups(context, counterGroups); isNewCounterGroupSheetOpen = false }, tr = ::tr)
        itemGroupForNewItem?.let { groupId -> CounterNameBottomSheet(title = tr("counter_new_item"), label = tr("counter_item_name"), onDismiss = { itemGroupForNewItem = null }, onSave = { name -> val i = counterGroups.indexOfFirst { it.id == groupId }; if (i >= 0) { counterGroups[i] = counterGroups[i].copy(items = counterGroups[i].items + CounterItem(title = name)); StorageManager.saveCounterGroups(context, counterGroups) }; itemGroupForNewItem = null }, tr = ::tr) }
        activeCounterTarget?.let { (groupId, itemId) -> val group = counterGroups.firstOrNull { it.id == groupId }; val item = group?.items?.firstOrNull { it.id == itemId }; if (item != null) CounterAmountBottomSheet(item = item, onDismiss = { activeCounterTarget = null }, onDeleteEntry = { entryId -> val i = counterGroups.indexOfFirst { it.id == groupId }; if (i >= 0) { val g = counterGroups[i]; counterGroups[i] = g.copy(items = g.items.map { if (it.id == itemId) it.copy(entries = it.entries.filterNot { e -> e.id == entryId }) else it }); StorageManager.saveCounterGroups(context, counterGroups) } }, onAdd = { amount -> val i = counterGroups.indexOfFirst { it.id == groupId }; if (i >= 0) { val g = counterGroups[i]; counterGroups[i] = g.copy(items = g.items.map { if (it.id == itemId) it.copy(entries = listOf(CounterEntry(amount = amount)) + it.entries) else it }); StorageManager.saveCounterGroups(context, counterGroups) }; activeCounterTarget = null }, tr = ::tr) }
        if (isBackupDialogOpen) AlertDialog(onDismissRequest = { isBackupDialogOpen = false }, title = { Text(tr("backup_title"), fontWeight = FontWeight.Bold) }, text = { Text(tr("backup_description")) }, confirmButton = { TextButton(onClick = { pendingBackupJson = BackupManager.export(context); isBackupDialogOpen = false; exportBackupLauncher.launch("takip-backup.json") }) { Text(tr("backup_export")) } }, dismissButton = { Row { TextButton(onClick = { isBackupDialogOpen = false; importBackupLauncher.launch(arrayOf("application/json", "text/*")) }) { Text(tr("backup_import")) }; TextButton(onClick = { isBackupDialogOpen = false }) { Text(tr("common_cancel")) } } })

        activeRoutineDetail?.let { routine ->
            RoutineDetailBottomSheet(
                routine = routine,
                onDismiss = { activeRoutineDetail = null },
                onToggleItem = { itemId ->
                    val index = routines.indexOfFirst { it.id == routine.id }
                    if (index != -1) {
                        val currentRoutine = routines[index]
                        val updatedItems = currentRoutine.items.map { item ->
                            if (item.id == itemId) {
                                val nextChecked = !item.isChecked
                                item.copy(
                                    isChecked = nextChecked,
                                    checkedDate = if (nextChecked) LocalDate.now() else null
                                )
                            } else item
                        }
                        routines[index] = currentRoutine.copy(items = updatedItems)
                        activeRoutineDetail = routines[index]
                        StorageManager.saveRoutines(context, routines)
                    }
                },
                onCompleteRoutine = {
                    val index = routines.indexOfFirst { it.id == routine.id }
                    if (index != -1) {
                        routines[index] = routines[index].copy(
                            isCompleted = true,
                            completedDate = LocalDate.now()
                        )
                        activeRoutineDetail = routines[index]
                        StorageManager.saveRoutines(context, routines)
                    }
                },
                onDeleteRoutine = {
                    pendingDeleteAction = {
                        routines.removeAll { it.id == routine.id }
                        activeRoutineDetail = null
                        StorageManager.saveRoutines(context, routines)
                    }
                },
                tr = ::tr
            )
        }

        if (isNewRoutineSheetOpen) {
            NewRoutineBottomSheet(
                onDismiss = { isNewRoutineSheetOpen = false },
                onSave = { newRoutine ->
                    routines.add(0, newRoutine)
                    StorageManager.saveRoutines(context, routines)
                    isNewRoutineSheetOpen = false
                },
                tr = ::tr
            )
        }

        if (isNewNoteSheetOpen || activeNoteEditor != null) {
            NoteEditorBottomSheet(
                note = activeNoteEditor,
                onDismiss = {
                    isNewNoteSheetOpen = false
                    activeNoteEditor = null
                },
                onSave = { title, content ->
                    if (activeNoteEditor != null) {
                        val index = notes.indexOfFirst { it.id == activeNoteEditor!!.id }
                        if (index != -1) {
                            notes[index] = notes[index].copy(title = title, content = content, date = LocalDate.now())
                        }
                    } else {
                        notes.add(0, Note(title = title, content = content))
                    }
                    StorageManager.saveNotes(context, notes)
                    isNewNoteSheetOpen = false
                    activeNoteEditor = null
                },
                onDelete = {
                    pendingDeleteAction = {
                        activeNoteEditor?.let { notes.remove(it) }
                        StorageManager.saveNotes(context, notes)
                        activeNoteEditor = null
                        isNewNoteSheetOpen = false
                    }
                },
                tr = ::tr
            )
        }

        if (isCustomWaterSheetOpen) {
            CustomWaterBottomSheet(
                onDismiss = { isCustomWaterSheetOpen = false },
                onAdd = { amount ->
                    val now = LocalTime.now()
                    val timeStr = String.format(Locale.getDefault(), "%02d:%02d", now.hour, now.minute)
                    waterLogs.add(0, WaterLog(time = timeStr, amount = amount, date = LocalDate.now()))
                    StorageManager.saveWaterLogs(context, waterLogs)
                    isCustomWaterSheetOpen = false
                },
                tr = ::tr
            )
        }

        if (isWaterGoalSheetOpen) {
            WaterGoalBottomSheet(
                currentGoal = waterGoal,
                onDismiss = { isWaterGoalSheetOpen = false },
                onSave = {
                    waterGoal = it
                    sharedPrefs.edit().putInt("water_goal", it).apply()
                    isWaterGoalSheetOpen = false
                },
                tr = ::tr
            )
        }

        if (isAddSavingsSheetOpen) {
            AddSavingsBottomSheet(
                currency = savingsCurrency,
                onDismiss = { isAddSavingsSheetOpen = false },
                onSave = { amount, note, date ->
                    savingsEntries.add(0, SavingsEntry(amount = amount, note = note, date = date))
                    StorageManager.saveSavingsEntries(context, savingsEntries)
                    isAddSavingsSheetOpen = false
                },
                tr = ::tr
            )
        }

        if (isSavingsSettingsSheetOpen) {
            SavingsSettingsBottomSheet(
                currentCurrency = savingsCurrency,
                currentGoal = savingsGoal,
                onDismiss = { isSavingsSettingsSheetOpen = false },
                onSave = { newCurrency, newGoal ->
                    savingsCurrency = newCurrency
                    savingsGoal = newGoal
                    sharedPrefs.edit()
                        .putString("savings_currency", newCurrency)
                        .putString("savings_goal", newGoal.toString())
                        .apply()
                    isSavingsSettingsSheetOpen = false
                },
                tr = ::tr
            )
        }

        if (isChangePinSheetOpen) {
            ChangePinBottomSheet(
                onDismiss = { isChangePinSheetOpen = false },
                onChangePin = { newPin ->
                    diaryKey?.let { oldKey ->
                        val decryptedList = diaryEntries.map { entry ->
                            val text = try {
                                CryptoHelper.decrypt(entry.cipherText, entry.iv, oldKey)
                            } catch (_: Exception) { "" }
                            Pair(entry.date, text)
                        }
                        val newSalt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
                        val newSaltStr = Base64.encodeToString(newSalt, Base64.NO_WRAP)
                        val newKey = CryptoHelper.deriveKey(newPin, newSalt)
                        val newPinHash = Base64.encodeToString(newKey.encoded, Base64.NO_WRAP)

                        val reEncryptedEntries = decryptedList.map { (date, text) ->
                            val (ct, iv) = CryptoHelper.encrypt(text, newKey)
                            DiaryEntry(date = date, cipherText = ct, iv = iv)
                        }

                        diaryEntries.clear()
                        diaryEntries.addAll(reEncryptedEntries)
                        StorageManager.saveDiaryEntries(context, diaryEntries)

                        sharedPrefs.edit()
                            .putString("diary_salt", newSaltStr)
                            .putString("diary_pinhash", newPinHash)
                            .apply()

                        diarySaltBase64 = newSaltStr
                        diaryPinHash = newPinHash
                        diaryKey = newKey
                    }
                    isChangePinSheetOpen = false
                },
                tr = ::tr
            )
        }

        selectedCalendarDay?.let { day ->
            DayInspectorBottomSheet(
                date = day,
                routines = routines,
                waterLogs = waterLogs,
                savingsEntries = savingsEntries,
                diaryEntries = diaryEntries,
                currency = savingsCurrency,
                onDismiss = { selectedCalendarDay = null },
                tr = ::tr
            )
        }

        pendingDeleteAction?.let { deleteAction ->
            AlertDialog(
                onDismissRequest = { pendingDeleteAction = null },
                title = { Text(tr("delete_confirm_title"), fontWeight = FontWeight.Bold) },
                text = { Text(tr("delete_confirm_body")) },
                confirmButton = {
                    TextButton(onClick = {
                        pendingDeleteAction = null
                        deleteAction()
                    }) { Text(tr("common_delete"), color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { pendingDeleteAction = null }) { Text(tr("common_cancel")) }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        if (isLanguageWheelOpen || selectedLanguageCode == null) {
            WheelLanguageModalBottomSheet(
                languages = availableLanguages,
                currentCode = activeLanguagePack.code,
                isMandatory = selectedLanguageCode == null,
                onConfirmLanguage = { pack ->
                    selectedLanguageCode = pack.code
                    sharedPrefs.edit().putString("selected_language_code", pack.code).apply()
                    if (!sharedPrefs.contains("savings_currency")) {
                        savingsCurrency = pack.defaultCurrency
                        sharedPrefs.edit().putString("savings_currency", pack.defaultCurrency).apply()
                    }
                    isLanguageWheelOpen = false
                },
                onDismiss = {
                    if (selectedLanguageCode != null) {
                        isLanguageWheelOpen = false
                    }
                },
                tr = ::tr
            )
        }
    }
}

@Composable
fun HeaderBar(
    selectedTab: Int,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLanguageClick: () -> Unit,
    onBackupClick: () -> Unit,
    tr: (String) -> String
) {
    val titles = listOf(
        tr("title_routines"),
        tr("title_notes"),
        tr("title_diary"),
        tr("title_water"),
        tr("title_calendar"),
        tr("title_counters"),
        tr("title_savings"),
        tr("title_reminders")
    )
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("d MMMM EEEE", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = today.format(formatter),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = titles[selectedTab],
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onLanguageClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = onBackupClick, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))) {
                Icon(imageVector = Icons.Default.FileDownload, contentDescription = tr("backup_title"), tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
            }

            IconButton(
                onClick = onThemeToggle,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Icons.Default.Checklist,
        Icons.Default.Description,
        Icons.Default.Lock,
        Icons.Default.WaterDrop,
        Icons.Default.DateRange,
        Icons.Default.AddCircle,
        Icons.Default.Savings,
        Icons.Default.Notifications
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, icon ->
                val isSelected = selectedTab == index
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1.0f,
                    animationSpec = tween(250),
                    label = ""
                )
                val translateY by animateFloatAsState(
                    targetValue = if (isSelected) -2f else 0f,
                    animationSpec = tween(250),
                    label = ""
                )
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    animationSpec = tween(200),
                    label = ""
                )
                val dotColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    animationSpec = tween(200),
                    label = ""
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelect(index) }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier
                            .size(21.dp)
                            .offset(y = translateY.dp)
                            .scale(scale)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }
        }
    }
}

@Composable
fun RoutinesScreen(
    routines: List<Routine>,
    onRoutineClick: (Routine) -> Unit,
    tr: (String) -> String
) {
    if (routines.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = tr("common_empty"), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }
            items(routines, key = { it.id }) { routine ->
                val checkedCount = routine.items.count { it.isChecked }
                val totalCount = routine.items.size

                val badgeText = when {
                    routine.isCompleted -> tr("routine_completed")
                    routine.dateType == RoutineDateType.SINGLE && routine.startDate != null -> {
                        val diff = ChronoUnit.DAYS.between(LocalDate.now(), routine.startDate)
                        if (diff == 0L) tr("routine_last_day") else if (diff < 0) tr("routine_expired") else "$diff ${tr("routine_days_left")}"
                    }
                    routine.dateType == RoutineDateType.RANGE && routine.endDate != null -> {
                        val diff = ChronoUnit.DAYS.between(LocalDate.now(), routine.endDate)
                        if (diff == 0L) tr("routine_ending_today") else if (diff < 0) tr("routine_expired") else "$diff ${tr("routine_days_left")}"
                    }
                    else -> tr("routine_timing_none")
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onRoutineClick(routine) },
                    color = if (routine.isCompleted) CompletedColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (routine.isCompleted) CompletedColor.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = routine.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    color = if (routine.isCompleted) CompletedColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (routine.isCompleted) CompletedColor else MaterialTheme.colorScheme.primary
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "$checkedCount / $totalCount",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailBottomSheet(
    routine: Routine,
    onDismiss: () -> Unit,
    onToggleItem: (String) -> Unit,
    onCompleteRoutine: () -> Unit,
    onDeleteRoutine: () -> Unit,
    tr: (String) -> String
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = routine.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDeleteRoutine) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                routine.items.forEach { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !routine.isCompleted) { onToggleItem(item.id) },
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = { if (!routine.isCompleted) onToggleItem(item.id) },
                                enabled = !routine.isCompleted,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            )
                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (item.isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f) else MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!routine.isCompleted) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDeleteRoutine,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                    ) {
                        Text(tr("common_delete"), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onCompleteRoutine,
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(tr("routine_finish"), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = CompletedColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CompletedColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tr("routine_completed"),
                                color = CompletedColor,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Button(
                        onClick = onDeleteRoutine,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                    ) {
                        Text(tr("common_delete"), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRoutineBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Routine) -> Unit,
    tr: (String) -> String
) {
    var title by remember { mutableStateOf("") }
    var dateType by remember { mutableStateOf(RoutineDateType.NONE) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var isDatePickerOpen by remember { mutableStateOf(false) }

    val items = remember { mutableStateListOf("", "") }
    var pendingItemRemoval by remember { mutableStateOf<Int?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = tr("routine_new"),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(tr("common_title")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(tr("routine_timing"), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RoutineDateType.values().forEach { type ->
                    val isSelected = dateType == type
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                dateType = type
                                if (type != RoutineDateType.NONE) isDatePickerOpen = true
                            },
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = when (type) {
                                    RoutineDateType.NONE -> tr("routine_timing_none")
                                    RoutineDateType.SINGLE -> tr("routine_timing_single")
                                    RoutineDateType.RANGE -> tr("routine_timing_range")
                                },
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            if (dateType != RoutineDateType.NONE && startDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isDatePickerOpen = true },
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = if (dateType == RoutineDateType.SINGLE) {
                            startDate!!.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))
                        } else {
                            "${startDate!!.format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))} - ${endDate?.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())) ?: ""}"
                        },
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(tr("routine_items"), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            items.forEachIndexed { index, itemText ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = itemText,
                        onValueChange = { items[index] = it },
                        placeholder = { Text("${tr("routine_item_placeholder")} ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    if (items.size > 1) {
                        IconButton(onClick = { pendingItemRemoval = index }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Button(
                onClick = { items.add("") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(tr("routine_add_item"), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val validItems = items.filter { it.isNotBlank() }.map { RoutineItem(text = it) }
                        if (validItems.isNotEmpty()) {
                            onSave(
                                Routine(
                                    title = title,
                                    dateType = dateType,
                                    startDate = startDate,
                                    endDate = endDate,
                                    items = validItems
                                )
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(tr("common_save"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    pendingItemRemoval?.let { index ->
        AlertDialog(
            onDismissRequest = { pendingItemRemoval = null },
            title = { Text(tr("delete_confirm_title"), fontWeight = FontWeight.Bold) },
            text = { Text(tr("delete_confirm_body")) },
            confirmButton = { TextButton(onClick = { if (index in items.indices && items.size > 1) items.removeAt(index); pendingItemRemoval = null }) { Text(tr("common_delete"), color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { pendingItemRemoval = null }) { Text(tr("common_cancel")) } }
        )
    }

    if (isDatePickerOpen) {
        CustomDatePickerModal(
            isRange = dateType == RoutineDateType.RANGE,
            onDismiss = { isDatePickerOpen = false },
            onDateSelected = { start, end ->
                startDate = start
                endDate = end
                isDatePickerOpen = false
            },
            tr = tr
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerModal(
    isRange: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate, LocalDate?) -> Unit,
    tr: (String) -> String
) {
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { displayedMonth = displayedMonth.minusMonths(1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
                Text(
                    text = displayedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { displayedMonth = displayedMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val daysOfWeek = listOf(
                tr("cal_day_pt"),
                tr("cal_day_sa"),
                tr("cal_day_ca"),
                tr("cal_day_pe"),
                tr("cal_day_cu"),
                tr("cal_day_ct"),
                tr("cal_day_pz")
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                daysOfWeek.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val firstDayOfMonth = displayedMonth.atDay(1).dayOfWeek.value
            val totalDays = displayedMonth.lengthOfMonth()
            val gridCells = (1 until firstDayOfMonth).map { null } + (1..totalDays).map { displayedMonth.atDay(it) }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(250.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(gridCells) { day ->
                    if (day != null) {
                        val isStart = selectedStartDate == day
                        val isEnd = selectedEndDate == day
                        val inRange = isRange && selectedStartDate != null && selectedEndDate != null && day.isAfter(selectedStartDate) && day.isBefore(selectedEndDate)

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isStart || isEnd -> MaterialTheme.colorScheme.primary
                                        inRange -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable {
                                    if (!isRange) {
                                        selectedStartDate = day
                                    } else {
                                        if (selectedStartDate == null || (selectedStartDate != null && selectedEndDate != null)) {
                                            selectedStartDate = day
                                            selectedEndDate = null
                                        } else if (day.isBefore(selectedStartDate)) {
                                            selectedStartDate = day
                                        } else {
                                            selectedEndDate = day
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isStart || isEnd) FontWeight.Bold else FontWeight.Normal,
                                color = if (isStart || isEnd) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(38.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    selectedStartDate?.let { start ->
                        onDateSelected(start, selectedEndDate)
                    }
                },
                enabled = selectedStartDate != null && (!isRange || selectedEndDate != null),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(tr("common_apply"), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun NotesScreen(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    tr: (String) -> String
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredNotes = notes.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(tr("common_search")) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredNotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(tr("common_empty"), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNotes, key = { it.id }) { note ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onNoteClick(note) },
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = note.date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorBottomSheet(
    note: Note?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    onDelete: () -> Unit,
    tr: (String) -> String
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (note == null) tr("note_new") else tr("note_edit"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (note != null) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text(tr("common_title")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text(tr("note_content_placeholder")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { if (title.isNotBlank() || content.isNotBlank()) onSave(title, content) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(tr("common_save"), fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun DiaryScreen(
    entries: List<DiaryEntry>,
    diaryKey: SecretKeySpec?,
    hasPinConfigured: Boolean,
    onConfigurePin: (String) -> Unit,
    onUnlock: (String) -> Boolean,
    onLock: () -> Unit,
    onOpenChangePin: () -> Unit,
    onAddEntry: (String) -> Unit,
    onDeleteEntry: (DiaryEntry) -> Unit,
    tr: (String) -> String
) {
    if (diaryKey == null) {
        DiaryPinUnlockView(
            hasPinConfigured = hasPinConfigured,
            onPinComplete = { pin ->
                if (!hasPinConfigured) {
                    onConfigurePin(pin)
                    true
                } else {
                    onUnlock(pin)
                }
            },
            tr = tr
        )
    } else {
        var entryText by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tr("diary_records"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onOpenChangePin,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = DiaryColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(tr("diary_change_pin"), color = DiaryColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = onLock,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(tr("diary_lock"), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = entryText,
                onValueChange = { entryText = it },
                placeholder = { Text(tr("diary_placeholder")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (entryText.isNotBlank()) {
                        onAddEntry(entryText)
                        entryText = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DiaryColor)
            ) {
                Text(tr("common_save"), fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (entries.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(tr("common_empty"), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(entries, key = { it.id }) { entry ->
                        val decrypted = remember(entry.cipherText, diaryKey) {
                            try {
                                CryptoHelper.decrypt(entry.cipherText, entry.iv, diaryKey)
                            } catch (_: Exception) {
                                ""
                            }
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = entry.date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DiaryColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = decrypted,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(onClick = { onDeleteEntry(entry) }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun DiaryPinUnlockView(
    hasPinConfigured: Boolean,
    onPinComplete: (String) -> Boolean,
    tr: (String) -> String
) {
    var pin by remember { mutableStateOf("") }
    var tempPinForSetup by remember { mutableStateOf<String?>(null) }
    var isConfirmingSetup by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(DiaryColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = DiaryColor, modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                !hasPinConfigured && !isConfirmingSetup -> tr("diary_pin_new")
                !hasPinConfigured && isConfirmingSetup -> tr("diary_pin_confirm")
                else -> tr("diary_pin_title")
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
        ) {
            if (pin.isEmpty()) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    )
                }
            } else {
                repeat(pin.length) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(DiaryColor)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "✓", "0", "⌫")
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.width(250.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(keys) { key ->
                Surface(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (key == "⌫") {
                                if (pin.isNotEmpty()) pin = pin.dropLast(1)
                            } else if (key == "✓") {
                                if (pin.length in 4..12) {
                                    if (!hasPinConfigured) {
                                        if (!isConfirmingSetup) {
                                            tempPinForSetup = pin
                                            pin = ""
                                            isConfirmingSetup = true
                                        } else {
                                            if (pin == tempPinForSetup) {
                                                onPinComplete(pin)
                                            } else {
                                                coroutineScope.launch {
                                                    shakeOffset.animateTo(
                                                        targetValue = 0f,
                                                        animationSpec = keyframes {
                                                            durationMillis = 350
                                                            -18f at 50
                                                            18f at 100
                                                            -12f at 150
                                                            12f at 200
                                                            -6f at 250
                                                            6f at 300
                                                            0f at 350
                                                        }
                                                    )
                                                    pin = ""
                                                    isConfirmingSetup = false
                                                    tempPinForSetup = null
                                                }
                                            }
                                        }
                                    } else {
                                        val success = onPinComplete(pin)
                                        if (!success) {
                                            coroutineScope.launch {
                                                shakeOffset.animateTo(
                                                    targetValue = 0f,
                                                    animationSpec = keyframes {
                                                        durationMillis = 350
                                                        -18f at 50
                                                        18f at 100
                                                        -12f at 150
                                                        12f at 200
                                                        -6f at 250
                                                        6f at 300
                                                        0f at 350
                                                    }
                                                )
                                                pin = ""
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (pin.length < 12) {
                                    pin += key
                                }
                            }
                        },
                    color = if (key == "✓") DiaryColor else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (key == "✓") DiaryColor else MaterialTheme.colorScheme.outline),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (key == "✓") {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        } else {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePinBottomSheet(
    onDismiss: () -> Unit,
    onChangePin: (String) -> Unit,
    tr: (String) -> String
) {
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(tr("diary_change_pin"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = newPin,
                onValueChange = {
                    if (it.length <= 12) {
                        newPin = it.filter { ch -> ch.isDigit() }
                        isError = false
                    }
                },
                label = { Text(tr("diary_pin_new_label")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPin,
                onValueChange = {
                    if (it.length <= 12) {
                        confirmPin = it.filter { ch -> ch.isDigit() }
                        isError = false
                    }
                },
                label = { Text(tr("diary_pin_confirm_label")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            if (isError) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = tr("diary_pin_mismatch"),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (newPin.length in 4..12 && newPin == confirmPin) {
                        onChangePin(newPin)
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DiaryColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(tr("diary_pin_update"), fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun WaterScreen(
    waterLogs: List<WaterLog>,
    goal: Int,
    onAddWater: (Int) -> Unit,
    onOpenCustomWater: () -> Unit,
    onOpenGoalSetting: () -> Unit,
    onDeleteLog: (WaterLog) -> Unit,
    onResetToday: () -> Unit,
    tr: (String) -> String
) {
    val todayLogs = waterLogs.filter { it.date == LocalDate.now() }
    val totalToday = todayLogs.sumOf { it.amount }
    val progress = if (goal > 0) (totalToday.toFloat() / goal.toFloat()).coerceIn(0f, 1f) else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onOpenGoalSetting() },
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(tr("water_goal"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text("$goal ml", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = WaterColor)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(190.dp)
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            val outlineColor = MaterialTheme.colorScheme.outline
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 12.dp.toPx()
                drawArc(
                    color = outlineColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawArc(
                    color = WaterColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$totalToday / $goal ml",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val presets = listOf(200, 330, 500)
            presets.forEach { ml ->
                Button(
                    onClick = { onAddWater(ml) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("+$ml", color = WaterColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Button(
                onClick = onOpenCustomWater,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WaterColor)
            ) {
                Text(tr("common_custom"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(tr("common_today"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (todayLogs.isNotEmpty()) {
                Text(
                    text = tr("common_reset"),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onResetToday() }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (todayLogs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(tr("common_empty"), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todayLogs, key = { it.id }) { log ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(log.time, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text("+${log.amount} ml", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = WaterColor)
                            IconButton(onClick = { onDeleteLog(log) }, modifier = Modifier.size(22.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWaterBottomSheet(
    onDismiss: () -> Unit,
    onAdd: (Int) -> Unit,
    tr: (String) -> String
) {
    var amountText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(tr("water_add_amount"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                label = { Text(tr("water_amount_label")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amount = amountText.toIntOrNull()
                    if (amount != null && amount > 0) onAdd(amount)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WaterColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(tr("common_save"), fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterGoalBottomSheet(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
    tr: (String) -> String
) {
    var goalText by remember { mutableStateOf(currentGoal.toString()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(tr("water_set_goal"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = goalText,
                onValueChange = { goalText = it.filter { ch -> ch.isDigit() } },
                label = { Text(tr("water_goal_label")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val goal = goalText.toIntOrNull()
                    if (goal != null && goal > 0) onSave(goal)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WaterColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(tr("common_save"), fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    reminders: List<AppReminder>,
    onAddReminder: (AppReminder) -> Unit,
    onDeleteReminder: (AppReminder) -> Unit,
    tr: (String) -> String
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var daily by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var hour by remember { mutableIntStateOf(12) }
    var minute by remember { mutableIntStateOf(0) }
    var calendarOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = title, onValueChange = { title = it }, label = { Text(tr("reminder_title")) },
            modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = message, onValueChange = { message = it }, label = { Text(tr("reminder_message")) },
            modifier = Modifier.fillMaxWidth(), minLines = 2, shape = RoundedCornerShape(14.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf(false, true).forEach { isDaily ->
                val selected = daily == isDaily
                Surface(
                    modifier = Modifier.weight(1f).height(44.dp).clip(RoundedCornerShape(12.dp)).clickable { daily = isDaily },
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)
                ) { Box(contentAlignment = Alignment.Center) {
                    Text(if (isDaily) tr("reminder_daily") else tr("reminder_once"),
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                } }
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { calendarOpen = true },
            color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(if (daily) tr("reminder_start") else tr("reminder_select_date"), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
                Text(startDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                if (daily) {
                    Text(tr("reminder_end"), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
                    Text(endDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable {
                TimePickerDialog(context, { _, selectedHour, selectedMinute ->
                    hour = selectedHour; minute = selectedMinute
                }, hour, minute, true).show()
            },
            color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)
        ) {
            Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(tr("reminder_time"), fontWeight = FontWeight.SemiBold)
                Text(String.format(Locale.getDefault(), "%02d:%02d", hour, minute), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
        Button(
            onClick = {
                if (title.isNotBlank() && !startDate.isBefore(LocalDate.now()) &&
                    (startDate.isAfter(LocalDate.now()) || LocalTime.of(hour, minute).isAfter(LocalTime.now())) &&
                    (!daily || !endDate.isBefore(startDate))) {
                    val actualEnd = if (daily) endDate else startDate
                    onAddReminder(AppReminder(title = title.trim(), message = message.trim(), startDate = startDate, endDate = actualEnd, hour = hour, minute = minute, daily = daily))
                    title = ""; message = ""; daily = false; startDate = LocalDate.now(); endDate = LocalDate.now(); hour = 12; minute = 0
                }
            },
            enabled = title.isNotBlank() && !startDate.isBefore(LocalDate.now()) &&
                (startDate.isAfter(LocalDate.now()) || LocalTime.of(hour, minute).isAfter(LocalTime.now())) &&
                (!daily || !endDate.isBefore(startDate)),
            modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp)
        ) { Text(tr("reminder_add"), fontWeight = FontWeight.Bold) }

        if (reminders.isEmpty()) {
            Text(tr("reminder_empty"), color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f), modifier = Modifier.padding(vertical = 12.dp))
        } else {
            reminders.forEach { reminder ->
                Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(reminder.title, fontWeight = FontWeight.Bold)
                            if (reminder.message.isNotBlank()) Text(reminder.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f))
                            Text(
                                "${reminder.startDate} ${String.format(Locale.getDefault(), "%02d:%02d", reminder.hour, reminder.minute)}" +
                                    if (reminder.daily) " – ${reminder.endDate} · ${tr("reminder_daily")}" else " · ${tr("reminder_once")}",
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { onDeleteReminder(reminder) }) { Icon(Icons.Default.Delete, contentDescription = tr("common_delete"), tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    if (calendarOpen) {
        CustomDatePickerModal(
            isRange = daily,
            onDismiss = { calendarOpen = false },
            onDateSelected = { start, end ->
                startDate = start
                endDate = end ?: start
                calendarOpen = false
            },
            tr = tr
        )
    }
}

@Composable
fun CalendarScreen(
    routines: List<Routine>,
    waterLogs: List<WaterLog>,
    savingsEntries: List<SavingsEntry>,
    diaryEntries: List<DiaryEntry>,
    onDayClick: (LocalDate) -> Unit,
    tr: (String) -> String
) {
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { displayedMonth = displayedMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            Text(
                text = displayedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { displayedMonth = displayedMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val daysOfWeek = listOf(
            tr("cal_day_pt"),
            tr("cal_day_sa"),
            tr("cal_day_ca"),
            tr("cal_day_pe"),
            tr("cal_day_cu"),
            tr("cal_day_ct"),
            tr("cal_day_pz")
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            daysOfWeek.forEach { dayName ->
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val firstDayOfMonth = displayedMonth.atDay(1).dayOfWeek.value
        val totalDays = displayedMonth.lengthOfMonth()
        val gridCells = (1 until firstDayOfMonth).map { null } + (1..totalDays).map { displayedMonth.atDay(it) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(gridCells) { day ->
                if (day != null) {
                    val hasWater = waterLogs.any { it.date == day }
                    val hasRoutine = routines.any { it.completedDate == day || it.items.any { item -> item.checkedDate == day } }
                    val hasSavings = savingsEntries.any { it.date == day }
                    val hasDiary = diaryEntries.any { it.date == day }
                    val isToday = day == LocalDate.now()

                    Surface(
                        modifier = Modifier
                            .height(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onDayClick(day) },
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            if (isToday) 1.5.dp else 1.dp,
                            if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = day.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                if (hasWater) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(WaterColor))
                                if (hasRoutine) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(CompletedColor))
                                if (hasSavings) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(SavingsColor))
                                if (hasDiary) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(DiaryColor))
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(54.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayInspectorBottomSheet(
    date: LocalDate,
    routines: List<Routine>,
    waterLogs: List<WaterLog>,
    savingsEntries: List<SavingsEntry>,
    diaryEntries: List<DiaryEntry>,
    currency: String,
    onDismiss: () -> Unit,
    tr: (String) -> String
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dayWater = waterLogs.filter { it.date == date }.sumOf { it.amount }
    val dayRoutines = routines.filter { it.completedDate == date || it.items.any { item -> item.checkedDate == date } }
    val daySavings = savingsEntries.filter { it.date == date }.sumOf { it.amount }
    val hasDiary = diaryEntries.any { it.date == date }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy EEEE", Locale.getDefault())),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(tr("inspector_water"), style = MaterialTheme.typography.labelMedium, color = WaterColor, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("$dayWater ml", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(tr("inspector_savings"), style = MaterialTheme.typography.labelMedium, color = SavingsColor, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("+$daySavings $currency", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(tr("inspector_routine"), style = MaterialTheme.typography.labelMedium, color = CompletedColor, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    if (dayRoutines.isEmpty()) {
                        Text(tr("inspector_no_record"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
                    } else {
                        dayRoutines.forEach { r ->
                            Text(r.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            if (hasDiary) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(tr("inspector_diary"), style = MaterialTheme.typography.labelMedium, color = DiaryColor, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(tr("inspector_diary_exists"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun SavingsScreen(
    entries: List<SavingsEntry>,
    goal: Double,
    currency: String,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDeleteEntry: (SavingsEntry) -> Unit,
    tr: (String) -> String
) {
    val total = entries.sumOf { it.amount }
    val progress = if (goal > 0) (total / goal).coerceIn(0.0, 1.0).toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(tr("savings_total"), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("$total $currency", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = SavingsColor)
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Savings, contentDescription = null, tint = SavingsColor)
                    }
                }
                if (goal > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${tr("savings_goal_label")}: $goal $currency", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SavingsColor)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            SavingsAreaChart(entries = entries)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onAddClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SavingsColor)
        ) {
            Text(tr("common_add"), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (entries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(tr("common_empty"), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("+${entry.amount} $currency", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SavingsColor)
                                Text(
                                    text = "${entry.date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))}${if (entry.note.isNotBlank()) " • ${entry.note}" else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            IconButton(onClick = { onDeleteEntry(entry) }, modifier = Modifier.size(22.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun SavingsAreaChart(entries: List<SavingsEntry>) {
    var animTrigger by remember { mutableStateOf(false) }
    LaunchedEffect(entries.size) { animTrigger = true }

    val chartAnimProgress by animateFloatAsState(
        targetValue = if (animTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = ""
    )

    val sorted = entries.sortedBy { it.date }
    var cumulative = 0.0
    val points = sorted.map {
        cumulative += it.amount
        cumulative
    }

    Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 18.dp)) {
        if (points.size < 2) {
            drawLine(
                color = SavingsColor.copy(alpha = 0.2f),
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2.dp.toPx()
            )
            return@Canvas
        }

        val maxVal = points.maxOrNull() ?: 1.0
        val minVal = 0.0
        val widthStep = size.width / (points.size - 1)

        val coordinates = points.mapIndexed { index, value ->
            val x = index * widthStep
            val normalizedY = (((value - minVal) / (maxVal - minVal)) * size.height).toFloat() * chartAnimProgress
            val y = size.height - normalizedY
            Offset(x, y)
        }

        val strokePath = Path().apply {
            moveTo(coordinates.first().x, coordinates.first().y)
            for (i in 0 until coordinates.size - 1) {
                val p0 = coordinates[if (i == 0) 0 else i - 1]
                val p1 = coordinates[i]
                val p2 = coordinates[i + 1]
                val p3 = coordinates[if (i + 2 < coordinates.size) i + 2 else i + 1]

                val cp1x = p1.x + (p2.x - p0.x) / 6f
                val cp1y = p1.y + (p2.y - p0.y) / 6f
                val cp2x = p2.x - (p3.x - p1.x) / 6f
                val cp2y = p2.y - (p3.y - p1.y) / 6f

                cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
            }
        }

        val fillPath = Path().apply {
            addPath(strokePath)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(SavingsColor.copy(alpha = 0.3f), Color.Transparent)
            )
        )

        drawPath(
            path = strokePath,
            color = SavingsColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        coordinates.forEach { pt ->
            drawCircle(
                color = SavingsColor,
                radius = 3.5.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = Color.White,
                radius = 1.5.dp.toPx(),
                center = pt
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSavingsBottomSheet(
    currency: String,
    onDismiss: () -> Unit,
    onSave: (Double, String, LocalDate) -> Unit,
    tr: (String) -> String
) {
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(tr("savings_add_title"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("${tr("savings_amount_label")} ($currency)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text(tr("savings_note_label")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null && amount > 0) onSave(amount, noteText, LocalDate.now())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SavingsColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(tr("common_save"), fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsSettingsBottomSheet(
    currentCurrency: String,
    currentGoal: Double,
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit,
    tr: (String) -> String
) {
    var currency by remember { mutableStateOf(currentCurrency) }
    var goalText by remember { mutableStateOf(if (currentGoal > 0) currentGoal.toString() else "") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(tr("savings_settings"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text(tr("savings_currency_label")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = goalText,
                onValueChange = { goalText = it },
                label = { Text(tr("savings_goal_label")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val goal = goalText.toDoubleOrNull() ?: 0.0
                    onSave(currency, goal)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SavingsColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(tr("common_save"), fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun WheelLanguageModalBottomSheet(
    languages: List<LanguagePack>,
    currentCode: String,
    isMandatory: Boolean,
    onConfirmLanguage: (LanguagePack) -> Unit,
    onDismiss: () -> Unit,
    tr: (String) -> String
) {
    var chosenCode by remember(currentCode, languages) {
        mutableStateOf(languages.firstOrNull { it.code == currentCode }?.code ?: languages.firstOrNull()?.code.orEmpty())
    }

    AlertDialog(
        onDismissRequest = { if (!isMandatory) onDismiss() },
        title = {
            Text(
                text = tr("lang_select_title"),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                languages.forEach { pack ->
                    val selected = chosenCode == pack.code
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { chosenCode = pack.code },
                        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = pack.name,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            if (selected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = languages.any { it.code == chosenCode },
                onClick = { languages.firstOrNull { it.code == chosenCode }?.let(onConfirmLanguage) }
            ) { Text(tr("lang_select_button"), fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            if (!isMandatory) {
                TextButton(onClick = onDismiss) { Text(tr("common_cancel")) }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}


@Composable
fun CounterScreen(
    groups: List<CounterGroup>,
    onAddGroup: () -> Unit,
    onAddItem: (String) -> Unit,
    onItemClick: (String, String) -> Unit,
    onDeleteItem: (String, String) -> Unit,
    onDeleteGroup: (String) -> Unit,
    tr: (String) -> String
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(tr("counter_groups"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(onClick = onAddGroup, shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(tr("counter_new_group"))
            }
        }
        if (groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(tr("counter_empty"), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f), textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
                items(groups, key = { it.id }) { group ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(group.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = { onDeleteGroup(group.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = tr("common_delete"), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            if (group.items.isEmpty()) {
                                Text(tr("counter_no_items"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
                            }
                            group.items.forEach { item ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp))
                                        .clickable { onItemClick(group.id, item.id) },
                                    color = MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Row(modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                            Text("${tr("counter_total")}: ${item.total}", style = MaterialTheme.typography.labelMedium, color = CounterColor)
                                        }
                                        IconButton(onClick = { onDeleteItem(group.id, item.id) }) {
                                            Icon(Icons.Default.Close, contentDescription = tr("common_delete"), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                                        }
                                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = CounterColor, modifier = Modifier.padding(end = 8.dp).size(22.dp))
                                    }
                                }
                            }
                            TextButton(onClick = { onAddItem(group.id) }, modifier = Modifier.align(Alignment.End)) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(tr("counter_new_item"))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterNameBottomSheet(
    title: String,
    label: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    tr: (String) -> String
) {
    var value by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = MaterialTheme.colorScheme.surface, modifier = Modifier.imePadding()) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = value, onValueChange = { value = it }, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            Spacer(Modifier.height(16.dp))
            Button(onClick = { if (value.isNotBlank()) onSave(value.trim()) }, enabled = value.isNotBlank(), modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp)) {
                Text(tr("common_save"), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterAmountBottomSheet(
    item: CounterItem,
    onDismiss: () -> Unit,
    onDeleteEntry: (String) -> Unit,
    onAdd: (Long) -> Unit,
    tr: (String) -> String
) {
    var amountText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    fun submit() {
        val amount = amountText.toLongOrNull()
        if (amount != null && amount > 0L) onAdd(amount)
    }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = MaterialTheme.colorScheme.surface, modifier = Modifier.imePadding()) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 28.dp)) {
            Text(item.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("${tr("counter_total")}: ${item.total}", style = MaterialTheme.typography.titleMedium, color = CounterColor, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter(Char::isDigit) },
                label = { Text(tr("counter_amount")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() })
            )
            Spacer(Modifier.height(14.dp))
            Text(tr("counter_history"), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            if (item.entries.isEmpty()) {
                Text(tr("counter_no_history"), modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
            } else {
                item.entries.take(6).forEach { entry ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${entry.date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))} ${entry.time}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text("+${entry.amount}", style = MaterialTheme.typography.bodyMedium, color = CounterColor, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { onDeleteEntry(entry.id) }, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.Close, contentDescription = tr("common_delete"), tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { submit() }, enabled = (amountText.toLongOrNull() ?: 0L) > 0L, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = CounterColor)) {
                Text(tr("counter_add_amount"), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

