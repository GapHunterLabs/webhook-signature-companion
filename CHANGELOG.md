<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Webhook Signature Companion Changelog

## [Unreleased]

## [0.2.0]

### Fixed

- Recognizes Stripe's own official `Webhook.constructEvent(...)`
  verification helper -- previously a false positive, since it
  contains none of the other signal words. This was the single most
  canonical, recommended way to do exactly what this plugin checks for,
  for one of the three providers this plugin's own README names.

## [0.1.1]

### Added

- Review/star CTA: after 10 distinct real findings, a one-time
  notification asks whether to rate the plugin on Marketplace, with a
  permanent "Don't ask again" option. Standard mechanism used
  catalog-wide since 2026-08-24, rolled out
  to this plugin now.

## [0.1.0]

### Added

- Gutter warning icon on any Java/Kotlin Spring MVC endpoint whose
  mapping path contains "webhook" with no signature-verification
  signal anywhere in the handler body.
- 100% static text/PSI analysis, Java and Kotlin, no network calls, no
  telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/webhook-signature-companion/compare/0.2.0...HEAD
[0.2.0]: https://github.com/GapHunterLabs/webhook-signature-companion/compare/0.1.1...0.2.0
[0.1.1]: https://github.com/GapHunterLabs/webhook-signature-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/webhook-signature-companion/commits/0.1.0
