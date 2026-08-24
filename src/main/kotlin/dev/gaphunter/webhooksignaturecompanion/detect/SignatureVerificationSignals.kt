package dev.gaphunter.webhooksignaturecompanion.detect

/**
 * Text signals this plugin accepts as evidence that a webhook handler
 * body already verifies a signature -- deliberately broad substring
 * matches (not resolved calls) over the method body's own text, same
 * "match a known name, don't resolve a symbol" discipline as
 * `SqlSignalNames`/`HttpSignalNames` elsewhere in this catalog. A
 * method calling *any* helper whose own name contains one of these is
 * treated as handled -- this plugin doesn't try to verify the helper
 * itself does the check correctly, only that a real attempt exists.
 */
object SignatureVerificationSignals {

    private val METHOD_NAME_FRAGMENTS = listOf(
        "verifysignature",
        "validatesignature",
        "checksignature",
        "verifyhmac",
        "validatehmac",
        "isvalidsignature",
        "verifywebhook",
    )

    /** Broader, single-word signals that show up as literals/API calls even outside a named helper method (e.g. `Mac.getInstance("HmacSHA256")` inlined directly in the handler). */
    private val RAW_TEXT_FRAGMENTS = listOf("hmac", "mac.getinstance", "signature")

    fun bodyLooksVerified(bodyText: String): Boolean {
        val lower = bodyText.lowercase()
        if (METHOD_NAME_FRAGMENTS.any { lower.contains(it) }) return true
        return RAW_TEXT_FRAGMENTS.any { lower.contains(it) }
    }
}
