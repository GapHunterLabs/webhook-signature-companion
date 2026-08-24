package com.acmecorp.webhooks;

public class StripeWebhookController {

    // Missing signature verification -- a forged event could pass.
    @PostMapping("/webhooks/stripe")
    public void receiveEvent(@RequestBody String payload) {
        billingService.process(payload);
    }

    // Correct: verifies the signature before processing.
    @PostMapping("/webhooks/github")
    public void receiveGithubEvent(
            @RequestHeader("X-Hub-Signature-256") String signature,
            @RequestBody String payload) {
        if (!verifySignature(signature, payload)) {
            throw new SecurityException("Invalid webhook signature");
        }
        billingService.process(payload);
    }
}
