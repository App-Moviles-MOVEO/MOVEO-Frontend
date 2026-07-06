package com.example.moveo_frontend.util

/**
 * Motor de cupones (US27). Portado 1:1 del motor de la app Owner
 * (`lib/features/promotions/data/promo_model.dart`), determinístico y puro.
 *
 * El backend aún no expone `/promotions`, así que el catálogo de cupones de
 * campaña vive localmente ([PromoCatalog.offers]). Cuando exista el endpoint,
 * solo cambia la fuente de las ofertas — la lógica de aplicación no se toca.
 */
enum class DiscountType { PERCENT, FIXED }

enum class CouponStatus {
    APPLIED, NOT_FOUND, NOT_STARTED, EXPIRED, DISABLED, REPUTATION_TOO_LOW;

    val isSuccess: Boolean get() = this == APPLIED
}

data class PromoOffer(
    val code: String,
    val title: String,
    val type: DiscountType,
    val value: Double, // porcentaje (0–100) o monto fijo en soles según [type]
    val startMillis: Long,
    val endMillis: Long,
    val minReputation: Double = 0.0,
    val enabled: Boolean = true
) {
    /** Descuento en soles sobre [base] (nunca mayor que [base]). */
    fun discountOn(base: Double): Double {
        val raw = if (type == DiscountType.PERCENT) base * value / 100 else value
        return raw.coerceIn(0.0, base)
    }
}

data class CouponOutcome(
    val status: CouponStatus,
    val discount: Double = 0.0,
    val finalAmount: Double = 0.0,
    val promo: PromoOffer? = null
) {
    /** Mensaje para el usuario según el resultado de aplicar el cupón. */
    fun message(): String = when (status) {
        CouponStatus.APPLIED -> "Cupón aplicado: ${promo?.title ?: "descuento"}"
        CouponStatus.NOT_FOUND -> "Ese cupón no existe"
        CouponStatus.NOT_STARTED -> "Este cupón todavía no está vigente"
        CouponStatus.EXPIRED -> "Este cupón ya venció"
        CouponStatus.DISABLED -> "Este cupón no está disponible"
        CouponStatus.REPUTATION_TOO_LOW -> "Tu reputación no alcanza para este cupón"
    }
}

object PromoCatalog {
    // Cupones de campaña de la plataforma (mientras no exista el endpoint de promos).
    val offers: List<PromoOffer> = listOf(
        PromoOffer("MOVEO10", "10% de descuento", DiscountType.PERCENT, 10.0, 0L, Long.MAX_VALUE),
        PromoOffer("BIENVENIDA20", "20% de bienvenida", DiscountType.PERCENT, 20.0, 0L, Long.MAX_VALUE),
        PromoOffer("VERANO15", "15% de verano", DiscountType.PERCENT, 15.0, 0L, Long.MAX_VALUE),
        PromoOffer("MOVEO25", "S/ 25 de descuento", DiscountType.FIXED, 25.0, 0L, Long.MAX_VALUE)
    )

    fun apply(
        code: String,
        amount: Double,
        reputation: Double = 0.0,
        now: Long = System.currentTimeMillis()
    ): CouponOutcome {
        val normalized = code.trim().uppercase()
        val match = offers.firstOrNull { it.code.trim().uppercase() == normalized }
            ?: return CouponOutcome(CouponStatus.NOT_FOUND)
        if (!match.enabled) return CouponOutcome(CouponStatus.DISABLED, promo = match)
        if (now < match.startMillis) return CouponOutcome(CouponStatus.NOT_STARTED, promo = match)
        if (now > match.endMillis) return CouponOutcome(CouponStatus.EXPIRED, promo = match)
        if (reputation < match.minReputation) return CouponOutcome(CouponStatus.REPUTATION_TOO_LOW, promo = match)
        val discount = match.discountOn(amount)
        return CouponOutcome(
            status = CouponStatus.APPLIED,
            discount = discount,
            finalAmount = (amount - discount).coerceIn(0.0, amount),
            promo = match
        )
    }
}
