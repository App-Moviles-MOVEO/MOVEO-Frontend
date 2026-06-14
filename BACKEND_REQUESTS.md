# MOVEO Backend — Pedidos para el flujo de renta por fechas

Documento para el equipo backend. La app móvil (cliente/renter) está implementando el
flujo completo de renta: catálogo → detalle → selección de fechas → reserva → pago.
Al revisar el backend actual encontramos **vacíos en el manejo de disponibilidad por
fechas**. Aquí está todo lo que se necesita, ordenado por prioridad, con contratos
propuestos listos para implementar.

> Código revisado: `Rental/Domain/Services/RentalService.cs` (método `CreateAsync`),
> `Rental/Interfaces/REST/VehicleController.cs`, `Rental/Interfaces/REST/RentalController.cs`.

---

## 🔴 P1 — CRÍTICO: `POST /api/v1/rentals` no valida solapamiento de fechas

**Problema actual:** `RentalService.CreateAsync` solo verifica que el vehículo exista
y guarda la reserva. **Dos usuarios pueden reservar el mismo auto en las mismas fechas.**
La app va a validar por su lado (consultando las reservas del vehículo antes de crear),
pero eso no cubre una carrera: dos clientes confirmando al mismo segundo. La única
protección real es en el servidor, dentro de la transacción.

**Qué se pide:** antes de insertar, verificar que NO exista otra reserva del mismo
`vehicleId` en estado **`pending`, `accepted` o `active`** cuyo rango se cruce con el
solicitado. Regla de solapamiento estándar (rangos `[start, end)`):

```
existe_conflicto = (nueva.startDate < existente.endDate) AND (existente.startDate < nueva.endDate)
```

- `cancelled` y `completed` **no bloquean** (sus fechas quedan libres).
- Usar `[start, end)` — fin exclusivo — para que una reserva que termina el día 11
  permita a otra empezar el día 11 (entrega/recojo el mismo día).

**Respuesta si hay conflicto:** `409 Conflict`

```json
{
  "error": "vehicle_not_available",
  "message": "El vehículo ya tiene una reserva en ese rango de fechas",
  "conflictingRanges": [
    { "startDate": "2026-06-20T10:00:00Z", "endDate": "2026-06-22T10:00:00Z" }
  ]
}
```

(`conflictingRanges` es opcional pero ayuda a la app a sugerir otras fechas.)

**Validaciones adicionales en el mismo endpoint (hoy no existen):**
| Caso | Respuesta esperada |
|------|--------------------|
| `endDate <= startDate` | `400` "endDate debe ser posterior a startDate" |
| `startDate` en el pasado (antes de hoy 00:00 UTC) | `400` |
| Vehículo con `status` ≠ `active` (suspendido/eliminado) | `409` "vehicle_not_active" |
| `renterId == ownerId` (dueño reservando su propio auto) | `400` |

---

## 🟠 P2 — Disponibilidad de un vehículo: `GET /api/v1/vehicles/{id}/availability`

La app necesita **pintar en el calendario las fechas ocupadas** (no seleccionables)
cuando el cliente va a reservar. Hoy el único camino es `GET /rentals?vehicleId=X` y
filtrar en la app, lo cual expone reservas de otros usuarios (ids, precios, notas) solo
para saber qué días están tomados. Mejor un endpoint dedicado que devuelva únicamente
los rangos:

**Request:** `GET /api/v1/vehicles/{id}/availability?from=2026-06-01&to=2026-08-31`
(`from`/`to` opcionales; default: desde hoy hasta +3 meses)

**Respuesta 200:**
```json
{
  "vehicleId": 5,
  "busyRanges": [
    { "startDate": "2026-06-20T10:00:00Z", "endDate": "2026-06-22T10:00:00Z" },
    { "startDate": "2026-07-01T09:00:00Z", "endDate": "2026-07-05T09:00:00Z" }
  ]
}
```

Solo cuentan reservas `pending`, `accepted`, `active` (mismo criterio que P1).

---

## 🟠 P3 — Catálogo filtrado por fechas: `GET /api/v1/vehicles?startDate&endDate`

El diseño de la app tiene un selector de fechas en el catálogo ("9–11 may"): el cliente
elige cuándo necesita el auto y la lista debe mostrar **solo autos libres en ese rango**.

**Qué se pide:** dos query params opcionales en el `GET /vehicles` existente:

```
GET /api/v1/vehicles?district=Miraflores&startDate=2026-06-20&endDate=2026-06-22
```

- Si vienen ambos, excluir vehículos con alguna reserva `pending/accepted/active`
  que se solape con el rango (misma regla de P1).
- Si no vienen, comportamiento actual (sin cambio).
- Acepta fecha simple `yyyy-MM-dd` o ISO completo; interpretar en UTC.
- Combinable con los filtros existentes (`district`, `bodyType`, `minPrice`, `maxPrice`, `ownerId`, `status`).

---

## 🟡 P4 — Filtros de transmisión y combustible en `GET /api/v1/vehicles`

El catálogo tiene chips "automático" y "eléctrico". Hoy el backend solo filtra por
`ownerId`, `status`, `minPrice`, `maxPrice`, `district`, `bodyType`. Mientras tanto la
app filtrará en memoria, pero con muchos vehículos eso no escala.

```
GET /api/v1/vehicles?transmission=automatic
GET /api/v1/vehicles?fuelType=electric
```

Valores: `transmission` = `automatic` | `manual`; `fuelType` = `gasoline` | `diesel` | `electric` | `hybrid` | `gas`.

---

## 🟡 P5 — Expiración de reservas `pending` sin pagar

**Caso borde importante:** con P1 implementado, una reserva `pending` bloquea fechas.
Si un cliente crea la reserva y **nunca paga**, esas fechas quedan bloqueadas para
siempre y el auto se vuelve inalquilable.

**Qué se pide (cualquiera de las dos opciones):**
- **Opción A (preferida):** job/chequeo que pase a `cancelled` las reservas `pending`
  con más de **N minutos** sin pago (sugerido: 30 min). Puede ser lazy: al consultar
  disponibilidad, ignorar `pending` viejas.
- **Opción B:** que las `pending` no bloqueen y solo bloqueen `accepted/active`
  (menos seguro: dos personas pueden pagar el mismo rango).

Documentar cuál se eligió para que la app muestre el mensaje correcto.

---

## 🟢 P6 — Deseables (no bloquean a la app)

1. **Orden por cercanía:** `GET /vehicles?lat=-12.04&lng=-77.04&sort=distance` usando
   el lat/lng que ya guarda cada vehículo. La app hoy mostrará el distrito en su lugar.
2. **`conflictingRanges` en el 409** de P1 (descrito arriba) para sugerir fechas libres.
3. **Paginación** en `GET /vehicles` (`page`, `pageSize`) para cuando el catálogo crezca.
4. **`PATCH /rentals/{id}` con `status: "cancelled"`**: confirmar que al cancelar se
   liberan las fechas (con P1/P2 ya implementados esto sale gratis si el filtro de
   estados es correcto — solo verificarlo con un test).

---

## Resumen de convenciones (para que app y backend coincidan)

| Tema | Convención |
|------|------------|
| Zona horaria | Todo en **UTC**, formato ISO 8601 (`2026-06-20T10:00:00Z`) |
| Rango de reserva | `[startDate, endDate)` — fin **exclusivo** |
| Estados que bloquean fechas | `pending`, `accepted`, `active` |
| Estados que liberan fechas | `cancelled`, `completed` |
| Conflicto de fechas | HTTP **409** con `error: "vehicle_not_available"` |
| Datos inválidos | HTTP **400** con mensaje descriptivo |

## Orden sugerido de implementación

1. **P1** (validación 409) — sin esto hay dobles reservas en producción.
2. **P2** (availability) — la app deja de leer reservas ajenas.
3. **P3** (catálogo por fechas) — habilita el buscador por fechas del diseño.
4. P4, P5, P6 según tiempo.

> Mientras P1–P3 no estén, la app funcionará igual: valida solapamiento por su cuenta
> leyendo `GET /rentals?vehicleId=X` (ya disponible). Cuando el backend publique estos
> endpoints, la app solo cambia la fuente de datos — el flujo de UI no se toca.
