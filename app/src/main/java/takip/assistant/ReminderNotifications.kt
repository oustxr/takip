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

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.os.Build
import androidx.core.app.NotificationCompat
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

object ReminderStore {
    private const val PREFS = "takip_assistant_storage"
    private const val KEY = "scheduled_reminders_v1"

    fun load(context: Context): List<AppReminder> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                AppReminder(
                    id = o.optString("id", UUID.randomUUID().toString()),
                    title = o.optString("title"), message = o.optString("message"),
                    startDate = LocalDate.parse(o.getString("startDate")),
                    endDate = LocalDate.parse(o.getString("endDate")),
                    hour = o.getInt("hour"), minute = o.getInt("minute"),
                    daily = o.getBoolean("daily"), enabled = o.optBoolean("enabled", true)
                )
            }
        } catch (_: Exception) { emptyList() }
    }

    fun save(context: Context, reminders: List<AppReminder>) {
        val array = JSONArray()
        reminders.forEach { r ->
            array.put(JSONObject().apply {
                put("id", r.id); put("title", r.title); put("message", r.message)
                put("startDate", r.startDate.toString()); put("endDate", r.endDate.toString())
                put("hour", r.hour); put("minute", r.minute); put("daily", r.daily); put("enabled", r.enabled)
            })
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, array.toString()).apply()
    }

    fun update(context: Context, reminder: AppReminder) {
        val list = load(context).map { if (it.id == reminder.id) reminder else it }
        save(context, list)
    }
}

object ReminderScheduler {
    private const val CHANNEL_ID = "takip_reminders"
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Takip hatırlatıcıları", NotificationManager.IMPORTANCE_DEFAULT))
        }
    }

    fun schedule(context: Context, reminder: AppReminder, date: LocalDate? = null) {
        if (!reminder.enabled) return
        val targetDate = date ?: if (reminder.startDate.isBefore(LocalDate.now())) LocalDate.now() else reminder.startDate
        if (targetDate.isAfter(reminder.endDate) || targetDate.isBefore(reminder.startDate)) return
        val triggerAt = targetDate.atTime(reminder.hour, reminder.minute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (triggerAt <= System.currentTimeMillis()) {
            if (reminder.daily) {
                val next = LocalDate.now().plusDays(1)
                if (!next.isAfter(reminder.endDate)) schedule(context, reminder, next)
            }
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ID, reminder.id); putExtra(EXTRA_DATE, targetDate.toString())
        }
        val pending = PendingIntent.getBroadcast(context, reminder.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        }
    }

    fun requestExactAlarmAccess(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!manager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

    fun rescheduleAll(context: Context) {
        ReminderStore.load(context).filter { it.enabled }.forEach { schedule(context, it) }
    }

    fun cancel(context: Context, id: String) {
        val intent = Intent(context, ReminderAlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(context, id.hashCode(), intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (pending != null) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pending)
            pending.cancel()
        }
    }

    fun notify(context: Context, reminder: AppReminder) {
        ensureChannel(context)
        val openIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val contentPending = openIntent?.let { PendingIntent.getActivity(context, reminder.id.hashCode(), it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE) }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(reminder.title)
            .setContentText(reminder.message.ifBlank { reminder.title })
            .setStyle(NotificationCompat.BigTextStyle().bigText(reminder.message.ifBlank { reminder.title }))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .apply { if (contentPending != null) setContentIntent(contentPending) }
            .build()
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(reminder.id.hashCode(), notification)
    }

    internal const val EXTRA_ID = "reminder_id"
    internal const val EXTRA_DATE = "reminder_date"
}

class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra(ReminderScheduler.EXTRA_ID) ?: return
        val dateText = intent.getStringExtra(ReminderScheduler.EXTRA_DATE) ?: return
        val date = try { LocalDate.parse(dateText) } catch (_: Exception) { return }
        val reminder = ReminderStore.load(context).firstOrNull { it.id == id && it.enabled } ?: return
        if (date.isBefore(reminder.startDate) || date.isAfter(reminder.endDate)) return
        ReminderScheduler.notify(context, reminder)
        if (reminder.daily && date.isBefore(reminder.endDate)) {
            ReminderScheduler.schedule(context, reminder, date.plusDays(1))
        } else {
            ReminderStore.update(context, reminder.copy(enabled = false))
        }
    }
}

class ReminderRestoreReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val today = LocalDate.now()
        ReminderStore.load(context).filter { it.enabled }.forEach { reminder ->
            val nextDate = if (reminder.startDate.isAfter(today)) reminder.startDate else today
            if (!nextDate.isAfter(reminder.endDate)) ReminderScheduler.schedule(context, reminder, nextDate)
        }
    }
}
