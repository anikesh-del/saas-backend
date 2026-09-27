# System Architecture

## Architecture Diagram

![SaaS Backend Architecture](diagrams/Multi-tenant_System-Architecture.png)

The system has three moving parts: the client, the Spring Boot app, and Postgres — plus Stripe sitting outside the application boundary entirely.

Client requests flow through the app's internal layers (auth, tenant resolution, RBAC, tenant-scope AOP, service, repository) and land on Postgres, which is where tenant isolation actually gets enforced via RLS — not just trusted from the application code.

Stripe is drawn as external and bidirectional on purpose: the app calls out to Stripe to create Checkout sessions, but Stripe also calls *into* the app via webhooks. That second arrow is the one worth pausing on — webhook callbacks don't go through the same trust boundary as everything else. No JWT, no `X-Tenant-Id`, no `TenantContext`, none of the AOP layers. The only thing authenticating a webhook request is Stripe's signature on the raw payload, verified in `StripeWebhookController` before anything else touches it. If that diagram showed Stripe's callback merging into the same pipeline as authenticated client traffic, it'd be misrepresenting the actual security model — see `Security_Model.md` for why that separation matters.