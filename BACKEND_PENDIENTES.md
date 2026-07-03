# Pendientes del Backend — para dejar todo el flujo completo

Estado al 2026-07-03, verificado contra el deploy de Railway
(`https://successful-recreation-production-7377.up.railway.app/api/v1/`).

## Qué ya funciona (no pedir de nuevo)

- `PATCH /rentals/{id}` acepta `status`, `acceptedAt`, `completedAt`, `vehicleRated`, `vehicleRating` → la app lo usa para cancelar (US54), aceptar/iniciar/finalizar el viaje (US20) y guardar la nota del viaje.
- `POST /reviews` guarda la reseña con `rentalId/vehicleId/reviewerId/revieweeId/rating/comment/type` (verificado con 201) y el rating del vehículo se recalcula solo (promedio en `GET /vehicles`).
- `POST /payments` + `GET /payments/rental/{id}` → la app registra reembolsos automáticos (`type="refund"`) al cancelar (US26/US33).
- `POST /auth/forgot-password` y `POST /auth/kyc` ya están desplegados.

## Lo que falta (en orden de prioridad)

### 1. Rating agregado del usuario en `auth/me` / `GET /users/{id}`
El perfil necesita la calificación real del usuario. Hoy la app la calcula en el
cliente con 3 requests (`/user-reviews?reviewedUserId=`, `/reviews/reviewee/{id}`,
`/rentals/user/{id}`). Pedido: que `auth/me` (o el UserResource) incluya:
```json
{ "rating": 4.6, "reviewsCount": 12, "tripsCompleted": 8 }
```
(promedio de UserReviews + Reviews donde es reviewee, y count de rentals `completed`).

### 2. Exponer el endpoint de cancelación que ya existe en el dominio
En el código del backend ya están `CancelRentalCommand`, `CancelRentalResource` y
`Rental.Cancel()`, pero **no hay ruta HTTP que los exponga**. Pedido:
`POST /rentals/{id}/cancel { "reason": "..." }` que en una sola transacción:
- valide la política (≥48h→100%, 24-48h→50%, <24h→0%),
- ponga `status=cancelled`,
- cree el Payment `type="refund"` por lo efectivamente pagado.
Hoy la app hace estos 3 pasos por separado (PATCH + GET payments + POST payments);
funciona, pero no es atómico.

### 3. Tracking GPS real (US06)
No existe ningún endpoint de posiciones. La app simula el recorrido localmente.
Pedido mínimo: `GET /rentals/{id}/tracking` → `[{ "lat": -12.04, "lng": -77.04, "timestamp": "..." }]`
y `POST /rentals/{id}/tracking` para que el dispositivo del conductor publique posiciones.
Ideal: SignalR/WebSocket para push en vivo.

### 4. Rechazar reseñas duplicadas
`POST /reviews` permite calificar el mismo rental N veces. La app lo mitiga
marcando `vehicleRated=true` en el rental, pero conviene que el backend devuelva
**409** si ya existe una review con el mismo `rentalId` + `reviewerId`.

### 5. Validación de solapamiento en `POST /rentals`
El backend acepta reservas con fechas ocupadas; la app valida el solapamiento en
el cliente antes de crear. Pedido: devolver **409** cuando `[startDate, endDate)`
se cruce con un rental `pending/accepted/active` del mismo vehículo.

### 6. Aceptación real del propietario (app de owners)
La transición `pending → accepted` es del propietario. En esta app quedó un botón
**demo** ("Aceptar reserva (demo propietario)") que hace el `PATCH`. Cuando la app
de owners esté lista, ese botón se elimina de aquí — el endpoint ya existe, solo
hay que consumirlo desde la otra app (junto con US05 acreditar propiedad, US50
editar vehículo y US55 gestionar reservas recibidas).

### 7. (Opcional) Notificaciones automáticas
Al cambiar el estado de un rental (aceptado/cancelado/reembolsado), crear la
`Notification` para el otro usuario, así la campanita de la app muestra los eventos
sin que la app tenga que crearlos.
