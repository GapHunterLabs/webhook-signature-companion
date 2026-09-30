# Demo data

`StripeWebhookController.java` — `receiveEvent` is missing signature
verification (should show a warning), `receiveGithubEvent` has it
correctly (should not).

## Trying it by hand

1. `./gradlew runIde` from `webhook-signature-companion`, open this
   `demo/` folder as the project.
2. Open `StripeWebhookController.java`: a warning icon appears in the
   gutter of `receiveEvent` and not of `receiveGithubEvent`.

The recorded media (hero GIF, clips and cover) live in `docs/media/`.
