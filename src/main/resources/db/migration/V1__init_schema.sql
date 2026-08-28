--1.TENANTS

CREATE TABLE tenants(
    tenant_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    status TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--2.USERS

CREATE TABLE users(
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    hashed_password TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--3.ROLES

CREATE TABLE roles (
    role_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

--4.TENANT MEMBERSHIPS

CREATE TABLE tenant_memberships(
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY(tenant_id,user_id),

    CONSTRAINT fk_tenant_memberships_tenant
       FOREIGN KEY(tenant_id) REFERENCES tenants (tenant_id),

    CONSTRAINT fk_tenant_memberships_usetr
      FOREIGN KEY(user_id) REFERENCES users(user_id),

    CONSTRAINT fk_tenant_memberships_role
        FOREIGN KEY (role_id)
        REFERENCES roles (role_id)
);

--5. Projects

CREATE TABLE projects(
    project_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_projects_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants (tenant_id),

    CONSTRAINT fk_projects_created_by
        FOREIGN KEY (created_by)
        REFERENCES users (user_id)
);

--6. tasks

CREATE TABLE tasks (
    task_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    project_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,
    assigned_to BIGINT,
    due_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tasks_project
        FOREIGN KEY (project_id)
        REFERENCES projects (project_id),

    CONSTRAINT fk_tasks_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants (tenant_id),

    CONSTRAINT fk_tasks_assigned_to
        FOREIGN KEY (assigned_to)
        REFERENCES users (user_id)
);

--7. Audit log

CREATE TABLE audit_log (
    audit_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id BIGINT,
    table_name TEXT NOT NULL,
    row_id BIGINT NOT NULL,
    operation TEXT NOT NULL,
    changed_by BIGINT NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    old_data JSONB,
    new_data JSONB,

    CONSTRAINT fk_audit_log_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants (tenant_id),

    CONSTRAINT fk_audit_log_changed_by
        FOREIGN KEY (changed_by)
        REFERENCES users (user_id)
);

--8. plans 

CREATE TABLE plans (
    plan_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    price_cents INTEGER NOT NULL,
    stripe_price_id TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--9. plan features

CREATE TABLE plan_features (
    plan_id BIGINT NOT NULL,
    feature_key TEXT NOT NULL,
    feature_value TEXT NOT NULL,

    PRIMARY KEY (plan_id, feature_key),

    CONSTRAINT fk_plan_features_plan
        FOREIGN KEY (plan_id)
        REFERENCES plans (plan_id)
);

--10. tenant subscriptions
CREATE TABLE tenant_subscriptions (
    subscription_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    stripe_customer_id TEXT,
    stripe_subscription_id TEXT,
    status TEXT NOT NULL,
    current_period_start TIMESTAMPTZ,
    current_period_end TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tenant_subscriptions_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants (tenant_id),

    CONSTRAINT fk_tenant_subscriptions_plan
        FOREIGN KEY (plan_id)
        REFERENCES plans (plan_id)
);

--11. webhook events

CREATE TABLE webhook_events (
    event_id TEXT PRIMARY KEY,
    event_type TEXT NOT NULL,
    status TEXT NOT NULL,
    received_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMPTZ
);

--seeded roles 

INSERT INTO roles (name)
VALUES
    ('owner'),
    ('admin'),
    ('member');
