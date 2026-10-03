# Matching
Backend owns matching. Demo Android filtering is text/date/seats only and is never production matching.
Candidate query: backend/src/modules/matching/candidate-query.sql. Filters PUBLISHED, time window, available seats, both points near route using ST_DWithin, and pickup-before-drop using ST_LineLocatePoint. Use longitude latitude WKT, bound parameters, indexes, and bounded result count. Never concatenate untrusted coordinates into SQL.
Initial configuration (database row, tunable): 2000m proximity, +/-30min, maximum 600s detour. Candidates still need external detour estimates and scoring from pickup/drop distances, departure compatibility, detour and overlap. Provider failures must not produce invented route scores. Keyset pagination, looped-route edge cases, antimeridian cases, performance testing and ranking remain pending.
The integration test checks basic proximity/direction only; it does not claim end-to-end production matching.
