# Request Lifecycle

![Request Lifecycle](diagrams/Request-Lifecycle.png)

Walking a tenant-scoped write (`POST /api/v1/projects`) through the stack:

`JWT filter → TenantContextFilter → Controller → Transaction interceptor → RoleCheckAspect (@RequiresRole) → TenantScopeAspect (SET LOCAL) → Service → Repository → Postgres (RLS) → AuditService`

Two things matter more than the list itself:

**The `X-Tenant-Id` header only becomes trustworthy after `TenantContextFilter` checks it.** It arrives as a claim, not a credential — the filter runs a `tenant_memberships` lookup scoped to the JWT's `user_id` before binding tenant ID and role to `TenantContext`. Missing header → 400. No membership row → 403. Only after that does anything downstream get to assume the tenant context is real.

**AOP ordering is load-bearing, not cosmetic.** The transaction interceptor (`@Order(100)`) has to wrap *outside* `TenantScopeAspect` (`@Order(250)`), because `TenantScopeAspect` runs `SET LOCAL app.current_tenant_id = ?` via `JdbcTemplate`, and that statement has to land on the exact same physical connection Hibernate later borrows for the actual `INSERT`/`SELECT`. If the transaction hadn't already opened and bound a connection before the aspect fires, `SET LOCAL` could execute on a different connection than the one the repository call ends up using — and the RLS policy would silently check against an unset (or wrong) session variable instead of the tenant that's actually making the request. This is the single detail in this whole project most likely to get probed hard in an interview, and it's the reason `TransactionConfig` explicitly sets `@EnableTransactionManagement(order = 100)`.

`RoleCheckAspect` (`@Order(150)`) sits between the two — it just reads the role already bound to `TenantContext` by the filter, so its position relative to the transaction boundary doesn't carry the same correctness risk that `TenantScopeAspect`'s does.