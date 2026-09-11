ALTER TABLE projects ENABLE ROW LEVEL SECURITY;

CREATE POLICY projects_tenant_isolation
ON projects
USING (
tenant_id = current_setting('app.current_tenant_id', true)::bigint
)
WITH CHECK (
tenant_id = current_setting('app.current_tenant_id', true)::bigint
);

ALTER TABLE tasks ENABLE ROW LEVEL SECURITY;

CREATE POLICY tasks_tenant_isolation
ON tasks
USING (
tenant_id = current_setting('app.current_tenant_id', true)::bigint
)
WITH CHECK (
tenant_id = current_setting('app.current_tenant_id', true)::bigint
);