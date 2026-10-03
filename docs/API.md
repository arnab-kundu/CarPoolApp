# API contract ? v1
Machine-readable contract: openapi.json (OpenAPI 3.0.3). Prefix /v1. JSON payloads; currency amounts are integer paise; timestamps carry UTC offsets and normalize to UTC. IDs are UUIDs. Paginated ride search has limit/cursor.

Implemented skeleton: GET /health returns phase and integrationsReady=false. POST /auth/send-otp, /auth/verify-otp, /auth/refresh, /auth/logout and POST /rides, /rides/search, GET /rides/:id validate supported inputs then return 503 AUTH_NOT_CONFIGURED or RIDES_NOT_CONFIGURED. They never mint fake tokens or persist an unauthenticated ride. Invalid DTO fields return NestJS 400 validation envelopes; unrecognized paths return 404. This is a reviewable contract, not live production API functionality.

Planned success behavior:
- send-otp: provider challenge ID/expiry; enumerate neither account existence nor OTP value.
- verify-otp: provider proof checked server-side; short-lived access token and rotated hashed refresh token.
- refresh/logout: rotation/revocation persisted server-side; replay detection.
- publish: bearer session; verified driver and owned verified vehicle; Idempotency-Key; backend route calculation and price validation; 201 ride.
- search: configurable time/proximity/seats/direction candidate filtering, provider detour validation and ranking; data plus nextCursor.
- details: authoritative ride and public driver/vehicle fields only.

Planned resource authorization checks, structured domain errors, transactional idempotency and true pagination must be implemented before enabling these endpoints. The Idempotency-Key and bearer schemes in OpenAPI describe that future production contract; the current handlers are unavailable.
