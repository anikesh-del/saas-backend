--plans
INSERT INTO plans (
    name,
    price_cents,
    stripe_price_id
)
VALUES
    ('FREE', 0, NULL),
    ('PRO', 999, 'price_PRO_TEST_ID'),
    ('ENTERPRISE', 4999, 'price_ENTERPRISE_TEST_ID');

--plan features 
INSERT INTO plan_features (
    plan_id,
    feature_key,
    feature_value
)
VALUES
    -- FREE
    ((SELECT plan_id FROM plans WHERE name = 'FREE'), 'max_tasks', '10'),
    ((SELECT plan_id FROM plans WHERE name = 'FREE'), 'max_projects', '2'),
    ((SELECT plan_id FROM plans WHERE name = 'FREE'), 'max_users', '3'),
    ((SELECT plan_id FROM plans WHERE name = 'FREE'), 'audit_log_access', 'false'),

    -- PRO
    ((SELECT plan_id FROM plans WHERE name = 'PRO'), 'max_tasks', '100'),
    ((SELECT plan_id FROM plans WHERE name = 'PRO'), 'max_projects', '20'),
    ((SELECT plan_id FROM plans WHERE name = 'PRO'), 'max_users', '20'),
    ((SELECT plan_id FROM plans WHERE name = 'PRO'), 'audit_log_access', 'true'),

    -- ENTERPRISE
    ((SELECT plan_id FROM plans WHERE name = 'ENTERPRISE'), 'max_tasks', '1000'),
    ((SELECT plan_id FROM plans WHERE name = 'ENTERPRISE'), 'max_projects', '100'),
    ((SELECT plan_id FROM plans WHERE name = 'ENTERPRISE'), 'max_users', '100'),
    ((SELECT plan_id FROM plans WHERE name = 'ENTERPRISE'), 'audit_log_access', 'true');

--system user 
INSERT INTO users (
    name,
    email,
    hashed_password,
    is_active
)
VALUES (
    'System',
    'system@internal',
    '$2a$10$DISABLEDDISABLEDDISABLEDDISABLEDDISABLEDDISAB',
    false
)
ON CONFLICT (email) DO NOTHING;