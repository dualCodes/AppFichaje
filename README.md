# Sistema de Control de Presencia e Incidencias
### PMDM + SGE 2026 — Entrega 2

**Alumno:** Yasser Suliman Orange  

## Indice
- [Sistema de Control de Presencia e Incidencias](#sistema-de-control-de-presencia-e-incidencias)
    - [PMDM + SGE 2026 — Entrega 1](#pmdm--sge-2026--entrega-1)
  - [Indice](#indice)
  - [1. Diagrama ER de la base de datos](#1-diagrama-er-de-la-base-de-datos)
  - [2. API REST](#2-api-rest)
    - [URL base](#url-base)
    - [Swagger UI](#swagger-ui)
    - [Tabla de endpoints](#tabla-de-endpoints)
      - [Autenticación](#autenticación)
      - [Presencia](#presencia)
      - [Incidencias](#incidencias)
      - [Administración](#administración)
  - [3. Sitio web](#3-sitio-web)
    - [URL de acceso](#url-de-acceso)
    - [Credenciales](#credenciales)
    - [Notas de acceso](#notas-de-acceso)


---

## 1. Diagrama ER de la base de datos

![](image.png)
---

## 2. API REST

### URL base

https://yaliora113.eu.pythonanywhere.com

### Swagger UI  
https://yaliora113.eu.pythonanywhere.com/api/docs/swagger-ui

---

### Tabla de endpoints

Los endpoints marcados con **(JWT)** requieren la cabecera `Authorization: Bearer <token>`.  
Los marcados con **(Admin)** solo son accesibles si el token pertenece a un usuario con rol Administrador.

#### Autenticación

| Punto final | Método | Cuerpo JSON | Código | Respuesta |
|---|---|---|---|---|
| `/api/auth/register` | POST | `nif`, `nombre`, `apellidos`, `email`, `password` | 201 | `{ mensaje }` — 409 si NIF o email ya existen |
| `/api/auth/login` | POST | `identificador` (NIF o email), `password` | 200 | `{ access_token, usuario: { id, nombre, apellidos, email, nif, rol, id_empresa } }` |
| `/api/auth/solicitar-cambio-password` | POST | `email` | 200 | `{ mensaje }` — envía enlace de cambio de contraseña (caduca en 1h) |

#### Presencia

| Punto final | Método | Cuerpo JSON / Query params | Código | Respuesta |
|---|---|---|---|---|
| `/api/presencia/entrada` (JWT) | POST | `lat`, `lon` | 201 | `{ mensaje, id_registro, hora_entrada, distancia_m }` — valida radio GPS |
| `/api/presencia/salida` (JWT) | POST | `lat`, `lon` | 200 | `{ mensaje, id_registro, hora_entrada, hora_salida, duracion_minutos, distancia_m }` |
| `/api/presencia/entrada-nfc` (JWT) | POST | — | 201 | `{ mensaje, id_registro, hora_entrada }` — sin validación GPS |
| `/api/presencia/salida-nfc` (JWT) | POST | — | 200 | `{ mensaje, id_registro, hora_entrada, hora_salida, duracion_minutos }` — sin validación GPS |
| `/api/presencia/estado` (JWT) | GET | — | 200 | `{ estado, hora_entrada, id_registro, ultima_salida }` — estado: `dentro` \| `fuera` |
| `/api/presencia/horario-hoy` (JWT) | GET | — | 200 | `{ dia_semana, franjas: [ { dia, hora_entrada, hora_salida } ], tiene_horario }` |
| `/api/presencia/mis-registros` (JWT) | GET | query: `desde` (YYYY-MM-DD), `hasta` (YYYY-MM-DD), `id_trabajador`* | 200 | `{ registros: [ { id_registro, hora_entrada, hora_salida, duracion_minutos, lat_entrada, long_entrada } ], total }` |
| `/api/presencia/resumen-mensual` (JWT) | GET | query: `mes` (YYYY-MM, default: actual), `id_trabajador`* | 200 | `{ mes, id_trabajador, horas_trabajadas, horas_teoricas, horas_extra, num_fichajes }` |

*\* `id_trabajador` solo tiene efecto si el token pertenece a un Administrador.*

#### Incidencias

| Punto final | Método | Cuerpo JSON / Query params | Código | Respuesta |
|---|---|---|---|---|
| `/api/incidencias` (JWT) | POST | `descripcion`, `fecha_hora` (ISO 8601, opcional — default: momento actual) | 201 | `{ id_incidencia, fecha_hora, descripcion }` |
| `/api/incidencias` (JWT) | GET | query: `desde` (YYYY-MM-DD), `hasta` (YYYY-MM-DD) | 200 | `{ incidencias: [ { id_incidencia, fecha_hora, descripcion } ], total }` |
| `/api/admin/incidencias` (JWT, Admin) | GET | query: `desde`, `hasta`, `id_trabajador` | 200 | `{ incidencias: [ { id_incidencia, fecha_hora, descripcion, trabajador: { id, nombre, apellidos, nif } } ], total }` |

#### Administración

| Punto final | Método | Cuerpo JSON / Query params | Código | Respuesta |
|---|---|---|---|---|
| `/api/admin/empleados` (JWT, Admin) | GET | — | 200 | `{ trabajadores: [ { id_trabajador, nif, nombre, apellidos, email, telef, rol, id_empresa, id_horario } ], total }` |
| `/api/admin/empresa` (JWT, Admin) | GET | — | 200 | `{ id_empresa, nombrecomercial, cif, lat, lon, radio }` |
| `/api/admin/empresa` (JWT, Admin) | PATCH | `lat` (opcional), `lon` (opcional), `radio` (opcional) | 200 | `{ mensaje }` |

---

## 3. Sitio web

### URL de acceso

```
https://yaliora113.eu.pythonanywhere.com
```

### Credenciales

| Campo | Valor |
|-------|-------|
| **Usuario (NIF o email)** | `12345678A`/`admin@rrhh.com` |
| **Contraseña** | `admin` |

### Notas de acceso

La direccion de correo electronico para comprobar que el envio de email funciona es mi email del instituto (`yaliora113@iesfuengirola1.es`).
