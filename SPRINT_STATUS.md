# Estado de historias — MOVEO Frontend

Verificado contra el código el 2026-07-03 (actualizado tras implementar US54, US26/US33, US06 y US20 en esta app).
Leyenda: ✅ Hecho · ⚠️ Parcial (UI hecha, sin backend real o sin acción completa) · ❌ No hecho

## Sprint 3 (To Do / Doing / Code Review / Done)

| US | Historia | Estado | Nota |
|----|----------|--------|------|
| US55 | Gestionar estado de reservas recibidas como proveedor | ❌ No hecho | No hay UI de proveedor para aceptar/rechazar reservas (lado owner es mock, fuera del alcance de esta app renter) |
| US54 | Cancelar reserva de alquiler según políticas | ✅ Hecho | Botón "Cancelar reserva" en el detalle con política (≥48h→100%, 24-48h→50%, <24h→0%) vía `PATCH /rentals/{id}` |
| US50 | Editar información y precio de vehículo publicado | ❌ No hecho | MyListingsScreen solo lista, no permite editar |
| US05 | Acreditar propiedad de vehículo | 🔀 Otra app | Flujo de propietario (subir tarjeta de propiedad al publicar): va en la app de owners, igual que US50 y US55 |
| US02 | Verificar identidad mediante KYC | ✅ Hecho | KycScreen sube DNI frente/reverso + selfie vía AuthApi (backend en modo demo) |
| US04 | Recuperar contraseña olvidada | ✅ Hecho | ForgotPasswordScreen conectada a `auth/forgot-password` |
| US06 | Monitorear ruta en tiempo real vía GPS | ✅ Hecho | Marcador del vehículo avanza en vivo por la ruta, cámara lo sigue, barra de progreso y ETA (fuente GPS simulada: el backend no expone tracking) |
| US20 | Confirmar llegada al destino final | ✅ Hecho | Diálogo de confirmación → `PATCH /rentals/{id}` status=completed + completedAt, luego pasa a calificar |
| US25 | Emitir comprobantes y contratos digitales | ❌ No hecho | Sin rastro de comprobantes/contratos en el código |
| US26 | Procesar reembolsos automáticos | ✅ Hecho | Al cancelar, calcula el % según política, verifica lo pagado (`GET /payments/rental/{id}`) y registra el reembolso (`POST /payments` type=refund) |
| US33 | Ejecutar reembolsos automatizados | ✅ Hecho | Mismo flujo automático de US26: sin intervención manual, con resultado mostrado al usuario |
| SP01 | Investigar integraciones de pasarelas de pago | ✅ Hecho | Materializado: pago con tarjeta (Stripe demo) y Yape (`POST /rentals/{id}/pay`) en PaymentScreen |
| SP02 | Evaluar soluciones de GPS y mapas | ✅ Hecho | Materializado: Google Maps Compose integrado (catálogo "más cercano" por GPS, tracking) |

## Sprint 4 / TF (Pendiente)

| US | Historia | Estado | Nota |
|----|----------|--------|------|
| US32 | Liquidar cuota de carpooling digitalmente | ❌ No hecho | |
| US23 | Pagar cuota de asiento compartido de forma digital | ⚠️ Parcial | CarpoolConfirmScreen tiene "Pagar con Yape" pero solo reserva asientos (`api.book`), no procesa pago real |
| US08 | Activar alerta de emergencia durante viaje | ⚠️ Parcial | Botón SOS existe en TripTrackingScreen pero sin acción (`onClick` vacío) |
| US12 | Registrar checklist fotográfico del vehículo | ❌ No hecho | |
| US21 | Vincular métodos de pago electrónicos | ⚠️ Parcial | PaymentMethodsScreen existe pero la lista es mock, no vincula métodos reales |
| US09 | Validar inicio de viaje con código PIN | ❌ No hecho | |
| US11 | Filtrar rutas por preferencia de género | ❌ No hecho | |
| US16 | Aprobar solicitudes de pasajeros y controlar aforo | ❌ No hecho | |
| US51 | Retirar temporalmente vehículo del catálogo público | ❌ No hecho | |
| US30 | Configurar umbrales de reputación mínimos | ❌ No hecho | |
| US10 | Gestionar contactos de confianza | ⚠️ Parcial | UI en SafetyScreen (añadir contacto) pero solo local, sin backend |
| US17 | Automatizar rutas recurrentes semanales | ❌ No hecho | |
| US27 | Aplicar cupones y beneficios promocionales | ❌ No hecho | |
| US29 | Recompensar usuarios con alta reputación | ⚠️ Parcial | RewardsScreen existe pero es UI estática, sin lógica ni backend |
| US34 | Gestionar ofertas promocionales temporales | ❌ No hecho | |
| US36 | Reconocer comportamiento positivo con distintivos | ❌ No hecho | |
| US38 | Filtrar solicitudes por umbral de confianza | ❌ No hecho | |
| US07 | Rastrear viaje activo para seguridad de pasajero | ⚠️ Parcial | Mismo TripTrackingScreen de US06: mapa funcional con datos demo |
| US40 | Monitorear anomalías financieras (Admin) | ❌ No hecho | |
| US41 | Mediar disputas de reputación (Admin) | ❌ No hecho | |
| US45 | Solicitar baja voluntaria y eliminación de datos | ❌ No hecho | |
| US46 | Enviar solicitud de alianza corporativa | ❌ No hecho | |
| SP03 | Analizar opciones de KYC mediante IA | ❌ No hecho | KYC actual es subida manual de fotos, sin IA |
| SP04 | Investigar arquitectura de microservicios y contenedorización | ❌ No hecho | Nada en este repo (sería del backend) |
