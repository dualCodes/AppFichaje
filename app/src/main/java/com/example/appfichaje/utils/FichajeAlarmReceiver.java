package com.example.appfichaje.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.appfichaje.MainActivity;
import com.example.appfichaje.R;

public class FichajeAlarmReceiver extends BroadcastReceiver {

    private static final int NOTIF_ID_ENTRADA = 2001;
    private static final int NOTIF_ID_SALIDA = 2002;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            // Re-schedule notifications after reboot if user is logged in
            if (TokenManager.isLoggedIn(context)) {
                // Notifications will be rescheduled when the app is next opened
            }
            return;
        }

        String tipo = intent.getStringExtra(NotificationScheduler.EXTRA_TIPO);
        if (tipo == null) return;

        NotificationScheduler.createNotificationChannel(context);

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int piFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            piFlags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, piFlags);

        String titulo, mensaje;
        int notifId;
        if (NotificationScheduler.TIPO_ENTRADA.equals(tipo)) {
            titulo = "Hora de fichar entrada";
            mensaje = "Recuerda registrar tu entrada al trabajo";
            notifId = NOTIF_ID_ENTRADA;
        } else {
            titulo = "Hora de fichar salida";
            mensaje = "Recuerda registrar tu salida del trabajo";
            notifId = NOTIF_ID_SALIDA;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, NotificationScheduler.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notifId, builder.build());
        }
    }
}
