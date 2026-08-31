# Webhook Signature Companion

Gutter warning icon on any Java/Kotlin Spring MVC endpoint whose mapping
path contains "webhook" with no signature-verification signal anywhere
in the handler body — the classic footgun where anyone who guesses (or
finds leaked) the endpoint URL can send a forged event, because nothing
checks the payload really came from the real sender.

## Why it exists

Stripe, GitHub, Slack, and most real webhook providers document
signature verification as a required step, but nothing in the IDE
flags a handler that skips it — it's a code-review-only discipline
today, easy to forget when copy-pasting a new webhook receiver from an
old one.

## Why built this way

- **100% static text/PSI analysis** — matches the mapping path and a
  broad set of signature-verification signals by simple text (method
  names like `verifySignature`, or raw text like `hmac`/
  `Mac.getInstance`/Stripe's own official `Webhook.constructEvent`
  helper), so it works whether the real crypto library is on the
  classpath or not.
- **Deliberately broad on what counts as "verified"** — any call whose
  name looks like a verification helper is accepted; this plugin
  doesn't try to confirm the helper's logic is actually *correct*, only
  that a real attempt exists. A false negative (missing a real check
  written unusually) is a far cheaper mistake than a false positive
  nagging correct code.

## v0.1 scope — stated honestly, not exhaustively

Only recognizes "webhook" literally in the mapping path — an endpoint
named differently (e.g. `/events/stripe`) isn't covered. Spring MVC
only, not JAX-RS or other frameworks.

## Usage

Open any Java/Kotlin Spring controller. A webhook endpoint with no
verification signal in its body shows a warning icon on the method
name.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
