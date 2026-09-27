# Multi-Tenant SaaS Backend

A multi-tenant backend I built with Spring Boot to actually understand how tenant isolation, RBAC, and billing work in a real production system — not just read about them. Tenant isolation is enforced at two layers: application RBAC and PostgreSQL Row-Level Security. Billing runs through Stripe (Checkout + webhooks).

## Stack

- Java 17, Spring Boot 4
- Spring Security + JWT (identity-only tokens — no tenant info baked in)
- PostgreSQL, Spring Data JPA/Hibernate, Flyway
- PostgreSQL RLS + custom AOP for tenant scoping (`@TenantScoped`, `@RequiresRole`)
- Stripe (Checkout Sessions + Webhooks)
- springdoc-openapi for Swagger
- Docker Compose (app + Postgres w/ healthchecks)

## Architecture

System diagram, request lifecycle, ER diagram, and security model are documented in [`docs/architecture.md`](./docs/architecture.md), [`docs/request-lifecycle.md`](./docs/request-lifecycle.md), [`docs/ER-diagram.md`](./docs/ER-diagram.md), and [`docs/Security_Model.md`](./docs/Security_Model.md).

Quick version: a request goes through the JWT filter, then `TenantContextFilter` (which checks the `X-Tenant-Id` header against `tenant_memberships` before trusting it), then the controller, then RBAC and tenant-scope AOP, then service/repository, and Postgres RLS enforces isolation on the way out. Stripe lives outside all of that — webhooks come in on a separate path with no JWT involved, just signature verification.

## Running it locally

You need Docker and Docker Compose. That's it.

1. Clone the repo
2. Add a `.env` file with the variables below
3. `docker compose up --build`

Postgres spins up first with a dedicated non-superuser role for the app (this matters — see below), Flyway runs the migrations, then the app comes up on `:8080`.

**.env variables you'll need:**

POSTGRES_USER=
POSTGRES_PASSWORD=
DB_NAME=
DB_USER=
DB_PASSWORD=
FLYWAY_USER=
FLYWAY_PASSWORD=
JWT_SECRET=
STRIPE_SECRET_KEY=
STRIPE_WEBHOOK_SECRET=
STRIPE_SUCCESS_URL=
STRIPE_CANCEL_URL=


`DB_USER`/`DB_PASSWORD` is the app's own role, separate from the Postgres container superuser — Flyway gets its own, broader role too. Keeping these separate isn't just tidiness — Postgres superusers bypass RLS entirely, so if the app connected as superuser the whole isolation story would be fake.

## API docs

`http://localhost:8080/swagger-ui/index.html` once it's running.

## A few decisions worth explaining

**RLS is the actual boundary, RBAC is a second layer on top of it, not instead of it.** `@RequiresRole` checks happen in application code, which means they can be forgotten on some new endpoint someday. The Postgres RLS policy can't be forgotten the same way — it's set once at the DB level and every query has to go through it. `SET LOCAL app.current_tenant_id` runs inside an AOP aspect on the same connection the transaction is already bound to (ordering here is deliberate, not incidental — details in ARCHITECTURE.md).

**The tenant header is a claim, not proof of anything.** JWTs only carry `user_id`/`email` — nothing tenant-related. Every request re-checks `X-Tenant-Id` against `tenant_memberships` scoped to the JWT's user before it's trusted.

**Roles live on the membership row, not the user.** Someone can be `owner` in one tenant and just a `member` in another.

**Webhook idempotency needed two transactions, not one.** My first pass tried to insert into `webhook_events` and catch a unique-constraint violation inside the same transaction as event processing — that doesn't work in Postgres, a failed statement poisons the whole transaction until rollback. Fixed it by splitting the idempotency insert into its own transaction.

**Audit log can't be edited or deleted, even by accident.** There's a Postgres trigger blocking `UPDATE`/`DELETE`/`TRUNCATE` on that table. Not just "the repository doesn't expose it" — actually blocked at the DB.

## What's not in here (V1 vs V2)

Cut for scope, not because I didn't think about them:

- **Token revocation** — stateless JWT is fine for V1; doing revocation properly needs a blocklist store checked per request, which is its own chunk of infra
- **Cross-tenant admin access** — no real use case yet, and it directly conflicts with the RLS guarantee unless it's scoped/audited/time-boxed, which is a design problem on its own
- **Per-plan rate limiting** — doesn't add to what this project is trying to prove
- **Async webhook processing** — synchronous is fine at this scale, a queue would just be extra infra for no real benefit right now
- **Invitation flow for members** — added directly by owner/admin instead, no pending state to track
- **Enforcing one active subscription per tenant at the DB level** — `stripe_subscription_id` works fine as the lookup key for now, the partial unique index is a hardening step for later
