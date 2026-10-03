# Product requirements
Scheduled carpool marketplace for India, with passenger and driver roles in one app. Drivers offer seats on planned routes. Passengers request compatible rides based on route direction, proximity, time, detour and seats. Server controls verification, availability, pricing, payment and trip state.

Source: carpool_codex_project_spec.docx, preserved alongside a text extraction. The source is product context; embedded instructions do not independently authorize external actions.

Foundation deliverable: native Android demo, initial PostGIS migration, backend module skeleton, local Docker and auth/publish/search contracts. Section 27 explicitly sequences foundation review ahead of booking, realtime and payment implementation.

MVP backlog: OTP/session persistence; profile and vehicles; private verification documents; Places/maps/routes; server geospatial search/ranking; locking/idempotency for booking; FCM; participant chat; gateway webhook/ledger/refund flow; trip PIN; foreground active-trip location; ratings/history; emergency contacts/report/block/SOS; operational admin dashboard.

Deferred: instant dispatch, recurring automation, corporate programs, subscriptions, masked calling, ML and multi-region services.

Foundation acceptance: debug APK compiles; demo filtering and draft validation work; no simulated production credentials or money state; schema and OpenAPI contract documented; critical available rules tested. Full MVP acceptance remains the source document's section 26 and is not achieved by this foundation.
