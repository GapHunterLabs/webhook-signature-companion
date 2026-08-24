# Demo data for screenshots

`StripeWebhookController.java` — `receiveEvent` is missing signature
verification (should show a warning), `receiveGithubEvent` has it
correctly (should not).

## How to get the screenshot

1. `./gradlew runIde` from `webhook-signature-companion`, open this
   `demo/` folder as the project.
2. Full Screen, open `StripeWebhookController.java` — a warning icon
   should appear on `receiveEvent` but not on `receiveGithubEvent`.
3. Screenshot with both methods and the icon contrast visible, save
   into `webhook-signature-companion/docs/screenshots/`. Close the
   sandbox.
