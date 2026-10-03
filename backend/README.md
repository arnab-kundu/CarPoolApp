# Backend API

NestJS modular monolith foundation for Together. Requires Node.js 22 or later and npm.

`GET /v1/health` works. Auth and ride controllers validate requests and return 503 until production adapters exist. The API skeleton does not yet connect to PostgreSQL or Redis.

## Install and run

Run from this `backend` directory:

```powershell
npm ci
npm run build
npm start
```

The default port is 3000; set `PORT` in your shell to override it. The health endpoint is `http://localhost:3000/v1/health`.

For development with automatic restarts:

```powershell
npm run dev
```

## Local infrastructure

Run from the repository root:

```powershell
Copy-Item .env.example .env
# Set a local database password in .env before starting services.
docker compose up -d postgres redis
```

PostgreSQL/PostGIS is exposed on localhost:5432 and Redis on localhost:6379. Docker Compose reads the root `.env`; the Node commands below require environment variables to be set in the shell.

The API Docker image can be built and started from the repository root with:

```powershell
docker compose up --build api
```

## Migrations

Run from `backend`, with `DATABASE_URL` set to your local PostgreSQL connection string:

```powershell
$env:DATABASE_URL = 'postgresql://carpool:YOUR_LOCAL_PASSWORD@localhost:5432/carpool'
npm run migrate
```

Use the database/user/password configured in the root `.env`. Migrations are explicit, versioned, checksummed, and transactional.

## Tests

Run from `backend`:

```powershell
npm test
```

This builds TypeScript and runs domain, HTTP, and integration tests. Set `TEST_DATABASE_URL` to a migrated disposable PostGIS database to run the geospatial integration test; otherwise that test is skipped.

## Layout

- `src`: API modules and domain code.
- `test`: automated tests.
- `scripts`: migration tooling.
- `migrations`: database migrations.
- `dist`: generated JavaScript, ignored by Git.

See the [API specification](../docs/API.md), [database design](../docs/DATABASE.md), [architecture](../docs/ARCHITECTURE.md), and [recorded validation](../docs/VALIDATION.md). Provider-backed authentication, booking, and payment integrations remain pending.

## Postman

Import [CarPoolApp Backend collection](postman/CarPoolApp.postman_collection.json) into Postman. Start the backend, then run requests or the collection with `baseUrl` set to `http://localhost:3000` (without `/v1`). Edit the collection variables for sample IDs, phone, and departure time as needed.

The collection covers all eight implemented routes and includes five invalid-input examples. Its response checks expect 200 for health, 503 with the configured error code for valid auth/ride requests, and 400 for validation failures. Auth values are placeholders; provider-backed login and token generation are pending.
