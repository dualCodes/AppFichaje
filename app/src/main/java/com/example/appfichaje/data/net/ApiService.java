package com.example.appfichaje.data.net;

import com.example.appfichaje.data.model.CrearIncidenciaRequest;
import com.example.appfichaje.data.model.EntradaNfcResponse;
import com.example.appfichaje.data.model.EntradaResponse;
import com.example.appfichaje.data.model.EstadoResponse;
import com.example.appfichaje.data.model.FichajeGpsRequest;
import com.example.appfichaje.data.model.HorarioHoyResponse;
import com.example.appfichaje.data.model.Incidencia;
import com.example.appfichaje.data.model.IncidenciasListResponse;
import com.example.appfichaje.data.model.LoginRequest;
import com.example.appfichaje.data.model.LoginResponse;
import com.example.appfichaje.data.model.MisRegistrosResponse;
import com.example.appfichaje.data.model.ResumenMensualResponse;
import com.example.appfichaje.data.model.SalidaNfcResponse;
import com.example.appfichaje.data.model.SalidaResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // GPS fichaje
    @POST("/api/presencia/entrada")
    Call<EntradaResponse> ficharEntradaGps(@Body FichajeGpsRequest fichajeGpsRequest);

    @POST("/api/presencia/salida")
    Call<SalidaResponse> ficharSalidaGps(@Body FichajeGpsRequest fichajeGpsRequest);

    // NFC fichaje
    @POST("/api/presencia/entrada-nfc")
    Call<EntradaNfcResponse> ficharEntradaNfc();

    @POST("/api/presencia/salida-nfc")
    Call<SalidaNfcResponse> ficharSalidaNfc();

    // Estado y horario
    @GET("/api/presencia/estado")
    Call<EstadoResponse> getEstado();

    @GET("/api/presencia/horario-hoy")
    Call<HorarioHoyResponse> getHorarioHoy();

    // Historial y resumen
    @GET("/api/presencia/mis-registros")
    Call<MisRegistrosResponse> getMisRegistros(
            @Query("desde") String desde,
            @Query("hasta") String hasta
    );

    @GET("/api/presencia/resumen-mensual")
    Call<ResumenMensualResponse> getResumenMensual(@Query("mes") String mes);

    // Incidencias
    @POST("/api/incidencias")
    Call<Incidencia> crearIncidencia(@Body CrearIncidenciaRequest request);

    @GET("/api/incidencias")
    Call<IncidenciasListResponse> getMisIncidencias(
            @Query("desde") String desde,
            @Query("hasta") String hasta
    );
}

