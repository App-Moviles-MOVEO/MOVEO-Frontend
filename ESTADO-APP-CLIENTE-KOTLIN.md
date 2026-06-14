# WheelsPe / MOVEO — Estado de la App Móvil Cliente (Android / Kotlin)

> **Alcance de esta app:** SOLO el lado **Cliente / Arrendatario** (quien alquila el auto y usa
> los servicios: catálogo, reserva, pago, carpooling como pasajero, seguridad, reputación,
> recompensas). Las funciones de **Propietario/Conductor** (publicar auto, publicar ruta, gestionar
> mis publicaciones) existen como pantallas demo locales pero **están fuera del alcance** de esta
> app — irán en otra app/panel.
>
> **Repo:** MOVEO-Frontend (Kotlin) · **Backend:** MOVEO-Backend (.NET) · **Fecha:** 2026-06-13
>
> Leyenda: ✅ Hecho y conectado al backend real · 🟡 Hecho a medias / simulado / falta backend ·
> ⬜ Pendiente · 🔵 Solo UI (sin backend disponible aún) · ⛔ Fuera de alcance (rol propietario)

---

## 1. Resumen ejecutivo

La app cliente tiene **el flujo central de alquiler completo y funcionando contra el backend real**:
registro → KYC → login → catálogo de autos → detalle → selección de fechas → reserva → pago
(tarjeta vía Stripe en modo demo + Yape en un paso). Además están conectados: carpooling
(buscar ruta → detalle → reservar asiento), chat con el propietario/conductor, notificaciones,
reseñas (dar y consultar) y el perfil con su reputación.

Quedan **pendientes o a medias** funciones que dependen de endpoints que el backend todavía no
expone (GPS en tiempo real, botón de pánico real, métodos de pago tokenizados, escrow real,
recuperar contraseña, cupones/promos). Esas pantallas están en su mayoría como UI lista o
simulada, a la espera del backend.

---

## 2. Lo que YA está hecho en esta app

### 2.1 Identidad y acceso (EP01)
| US | Función | Estado | Detalle |
|----|---------|--------|---------|
| US01 | Registro con rol único | ✅ | Formulario + validación, `POST /auth/register`. |
| US02 | Verificación KYC (documento + selfie) | ✅ | Carga y consulta de estado conectada al backend. |
| US03 | Iniciar sesión + persistencia | ✅ | Login real + sesión guardada. **Ver diferencia importante en §5: el backend NO usa JWT, es sin token.** |
| US04 | Recuperar contraseña | 🟡 | **UI lista** y llama al endpoint, pero el backend aún no lo tiene → no funciona end-to-end. |

### 2.2 Alquiler de vehículos (EP03 / EP04) — núcleo de la app
| US | Función | Estado | Detalle |
|----|---------|--------|---------|
| US22 | Catálogo de vehículos | ✅ | Lista real, búsqueda por distrito, filtros (Compacto/Sedán/SUV por backend; Automático/Eléctrico en app), orden por cercanía con GPS, imagen real del auto. |
| US22 | Detalle de vehículo | ✅ | Specs, datos del propietario, chat, info de garantía, total según días. |
| — | **Selección de fechas + disponibilidad** | ✅ | Calendario que bloquea días ya reservados; valida solapamiento antes de reservar (la app lee las reservas del vehículo mientras el backend no expone disponibilidad — ver `BACKEND_REQUESTS.md`). |
| US31 | Pago de alquiler multicanal | 🟡 | Flujo completo: tarjeta con **Stripe en modo demo** + **Yape en un paso**. Falta pasarela de pago real en producción (depende del backend / SP01). |
| US24 | Garantía / Escrow | 🟡 | Se muestra y se cobra como concepto en el resumen de pago, pero **la retención/liberación real no existe** (backend pendiente). |
| — | Mis reservas (lista + detalle) | ✅ | Reservas del usuario desde el backend. |

### 2.3 Movilidad compartida / Carpooling como pasajero (EP05)
| US | Función | Estado | Detalle |
|----|---------|--------|---------|
| US14 | Buscar rutas | ✅ | Listado conectado (filtro por comunidad/dominio institucional pendiente en backend). |
| — | Detalle de ruta | ✅ | Con datos del conductor y chat. |
| US15 | Reservar asiento | ✅ | Pantalla de confirmación de reserva de asiento conectada. |

### 2.4 Reputación y comunicación (EP07 / EP05)
| US | Función | Estado | Detalle |
|----|---------|--------|---------|
| US28 / US35 | Evaluar servicio (calificación) | ✅ | Pantalla de calificación tras el viaje, conectada a reseñas del backend. |
| US19 / US37 | Consultar reputación / historial de reseñas | ✅ | Perfil muestra reseñas recibidas (rating + nº). |
| US18 | Mensajería / chat | ✅ | Chat 1-a-1 con propietario/conductor (`/messages`), conectado. |
| — | Notificaciones | ✅ | Bandeja de notificaciones del usuario, conectada. |

### 2.5 Información, seguridad y fidelización (EP02 / EP08)
| US | Función | Estado | Detalle |
|----|---------|--------|---------|
| US44 | Centro de ayuda / FAQ | ✅ | Pantalla de ayuda dentro de la app. |
| US29 / US36 | Recompensas / puntos | 🟡 | Pantalla de recompensas con puntos del perfil; **sin lógica de canje ni badges reales** (backend pendiente). |
| — | Perfil + editar perfil | ✅ | Datos del usuario; edición local. |
| — | Ajustes | ✅ | Pantalla de configuración. |

---

## 3. Lo que FALTA o está incompleto (alcance cliente)

| US | Función | Estado | Qué falta |
|----|---------|--------|-----------|
| US06 | Monitoreo GPS en tiempo real | 🔵 | Pantalla de seguimiento existe pero es **simulada**; falta endpoint de ubicación en vivo (WebSocket/SSE) en backend. |
| US07 | Rastreo de viaje / desvíos | 🔵 | Igual que US06, depende de GPS en tiempo real. |
| US08 | Botón de pánico / emergencia | 🔵 | Pantalla de seguridad es **solo UI** (toggles y contactos no se guardan, no crea incidente real). Falta conectar a `/incidents` + GPS + push. |
| US10 | Contactos de confianza | 🔵 | Listado de contactos es fijo en UI; falta backend (`trusted-contacts`). |
| US11 | Filtro "Solo Mujeres" | 🟡 | Existe el toggle en UI; falta que el backend filtre por género en rutas. |
| US21 | Vincular métodos de pago | 🔵 | Pantalla de métodos de pago es **mock**; falta tokenización en backend. |
| US25 | Comprobantes / contratos (PDF) | ⬜ | No implementado en la app cliente (backend tiene `/invoices`, falta consumirlo). |
| US26 / US33 | Reembolsos | ⬜ | No hay UI de reembolso para el cliente (backend ya soporta refund). |
| US27 | Cupones y beneficios | ⬜ | No implementado. |
| US34 | Ofertas promocionales | ⬜ | No implementado. |
| US30 / US38 | Umbrales de reputación | ⬜ | No aplica directamente al cliente / no implementado. |
| US45 | Baja voluntaria / eliminación de datos (GDPR) | ⬜ | No implementado (backend pendiente). |
| US32 | Liquidar cuota de carpooling (pago) | 🟡 | La reserva de asiento existe; el **cobro real de la cuota** comparte el mismo límite de pasarela que US31. |

---

## 4. Fuera de alcance de esta app (rol Propietario/Conductor)

Estas existen como **pantallas demo locales** pero **no son responsabilidad de la app cliente**.
Se aclara para que no se cuenten como "pendientes" del cliente:

| US | Función | Nota |
|----|---------|------|
| US05 | Acreditar propiedad de vehículo | ⛔ Rol propietario. |
| US13 | Publicar ruta de carpooling | ⛔ Rol conductor (en el backlog figura como Sprint 1, pero corresponde a la app/panel del proveedor). |
| US16 | Aprobar pasajeros / aforo | ⛔ Rol conductor. |
| — | Publicar vehículo / Mis publicaciones | ⛔ Demo local; va en app/panel de propietario. |
| US40 / US41 | Paneles de administrador | ⛔ Van en panel web admin, no en la app móvil de usuario. |

---

## 5. Diferencias / inconsistencias detectadas frente a los documentos enviados

> Esto es lo más importante para corregir el reporte: **lo que dice el backlog no coincide del todo
> con lo que el backend realmente expone y con cómo lo consume la app.**

1. **Autenticación: el backlog dice "JWT / Bearer token", pero el backend real es SIN token (stateless).**
   `POST /auth/login` devuelve el objeto usuario directamente; la "sesión" de la app es el `userId`
   guardado, que se envía como `?userId=` / `renterId` / `ownerId`. **No hay header `Authorization: Bearer`.**
   → Corregir el backlog o el backend para alinear (la nota de "guardar JWT" no aplica hoy).

2. **Nombres de endpoints distintos a los del backlog.** El backlog lista rutas que NO son las que
   la app usa realmente:
   | Backlog dice | Backend real / lo que usa la app |
   |---|---|
   | `/reservations` | `/rentals` (crear, por usuario, por vehículo, detalle) |
   | `/routes` (carpooling) | `/adventure-routes` con `type="carpool"` |
   | `/transactions` (pago) | `/rentals/{id}/pay` (pago en un paso) |
   | mensajería "pendiente" | `/messages` **ya existe y está conectado** |
   | ratings `/users/{id}/ratings` | `/reviews` y `/user-reviews` |
   | — | `/notifications/user/{userId}` **ya existe y está conectado** |
   → El backlog marca mensajería (US18) y varias como pendientes, pero **en la práctica ya funcionan**
     por otros endpoints. Conviene actualizar el contrato (Swagger) como fuente única de verdad.

3. **Disponibilidad por fechas no existe en el backend.** No hay validación de solapamiento ni
   endpoint de disponibilidad. La app lo cubre validando por su cuenta (lee las reservas del vehículo).
   Pedidos formales al backend documentados en `BACKEND_REQUESTS.md` (P1: rechazo 409 por solapamiento,
   P2: `/vehicles/{id}/availability`, P3: catálogo filtrado por fechas, P4: filtros transmisión/combustible).

4. **Pago "multicanal real" no existe (SP01 pendiente).** Stripe está en **modo demo** y Yape se
   resuelve en un paso sin pasarela real. El backlog lo marca 🟡 correctamente, pero en el reporte
   conviene dejar claro que **no hay cobro real en producción**.

5. **Escrow (US24) es solo visual.** Se cobra y explica la garantía en el resumen, pero no hay
   retención/liberación real porque el backend no lo soporta.

6. **Inconsistencia de títulos US22 ya señalada en el propio informe** (catálogo vs pago) — se mantiene;
   esta app trata US22 como **catálogo**, que es lo correcto según el Product Backlog 2.4.3.

---

## 6. Matriz rápida (User Story × estado en la app cliente)

| US | Título | Estado en esta app |
|----|--------|--------------------|
| US01 | Registro | ✅ |
| US02 | KYC | ✅ |
| US03 | Login + sesión | ✅ (sin JWT, ver §5) |
| US04 | Recuperar contraseña | 🟡 UI lista, backend pendiente |
| US22 | Catálogo + detalle | ✅ |
| US31 | Pago de alquiler | 🟡 demo (sin pasarela real) |
| US24 | Escrow / garantía | 🟡 solo visual |
| US14 | Buscar rutas carpooling | ✅ |
| US15 | Reservar asiento | ✅ |
| US32 | Pago cuota carpooling | 🟡 demo |
| US28/US35 | Calificar servicio | ✅ |
| US19/US37 | Reputación / reseñas | ✅ |
| US18 | Mensajería / chat | ✅ |
| — | Notificaciones | ✅ |
| — | Mis reservas | ✅ |
| US44 | Ayuda / FAQ | ✅ |
| US29/US36 | Recompensas / badges | 🟡 puntos UI, sin canje |
| US06/US07 | GPS en tiempo real | 🔵 simulado |
| US08 | Botón de pánico | 🔵 solo UI |
| US10 | Contactos de confianza | 🔵 solo UI |
| US11 | Filtro solo mujeres | 🟡 UI, falta backend |
| US21 | Métodos de pago | 🔵 mock |
| US25 | Comprobantes / contratos | ⬜ |
| US26/US33 | Reembolsos (cliente) | ⬜ |
| US27/US34 | Cupones / promos | ⬜ |
| US45 | Baja / borrado de datos | ⬜ |
| US05/US13/US16 | Publicar auto / ruta / aprobar | ⛔ fuera de alcance (propietario) |

---

## 7. Conclusión para el reporte

- **El camino crítico del cliente (descubrir → reservar → pagar un auto) está completo y conectado.**
- Carpooling como pasajero, chat, reseñas, notificaciones y perfil **también están conectados.**
- Lo que falta es, en su mayoría, **dependiente del backend** (pago real, escrow, GPS, pánico,
  métodos de pago, recuperar contraseña, cupones) — no de la app.
- **Acción recomendada:** alinear el backlog/Swagger con la realidad del backend (sin JWT y con los
  nombres reales de endpoints), y priorizar los pedidos de `BACKEND_REQUESTS.md` para cerrar
  disponibilidad por fechas y pago real.
