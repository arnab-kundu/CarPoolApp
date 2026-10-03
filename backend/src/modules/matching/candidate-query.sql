-- Parameters: pickup WKT $1, dropoff WKT $2, earliest $3, latest $4,
-- requested seats $5, configurable proximity metres $6, limit $7.
-- Candidate reduction ONLY. Routes provider must validate detour and ranking.
WITH points AS (
 SELECT ST_GeomFromText($1,4326) AS pickup, ST_GeomFromText($2,4326) AS dropoff
)
SELECT r.id,
 ST_Distance(rr.geometry::geography,p.pickup::geography) AS pickup_distance_m,
 ST_Distance(rr.geometry::geography,p.dropoff::geography) AS dropoff_distance_m
FROM rides r JOIN ride_routes rr ON rr.ride_id=r.id CROSS JOIN points p
WHERE r.status='PUBLISHED' AND r.departure_time BETWEEN $3 AND $4
 AND r.available_seats >= $5
 AND ST_DWithin(rr.geometry::geography,p.pickup::geography,$6)
 AND ST_DWithin(rr.geometry::geography,p.dropoff::geography,$6)
 AND ST_LineLocatePoint(rr.geometry,p.pickup) < ST_LineLocatePoint(rr.geometry,p.dropoff)
ORDER BY r.departure_time,r.id LIMIT $7;
