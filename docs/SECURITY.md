# Security boundaries
No production secrets or provider keys are included. Android foundation has no network or location permissions. Demo fixture badges are sample data, not trusted verification state. Production server must verify every session and resource entitlement.

Future requirements: TLS; revocable rotated sessions; provider-verified phone auth; per-resource authorization; OTP/search/chat/booking/payment/location limits; private document storage and encryption; restricted Maps keys; server-side route/price verification; transactional seats; idempotent signed webhooks; minimized active-trip location; retention and account deletion; redacted audit/structured logging; operational alerting.

Current rate limiter is in-memory and is not a production distributed control. API handlers fail closed until adapters are implemented. Logs omit request bodies and credentials. Authentication, infrastructure hardening, Sentry and backup/restore remain unimplemented.
