package com.example.appfichaje;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.appfichaje.ui.admin.AdminFragment;
import com.example.appfichaje.ui.historial.HistorialFragment;
import com.example.appfichaje.ui.incidencias.IncidenciasFragment;
import com.example.appfichaje.ui.main.FichajeFragment;
import com.example.appfichaje.utils.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FichajeFragment fichajeFragment;
    private HistorialFragment historialFragment;
    private IncidenciasFragment incidenciasFragment;
    private AdminFragment adminFragment;
    private Fragment activeFragment;

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        setupFragments();
        setupBottomNavigation();
        setupAdminTab();
    }

    private void setupFragments() {
        fichajeFragment = new FichajeFragment();
        historialFragment = new HistorialFragment();
        incidenciasFragment = new IncidenciasFragment();
        adminFragment = new AdminFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, adminFragment, "admin").hide(adminFragment)
                .add(R.id.fragment_container, incidenciasFragment, "incidencias").hide(incidenciasFragment)
                .add(R.id.fragment_container, historialFragment, "historial").hide(historialFragment)
                .add(R.id.fragment_container, fichajeFragment, "fichaje")
                .commit();

        activeFragment = fichajeFragment;
    }

    private void setupAdminTab() {
        String rol = TokenManager.getUserRol(this);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.getMenu().findItem(R.id.nav_admin).setVisible("Administrador".equalsIgnoreCase(rol));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_fichaje) {
                showFragment(fichajeFragment);
                return true;
            } else if (id == R.id.nav_historial) {
                showFragment(historialFragment);
                return true;
            } else if (id == R.id.nav_incidencias) {
                showFragment(incidenciasFragment);
                return true;
            } else if (id == R.id.nav_admin) {
                showFragment(adminFragment);
                return true;
            }
            return false;
        });
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit();
        activeFragment = fragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableNfcForegroundDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableNfcForegroundDispatch();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNfcIntent(intent);
    }

    private void enableNfcForegroundDispatch() {
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) return;
        try {
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                    this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    android.app.PendingIntent.FLAG_MUTABLE);

            // TAG_DISCOVERED captura cualquier etiqueta NFC con máxima prioridad,
            // evitando que el sistema muestre "Servicio NFC" antes que la app.
            // La validación del contenido NDEF se hace en handleNfcIntent.
            android.content.IntentFilter[] filters = new android.content.IntentFilter[]{
                    new android.content.IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
            };

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disableNfcForegroundDispatch() {
        if (nfcAdapter != null) {
            try {
                nfcAdapter.disableForegroundDispatch(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final String TEXTO_TARJETA_VALIDA = "fichaje";

    private void handleNfcIntent(Intent intent) {
        String action = intent.getAction();
        if (!NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                && !NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
                && !NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            return;
        }

        // Leer los mensajes NDEF de la etiqueta, igual que en el ejemplo del profesor
        Parcelable[] rawMessages =
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages == null || rawMessages.length == 0) {
            Toast.makeText(this, "Tarjeta NFC sin contenido válido", Toast.LENGTH_SHORT).show();
            return;
        }

        NdefMessage[] messages = new NdefMessage[rawMessages.length];
        for (int i = 0; i < rawMessages.length; i++) {
            messages[i] = (NdefMessage) rawMessages[i];
        }

        NdefRecord record = messages[0].getRecords()[0];
        String texto = leerTexto(record);

        // Solo fichar si el texto de la tarjeta es el correcto
        if (!TEXTO_TARJETA_VALIDA.equals(texto.trim())) {
            Toast.makeText(this, "Tarjeta NFC no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tarjeta válida: notificar al fragmento de fichaje
        if (activeFragment instanceof FichajeFragment) {
            ((FichajeFragment) activeFragment).onNfcTagDetected();
        } else {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.nav_fichaje);
            fichajeFragment.onNfcTagDetected();
        }
    }

    // Mismo método que usa el profesor para leer el texto de un registro NDEF
    private String leerTexto(NdefRecord record) {
        byte[] payload = record.getPayload();
        int languageCodeLength = payload[0] & 0x3F;
        return new String(payload,
                languageCodeLength + 1,
                payload.length - languageCodeLength - 1,
                StandardCharsets.UTF_8);
    }
}

