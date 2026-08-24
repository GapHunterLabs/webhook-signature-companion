package dev.gaphunter.webhooksignaturecompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class KotlinWebhookFinderTest : BasePlatformTestCase() {

    fun `test a webhook endpoint with no verification signal is flagged`() {
        val file = myFixture.configureByText(
            "WebhookController.kt",
            """
            class WebhookController {
                @PostMapping("/webhooks/stripe")
                fun receiveEvent(@RequestBody payload: String) {
                    orderService.process(payload)
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinWebhookFinder.findAll(file).size)
    }

    fun `test a webhook endpoint calling a verifySignature-named helper is not flagged`() {
        val file = myFixture.configureByText(
            "WebhookController.kt",
            """
            class WebhookController {
                @PostMapping("/webhooks/stripe")
                fun receiveEvent(@RequestHeader("Stripe-Signature") sig: String, @RequestBody payload: String) {
                    if (!verifySignature(sig, payload)) throw SecurityException()
                    orderService.process(payload)
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinWebhookFinder.findAll(file).isEmpty())
    }

    fun `test a non-webhook endpoint is never flagged, even with no verification`() {
        val file = myFixture.configureByText(
            "OrderController.kt",
            """
            class OrderController {
                @PostMapping("/orders")
                fun createOrder(@RequestBody payload: String) {
                    orderService.process(payload)
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinWebhookFinder.findAll(file).isEmpty())
    }
}
