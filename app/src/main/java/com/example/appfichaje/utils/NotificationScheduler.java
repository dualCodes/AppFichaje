package com.example.appfichaje.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.example.appfichaje.data.model.FranjaHoraria;

public class NotificationScheduler {

    public static final String CHANNEL_ID = "fichaje_reminders";
    public static final String EXTRA_TIPO = "tipo_recordatorio";
    public static final String TIPO_ENTRADA = "entrada";
    public static final String TIPO_SALIDA = "salida";

    private static final int ALARM_ID_ENTRADA = 1001;
    private static final int ALARM_ID_SALIDA = 1002;

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorios de Fichaje",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones para recordar fichar entrada y salida");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void scheduleReminders(Context context, List<FranjaHoraria> franjas) {
        if (franjas == null || franjas.isEmpty()) return;

        // Use first franja for entrada reminder and last for salida
        FranjaHoraria primera = franjas.get(0);
        FranjaHoraria ultima = franjas.get(franjas.size() - 1);

        scheduleAlarm(context, primera.getHoraEntrada(), TIPO_ENTRADA, ALARM_ID_ENTRADA, -15);
        scheduleAlarm(context, ultima.getHoraSalida(), TIPO_SALIDA, ALARM_ID_SALIDA, -5);
    }

    private static void scheduleAlarm(Context context, String horaStr, String tipo,
                                       int alarmId, int minuteOffset) {
        if (horaStr == null || horaStr.isEmpty()) return;

        try {
            // Parse HH:mm or HH:mm:ss
            String[] parts = horaStr.split(":");
            if (parts.length < 2) return;
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute + minuteOffset);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            // If time already passed today, skip
            if (cal.getTimeInMillis() <= System.currentTimeMillis()) return;

            Intent intent = new Intent(context, FichajeAlarmReceiver.class);
            intent.setAction("com.example.appfichaje.FICHAJE_REMINDER");
            intent.putExtra(EXTRA_TIPO, tipo);

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, alarmId, intent, flags);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void cancelAll(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        int flags = PendingIntent.FLAG_NO_CREATE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        Intent intent = new Intent(context, FichajeAlarmReceiver.class);
        intent.setAction("com.example.appfichaje.FICHAJE_REMINDER");

        PendingIntent piEntrada = PendingIntent.getBroadcast(context, ALARM_ID_ENTRADA, intent, flags);
        PendingIntent piSalida = PendingIntent.getBroadcast(context, ALARM_ID_SALIDA, intent, flags);

        if (piEntrada != null) alarmManager.cancel(piEntrada);
        if (piSalida != null) alarmManager.cancel(piSalida);
    }
}
