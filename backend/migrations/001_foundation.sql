CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE TYPE verification_status AS ENUM ('NOT_SUBMITTED','SUBMITTED','UNDER_REVIEW','VERIFIED','REJECTED','EXPIRED');
CREATE TYPE ride_status AS ENUM ('DRAFT','PUBLISHED','IN_PROGRESS','COMPLETED','CANCELLED','EXPIRED');
CREATE TABLE users (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
 phone_e164 varchar(16) NOT NULL UNIQUE CHECK (phone_e164 ~ '^\+[1-9][0-9]{7,14}$'),
 status text NOT NULL DEFAULT 'ACTIVE' CHECK(status IN ('ACTIVE','SUSPENDED','DELETED')),
 created_at timestamptz NOT NULL DEFAULT now(), updated_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE user_profiles (
 user_id uuid PRIMARY KEY REFERENCES users(id), display_name varchar(100) NOT NULL,
 photo_storage_key text, created_at timestamptz NOT NULL DEFAULT now(), updated_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE sessions (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), user_id uuid NOT NULL REFERENCES users(id),
 refresh_token_hash text NOT NULL UNIQUE, expires_at timestamptz NOT NULL, revoked_at timestamptz,
 created_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX sessions_user_idx ON sessions(user_id);
CREATE TABLE otp_challenges (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), phone_e164 varchar(16) NOT NULL,
 provider_reference text NOT NULL, expires_at timestamptz NOT NULL, consumed_at timestamptz,
 attempts integer NOT NULL DEFAULT 0 CHECK(attempts BETWEEN 0 AND 5), created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE user_devices (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), user_id uuid NOT NULL REFERENCES users(id),
 push_token text NOT NULL UNIQUE, created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE user_verifications (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), user_id uuid NOT NULL REFERENCES users(id),
 kind text NOT NULL CHECK(kind IN ('DRIVER','IDENTITY')), status verification_status NOT NULL DEFAULT 'NOT_SUBMITTED',
 private_document_key text, provider_reference text, reviewed_by uuid REFERENCES users(id), reviewed_at timestamptz,
 expires_at timestamptz, created_at timestamptz NOT NULL DEFAULT now(), updated_at timestamptz NOT NULL DEFAULT now(),
 UNIQUE(user_id,kind)
);
CREATE TABLE vehicles (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), owner_id uuid NOT NULL REFERENCES users(id),
 registration_number varchar(30) NOT NULL UNIQUE, make varchar(80) NOT NULL, model varchar(80) NOT NULL,
 color varchar(50), passenger_seats smallint NOT NULL CHECK(passenger_seats BETWEEN 1 AND 6),
 verification_status verification_status NOT NULL DEFAULT 'NOT_SUBMITTED',
 created_at timestamptz NOT NULL DEFAULT now(), updated_at timestamptz NOT NULL DEFAULT now(),
 UNIQUE(id,owner_id)
);
CREATE TABLE vehicle_documents (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), vehicle_id uuid NOT NULL REFERENCES vehicles(id),
 kind text NOT NULL CHECK(kind IN ('REGISTRATION','INSURANCE','OTHER')), private_storage_key text NOT NULL,
 status verification_status NOT NULL DEFAULT 'SUBMITTED', expires_at timestamptz,
 created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE rides (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), driver_id uuid NOT NULL REFERENCES users(id), vehicle_id uuid NOT NULL,
 origin_place_id text NOT NULL, origin_location geography(Point,4326) NOT NULL, origin_address text NOT NULL,
 destination_place_id text NOT NULL, destination_location geography(Point,4326) NOT NULL, destination_address text NOT NULL,
 departure_time timestamptz NOT NULL, total_seats smallint NOT NULL CHECK(total_seats BETWEEN 1 AND 6),
 available_seats smallint NOT NULL CHECK(available_seats >= 0 AND available_seats <= total_seats),
 price_per_seat_minor bigint NOT NULL CHECK(price_per_seat_minor >= 0), currency char(3) NOT NULL DEFAULT 'INR' CHECK(currency='INR'),
 status ride_status NOT NULL DEFAULT 'DRAFT', version integer NOT NULL DEFAULT 1,
 created_at timestamptz NOT NULL DEFAULT now(), updated_at timestamptz NOT NULL DEFAULT now(),
 FOREIGN KEY(vehicle_id,driver_id) REFERENCES vehicles(id,owner_id)
);
CREATE INDEX rides_origin_gist ON rides USING gist(origin_location);
CREATE INDEX rides_destination_gist ON rides USING gist(destination_location);
CREATE INDEX rides_search_idx ON rides(departure_time,id) WHERE status='PUBLISHED';
CREATE TABLE ride_routes (
 ride_id uuid PRIMARY KEY REFERENCES rides(id), geometry geometry(LineString,4326) NOT NULL,
 encoded_polyline text NOT NULL, distance_meters integer NOT NULL CHECK(distance_meters > 0),
 duration_seconds integer NOT NULL CHECK(duration_seconds > 0), provider text NOT NULL,
 calculated_at timestamptz NOT NULL DEFAULT now(), CHECK(ST_IsValid(geometry)), CHECK(ST_NPoints(geometry)>=2)
);
CREATE INDEX ride_routes_geometry_gist ON ride_routes USING gist(geometry);
CREATE INDEX ride_routes_geography_gist ON ride_routes USING gist((geometry::geography));
CREATE TABLE ride_waypoints (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), ride_id uuid NOT NULL REFERENCES rides(id), sequence integer NOT NULL CHECK(sequence>=0),
 location geography(Point,4326) NOT NULL, address text NOT NULL, UNIQUE(ride_id,sequence)
);
CREATE TABLE matching_configuration (
 id integer PRIMARY KEY CHECK(id=1), proximity_meters integer NOT NULL CHECK(proximity_meters>0),
 time_tolerance_minutes integer NOT NULL CHECK(time_tolerance_minutes>=0), max_detour_seconds integer NOT NULL CHECK(max_detour_seconds>=0),
 updated_at timestamptz NOT NULL DEFAULT now()
);
-- Starting values from the specification, to be tuned before launch.
INSERT INTO matching_configuration VALUES (1,2000,30,600,now());
CREATE TABLE idempotency_keys (
 actor_id uuid NOT NULL REFERENCES users(id), scope varchar(100) NOT NULL, key varchar(200) NOT NULL,
 request_hash text NOT NULL, response_json jsonb, expires_at timestamptz NOT NULL,
 PRIMARY KEY(actor_id,scope,key)
);
CREATE TABLE audit_logs (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), actor_id uuid REFERENCES users(id), action text NOT NULL,
 resource_type text NOT NULL, resource_id uuid, request_id uuid NOT NULL, metadata jsonb NOT NULL DEFAULT '{}',
 created_at timestamptz NOT NULL DEFAULT now()
);
-- Booking, trips, payment/ledger and safety tables intentionally deferred until their reviewed phase.
