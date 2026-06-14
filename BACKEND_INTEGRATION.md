# MOVEO — Ajustes del Backend para integrar la App móvil (apartado Cliente / Renter)

> Documento para el equipo de **backend (.NET 9 / MySQL)**.
> Resume **qué cambiar, qué agregar, qué quitar y qué corregir en la base de datos** para que el
> backend funcione **al 100% con la app móvil Android** en el flujo del **cliente/arrendatario (renter)**.
>
> Está escrito comparando el contrato real que **espera la app** (sus DTOs/llamadas Retrofit) contra
> la documentación actual del backend. Donde el cambio es más fácil hacerlo en la app, se indica
> explícitamente con **[lo ajusta la app]**.

---

## 0. TL;DR — Checklist mínimo para que el cliente funcione

| # | Cambio | Dónde | Prioridad |
|---|--------|-------|-----------|
| 1 | Devolver `ownerName` en `VehicleResource` y `vehicleName` en `RentalResource` (datos ya unidos por JOIN) | Backend | 🔴 Alta |
| 2 | Aceptar `paymentMethod: "yape"` en `/payments` | Backend | 🔴 Alta |
| 3 | Endpoint para crear el pago de una reserva en 1 paso (ver §6) | Backend | 🔴 Alta |
| 4 | Definir qué pasa con **Carpooling** (la app lo tiene; el backend NO) — ver §8 | Ambos | 🔴 Alta |
| 5 | `GET /vehicles` con filtro por tipo de carrocería (`bodyType`) o quitar el filtro de la app | Ambos | 🟡 Media |
| 6 | (Opcional) Endpoint Stripe PaymentIntent si se quiere pasarela real | Backend | 🟢 Baja |
| 7 | Usar **Migraciones EF Core**, NO `EnsureCreated`, en producción | Backend | 🔴 Alta |
| 8 | No crear tablas para módulos que el cliente no usa (ver §11) | Backend | 🟡 Media |

La app se adapta a: **IDs enteros**, **fechas ISO 8601**, **sin token JWT** (manda `userId`), y **dinero decimal**. Eso es trabajo del lado app **[lo ajusta la app]**.

---

## 1. URL base y prefijo

- El backend expone todo bajo **`/api/v1`**.
- **[lo ajusta la app]** La app pondrá `BASE_URL = http://10.0.2.2:5128/api/v1/` (emulador) — antes apuntaba a `:8080/` sin prefijo.
- ⚠️ Confirmar el **puerto público de Railway** y la **URL final de producción** para ponerla en la app.

**Acción backend:** ninguna, solo confirmar la URL pública de prod.

---

## 2. Autenticación — el cambio más importante de contrato

### Lo que la app esperaba (viejo) vs lo que el backend hace (real)

| Tema | App esperaba | Backend real | Resolución |
|------|--------------|--------------|------------|
| Respuesta de login/register | `{ token, user }` | El **objeto usuario directo** (sin `token`) | **[lo ajusta la app]** quitar `token`, leer el usuario directo |
| Sesión | header `Authorization: Bearer <token>` | **No hay token**, se manda `userId` por query | **[lo ajusta la app]** guardar `id` y mandarlo como `?userId=` |
| Registro | `{ name, email, phone, password, role }` | `{ firstName, lastName, email, password, phone?, dni?, licenseNumber?, address?, role }` | **[lo ajusta la app]** separar nombre en `firstName`/`lastName` |
| `role` | enum `PROVIDER/RENTER/PASSENGER` | `"renter"` / `"owner"` | **[lo ajusta la app]** usar `"renter"`/`"owner"` |
| `GET /auth/me` | sin parámetros | requiere `?userId={id}` | **[lo ajusta la app]** |

**Acción backend (recomendada, opcional pero importante para producción):**
- Mantén `/auth/login` y `/auth/register` como están (la app se adapta).
- ⚠️ **Recomendación fuerte:** agregar **JWT** más adelante (ver §10). Mandar el `userId` en claro como query param permite que cualquiera consulte/modifique datos de otro usuario (ver §9 de tu doc, "Validación de propiedad"). Para una demo está bien; para producción **no**.

---

## 3. Convenciones que la app adoptará **[lo ajusta la app]**

- **IDs = enteros** (`int`). La app cambiará sus `id: String` a `Int`.
- **Fechas = ISO 8601 UTC** (`2026-06-10T09:00:00Z`). La app dejará de mandar `"yyyy-MM-dd"` y mandará ISO completo.
- **Dinero = decimal** (`dailyPrice`, `totalPrice`, `amount`). La app usará `Double`/`BigDecimal` en vez de `Int`.

**Acción backend:** ninguna. Solo **ser consistente**: que TODOS los endpoints devuelvan fechas en el mismo formato ISO y montos como número decimal (no string).

---

## 4. Vehículos (`/vehicles`) — mapeo y un cambio necesario

La app muestra una **tarjeta de vehículo** con: marca, modelo, año, **precio/día**, **rating**, **nombre del dueño**, **ubicación (texto)**, transmisión, asientos, combustible, tipo.

| Campo que muestra la app | En `VehicleResource` | Nota |
|--------------------------|----------------------|------|
| `pricePerDay` | `dailyPrice` | **[app]** renombrar |
| `fuel` | `fuelType` | **[app]** |
| `location` (texto) | `location.district` + `location.address` | **[app]** concatenar |
| `lat`, `lng` | `location.lat/lng` | ok |
| `ownerName` | ❌ **no existe** (solo `ownerId`) | 🔴 **ver abajo** |
| `rating` | ❌ **no existe** en vehículo | 🔴 **ver abajo** |
| `type` (Compacto/Sedán/SUV) | ❌ no existe | 🟡 ver §5 |

### 🔴 Cambios pedidos al backend
1. **`ownerName`**: agregar al `VehicleResource` el nombre del propietario (un `JOIN` con `Users` por `ownerId`). La app necesita mostrar "Rosa M." sin tener que hacer una segunda llamada por cada auto.
2. **`rating`** y **`reviewsCount`** del vehículo: calcularlos desde `reviews` (promedio de `rating` donde `vehicleId = X`) y devolverlos en `VehicleResource`. Hoy el rating no se devuelve.
3. Devolver `images` siempre como `[]` (no `null`) para evitar nulls en la app.

> Esto **no requiere tablas nuevas**, solo incluir/computar datos al serializar (EF `.Include()` / proyección).

---

## 5. Tipo de carrocería (filtro del catálogo)

La app filtra el catálogo por **Compacto / Sedán / SUV**. El backend no tiene ese campo.

**Opciones (elige una):**
- **(A)** Agregar `bodyType` (string) a `Vehicle` y soportar `GET /vehicles?bodyType=SUV`.
- **(B) [lo ajusta la app]** Quitar ese filtro de la app y filtrar solo por precio/distrito (que el backend sí soporta).

Recomendado: **(A)** si es barato; si no, **(B)**.

---

## 6. Alquileres + Pagos — flujo del cliente (el corazón del apartado renter)

### Flujo real en la app (pantalla de pago)
1. El usuario elige fechas y número de días.
2. Toca **"Pagar con tarjeta"** (Stripe) **o** **"Pagar con Yape (rápido)"**.
3. Se crea la **reserva** (`POST /rentals`) y luego el **pago** (`POST /payments`).

### Lo que la app mandaba vs lo que el backend pide

**Crear reserva** — el backend (`CreateRentalResource`) necesita MÁS campos de los que la app mandaba:

| Campo backend | ¿La app lo tiene? | Resolución |
|---------------|-------------------|------------|
| `vehicleId` | sí | ok |
| `renterId` | sí (es el user logueado) | **[app]** mandarlo |
| `ownerId` | viene del vehículo | **[app]** tomarlo de `vehicle.ownerId` |
| `startDate`/`endDate` | sí (pasar a ISO) | **[app]** |
| `totalPrice` | sí (lo calcula la app) | **[app]** mandarlo |
| `pickupLocation`/`returnLocation` | usar la ubicación del vehículo | **[app]** |

**🔴 Cambio pedido al backend:** que `RentalResource` devuelva **`vehicleName`** (marca + modelo) y opcionalmente **`vehicleImage`** vía JOIN con `Vehicles`. La app lista "Mis reservas" mostrando el nombre del vehículo; hoy el backend solo da `vehicleId`.

**Crear pago** (`POST /payments`):
- 🔴 **Aceptar `paymentMethod: "yape"`** (además de `card`/`cash`/`transfer`). El botón Yape de la app manda exactamente eso.
- La app mandará: `{ payerId, recipientId(ownerId), rentalId, amount, currency:"PEN", paymentMethod:"yape", type:"rental_payment", status:"completed" }`.

**🔴 Sugerencia para simplificar (1 endpoint):** hoy el cliente debe hacer 2 llamadas (crear rental + crear payment) y enlazarlas. Sería más robusto un endpoint:
```
POST /api/v1/rentals/{id}/pay
Body: { "paymentMethod": "yape", "amount": 240.00 }
→ crea el Payment, lo enlaza al rental y devuelve { rental, payment }
```
Opcional, pero evita estados inconsistentes (reserva creada sin pago).

### Stripe (pasarela real) — opcional
La app ya integra **Stripe PaymentSheet**. Para que funcione de forma segura necesita que el **backend** cree el *PaymentIntent* (la secret key NUNCA va en la app):
```
POST /api/v1/payments/intent
Body: { "amount": 24000, "currency": "pen", "rentalId": 100 }   // amount en céntimos
→ 200 { "clientSecret": "pi_..._secret_..." }
```
- Internamente: `StripeConfiguration.ApiKey = sk_live/sk_test`; `new PaymentIntentService().Create(...)`.
- Si **no** se implementa, la app puede usar **solo el botón Yape** (pago directo) para todas las pruebas. **Por eso se agregó Yape: para no depender de Stripe en cada prueba.**

---

## 7. Reseñas y Notificaciones (mapeo menor)

- **Reseñas recibidas (perfil):** la app usa `GET /reviews/me`. En el backend equivale a **`GET /api/v1/user-reviews?reviewedUserId={id}`** (reseñas entre usuarios) y/o `GET /reviews/reviewee/{id}`. **[lo ajusta la app]** apuntar a esos. 🔴 **Pedido:** que la reseña devuelva el **nombre del autor** (`reviewerName`) vía JOIN, no solo `reviewerId`.
- **Notificaciones:** `GET /notifications/user/{userId}`. **[lo ajusta la app]**. El campo `time` relativo ("hace 5 min") lo calcula la app a partir de `createdAt` ISO. ✅ sin cambio backend.

---

## 8. 🔴 Carpooling — el mayor desajuste a decidir

La app tiene un módulo completo de **Carpooling** (buscar rutas, publicar viaje, reservar asiento, confirmar compra, chat con el conductor). **El backend NO tiene endpoints de carpooling** — lo más parecido es **`/adventure-routes`**, que es otra cosa (rutas turísticas asociadas a un alquiler).

**Hay que elegir una de estas tres rutas:**

- **(A) El backend agrega un módulo Carpool** con su tabla `carpool_routes` y `carpool_bookings`:
  - `GET /carpool-routes?onlyWomen=&verified=`, `GET /carpool-routes/{id}`, `POST /carpool-routes`, `POST /carpool-routes/{id}/book`.
  - Campos por ruta: `driverId, origin, destination, date, time, seatsAvailable, pricePerSeat, vehicleModel, community, onlyWomen, lat/lng`.
- **(B) [lo ajusta la app]** Mapear el "carpool" de la app sobre **`/adventure-routes`** (reusar esa tabla). Funciona si aceptas que "ruta de carpool" = "adventure route". Requiere alinear campos (origin/destination ≈ startLocation/endLocation, pricePerSeat ≈ estimatedCost, etc.).
- **(C)** Dejar Carpooling como **feature solo-mock en la app** (no toca backend) y enfocar la integración real solo en alquiler de autos.

> **Recomendación:** si el objetivo es "apartado cliente funcionando al 100%", lo más rápido es **(C)** ahora y **(A)** después. Avísame cuál eligen y ajusto la app.

---

## 9. Chat y KYC — funciones de la app sin backend

- **Chat** (`/chats/...`): la app tiene chat con el dueño/conductor. El backend no lo tiene (lo más cercano son los mensajes de `support-tickets`, que es soporte, no chat 1-a-1).
  - Opción: backend agrega `GET/POST /messages?between=a,b`, o se deja el chat como mock por ahora.
- **KYC** (`POST /auth/kyc` con fotos): el backend no recibe archivos (las imágenes son URLs). El usuario tiene flags `verified.{email,phone,dni,license}`.
  - Opción: por ahora la app marca KYC como demo; cuando definan storage (S3/Cloudinary) se sube y se setean los flags vía `PATCH /users/{id}`.

**Acción backend:** decidir si entran ahora o quedan para después (no bloquean el flujo de alquiler).

---

## 10. Seguridad (recomendaciones para producción)

1. **JWT**: emitir token en login y proteger endpoints. Hoy cualquier cliente puede mandar el `userId` que quiera.
2. **Validar propiedad**: que `renterId`/`ownerId` del body coincidan con el usuario autenticado.
3. **HTTPS** obligatorio en producción (Railway ya lo da).
4. **No exponer `password`/hash** en ninguna respuesta (verificar `UserResource`).

---

## 11. 🔴 Base de datos — "no crear tablas por las puras"

### Usa Migraciones, no `EnsureCreated`
Tu doc dice que al arrancar ejecuta **`EnsureCreated`**. Eso:
- **No aplica cambios** de esquema si la tabla ya existe (te obliga a borrar la BD o crear tablas a mano cuando cambia un modelo) → justo el problema que quieres evitar.
- **Acción:** cambiar a **EF Core Migrations**:
  ```bash
  dotnet ef migrations add InitialCreate
  dotnet ef database update
  ```
  Y en `Program.cs` u
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- 
- sar `db.Database.Migrate();` en lugar de `EnsureCreated()`. Así el esquema evoluciona sin recrear tablas ni perder datos.

### Tablas que el **cliente (renter)** realmente usa
Crea/mantén tablas SOLO para lo que se usa en el flujo cliente:

| Entidad | ¿La usa el cliente? | Notas |
|---------|---------------------|-------|
| `Users` | ✅ | login, perfil, verificaciones |
| `Vehicles` | ✅ | catálogo, detalle |
| `Rentals` | ✅ | reservas |
| `Payments` | ✅ | pagos (incluido Yape) |
| `Reviews` / `UserReviews` | ✅ | reseñas recibidas/dadas |
| `Notifications` | ✅ | notificaciones |
| `SupportTickets` (+ messages) | 🟡 | solo si activas Ayuda/soporte real |
| `AdventureRoutes` | ❓ | solo si eliges la opción (B) de §8; si no, **no crear** |
| `CarpoolRoutes` | ❓ | solo si eliges la opción (A) de §8 |

> Si Carpooling queda como mock (opción C) y no usas rutas de aventura, **no generes esas tablas**: ahorra esquema y mantenimiento.

### Integridad referencial (evita datos duplicados)
Define las **foreign keys** para no duplicar nombres/datos y poder hacer los JOIN que pide la app:
- `Rentals.vehicleId → Vehicles.id`, `Rentals.renterId/ownerId → Users.id`
- `Payments.rentalId → Rentals.id`, `Payments.payerId/recipientId → Users.id`
- `Reviews.vehicleId → Vehicles.id`, `Reviews.reviewerId/revieweeId → Users.id`
- `Notifications.userId → Users.id`

Así `ownerName`, `vehicleName`, `reviewerName` se obtienen con `Include()/JOIN` y **no** se guardan repetidos.

---

## 12. Resumen de acciones concretas para el backend

🔴 **Imprescindible para el cliente:**
1. `VehicleResource`: agregar `ownerName`, `rating`, `reviewsCount` (vía JOIN/cálculo).
2. `RentalResource`: agregar `vehicleName` (vía JOIN).
3. `/payments`: aceptar `paymentMethod: "yape"`.
4. (Recom.) endpoint `POST /rentals/{id}/pay` para pago en 1 paso.
5. Migraciones EF Core en vez de `EnsureCreated`.
6. Decidir Carpooling (§8) y avisar a la app.

🟡 **Deseable:**
7. `bodyType` en vehículos + filtro.
8. `reviewerName` en reseñas.
9. Definir Chat/KYC.

🟢 **Opcional / producción:**
10. `POST /payments/intent` (Stripe seguro).
11. JWT + validación de propiedad.

---

*Cualquier campo extra o endpoint que la app necesite se irá afinando aquí. Cuando definan lo de Carpooling (§8) y Stripe vs Yape (§6), la app se termina de alinear.*
