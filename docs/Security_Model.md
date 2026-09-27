# Security Model

![Security Model](diagrams/Security%20Model.png)

Three layers, in order of how much they can be trusted blindly:

**JWT is identity-only.** `user_id`, `email`, `iat`, `exp` — no tenant claims baked in. This is a deliberate constraint: if tenant membership were encoded in the token, a role change or membership revocation wouldn't take effect until the token expired. Keeping the JWT dumb means every tenant-scoped request re-checks membership live.

**`X-Tenant-Id` is a claim, not a credential.** Anyone can put any tenant ID in that header. What makes it trustworthy is `TenantContextFilter` checking it against `tenant_memberships`, scoped to the authenticated JWT's `user_id`, on every single request — not caching it, not trusting a previous request's context. The header is only ever as good as that lookup.

**RLS is the real isolation boundary — RBAC is defense-in-depth on top of it, not instead of it.** `@RequiresRole` checks happen in application code, which means they're only as reliable as remembering to annotate every new endpoint correctly. The Postgres RLS policy can't be forgotten the same way once it's on the table — every query against `projects`/`tasks` goes through `current_setting('app.current_tenant_id', true)`, checked with the `missing_ok` flag so an unset session variable fails closed (zero rows) instead of erroring. Even a bug that skipped the RBAC check entirely still couldn't leak another tenant's rows, because the database itself is doing the filtering, not the service layer's judgment.

One condition this depends on that's easy to state wrong: RLS only holds if the app connects as a non-superuser Postgres role. A superuser bypasses RLS unconditionally, regardless of what policies exist — so the isolation guarantee is really "RLS policies + a deliberately unprivileged `saas_app` DB role," not RLS alone. Worth saying both halves if asked, since either one alone is an incomplete answer.

Stripe webhooks sit outside this entire model. `/api/v1/webhooks/stripe` is `permitAll()`, excluded from `TenantContextFilter`, and never touches `TenantContext`, RBAC, or RLS. The only authentication is Stripe's signature on the raw request body, verified before the event is processed. It's a separate trust boundary because it *has* to be — Stripe doesn't have a JWT for your app, and forcing it through the same pipeline as user traffic would mean either weakening that pipeline or building a fake identity for Stripe, both worse than just treating it as its own thing.