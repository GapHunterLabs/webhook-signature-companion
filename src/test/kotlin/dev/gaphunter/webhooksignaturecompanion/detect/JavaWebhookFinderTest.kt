package dev.gaphunter.webhooksignaturecompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class JavaWebhookFinderTest : BasePlatformTestCase() {

    fun `test a webhook endpoint with no verification signal is flagged`() {
        val file = myFixture.configureByText(
            "WebhookController.java",
            """
            class WebhookController {
                @PostMapping("/webhooks/stripe")
                void receiveEvent(@RequestBody String payload) {
                    orderService.process(payload);
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaWebhookFinder.findAll(file).size)
    }

    fun `test a webhook endpoint calling a verifySignature-named helper is not flagged`() {
        val file = myFixture.configureByText(
            "WebhookController.java",
            """
            class WebhookController {
                @PostMapping("/webhooks/stripe")
                void receiveEvent(@RequestHeader("Stripe-Signature") String sig, @RequestBody String payload) {
                    if (!verifySignature(sig, payload)) throw new SecurityException();
                    orderService.process(payload);
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaWebhookFinder.findAll(file).isEmpty())
    }

    fun `test a webhook endpoint using Mac-getInstance inline is not flagged`() {
        val file = myFixture.configureByText(
            "WebhookController.java",
            """
            class WebhookController {
                @PostMapping("/webhooks/github")
                void receiveEvent(@RequestBody String payload) throws Exception {
                    Mac mac = Mac.getInstance("HmacSHA256");
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaWebhookFinder.findAll(file).isEmpty())
    }

    fun `test a webhook endpoint using Stripe's official Webhook-constructEvent is not flagged`() {
        val file = myFixture.configureByText(
            "WebhookController.java",
            """
            class WebhookController {
                @PostMapping("/webhooks/stripe")
                void receiveEvent(@RequestHeader("Stripe-Signature") String sig, @RequestBody String payload) throws Exception {
                    Event event = Webhook.constructEvent(payload, sig, endpointSecret);
                    orderService.process(event);
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaWebhookFinder.findAll(file).isEmpty())
    }

    fun `test a non-webhook endpoint is never flagged, even with no verification`() {
        val file = myFixture.configureByText(
            "OrderController.java",
            """
            class OrderController {
                @PostMapping("/orders")
                void createOrder(@RequestBody String payload) {
                    orderService.process(payload);
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaWebhookFinder.findAll(file).isEmpty())
    }

    fun `test webhook matching is case-insensitive`() {
        val file = myFixture.configureByText(
            "WebhookController.java",
            """
            class WebhookController {
                @PostMapping("/Webhooks/Stripe")
                void receiveEvent(@RequestBody String payload) {
                    orderService.process(payload);
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaWebhookFinder.findAll(file).size)
    }
}
