package com.example.appfichaje.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    private static final SimpleDateFormat INPUT_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    static {
        // La API devuelve fechas en UTC sin indicador de zona
        INPUT_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        // TIME_FORMAT y DATE_FORMAT usan la zona horaria local del dispositivo
    }

    /** Convierte "2026-05-08T10:00:00" (UTC) → "12:00" (hora local). */
    public static String utcIsoToLocalTime(String isoUtc) {
        if (isoUtc == null) return "--:--";
        try {
            Date date = INPUT_FORMAT.parse(normalize(isoUtc));
            return TIME_FORMAT.format(date);
        } catch (ParseException e) {
            // Fallback: devuelve los primeros 5 caracteres de la parte hora
            String[] parts = isoUtc.replace("T", " ").split(" ");
            if (parts.length >= 2 && parts[1].length() >= 5) {
                return parts[1].substring(0, 5);
            }
            return isoUtc;
        }
    }

    /** Convierte "2026-05-08T10:00:00" (UTC) → "08/05/2026" (fecha local). */
    public static String utcIsoToLocalDate(String isoUtc) {
        if (isoUtc == null) return "";
        try {
            Date date = INPUT_FORMAT.parse(normalize(isoUtc));
            return DATE_FORMAT.format(date);
        } catch (ParseException e) {
            // Fallback: reformatea la parte de fecha del string
            String[] parts = isoUtc.replace("T", " ").split(" ");
            if (parts.length >= 1) {
                String[] dp = parts[0].split("-");
                if (dp.length == 3) return dp[2] + "/" + dp[1] + "/" + dp[0];
            }
            return isoUtc;
        }
    }

    private static String normalize(String isoUtc) {
        String s = isoUtc.replace(" ", "T");
        // Eliminar milisegundos si los hay ("2026-05-08T10:00:00.123" → "2026-05-08T10:00:00")
        int dot = s.indexOf('.');
        if (dot != -1) s = s.substring(0, dot);
        return s;
    }
}
